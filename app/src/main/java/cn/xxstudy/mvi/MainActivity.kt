package cn.xxstudy.mvi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import cn.xxstudy.mvi.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var loginViewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        binding.btnLogin.setOnClickListener {
            lifecycleScope.launch {
                binding.progressBar.visibility = View.VISIBLE
                loginViewModel.channel.send(LoginIntent.Login(binding.editTextTextEmailAddress.editableText.toString(),
                    binding.editTextTextPassword.editableText.toString()))
            }
        }

        lifecycleScope.launch {
            loginViewModel.state.collect {
                binding.progressBar.visibility = View.GONE
                when (it) {
                    is LoginState.InitState -> {
                        println("Sensi init....")
                    }
                    is LoginState.LoginSuccess -> Toast.makeText(this@MainActivity,
                        "SUCCESS",
                        Toast.LENGTH_SHORT).show()
                    is LoginState.LoginFailed -> Toast.makeText(this@MainActivity,
                        "FAILED",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.button.setOnClickListener {
            lifecycleScope.launch {
                val time = measureTimeMillis {
                    binding.textView.text = ""
                    "".let {

                    }
                    binding.textView.append("start ...\n")
                    println("当前线程1：${Looper.myLooper() == Looper.getMainLooper()}")

                    val task1 = async(Dispatchers.IO) {
                        withContext(Dispatchers.Main) {
                            binding.textView.append("not lazy...${Looper.myLooper() == Looper.getMainLooper()}\n")
                        }
                        println("当前线程2：${Looper.myLooper() == Looper.getMainLooper()}")
                        delay(1000)
                        "complete task1"
                    }

                    val task2 = async(Dispatchers.IO) {
                        withContext(Dispatchers.Main) {
                            binding.textView.append("not lazy2...${Looper.myLooper() == Looper.getMainLooper()}\n")
                        }
                        delay(3000)
                        "complete task2"
                    }
                    binding.textView.append("...who exe...\n")
                    binding.textView.append("result ...${task1.await()}_${task2.await()}\n")
                    binding.textView.append("executor finished ...\n")
                }
                binding.textView.append("count time ...${time}")
            }


        }

        binding.button2.setOnClickListener {
            lifecycleScope.launch {
                autoIncrement5()
            }
        }

        binding.login.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    //使用synchronized
    suspend fun autoIncrement() {
        var count = 0
        val obj = Any()
        withContext(Dispatchers.IO) {
            repeat(100) {
                launch {
                    repeat(10000) {
                        synchronized(obj) {
                            count++
                        }
                    }
                }
            }
            delay(3000)
            withContext(Dispatchers.Main) {
                binding.textView.text = count.toString()
            }
        }
    }

    //使用线程安全的数据结构
    suspend fun autoIncrement2() {
        var count = AtomicInteger(0)
        val obj = Any()
        withContext(Dispatchers.IO) {
            repeat(100) {
                launch {
                    repeat(10000) {
                        synchronized(obj) {
                            count.incrementAndGet()
                        }
                    }
                }
            }
            delay(3000)
            withContext(Dispatchers.Main) {
                binding.textView.text = count.toString()
            }
        }
    }

    //使用ReentrantLock
    suspend fun autoIncrement3() {
        val lock = ReentrantLock()
        var count = 0
        withContext(Dispatchers.IO) {
            repeat(100) {
                launch {
                    repeat(10000) {
                        lock.lock()
                        try {
                            count++
                        } finally {
                            lock.unlock()
                        }
                    }
                }
            }
            delay(3000)
            withContext(Dispatchers.Main) {
                binding.textView.text = count.toString()
            }
        }
    }

    //使用Mutex withLock
    suspend fun autoIncrement4() {
        var count = 0
        val mutex = Mutex()
        withContext(Dispatchers.IO) {
            repeat(100) {
                launch {
                    repeat(10000) {
                        //使用扩展函数，等同于下宀的try finally
                        mutex.withLock {
                            count++
                        }
                        /*mutex.lock()
                        try {
                            count++
                        } finally {
                            mutex.unlock()
                        }*/
                    }
                }
            }
            delay(3000)
            withContext(Dispatchers.Main) {
                binding.textView.text = count.toString()
            }
        }
    }

    //限制线程
    @OptIn(DelicateCoroutinesApi::class)
    suspend fun autoIncrement5() {
        var count = 0
        val context = newSingleThreadContext("test")
        withContext(context) {
            repeat(100) {
                launch {
                    repeat(10000) {
                        count++
                    }
                }
            }
            delay(3000)
            withContext(Dispatchers.Main) {
                binding.textView.text = count.toString()
            }
        }
    }
}
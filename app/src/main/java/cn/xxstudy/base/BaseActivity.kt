package cn.xxstudy.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

/**
 * @date: 2023/4/14 17:30
 * @author: Sensi
 * @remark:
 */
abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    private var _binding: VB? = null
    protected val binding: VB get() = requireNotNull(_binding) { "Not inited or destroyed" }

    open val viewBindingClass: Class<VB>
        @Suppress("UNCHECKED_CAST")
        get() = (this::class.java.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<VB>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflate = viewBindingClass.getMethod("inflate", LayoutInflater::class.java)
        @Suppress("UNCHECKED_CAST")
        _binding = inflate(null, layoutInflater) as VB

        setContentView(binding.root)

        onViewCreated(savedInstanceState)
    }


    abstract fun onViewCreated(savedInstanceState: Bundle?)

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
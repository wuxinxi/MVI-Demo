package cn.xxstudy.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

/**
 * @date: 2023/4/12 16:11
 * @author: Sensi
 * @remark:
 */
class LoginViewModel : ViewModel() {
    private val _channel = Channel<LoginIntent>(Channel.UNLIMITED)
    val channel: Channel<LoginIntent>
        get() = _channel

    private val _state = MutableStateFlow<LoginState>(LoginState.InitState)
    val state: StateFlow<LoginState>
        get() = _state

    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            _channel.consumeAsFlow().collect {
                when (it) {
                    is LoginIntent.Login -> login(it.userEmail, it.userPassword)
                }
            }
        }
    }


    private fun login(userName: String, userPassword: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                delay(1000 * 5)
                val success = Random().nextBoolean()
                _state.value =
                    if (success) LoginState.LoginSuccess(
                        User(
                            userName,
                            userName
                        )
                    ) else LoginState.LoginFailed("ERROR")
            }
        }
    }
}
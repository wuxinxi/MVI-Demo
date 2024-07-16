package cn.xxstudy.mvi

/**
 * @date: 2023/4/12 16:13
 * @author: Sensi
 * @remark:
 */
sealed class LoginState {
    object InitState : LoginState()
    data class LoginSuccess(val user: User) : LoginState()
    data class LoginFailed(val error: String) : LoginState()
}

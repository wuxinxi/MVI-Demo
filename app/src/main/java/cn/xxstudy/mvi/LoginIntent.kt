package cn.xxstudy.mvi

/**
 * @date: 2023/4/12 16:12
 * @author: Sensi
 * @remark:
 */
sealed class LoginIntent {
    data class Login(val userEmail: String, val userPassword: String) : LoginIntent()
}

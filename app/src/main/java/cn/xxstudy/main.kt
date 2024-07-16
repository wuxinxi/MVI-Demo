package cn.xxstudy


fun main() {

    val q = fun(x: Int, y: Int): Int = x + y
    val q1 = q(1, 2)
    println(q1)


    val isEmpty = fun(name: String?): Boolean = name?.isEmpty() ?: true

    isEmpty("haha")

    val isEmpty2 = { name: String -> name.isEmpty() }


    val isEmpty3: (String) -> Boolean = { name -> name.isEmpty() }

    isEmpty3("dd")

    val test2: (Int, Int) -> Int = { i, j -> i + j }

}


object CacheUtils {
    fun save(key: String, value: String) {

    }

    fun getValue(key: String) = key
}

data class User(val userName: String, val userEmail: String)


class Person(name: String) {
    operator fun component1() = name

    val name: String = name
        get() = field + "_"
}

class Result(val message: String, val code: Int) {

    operator fun component1() = message

    operator fun component2() = code
}
package indi.yume.view.avocadoviews.loadinglayout

import org.junit.Test

/**
 * Created by yume on 17-4-21.
 */

class TestClass {
    @Test
    @Throws(Exception::class)
    fun kotlinTest() {
        val model = TestModel()
        model.ss = "1"
        model.ss = "2"
    }
}

class TestModel {
    lateinit var ss: String


}
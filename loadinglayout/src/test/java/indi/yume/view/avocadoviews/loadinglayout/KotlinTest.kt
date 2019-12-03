package indi.yume.view.avocadoviews.loadinglayout

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Test
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * Created by yume on 17-4-21.
 */

class TestClass {
    @Test
    @Throws(Exception::class)
    fun kotlinTest() {
//        val model = TestModel()
//        model.ss = "1"
//        model.ss = "2"
Objects.nonNull()
        Observable.fromIterable(listOf(1, 2, 3, 4))
                .doOnNext { println("doOnNext-1 $it") }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .doOnNext {
                    println("doOnNext-2 $it")
                    Thread.sleep(20)
                }
                .subscribe()

        println("end")
        Thread.sleep(2000)
    }
}

class TestModel {
    lateinit var ss: String


}
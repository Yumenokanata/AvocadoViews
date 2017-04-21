package indi.yume.view.avocadoviews.loadinglayout

/**
 * Created by yume on 17-4-20.
 */

sealed class LoadingResult<out T> {
    fun <R> map(mapper: (T) -> R): LoadingResult<R> =
            when(this) {
                is Success -> success(mapper(data))
                is LastData -> LastData(mapper(data))
                is NoMoreData -> noMore()
                is Failure -> fail(fail)
            }

    fun <R> flatMap(mapper: (T) -> LoadingResult<R>): LoadingResult<R> =
            when(this) {
                is Success -> mapper(data)
                is LastData -> mapper(data)
                is NoMoreData -> noMore()
                is Failure -> fail(fail)
            }

    companion object {
        @JvmStatic
        fun <T> fail(t: Throwable): LoadingResult<T> = Failure(t)

        @JvmStatic
        fun <T> success(data: T): LoadingResult<T> = Success(data)

        @JvmStatic
        fun <T> lastData(data: T): LoadingResult<T> = LastData(data)

        @JvmStatic
        fun <T> noMore(): LoadingResult<T> = NoMoreData
    }
}

data class Failure(val fail: Throwable) : LoadingResult<Nothing>()

data class Success<out T>(val data: T) : LoadingResult<T>()

data class LastData<out T>(val data: T) : LoadingResult<T>()

object NoMoreData : LoadingResult<Nothing>()
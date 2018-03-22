package indi.yume.view.avocadoviews.loadinglayout

/**
 * Created by yume on 18-3-22.
 */

sealed class LoadingData<out T> {
    abstract fun isEmpty(): Boolean

    fun <K> map(f: (T) -> K): LoadingData<K> =
            when(this) {
                is HasData -> HasData(f(t))
                is NoData -> this
            }

    fun <K> flatMap(f: (T) -> LoadingData<K>): LoadingData<K> =
            when(this) {
                is HasData -> f(t)
                is NoData -> this
            }

    fun <K> fold(ifEmpty: () -> K, some: (T) -> K): K =
            when(this) {
                is HasData -> some(t)
                is NoData -> ifEmpty()
            }

    fun orNull(): T? =
            when(this) {
                is HasData -> t
                is NoData -> null
            }
}

data class HasData<out T>(val t: T) : LoadingData<T>() {
    override fun isEmpty(): Boolean = false
}

object NoData : LoadingData<Nothing>() {
    override fun isEmpty(): Boolean = true
}


fun <T> LoadingData<T>.getOr(default: T): T = fold({ default }, { it })

fun <T> LoadingData<T>.getOrDefault(default: () -> T): T = fold(default, { it })

fun <A, B : A> LoadingData<B>.orElse(alternative: () -> LoadingData<B>): LoadingData<B> = if (isEmpty()) alternative() else this

fun <T> LoadingData<T>.or(value: LoadingData<T>): LoadingData<T> = if (isEmpty()) value else this
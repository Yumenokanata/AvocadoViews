package indi.yume.view.avocadoviews.loadinglayout

import android.support.annotation.IdRes
import android.util.Log
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.internal.schedulers.NewThreadScheduler
import io.reactivex.subjects.PublishSubject
import java.lang.Exception
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * Created by yume on 17-4-20.
 */
typealias ActionTrunk = (LoadingState) -> Action

class Store(val realWorld: RealWorld) {

    internal val eventSubject = PublishSubject.create<ActionTrunk>()

    internal val renderSubject = PublishSubject.create<LoadingState>().toSerialized()

    var renderCallback: ((LoadingState) -> Unit)? = null

    init {
        eventSubject.toFlowable(BackpressureStrategy.DROP)
                .observeOn(NewThreadScheduler())
                .scan<StateData>(StateData(LoadingState.empty(), LoadingState.empty(), EmptyAction))
                { stateData, trunk ->
                    try {
                        val action = trunk(stateData.newState)
                        Log.d(TAG, "start invoke action: ${action::class.simpleName}")
                        val newState = action
                                .effect(realWorld, stateData.newState)
                                .doOnNext { render(it) }
                                .blockingLast(stateData.newState)

                        StateData(stateData.newState, newState, action)
                    } catch (e: Exception) {
                        Log.e(TAG, "Deal event error: ", e)
                        render(stateData.newState)
                        stateData
                    }
                }
                .subscribe({ newState -> },
                        { t -> Log.e(TAG, "Deal event error: ", t) },
                        { Log.e(TAG, "Loading Store event looper has dead.") })

        renderSubject
                .doOnNext { state -> Log.d(TAG, "current state: ${state}") }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ renderCallback?.invoke(it) },
                        { t -> Log.e(TAG, "Render state error: ", t) },
                        { Log.e(TAG, "Loading Store render looper has dead.") })
    }

    private fun render(state: LoadingState) = renderSubject.onNext(state)

    fun dispatch(action: Action) {
        eventSubject.onNext { action }
    }

    fun dispatch(trunk: ActionTrunk) {
        eventSubject.onNext(trunk)
    }

    fun unsubscribe() {
        eventSubject.onComplete()
        renderSubject.onComplete()
    }

    companion object {
        val TAG: String = Store::class.simpleName ?: "Store"
    }
}

data class StateData(
        val oldState: LoadingState,
        val newState: LoadingState,
        val event: Action
)
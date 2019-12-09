package indi.yume.view.avocadoviews.loadinglayout

import android.annotation.SuppressLint
import android.util.Log
import indi.yume.view.avocadoviews.loadinglayout.LoadingCore.Companion.enableLog
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.internal.schedulers.NewThreadScheduler
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.lang.Exception

/**
 * Created by yume on 17-4-20.
 */

interface CoreStore {
    var renderCallback: ((LoadingState) -> Unit)?

    fun bind(): Observable<LoadingState>

    fun dispatch(action: Action)

    fun unsubscribe()
}

@SuppressLint("CheckResult")
class Store(val realWorld: RealWorld, initState: LoadingState = LoadingState.empty()) : CoreStore {

    internal val eventSubject = PublishSubject.create<Action>()

    internal val renderSubject = BehaviorSubject.createDefault(initState).toSerialized()

    override var renderCallback: ((LoadingState) -> Unit)? = null

    init {
        eventSubject.toFlowable(BackpressureStrategy.LATEST)
                .observeOn(NewThreadScheduler(), false, 1)
                .scan<StateData>(StateData(initState, initState, EmptyAction))
                { stateData, action ->
                    try {
                        if (enableLog)
                            Log.d(TAG, "start invoke action: ${action::class.java.simpleName}")
                        val newState = action
                                .effect(realWorld, stateData.newState)
                                .doOnNext { render(it) }
                                .blockingLast(stateData.newState)

                        StateData(stateData.newState, newState, action)
                    } catch (e: Exception) {
                        if (enableLog) Log.e(TAG, "Deal event error: ", e)
                        render(stateData.newState.copy(isError = true))
                        stateData
                    }
                }
                .subscribe({ newState -> },
                        { t -> if (enableLog) Log.e(TAG, "Deal event error: ", t) },
                        { if (enableLog) Log.e(TAG, "Loading Store event looper has dead.") })

        renderSubject
                .doOnNext { state -> if (enableLog) Log.d(TAG, "current state: ${state}") }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ renderCallback?.invoke(it) },
                        { t -> if (enableLog) Log.e(TAG, "Render state error: ", t) },
                        { if (enableLog) Log.e(TAG, "Loading Store render looper has dead.") })
    }

    private fun render(state: LoadingState) = renderSubject.onNext(state)

    override fun bind(): Observable<LoadingState> = renderSubject

    override fun dispatch(action: Action) {
        eventSubject.onNext(action)
    }

    override fun unsubscribe() {
        eventSubject.onComplete()
        renderSubject.onComplete()
    }

    companion object {
        val TAG: String = Store::class.java.simpleName ?: "Store"
    }
}

data class StateData(
        val oldState: LoadingState,
        val newState: LoadingState,
        val event: Action
)
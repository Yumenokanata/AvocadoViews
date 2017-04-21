package indi.yume.view.avocadoviews.loadinglayout

/**
 * Created by yume on 17-4-21.
 */
enum class LoadMoreStatus{
    // LoadMore view is invisible
    INVISIBLE,

    // LoadMore view is disable, for example: refreshing, no more data
    DISABLE,

    // LoadMore view has be shown
    NORMAL,

    // More data is loading
    LOADING
}
package indi.yume.view.avocadoviews.recyclerlayout

/**
 * Created by yume on 16-9-13.
 */
class NoMoreDataException : RuntimeException("Observable can not provide more data.")

class LastDataException(val list: List<out Any>) : RuntimeException("This last Data of provider.")
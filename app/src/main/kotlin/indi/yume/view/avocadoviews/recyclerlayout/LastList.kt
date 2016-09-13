package indi.yume.view.avocadoviews.recyclerlayout

import java.util.*

/**
 * Created by yume on 16-9-13.
 */
internal class LastList<T>: ArrayList<T> {
    constructor(): super()
    constructor(capacity: Int): super(capacity)
    constructor(collection: MutableCollection<out T>): super(collection)
}
package indi.yume.view.avocadoviews.loadinglayout

import android.view.View

/**
 * Created by yume on 17-4-21.
 */

inline fun <reified T : View> View.find(id: Int): T = findViewById(id) as T
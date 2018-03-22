package indi.yume.view.avocadoviews.loadinglayout

import android.view.View

/**
 * Created by yume on 17-4-21.
 */

fun <T : View> View.find(id: Int?): T? = if(id != null) findViewById(id) else null
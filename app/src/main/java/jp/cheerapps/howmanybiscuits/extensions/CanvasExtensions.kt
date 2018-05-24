package jp.cheerapps.howmanybiscuits.extensions

import android.graphics.Canvas

fun Canvas.saveRestore(block: Canvas.() -> Unit) {
    save()
    block()
    restore()
}
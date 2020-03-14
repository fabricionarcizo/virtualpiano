package dk.itu.moapd.virtualpiano.ui

import android.graphics.RectF

class PianoKey(
    private var mSound: Int,
    private var mRectF: RectF) {

    val sound
        get() = mSound
    val rectF
        get() = mRectF
    var isPressed = false

}

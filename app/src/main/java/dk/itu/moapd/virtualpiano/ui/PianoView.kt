package dk.itu.moapd.virtualpiano.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import dk.itu.moapd.virtualpiano.player.SoundPlayer

class PianoView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val soundPlayer = SoundPlayer(context!!)

    private val blackColor = Paint().apply { color = Color.BLACK }
    private val whiteColor = Paint().apply { color = Color.WHITE }
    private val grayColor = Paint().apply {
        color = Color.GRAY
        style = Paint.Style.FILL
    }

    private val whiteKeys: ArrayList<PianoKey> = ArrayList()
    private val blackKeys: ArrayList<PianoKey> = ArrayList()

    private var keyWidth: Float = 0f
    private var keyHeight: Float = 0f

    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            invalidate()
        }
    }

    companion object {
        private const val numberOfKeys = 8
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        keyWidth = (w / numberOfKeys).toFloat()
        keyHeight = h.toFloat()
        var count = numberOfKeys + 1

        for (i in 0 until numberOfKeys) {
            var left = i * keyWidth
            var right = (i + 1) * keyWidth

            if (i == numberOfKeys - 1)
                right = w.toFloat()

            var rect = RectF(left, 0f, right, h.toFloat())
            whiteKeys.add(PianoKey(i + 1, rect))

            if (i != 0 && i != 3 && i != 7) {
                left = (i - 1) * keyWidth +
                        0.5f * keyWidth +
                        0.25f * keyWidth
                right = i * keyWidth +
                        0.25f * keyWidth
                rect = RectF(left, 0f, right, 0.67f * h)
                blackKeys.add(PianoKey(count++, rect))
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        for (key in whiteKeys)
            canvas?.drawRect(key.rectF,
                if (key.isPressed) grayColor else whiteColor)

        for (i in 1 until numberOfKeys)
            canvas?.drawLine(i * keyWidth, 0f, i * keyWidth,
                keyHeight, blackColor)

        for (key in blackKeys)
            canvas?.drawRect(key.rectF,
                if (key.isPressed) grayColor else blackColor)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)

        val action = event?.action
        val isPressed = action == MotionEvent.ACTION_DOWN ||
                action == MotionEvent.ACTION_MOVE

        if (action == MotionEvent.ACTION_UP)
            performClick()

        val pressedKeys: ArrayList<PianoKey> = ArrayList()

        for (i in 0 until event!!.pointerCount) {
            val x = event.getX(i)
            val y = event.getY(i)
            val key: PianoKey? = keyForCoordinates(x, y)
            if (key != null) {
                key.isPressed = isPressed
                pressedKeys.add(key)
            }
        }

        val keyboard: ArrayList<PianoKey> = ArrayList(whiteKeys)
        keyboard.addAll(blackKeys)

        for (key in keyboard) {
            if (pressedKeys.contains(key) && key.isPressed) {
                if (soundPlayer.isNotPlaying(key.sound)) {
                    soundPlayer.playNote(key.sound)
                    invalidate()
                }
            } else {
                soundPlayer.stopNote(key.sound)
                releasePianoKey(key)
            }
        }

        return true

    }

    private fun keyForCoordinates(x: Float, y: Float): PianoKey? {
        for (key in blackKeys)
            if (key.rectF.contains(x, y))
                return key

        for (key in whiteKeys)
            if (key.rectF.contains(x, y))
                return key

        return null
    }

    private fun releasePianoKey(key: PianoKey) {
        mHandler.postDelayed({
            key.isPressed = false
            mHandler.sendEmptyMessage(0)
        }, 100)
    }

}

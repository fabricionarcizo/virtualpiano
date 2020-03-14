package dk.itu.moapd.virtualpiano.ui;

import android.graphics.RectF;

class PianoKey {

    private final int mSound;
    private final RectF mRectF;
    private boolean mPressed;

    public PianoKey(int sound, RectF rectF) {
        mSound = sound;
        mRectF = rectF;
    }

    public int getSound() {
        return mSound;
    }

    public RectF getRectF() {
        return mRectF;
    }

    public boolean isPressed() {
        return mPressed;
    }

    public void setPressed(boolean pressed) {
        mPressed = pressed;
    }

}

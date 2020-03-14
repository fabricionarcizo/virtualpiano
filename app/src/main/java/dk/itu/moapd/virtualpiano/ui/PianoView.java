package dk.itu.moapd.virtualpiano.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import dk.itu.moapd.virtualpiano.player.SoundPlayer;

public class PianoView extends View {

    private static final int sNumberOfKeys = 8;

    private final Paint mBlackColor;
    private final Paint mWhiteColor;
    private final Paint mGrayColor;

    private final ArrayList<PianoKey> mWhiteKeys = new ArrayList<>();
    private final ArrayList<PianoKey> mBlackKeys = new ArrayList<>();

    private int mPianoKeyWidth;
    private int mPianoKeyHeight;

    private final SoundPlayer mSoundPlayer;

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            invalidate();
        }
    };

    public PianoView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mBlackColor = new Paint();
        mBlackColor.setColor(Color.BLACK);

        mWhiteColor = new Paint();
        mWhiteColor.setColor(Color.WHITE);

        mGrayColor = new Paint();
        mGrayColor.setColor(Color.GRAY);
        mGrayColor.setStyle(Paint.Style.FILL);

        mSoundPlayer = new SoundPlayer(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldWidth, int oldHeight) {
        super.onSizeChanged(w, h, oldWidth, oldHeight);

        mPianoKeyWidth = w / sNumberOfKeys;
        mPianoKeyHeight = h;
        int count = sNumberOfKeys + 1;

        for (int i = 0; i < sNumberOfKeys; i++) {
            float left = i * mPianoKeyWidth;
            float right = (i + 1) * mPianoKeyWidth;

            if (i == sNumberOfKeys - 1)
                right = w;

            RectF rect = new RectF(left, 0, right, h);
            mWhiteKeys.add(new PianoKey(i + 1, rect));

            if (i != 0 && i != 3 && i != 7) {
                left = (float) (i - 1) * mPianoKeyWidth +
                        0.5f * mPianoKeyWidth +
                        0.25f * mPianoKeyWidth;
                right = (float) i * mPianoKeyWidth +
                        0.25f * mPianoKeyWidth;

                rect = new RectF(left, 0, right, 0.67f * h);
                mBlackKeys.add(new PianoKey(count++, rect));
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (PianoKey key: mWhiteKeys)
            canvas.drawRect(key.getRectF(), key.isPressed() ? mGrayColor : mWhiteColor);

        for (int i = 1; i < sNumberOfKeys; i++)
            canvas.drawLine(i * mPianoKeyWidth, 0, i * mPianoKeyWidth,
                    mPianoKeyHeight, mBlackColor);

        for (PianoKey key: mBlackKeys)
            canvas.drawRect(key.getRectF(), key.isPressed() ? mGrayColor : mBlackColor);
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        int action = event.getAction();
        boolean isPressed = (action == MotionEvent.ACTION_DOWN ||
                action == MotionEvent.ACTION_MOVE);

        if (action == MotionEvent.ACTION_UP)
            performClick();

        ArrayList<PianoKey> pressedKeys = new ArrayList<>();

        for (int i = 0; i < event.getPointerCount(); i++) {
            float x = event.getX(i);
            float y = event.getY(i);

            PianoKey key = keyForCoordinates(x, y);
            if (key != null) {
                key.setPressed(isPressed);
                pressedKeys.add(key);
            }
        }

        ArrayList<PianoKey> keyboard = new ArrayList<>(mWhiteKeys);
        keyboard.addAll(mBlackKeys);

        for (PianoKey key : keyboard) {
            if (pressedKeys.contains(key) && key.isPressed()) {
                if (mSoundPlayer.isNotPlaying(key.getSound())) {
                    mSoundPlayer.playNote(key.getSound());
                    invalidate();
                }
            } else {
                mSoundPlayer.stopNote(key.getSound());
                releasePianoKey(key);
            }
        }

        return true;
    }

    private PianoKey keyForCoordinates(float x, float y) {
        for (PianoKey key : mBlackKeys)
            if (key.getRectF().contains(x, y))
                return key;

        for (PianoKey key : mWhiteKeys)
            if (key.getRectF().contains(x, y))
                return key;

        return null;
    }

    private void releasePianoKey(final PianoKey key) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                key.setPressed(false);
                mHandler.sendEmptyMessage(0);
            }
        }, 100);
    }

}

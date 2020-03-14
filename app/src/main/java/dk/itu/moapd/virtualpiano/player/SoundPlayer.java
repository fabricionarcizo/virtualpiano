package dk.itu.moapd.virtualpiano.player;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.SparseArray;
import android.util.SparseIntArray;

import dk.itu.moapd.virtualpiano.R;

public class SoundPlayer {

    private SparseArray<PlayThread> mThreads;
    private Context mContext;
    private static final SparseIntArray SOUND_MAP = new SparseIntArray();

    static {
        // White keys.
        SOUND_MAP.put(1, R.raw.c4);
        SOUND_MAP.put(2, R.raw.d4);
        SOUND_MAP.put(3, R.raw.e4);
        SOUND_MAP.put(4, R.raw.f4);
        SOUND_MAP.put(5, R.raw.g4);
        SOUND_MAP.put(6, R.raw.a4);
        SOUND_MAP.put(7, R.raw.b4);
        SOUND_MAP.put(8, R.raw.c5);

        // Black keys.
        SOUND_MAP.put(9, R.raw.db4);
        SOUND_MAP.put(10, R.raw.eb4);
        SOUND_MAP.put(11, R.raw.gb4);
        SOUND_MAP.put(12, R.raw.ab4);
        SOUND_MAP.put(13, R.raw.bb4);
    }

    public SoundPlayer(Context context) {
        mContext = context;
        mThreads = new SparseArray<>();
    }

    public void playNote(int note) {
        if (isNotPlaying(note)) {
            PlayThread thread = new PlayThread(note);
            thread.start();
            mThreads.put(note, thread);
        }
    }

    public void stopNote(int note) {
        PlayThread thread = mThreads.get(note);
        if (thread != null) {
            mThreads.remove(note);
            try {
                Thread.currentThread();
                Thread.sleep(100);
                thread.mediaPlayer.stop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isNotPlaying(int note) {
        return mThreads.get(note) == null;
    }

    private class PlayThread extends Thread {

        private final int mNote;
        private MediaPlayer mediaPlayer;

        PlayThread(int note) {
            mNote = note;
        }

        @Override
        public void run() {
            try {
                int filename = SOUND_MAP.get(mNote);

                mediaPlayer = MediaPlayer.create(mContext, filename);
                mediaPlayer.start();

                while (mediaPlayer.getCurrentPosition() < mediaPlayer.getDuration() &&
                        mediaPlayer.isPlaying())
                    sleep(100);

                mediaPlayer.stop();

            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } finally {
                if (mediaPlayer != null)
                    mediaPlayer.release();
            }
        }

    }
}

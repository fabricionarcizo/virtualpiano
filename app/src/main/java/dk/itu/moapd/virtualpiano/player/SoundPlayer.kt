package dk.itu.moapd.virtualpiano.player

import android.content.Context
import android.media.MediaPlayer
import android.util.SparseArray
import android.util.SparseIntArray
import dk.itu.moapd.virtualpiano.R

class SoundPlayer(private var context: Context) {

    private val threads: SparseArray<PlayThread?> = SparseArray()
    private val SOUND_MAP = SparseIntArray()

    init {
        // White keys.
        SOUND_MAP.put(1, R.raw.c4)
        SOUND_MAP.put(2, R.raw.d4)
        SOUND_MAP.put(3, R.raw.e4)
        SOUND_MAP.put(4, R.raw.f4)
        SOUND_MAP.put(5, R.raw.g4)
        SOUND_MAP.put(6, R.raw.a4)
        SOUND_MAP.put(7, R.raw.b4)
        SOUND_MAP.put(8, R.raw.c5)

        // Black keys.
        SOUND_MAP.put(9, R.raw.db4)
        SOUND_MAP.put(10, R.raw.eb4)
        SOUND_MAP.put(11, R.raw.gb4)
        SOUND_MAP.put(12, R.raw.ab4)
        SOUND_MAP.put(13, R.raw.bb4)
    }

    fun playNote(note: Int) {
        if (isNotPlaying(note)) {
            val thread = PlayThread(note)
            thread.start()
            threads.put(note, thread)
        }
    }

    fun stopNote(note: Int) {
        val thread: PlayThread? = threads.get(note)
        if (thread != null) {
            threads.remove(note)
            try {
                Thread.currentThread()
                Thread.sleep(100)
                thread.mediaPlayer.stop()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    fun isNotPlaying(note: Int) =
        threads.get(note) == null

    inner class PlayThread(var note: Int) : Thread() {

        lateinit var mediaPlayer: MediaPlayer

        override fun run() {
            super.run()
            try {
                val filename = SOUND_MAP[note]

                mediaPlayer = MediaPlayer.create(context, filename)
                mediaPlayer.start()

                while (mediaPlayer.currentPosition < mediaPlayer.duration &&
                       mediaPlayer.isPlaying)
                    sleep(100)

                mediaPlayer.stop()

            } catch (ex: InterruptedException) {
                ex.printStackTrace()
            } finally {
                mediaPlayer.release()
            }
        }
    }
}

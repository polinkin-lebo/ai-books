package dev.aibooks.reader;

import android.content.Context;
import android.media.*;
import android.os.Handler;
import android.os.Looper;

final class Narration {
    private final MainActivity host;
    private final AudioManager audio;
    private final AudioFocusRequest request;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private MediaPlayer player;
    boolean dog;
    private final Runnable update =
            new Runnable() {
                public void run() {
                    if (!playing()) return;
                    host.playbackTick(player.getCurrentPosition());
                    handler.postDelayed(this, 240);
                }
            };

    Narration(MainActivity host) {
        this.host = host;
        audio = (AudioManager) host.getSystemService(Context.AUDIO_SERVICE);
        AudioAttributes attrs =
                new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build();
        request =
                new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                        .setAudioAttributes(attrs)
                        .setWillPauseWhenDucked(true)
                        .setOnAudioFocusChangeListener(
                                change -> {
                                    if (change < 0) host.pausePlayback();
                                },
                                handler)
                        .build();
    }

    boolean playing() {
        return player != null && player.isPlaying();
    }

    int position() {
        return player == null ? 0 : player.getCurrentPosition();
    }

    void start(boolean nextDog, int position) {
        close();
        if (audio.requestAudioFocus(request) != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            host.notice("Не удалось включить звук");
            return;
        }
        dog = nextDog;
        player =
                MediaPlayer.create(
                        host,
                        dog ? R.raw.chekhov : R.raw.anna,
                        request.getAudioAttributes(),
                        AudioManager.AUDIO_SESSION_ID_GENERATE);
        if (player == null) {
            close();
            host.notice("Не удалось открыть аудиофрагмент");
            return;
        }
        player.setOnErrorListener(
                (mp, w, e) -> {
                    close();
                    host.audioEnded(false);
                    host.notice("Не удалось воспроизвести фрагмент");
                    return true;
                });
        player.setOnCompletionListener(
                mp -> {
                    close();
                    host.audioEnded(true);
                });
        seek(position);
        player.start();
        handler.postDelayed(update, 240);
    }

    void seek(int ms) {
        if (player != null)
            player.seekTo(
                    Math.max(0, Math.min(ms, player.getDuration())), MediaPlayer.SEEK_CLOSEST);
    }

    void close() {
        handler.removeCallbacks(update);
        if (player != null) {
            player.release();
            player = null;
        }
        audio.abandonAudioFocusRequest(request);
    }
}

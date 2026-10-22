package com.gathin.tvhome;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;

public final class AdhanPlayer {
    private static final String TAG = "AdhanPlayer";
    private MediaPlayer player;
    private AudioManager audioManager;
    private AudioFocusRequest focusRequest;
    private Runnable onCompleteCallback;

    public void play(Context context, boolean isSubuh, Runnable onFinish) {
        int rawRes = isSubuh ? R.raw.adzan_subuh : R.raw.adzan;
        play(context, rawRes, onFinish);
    }

    public void play(Context context, Runnable onFinish) {
        play(context, false, onFinish);
    }

    public void play(Context context, int rawResId, Runnable onFinish) {
        stop();
        this.onCompleteCallback = onFinish;
        try {
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                            .setAudioAttributes(new AudioAttributes.Builder()
                                    .setUsage(AudioAttributes.USAGE_ALARM)
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .build())
                            .build();
                    audioManager.requestAudioFocus(focusRequest);
                } else {
                    audioManager.requestAudioFocus(null, AudioManager.STREAM_ALARM, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
                }
            }

            player = MediaPlayer.create(context, rawResId);
            if (player == null) {
                Log.e(TAG, "Failed to create MediaPlayer for resId: " + rawResId);
                if (onCompleteCallback != null) onCompleteCallback.run();
                return;
            }

            player.setOnCompletionListener(mp -> stop());
            player.setOnErrorListener((mp, what, extra) -> {
                stop();
                return true;
            });
            player.start();
        } catch (Exception e) {
            Log.e(TAG, "Error playing adhan: " + e.getMessage(), e);
            stop();
        }
    }

    public void stop() {
        if (player != null) {
            try {
                if (player.isPlaying()) {
                    player.stop();
                }
                player.release();
            } catch (Exception ignored) {}
            player = null;
        }
        if (audioManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && focusRequest != null) {
                audioManager.abandonAudioFocusRequest(focusRequest);
                focusRequest = null;
            } else {
                audioManager.abandonAudioFocus(null);
            }
        }
        if (onCompleteCallback != null) {
            Runnable cb = onCompleteCallback;
            onCompleteCallback = null;
            cb.run();
        }
    }

    public boolean isPlaying() {
        try {
            return player != null && player.isPlaying();
        } catch (Exception e) {
            return false;
        }
    }
}

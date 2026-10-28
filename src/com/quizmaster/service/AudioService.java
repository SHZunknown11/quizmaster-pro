package com.quizmaster.service;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;

/**
 * Procedural audio feedback service using standard javax.sound.sampled.
 * Generates subtle UI sound effects without external audio files:
 * - Click/Tick
 * - Correct answer chime
 * - Incorrect answer thud
 * - Quiz complete fanfare chime
 */
public class AudioService {

    private static boolean soundEnabled = true;

    public static boolean isSoundEnabled() {
        return soundEnabled;
    }

    public static void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
    }

    /**
     * Subtle button tap sound.
     */
    public static void playClick() {
        if (!soundEnabled) return;
        playToneAsync(600, 45, 0.25f, WaveType.SINE);
    }

    /**
     * Crisp, pleasant chime for correct answers (two rising harmonic tones).
     */
    public static void playCorrect() {
        if (!soundEnabled) return;
        new Thread(() -> {
            try {
                playTone(523, 70, 0.4f, WaveType.SINE); // C5
                Thread.sleep(60);
                playTone(659, 110, 0.45f, WaveType.SINE); // E5
            } catch (Exception ignored) {}
        }).start();
    }

    /**
     * Soft, low-pitch double thud for incorrect answers.
     */
    public static void playWrong() {
        if (!soundEnabled) return;
        new Thread(() -> {
            try {
                playTone(220, 80, 0.35f, WaveType.TRIANGLE); // A3
                Thread.sleep(70);
                playTone(185, 120, 0.35f, WaveType.TRIANGLE); // F#3
            } catch (Exception ignored) {}
        }).start();
    }

    /**
     * Fanfare chime when completing a quiz.
     */
    public static void playComplete() {
        if (!soundEnabled) return;
        new Thread(() -> {
            try {
                playTone(440, 80, 0.35f, WaveType.SINE);
                Thread.sleep(70);
                playTone(554, 80, 0.35f, WaveType.SINE);
                Thread.sleep(70);
                playTone(659, 140, 0.45f, WaveType.SINE);
            } catch (Exception ignored) {}
        }).start();
    }

    private static void playToneAsync(int freqHz, int durationMs, float volume, WaveType type) {
        new Thread(() -> playTone(freqHz, durationMs, volume, type)).start();
    }

    private static void playTone(int freqHz, int durationMs, float volume, WaveType type) {
        try {
            float sampleRate = 44100f;
            int numSamples = (int) (sampleRate * (durationMs / 1000.0));
            byte[] buffer = new byte[numSamples];

            for (int i = 0; i < numSamples; i++) {
                double period = sampleRate / freqHz;
                double angle = 2.0 * Math.PI * i / period;
                double sample = 0;

                switch (type) {
                    case SINE:
                        sample = Math.sin(angle);
                        break;
                    case TRIANGLE:
                        sample = (2.0 / Math.PI) * Math.asin(Math.sin(angle));
                        break;
                }

                // Envelope: smooth fade out to avoid clicks
                double envelope = 1.0;
                int fadeOutSamples = Math.min(numSamples / 3, 2000);
                if (i > numSamples - fadeOutSamples) {
                    envelope = (double) (numSamples - i) / fadeOutSamples;
                }

                buffer[i] = (byte) (sample * volume * envelope * 127.0);
            }

            AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, false);
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            if (!AudioSystem.isLineSupported(info)) {
                return;
            }

            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(format, buffer, 0, buffer.length);
            clip.start();
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });
        } catch (Exception ignored) {
            // Audio is optional and non-blocking
        }
    }

    private enum WaveType {
        SINE, TRIANGLE
    }
}

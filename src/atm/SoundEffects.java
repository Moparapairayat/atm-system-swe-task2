package atm;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

/** Lightweight, generated ATM-style sound effects; no external audio files are required. */
public final class SoundEffects {
    private static final float RATE = 22_050f;

    private SoundEffects() { }

    public static void keypad() { play(new int[]{1120}, new int[]{45}); }
    public static void accepted() { play(new int[]{740, 988}, new int[]{90, 130}); }
    public static void warning() { play(new int[]{250, 190}, new int[]{120, 180}); }
    public static void cardReader() { play(new int[]{430, 560, 680}, new int[]{80, 60, 80}); }
    public static void cardEject() { play(new int[]{680, 560, 430}, new int[]{80, 60, 90}); }
    public static void cashDispenser() { play(new int[]{110, 125, 140, 160}, new int[]{130, 130, 130, 180}); }
    public static void receiptPrinter() { play(new int[]{1800, 1500, 1800}, new int[]{45, 45, 70}); }

    private static void play(int[] frequencies, int[] durations) {
        Thread soundThread = new Thread(() -> {
            try {
                AudioFormat format = new AudioFormat(RATE, 8, 1, true, false);
                try (SourceDataLine line = AudioSystem.getSourceDataLine(format)) {
                    line.open(format); line.start();
                    for (int i = 0; i < frequencies.length; i++) tone(line, frequencies[i], durations[i]);
                    line.drain();
                }
            } catch (Exception ignored) {
                // Audio support differs by device; ATM functionality must still work without it.
            }
        }, "atm-sound");
        soundThread.setDaemon(true);
        soundThread.start();
    }

    private static void tone(SourceDataLine line, int frequency, int milliseconds) {
        int samples = (int) (RATE * milliseconds / 1000);
        byte[] data = new byte[samples];
        for (int i = 0; i < samples; i++) {
            double fade = Math.min(1.0, Math.min(i / 70.0, (samples - i) / 70.0));
            data[i] = (byte) (Math.sin(2 * Math.PI * i * frequency / RATE) * 42 * fade);
        }
        line.write(data, 0, data.length);
    }
}

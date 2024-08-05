package dev.nottekk.notvolt.utils.data;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A utility class to work with Audio files.
 */
public class AudioUtil {

    /**
     * Constructor for the AudioUtil class.
     */
    private AudioUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * WAV sample rate.
     */
    private static final long SAMPLE_RATE = 48000L;

    /**
     * Audio channels.
     */
    private static final int CHANNELS = 2;

    /**
     * Convert a PCM ByteBuffer into a WAV encoded byte array.
     * @param byteBuffer The PCM ByteBuffer.
     * @return The WAV encoded byte array.
     * @throws IOException If an error occurs.
     */
    public static byte[] convertPCMtoWAV(ByteBuffer byteBuffer) throws IOException {
        AudioInputStream audioInputStream =
                AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED,
                        new AudioInputStream(new ByteArrayInputStream(byteBuffer.array()),
                                new AudioFormat(SAMPLE_RATE, 16, CHANNELS, true, true), byteBuffer.limit()));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }
}

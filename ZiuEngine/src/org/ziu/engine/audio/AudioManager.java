package org.ziu.engine.audio;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.openal.AL;
import static org.lwjgl.openal.AL10.*;
import org.lwjgl.openal.ALC;
import static org.lwjgl.openal.ALC10.*;
import org.lwjgl.openal.ALCCapabilities;
import org.ziu.engine.graphics.Camera;
import org.ziu.engine.graphics.Transformation;

import static org.lwjgl.system.MemoryUtil.NULL;

public class AudioManager {

    private long device;

    private long context;

    private AudioListener listener;

    private final List<AudioBuffer> audioBufferList;

    private final Map<String, AudioSource> audioSourceMap;

    private final Matrix4f cameraMatrix;

    public AudioManager() {
        audioBufferList = new ArrayList<>();
        audioSourceMap = new HashMap<>();
        cameraMatrix = new Matrix4f();
    }

    public void init() throws Exception {
        this.device = alcOpenDevice((ByteBuffer) null);
        if (device == NULL) {
            throw new IllegalStateException("Failed to open the default OpenAL device.");
        }
        ALCCapabilities deviceCaps = ALC.createCapabilities(device);
        this.context = alcCreateContext(device, (IntBuffer) null);
        if (context == NULL) {
            throw new IllegalStateException("Failed to create OpenAL context.");
        }
        alcMakeContextCurrent(context);
        AL.createCapabilities(deviceCaps);
    }

    public void addAudioSource(String name, AudioSource audioSource) {
        this.audioSourceMap.put(name, audioSource);
    }

    public AudioSource getAudioSource(String name) {
        return this.audioSourceMap.get(name);
    }

    public void playAudioSource(String name) {
        AudioSource audioSource = this.audioSourceMap.get(name);
        if (audioSource != null && !audioSource.isPlaying()) {
            audioSource.play();
        }
    }

    public void removeAudioSource(String name) {
        this.audioSourceMap.remove(name);
    }

    public void addAudioBuffer(AudioBuffer audioBuffer) {
        this.audioBufferList.add(audioBuffer);
    }

    public AudioListener getListener() {
        return this.listener;
    }

    public void setListener(AudioListener listener) {
        this.listener = listener;
    }

    public void updateListenerPosition(Camera camera) {
        // Update camera matrix with camera data
        Transformation.updateGenericViewMatrix(camera.getPosition(), camera.getRotation(), cameraMatrix);
        
        listener.setPosition(camera.getPosition());
        Vector3f at = new Vector3f();
        cameraMatrix.positiveZ(at).negate();
        Vector3f up = new Vector3f();
        cameraMatrix.positiveY(up);
        listener.setOrientation(at, up);
    }

    public void setAttenuationModel(int model) {
        alDistanceModel(model);
    }
    
    public void cleanup() {
        for (AudioSource audioSource : audioSourceMap.values()) {
            audioSource.cleanup();
        }
        audioSourceMap.clear();
        for (AudioBuffer audioBuffer : audioBufferList) {
            audioBuffer.cleanup();
        }
        audioBufferList.clear();
        if (context != NULL) {
            alcDestroyContext(context);
        }
        if (device != NULL) {
            alcCloseDevice(device);
        }
    }
}
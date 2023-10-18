package org.example;

public class Radio {
    private final int MAX_VOLUME = 100;
    private final int MIN_VOLUME = 0;
    private final int VOLUME_CHANGE = 10;
    private boolean isPlaying = false;
    private int volume = 50;  // in %

    private float frequency = 89.8f;  // in MHz

    public void turnOn() {
        this.isPlaying = true;
    }

    public void turnOff() {
        this.isPlaying = false;
    }

    public void lowerVolume() {
        if (this.MIN_VOLUME < volume) {
            this.volume -= VOLUME_CHANGE;
        }

    }

    public void upVolume() {
        if (volume < this.MAX_VOLUME) {
            this.volume += VOLUME_CHANGE;
        }
    }

    public void changeFrequency(float frequency) {
        this.frequency = frequency;
    }

    public boolean isPlaying() {
        return this.isPlaying;
    }

    public int getVolume() {
        return this.volume;
    }

    public float getFrequency() {
        return this.frequency;
    }
}

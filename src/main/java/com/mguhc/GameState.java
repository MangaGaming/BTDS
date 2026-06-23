package com.mguhc;

public enum GameState {
    WAITING,
    PLAYING,
    ENDED;

    public boolean isWaiting() {
        return this == WAITING;
    }

    public boolean isPlaying() {
        return this == PLAYING;
    }

    public boolean isEnded() {
        return this == ENDED;
    }
}

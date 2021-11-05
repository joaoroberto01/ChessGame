package com.jrgc.chessgame.models;

public class MoveEvent {
    private final BoardPosition source, destination;
    private boolean success, captured;

    public MoveEvent(BoardPosition source, BoardPosition destination) {
        this.source = source;
        this.destination = destination;
    }

    public BoardPosition getSource() {
        return source;
    }

    public BoardPosition getDestination() {
        return destination;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
        if (!success)
            captured = false;
    }

    public boolean hasCaptured() {
        return captured;
    }

    public void setCaptured(boolean captured) {
        this.captured = captured;
    }
}

package com.jrgc.chessgame.models.game;

import com.jrgc.chessgame.models.pieces.Piece;

public class MoveEvent {

    public enum CastlingType {
        NONE, SHORT, LONG;
    }

    private final BoardPosition source, destination;
    private boolean success, pawnPromoted;
    private Piece capturedPiece;

    private CastlingType castlingType = CastlingType.NONE;

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
    }

    public boolean hasCaptured() {
        return capturedPiece != null;
    }

    public Piece getCapturedPiece(){
        return capturedPiece;
    }

    public void setCapturedPiece(Piece capturedPiece) {
        this.capturedPiece = capturedPiece;
    }

    public boolean isPawnPromoted() {
        return pawnPromoted;
    }

    public void setPawnPromoted(boolean pawnPromoted) {
        this.pawnPromoted = pawnPromoted;
    }

    public CastlingType getCastlingType() {
        return castlingType;
    }

    public void setCastlingType(CastlingType castlingType) {
        this.castlingType = castlingType;
    }
}

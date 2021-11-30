package com.jrgc.chessgame.models.game;

import com.jrgc.chessgame.models.pieces.Piece;

public class MoveEvent {



    public enum CastlingType {
        NONE, SHORT, LONG;
    }

    public enum AmbiguitityType {
        NONE, ROW, COLUMN;
    }

    private final BoardPosition source, destination;
    private boolean success, check, checkmatte, pawnPromoted;
    private AmbiguitityType ambiguitityType;
    private Piece capturedPiece;
    private Piece.PieceType promotedPiece, movedPiece;

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

    public Piece.PieceType getPromotedPiece() {
        return promotedPiece;
    }

    public void setPromotedPiece(Piece.PieceType promotedPiece) {
        this.promotedPiece = promotedPiece;
    }

    public Piece.PieceType getMovedPiece() {
        return movedPiece;
    }

    public void setMovedPiece(Piece.PieceType movedPiece) {
        this.movedPiece = movedPiece;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public boolean isCheckmatte() {
        return checkmatte;
    }

    public void setCheckmatte(boolean checkmatte) {
        this.checkmatte = checkmatte;
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

    public AmbiguitityType getAmbiguity() {
        return ambiguitityType;
    }

    public void setAmbiguitityType(AmbiguitityType ambiguitityType) {
        this.ambiguitityType = ambiguitityType;
    }
}

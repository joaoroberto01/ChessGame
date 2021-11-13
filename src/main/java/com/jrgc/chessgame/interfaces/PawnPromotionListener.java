package com.jrgc.chessgame.interfaces;

import com.jrgc.chessgame.models.pieces.Piece;

public interface PawnPromotionListener {
    void onPromotionPieceSelected(Piece.PieceType pieceType);
}

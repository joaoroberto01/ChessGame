package com.jrgc.chessgame.models;

public enum SoundAlert {
    MOVE, INVALID, CAPTURE, PAWN_PROMOTION, CHECK, CHECKMATTE, DRAW;

    public String getUrl(){
        return "sounds/" + toString().toLowerCase() + ".mp3";
    }
}

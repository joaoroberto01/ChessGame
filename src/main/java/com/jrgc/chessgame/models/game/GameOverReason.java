package com.jrgc.chessgame.models.game;

public enum GameOverReason {
    TIME_OVER, SURRENDER, DRAW_DEAL, CHECKMATTE;

    @Override
    public String toString() {
        return switch (this){
            case TIME_OVER -> "Tempo esgotado";
            case SURRENDER -> "Rendição";
            case DRAW_DEAL -> "Acordo";
            case CHECKMATTE -> "Chequemate";
        };
    }
}

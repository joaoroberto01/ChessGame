package com.jrgc.chessgame.models.game;

public enum DrawType {
    STALEMATE, FIFTY_MOVES, INSUFFICIENT_MATERIAL;

    @Override
    public String toString() {
        return switch (this){
            case STALEMATE -> "Afogamento";
            case FIFTY_MOVES -> "50 movimentos sem captura ou movimento de peões";
            case INSUFFICIENT_MATERIAL -> "Material insuficiente";
        };
    }
}

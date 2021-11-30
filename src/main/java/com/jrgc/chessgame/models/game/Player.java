package com.jrgc.chessgame.models.game;

import com.jrgc.chessgame.Settings;

public enum Player {
    WHITE, BLACK;

    public static Player get(int index){
        if (index <= 1)
            return Player.BLACK;
        else if(index >= 6)
            return Player.WHITE;

        return null;
    }

    public static Player getOpponent(Player current){
        return current == WHITE ? BLACK : WHITE;
    }

    public int getIndex(){
        return this == Player.WHITE ? 0 : 1;
    }

    @Override
    public String toString() {
        return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
    }
}

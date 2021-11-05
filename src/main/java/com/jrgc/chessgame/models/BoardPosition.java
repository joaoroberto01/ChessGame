package com.jrgc.chessgame.models;

import com.jrgc.chessgame.BoardUtils;

public class BoardPosition {
    public int line, column;

    public BoardPosition(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public BoardPosition delta(BoardPosition boardPosition){
        return new BoardPosition(line - boardPosition.line, column - boardPosition.column);
    }

    public BoardPosition deltaAbs(BoardPosition boardPosition){
        int line = Math.abs(this.line - boardPosition.line);
        int column = Math.abs(this.column - boardPosition.column);

        return new BoardPosition(line, column);
    }

    public String toFormattedString(){
        int number = Math.abs(line - (BoardUtils.BOARD_SIZE - 1)) + 1;

        return String.format("%c%d", column + 65, number);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BoardPosition that = (BoardPosition) o;

        return line == that.line && column == that.column;
    }

    @Override
    public String toString() {
        return "BoardPosition{" +
                "line=" + line +
                ", column=" + column +
                '}';
    }
}

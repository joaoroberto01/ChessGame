package com.jrgc.chessgame.models;

import com.jrgc.chessgame.utils.BoardUtils;

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

    public boolean isBetweenPositions(BoardPosition start, BoardPosition end){
        BoardPosition delta = start.deltaAbs(end);
        if (delta.column == delta.line)
            return isInRange(this.line, start.line, end.line) && isInRange(this.column, start.column, end.column);
        else if (delta.column == 0)
            return this.column == start.column && isInRange(this.line, start.line, end.line);
        else if (delta.line == 0)
            return this.line == start.line && isInRange(this.column, start.column, end.column);

        return false;
    }

    public static boolean isInRange(int value, int a, int b){
        return value <= Math.max(a, b) && value >= Math.min(a,b);
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

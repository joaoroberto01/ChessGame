package com.jrgc.chessgame.models.game;

import com.jrgc.chessgame.utils.BoardUtils;

public class BoardPosition {
    public int row, column;

    public BoardPosition(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public BoardPosition delta(BoardPosition boardPosition){
        return new BoardPosition(row - boardPosition.row, column - boardPosition.column);
    }

    public BoardPosition deltaAbs(BoardPosition boardPosition){
        int row = Math.abs(this.row - boardPosition.row);
        int column = Math.abs(this.column - boardPosition.column);

        return new BoardPosition(row, column);
    }

    public String formattedRow(){
        int number = Math.abs(row - BoardUtils.BOARD_SIZE);
        //int number = Math.abs(row - (BoardUtils.BOARD_SIZE - 1)) + 1;

        return String.valueOf(number);
    }

    public String formattedColumn(){
        return String.format("%c", column + 97);
    }

    public String toFormattedString(){
        return formattedColumn() + formattedRow();
    }

    public boolean isBetweenPositions(BoardPosition start, BoardPosition end){
        BoardPosition delta = start.deltaAbs(end);
        if (delta.column == delta.row)
            return isInRange(this.row, start.row, end.row) && isInRange(this.column, start.column, end.column);
        else if (delta.column == 0)
            return this.column == start.column && isInRange(this.row, start.row, end.row);
        else if (delta.row == 0)
            return this.row == start.row && isInRange(this.column, start.column, end.column);

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

        return row == that.row && column == that.column;
    }

    @Override
    public String toString() {
        return "BoardPosition{" +
                "row=" + row +
                ", column=" + column +
                '}';
    }
}

package com.jrgc.chessgame.models;

import com.jrgc.chessgame.GameState;
import com.jrgc.chessgame.models.pieces.Piece;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class GameTurnLog {
    private final Piece piece;
    private final MoveEvent moveEvent;

    public GameTurnLog(Piece piece, MoveEvent moveEvent) {
        this.piece = piece;
        this.moveEvent = moveEvent;
    }

    public Piece getPiece(){
        return piece;
    }

    public MoveEvent getMoveEvent() {
        return moveEvent;
    }

    public void writeToFile(){
        try {
            String from = moveEvent.getSource().toFormattedString();
            String to = moveEvent.getDestination().toFormattedString();

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("match_log.txt", true));
            if (piece != null)
                bufferedWriter.append(String.format("%s: ", piece.getPlayer() + " " + piece.getPieceType()));
            else
                bufferedWriter.append("null: ");
            bufferedWriter.append(String.format("%s -> %s", from, to));
            bufferedWriter.append("\n");
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clearLog(){
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("match_log.txt"));
            bufferedWriter.write("");
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

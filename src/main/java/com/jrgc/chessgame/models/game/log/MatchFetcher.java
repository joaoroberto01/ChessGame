package com.jrgc.chessgame.models.game.log;

import com.jrgc.chessgame.models.game.BoardPosition;
import com.jrgc.chessgame.models.game.MoveEvent;
import com.jrgc.chessgame.models.game.Player;
import com.jrgc.chessgame.models.pieces.Piece;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MatchFetcher {
    private final String fileUrl;
    private String date, whitePlayer, blackPlayer;
    private Player winner;

    public MatchFetcher(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getDate() {
        return date;
    }

    public String getWhitePlayer() {
        return whitePlayer;
    }

    public String getBlackPlayer() {
        return blackPlayer;
    }

    public Player getWinner() {
        return winner;
    }

    public List<MoveEvent> fetch(){
        try {
            List<MoveEvent> moves = new ArrayList<>();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileUrl));

            bufferedReader.lines().forEach(line -> {
                if (line.isEmpty())
                    return;

                int begin = line.indexOf("\"") + 1;
                int end = line.lastIndexOf("\"");
                if (line.contains("Date"))
                    date = line.substring(begin, end);
                else if (line.contains("White"))
                    whitePlayer = line.substring(begin, end);
                else if (line.contains("Black"))
                    blackPlayer = line.substring(begin, end);
                else if (line.contains(".")){
                    String[] logs = line.substring(line.indexOf(".") + 2).split(" ");

                    MoveEvent whiteTurn = MatchFetcher.moveFrom(logs[0]);

                    moves.add(whiteTurn);
                    if (logs.length > 1)
                        moves.add(MatchFetcher.moveFrom(logs[1]));
                }else if (line.contains("-")){
                    winner = null;
                    if (line.equals("1-0"))
                        winner = Player.WHITE;
                    else if (line.equals("0-1"))
                        winner = Player.BLACK;
                }
            });

            bufferedReader.close();
            return moves;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static MoveEvent moveFrom(String log){
        MoveEvent castlingMoveEvent = new MoveEvent(null, null);
        if (log.equals("O-O-O")){
            castlingMoveEvent.setCastlingType(MoveEvent.CastlingType.LONG);
            return castlingMoveEvent;
        }

        if (log.equals("O-O")){
            castlingMoveEvent.setCastlingType(MoveEvent.CastlingType.SHORT);
            return castlingMoveEvent;
        }

        log = log.replaceAll("[+#x]", "");
        log = new StringBuilder(log).reverse().toString();

        if (log.length() == 0)
            return null;

        Piece.PieceType pieceType = Piece.PieceType.PAWN;
        Piece.PieceType promotedPiece = null;

        BoardPosition source = new BoardPosition(-1, -1);
        char char1 = log.charAt(0);
        int rowIndex = 0;

        if (char1 > '8'){
            promotedPiece = checkPieceType(char1);
            rowIndex = 1;
        }

        int row = charToRow(log.charAt(rowIndex));
        int column = log.charAt(rowIndex + 1) - 97;

        char char3 = log.length() > rowIndex + 2 ? log.charAt(rowIndex + 2) : 0;
        char char4 = log.length() > rowIndex + 3 ? log.charAt(rowIndex + 3) : 0;

        //if its a piece letter
        if (char3 > '8' && char3 < 'a') {
            pieceType = checkPieceType(char3);
        }else if (char3 >= 'a')
            source.column = char3 - 97;
        else if (char3 >= '0')
            source.row = charToRow(char3);

        if (char4 > '8' && char4 < 'a')
            pieceType = checkPieceType(char4);

        BoardPosition destination = new BoardPosition(row, column);

        MoveEvent moveEvent = new MoveEvent(source, destination);
        moveEvent.setPromotedPiece(promotedPiece);
        moveEvent.setMovedPiece(pieceType);

        return moveEvent;
    }

    private static int charToRow(char c){
        return Math.abs(c - 48 - 8);
    }

    private static Piece.PieceType checkPieceType(char c){
        for (Piece.PieceType p : Piece.PieceType.values()) {
            if (p != Piece.PieceType.PAWN && c == p.toLetter().charAt(0))
                return p;
        }

        return Piece.PieceType.PAWN;
    }
}

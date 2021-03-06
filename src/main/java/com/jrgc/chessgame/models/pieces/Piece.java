package com.jrgc.chessgame.models.pieces;

import com.jrgc.chessgame.Settings;
import com.jrgc.chessgame.utils.BoardUtils;
import com.jrgc.chessgame.ChessApplication;
import com.jrgc.chessgame.GameState;
import com.jrgc.chessgame.models.game.BoardPosition;
import com.jrgc.chessgame.models.game.Player;
import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.jrgc.chessgame.utils.BoardUtils.*;

public abstract class Piece {

    public enum PieceType {
        PAWN, ROOK, KNIGHT, BISHOP, KING, QUEEN;

        public static PieceType get(int i, int j){
            PieceType[] pieces = new PieceType[]{ROOK, KNIGHT, BISHOP, QUEEN, KING, BISHOP, KNIGHT, ROOK};
            if (i == 1 || i == 6)
                return PAWN;
            if (i == 0 || i == 7)
                return pieces[j];

            return null;
        }

        public String getPieceRule(){
            return switch (this){
                case KING -> King.getRule();
                case QUEEN -> Queen.getRule();
                case KNIGHT -> Knight.getRule();
                case ROOK -> Rook.getRule();
                case BISHOP -> Bishop.getRule();
                case PAWN -> Pawn.getRule();
            };
        }

        public String toFormattedString(){
            return switch (this){
                case KING -> "Rei";
                case QUEEN -> "Rainha";
                case ROOK -> "Torre";
                case KNIGHT -> "Cavalo";
                case BISHOP -> "Bispo";
                case PAWN -> "Peão";
            };
        }

        public String toLetter(){
            if (this == KNIGHT)
                return "N";
            if (this == PAWN)
                return "";

            return toString().charAt(0) + "";
        }

        @Override
        public String toString() {
            return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
        }
    }

    private final Player player;
    private final PieceType pieceType;

    private final BoardPosition boardPosition;
    private List<BoardPosition> possibleMoves = new ArrayList<>();
    private int moveCount = 0;

    protected Piece(Player player, PieceType pieceType, BoardPosition boardPosition){
        this.player = player;
        this.pieceType = pieceType;
        this.boardPosition = boardPosition;
    }

    public Player getPlayer() {
        return player;
    }

    public PieceType getPieceType() {
        return pieceType;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public void incrementMoveCount() {
        moveCount++;
    }

    public BoardPosition getBoardPosition() {
        return boardPosition;
    }

    public void setBoardPosition(int row, int column) {
        boardPosition.row = row;
        boardPosition.column = column;
    }

    public List<BoardPosition> getPossibleMoves(){
        return possibleMoves;
    }

    public void setPossibleMoves(){
        possibleMoves.clear();
        for (int i = 0; i < BOARD_SIZE; i++){
            for (int j = 0; j < BOARD_SIZE; j++){
                BoardPosition to = new BoardPosition(i, j);
                if (canMove(to))
                    possibleMoves.add(to);
            }
        }
    }

    public static Piece create(int i, int j){
        Player player = Player.get(i);
        PieceType pieceType = PieceType.get(i, j);

        if (player == null || pieceType == null)
            return null;

        BoardPosition boardPosition = new BoardPosition(i, j);

        return switch (pieceType){
            case PAWN -> new Pawn(player, boardPosition);
            case ROOK -> new Rook(player, boardPosition);
            case KNIGHT -> new Knight(player, boardPosition);
            case BISHOP -> new Bishop(player, boardPosition);
            case KING -> new King(player, boardPosition);
            case QUEEN -> new Queen(player, boardPosition);
        };
    }

    public Image getPieceImage() {
        return getPieceImage(getPieceType(), getPlayer());
    }

    public static Image getPieceImage(PieceType pieceType, Player player) {
        String folder = Settings.getInstance().getPieceStyle().getPath();
        String path = "images/" + folder + pieceType.toString().toLowerCase();
        path += player == Player.WHITE ? "_white.png" : "_black.png";

        InputStream imageInputStream = ChessApplication.class.getResourceAsStream(path);
        return imageInputStream != null ? new Image(imageInputStream) : null;
    }

    public boolean cantPreventCheckmatte(BoardPosition to){
        King king = BoardUtils.getPlayerKing(getPlayer());
        if (king == null)
            return true;

        GameState gameState = GameState.getInstance();
        Piece destinationPiece = gameState.getPieceAt(to);

        Player opponent = Player.getOpponent(getPlayer());
        if (!gameState.isCheck(opponent))
            return false;

        List<Piece> threats = king.getThreats();
        if (threats.contains(destinationPiece))
            return false;

        for (Piece threat : threats) {
            for (BoardPosition move : threat.getPossibleMoves())
                if (move.equals(to) && move.isBetweenPositions(threat.boardPosition, king.getBoardPosition()))
                    return false;
        }

        return true;
    }

    public abstract boolean canMove(BoardPosition to);

    public Piece copy() {
        return copy(pieceType);
    }

    protected Piece copy(PieceType pieceType){
        BoardPosition boardPosition = new BoardPosition(getBoardPosition().row, getBoardPosition().column);

        return switch (pieceType){
            case PAWN -> new Pawn(player, boardPosition);
            case ROOK -> new Rook(player, boardPosition);
            case KNIGHT -> new Knight(player, boardPosition);
            case BISHOP -> new Bishop(player, boardPosition);
            case KING -> new King(player, boardPosition);
            case QUEEN -> new Queen(player, boardPosition);
        };
    }

    public boolean equals(Piece piece){
        return getBoardPosition().equals(piece.getBoardPosition()) && getPieceType() == piece.getPieceType() && getPlayer() == piece.getPlayer();
    }

    @Override
    public String toString() {
        String name = "";
        if (player == Player.WHITE)
            name += "W.";
        else
            name += "B.";

        name += pieceType.toString().substring(0, 2);

        return name;
    }
}

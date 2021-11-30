package com.jrgc.chessgame.utils;

import com.jrgc.chessgame.models.game.DrawType;
import com.jrgc.chessgame.models.game.log.GameTurn;
import com.jrgc.chessgame.models.game.log.MatchLog;
import com.jrgc.chessgame.models.pieces.King;
import com.jrgc.chessgame.models.pieces.Pawn;
import com.jrgc.chessgame.models.pieces.Piece;
import com.jrgc.chessgame.GameState;
import com.jrgc.chessgame.models.game.BoardPosition;
import com.jrgc.chessgame.models.game.Player;

import java.util.List;

import static com.jrgc.chessgame.utils.BoardUtils.updateAllPossibleMoves;

public class Validator {
    public static boolean crossValidation(Piece currentPiece, int range, BoardPosition to){
        BoardPosition from = currentPiece.getBoardPosition();
        BoardPosition delta = from.deltaAbs(to);

        if (delta.column == delta.row) {
            int steprow = - Integer.compare(from.row, to.row);
            int stepColumn = - Integer.compare(from.column, to.column);

            return walkThrough(currentPiece, to, range, steprow, stepColumn);
        }

        return false;
    }

    public static boolean lineValidation(Piece currentPiece, int range, BoardPosition to){
        BoardPosition from = currentPiece.getBoardPosition();
        BoardPosition delta = from.deltaAbs(to);

        if (delta.column == 0 || delta.row == 0) {
            int steprow = - Integer.compare(from.row, to.row);
            int stepColumn = - Integer.compare(from.column, to.column);

            return walkThrough(currentPiece, to, range, steprow, stepColumn);
        }

        return false;
    }

    private static boolean walkThrough(Piece currentPiece, BoardPosition to, int range, int steprow, int stepColumn){
        GameState gameState = GameState.getInstance();

        Piece destinationPiece = gameState.getPieceAt(to);
        boolean playersDiff = destinationPiece != null && currentPiece.getPlayer() != destinationPiece.getPlayer();

        BoardPosition from = currentPiece.getBoardPosition();

        BoardPosition target = new BoardPosition(from.row, from.column);
        for (int i = 0; i < range; i++) {
            target.row += steprow;
            target.column += stepColumn;

            Piece piece = gameState.getPieceAt(target);
            if (target.equals(to))
                return piece == null || playersDiff;
            else if (piece != null || (target.row == 0 && steprow != 0) || (target.column == 0 && stepColumn != 0))
                return false;
        }

        return false;
    }

    public static boolean checkValidation(Player player){
        GameState gameState = GameState.getInstance();

        Player opponent = Player.getOpponent(player);
        King king = BoardUtils.getPlayerKing(opponent);
        if (king == null)
            return false;

        List<Piece> pieces = gameState.getPlayerPieces(player);

        king.getThreats().clear();

        boolean check = false;
        for (Piece piece : pieces){
            boolean contains = piece instanceof Pawn ?
                    ((Pawn) piece).isThreatTo(king.getBoardPosition()) : piece.getPossibleMoves().contains(king.getBoardPosition());

            check = check || contains;

            if (contains)
                king.getThreats().add(piece);
        }

        return check;
    }

    public static boolean checkmatteIfMove(Piece sourcePiece, BoardPosition to){
        Player opponent = Player.getOpponent(sourcePiece.getPlayer());

        BoardPosition from = sourcePiece.getBoardPosition();
        GameState gameState = GameState.getInstance(GameState.StateType.POSSIBILITY);

        Piece destinationPiece = gameState.getPieceAt(to);

        if (destinationPiece != null)
            gameState.getPlayerPieces(opponent).removeIf(piece -> piece.equals(destinationPiece));

        gameState.setPieceAt(sourcePiece.getBoardPosition(), to);
        gameState.setPieceAt((Piece) null, from);

        updateAllPossibleMoves();

        boolean check = Validator.checkValidation(opponent);

        GameState.setStateType(GameState.StateType.DEFAULT);

        return check;
    }

    public static boolean checkmatteValidation(Piece attackingPiece){
        GameState gameState = GameState.getInstance();

        Player player = attackingPiece.getPlayer();
        Player opponent = Player.getOpponent(player);

        King king = BoardUtils.getPlayerKing(opponent);
        if (king == null)
            return false;

        BoardPosition kingPosition = king.getBoardPosition();
        if (kingPosition == null)
            return false;

        updateAllPossibleMoves();

        return gameState.isCheck(player) && playerHasNoMoves(opponent);
    }

    public static boolean playerHasNoMoves(Player player){
        GameState gameState = GameState.getInstance();
        List<Piece> playerPieces = gameState.getPlayerPieces(player);

        boolean noOpponentMoves = true;
        for (Piece piece : playerPieces){
            piece.getPossibleMoves().removeIf(boardPosition -> checkmatteIfMove(piece, boardPosition));

            noOpponentMoves = noOpponentMoves && piece.getPossibleMoves().isEmpty();
        }

        return noOpponentMoves;
    }

    public static boolean piecesAreThreatTo(List<Piece> pieces, BoardPosition to){
        for (Piece piece : pieces){
            boolean isThreat = piece instanceof Pawn ?
                    ((Pawn) piece).isThreatTo(to) : piece.getPossibleMoves().contains(to);

            if (isThreat)
                return true;
        }

        return false;
    }

    public static DrawType draw() {
        GameState gameState = GameState.getInstance();

        Player currentPlayer = gameState.getCurrentPlayer();

        boolean stalemate = !gameState.isCheck() && Validator.playerHasNoMoves(currentPlayer);
        if (stalemate)
            return DrawType.STALEMATE;

        int moves = 0;

        for (GameTurn gameTurn : GameState.getGameTurnsLog()) {
            if (++moves >= 50)
                return DrawType.FIFTY_MOVES;

            if (gameTurn.getMoveEvent().hasCaptured() || gameTurn.getPiece() instanceof Pawn)
                moves = 0;
        }

        if (insufficientMaterial(Player.WHITE) && insufficientMaterial(Player.BLACK))
            return DrawType.INSUFFICIENT_MATERIAL;

        return null;
    }

    public static boolean insufficientMaterial(Player player){
        GameState gameState = GameState.getInstance();
        List<Piece> pieces = gameState.getPlayerPieces(player);

        if (pieces.size() == 0 || pieces.size() > 3)
            return false;

        int king = 0, knights = 0, bishop = 0;

        for (Piece piece : pieces){
            switch (piece.getPieceType()){
                case KING -> king++;
                case KNIGHT -> knights++;
                case BISHOP -> bishop++;
                default -> {
                    return false;
                }
            }
        }

        // rei e um bispo, o rei e um cavalo ou o rei e dois cavalos contra um rei sozinho
        //0 bishop, 0 knights
        //0 bishop, 1 knight
        //1 bishop, 0 knights
        //0 bishop, 2 knight vs 1 king

        if (king != 1)
            return false;

        return (bishop == 0 && knights < 2) || (bishop == 1 && knights == 0) ||
                (bishop == 0 && knights == 2 && isOpponentKingOnly(player));
    }

    private static boolean isOpponentKingOnly(Player player){
        GameState gameState = GameState.getInstance();
        Player opponent = Player.getOpponent(player);

        for (Piece piece : gameState.getPlayerPieces(opponent)){
            if (!(piece instanceof King))
                return false;
        }

        return true;
    }
}

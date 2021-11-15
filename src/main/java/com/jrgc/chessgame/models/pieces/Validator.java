package com.jrgc.chessgame.models.pieces;

import com.jrgc.chessgame.utils.BoardUtils;
import com.jrgc.chessgame.GameState;
import com.jrgc.chessgame.models.game.BoardPosition;
import com.jrgc.chessgame.models.game.Player;

import java.util.List;

import static com.jrgc.chessgame.utils.BoardUtils.updateAllPossibleMoves;

public class Validator {
    protected static boolean crossValidation(Piece currentPiece, int range, BoardPosition to){
        BoardPosition from = currentPiece.getBoardPosition();
        BoardPosition delta = from.deltaAbs(to);

        if (delta.column == delta.line) {
            int stepLine = - Integer.compare(from.line, to.line);
            int stepColumn = - Integer.compare(from.column, to.column);

            return walkThrough(currentPiece, to, range, stepLine, stepColumn);
        }

        return false;
    }

    protected static boolean lineValidation(Piece currentPiece, int range, BoardPosition to){
        BoardPosition from = currentPiece.getBoardPosition();
        BoardPosition delta = from.deltaAbs(to);

        if (delta.column == 0 || delta.line == 0) {
            int stepLine = - Integer.compare(from.line, to.line);
            int stepColumn = - Integer.compare(from.column, to.column);

            return walkThrough(currentPiece, to, range, stepLine, stepColumn);
        }

        return false;
    }

    private static boolean walkThrough(Piece currentPiece, BoardPosition to, int range, int stepLine, int stepColumn){
        GameState gameState = GameState.getInstance();

        Piece destinationPiece = gameState.getPieceAt(to);
        boolean playersDiff = destinationPiece != null && currentPiece.getPlayer() != destinationPiece.getPlayer();

        BoardPosition from = currentPiece.getBoardPosition();

        BoardPosition target = new BoardPosition(from.line, from.column);
        for (int i = 0; i < range; i++) {
            target.line += stepLine;
            target.column += stepColumn;

            Piece piece = gameState.getPieceAt(target);
            if (target.equals(to))
                return piece == null || playersDiff;
            else if (piece != null || (target.line == 0 && stepLine != 0) || (target.column == 0 && stepColumn != 0))
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

    public static boolean draw() {
        GameState gameState = GameState.getInstance();

        Player currentPlayer = gameState.getCurrentPlayer();

        boolean stalemate = !gameState.isCheck() && Validator.playerHasNoMoves(currentPlayer);
        boolean fiftyMoves = false;

        return stalemate || fiftyMoves;
    }
}

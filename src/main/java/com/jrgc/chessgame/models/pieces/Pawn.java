package com.jrgc.chessgame.models.pieces;

import com.jrgc.chessgame.GameState;
import com.jrgc.chessgame.models.game.BoardPosition;
import com.jrgc.chessgame.models.game.log.GameTurn;
import com.jrgc.chessgame.models.game.log.MatchLog;
import com.jrgc.chessgame.models.game.MoveEvent;
import com.jrgc.chessgame.models.game.Player;

import java.util.List;

public class Pawn extends Piece {

    protected Pawn(Player player, BoardPosition boardPosition){
        super(player, PieceType.PAWN, boardPosition);
    }

    public Piece transform(PieceType pieceType){
        return copy(pieceType);
    }

    public boolean isThreatTo(BoardPosition boardPosition){
        for (BoardPosition possibleMove : getPossibleMoves()){
            if (possibleMove.equals(boardPosition)) {
                BoardPosition delta = getBoardPosition().delta(possibleMove);

                if (delta.column == 0)
                    continue;

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canMove(BoardPosition to) {
        GameState gameState = GameState.getInstance();

        BoardPosition from = getBoardPosition();
        Piece destinationPiece = gameState.getPieceAt(to);
        boolean playersDiff = destinationPiece != null && getPlayer() != destinationPiece.getPlayer();
        boolean verticalFree = true;

        if (cantPreventCheckmatte(to))
            return false;

        int range = getMoveCount() == 0 ? 2 : 1;

        BoardPosition delta = from.delta(to);
        if (getPlayer() == Player.BLACK)
            delta.row = -delta.row;

        BoardPosition target = new BoardPosition(from.row, from.column);
        for (int i = 0; i < delta.row; i++) {
            target.row += getPlayer() == Player.BLACK ? 1 : -1;

            Piece piece = gameState.getPieceAt(target);
            if (piece != null)
                verticalFree = false;
        }

        return (destinationPiece == null && verticalFree && delta.column == 0 && delta.row > 0 && delta.row <= range)
                || (playersDiff && Math.abs(delta.column) == 1 && delta.row == 1) || enPassantCapture(from, to) != null;
    }

    public Piece enPassantCapture(BoardPosition from, BoardPosition to) {
        List<GameTurn> gameTurns = GameState.getGameTurnsLog();
        if (gameTurns.isEmpty())
            return null;

        int lastIndex = gameTurns.size() - 1;
        GameTurn lastTurn = gameTurns.get(lastIndex);

        if (lastTurn.getPiece().getPieceType() != PieceType.PAWN)
            return null;

        MoveEvent lastMove = lastTurn.getMoveEvent();

        BoardPosition lastMoveDestination = lastMove.getDestination();
        BoardPosition lastMoveDelta = lastMove.getSource().delta(lastMoveDestination);

        BoardPosition delta = from.delta(to);
        if (getPlayer() == Player.BLACK)
            delta.row = -delta.row;

        boolean lastMoveVertical = Math.abs(lastMoveDelta.row) == 2 && lastMoveDelta.column == 0;
        boolean diagonalMove = Math.abs(delta.row) == Math.abs(delta.column) && delta.row == 1;
        boolean destinationIsBehind = to.column == lastMoveDestination.column && to.row != lastMoveDestination.row &&
                BoardPosition.isInRange(to.row, lastMoveDestination.row, lastMoveDestination.row + (getPlayer() == Player.WHITE ? -1 : 1));

        return lastMoveVertical && diagonalMove && destinationIsBehind ? lastTurn.getPiece() : null;
    }

    public static String getRule(){
        return """
                O peão se movimenta verticalmente. É a única peça do xadrez que nunca retrocede no tabuleiro.
                No seu primeiro movimento, é possivel andar uma ou duas casas na mesma coluna.
                Após esse movimento é possível andar apenas uma casa.
                        
                Caso ele alcance a primeira fileira do adversário no tabuleiro, o jogador deve promover seu peão,
                escolhendo entre: dama, torre, bispo ou cavalo, mesmo que haja outras em jogo.
                        
                Captura apenas peças inimigas na diagonal a sua frente, andando apenas uma casa.
                Desse modo, qualquer peça pode parar a marcha de um peão.
                        
                O peão pode realizar o movimento especial "En passant",
                capturando o peão que andou duas casas na ultima jogada.
                Desde de que o peão adversário que andou na ultima jogada esteja ao seu lado.""";
    }
}

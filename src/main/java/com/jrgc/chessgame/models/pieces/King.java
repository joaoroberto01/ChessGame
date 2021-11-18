package com.jrgc.chessgame.models.pieces;

import com.jrgc.chessgame.GameState;
import com.jrgc.chessgame.models.game.BoardPosition;
import com.jrgc.chessgame.models.game.MoveEvent;
import com.jrgc.chessgame.models.game.Player;
import com.jrgc.chessgame.utils.Validator;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {
    private final List<Piece> threats = new ArrayList<>();

    protected King(Player player, BoardPosition boardPosition){
        super(player, PieceType.KING, boardPosition);
    }

    public List<Piece> getThreats() {
        return threats;
    }

    @Override
    public boolean canMove(BoardPosition to) {
        GameState gameState = GameState.getInstance();

        Player opponent = Player.getOpponent(getPlayer());
        if (gameState.isCheck(opponent)){
            List<Piece> pieces = gameState.getPlayerPieces(opponent);
            if (Validator.piecesAreThreatTo(pieces, to))
                return false;
        }

        boolean castling = castlingMove(to) != MoveEvent.CastlingType.NONE;

        return castling || Validator.crossValidation(this, 1, to)
                        || Validator.lineValidation(this, 1, to);
    }

    public MoveEvent.CastlingType castlingMove(BoardPosition to){
        GameState gameState = GameState.getInstance();

        if (gameState.isCheck(getPlayer()) || getMoveCount() != 0)
            return MoveEvent.CastlingType.NONE;

        BoardPosition delta = getBoardPosition().deltaAbs(to);
        BoardPosition from = getBoardPosition();

        if (delta.line != 0 || delta.column != 2)
            return MoveEvent.CastlingType.NONE;

        int stepColumn = - Integer.compare(from.column, to.column);
        int range = stepColumn == -1 ? 3 : 2;

        Player opponent = Player.getOpponent(getPlayer());
        List<Piece> opponentPieces = gameState.getPlayerPieces(opponent);

        BoardPosition target = new BoardPosition(from.line, from.column);
        for (int i = 0; i < range; i++) {
            target.column += stepColumn;

            if (target.column == -1 || target.column == 8)
                return MoveEvent.CastlingType.NONE;

            Piece piece = gameState.getPieceAt(target);
            if (piece != null || Validator.piecesAreThreatTo(opponentPieces, target))
                return MoveEvent.CastlingType.NONE;
        }

        target.column += stepColumn;

        if (target.column == -1 || target.column == 8)
            return MoveEvent.CastlingType.NONE;

        Piece rook = gameState.getPieceAt(target);
        if(!(rook instanceof Rook) || rook.getMoveCount() != 0)
            return MoveEvent.CastlingType.NONE;

        return stepColumn == -1 ? MoveEvent.CastlingType.LONG : MoveEvent.CastlingType.SHORT;
    }

    public static String getRule(){
        return """
                O rei se movimenta em todas as direções (diagonal, vertical ou horizontal), limitado a apenas uma casa,
                desde que estejam desobstruídas.
                
                O rei pode fazer um movimento especial chamado "roque" com a torre desde que:
                - Nenhuma das duas peças tenha sido movimentada durante a partida;
                - Não haja nenhuma peça amiga entre o rei e a torre;
                - Nenhuma das casas pelas quais o rei irá passar ou ficar esteja sob ataque de peça inimiga.

                Podendo ser feito o roque pequeno (torre do lado do rei) ou o grande (torre do lado da rainha).

                O rei pode capturar qualquer peça adversária com exceção do rei adversário.
                Um rei deverá manter distância mínima de duas casas do outro rei, senão será considerado um lance irregular.""";
    }
}

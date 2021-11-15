package com.jrgc.chessgame;

import com.jrgc.chessgame.models.game.*;
import com.jrgc.chessgame.models.pieces.Piece;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.List;

import static com.jrgc.chessgame.utils.BoardUtils.BOARD_SIZE;

public class GameState {
    //TODO REMOVE
    public void boardLog() {
        System.out.println(stateType);
        for (int i = 0; i < BOARD_SIZE; i++){
            for (int j = 0; j < BOARD_SIZE; j++){
                if (board[i][j] == null)
                    System.out.print("---- ");
                else
                    System.out.print(board[i][j] + " ");
            }
            System.out.println("");
        }
    }

    public enum StateType {
        DEFAULT, POSSIBILITY;
    }

    private static GameState gameState, possibilityState;
    private static GameStatus gameStatus = GameStatus.RUNNING;
    private static StateType stateType = StateType.DEFAULT;

    private static final List<GameTurnLog> gameTurnsLog = new ArrayList<>();

    private final Piece[][] board = new Piece[BOARD_SIZE][BOARD_SIZE];
    private final ImageView[][] boardImageViews = new ImageView[BOARD_SIZE][BOARD_SIZE];

    private Player currentPlayer = Player.BLACK;
    private boolean isWhiteCheck = false, isBlackCheck = false;
    private boolean isCheckmatte = false, isDraw = false, gameOver = false;

    private final List<Piece> whitePieces = new ArrayList<>();
    private final List<Piece> blackPieces = new ArrayList<>();

    private GameState(){
        if (stateType == StateType.DEFAULT)
            GameTurnLog.clearLog();
    }

    public static GameState getInstance(boolean reset){
        if (gameState == null || reset)
            gameState = new GameState();

        return stateType == StateType.DEFAULT ? gameState : possibilityState;
    }

    public static GameState getInstance(){
        return getInstance(false);
    }

    public static GameState getInstance(StateType stateType){
        setStateType(stateType);
        return getInstance(false);
    }

    public static void setStateType(StateType stateType){
        GameState.stateType = stateType;

        possibilityState = stateType == StateType.POSSIBILITY ? copyMainState() : null;
    }

    public static List<GameTurnLog> getGameTurnsLog() {
        return gameTurnsLog;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void toggleCurrentPlayer(){
        currentPlayer = Player.getOpponent(currentPlayer);
    }

    public boolean isCheck(Player player){
        return player == Player.WHITE ? isWhiteCheck : isBlackCheck;
    }

    public boolean isCheck(){
        return isWhiteCheck || isBlackCheck;
    }

    public void setCheck(Player player, boolean isCheck) {
        if (player == Player.WHITE)
            isWhiteCheck = isCheck;
        else
            isBlackCheck = isCheck;
    }

    public boolean isCheckmatte(){
        return isCheckmatte;
    }

    public void setCheckmatte(boolean isCheckmatte) {
        this.isCheckmatte = isCheckmatte;
    }

    public boolean isDraw(){
        return isDraw;
    }

    public void setDraw(boolean isDraw) {
        this.isDraw = isDraw;
    }

    public void setGameOver(boolean gameOver){
        this.gameOver = gameOver;
    }

    public boolean isGameOver(){
        return gameOver;
    }

    public GameStatus getGameStatus(){
        return gameStatus;
    }

    public void updateGameStatus(){
        if (isCheckmatte)
            gameStatus = GameStatus.CHECKMATTE;
        else if (isCheck())
            gameStatus = GameStatus.CHECK;
        else if (isDraw)
            gameStatus = GameStatus.DRAW;
        else
            gameStatus = GameStatus.RUNNING;

        setGameOver(isCheckmatte || isDraw);
    }

    public List<Piece> getPlayerPieces(Player player) {
        return player == Player.WHITE ? whitePieces : blackPieces;
    }

    public Piece getPieceAt(BoardPosition boardPosition) {
        if (boardPosition == null)
            return null;
        return board[boardPosition.line][boardPosition.column];
    }

    public Piece getPieceAt(int i, int j){
        return getPieceAt(new BoardPosition(i, j));
    }

    public void setPieceAt(Piece sourcePiece, BoardPosition boardPosition){
        if (sourcePiece != null)
            sourcePiece.setBoardPosition(boardPosition.line, boardPosition.column);
        board[boardPosition.line][boardPosition.column] = sourcePiece;
    }

    public void setPieceAt(BoardPosition piecePosition, BoardPosition destination){
        Piece piece = getPieceAt(piecePosition);

        setPieceAt(piece, destination);
    }

    public ImageView getPieceImageView(BoardPosition boardPosition){
        return boardImageViews[boardPosition.line][boardPosition.column];
    }

    public ImageView getPieceImageView(int i, int j){
        return getPieceImageView(new BoardPosition(i, j));
    }

    public void setPieceImageView(ImageView pieceImageView, BoardPosition boardPosition){
        boardImageViews[boardPosition.line][boardPosition.column] = pieceImageView;
    }

    private static GameState copyMainState(){
        GameState state = new GameState();

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                Piece piece = gameState.board[i][j];
                if (piece != null) {
                    piece = piece.copy();
                    if (piece.getPlayer() == Player.WHITE)
                        state.whitePieces.add(piece);
                    else
                        state.blackPieces.add(piece);
                }
                state.board[i][j] = piece;
            }
        }
        state.isWhiteCheck = gameState.isWhiteCheck;
        state.isBlackCheck = gameState.isBlackCheck;

        return state;
    }
}

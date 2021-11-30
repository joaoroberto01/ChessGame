package com.jrgc.chessgame.controllers;

import com.jrgc.chessgame.ChessApplication;
import com.jrgc.chessgame.GameState;
import com.jrgc.chessgame.Settings;
import com.jrgc.chessgame.interfaces.ConfirmationListener;
import com.jrgc.chessgame.models.SoundAlert;
import com.jrgc.chessgame.models.game.*;
import com.jrgc.chessgame.models.game.log.GameTurn;
import com.jrgc.chessgame.models.game.log.MatchFetcher;
import com.jrgc.chessgame.models.game.log.MatchLog;
import com.jrgc.chessgame.models.pieces.King;
import com.jrgc.chessgame.models.pieces.Pawn;
import com.jrgc.chessgame.models.pieces.Piece;
import com.jrgc.chessgame.utils.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

import static com.jrgc.chessgame.utils.BoardUtils.*;

public class SimulateGameController {

    @FXML
    private Button nextMoveButton;

    @FXML
    private ListView<MatchLog> gameLogListView;

    @FXML
    private Label turnLabel;

    @FXML
    private GridPane boardGridView;

    private final Settings settings = Settings.getInstance();

    private BoardPosition sourcePosition = null, destinationPosition = null;
    private GameState gameState;
    private MoveEvent simulationMoveEvent;

    private int i;
    private MatchFetcher matchFetcher;
    private List<MoveEvent> gameTurns;

    public void initialize() {
        i = 0;
        gameState = GameState.getInstance(true);
        setupGrid();
        toggleCurrentPlayer();

        gameLogListView.getItems().clear();
    }

    private Button buttonAt(int i, int j){
        return (Button) gameState.getPieceImageView(i, j).getParent();
    }

    private void setupGrid() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                BoardPosition currentPosition = new BoardPosition(i, j);
                Piece piece = Piece.create(i, j);
                gameState.setPieceAt(piece, currentPosition);

                ImageView pieceImageView = gameState.getPieceImageView(i, j);
                if (pieceImageView == null)
                    pieceImageView = new ImageView();
                pieceImageView.setFitHeight(48);
                pieceImageView.setFitWidth(48);
                pieceImageView.setRotate(0);
                if (piece != null) {
                    pieceImageView.setImage(piece.getPieceImage());
                    gameState.getPlayerPieces(piece.getPlayer()).add(piece);
                }

                gameState.setPieceImageView(pieceImageView, currentPosition);

                Button tile = new Button();
                tile.setPrefHeight(48);
                tile.setPrefWidth(48);
                tile.setPadding(new Insets(16));
                tile.setGraphic(gameState.getPieceImageView(currentPosition));

                String tileBackground = (i + j) % 2 == 0 ? "light-tile" : "dark-tile";
                tile.getStyleClass().add(tileBackground);
                tile.getStyleClass().add(settings.getBoardStyle().toStyleString());
                boardGridView.add(tile, j, i, 1, 1);
            }
        }

        for (Piece whitePiece : gameState.getPlayerPieces(Player.WHITE))
            whitePiece.setPossibleMoves();

        for (Piece blackPiece : gameState.getPlayerPieces(Player.BLACK))
            blackPiece.setPossibleMoves();
    }

    private void toggleCurrentPlayer() {
        if (matchFetcher == null)
            return;

        gameState.toggleCurrentPlayer();

        Player currentPlayer = gameState.getCurrentPlayer();
        turnLabel.setText("Vez de " + (currentPlayer == Player.WHITE ? matchFetcher.getWhitePlayer() : matchFetcher.getBlackPlayer()));
        toggleLabelColor(currentPlayer);
    }

    private void toggleLabelColor(Player player) {
        turnLabel.setTextFill(player == Player.WHITE ? Color.WHITE : Color.BLACK);
    }

    private void deselectCheckWarning(){
        for (int i = 0; i < BOARD_SIZE; i++){
            for (int j = 0; j < BOARD_SIZE; j++){
                Button button = (Button) gameState.getPieceImageView(i, j).getParent();
                button.getStyleClass().removeAll("check");
            }
        }
    }

    private void highlightCheckWarning(Player player, boolean highlight){
        King king = BoardUtils.getPlayerKing(player);
        if (king == null)
            return;

        Button threatenedKingButton = (Button) gameState.getPieceImageView(king.getBoardPosition()).getParent();

        if (highlight && !threatenedKingButton.getStyleClass().contains("check"))
            threatenedKingButton.getStyleClass().add("check");
    }

    private BoardPosition getClickIndex(Node target) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++)
                if (gameState.getPieceImageView(i, j).equals(target))
                    return new BoardPosition(i, j);
        }

        return null;
    }

    private void completeTurn(MoveEvent moveEvent) {
        Piece movedPiece = gameState.getPieceAt(moveEvent.getDestination());

        updateAllPossibleMoves();

        if (simulationMoveEvent.getPromotedPiece() != null) {
            movedPiece = ((Pawn) movedPiece).transform(simulationMoveEvent.getPromotedPiece());
            gameState.getPieceImageView(movedPiece.getBoardPosition()).setImage(movedPiece.getPieceImage());
        }

        boolean whiteCheck = Validator.checkValidation(Player.WHITE);
        boolean blackCheck = Validator.checkValidation(Player.BLACK);

        gameState.setCheck(Player.WHITE, whiteCheck);
        gameState.setCheck(Player.BLACK, blackCheck);

        deselectCheckWarning();

        if (gameState.isCheck())
            gameState.setCheckmatte(Validator.checkmatteValidation(movedPiece));

        Player movedPlayer = gameState.getCurrentPlayer();

        moveEvent.setCheck(gameState.isCheck(movedPlayer));
        moveEvent.setCheckmatte(gameState.isCheckmatte());

        toggleCurrentPlayer();

        DrawType draw = Validator.draw();
        gameState.setDraw(draw != null);
        gameState.updateGameStatus();

        List<GameTurn> gameTurns = GameState.getGameTurnsLog();

        GameTurn gameTurn = new GameTurn(movedPiece, moveEvent);
        gameTurns.add(gameTurn);

        int logCount = gameLogListView.getItems().size();
        MatchLog matchLog;
        if (movedPlayer == Player.WHITE) {
            matchLog = new MatchLog(gameTurn);
            gameLogListView.getItems().add(matchLog);
        }else {
            matchLog = gameLogListView.getItems().get(logCount - 1);
            matchLog.setBlackTurn(gameTurn);
            gameLogListView.getItems().set(logCount - 1, matchLog);
        }

        Player winner;

        switch (gameState.getGameStatus()) {
            case CHECKMATTE -> {
                winner = whiteCheck ? Player.WHITE : Player.BLACK;
                toggleLabelColor(winner);
                turnLabel.setText("Chequemate");

                SceneManager.popUpGameOver(getStage(), winner, GameOverReason.CHECKMATTE, null);
            }
            case DRAW -> {
                turnLabel.setText("Empate");

                System.out.println("Empate: " + draw);

                SceneManager.popUpGameOver(getStage(), null, null, draw);
            }
            case CHECK -> {
                playSound(SoundAlert.CHECK);
                highlightCheckWarning(Player.BLACK, whiteCheck);
                highlightCheckWarning(Player.WHITE, blackCheck);
            }
            case RUNNING -> playSound(moveEvent.hasCaptured() ? SoundAlert.CAPTURE : SoundAlert.MOVE);
        }

        if (gameState.isGameOver()) {
            playSound(SoundAlert.GAME_OVER);
        }
    }

    private Stage getStage(){
        return (Stage) boardGridView.getScene().getWindow();
    }

    private void playSound(SoundAlert soundAlert) {
        if (!settings.isSoundOn())
            return;

        AudioClip audioClip = new AudioClip(ChessApplication.class.getResource(soundAlert.getUrl()).toString());
        audioClip.play();
    }

    private void select(Button clickedButton) {
        if (gameState.isGameOver())
            return;

        BoardPosition clickedPosition = getClickIndex(clickedButton.getGraphic());

        if (clickedPosition == null)
            return;

        if (sourcePosition == null) {
            Piece selectedPiece = gameState.getPieceAt(clickedPosition);
            if (selectedPiece == null || selectedPiece.getPlayer() != gameState.getCurrentPlayer())
                return;

            sourcePosition = clickedPosition;
            selectedPiece.setPossibleMoves();
        } else if (destinationPosition == null) {
            destinationPosition = clickedPosition;
            MoveEvent moveEvent = BoardUtils.moveOnBoard(sourcePosition, destinationPosition);
            if (moveEvent.isSuccess())
                completeTurn(moveEvent);

            sourcePosition = destinationPosition = null;
        }
    }

    public void onNextMove() {
        if (i > gameTurns.size() - 1) {
            nextMoveButton.setDisable(true);
            SceneManager.popUpSimulationGameOver(getStage(), matchFetcher.getWinner(), matchFetcher.getWhitePlayer(), matchFetcher.getBlackPlayer());
            String title = "Empate";
            if (matchFetcher.getWinner() != null) {
                title = matchFetcher.getWinner() == Player.WHITE ? matchFetcher.getWhitePlayer() : matchFetcher.getBlackPlayer();
                title += " venceu";
            }
            turnLabel.setText(title);
            return;
        }
        simulationMoveEvent = gameTurns.get(i++);
        Player currentPlayer = gameState.getCurrentPlayer();

        if (simulationMoveEvent.getCastlingType() != MoveEvent.CastlingType.NONE){
            int row = currentPlayer == Player.WHITE ? 7 : 0;
            int column = 4; //King Column
            select(buttonAt(row, column));
            column = simulationMoveEvent.getCastlingType() == MoveEvent.CastlingType.LONG ? 2 : 6;
            select(buttonAt(row, column));
            return;
        }

        BoardPosition moveSource = simulationMoveEvent.getSource();
        BoardPosition moveDestination = simulationMoveEvent.getDestination();
        BoardPosition sourcePosition = null;

        for (Piece piece : gameState.getPlayerPieces(currentPlayer)){
            boolean samePosition = false;
            if (moveSource.row == -1 && moveSource.column == -1) {
                samePosition = true;
            }else if (moveSource.row == -1){
                samePosition = piece.getBoardPosition().column == moveSource.column;
            }else if (moveSource.column == -1){
                samePosition = piece.getBoardPosition().row == moveSource.row;
            }

            if (piece.getPieceType() == simulationMoveEvent.getMovedPiece() && samePosition && piece.canMove(moveDestination)){
                sourcePosition = piece.getBoardPosition();
                break;
            }
        }

        if (sourcePosition != null) {
            select(buttonAt(sourcePosition.row, sourcePosition.column));
            select(buttonAt(moveDestination.row, moveDestination.column));
        }
    }

    public void onLoadGame() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Escolher local da partida");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Chess files", "*.match", "*.pgn"));
        File chosenFile = fileChooser.showOpenDialog(new Stage());

        if (chosenFile == null)
            return;

        matchFetcher = new MatchFetcher(chosenFile.getPath());
        gameTurns = matchFetcher.fetch();

        nextMoveButton.setDisable(gameTurns == null);

        if (gameTurns == null)
            return;

        initialize();
        getStage().setTitle(matchFetcher.getWhitePlayer() + " vs " + matchFetcher.getBlackPlayer() + " - " + ClockUtils.toFormattedDate(matchFetcher.getDate()));
    }

    public void onBackClick() {
        SceneManager.popUpConfirmation(getStage(), "Voltar para o menu?", new ConfirmationListener() {
            @Override
            public void onConfirmationAccepted() {
                SceneManager.goToPreGame(getStage());
            }

            @Override
            public void onConfirmationDenied() {

            }
        });
    }
}
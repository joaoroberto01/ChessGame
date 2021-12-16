package com.jrgc.chessgame.controllers;

import com.jrgc.chessgame.ChessApplication;
import com.jrgc.chessgame.GameState;
import com.jrgc.chessgame.Settings;
import com.jrgc.chessgame.interfaces.ConfirmationListener;
import com.jrgc.chessgame.interfaces.CountdownTimerListener;
import com.jrgc.chessgame.interfaces.PawnPromotionListener;
import com.jrgc.chessgame.models.SoundAlert;
import com.jrgc.chessgame.models.game.*;
import com.jrgc.chessgame.models.game.log.GameTurn;
import com.jrgc.chessgame.models.game.log.MatchLog;
import com.jrgc.chessgame.models.pieces.King;
import com.jrgc.chessgame.models.pieces.Pawn;
import com.jrgc.chessgame.models.pieces.Piece;
import com.jrgc.chessgame.utils.*;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;

import static com.jrgc.chessgame.utils.BoardUtils.BOARD_SIZE;
import static com.jrgc.chessgame.utils.BoardUtils.updateAllPossibleMoves;

public class GameController implements EventHandler<MouseEvent> {

    @FXML
    private Button drawButton, surrenderButton;

    @FXML
    private ListView<MatchLog> gameLogListView;

    @FXML
    private Label turnLabel;

    @FXML
    private Text whiteTimerLabel, blackTimerLabel;

    @FXML
    private GridPane boardGridView;

    private final Settings settings = Settings.getInstance();

    private Piece.PieceType promotedPieceType;
    private BoardPosition sourcePosition = null, destinationPosition = null;
    private Button lastClickedButton;
    private GameState gameState;

    private CountdownTimer timer;

    public void initialize() {
        gameState = GameState.getInstance(true);
        setupGrid();
        toggleCurrentPlayer();

        gameLogListView.getItems().clear();
        drawButton.setDisable(false);
        surrenderButton.setDisable(false);

        setupTimer();
    }

    private void setupTimer() {
        if (!settings.isTimeOn())
            return;

        Font display = Font.loadFont(ChessApplication.class.getResourceAsStream("fonts/display-font.ttf"), 20);

        whiteTimerLabel.setFont(display);
        whiteTimerLabel.setText(ClockUtils.clockFormat(CountdownTimer.DEFAULT_SECONDS));

        blackTimerLabel.setFont(display);
        blackTimerLabel.setText(ClockUtils.clockFormat(CountdownTimer.DEFAULT_SECONDS));

        if (timer != null)
            timer.pause();

        timer = new CountdownTimer(new CountdownTimerListener() {
            @Override
            public void onCountdownTick(long timeLeft, Player currentPlayer) {
                Text timerLabel = currentPlayer == Player.WHITE ? whiteTimerLabel : blackTimerLabel;
                Platform.runLater(() -> timerLabel.setText(ClockUtils.clockFormat(timeLeft)));
            }

            @Override
            public void onCountdownFinished(Player loser) {
                if (gameState.isGameOver())
                    return;
                Platform.runLater(() -> {
                    Player opponent = Player.getOpponent(loser);
                    if (Validator.insufficientMaterial(opponent)) {
                        gameState.setDraw(true);
                        SceneManager.popUpGameOver(getStage(), null, GameOverReason.TIME_OVER, DrawType.INSUFFICIENT_MATERIAL);
                        finishGame(null);
                        turnLabel.setText("Empate");

                        return;
                    }

                    toggleLabelColor(opponent);
                    turnLabel.setText(settings.getName(opponent) + " venceu por tempo esgotado");
                    SceneManager.popUpGameOver(getStage(), opponent, GameOverReason.TIME_OVER, null);
                    finishGame(opponent);
                });
            }
        });
    }

    @FXML
    private void checkCase() {
        select(source(6,5));
        select(source(4,5));

        select(source(1,1));
        select(source(2,1));

        select(source(6,6));
        select(source(4,6));

        select(source(1,4));
        select(source(2,4));

        select(source(6,4));
        select(source(4,4));

        select(source(6,0));
        select(source(5,0));

        select(source(0,3));
        select(source(4,7));
        //clicked(source(7,4));
    }

    @FXML
    private void checkmatteCase() {
        select(source(6,4));
        select(source(4,4));

        select(source(1,4));
        select(source(2,4));

        select(source(7,5));
        select(source(2,0));

        select(source(1,1));
        select(source(2,1));

        select(source(6,5));
        select(source(4,5));

        select(source(0,2));
        select(source(2,0));

        select(source(6,6));
        select(source(4,6));

        select(source(0,3));
        select(source(4,7));

//        clicked(source(6,0));
//        clicked(source(5,0));
//
//        clicked(source(0,3));
//        clicked(source(4,7));
        //clicked(source(7,4));
    }

    private Button source(int i, int j){
        return (Button) gameState.getPieceImageView(i, j).getParent();
    }

    public void onResetClick() {
        SceneManager.popUpConfirmation(getStage(), "Reiniciar o jogo?", new ConfirmationListener() {
            @Override
            public void onConfirmationAccepted() {
                initialize();
            }

            @Override
            public void onConfirmationDenied() {

            }
        });
    }

    public void onDrawSuggestClick() {
        toggleCurrentPlayer();
        timer.pause();
        SceneManager.popUpConfirmation(getStage(), settings.getName(gameState.getCurrentPlayer()) + ", aceita o empate?", new ConfirmationListener() {
            @Override
            public void onConfirmationAccepted() {
                gameState.setDraw(true);
                SceneManager.popUpGameOver(getStage(), null, GameOverReason.DRAW_DEAL, null);
                finishGame(null);
                turnLabel.setText("Empate");
            }

            @Override
            public void onConfirmationDenied() {
                toggleCurrentPlayer();
                timer.start();
            }
        });
    }

    public void onSurrenderClick() {
        SceneManager.popUpConfirmation(getStage(), "Render-se?", new ConfirmationListener() {
            @Override
            public void onConfirmationAccepted() {
                Player currentPlayer = gameState.getCurrentPlayer();

                for (Piece piece : gameState.getPlayerPieces(currentPlayer))
                    if (piece instanceof King)
                        gameState.getPieceImageView(piece.getBoardPosition()).setRotate(90);

                Player opponent = Player.getOpponent(gameState.getCurrentPlayer());

                toggleLabelColor(opponent);
                turnLabel.setText(settings.getName(opponent) + " venceu por desistência");
                SceneManager.popUpGameOver(getStage(), opponent, GameOverReason.SURRENDER, null);
                finishGame(opponent);
            }

            @Override
            public void onConfirmationDenied() {

            }
        });
    }

    public void onExitClick() {
        SceneManager.popUpConfirmation(getStage(), "Voltar para seleção de nomes?", new ConfirmationListener() {
            @Override
            public void onConfirmationAccepted() {
                SceneManager.goToPlayers(getStage());
            }

            @Override
            public void onConfirmationDenied() {

            }
        });
    }

    private void finishGame(Player winner) {
        if (timer != null)
            timer.pause();

        gameState.setGameOver(true);
        drawButton.setDisable(true);
        surrenderButton.setDisable(true);

        MatchLog matchLog = new MatchLog(winner);
        matchLog.writeToFile();
        gameLogListView.getItems().add(matchLog);

        playSound(SoundAlert.GAME_OVER);
    }

    private double getBoardCenterX(Stage stage) {
        return stage.getX() + boardGridView.getWidth() / 3;
    }

    private double getBoardCenterY(Stage stage) {
        return stage.getY() + boardGridView.getHeight() / 2 - 20;
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

                tile.setOnMouseEntered(event -> {
                    tile.setCursor(piece == null || piece.getPlayer() != gameState.getCurrentPlayer() ? Cursor.DEFAULT : Cursor.HAND);
                });
                tile.setOnMouseClicked(this);

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
        gameState.toggleCurrentPlayer();

        Player currentPlayer = gameState.getCurrentPlayer();
        turnLabel.setText("Vez de " + settings.getName(currentPlayer));
        toggleLabelColor(currentPlayer);
    }

    private void toggleLabelColor(Player player) {
        turnLabel.setTextFill(player == Player.WHITE ? Color.WHITE : Color.BLACK);
    }

    private void selectTile(Button clickedButton){
        if (lastClickedButton != null)
            lastClickedButton.getStyleClass().remove("selected");

        lastClickedButton = clickedButton;

        if (clickedButton != null)
            clickedButton.getStyleClass().add("selected");
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

        if (moveEvent.isPawnPromoted()) {
            openPawnPromotionDialog(movedPiece);
            moveEvent.setPromotedPiece(promotedPieceType);
        }

        updateAllPossibleMoves();

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
            if (gameState.isGameOver())
                matchLog.writeToFile();
        }else {
            matchLog = gameLogListView.getItems().get(logCount - 1);
            matchLog.setBlackTurn(gameTurn);
            matchLog.writeFileHeader();
            matchLog.writeToFile();
            gameLogListView.getItems().set(logCount - 1, matchLog);
        }

        Player winner = null;

        switch (gameState.getGameStatus()) {
            case CHECKMATTE -> {
                winner = whiteCheck ? Player.WHITE : Player.BLACK;
                toggleLabelColor(winner);
                turnLabel.setText("Chequemate");

                SceneManager.popUpGameOver(getStage(), winner, GameOverReason.CHECKMATTE, null);
            }
            case DRAW -> {
                turnLabel.setText("Empate");

                SceneManager.popUpGameOver(getStage(), null, null, draw);
            }
            case CHECK -> {
                playSound(SoundAlert.CHECK);
                highlightCheckWarning(Player.BLACK, whiteCheck);
                highlightCheckWarning(Player.WHITE, blackCheck);
            }
            case RUNNING -> playSound(moveEvent.hasCaptured() ? SoundAlert.CAPTURE : SoundAlert.MOVE);
        }

        if (gameState.isGameOver())
            finishGame(winner);
    }

    private Stage getStage(){
        return (Stage) boardGridView.getScene().getWindow();
    }

    private void openPawnPromotionDialog(Piece movedPiece) {

        Stage stage = getStage();

        double x = getBoardCenterX(stage);
        double y = getBoardCenterY(stage);

        SceneManager.popUpPawnPromotion(stage, x, y, movedPiece.getPlayer(), new PawnPromotionListener() {
            @Override
            public void onPromotionPieceSelected(Piece.PieceType pieceType) {
                if (!(movedPiece instanceof Pawn))
                    return;

                promotedPieceType = pieceType;

                Pawn pawn = (Pawn) movedPiece;
                Piece newPiece = pawn.transform(pieceType);

                gameState.setPieceAt(newPiece, movedPiece.getBoardPosition());
                gameState.getPlayerPieces(pawn.getPlayer()).replaceAll(piece -> piece.equals(pawn) ? newPiece : piece);
                gameState.getPieceImageView(pawn.getBoardPosition()).setImage(newPiece.getPieceImage());
                updateAllPossibleMoves();

                playSound(SoundAlert.PAWN_PROMOTION);
            }
        });
    }

    private void playSound(SoundAlert soundAlert) {
        if (!settings.isSoundOn())
            return;

        AudioClip audioClip = new AudioClip(ChessApplication.class.getResource(soundAlert.getUrl()).toString());
        audioClip.play();
    }

    //On Tile Click
    @Override
    public void handle(MouseEvent event) {
        select((Button) event.getSource());
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
            selectTile(clickedButton);
            selectedPiece.setPossibleMoves();
            if (settings.shouldShowPath())
                BoardUtils.showPossiblePaths(selectedPiece);
            if (settings.isSoundOn())
                timer.start();
        } else if (destinationPosition == null) {
            destinationPosition = clickedPosition;
            MoveEvent moveEvent = BoardUtils.moveOnBoard(sourcePosition, destinationPosition);
            if (moveEvent.isSuccess()) {
                if (settings.isSoundOn())
                    timer.toggle();
                completeTurn(moveEvent);
            }else
                playSound(SoundAlert.INVALID);

            if (settings.shouldShowPath())
                BoardUtils.clearPaths();
            selectTile(null);
            sourcePosition = destinationPosition = null;
        }
    }
}
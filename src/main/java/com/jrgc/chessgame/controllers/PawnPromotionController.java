package com.jrgc.chessgame.controllers;

import com.jrgc.chessgame.interfaces.PawnPromotionListener;
import com.jrgc.chessgame.models.game.Player;
import com.jrgc.chessgame.models.pieces.Piece;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

public class PawnPromotionController {

    @FXML
    private Button btnBishop, btnKnight, btnQueen, btnRook;

    private PawnPromotionListener pawnPromotionListener;

    public void setup(Player player, PawnPromotionListener pawnPromotionListener){
        this.pawnPromotionListener = pawnPromotionListener;

        btnBishop.setGraphic(createImageView(Piece.PieceType.BISHOP, player));
        btnBishop.setPadding(new Insets(16));

        btnKnight.setGraphic(createImageView(Piece.PieceType.KNIGHT, player));
        btnKnight.setPadding(new Insets(16));

        btnQueen.setGraphic(createImageView(Piece.PieceType.QUEEN, player));
        btnQueen.setPadding(new Insets(16));

        btnRook.setGraphic(createImageView(Piece.PieceType.ROOK, player));
        btnRook.setPadding(new Insets(16));
    }

    public void onPromotionPieceClick(ActionEvent actionEvent){
        Button button = (Button) actionEvent.getSource();
        Piece.PieceType pieceType = null;
        if (button.equals(btnBishop))
            pieceType = Piece.PieceType.BISHOP;
        else if (button.equals(btnKnight))
            pieceType = Piece.PieceType.KNIGHT;
        else if (button.equals(btnQueen))
            pieceType = Piece.PieceType.QUEEN;
        else if (button.equals(btnRook))
            pieceType = Piece.PieceType.ROOK;

        pawnPromotionListener.onPromotionPieceSelected(pieceType);

        button.getScene().getWindow().hide();
    }

    private ImageView createImageView(Piece.PieceType pieceType, Player player){
        ImageView imageView = new ImageView();
        imageView.setFitWidth(32);
        imageView.setFitHeight(32);
        imageView.setImage(Piece.getPieceImage(pieceType, player));
        return imageView;
    }
}

package com.jrgc.chessgame.controllers;

import com.jrgc.chessgame.interfaces.ConfirmationListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class ConfirmationController {
    @FXML
    private Text confirmationTitle;

    private ConfirmationListener confirmationListener;

    public void setup(String title, ConfirmationListener confirmationListener){
        confirmationTitle.setText(title);
        this.confirmationListener = confirmationListener;
    }

    public void onAcceptClick() {
        confirmationListener.onConfirmationAccepted();
        close();
    }

    public void onDenyClick() {
        confirmationListener.onConfirmationDenied();
        close();
    }

    private void close(){
        confirmationTitle.getScene().getWindow().hide();
    }
}

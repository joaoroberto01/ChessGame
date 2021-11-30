package com.jrgc.chessgame.interfaces;

import com.jrgc.chessgame.models.game.Player;

public interface CountdownTimerListener {
    void onCountdownTick(long timeLeft, Player currentPlayer);
    void onCountdownFinished(Player loser);
}

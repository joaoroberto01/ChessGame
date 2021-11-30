package com.jrgc.chessgame.utils;

import com.jrgc.chessgame.GameState;
import com.jrgc.chessgame.Settings;
import com.jrgc.chessgame.interfaces.CountdownTimerListener;
import com.jrgc.chessgame.models.game.Player;

import java.util.Timer;
import java.util.TimerTask;

public class CountdownTimer {
    public static final long DEFAULT_SECONDS = 600;
    private static final long PERIOD = 1000;

    private final CountdownTimerListener countdownTimerListener;
    private final long interval;

    private Timer timer = new Timer();
    private Player currentPlayer;
    private final long[] seconds = new long[2];
    private boolean isTicking;

    private final Settings settings = Settings.getInstance();

    public CountdownTimer(CountdownTimerListener countdownTickListener) {
        interval = DEFAULT_SECONDS;
        this.countdownTimerListener = countdownTickListener;
        reset();
    }

    public void toggle(){
        if (!settings.isTimeOn())
            return;

        pause();
        currentPlayer = Player.getOpponent(currentPlayer);
        start();
    }

    public void reset(){
        isTicking = false;
        seconds[0] = seconds[1] = interval;
        currentPlayer = Player.WHITE;
    }

    public void start(){
        if (isTicking || !settings.isTimeOn())
            return;

        isTicking = true;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                GameState gameState = GameState.getInstance();
                if (gameState == null)
                    return;
                countdownTimerListener.onCountdownTick(--seconds[currentPlayer.getIndex()], currentPlayer);
                if (seconds[currentPlayer.getIndex()] == 0 || gameState.isGameOver()){
                    countdownTimerListener.onCountdownFinished(currentPlayer);
                    pause();
                }

            }
        }, 0, PERIOD);
    }

    public void pause(){
        isTicking = false;
        timer.cancel();
    }
}

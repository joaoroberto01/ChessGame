package com.jrgc.chessgame.models.game.log;

import com.jrgc.chessgame.Settings;
import com.jrgc.chessgame.models.game.Player;
import com.jrgc.chessgame.utils.ClockUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MatchLog {
    private static final String MATCHES_PATH = "matches/";
    private static int count = 1;
    private static String path = MATCHES_PATH + "match" + ClockUtils.getTimestamp() + ".mat";
    private static boolean headerWritten = false;

    private final GameTurn whiteTurn;
    private final int roundNumber;
    private GameTurn blackTurn;
    private Player winner;

    public MatchLog(GameTurn whiteTurn, GameTurn blackTurn){
        this.whiteTurn = whiteTurn;
        this.blackTurn = blackTurn;
        roundNumber = count++;

        File file = new File(MATCHES_PATH);
        if (!file.exists())
            file.mkdir();
    }

    public MatchLog(GameTurn whiteTurn){
        this(whiteTurn, null);
    }

    public MatchLog(Player winner){
        this(null, null);
        this.winner = winner;
    }

    public void setBlackTurn(GameTurn blackTurn) {
        this.blackTurn = blackTurn;
    }

    public void writeFileHeader(){
        if (headerWritten)
            return;
        try {
            String headerFormat = """
                    [Date "%s"]
                    [White "%s"]
                    [Black "%s"]
                    
                    """;

            Settings settings = Settings.getInstance();

            String header = String.format(headerFormat, ClockUtils.getDate(), settings.getName(Player.WHITE), settings.getName(Player.BLACK));

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path, true));
            bufferedWriter.append(header);
            bufferedWriter.close();
            headerWritten = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToFile(){
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path, true));
            bufferedWriter.append(toString());
            bufferedWriter.append("\n");
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void resetLog(){
        count = 1;
        headerWritten = false;
        path = MATCHES_PATH + ClockUtils.getTimestamp() + ".match";
    }

    @Override
    public String toString() {
        if (winner == Player.WHITE)
            return "1-0";
        if (winner == Player.BLACK)
            return "0-1";
        if (winner == null && whiteTurn == null && blackTurn == null)
            return "½-½";

        if (whiteTurn == null)
            return "";

        String blackTurnString = "";
        if (blackTurn != null)
            blackTurnString = blackTurn.toString();

        return String.format("%d. %s %s", roundNumber, whiteTurn, blackTurnString);
    }
}
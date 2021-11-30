package com.jrgc.chessgame;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jrgc.chessgame.models.game.Player;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Settings {
    private static final String SETTINGS_FILE = "game_settings.chess";
    private static Settings settings;

    public enum PieceStyle {
        CLASSIC, PIXEL, PROGRAMMING, POKEMON;

        public String getPath(){
            return super.toString().toLowerCase().concat("/");
        }

        @Override
        public String toString() {
            return switch (this){
                case CLASSIC -> "Clássico";
                case PIXEL -> "Pixel";
                case PROGRAMMING -> "Programação";
                case POKEMON -> "Pokémon";
            };
        }
    }

    public enum BoardStyle {
        BROWN, GRAY;

        public String toStyleString(){
            return super.toString().toLowerCase();
        }

        @Override
        public String toString() {
            return switch (this){
                case BROWN -> "Marrom";
                case GRAY -> "Cinza";
            };
        }
    }

    private boolean soundOn = false, timeOn = true, showPath = true;
    private PieceStyle pieceStyle = PieceStyle.PIXEL;
    private BoardStyle boardStyle = BoardStyle.BROWN;
    private final String[] names = new String[2];

    private Settings (){}

    public static Settings getInstance(){
        if (settings == null) {
            settings = new Settings();
            importSettings();
        }

        return settings;
    }

    public boolean isSoundOn() {
        return soundOn;
    }

    public void setSoundOn(boolean soundOn) {
        this.soundOn = soundOn;
    }

    public boolean isTimeOn() {
        return timeOn;
    }

    public void setTimeOn(boolean timeOn) {
        this.timeOn = timeOn;
    }

    public boolean shouldShowPath() {
        return showPath;
    }

    public void setShowPath(boolean showPath) {
        this.showPath = showPath;
    }

    public PieceStyle getPieceStyle() {
        return pieceStyle;
    }

    public void setPieceStyle(PieceStyle pieceStyle) {
        this.pieceStyle = pieceStyle;
    }

    public BoardStyle getBoardStyle() {
        return boardStyle;
    }

    public void setBoardStyle(BoardStyle boardStyle) {
        this.boardStyle = boardStyle;
    }

    public void setName(Player player, String name){
        names[player.getIndex()] = name;
    }

    public String getName(Player player){
        return names[player == Player.WHITE ? 0 : 1];
    }

    public void exportSettings(){
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(SETTINGS_FILE));

            Gson gson = new Gson();
            byte[] bytes = gson.toJson(this).getBytes(StandardCharsets.UTF_8);

            bufferedWriter.write(Base64.getEncoder().encodeToString(bytes));
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void importSettings(){
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(SETTINGS_FILE));
            String settingsString = new String(Base64.getDecoder().decode(bufferedReader.readLine()), StandardCharsets.UTF_8);
            bufferedReader.close();

            if (settingsString.isEmpty())
                return;

            JsonObject jsonSettings = (JsonObject) JsonParser.parseString(settingsString);

            settings.setSoundOn(jsonSettings.get("soundOn").getAsBoolean());
            settings.setTimeOn(jsonSettings.get("timeOn").getAsBoolean());

            PieceStyle pieceStyle;
            try {
                pieceStyle = PieceStyle.valueOf(jsonSettings.get("pieceStyle").getAsString());
            }catch (IllegalArgumentException e){
                pieceStyle = PieceStyle.PIXEL;
            }

            settings.setPieceStyle(pieceStyle);

            BoardStyle boardStyle;
            try {
                boardStyle = BoardStyle.valueOf(jsonSettings.get("boardStyle").getAsString());
            }catch (IllegalArgumentException e){
                boardStyle = BoardStyle.BROWN;
            }
            settings.setBoardStyle(boardStyle);

            JsonArray names = jsonSettings.getAsJsonArray("names");
            if (!names.get(0).isJsonNull())
                settings.setName(Player.WHITE, names.get(0).getAsString());

            if (!names.get(1).isJsonNull())
                settings.setName(Player.BLACK, names.get(1).getAsString());
        } catch (IOException|NullPointerException ignored) {}
    }
}

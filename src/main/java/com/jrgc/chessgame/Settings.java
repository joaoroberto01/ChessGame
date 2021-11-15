package com.jrgc.chessgame;

import com.jrgc.chessgame.models.game.Player;

public class Settings {
    private static Settings settings;

    public enum PieceStyle {
        CLASSIC, PIXEL;

        public String getPath(){
            return switch (this){
                case CLASSIC -> "classic/";
                case PIXEL -> "pixel/";
            };
        }
    }

    public enum BoardStyle {
        BROWN, GRAY;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    private boolean soundOn = false;
    private PieceStyle pieceStyle = PieceStyle.PIXEL;
    private BoardStyle boardStyle = BoardStyle.BROWN;
    private final String[] names = new String[]{"Tu é?", "Tu que deixa"};

    private Settings (){}

    public static Settings getInstance(){
        if (settings == null)
            settings = new Settings();
        return settings;
    }

    public boolean isSoundOn() {
        return soundOn;
    }

    public void setSoundOn(boolean soundOn) {
        this.soundOn = soundOn;
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
        int index = player == Player.WHITE ? 0 : 1;
        names[index] = name;
    }

    public String getName(Player player){
        return names[player == Player.WHITE ? 0 : 1];
    }
}

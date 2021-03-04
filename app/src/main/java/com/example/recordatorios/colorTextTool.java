package com.example.recordatorios;


import android.graphics.Color;

public class colorTextTool {

    int ColorT;

    public colorTextTool() {
    }

    public int colorTextTool(int colorBackground) {
        this.colorText(colorBackground);
        return ColorT;
    }

    public void colorText(int colorBackgroud) {
        switch (colorBackgroud) {
            case (0xFF9C27B0):// PURPLE 500
            case (0xFF673AB7): // DEEP PURPLE 500
            case (0xFF3F51B5): // INDIGO 500
            case (0xFF795548): // BROWN 500
                this.ColorT = Color.WHITE;
                break;
            default:
                this.ColorT = Color.BLACK;
                break;
        }
    }


}

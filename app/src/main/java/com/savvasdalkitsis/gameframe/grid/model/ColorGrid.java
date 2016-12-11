package com.savvasdalkitsis.gameframe.grid.model;

import android.support.annotation.ColorInt;

public class ColorGrid {

    private final int[][] colors = new int[16][16];

    public void setColor(@ColorInt int color, int column, int row) {
        checkValue(column, "Column");
        checkValue(row, "Row");
        colors[column - 1][row -1] = color;
    }

    public void fill(int color) {
        for (int i = 1; i <= 16; i++) {
            for (int j = 1; j <= 16; j++) {
                setColor(color, i, j);
            }
        }
    }

    @ColorInt
    public int getColor(int column, int row) {
        checkValue(column, "Column");
        checkValue(row, "Row");
        return colors[column - 1][row - 1];
    }

    private void checkValue(int value, final String valueName) {
        if (value < 1 || value > 16) {
            throw new IllegalArgumentException(valueName + " value should be between 1 and 16");
        }
    }
}
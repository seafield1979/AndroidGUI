package com.sunsunsoft.shutaro.ugui;

import android.graphics.Color;

import java.util.Random;

/**
 *　カスタマイズしたColorクラス
 */

public class MyColor extends Color {

    /**
     * ランダムな色を取得
     * @return
     */
    public static int getRandomColor() {
        Random rand = new Random();
        return (0xff << 24) | (rand.nextInt(255) << 16) | (rand.nextInt(255) << 8) | (rand.nextInt(255));

    }
}

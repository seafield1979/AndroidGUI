package com.sunsunsoft.shutaro.ugui;

/**
 * Size of Integer(width,height)
 */

public class Size {
    public int width, height;

    public Size() {}
    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }
    public Size(Size _size) {
        this.width = _size.width;
        this.height = _size.height;
    }
}

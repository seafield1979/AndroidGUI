package com.sunsunsoft.shutaro.ugui.util;

/**
 * Created by shutaro on 2017/06/15.
 * 要素がlong型のSize
 */
public class SizeL {
    public long width, height;

    public SizeL() {}
    public SizeL(long width, long height) {
        this.width = width;
        this.height = height;
    }
    public SizeL(Size _size) {
        this.width = _size.width;
        this.height = _size.height;
    }
}
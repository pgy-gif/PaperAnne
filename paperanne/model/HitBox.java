package com.paperanne.model;

public class HitBox {
    private int width;
    private int height;
    private double x;
    private double y;

    public HitBox(int width, int height, double x, double y) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
    }

    public double getLX() {
        return x;
    }

    public double getUY() {
        return y;
    }

    public double getDY() {
        return y + height;
    }

    public double getRX() {
        return x + width;
    }

    //检测边缘碰撞
    public boolean checkHit(HitBox hb) {
        return (hb.getRX() >= this.getLX() &&
                hb.getLX() <= this.getRX() &&
                hb.getUY() <= this.getDY() &&
                hb.getDY() >= this.getUY());
    }
}

package com.paperanne.model;

public class PlayerModel {
    public double x = 100, y = 300;
    public double velY = 0;
    public final double speed = 4;
    public final double jumpForce = -8;    //跳跃高度
    public final double gravity = 0.6;
    public boolean isJumping = false;
    public int sizeState = 0; // 0: 正常, 1: 大, -1: 小

    public double width = 36;
    public double height = 50;
    public boolean hasKey = false;

    // --- 生命值系统 ---
    public final int MAX_HEALTH = 5;
    public int health = MAX_HEALTH;
    public boolean isDead = false;

    // 受伤无敌时间（毫秒）
    public long lastDamageTime = 0;
    public final long INVINCIBLE_DURATION = 1500; // 受伤后1秒无敌

    public HitBox getHitBox() {
        // 假设 PlayerModel 存储了当前的 x, y 和 宽高
        return new HitBox((int) this.width, (int) this.height, this.x, this.y);
    }
}

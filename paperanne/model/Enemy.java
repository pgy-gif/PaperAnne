package com.paperanne.model;

import com.paperanne.model.HitBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Enemy {
    private double x, y;
    private double width = 50;
    private double height = 40;
    private double speed = 1.0;
    private double sightRange = 300.0; // 视线范围
    private boolean isScared = false; // 是否处于惊吓状态
    private double minX; // 左边界
    private double maxX; // 右边界
    private PaperModel platform; // 如果绑定了纸片，引用它；如果是地面，则为 null
    private boolean isSamePlane;//12/28 1609 isSamePlane更改为私有属性
    private ImageView img;
    private int flag = 1;//巡逻方向
    private int f = 0;
    private double buf;

    public Enemy(double startX, double startY, double minX, double maxX, PaperModel platform) {
        this.x = startX;
        this.y = startY;
        this.minX = minX;
        this.maxX = maxX;
        this.platform = platform;

        try {
            // 请确保路径正确
            Image image = new Image(getClass().getResourceAsStream("/com/paperanne/paperanne/images/soldier.png"));
            this.img = new ImageView(image);
            this.img.setFitWidth(width);
            this.img.setFitHeight(height);
        } catch (Exception e) {
            System.err.println("敌人图片加载失败");
        }
    }

    /**
     * 敌人AI逻辑更新
     *
     * @param player 玩家模型，用于判断距离和状态
     */
    public void update(PlayerModel player) {
        // --- 1. 如果绑定了纸片，实时更新边界和高度 ---
        if (platform != null) {
            // 敌人的 Y 始终贴在纸片表面 (纸片Y - 敌人高度)
            this.y = platform.getY() - this.height;
            // 敌人的活动范围就是纸片的左右边缘
            this.minX = platform.getX();
            this.maxX = platform.getX() + platform.getWidth();

            // 如果因为拖拽纸片导致敌人悬空（超出边界），强制拉回边界内
            if (this.x < minX) this.x = minX;
            if (this.x > maxX - width) this.x = maxX - width;
        }

        // --- 2. 判定是否处于同一水平面 ---
        // 容差设为 60 像素（大约一个身位的高度）
        if  (player.sizeState == 1){
            isSamePlane = Math.abs(player.y - this.y) < 60;
        }
        else{
            isSamePlane = Math.abs(player.y - this.y) < 30;//60范围过大
        }


        double dist = Math.abs(player.x - this.x);

        // --- 3. 吓退逻辑 (变大且在同层且在视线内) ---
        if (player.sizeState == 1){
            isScared = true;
        }
        else{
            isScared = false;
        }

        if (isScared && dist < sightRange && isSamePlane) {
            // 尝试向反方向移动
            if (player.x > this.x) {
                moveLeft(1.0); // 向左跑
            } else {
                moveRight(1.0); // 向右跑
            }
        }
        // --- 4. 追击逻辑 (同层 && 视线内 && 没死 && 没被吓跑) ---
        else if (isSamePlane && dist < sightRange && !player.isDead && !isScared) {
            if (player.x > this.x) {
                moveRight(1.0); // 向右追
            } else {
                moveLeft(1.0); // 向左追
            }
        }
        // --- 5. 否则巡逻 ---
        else{
            isScared = false;
            moveAround();

        }



    }
    //防卡死
    private boolean antiChoke() {
        f++;
        if(f == 1){
            buf = x;
        }
        if(f == 2){
            if(buf - x == 1.0 || x - buf == 1.0){
                f = 0;
                return false;
            }
            f = 0;
        }

        return true;
    }
    //12/28 1609 新增getter用于检测碰撞是否在同一平面
    public boolean isSamePlane(){
        return isSamePlane;
    }

    private void moveLeft(double speedMult) {
        double nextX = this.x - (speed * speedMult);
        if (nextX >= minX) { // 只有没撞墙才移动
            this.x = nextX;
            if(antiChoke()){
                img.setScaleX(-1);
            }
             // 面向左
        }
    }

    private void moveAround(){
        if(x >= maxX - width){
            flag = -1;
        }else if(x <= minX){
            flag = 1;
        }
        if(flag == 1){
            moveRight(1.0);
        }
        else if(flag == -1){
            moveLeft(1.0);
        }

    }

    private void moveRight(double speedMult) {
        double nextX = this.x + (speed * speedMult);
        if (nextX <= maxX - width) { // 只有没撞墙才移动
            this.x = nextX;
            if(antiChoke()){
                img.setScaleX(1);
            }
             // 面向右
        }
    }

    public ImageView painter() {
        if (img != null) {
            img.setX(x);
            img.setY(y);
        }
        return img;
    }

    public HitBox getHitBox() {
        return new HitBox((int) width, (int) height, x, y);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}


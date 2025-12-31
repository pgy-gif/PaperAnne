package com.paperanne.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class BugEnemy extends Enemy {
    // 全局静态警报状态
    public static boolean isAlerted = false;
    private double detectRange = 70.0;
    //alert提示图片
    private ImageView alertIcon;

    public BugEnemy(double startX, double startY, double minX, double maxX, PaperModel platform) {
        super(startX, startY, minX, maxX, platform);

        var stream = getClass().getResourceAsStream("/com/paperanne/images/worm_normal_move_a.png");
        if (stream != null) {
            this.getImg().setImage(new Image(stream));
        } else {
            System.err.println("错误：找不到虫子图片！路径尝试为: /com/paperanne/images/worm_normal_move_a.png");
        }

        this.alertIcon = new ImageView();
        var alertStream = getClass().getResourceAsStream("/com/paperanne/images/alert.png");
        if (alertStream != null) {
            this.alertIcon.setImage(new Image(alertStream));
        }

        // 设置感叹号大小并默认隐藏
        this.alertIcon.setFitWidth(20);
        this.alertIcon.setFitHeight(20);
        this.alertIcon.setVisible(false);
    }

    @Override
    public void update(PlayerModel player) {
        // 1. 调用父类巡逻逻辑
        super.moveAround();

        // 2. 发现安妮逻辑：使用 player 对象的坐标
        double distance = Math.sqrt(Math.pow(x - player.x, 2) + Math.pow(y - player.y, 2));
        if (distance < detectRange) {
            isAlerted = true;
            this.alertIcon.setVisible(true);
        } else {
            this.alertIcon.setVisible(false);
        }
    }

    @Override
    public ImageView painter() {
        ImageView bugImg = super.painter(); // 先更新并获取虫子图片

        if (alertIcon != null&&isAlerted) {
            System.out.println("感叹号坐标: " + alertIcon.getX() + "," + alertIcon.getY());
            // 将感叹号置于虫子头顶正上方
            alertIcon.setX(this.x + (50 / 2) - 10); // 虫子宽度的一半减去感叹号宽度的一半
            alertIcon.setY(this.y - 15); // 在虫子 Y 轴上方 25 像素
        }

        return bugImg;
    }

    public ImageView getAlertIcon() {
        return alertIcon;
    }

    // 让虫子永远不触发 checkEnemyCollision 里的伤害
    @Override
    public boolean isSamePlane() {
        return false; // 虫子只是侦察兵，永远返回 false 即可免疫伤害检测
    }
}
package com.paperanne.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import java.util.List;

public class Duck {
    private ImageView view;
    private double speed = 1.0;
    private double velY = 0;
    private final double GRAVITY = 0.5;
    private boolean isOnSurface = false;

    public Duck(double x, double y) {
        Image img = new Image(getClass().getResourceAsStream("/com/paperanne/images/duck.png"));
        view = new ImageView(img);
        view.setX(x); view.setY(y);
        view.setFitWidth(30); view.setFitHeight(40);
    }

    public void update(double playerX, double playerY, List<ImageView> papers, double groundY) {
        for (ImageView paper : papers) {
            // 假设你在 Level4View 中给电梯设置了 ID: elevator.setId("ELEVATOR");
            if ("ELEVATOR".equals(paper.getId())) {
                // 获取电梯在场景中的实时位置（包含动画造成的位移）
                double elevatorRealTop = paper.getBoundsInParent().getMinY();

                // 检查鸭子脚部是否在电梯顶部的范围内
                if (view.getBoundsInParent().intersects(paper.getBoundsInParent())) {
                    // 简单暴力法：如果碰撞了，且鸭子在电梯上方，则同步位置
                    if (view.getY() + view.getFitHeight() <= elevatorRealTop + 10) {
                        view.setY(elevatorRealTop - view.getFitHeight());
                    }
                }
            }
        }

        double dx = playerX - view.getX();
        double nextX = view.getX();

        // 1. 水平跟随逻辑 (仅当在一定范围内且没撞墙时移动)
        if (Math.abs(dx) > 20 && Math.abs(dx) < 250) {
            double dir = dx > 0 ? 1 : -1;
            double targetX = view.getX() + dir * speed;
            boolean hitWall = false;
            for (ImageView paper : papers) {
                if ("SPECIAL_WALL".equals(paper.getId()) && isColliding(targetX, view.getY(), paper)) {
                    hitWall = true; break;
                }
            }
            if (!hitWall) nextX = targetX;
            view.setScaleX(dir);
        }

        // 2. 核心：升降梯检测与绑定
        boolean onMovingPlatform = false;
        double currentBottom = view.getBoundsInParent().getMaxY();

        for (ImageView paper : papers) {
            var paperBounds = paper.getBoundsInParent();

            // 检测鸭子是否在纸片上方且横向重合
            boolean horizontalMatch = view.getBoundsInParent().getMaxX() > paperBounds.getMinX() &&
                    view.getBoundsInParent().getMinX() < paperBounds.getMaxX();

            // 判定：脚底距离平台表面很近（5像素以内）且正在下降或静止
            if (horizontalMatch && Math.abs(currentBottom - paperBounds.getMinY()) < 5) {
                // 核心：强制同步 Y 坐标，实现“粘附”效果
                view.setY(paperBounds.getMinY() - view.getFitHeight());
                velY = 0;
                onMovingPlatform = true;
                break;
            }
        }

        // 3. 如果没在平台上，应用重力
        if (!onMovingPlatform) {
            velY += GRAVITY;
            view.setY(view.getY() + velY);

            // 地面碰撞
            if (view.getY() + view.getFitHeight() > groundY) {
                view.setY(groundY - view.getFitHeight());
                velY = 0;
            }
        }

        view.setX(nextX);
    }

    private boolean isColliding(double x, double y, ImageView target) {
        return x + view.getFitWidth() > target.getX() && x < target.getX() + target.getFitWidth() &&
                y + view.getFitHeight() > target.getY() && y < target.getY() + target.getFitHeight();
    }

    public void addToPane(Pane pane) { pane.getChildren().add(view); }
    public ImageView getView() { return view; }
}
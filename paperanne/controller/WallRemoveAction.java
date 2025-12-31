package com.paperanne.controller;
import javafx.scene.image.ImageView;

// 实现类2：控制墙壁移除
public class WallRemoveAction implements MechanismAction {
    private ImageView wall;
    private double originalY;

    public WallRemoveAction(ImageView wall) {
        this.wall = wall;
        this.originalY = wall.getY();
    }

    @Override
    public void execute(boolean isActivated) {
        wall.yProperty().unbind();
        // 激活时飞走，关闭时回来
        wall.setY(isActivated ? -1000 : originalY);
    }
}
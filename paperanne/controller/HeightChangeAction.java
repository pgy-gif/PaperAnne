package com.paperanne.controller;

import javafx.animation.TranslateTransition;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class HeightChangeAction implements MechanismAction {
    private ImageView view;
    private double activeY, normalY;
    private TranslateTransition transition;

    public HeightChangeAction(ImageView view, double activeY, double normalY) {
        this.view = view;
        this.activeY = activeY;
        this.normalY = normalY;

        // 初始化动画器
        this.transition = new TranslateTransition(Duration.seconds(2), view);
    }

    @Override
    public void execute(boolean isActivated) {
        view.yProperty().unbind(); // 依然需要防止绑定冲突

        // 计算相对于初始位置需要移动的偏移量
        // 注意：Translate 是增量移动，为了逻辑简单，我们直接动画移动到绝对 Y 坐标
        transition.stop();
        if (isActivated) {
            transition.setToY(activeY - view.getY());
        } else {
            transition.setToY(0); // 回到初始位置
        }
        transition.play();
    }
}
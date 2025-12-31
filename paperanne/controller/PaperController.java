package com.paperanne.controller;

import com.paperanne.model.PaperModel;
import javafx.scene.image.ImageView;

public class PaperController {
    private double dragOffsetX;

    public void makeDraggable(ImageView view, PaperModel model) {
        view.setOnMousePressed(e -> dragOffsetX = e.getSceneX() - view.getX());
        view.setOnMouseDragged(e -> {
            double newX = e.getSceneX() - dragOffsetX;
            // 限制在屏幕内左右移动，保持 y 在地面
            newX = Math.max(0, Math.min(newX, 800 - view.getFitWidth()));
            view.setX(newX);
            model.setX(newX);
        });
    }
}
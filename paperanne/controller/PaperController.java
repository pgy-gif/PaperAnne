package com.paperanne.controller;

import com.paperanne.model.PaperModel;
import javafx.scene.image.ImageView;
import java.util.List;

public class PaperController {
    private double dragOffsetX;
    private final double SCREEN_WIDTH = 800;
    private boolean isSyncing = false; // 新增：防止联动时产生无限递归

    // 单张纸片拖拽
    public void makeDraggable(ImageView view, PaperModel model) {

        view.setOnMousePressed(e -> dragOffsetX = e.getSceneX() - view.getX());

        view.setOnMouseDragged(e -> {
            model.setX(e.getSceneX() - dragOffsetX);
        });

        model.xProperty().addListener((obs, oldVal, newVal) -> {
            syncAndWrap(view, model, newVal.doubleValue());
        });
    }

    // 新增：专门用于“绑定联动”的方法
    public void makeDraggableWithGroup(ImageView view, PaperModel model, List<ImageView> group) {
        view.setOnMousePressed(e -> {
            dragOffsetX = e.getSceneX() - view.getX();
        });

        view.setOnMouseDragged(e -> {
            if (isSyncing) return; // 如果正在同步中，不处理新的拖拽输入

            double oldX = model.getX();
            double nextX = e.getSceneX() - dragOffsetX;
            double deltaX = nextX - oldX; // 计算这一帧移动了多少

            if (Math.abs(deltaX) < 0.01) return;

            isSyncing = true;
            try {
                // 遍历组内所有纸片，应用相同的位移增量
                for (ImageView otherView : group) {
                    PaperModel otherModel = (PaperModel) otherView.getUserData();
                    if (otherModel != null) {
                        // 触发其他纸片的 model.setX，会进入它们各自的 Listener 处理环绕
                        otherModel.setX(otherModel.getX() + deltaX);

                        // 特殊处理：如果是当前被鼠标按住的这张纸瞬移了，我们需要修正 dragOffsetX
                        // 修正逻辑提取到了下面的监听器里
                    }
                }
            } finally {
                isSyncing = false;
            }
        });

        // 核心监听器：处理环绕和视图同步（逻辑与你提供的一致）
        model.xProperty().addListener((obs, oldVal, newVal) -> {
            double rawX = newVal.doubleValue();
            double width = view.getFitWidth();
            double correctedX = rawX;

            if (rawX >= SCREEN_WIDTH) {
                correctedX = -width + 1;
                // 只有当前这张纸是被鼠标拖拽的那张时，才修正 offset
                // 这样联动组的其他纸片瞬移时不会干扰你的鼠标坐标
                if (view.isPressed()) dragOffsetX += (SCREEN_WIDTH + width);
            } else if (rawX <= -width) {
                correctedX = SCREEN_WIDTH - 1;
                if (view.isPressed()) dragOffsetX -= (SCREEN_WIDTH + width);
            }

            view.setX(correctedX);
            if (correctedX != rawX) {
                model.setX(correctedX);
            }
        });
    }

    // 环绕逻辑
    private void syncAndWrap(ImageView view, PaperModel model, double rawX) {
        double width = view.getFitWidth();
        double correctedX = rawX;

        if (rawX >= SCREEN_WIDTH) {
            correctedX = -width + 1;
            dragOffsetX += (SCREEN_WIDTH + width);
        } else if (rawX <= -width) {
            correctedX = SCREEN_WIDTH - 1;
            dragOffsetX -= (SCREEN_WIDTH + width);
        }

        view.setX(correctedX);
        if (correctedX != rawX) model.setX(correctedX);
    }
}
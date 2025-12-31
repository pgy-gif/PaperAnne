package com.paperanne.controller;

import com.paperanne.model.*;
import com.paperanne.view.GameView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

/**
 * 游戏控制器
 */
public class GameController extends GameController_Abstract {



    @Override
    public void addEnemy(){
        // A. 地面敌人 (Ground Enemy)
        // 范围：x=500 到 x=800，绑定对象：null//详细赋值需在子类中实现
        Enemy groundEnemy = new Enemy(600, view.GROUND_Y - 40, 500, 800, null);
        enemies.add(groundEnemy);
        view.getRootPane().getChildren().add(groundEnemy.painter());

        // B. 纸片敌人 (Platform Enemy)
        // 找到第一个纸片 (假设它是那个可以拖动的平台)
        if (!view.getPaperViews().isEmpty()) {
            ImageView platformView = view.getPaperViews().get(0);
            PaperModel platformModel = (PaperModel) platformView.getUserData();

            // 创建敌人，位置设为纸片的X，Y自动计算，范围由 Model 决定
            // 初始 X 设为 platformModel.getX() + 20 (站在纸片中间)//须在子类中实现
            Enemy skyEnemy = new Enemy(platformModel.getX() + 20, 0, 0, 0, platformModel);
            enemies.add(skyEnemy);
            view.getRootPane().getChildren().add(skyEnemy.painter());
        }

        setCurrentLevelId(1);
    }

    public GameController(GameView view) {
        super(view);
    }



}


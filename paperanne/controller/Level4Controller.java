package com.paperanne.controller;

import com.paperanne.model.*;
import com.paperanne.view.Level4View;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.List;

public class Level4Controller extends GameController_Abstract {

    private Level4View l4View;

    public Level4Controller(Level4View view) {
        // 传入关卡ID为 4
        super(view, 4);
        this.l4View = view;

        for (Key k : Key.getItemList()) {
            ImageView keyImg = k.painter();
            if (!view.getRootPane().getChildren().contains(keyImg)) {
                view.getRootPane().getChildren().add(keyImg);
            }

        }

        startLevelLoop();

        //为纸片设定小组，拖拽时联动
        List<ImageView> draggableGroup = view.getDraggablePapers();

        for (ImageView pv : draggableGroup) {
            PaperModel pm = (PaperModel) pv.getUserData();
            // 使用新添加的带组功能的方法
            getPaperCtrl().makeDraggableWithGroup(pv, pm, draggableGroup);
        }

        for (ImageView portalView : view.getPortalGroup()) {
            // 为传送门创建临时的 PaperModel 用于存储坐标和处理环绕
            PaperModel portalModel = new PaperModel(portalView.getX(), portalView.getY(),
                    portalView.getFitWidth(), portalView.getFitHeight());
            portalView.setUserData(portalModel); // 必须存入 UserData 供 Controller 读取

            // 调用联动方法
            getPaperCtrl().makeDraggableWithGroup(portalView, portalModel, view.getPortalGroup());
        }
    }

    private void startLevelLoop() {
        this.timeline = new Timeline(new KeyFrame(Duration.millis(16), e -> {
            if (isGameFinished || checkLevel() == -1) return;

            // 获取特定纸片模型（用于玩家逻辑）
            PaperModel target = null;
            if (!view.getPaperViews().isEmpty()) {
                target = (PaperModel) view.getPaperViews().get(0).getUserData();
            }

            // A. 执行玩家更新
            playerCtrl.update(playerModel, view.getPlayerView(), activeKeys, view.GROUND_Y,
                    view.getPaperViews(), view.getCakeView(), view.getInteractTip(),
                    view.getPotionView(), view.getPortalA(), view.getPortalB(),
                    view.getDoorView(), view.getDoorOpenImg(), view.getKeyUI(),
                    view.getLeverView(), view.getLeverLeftImg(), view.getLeverRightImg(),
                    target, view.getLadders(), view.getLevers());

            // B. 执行关卡特有更新（如丑小鸭）
            Duck d = ((Level4View) view).getDuck();
            if (d != null) {
                d.update(playerModel.getX(), playerModel.getY(), view.getPaperViews(), view.GROUND_Y);
            }

            // C. 执行通用检测（调用父类 protected 方法）
            checkLevelComplete();
            checkPickUp();

            // D. 敌人逻辑
            for (Enemy en : enemies) {
                en.update(playerModel);
                en.painter();
                checkEnemyCollision(en);
            }
        }));
        this.timeline.setCycleCount(Timeline.INDEFINITE);
        this.timeline.play();
    }

    @Override
    public void addEnemy() {
        enemies.clear();
        BugEnemy.isAlerted = false;

        if (view == null) return;

        // 创建敌人实例
        BugEnemy bug1 = new BugEnemy(150, 460, 50, 400, null);
        BugEnemy bug2 = new BugEnemy(600, 110, 450, 750, null);
        FlyEnemy fly = new FlyEnemy(400, 290, 100, 700, null);

        enemies.add(bug1);
        enemies.add(bug2);
        enemies.add(fly);

        // 将所有图像节点加入场景
        for (Enemy e : enemies) {
            // 添加敌人本身的图片
            view.getRootPane().getChildren().add(e.getImg());

            // 特别处理：如果是虫子，把它的感叹号也加进去
            if (e instanceof BugEnemy) {
                BugEnemy bug = (BugEnemy) e;
                ImageView alert = bug.getAlertIcon();

                // 必须先加入 Children 列表，才能显示
                if (!view.getRootPane().getChildren().contains(alert)) {
                    view.getRootPane().getChildren().add(alert);
                }
                //防止被其他图片遮挡
                alert.toFront();
            }
        }
    }
}
package com.paperanne.controller;

import com.paperanne.model.Enemy;
import com.paperanne.model.PaperModel;
import com.paperanne.model.PlayerModel;
import com.paperanne.view.GameView;
import com.paperanne.view.GameView_level3;
import javafx.scene.image.ImageView;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class GameController_level3 extends GameController_Abstract {

    private boolean isSyncing = false;
    private List<ImageView> cloudViews = new ArrayList<>(); // 用于记录所有的云朵视图

    private List<ImageView> physicsBackup = new ArrayList<>(); // 物理箱备份
    private boolean isFallingThrough = false; // 核心状态锁：是否处于穿透坠落中

    public GameController_level3(GameView view) {
        super(view);
        PlayerModel playerModel = getPlayerModel();

        setupLevel3Papers();
        GameView_level3 v3 = (GameView_level3) view;

        // 2. 添加云朵并记录它们
        v3.addCloud(0, 130, 800, 40, this.paperCtrl, false);

        // 从总列表中提取云朵存入 cloudViews
        // 假设最后添加的两个是云朵（或者根据你 addCloud 的实现逻辑）
        List<ImageView> allPapers = view.getPaperViews();
        cloudViews.add(allPapers.get(allPapers.size() - 2));
        cloudViews.add(allPapers.get(allPapers.size() - 1));

        // 3. 核心取巧逻辑：监听玩家大小变化
        // 假设 getPlayerModel() 已经在父类中定义
        PlayerModel pm = getPlayerModel();

        // 初始检查一次
        handleCloudCollisionState(pm.sizeState);

        new javafx.animation.AnimationTimer() {
            @Override
            public void handle(long now) {
                PlayerModel pm = getPlayerModel();
                ImageView pv = view.getPlayerView();
                List<ImageView> currentPapers = view.getPaperViews();

                double playerBottom = pm.y + pv.getFitHeight();

                // --- 逻辑 1：触发穿透 ---
                // 只有当：玩家不在穿透模式 + 玩家变大/正常 + 玩家脚底碰撞到了云朵
                if (!isFallingThrough && pm.sizeState != -1) {
                    for (ImageView cloud : cloudViews) {
                        // 碰撞检测：脚底在云朵上边缘附近，且 velY 试图停下
                        if (pv.getBoundsInParent().intersects(cloud.getBoundsInParent())
                                && playerBottom >= cloud.getY() && playerBottom <= cloud.getY() + 15) {

                            // 开始穿透
//                            for(ImageView img:physicsBackup){
//                                if(img != ((GameView_level3)view).getReversiblePaper()){
//                                    physicsBackup.remove(img);
//                                }
//                            }
                            physicsBackup.clear();
                            ImageView reversible = ((GameView_level3)view).getReversiblePaper();

                            physicsBackup.addAll(currentPapers);
                            currentPapers.removeIf(img -> img !=((GameView_level3)view).getReversiblePaper());
//                            for(ImageView img:currentPapers){
//                                if(img != ((GameView_level3)view).getReversiblePaper()){
//                                    currentPapers.remove(img);
//                                }
//                            }
                            //currentPapers.clear();
                            isFallingThrough = true;
                            System.out.println("检测到踩云且非缩小态：开启虚化坠落");


                            //从云上坠落扣血
                            int damage = 1;
                            long currentTime = System.currentTimeMillis();
                            playerModel.health -= damage;
                            playerModel.lastDamageTime = currentTime;
                            System.out.println("受到伤害！剩余血量: " + playerModel.health);

                            //updateHealthUI();

                            // 延迟更新UI
                            Timer delayTimer = new Timer(700, e -> { // 延迟1秒
                                updateHealthUI();
                                ((Timer)e.getSource()).stop(); // 执行一次后停止
                            });
                            delayTimer.setRepeats(false); // 只执行一次
                            delayTimer.start();
                            break;
                        }
                    }
                }

                // --- 逻辑 2：坠落中强制维持虚化 ---
                if (isFallingThrough) {
                    // 确保每一帧列表都是空的，防止其他逻辑干扰
                    if (!currentPapers.isEmpty())
                    {
                        for(ImageView img:currentPapers){
                            if(img != ((GameView_level3)view).getReversiblePaper()){
                                currentPapers.remove(img);
                            }
                        }
                    }
                    //currentPapers.clear();

                    // --- 逻辑 3：落地恢复 ---
                    // 500 是 GROUND_Y。当脚底几乎贴地时，强制恢复
                    if (playerBottom >= 498) {
                        currentPapers.addAll(physicsBackup);
                        physicsBackup.clear();
                        isFallingThrough = false;

                        // 落地后，根据当前大小重新计算云朵实体状态
                        handleCloudCollisionState(pm.sizeState);
                        System.out.println("已落地：恢复物理实体");

                    }
                } else {
                    // 正常模式：每帧检查云朵是否应该存在于碰撞名单
                    handleCloudCollisionState(pm.sizeState);
                }
            }
        }.start();

    }

    private void handleCloudCollisionState(int sizeState) {
        // 只有在非穿透状态下才执行此逻辑
        if (isFallingThrough) return;

        List<ImageView> allPapers = view.getPaperViews();
        if (sizeState == -1) {
            for (ImageView cloud : cloudViews) {
                if (!allPapers.contains(cloud)) allPapers.add(cloud);
            }
        } else {
            allPapers.removeAll(cloudViews);
        }
    }

    private void setupLevel3Papers() {
        List<ImageView> allPapers = view.getPaperViews();
        GameView_level3 v3 = (GameView_level3) view;

        // 1. 初始化模型并绑定基础拖拽
        for (ImageView pv : allPapers) {
            PaperModel model = new PaperModel(pv.getX(), pv.getY(), pv.getFitWidth(), pv.getFitHeight());
            pv.setUserData(model); // 确保 UserData 永远是 Model

            pv.fitHeightProperty().bind(model.heightProperty());
            pv.yProperty().bind(model.yProperty());
            paperCtrl.makeDraggable(pv, model);

            // 2. 核心联动逻辑：在这里给每个 Model 绑定 X 监听，屏幕环绕
            model.xProperty().addListener((obs, oldX, newX) -> {
                if (isSyncing) return;

                double rawNewX = newX.doubleValue();
                double paperWidth = pv.getFitWidth();
                double correctedX = rawNewX;

                // --- 在这里执行环绕判定 ---
                if (rawNewX > 800) {
                    correctedX = -paperWidth;
                } else if (rawNewX < -paperWidth) {
                    correctedX = 800;
                }

                // 如果发生了环绕，更新当前纸片位置
                if (correctedX != rawNewX) {
                    isSyncing = true;
                    model.setX(correctedX);
                    pv.setX(correctedX);
                    isSyncing = false;
                    return; // 坐标已修正，跳过本次联动计算
                }

                // --- 原有的同组联动逻辑 ---
                double deltaX = correctedX - oldX.doubleValue();
                if (Math.abs(deltaX) < 0.01) return;

                isSyncing = true;
                try {
                    List<ImageView> currentGroup = v3.getPurplePapers().contains(pv) ?
                            v3.getPurplePapers() : v3.getBluePapers();

                    for (ImageView otherView : currentGroup) {
                        if (otherView == pv) continue;
                        PaperModel otherModel = (PaperModel) otherView.getUserData();
                        if (otherModel != null) {
                            double targetX = otherModel.getX() + deltaX;

                            // 对组内其他成员也应用环绕逻辑
                            if (targetX > 800) targetX = -otherView.getFitWidth();
                            else if (targetX < -otherView.getFitWidth()) targetX = 800;

                            otherModel.setX(targetX);
                            otherView.setX(targetX);
                        }
                    }
                } finally {
                    isSyncing = false;
                }
            });
        }

        // 3. 绑定特殊物体
        if (allPapers.size() >= 4) {
            bindObjectToPaper(view.getDoorView(), allPapers.get(3));
        }
        bindObjectToPaper(view.getLeverView(), allPapers.get(2));
    }

    public void bindObjectToPaper(ImageView target, ImageView platformView) {
        PaperModel model = (PaperModel) platformView.getUserData();
        if (model == null || target == null) return;

        // --- 核心修改：计算“上边界中心点”的固定偏移量 ---
        // X 偏移量 = (纸片宽度 - 物体宽度) / 2
        double offsetX = (model.getWidth() - target.getFitWidth()) / 2.0;
        // Y 偏移量 = -物体高度 (因为是在上边界上方)
        double offsetY = -target.getFitHeight();

        // 1. 初始化位置：立即将物体设置到该位置
        target.setX(model.getX() + offsetX);
        target.setY(model.getY() + offsetY);

        // 2. 监听纸片 X 轴移动
        model.xProperty().addListener((obs, oldVal, newVal) -> {
            target.setX(newVal.doubleValue() + offsetX);
        });

        // 3. 监听纸片 Y 轴移动 (处理纸片长高或移动 Y 的情况)
        model.yProperty().addListener((obs, oldVal, newVal) -> {
            target.setY(newVal.doubleValue() + offsetY);
        });
    }



    @Override
    public void addEnemy() {
//        if (view.getPaperViews().size() >= 2) {
//            ImageView redPaperView = view.getPaperViews().get(1);
//            PaperModel redPaperModel = (PaperModel) redPaperView.getUserData();
//            Enemy paperPatrolEnemy = new Enemy( 10, 0, 0, 0, redPaperModel);
//            enemies.add(paperPatrolEnemy);
//            view.getRootPane().getChildren().add(paperPatrolEnemy.painter());
//        }
        //        // 范围：x=500 到 x=800，绑定对象：null//详细赋值需在子类中实现
        Enemy groundEnemy = new Enemy(600, view.GROUND_Y - 40, 500, 800, null);
        enemies.add(groundEnemy);
        view.getRootPane().getChildren().add(groundEnemy.painter());
    }
}

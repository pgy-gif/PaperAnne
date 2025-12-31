package com.paperanne.controller;

import com.paperanne.model.*;
import com.paperanne.view.GameView;
import com.paperanne.view.Level4View;
import com.paperanne.view.ResultOverlay;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.paperanne.utils.LevelService;
import com.paperanne.utils.SceneNavigator;

import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

public abstract class GameController_Abstract {
    protected PlayerModel playerModel = new PlayerModel();
    protected PlayerController playerCtrl = new PlayerController();
    protected PaperController paperCtrl = new PaperController();
    protected Set<KeyCode> activeKeys = new HashSet<>();
    protected GameView view;
    protected List<Enemy> enemies = new ArrayList<>();//多个敌人
    //12/28 1629新增过关逻辑
    protected static int leveler = 0;

    //12/29 新增判断是否关卡结束
    protected boolean isGameFinished = false;

    //import levelService
    protected LevelService levelService = new LevelService();
    protected int currentLevelId;

    protected Timeline timeline;

    //12/28 1638检关卡切换&检查
    public static void updateLevel(){
        leveler = 1;
    }
    //12/28 1638重写含参版本，用于设置关卡失败状态
    public static void updateLevel(int leveler){
        if (leveler == -1 || leveler == 0){
            GameController.leveler = leveler;
        }
    }
    public static int checkLevel(){
        return leveler;
    }

    public GameController_Abstract(GameView view,int levelId){
        this.view = view;
        this.currentLevelId = levelId;

        for (ImageView pv : view.getPaperViews()) {
            PaperModel model = new PaperModel(pv.getX(), pv.getY(), pv.getFitWidth(), pv.getFitHeight());
            pv.setUserData(model);
            pv.fitHeightProperty().bind(model.heightProperty());
            pv.yProperty().bind(model.yProperty());
        }
        addEnemy();
    }
    protected void checkPickUp() {
        var iterator = Key.getItemList().iterator();
        while (iterator.hasNext()) {
            Key item = iterator.next();

            if (playerModel.getHitBox().checkHit(item.getHitBox())) {
                // --- 新增：遮挡检测 ---
                boolean isCovered = false;
                for (ImageView paperView : view.getPaperViews()) {
                    // 如果这个纸片包含了钥匙的中心点，且纸片是可见的
                    if (paperView.getBoundsInParent().contains(item.getX() + 32, item.getY() + 32)) {
                        isCovered = true;
                        break;
                    }
                }
                if (!isCovered) {
                    // 1. 从地图上移除钥匙实体
                    view.getRootPane().getChildren().remove(item.painter());
                    iterator.remove();

                    // 2. 更新数据状态
                    playerModel.hasKey = true;

                    // 3. 更新 UI 显示：让右下角的图标可见
                    if (view.getKeyUI() != null) {
                        view.getKeyUI().setVisible(true);
                    }

                    System.out.println("捡到了钥匙，UI已更新！");
                }
            }else{
                System.out.println("似乎有什么东西在下面");
            }
        }
    }

    public abstract void addEnemy();
    /**
     * 处理敌人碰撞与受伤逻辑
     */
    protected void checkEnemyCollision(Enemy en) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - playerModel.lastDamageTime < playerModel.INVINCIBLE_DURATION) {
            return;
        }

        if (playerModel.sizeState == 1) {
            return;
        }

        // 检测特定敌人的碰撞
        //12/28 1609新增isSamePlane（）检测是否在同一平面
        if (playerModel.getHitBox().checkHit(en.getHitBox()) && en.isSamePlane()) {
            int damage = (playerModel.sizeState == -1) ? 2 : 1;

            playerModel.health -= damage;
            playerModel.lastDamageTime = currentTime;
            System.out.println("受到伤害！剩余血量: " + playerModel.health);

            updateHealthUI();

            if (playerModel.health <= 0) {
                playerModel.health = 0;
                playerModel.isDead = true;
                showResult(false);
                updateLevel(-1);//12/28 1707 新增设定关卡状态level为-1
//                view.getGameOverText().setVisible(true);
//                view.getGameOverText().toFront();


            }
        }
    }

    /**
     * 更新左上角的红心显示
     */
    protected void updateHealthUI() {
        StringBuilder hearts = new StringBuilder("HP: ");
        for (int i = 0; i < playerModel.health; i++) {
            hearts.append("❤");
        }
        view.getHealthText().setText(hearts.toString());
    }

    protected void checkLevelComplete() {
        if (isGameFinished) return;

        // 1. 基础条件：安妮必须拿到钥匙TODO:加入钥匙，这里为测试临时修改
        if (!playerModel.hasKey) {
            ImageView door = view.getSuccessDoorView();
            if (door == null) return; // 防御性检查

            // 计算安妮与终点门的距离
            double dx = playerModel.getX() - door.getX();
            double dy = playerModel.getY() - door.getY();
            double playerDist = Math.sqrt(dx * dx + dy * dy);

            // --- 核心判定逻辑 ---
            boolean canPass = false;

            if (view instanceof Level4View) {
                // 第4关特殊逻辑：安妮和鸭子都要在场
                Duck duck = ((Level4View) view).getDuck();
                if (duck != null) {
                    double ddx = duck.getView().getX() - door.getX();
                    double ddy = duck.getView().getY() - door.getY();
                    double duckDist = Math.sqrt(ddx * ddx + ddy * ddy);

                    // 判定：安妮在 50px 内，鸭子在 80px 内
                    if (playerDist < 50 && duckDist < 80) {
                        canPass = true;
                    } else if (playerDist < 50) {
                        // 安妮到了但鸭子没到，显示提示
                        view.getInteractTip().setText("等等丑小鸭！");
                        view.getInteractTip().setVisible(true);
                    }
                }
            } else {
                // 其他普通关卡逻辑：只需安妮到达
                if (playerDist < 50) {
                    canPass = true;
                }
            }

            // 2. 执行通关
            if (canPass) {
                isGameFinished = true;
                showResult(true);
            }
        }
    }

    protected void showResult(boolean isSuccess) {
        // ... 停止逻辑 ...
        activeKeys.clear();
        this.stopGameLoop();
        isGameFinished = true;

        // 1. 计算星数
        int stars = 0;
        if (isSuccess) {
            stars = (playerModel.health >= 5) ? 3 : (playerModel.health >= 3 ? 2 : 1);

            // 2. 业务逻辑外包给 LevelService
            levelService.handleLevelPass(currentLevelId, stars);
        }

        // 3. 获取当前窗口 Stage (用于跳转)
        // 注意：view.getScene() 可能在某些初始化阶段为空，但在游戏结束时肯定不为空
        Stage currentStage = (Stage) view.getPlayerView().getScene().getWindow();

        ResultOverlay overlay = new ResultOverlay(isSuccess, stars,
                () -> {
                    System.out.println("请求重开...");
                    GameController_Abstract.updateLevel(0);
                    // 再次确保停止，防止某些极端情况下没停掉
                    this.stopGameLoop();
                    SceneNavigator.toGameLevel(currentStage, currentLevelId);
                },
                () -> {
                    this.stopGameLoop();
                    SceneNavigator.toMenu(currentStage);
                }
        );

        view.getRootPane().getChildren().add(overlay);
        overlay.toFront();
    }
    public void setCurrentLevelId(int id) {
        this.currentLevelId = id;
    }

    // 新增：专门用于绑定按键的方法
    public void setupInputListeners(Scene scene) {
        if (scene != null) {
            scene.setOnKeyPressed(e -> activeKeys.add(e.getCode()));
            scene.setOnKeyReleased(e -> activeKeys.remove(e.getCode()));
            // 确保场景获得焦点，否则捕获不到按键
            view.getRootPane().requestFocus();
            System.out.println("按键监听器已成功绑定到 Scene");
        }
    }

    public PaperController getPaperCtrl() {
        return paperCtrl;
    }

    public void stopGameLoop() {
        if (timeline != null) {
            timeline.stop();
            System.out.println("旧的游戏循环已停止");
        }
        activeKeys.clear(); // 清空按键，防止新一局自动走
    }
}

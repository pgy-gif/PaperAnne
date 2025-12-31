package com.paperanne.controller;

import com.paperanne.model.*;
import com.paperanne.utils.LevelService;
import com.paperanne.view.GameView;
import com.paperanne.utils.*;
import com.paperanne.view.ResultOverlay;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

public abstract class GameController_Abstract {
    private PlayerModel playerModel = new PlayerModel();
    private PlayerController playerCtrl = new PlayerController();
    private PaperController paperCtrl = new PaperController();
    private Set<KeyCode> activeKeys = new HashSet<>();
    protected GameView view;
    protected List<Enemy> enemies = new ArrayList<>();//多个敌人
    //12/28 1629新增过关逻辑
    protected static int leveler = 0;

    //12/29 新增判断是否关卡结束
    private boolean isGameFinished = false;

    //import levelService
    private LevelService levelService = new LevelService();
    protected int currentLevelId;

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

    public GameController_Abstract(GameView view){
        this.view = view;
        for (ImageView pv : view.getPaperViews()) {
            // 创建模型
            PaperModel model = new PaperModel(pv.getX(), pv.getY(), pv.getFitWidth(), pv.getFitHeight());
            // 将模型存入 View 的 UserData 槽位，方便后续随时取用
            pv.setUserData(model);
            paperCtrl.makeDraggable(pv, model);
        }

        // 初始化纸片控制器
        for (ImageView pv : view.getPaperViews()) {
            // 1. 创建模型
            PaperModel model = new PaperModel(pv.getX(), pv.getY(), pv.getFitWidth(), pv.getFitHeight());

            // 2. 将模型存入 View 的 UserData (关键步骤，为了后面能取出来)
            pv.setUserData(model);

            // --- 核心修复：添加绑定 ---
            // 让图片的 高度 永远跟随 模型的 高度
            pv.fitHeightProperty().bind(model.heightProperty());

            // 让图片的 Y坐标 永远跟随 模型的 Y坐标
            pv.yProperty().bind(model.yProperty());
            // -------------------------

            // 3. 设置拖拽逻辑
            paperCtrl.makeDraggable(pv, model);
        }

        //钥匙初始化
        for (Key b : Key.getItemList()) {
            view.getRootPane().getChildren().add(b.painter());
        }

        addEnemy();


        // 输入监听
        view.getScene().setOnKeyPressed(e -> activeKeys.add(e.getCode()));
        view.getScene().setOnKeyReleased(e -> activeKeys.remove(e.getCode()));

        // 游戏循环 (60 FPS)
        Timeline gameLoop = new Timeline(new KeyFrame(Duration.millis(16), e -> {
            // 如果玩家死了，停止所有逻辑，只显示 Game Over//12/28 1701 将判断条件设置为level = -1
            if (checkLevel() == -1) {
                return;
            } else if (checkLevel() == 1) {
                return;
            }
            // 动态获取目标纸片模型：例如获取纸片列表中的第0个
            PaperModel targetModel = null;
            if (!view.getPaperViews().isEmpty()) {
                targetModel = (PaperModel) view.getPaperViews().get(0).getUserData();
            }
            playerCtrl.update(
                    playerModel,             // 玩家模型
                    view.getPlayerView(),    // 玩家视图
                    activeKeys, // 按键集合
                    view.GROUND_Y,  // 地面高度
                    view.getPaperViews(),    // 纸片列表
                    view.getCakeView(),    // 蛋糕
                    view.getInteractTip(), // 交互提示
                    view.getPotionView(),   // 药水
                    view.getPortalA(), // 传送门A参数
                    view.getPortalB(),// 传送门B参数
                    view.getDoorView(),      // 传入门
                    view.getDoorOpenImg(),  // 传入开门图片
                    view.getKeyUI(),   // 传入右下角钥匙UI
                    view.getLeverView(),    // 传入拉杆视图
                    view.getLeverLeftImg(), // 传入左侧图片
                    view.getLeverRightImg(), // 传入右侧图片
                    targetModel,
                    view.getAchievementList()
            );
            checkLevelComplete();
            // --- 2. 更新敌人逻辑 ---
            for (Enemy en : enemies) {
                en.update(playerModel);
                en.painter(); // 刷新 UI 位置
                checkEnemyCollision(en); // 传入当前循环到的敌人进行检测
            }
            checkPickUp();
        }));
        gameLoop.setCycleCount(Timeline.INDEFINITE);
        gameLoop.play();
    }
    private void checkPickUp() {
        var iterator = Key.getItemList().iterator();
        while (iterator.hasNext()) {
            Key item = iterator.next();

            if (playerModel.getHitBox().checkHit(item.getHitBox())) {
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
        }
    }

    public abstract void addEnemy();
    /**
     * 处理敌人碰撞与受伤逻辑
     */
    private void checkEnemyCollision(Enemy en) {
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
                updateLevel(-1);//12/28 1707 新增设定关卡状态level为-1
                view.getGameOverText().setVisible(true);
                view.getGameOverText().toFront();
            }
        }
    }

    /**
     * 更新左上角的红心显示
     */
    private void updateHealthUI() {
        StringBuilder hearts = new StringBuilder("HP: ");
        for (int i = 0; i < playerModel.health; i++) {
            hearts.append("❤");
        }
        view.getHealthText().setText(hearts.toString());
    }

    private void checkLevelComplete() {
        if (isGameFinished) return;
        if (checkLevel() == 1){
            isGameFinished = true;
            showResult(true);
        }
    }

    private void showResult(boolean isSuccess) {
        // ... 停止逻辑 ...
        activeKeys.clear();
        isGameFinished = true;

        // 1. 计算星数
        int stars = 0;
        int achievements = 0;
        if (isSuccess) {
            for(Achievement a : view.getAchievementList()){
                if(a.isPickedUp()){
                    achievements++;
                }
            }
            if(achievements == 2){
                if(playerModel.health >= 3){
                    stars = 3;
                }
                else{
                    stars = 2;
                }
            }
            else{
                if(playerModel.health >= 3){
                    stars = 2;
                }
                else{
                    stars = 1;
                }
            }

            // 2. 业务逻辑外包给 LevelService
            levelService.handleLevelPass(currentLevelId, stars);
        }

        // 3. 获取当前窗口 Stage (用于跳转)
        // 注意：view.getScene() 可能在某些初始化阶段为空，但在游戏结束时肯定不为空
        Stage currentStage = (Stage) view.getPlayerView().getScene().getWindow();

        // 4. 创建 UI，把跳转逻辑外包给 SceneNavigator
        ResultOverlay overlay = new ResultOverlay(isSuccess, stars,
                () -> {
                    // 点击 Restart -> 委托给 Navigator
                    System.out.println("请求重开...");
                    SceneNavigator.toGameLevel(currentStage, currentLevelId);
                },
                () -> {
                    // 点击 Menu -> 委托给 Navigator
                    System.out.println("请求回菜单...");
                    SceneNavigator.toMenu(currentStage);
                }
        );

        view.getRootPane().getChildren().add(overlay);
        overlay.toFront();
    }
    public void setCurrentLevelId(int id) {
        this.currentLevelId = id;
    }
}

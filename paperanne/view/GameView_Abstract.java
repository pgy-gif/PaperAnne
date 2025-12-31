package com.paperanne.view;

import com.paperanne.model.Achievement;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public abstract class GameView_Abstract {
    protected Pane root = new Pane();
    private ImageView playerView;
    private List<ImageView> paperViews = new ArrayList<>();
    private Scene scene;
    private ImageView keyUI; // 钥匙 UI
    private Text interactTip; // 交互提示
    //蛋糕和药水
    protected ImageView cakeView; // 蛋糕
    protected ImageView potionView;   // 药水 UI

    //传送门
    protected ImageView portalA;
    protected ImageView portalB;

    //门
    protected ImageView doorView;
    protected Image doorClosedImg;
    protected Image doorOpenImg;

    //拉杆
    protected ImageView leverView;
    protected Image leverLeftImg;
    protected Image leverRightImg;
    protected double[][] positions;


    // 逻辑地面高度：角色脚底和纸片底部都在这个高度
    public final double GROUND_Y = 500;
    private final double SCENE_WIDTH = 800;
    private final double SCENE_HEIGHT = 545;

    //生命值和gameOver，TODO：gameOver待更新
    private javafx.scene.text.Text healthText;
    private javafx.scene.text.Text gameOverText;

    protected List<Achievement> achievementList;

    public GameView_Abstract() {
        // 1. 添加背景图 (最底层)
        try {
            Image backgroundImg = new Image(getClass().getResourceAsStream(setBg()));
            ImageView bgView = new ImageView(backgroundImg);
            bgView.setFitWidth(SCENE_WIDTH);
            bgView.setFitHeight(SCENE_HEIGHT);
            root.getChildren().add(bgView);
        } catch (Exception e) {
            // 背景加载失败的备选方案
            root.getChildren().add(new Rectangle(SCENE_WIDTH, SCENE_HEIGHT, Color.LIGHTBLUE));
        }

        addAllComponents();

        // 2.添加地面图片
        try {
            Image groundImg = new Image(getClass().getResourceAsStream("/com/paperanne/paperanne/images/ground.png"));
            ImageView groundView = new ImageView(groundImg);

            // 设置位置：图片的上边缘 = 逻辑地面高度
            groundView.setX(0);
            groundView.setY(GROUND_Y);

            // 设置尺寸：
            // 宽度 = 窗口宽度 (铺满左右)
            groundView.setFitWidth(SCENE_WIDTH);
            // 高度 = 窗口高度 - 地面起始Y (铺满底部剩余空间)
            groundView.setFitHeight(SCENE_HEIGHT - GROUND_Y);

            // 强制拉伸图片以填满目标区域 (防止图片比例不符导致留白)
            groundView.setPreserveRatio(false);

            root.getChildren().add(groundView);
        } catch (Exception e) {
            System.out.println("地面图片加载失败");
            // 如果图片加载失败，用原来的灰色矩形代替，保证能看见地
            Rectangle groundRect = new Rectangle(0, GROUND_Y, SCENE_WIDTH, SCENE_HEIGHT - GROUND_Y);
            groundRect.setFill(Color.DARKGRAY);
            root.getChildren().add(groundRect);
        }
        // ------------------------------------------

        // 3. 初始化纸片 (上层)
        Image pImg = new Image(getClass().getResourceAsStream("/com/paperanne/paperanne/images/paper_red.png"));
        // 注意：这里的 Y=450，纸片高度=50，所以纸片底部 = 450+50 = 500 (GROUND_Y)，正好接在地面图片上方
        //double[][] positions = {{200, 450, 150, 50}, {450, 450, 100, 50}, {100, 450, 80, 50}};
        setPositions();
        for (double[] p : getPositions()) {
            ImageView pv = new ImageView(pImg);
            pv.setX(p[0]);
            pv.setY(p[1]);
            pv.setFitWidth(p[2]);
            pv.setFitHeight(p[3]);
            paperViews.add(pv);
            root.getChildren().add(pv);
        }

        // 4. 初始化角色 (最上层)
        playerView = new ImageView(new Image(getClass().getResourceAsStream("/com/paperanne/paperanne/images/player.png")));
        playerView.setFitWidth(40);
        playerView.setFitHeight(60);
        root.getChildren().add(playerView);

        scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);

        try {
            Image keyImage = new Image(getClass().getResourceAsStream("/com/paperanne/paperanne/images/key_yellow.png"));
            keyUI = new ImageView(keyImage);
            keyUI.setFitWidth(40);
            keyUI.setFitHeight(40);

            // 固定在右下角 (Scene 宽度 800, 高度 545)
            keyUI.setX(740);
            keyUI.setY(500);

            keyUI.setOpacity(0.7);

            keyUI.setVisible(false); // 初始状态不可见
            root.getChildren().add(keyUI);
        } catch (Exception e) {
            System.out.println("UI钥匙图标加载失败");
        }

        // 添加蛋糕


        // --- 初始化 UI：生命值 ---
        healthText = new javafx.scene.text.Text("HP: ❤❤❤❤❤");
        healthText.setStyle("-fx-font-size: 24; -fx-fill: red; -fx-font-weight: bold;");
        healthText.setX(20);
        healthText.setY(40);
        root.getChildren().add(healthText);

        // --- 初始化 UI：游戏失败 ---
        gameOverText = new javafx.scene.text.Text("GAME OVER");
        gameOverText.setStyle("-fx-font-size: 60; -fx-fill: black; -fx-font-weight: bold;");
        gameOverText.setX(SCENE_WIDTH / 2 - 150);
        gameOverText.setY(SCENE_HEIGHT / 2);
        gameOverText.setVisible(false); // 默认隐藏
        root.getChildren().add(gameOverText);



        // 无论之前添加了什么，把玩家提到最前面
        if (playerView != null) {
            playerView.toFront();
        }
        // 提示文字应该在玩家更上层
        if (interactTip != null) {
            interactTip.toFront();
        }
        // 添加交互提示 (按 E 交互)
        interactTip = new javafx.scene.text.Text("按 E 交互");
        interactTip.setStyle("-fx-font-size: 16; -fx-fill: white; -fx-font-weight: bold;");
        interactTip.setVisible(false); // 默认隐藏
        root.getChildren().add(interactTip);
    }

    protected abstract void addAllComponents();

    public double[][] getPositions(){
        return positions;
    }
    public abstract void setPositions();


    public abstract String setBg();

    public Scene getScene() {
        return scene;
    }

    public ImageView getPlayerView() {
        return playerView;
    }

    public List<ImageView> getPaperViews() {
        return paperViews;
    }

    public ImageView getCakeView() {
        return cakeView;
    }

    public javafx.scene.text.Text getInteractTip() {
        return interactTip;
    }

    public Pane getRootPane() {
        return root;
    }

    public ImageView getKeyUI() {
        return keyUI;
    }

    public ImageView getPotionView() {
        return potionView;
    }

    public ImageView getPortalA() {
        return portalA;
    }

    public ImageView getPortalB() {
        return portalB;
    }

    public ImageView getDoorView() {
        return doorView;
    }

    public Image getDoorOpenImg() {
        return doorOpenImg;
    }

    public ImageView getLeverView() {
        return leverView;
    }

    public Image getLeverLeftImg() {
        return leverLeftImg;
    }

    public Image getLeverRightImg() {
        return leverRightImg;
    }

    public javafx.scene.text.Text getHealthText() {
        return healthText;
    }

    public javafx.scene.text.Text getGameOverText() {
        return gameOverText;
    }

    public void addEnemyView(ImageView view) {
        if (view != null) root.getChildren().add(view);
    }

    public ImageView getSuccessDoorView() {
        return doorView;
    }
    public List<Achievement> getAchievementList() {
        return achievementList;
    }

}

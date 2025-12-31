package com.paperanne.view;

import com.paperanne.model.PlayerModel;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;


import java.util.ArrayList;
import java.util.List;

public class GameView {
    private Pane root = new Pane();
    private ImageView playerView;
    private List<ImageView> paperViews = new ArrayList<>();
    private Scene scene;
    private ImageView keyUI; // 钥匙 UI
    private Text interactTip; // 交互提示
    //蛋糕和药水
    private ImageView cakeView; // 蛋糕
    private ImageView potionView;   // 药水 UI

    //传送门
    private ImageView portalA;
    private ImageView portalB;

    //门
    private ImageView doorView;
    private Image doorClosedImg;
    private Image doorOpenImg;

    //拉杆
    private ImageView leverView;
    private Image leverLeftImg;
    private Image leverRightImg;


    // 逻辑地面高度：角色脚底和纸片底部都在这个高度
    public final double GROUND_Y = 500;
    private final double SCENE_WIDTH = 800;
    private final double SCENE_HEIGHT = 545;

    //生命值和gameOver，TODO：gameOver待更新
    private javafx.scene.text.Text healthText;
    private javafx.scene.text.Text gameOverText;

    public GameView() {
        // 1. 添加背景图 (最底层)
        try {
            Image backgroundImg = new Image(getClass().getResourceAsStream("/com/paperanne/paperanne/images/background.png"));
            ImageView bgView = new ImageView(backgroundImg);
            bgView.setFitWidth(SCENE_WIDTH);
            bgView.setFitHeight(SCENE_HEIGHT);
            root.getChildren().add(bgView);
        } catch (Exception e) {
            // 背景加载失败的备选方案
            root.getChildren().add(new Rectangle(SCENE_WIDTH, SCENE_HEIGHT, Color.LIGHTBLUE));
        }

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
        double[][] positions = {{200, 450, 150, 50}, {450, 450, 100, 50}, {100, 450, 80, 50}};
        for (double[] p : positions) {
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
        try {
            Image cakeImg = new Image(getClass().getResourceAsStream("/com/paperanne/paperanne/images/cake.png"));
            cakeView = new ImageView(cakeImg);
            cakeView.setFitWidth(20);  // 角色宽度的一半 (40/2)
            cakeView.setFitHeight(30); // 角色高度的一半 (60/2)
            cakeView.setX(600);        // 设置一个位置
            cakeView.setY(GROUND_Y - 30);
            root.getChildren().add(cakeView);
        } catch (Exception e) {
            System.out.println("蛋糕图片加载失败");
        }

        // 添加药水
        try {
            Image potionImg = new Image(getClass().getResourceAsStream("/com/paperanne/paperanne/images/potion.png"));
            potionView = new ImageView(potionImg);
            potionView.setFitWidth(20);  // 角色宽度的一半
            potionView.setFitHeight(30); // 角色高度的一半
            potionView.setX(400);        // 设置药水位置
            potionView.setY(GROUND_Y - 30);
            root.getChildren().add(potionView);
        } catch (Exception e) {
            System.out.println("药水图片加载失败");
        }

        //添加传送门
        try {
            Image portalImg = new Image(getClass().getResourceAsStream("/com/paperanne/paperanne/images/portal.png"));
            portalA = new ImageView(portalImg);
            portalA.setFitWidth(50);
            portalA.setFitHeight(80);
            portalA.setX(50);  // 传送门A的位置
            portalA.setY(GROUND_Y - 80);

            portalB = new ImageView(portalImg);
            portalB.setFitWidth(50);
            portalB.setFitHeight(80);
            portalB.setX(680); // 传送门B的位置
            portalB.setY(GROUND_Y - 80);

            root.getChildren().addAll(portalA, portalB);
        } catch (Exception e) {
            System.out.println("传送门图片加载失败");
        }

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

        // 添加门
        try {
            doorClosedImg = new Image(getClass().getResourceAsStream("/com/paperanne/paperanne/images/door_closed.png"));
            doorOpenImg = new Image(getClass().getResourceAsStream("/com/paperanne/paperanne/images/door_open.png"));

            doorView = new ImageView(doorClosedImg);
            doorView.setFitWidth(60);
            doorView.setFitHeight(80);
            doorView.setX(720); // 设置门的位置
            doorView.setY(GROUND_Y - 80);

            root.getChildren().add(doorView);
        } catch (Exception e) {
            System.out.println("门图片加载失败");
        }

        //添加拉杆
        try {
            leverLeftImg = new Image(getClass().getResourceAsStream("/com/paperanne/paperanne/images/lever_left.png"));
            leverRightImg = new Image(getClass().getResourceAsStream("/com/paperanne/paperanne/images/lever_right.png"));

            leverView = new ImageView(leverLeftImg);
            leverView.setFitWidth(40);
            leverView.setFitHeight(40);
            leverView.setX(200); // 根据需要设置位置
            leverView.setY(GROUND_Y - 40);

            root.getChildren().add(leverView);
        } catch (Exception e) {
            System.out.println("拉杆图片加载失败");
        }

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

}

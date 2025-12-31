package com.paperanne.view;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import java.io.InputStream;

public class ResultOverlay extends StackPane {

    public ResultOverlay(boolean isSuccess, int starCount, Runnable restartHandler, Runnable menuHandler) {
        this.setPrefSize(800, 545);
        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        try {
            String basePath = "/com/paperanne/images/";
            VBox content = new VBox(25); // 间距调大一点
            content.setAlignment(Pos.CENTER);

            // 1. 加载 成功/失败 的标题图
            String titleImgName = isSuccess ? "you_win.png" : "you_lose.png";
            InputStream titleStream = getClass().getResourceAsStream(basePath + titleImgName);
            if (titleStream != null) {
                ImageView titleView = new ImageView(new Image(titleStream));
                titleView.setFitWidth(350);
                titleView.setPreserveRatio(true);
                content.getChildren().add(titleView);
            }

            // 2. 【核心新增】加载星数图片
            // 图片名为 star_0.png, star_1.png 等
            String starImgName = "star_" + starCount + ".png";
            InputStream starStream = getClass().getResourceAsStream(basePath + starImgName);
            if (starStream != null) {
                ImageView starView = new ImageView(new Image(starStream));
                starView.setFitWidth(200); // 星星显示稍小一些
                starView.setPreserveRatio(true);
                content.getChildren().add(starView);
            } else {
                // 如果没图，显示文字保底
                javafx.scene.text.Text starText = new javafx.scene.text.Text("Rating: " + starCount + " Stars");
                starText.setStyle("-fx-fill: gold; -fx-font-size: 30; -fx-font-weight: bold;");
                content.getChildren().add(starText);
            }

            // 3. 加载按钮
            Button btnRestart = createImgButton(basePath + "restart.png", "RESTART", restartHandler);
            Button btnMenu = createImgButton(basePath + "menu.png", "MENU", menuHandler);

            HBox buttonBox = new HBox(40, btnRestart, btnMenu);
            buttonBox.setAlignment(Pos.CENTER);
            content.getChildren().add(buttonBox);

            this.getChildren().add(content);
            this.setOpacity(0);
            playFadeIn();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Button createImgButton(String path, String altText, Runnable action) {
        Button btn = new Button();
        InputStream is = getClass().getResourceAsStream(path);

        if (is != null) {
            // 如果图片存在，显示图片
            ImageView icon = new ImageView(new Image(is));
            btn.setGraphic(icon);
            btn.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-cursor: hand;");
        } else {
            // 【重要】如果图片路径依然不对，这里会显示文字而不是让程序崩溃！
            System.err.println("错误：找不到图片资源 " + path + "，已切换为文字模式");
            btn.setText(altText);
            btn.setStyle("-fx-font-size: 20px; -fx-min-width: 100px;");
        }

        btn.setOnAction(e -> action.run());
        return btn;
    }

    private void playFadeIn() {
        FadeTransition ft = new FadeTransition(Duration.millis(1000), this);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }
}
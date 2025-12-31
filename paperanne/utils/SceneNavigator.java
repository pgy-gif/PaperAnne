package com.paperanne.utils; // 建议放在 utils 包下

import com.paperanne.controller.GameController_Abstract;
import com.paperanne.controller.Level4Controller;
import com.paperanne.view.GameView;
import com.paperanne.view.Level4View;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SceneNavigator {

    // 跳转回菜单
    public static void toMenu(Stage stage) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    SceneNavigator.class.getResource("/com/paperanne/menu.fxml")
            );

            // 1. 加载新的布局
            Parent root = loader.load();

            // 2. 创建 Scene 并指定固定的游戏尺寸
            // 这里的 800, 600 应该和你代码关卡的尺寸保持一致
            Scene menuScene = new Scene(root, 800, 600);

            // 3. 应用到 Stage
            stage.setScene(menuScene);

            // 4. 【关键步骤】强制 Stage 调整大小并居中
            stage.setWidth(800);
            stage.setHeight(600);
            stage.setResizable(false); // 通常游戏菜单不建议手动拉伸
            stage.centerOnScreen();    // 让窗口回到屏幕中央

            stage.show();
        } catch (Exception e) {
            System.err.println("加载菜单失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
/*
    // 重开/跳转到指定关卡
    public static void toGameLevel(Stage stage, int levelId) {
        try {
            // 假设你的游戏界面是 GameView.fxml
            FXMLLoader loader = new FXMLLoader(SceneNavigator.class.getResource("/view/GameView.fxml"));
            Parent root = loader.load();

            // 如果你的 GameView 对应的 Controller 需要传参，可以在这里获取 controller 设置
            // GameController controller = loader.getController();
            // controller.initLevel(levelId);

            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("错误：无法加载关卡 " + levelId);
        }
    }

  */
    /**
     * 统一关卡跳转入口
     * 修改点：添加了对 levelId == 4 的支持，并统一了 Scene 的创建逻辑
     */
    public static void toGameLevel(Stage stage, int levelId) {
        GameView view;
        // 1. 初始化为 null，防止“无法解析”或“未初始化”错误
        GameController_Abstract controller = null;

        if (levelId == 4) {
            view = new Level4View();
            // 2. 赋值给上面定义的变量
            controller = new Level4Controller((Level4View) view);
        } else {
            view = new GameView();
            // 如果有其他关卡的 Controller，可以在这里赋值
            // if (levelId == 1) controller = new Level1Controller(view, 1);
        }

        Parent root = view.getRootPane();

        // 强行解绑逻辑（保持不变）
        if (root.getScene() != null) {
            root.getScene().setRoot(new javafx.scene.layout.Pane());
        }

        if (stage.getScene() == null) {
            stage.setScene(new Scene(root, 800, 540));
        } else {
            stage.getScene().setRoot(root);
        }

        // 3. 只有当 controller 不为空时，才执行监听绑定
        if (controller != null) {
            controller.setupInputListeners(stage.getScene());
        }

        stage.show();
        root.requestFocus();
    }

    /**
     * 专门跳转到第四关的快捷方法
     */
    public static void toLevel4(Stage stage) {
        // 直接调用上面的统一入口即可，保证逻辑复用
        toGameLevel(stage, 4);
    }
}
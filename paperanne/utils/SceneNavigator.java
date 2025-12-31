package com.paperanne.utils; // 建议放在 utils 包下

import com.paperanne.controller.GameController;
import com.paperanne.view.GameView;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;

public class SceneNavigator {

    // 跳转回菜单
    public static void toMenu(Stage stage) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    SceneNavigator.class.getResource("/com/paperanne/paperanne/menu.fxml")
            );

            // 1. 加载新的布局
            Parent root = loader.load();

            // 2. 创建 Scene 并指定固定的游戏尺寸
            // 这里的 800, 600 应该和你代码关卡的尺寸保持一致
            Scene menuScene = new Scene(root, 800, 600);

            // 3. 应用到 Stage
            stage.setWidth(800);
            stage.setHeight(600);
            stage.setResizable(false); // 通常游戏菜单不建议手动拉伸
            stage.centerOnScreen();
            stage.setScene(menuScene);

            // 4. 【关键步骤】强制 Stage 调整大小并居中
                // 让窗口回到屏幕中央

            stage.show();
        } catch (Exception e) {
            System.err.println("加载菜单失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 重开/跳转到指定关卡
//    public static void toGameLevel(Stage stage, int levelId) {
//        try {
//            // 假设你的游戏界面是 GameView.fxml
//            FXMLLoader loader = new FXMLLoader(SceneNavigator.class.getResource("/view/GameView.fxml"));
//            Parent root = loader.load();
//
//             //如果你的 GameView 对应的 Controller 需要传参，可以在这里获取 controller 设置
//             GameController controller = loader.getController();
//             controller.initLevel(levelId);
//
//            stage.setScene(new Scene(root));
//            stage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.err.println("错误：无法加载关卡 " + levelId);
//        }



    public static void toGameLevel(Stage stage, int levelId) {
        // 1. 创建视图实例 (纯代码编写的 View)
        GameView gameView = new GameView();

        // 2. 根据 levelId 创建对应的 Controller
        // 假设你的 Controller 构造函数接收 View 和 levelId
        // 这里以 Level1Controller 为例，或者你可以根据 ID switch 出不同的 Controller
        if (levelId == 1) {
            new GameController(gameView);
        } else if (levelId == 2) {
            // new Level2Controller(gameView, 2);
        }

        // 3. 将 View 放入 Scene 并展示
        // 注意：gameView 通常应该继承自 Parent（如 Pane, StackPane 等）
        Panel panel = new Panel();
        Scene gameScene = new Scene(gameView.getRootPane(), 800, 600);
        stage.setScene(gameScene);
        stage.show();

        System.out.println("成功进入代码构建的关卡: " + levelId);
    }

}
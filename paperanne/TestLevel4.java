package com.paperanne;

import com.paperanne.utils.SceneNavigator;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class TestLevel4 extends Application {

    @Override
    public void start(Stage stage) {

        SceneNavigator.toLevel4(stage);

        stage.setTitle("Level 4 物理结构测试");
        stage.show();
    }
}
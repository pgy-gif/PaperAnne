package com.paperanne;

import com.paperanne.controller.GameController;
import com.paperanne.model.GameModel;
import com.paperanne.model.Key;
import com.paperanne.view.GameView;
import javafx.application.Application;
import javafx.stage.Stage;

public class PaperAnne extends Application {

    @Override
    public void start(Stage stage) {
        GameController.updateLevel(0);
        /*此处应该移到关卡内部
        Key.initLevelItems();

         */
        GameView view = new GameView();
        new GameController(view,1);

        stage.setTitle("Paper Anne");
        stage.setScene(view.getScene());
        stage.setResizable(false);
        stage.show();
    }

}

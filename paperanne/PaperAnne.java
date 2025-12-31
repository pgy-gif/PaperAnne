package com.paperanne;

import com.paperanne.controller.GameController;
//import com.paperanne.model.GameModel;
import com.paperanne.model.Key;
import com.paperanne.view.GameView;
import javafx.application.Application;
import javafx.stage.Stage;

public class PaperAnne extends Application {

    @Override
    public void start(Stage stage) {
        GameController.updateLevel(0);
        Key.initLevelItems();
        GameView view = new GameView();
        new GameController(view);

        stage.setTitle("Paper Anne - MVC Demo");
        stage.setScene(view.getScene());
        stage.setResizable(false);
        stage.show();
    }

}

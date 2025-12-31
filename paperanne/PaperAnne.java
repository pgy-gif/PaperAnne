package com.paperanne;

import com.paperanne.controller.GameController;
import com.paperanne.controller.GameController_level3;
import com.paperanne.model.GameModel;
import com.paperanne.model.Key;
import com.paperanne.view.GameView;
import com.paperanne.view.GameView_level3;
import javafx.application.Application;
import javafx.stage.Stage;

public class   PaperAnne extends Application {

    @Override
    public void start(Stage stage) {
        GameController.updateLevel(0);
        Key.initLevelItems();
        //GameView view = new GameView();
        GameView_level3 view = new GameView_level3();

        //new GameController(view);
        new GameController_level3(view); // 第三关


        stage.setTitle("Paper Anne - MVC Demo");
        stage.setScene(view.getScene());
        stage.setResizable(false);
        stage.show();
    }

}

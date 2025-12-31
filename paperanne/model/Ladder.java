package com.paperanne.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class Ladder {
    private ImageView view;
    private double x, y, width, height;

    public Ladder(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        // 加载梯子贴图（确保资源文件夹中有 ladder.png）
        Image img = new Image(getClass().getResourceAsStream("/com/paperanne/images/ladder_middle.png"));
        view = new ImageView(img);
        view.setX(x);
        view.setY(y);
        view.setFitWidth(width);
        view.setFitHeight(height);
    }

    public void addToPane(Pane pane) {
        pane.getChildren().add(view);
        //view.toBack(); // 梯子应该在玩家后面,但是实际上这个并没有起作用，反而让梯子不可见
    }

    public ImageView getView() { return view; }
    public double getX() { return x; }
    public double getY() { return y; }
}
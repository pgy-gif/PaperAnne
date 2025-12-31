package com.paperanne.model;

import com.paperanne.controller.MechanismAction;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class GameLever {
    private ImageView view;
    private Image leftImg, rightImg;
    private MechanismAction action;
    private boolean isRight = false;

    public GameLever(ImageView view, Image left, Image right, MechanismAction action) {
        this.view = view;
        this.leftImg = left;
        this.rightImg = right;
        this.action = action;
    }

    public void toggle() {
        isRight = !isRight;
        view.setImage(isRight ? rightImg : leftImg);
        if (action != null) action.execute(isRight);
    }

    public ImageView getView() { return view; }
}
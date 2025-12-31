package com.paperanne.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Achievement {

    private ImageView img;
    private double x;
    private double y;
    private int width;
    private int height;
    private boolean isPickedUp = false;
    private boolean visible = true;
    private int id;

    public Achievement(String imgURL,double x,double y,int width,int height,int id){
        img = new ImageView(new Image(getClass().getResourceAsStream(imgURL)));
        this.x = x;
        this.y = y;
        this.id = id;
        this.width = width;
        this.height = height;
        img.setFitWidth(width);
        img.setFitHeight(height);
        img.setX(x);
        img.setY(y);
        img.setVisible(visible);
    }
    public ImageView paint(){
        return img;
    }
    public void setPickedUp(){
        isPickedUp = true;
    }
    public boolean isPickedUp() {
        return isPickedUp;
    }
    public void updateAchievement(){
        if(isPickedUp){
            visible = false;
        }
        img.setVisible(visible);
    }
    public int getId(){
        if(isPickedUp){
            return id;
        }
        return 0;
    }
}

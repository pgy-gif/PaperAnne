package com.paperanne.view;

import com.paperanne.model.Achievement;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.List;

public class GameView extends GameView_Abstract {

    public GameView() {
        super();

    }

    @Override
    public void setPositions() {
        positions = new double[][]{{200, 450, 150, 50}, {450, 420, 100, 80}, {100, 450, 80, 50}};
    }
    @Override
    public String setBg(){
        return "/com/paperanne/paperanne/images/background.png";
    }
    @Override
    protected void addAllComponents(){
        try {
            Image cakeImg = new Image(getClass().getResourceAsStream("/com/paperanne/paperanne/images/cake.png"));
            cakeView = new ImageView(cakeImg);
            cakeView.setFitWidth(30);  // 角色宽度的一半 (40/2)
            cakeView.setFitHeight(40); // 角色高度的一半 (60/2)
            cakeView.setX(400);        // 设置一个位置
            cakeView.setY(330);
            root.getChildren().add(cakeView);
        } catch (Exception e) {
            System.out.println("蛋糕图片加载失败");
        }

        // 添加药水
        try {
            Image potionImg = new Image(getClass().getResourceAsStream("/com/paperanne/paperanne/images/potion.png"));
            potionView = new ImageView(potionImg);
            potionView.setFitWidth(30);  // 角色宽度的一半
            potionView.setFitHeight(40); // 角色高度的一半
            potionView.setX(600);        // 设置药水位置
            potionView.setY(GROUND_Y - 30);
            root.getChildren().add(potionView);
        } catch (Exception e) {
            System.out.println("药水图片加载失败");
        }

        //添加传送门
        try {
            Image portalImg = new Image(getClass().getResourceAsStream("/com/paperanne/paperanne/images/portal.png"));
            portalA = new ImageView(portalImg);
            portalA.setFitWidth(50);
            portalA.setFitHeight(80);
            portalA.setX(50);  // 传送门A的位置
            portalA.setY(GROUND_Y - 80);

            portalB = new ImageView(portalImg);
            portalB.setFitWidth(50);
            portalB.setFitHeight(80);
            portalB.setX(680); // 传送门B的位置
            portalB.setY(GROUND_Y - 80);

            root.getChildren().addAll(portalA, portalB);
        } catch (Exception e) {
            System.out.println("传送门图片加载失败");
        }

        // 添加门
        try {
            doorClosedImg = new Image(getClass().getResourceAsStream("/com/paperanne/paperanne/images/door_closed.png"));
            doorOpenImg = new Image(getClass().getResourceAsStream("/com/paperanne/paperanne/images/door_open.png"));

            doorView = new ImageView(doorClosedImg);
            doorView.setFitWidth(60);
            doorView.setFitHeight(80);
            doorView.setX(720); // 设置门的位置
            doorView.setY(GROUND_Y - 80);

            root.getChildren().add(doorView);
        } catch (Exception e) {
            System.out.println("门图片加载失败");
        }

        //添加拉杆
        try {
            leverLeftImg = new Image(getClass().getResourceAsStream("/com/paperanne/paperanne/images/lever_left.png"));
            leverRightImg = new Image(getClass().getResourceAsStream("/com/paperanne/paperanne/images/lever_right.png"));

            leverView = new ImageView(leverLeftImg);
            leverView.setFitWidth(40);
            leverView.setFitHeight(40);
            leverView.setX(200); // 根据需要设置位置
            leverView.setY(GROUND_Y - 40);

            root.getChildren().add(leverView);
        } catch (Exception e) {
            System.out.println("拉杆图片加载失败");
        }
        achievementList = new ArrayList<>();
        try{

            achievementList.add(new Achievement("/com/paperanne/images/gem_red.png",400,460,20,18,1));
            achievementList.add(new Achievement("/com/paperanne/images/gem_yellow.png",700,350,20,18,2));
            for(Achievement a : achievementList){
                root.getChildren().add(a.paint());
            }
        }catch(Exception e){
            System.out.println("碎片图片加载失败");
        }
    }


}
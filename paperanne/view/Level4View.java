package com.paperanne.view;

import com.paperanne.controller.HeightChangeAction;
import com.paperanne.controller.MechanismAction;
import com.paperanne.controller.WallRemoveAction;
import com.paperanne.model.Duck;
import com.paperanne.model.GameLever;
import com.paperanne.model.Key;
import com.paperanne.model.Ladder;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.ArrayList;
import java.util.List;

public class Level4View extends GameView {

    // 定义三层的高度参考（假设数值）
    public final double LAYER_1_Y = 500; // 地面
    public final double LAYER_2_Y = 350; // 第二层
    public final double LAYER_3_Y = 150; // 第三层
    private List<Ladder> ladders = new ArrayList<>();
    //可拖拽纸片集合，不可拖拽的放进父类中
    private List<ImageView> draggablePapers = new ArrayList<>();
    //传送门组
    private List<ImageView> portalGroup = new ArrayList<>();
    //丑小鸭
    private Duck duck;

    //墙
    private ImageView wall1;//被拉杆控制的
    private ImageView wall2;
    //电梯（丑小鸭使用）
    private ImageView elevator;
    //栏杆状态
    private boolean isLeverPulled = false;


    public Level4View() {
        super();
        setupLevel4Layout();
        // --- 1. 获取传送门并赋值给父类变量 (修复交互的核心) ---
        List<ImageView> portals = this.getPortalGroup();
        if (portals.size() >= 2) {
            // 将 view 里的传送门赋值给父类的成员变量 portalA 和 portalB
            // 这样 PlayerController 才能检测到碰撞
            super.setPortalA(portals.get(0));
            super.setPortalB(portals.get(1));
        }
    }

    private void setupLevel4Layout() {

        setupKey();
        // 2. 添加第二层的纸片
        ImageView platform2 = new ImageView(new Image(getClass().getResourceAsStream("/com/paperanne/images/ground_up.png")));
        platform2.setX(0);
        platform2.setY(LAYER_2_Y);
        platform2.setFitWidth(800);
        platform2.setFitHeight(30);
        getPaperViews().add(platform2);
        getRootPane().getChildren().add(platform2);

        // 3. 添加第三层的纸片
        ImageView platform3 = new ImageView(new Image(getClass().getResourceAsStream("/com/paperanne/images/ground_up.png")));
        platform3.setX(0);
        platform3.setY(LAYER_3_Y);
        platform3.setFitWidth(800);
        platform3.setFitHeight(30);
        getPaperViews().add(platform3);
        getRootPane().getChildren().add(platform3);

        // 5. 初始化玩家位置在地面
        getPlayerView().setX(50);
        getPlayerView().setY(LAYER_1_Y - 50);

        addDraggablePaper(100, 180, 200,170,"/com/paperanne/images/paper_yellow1.png"); // 在第一层和第二层之间加个小踏板
        addDraggablePaper(500, 380, 170,120,"/com/paperanne/images/paper_yellow2.png");

        // 创建连接第二层(350)到第三层(150)的梯子
        Ladder ladder2 = new Ladder(550, 150, 70, 200);
        ladders.add(ladder2);
        ladder2.addToPane(getRootPane());



        //终点门
        setupSuccessDoor();
        getSuccessDoorView().setX(650);
        getSuccessDoorView().setY(LAYER_3_Y - 80); // 减去门的高度
        getSuccessDoorView().setVisible(true);


        //传送门
        setupPortals();
        //鸭子
        setupDuck();
        //墙
        setupWalls();
        //电梯
        setupElevator();
        //拉杆
        setupLever();

    }

    private void addDraggablePaper(double x, double y, double width,double height,String path) {
        // 加载纸片纹理
        Image img = new Image(getClass().getResourceAsStream(path));
        ImageView paperView = new ImageView(img);
        paperView.setX(x);
        paperView.setY(y);
        paperView.setFitWidth(width);
        paperView.setFitHeight(height);

        // 加入 getPaperViews() 列表，实现安妮站在上面
        getPaperViews().add(paperView);

        // 加入专门的拖拽列表
        draggablePapers.add(paperView);

        getRootPane().getChildren().add(paperView);
    }

    private void setupPortals() {
        Image portalImg = new Image(getClass().getResourceAsStream("/com/paperanne/images/portal.png"));

        ImageView portalA = new ImageView(portalImg);
        portalA.setX(100);
        portalA.setY(LAYER_2_Y - 80); // 放在第三层
        portalA.setFitWidth(60);
        portalA.setFitHeight(80);

        ImageView portalB = new ImageView(portalImg);
        portalB.setX(600);
        portalB.setY(LAYER_1_Y - 80); // 放在地面
        portalB.setFitWidth(60);
        portalB.setFitHeight(80);

        portalGroup.add(portalA);
        portalGroup.add(portalB);


        getRootPane().getChildren().addAll(portalA, portalB);
    }

    public void setupSuccessDoor(){
        try {
            successDoorImg = new Image(getClass().getResourceAsStream("/com/paperanne/images/successDoor.png"));
            successDoorView = new ImageView(successDoorImg);
            successDoorView.setFitWidth(60);
            successDoorView.setFitHeight(80);
            successDoorView.setX(700);
            successDoorView.setY(GROUND_Y - 80);

            root.getChildren().add(successDoorView);
        } catch (Exception e) {
            System.err.println("终点门图片加载失败");
        }
    }

    private void setupWalls() {
        Image wallImg = new Image(getClass().getResourceAsStream("/com/paperanne/images/wall.png"));

        // --- 墙1：动态墙（会被拉杆控制） ---
        wall1 = new ImageView(wallImg);
        wall1.setX(100);
        wall1.setY(LAYER_2_Y - 80); // 放在第二层上，高度 80
        wall1.setFitWidth(20);
        wall1.setFitHeight(80);
        wall1.setId("SPECIAL_WALL");

        // --- 墙2：静态墙（永远在那，增加解谜难度） ---
        wall2 = new ImageView(wallImg);
        wall2.setX(200);           // 放在另一个位置
        wall2.setY(LAYER_2_Y - 80);
        wall2.setFitWidth(20);
        wall2.setFitHeight(80);
        wall2.setId("SPECIAL_WALL");

        // 统统加入物理列表
        getPaperViews().addAll(List.of(wall1, wall2));
        getRootPane().getChildren().addAll(wall1, wall2);
    }

    private void setupDuck() {
        duck = new Duck(150, LAYER_2_Y - 40); // 初始位置
        duck.addToPane(getRootPane());
    }

    private void setupLever() {
        leverLeftImg = new Image(getClass().getResourceAsStream("/com/paperanne/images/lever_left.png"));
        leverRightImg = new Image(getClass().getResourceAsStream("/com/paperanne/images/lever_right.png"));

        // --- 拉杆 1：控制墙壁 ---
        ImageView lv1 = new ImageView(leverLeftImg);
        lv1.setX(250);
        lv1.setY(LAYER_3_Y - 40);
        lv1.setFitWidth(40);
        lv1.setFitHeight(40);
        MechanismAction wallAction = new WallRemoveAction(wall1);
        levers.add(new GameLever(lv1, leverLeftImg, leverRightImg, wallAction));
        getRootPane().getChildren().add(lv1);

        // --- 拉杆 2：控制电梯  ---
        ImageView lv2 = new ImageView(leverLeftImg);
        lv2.setX(450);            // 放一个不同的位置，例如电梯旁边
        lv2.setY(LAYER_2_Y - 40); // 放在第二层
        lv2.setFitWidth(40);
        lv2.setFitHeight(40);

        // 绑定电梯升降动画 (从 LAYER_2_Y 到 LAYER_3_Y)
        MechanismAction liftAction = new HeightChangeAction(elevator, LAYER_3_Y, LAYER_2_Y);
        levers.add(new GameLever(lv2, leverLeftImg, leverRightImg, liftAction));
        getRootPane().getChildren().add(lv2);
    }

    private void setupElevator() {
        Image plateImg = new Image(getClass().getResourceAsStream("/com/paperanne/images/elevator.png"));
        elevator = new ImageView(plateImg); // 赋值给成员变量
        elevator.setX(0);
        elevator.setY(LAYER_2_Y);
        elevator.setFitWidth(100);
        elevator.setFitHeight(20);

        elevator.setId("ELEVATOR");

        getPaperViews().add(elevator);
        getRootPane().getChildren().add(elevator);
    }

    public void setupKey(){
        try {
            Key.initLevelItems();

            Image keyImage = new Image(getClass().getResourceAsStream("/com/paperanne/images/key_yellow.png"));
            keyUI = new ImageView(keyImage);
            keyUI.setFitWidth(40);
            keyUI.setFitHeight(40);

            // 固定在右下角 (Scene 宽度 800, 高度 545)
            keyUI.setX(740);
            keyUI.setY(500);

            keyUI.setOpacity(0.7);

            keyUI.setVisible(false); // 初始状态不可见
            root.getChildren().add(keyUI);
        } catch (Exception e) {
            System.out.println("UI钥匙图标加载失败");
        }
    }


    // 供 Controller 调用
    public List<ImageView> getPortalGroup() {
        return portalGroup;
    }

    public List<ImageView> getDraggablePapers() {
        return draggablePapers;
    }

    public List<Ladder> getLadders() { return ladders; }

    public Duck getDuck() { return duck; }
}
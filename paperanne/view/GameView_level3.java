package com.paperanne.view;

import com.paperanne.controller.PaperController;
import com.paperanne.model.Key;
import com.paperanne.model.PaperModel;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class GameView_level3 extends GameView {
    // 仅保留引用，不在此处写联动监听
    private List<ImageView> purplePapers = new ArrayList<>();
    private List<ImageView> bluePapers = new ArrayList<>();
    private ImageView reversiblePaper;

    public GameView_level3() {
        super();
        // 1. 清理默认
        List<ImageView> defaultPapers = getPaperViews();
        this.getRootPane().getChildren().removeAll(defaultPapers);
        defaultPapers.clear();

        // 2. 添加纸片 (tag 0:紫, 1:蓝)
        addCustomPaper(300, 60, "/com/paperanne/paperanne/images/blue_high.png", 1);
        //addCustomPaper(120, 120, "/com/paperanne/paperanne/images/blue_low.png", 1);
        addCustomPaper(60, 60, "/com/paperanne/paperanne/images/blue.png", 1);
        addCustomPaper(240, 60, "/com/paperanne/paperanne/images/purple_high.png", 0);
        addCustomPaper(360, 60, "/com/paperanne/paperanne/images/purple.png", 0);

        addCustomPaper(0, 60, "/com/paperanne/paperanne/images/purple_reversible.png", 0);
        //addCustomPaper(60, 60, "/com/paperanne/paperanne/images/blue.png", 1);
        //addCustomPaper(0, 60, "/com/paperanne/paperanne/images/purple_reversible_fake.jpg", 0);


        // 重新设置传送门 B 的位置
        if (getPortalB() != null) {
            getPortalB().setX(110);
            getPortalB().setY(50);
        }



        //可翻转纸片
        this.reversiblePaper = purplePapers.get(2);

        // 3. 定位交互提示
        new AnimationTimer() {
            @Override public void handle(long now) { checkReversibleInteraction(); }
        }.start();

        bindSceneListeners();
    }

    public void addCustomPaper(double x, double targetWidth, String imagePath, int tag) {
        Image img = new Image(getClass().getResource(imagePath).toExternalForm());
        double targetHeight = targetWidth * (img.getHeight() / img.getWidth());

        ImageView pv = new ImageView(img);
        pv.setX(x);
        pv.setY(500 - targetHeight);
        pv.setFitWidth(targetWidth);
        pv.setFitHeight(targetHeight);

    // 注意：此处先不存 Tag 到 UserData，由 Controller统一存入 Model
        //if (!imagePath.contains("fake"))
            getPaperViews().add(pv);
        getRootPane().getChildren().add(pv);
        if (tag == 0) purplePapers.add(pv); else bluePapers.add(pv);
        if (imagePath.contains("fake")){
            pv.setVisible(false);
            purplePapers.add(pv);

        }
        System.out.println("add paper size" + getPaperViews().size());
    }

    public void addCloud(double x, double y, double width, double height,PaperController paperCtrl,boolean isDraggable) {
        Image img = new Image(getClass().getResource("/com/paperanne/paperanne/images/cloud.png").toExternalForm());
        double targetHeight = width * (img.getHeight() / img.getWidth());

        ImageView cloudView = new ImageView(img);
        cloudView.setX(x);
        cloudView.setY(y);
        cloudView.setFitWidth(width);
        //cloudView.setFitHeight(targetHeight);
        cloudView.setFitHeight(height);
        cloudView.setOpacity(0.8);


        // 为云朵创建独立的 Model
        PaperModel cloudModel = new PaperModel(x, y, width, targetHeight);
        cloudView.setUserData(cloudModel);

        // 关键：手动绑定基础拖拽，但不加入任何 List
        if (isDraggable) paperCtrl.makeDraggable(cloudView, cloudModel);
        //paperCtrl.makeDraggable(cloudView, cloudModel);

        // 只添加到根面板，不加入 paperViews 列表
        getRootPane().getChildren().add(cloudView);
        getPaperViews().add(cloudView);
        cloudView.toFront();
    }

    // 切换纸片状态（仅负责图片和列表维护）
    public void tryTogglePaper() {
        // 原逻辑：只要提示可见就触发 (导致拉杆时也触发)
        // if (!getInteractTip().isVisible()) return;

        // 新逻辑：增加物理距离判断，确保玩家真的在那张纸片旁边
        ImageView playerV = getPlayerView();
        if (playerV == null || reversiblePaper == null) return;

        // 只有当玩家和这张特殊纸片真正接触时，才允许切换
        if (playerV.getBoundsInParent().intersects(reversiblePaper.getBoundsInParent())) {
            if (purplePapers.contains(reversiblePaper)) {
                purplePapers.remove(reversiblePaper);
                bluePapers.add(reversiblePaper);
                reversiblePaper.setImage(new Image(getClass().getResourceAsStream("/com/paperanne/paperanne/images/blue_reversible.png")));
            } else {
                bluePapers.remove(reversiblePaper);
                purplePapers.add(reversiblePaper);
                reversiblePaper.setImage(new Image(getClass().getResourceAsStream("/com/paperanne/paperanne/images/purple_reversible.png")));
            }
        }
    }

    private void checkReversibleInteraction() {
        ImageView playerV = getPlayerView();
        if (playerV == null || reversiblePaper == null) return;

        // 1. 获取当前的提示对象
        Text tip = getInteractTip();

        // 2. 判定玩家是否靠近了那张特殊纸片
        boolean nearReversible = playerV.getBoundsInParent().intersects(reversiblePaper.getBoundsInParent());

        if (nearReversible) {
            // 如果靠近了特殊纸片，强制显示并更新文字
            tip.setVisible(true);
            tip.setText("按 E 交互");
            // 这里你可以选择让提示跟随纸片，或者跟随玩家
            tip.setX(playerV.getX());
            tip.setY(playerV.getY() - 20);
            tip.toFront();
        }
        // 注意：这里删除了 else { tip.setVisible(false); }
        // 这样当玩家离开特殊纸片时，它不会强制关闭 tip。
        // tip 的隐藏将由 PlayerController 里的逻辑（没碰到蛋糕/拉杆）来完成。
    }
    private void bindSceneListeners() {
        if (getRootPane().getScene() != null) setupKeyListeners(getRootPane().getScene());
        else getRootPane().sceneProperty().addListener((o, oldS, newS) -> { if(newS != null) setupKeyListeners(newS); });
    }

    private void setupKeyListeners(Scene scene) {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, e -> { if (e.getCode() == KeyCode.E) tryTogglePaper(); });
    }

    // 提供给 Controller 使用的 Getter
    public List<ImageView> getPurplePapers() { return purplePapers; }
    public List<ImageView> getBluePapers() { return bluePapers; }

    public ImageView getReversiblePaper() {
        return reversiblePaper;
    }
}
//package com.paperanne.view;
//
//import com.paperanne.model.PaperModel;
//import javafx.animation.AnimationTimer;
//import javafx.beans.value.ChangeListener;
//import javafx.beans.value.ObservableValue;
//import javafx.scene.Scene;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.input.KeyCode;
//import javafx.scene.input.KeyEvent;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class GameView_level3 extends GameView {
//    // 分别存储紫色(tag 0)和蓝色(tag 1)的纸片视图
//    private List<ImageView> purplePapers = new ArrayList<>();
//    private List<ImageView> bluePapers = new ArrayList<>();
//
//    private ImageView reversiblePaper; // 可翻转纸片
//
//    private boolean isSyncing = false; // 防止死循环的锁
//    public GameView_level3() {
//        super();
//
//        // 1. 清理父类默认生成的纸片
//        List<ImageView> defaultPapers = getPaperViews();
//        this.getRootPane().getChildren().removeAll(defaultPapers);
//        defaultPapers.clear();
//
//        // 2. 添加纸片并分类 (0: 紫色, 1: 蓝色)
//        // 蓝色组
//        addCustomPaper(300, 60, "/com/paperanne/paperanne/images/blue_high.png", 1);
//        addCustomPaper(120, 120, "/com/paperanne/paperanne/images/blue_low.png", 1);
//        addCustomPaper(60, 60, "/com/paperanne/paperanne/images/blue.png", 1);
//
//        // 紫色组
//        addCustomPaper(240, 60, "/com/paperanne/paperanne/images/purple_high.png", 0);
//        addCustomPaper(360, 60, "/com/paperanne/paperanne/images/purple.png", 0);
//        addCustomPaper(0, 60, "/com/paperanne/paperanne/images/purple_reversible.png", 0);
//
//        // 3. 执行组内绑定逻辑
////        bindGroup(purplePapers);
////        bindGroup(bluePapers);
//
////        // 4. 设置其他道具位置（保持原有逻辑）
////        setupOtherElements();
//
////        // 1. 创建主纸片 (锚点)
////        addCustomPaper(200, 450,  "/com/paperanne/paperanne/images/paper_red.png");
////        ImageView master = getPaperViews().get(0);
////
////// 2. 创建从属纸片
////        addCustomPaper(300, 450,  "/com/paperanne/paperanne/images/paper_red.png");
////        ImageView follower = getPaperViews().get(1);
////
////// 3. 计算并绑定相对位置 (偏移量 = 300 - 200 = 100)
////        double offsetX = follower.getX() - master.getX();
////        double offsetY = follower.getY() - master.getY();
////
////// 执行绑定：从属坐标 = 主坐标 + 偏移量
////        follower.xProperty().bind(master.xProperty().add(offsetX));
////        follower.yProperty().bind(master.yProperty().add(offsetY));
////
////        // 1. 获取当前所有的纸片视图
////        List<ImageView> defaultPapers = getPaperViews();
////
////        // 2. 从 root 布局中移除这些视图，防止它们留在屏幕上
////        this.getRootPane().getChildren().removeAll(defaultPapers);
////
////        // 3. 清空列表数据，防止后续逻辑（如碰撞检测）还会遍历到它们
////        defaultPapers.clear();
////
////        // 设置第三关独有的地形
////        // 添加纸片
////        addCustomPaper(300, 60, "/com/paperanne/paperanne/images/blue_high.png");
////        addCustomPaper(120, 120, "/com/paperanne/paperanne/images/blue_low.png");
////        addCustomPaper(60,60,"/com/paperanne/paperanne/images/blue.png");
////        addCustomPaper(240, 60, "/com/paperanne/paperanne/images/purple_high.png");
////        addCustomPaper(360, 60, "/com/paperanne/paperanne/images/purple.png");
////        addCustomPaper(0, 60, "/com/paperanne/paperanne/images/purple_reversible.png");
//
//        // 设置第三关的道具位置
//        if (getPotionView() != null) {
//            getPotionView().setX(300);
//            getPotionView().setY(450);
//        }
//        // 改变传送门位置
//        if (getPortalA() != null) {
//            getPortalA().setX(100);
//            getPortalA().setY(GROUND_Y - 80); // 放在地面上
//        }
//        if (getPortalB() != null) {
//            getPortalB().setX(320);
//            getPortalB().setY(getPaperViews().get(3).getY()-getPortalB().getFitHeight()); // 把传送门 B 放在紫色纸片上面
//        }
//
//        // 改变药水位置
//        if (getPotionView() != null) {
//            getPotionView().setX(300);
//            getPotionView().setY(450);
//        }
//
//        // 改变蛋糕位置
//        if (getCakeView() != null) {
//            getCakeView().setX(150);
//            getCakeView().setY(GROUND_Y - 30);
//        }
//
//        // 改变门的位置
//        if (getDoorView() != null) {
//            getDoorView().setX(700);
//            getDoorView().setY(GROUND_Y - 80);
//        }
//
//        // 改变拉杆位置
//        if (getLeverView() != null) {
//            getLeverView().setX(50);
//            getLeverView().setY(GROUND_Y - 40);
//        }
//
////        // 记录这张特殊的纸片（假设它是紫色组的某一张）
//        this.reversiblePaper = purplePapers.get(2);
//
//        // 1. 启动交互检测计时器
//        AnimationTimer interactionTimer = new AnimationTimer() {
//            @Override
//            public void handle(long now) {
//                checkReversibleInteraction();
//            }
//        };
//        interactionTimer.start();
//
//        // 2. 修复 'rootProperty' 和 'getScene()' 的解析问题
//        // 使用 getRootPane() 获取父类中的 root (Pane)
//        // 监听 sceneProperty() 以确保在 View 被放入 Stage 后能拿到 Scene
//        Scene currentScene = getRootPane().getScene();
//        if (currentScene != null) {
//            setupKeyListeners(currentScene);
//            System.out.println("Scene 已存在，直接绑定");
//        } else {
//            getRootPane().sceneProperty().addListener((obs, oldScene, newScene) -> {
//                if (newScene != null) {
//                    setupKeyListeners(newScene);
//                    System.out.println("Scene 动态绑定成功");
//                }
//            });
//        }
//
//        initGlobalLinkage();
//    }
//    private void initGlobalLinkage() {
//        // 遍历所有纸片，给它们挂载“同一套”逻辑
//        for (ImageView paper : getPaperViews()) {
//            paper.xProperty().addListener((obs, oldVal, newVal) -> {
//                if (isSyncing) return; // 锁住，防止循环触发
//
//                // 1. 看看当前动的是谁，它是哪家的？
//                int currentTag = (int) paper.getUserData();
//
//                // 2. 找到它真正的家人（实时根据 Tag 判断，而不是根据 List 判断）
//                List<ImageView> family = (currentTag == 0) ? purplePapers : bluePapers;
//
//                // 3. 开始同步
//                isSyncing = true;
//                double deltaX = newVal.doubleValue() - oldVal.doubleValue();
//                for (ImageView member : family) {
//                    if (member != paper) {
//                        member.setX(member.getX() + deltaX);
//                    }
//                }
//                isSyncing = false;
//            });
//        }
//    }
//
//    private void setupKeyListeners(Scene scene) {
//        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
//            if (event.getCode() == KeyCode.E) {
//                tryTogglePaper();
//                System.out.println("E 按下");
//            }
//        });
//    }
//
//    private void checkReversibleInteraction() {
//        // 使用 getPlayerView() 解决 private 访问权限问题
//        ImageView playerV = getPlayerView();
//        if (playerV == null || reversiblePaper == null) return;
//
//        double pX = playerV.getX() + playerV.getFitWidth() / 2;
//        double rX = reversiblePaper.getX() + reversiblePaper.getFitWidth() / 2;
//
//        // 玩家与纸片的距离判断
//        if (Math.abs(pX - rX) < 60) {
//            getInteractTip().setVisible(true);
//            getInteractTip().setText("按 E 交互");
//            getInteractTip().setX(reversiblePaper.getX());
//            getInteractTip().setY(reversiblePaper.getY() - 20);
//        } else {
//            // 注意：如果还有其他交互物体，这里的隐藏逻辑需要更严谨
//            // getInteractTip().setVisible(false);
//        }
//    }
//
//    private void tryTogglePaper() {
//        if (!getInteractTip().isVisible()) return;
//
//        if (purplePapers.contains(reversiblePaper)) {
//            // 紫转蓝
//            purplePapers.remove(reversiblePaper);
//            reversiblePaper.setUserData(1); // 换掉身份牌，监听器下次运行时会自动认蓝家
//            bluePapers.add(reversiblePaper);
//            reversiblePaper.setImage(new Image(getClass().getResourceAsStream("/com/paperanne/paperanne/images/blue_reversible.png")));
//        } else {
//            // 蓝转紫
//            bluePapers.remove(reversiblePaper);
//            reversiblePaper.setUserData(0);
//            purplePapers.add(reversiblePaper);
//            reversiblePaper.setImage(new Image(getClass().getResourceAsStream("/com/paperanne/paperanne/images/purple_reversible.png")));
//        }
//
//        System.out.println("当前身份 Tag: " + reversiblePaper.getUserData());
//    }
////    private void tryTogglePaper() {
////        if (!getInteractTip().isVisible()) return;
////
////        String purplePath = "/com/paperanne/paperanne/images/purple_reversible.png";
////        String bluePath = "/com/paperanne/paperanne/images/blue_reversible.png";
////
////        if (purplePapers.contains(reversiblePaper)) {
////            purplePapers.remove(reversiblePaper);
////            blueGroupAdd(reversiblePaper, bluePath);
////            System.out.println("纸片交换成功！");
////        } else if (bluePapers.contains(reversiblePaper)) {
////            bluePapers.remove(reversiblePaper);
////            purpleGroupAdd(reversiblePaper, purplePath);
////        }
////    }
//
//    private void blueGroupAdd(ImageView pv, String path) {
//        bluePapers.add(pv);
//        pv.setImage(new Image(getClass().getResourceAsStream(path)));
//    }
//
//    private void purpleGroupAdd(ImageView pv, String path) {
//        purplePapers.add(pv);
//        pv.setImage(new Image(getClass().getResourceAsStream(path)));
//    }
//
//
//
//    /**
//     * 实现组内纸片的全等价联动：拖动任意一张，全组跟随
//     */
//    private void bindGroup(List<ImageView> group) {
//        for (ImageView current : group) {
//            // X 轴联动
//            current.xProperty().addListener((obs, oldVal, newVal) -> {
//                if (isSyncing) return;
//                isSyncing = true;
//                double deltaX = newVal.doubleValue() - oldVal.doubleValue();
//
//                // 关键：动态获取当前纸片所在的组（可能是紫色，也可能是蓝色）
//                List<ImageView> currentGroup = purplePapers.contains(current) ? purplePapers : bluePapers;
//
//                for (ImageView other : currentGroup) {
//                    if (other != current) {
//                        other.setX(other.getX() + deltaX);
//                    }
//                }
//                isSyncing = false;
//            });
//
//        }
//    }
////    private void bindGroup(List<ImageView> group) {
////        if (group.size() <= 1) return;
////
////        for (ImageView current : group) {
////            // 为每一张纸片的 X 属性添加监听
////            current.xProperty().addListener((obs, oldVal, newVal) -> {
////                if (isSyncing) return; // 如果正在同步中，跳过，防止无限递归
////
////                isSyncing = true;
////                double deltaX = newVal.doubleValue() - oldVal.doubleValue();
////
////                for (ImageView other : group) {
////                    if (other != current) {
////                        other.setX(other.getX() + deltaX);
////                    }
////                }
////                isSyncing = false;
////            });
////
////            // 如果需要 Y 轴也同步，同理添加 Y 的监听
////            current.yProperty().addListener((obs, oldVal, newVal) -> {
////                if (isSyncing) return;
////                isSyncing = true;
////                double deltaY = newVal.doubleValue() - oldVal.doubleValue();
////                for (ImageView other : group) {
////                    if (other != current) {
////                        other.setY(other.getY() + deltaY);
////                    }
////                }
////                isSyncing = false;
////            });
////        }
////    }
//
//    /**
//     * 修改后的添加纸片方法，支持 tag 分类
//     * @param tag 0 为紫色，1 为蓝色
//     */
//    public void addCustomPaper(double x, double targetWidth, String imagePath, int tag) {
//        String fullPath = getClass().getResource(imagePath).toExternalForm();
//        Image img = new Image(fullPath);
//        double ratio = img.getHeight() / img.getWidth();
//        double targetHeight = targetWidth * ratio;
//        double targetY = 500 - targetHeight;
//
//        ImageView paperView = new ImageView(img);
//        paperView.setX(x);
//        paperView.setY(targetY);
//        paperView.setFitWidth(targetWidth);
//        paperView.setFitHeight(targetHeight);
//
//        // 使用 UserData 存储当前的 tag (0:紫, 1:蓝)
//        paperView.setUserData(tag);
//
//        // --- 核心修改：在创建时直接绑定动态监听 ---
////        setupDynamicLinkage(paperView);
////
////        getPaperViews().add(paperView);
////        getRootPane().getChildren().add(paperView);
////
////        if (tag == 0) purplePapers.add(paperView);
////        else if (tag == 1) bluePapers.add(paperView);
//        // 添加到总列表（用于碰撞检测等）
//        getPaperViews().add(paperView);
//        // 添加到场景布局
//        getRootPane().getChildren().add(paperView);
//
//        // 根据 tag 分类存放，用于绑定逻辑
//        if (tag == 0) {
//            purplePapers.add(paperView);
//        } else if (tag == 1) {
//            bluePapers.add(paperView);
//        }
//    }
//
//    private void setupSmartLinkage(ImageView current) {
//        current.xProperty().addListener((obs, oldVal, newVal) -> {
//            if (isSyncing) return;
//
//            // 获取该纸片当前的身份标签
//            Object tagObj = current.getUserData();
//            if (tagObj == null) return;
//            int currentTag = (int) tagObj;
//
//            // 根据标签选择对应的同步列表
//            List<ImageView> myCurrentGroup = (currentTag == 0) ? purplePapers : bluePapers;
//
//            isSyncing = true;
//            double deltaX = newVal.doubleValue() - oldVal.doubleValue();
//            for (ImageView other : myCurrentGroup) {
//                if (other != current) {
//                    other.setX(other.getX() + deltaX);
//                }
//            }
//            isSyncing = false;
//        });
//    }
//
//    private void setupDynamicLinkage(ImageView current) {
//        current.xProperty().addListener((obs, oldVal, newVal) -> {
//            if (isSyncing) return;
//            isSyncing = true;
//
//            double deltaX = newVal.doubleValue() - oldVal.doubleValue();
//
//            // 实时获取：我现在在哪个组？
//            List<ImageView> currentGroup = null;
//            if (purplePapers.contains(current)) {
//                currentGroup = purplePapers;
//            } else if (bluePapers.contains(current)) {
//                currentGroup = bluePapers;
//            }
//
//            // 如果找到了所属组，同步组内其他成员
//            if (currentGroup != null) {
//                for (ImageView other : currentGroup) {
//                    if (other != current) {
//                        other.setX(other.getX() + deltaX);
//                    }
//                }
//            }
//
//            isSyncing = false;
//        });
//
//        // Y 轴同理 (如果需要)
//        current.yProperty().addListener((obs, oldVal, newVal) -> {
//            if (isSyncing) return;
//            isSyncing = true;
//            double deltaY = newVal.doubleValue() - oldVal.doubleValue();
//            List<ImageView> currentGroup = purplePapers.contains(current) ? purplePapers :
//                    (bluePapers.contains(current) ? bluePapers : null);
//            if (currentGroup != null) {
//                for (ImageView other : currentGroup) {
//                    if (other != current) {
//                        other.setY(other.getY() + deltaY);
//                    }
//                }
//            }
//            isSyncing = false;
//        });
//    }
//    public List<ImageView> getPurplePapers() { return purplePapers; }
//    public List<ImageView> getBluePapers() { return bluePapers; }
//    // 确保 isSyncing 是 public 或提供 setter，以便控制逻辑
//    public void setSyncing(boolean syncing) { this.isSyncing = syncing; }
////    public void addCustomPaper(double x, double targetWidth, String imagePath) {
////        // 1. 加载图片
////        //Image img = new Image(getClass().getResourceAsStream(imagePath));
////        String fullPath = getClass().getResource(imagePath).toExternalForm();
////        Image img = new Image(fullPath);
////        // 2. 获取原始比例 (Height / Width)
////        double ratio = img.getHeight() / img.getWidth();
////
////        // 3. 根据传入的宽度，计算不缩放的高度
////        double targetHeight = targetWidth * ratio;
////
////        // 4. 计算 Y 坐标（逻辑地面高度 500 - 纸片高度）
////        // 这样无论纸片多高，底边都在同一水平线上
////        double targetY = 500 - targetHeight;
////
////        // 5. 创建视图
////        ImageView paperView = new ImageView(img);
////        paperView.setX(x);
////        paperView.setY(targetY);
////        paperView.setFitWidth(targetWidth);
////        paperView.setFitHeight(targetHeight);
////
////        // 6. 添加到列表和场景
////        getPaperViews().add(paperView);
////        getRootPane().getChildren().add(paperView);
////
////        // 调试信息
////        System.out.println("图片: " + imagePath + " | 自动计算高度: " + targetHeight);
////    }
//
//}

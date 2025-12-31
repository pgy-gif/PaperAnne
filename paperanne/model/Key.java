package com.paperanne.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Blocks 类代表游戏中的可拾取物品（方块）
 */
public class Key {
    private int width = 64;
    private int height = 64;
    private double x;
    private double y;
    private Image image;
    private ImageView img;

    // 用于管理关卡中所有方块的静态列表
    private static List<Key> itemList = new ArrayList<>();

    /**
     * 构造函数：创建一个具体的方块实例
     *
     * @param imagePath 图片在 resources 下的路径，例如 "/images/block_blue_s.png"
     */
    public Key(int width, int height, double x, double y, String imagePath) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;

        try {
            // 使用 getClass().getResource 确保在打包成 JAR 后依然能读取到图片
            this.image = new Image(getClass().getResourceAsStream(imagePath));
            this.img = new ImageView(this.image);
            this.img.setFitWidth(width);
            this.img.setFitHeight(height);
        } catch (Exception e) {
            System.err.println("无法加载图片资源: " + imagePath);
        }
    }

    /**
     * 渲染方法：设置 ImageView 的坐标并返回
     */
    public ImageView painter() {
        if (img != null) {
            img.setX(x);
            img.setY(y);
        }
        return img;
    }

    /**
     * 获取该方块对应的碰撞箱
     */
    public HitBox getHitBox() {
        return new HitBox(width, height, x, y);
    }

    /**
     * 静态方法：初始化当前关卡的所有拾取物
     * 解决了原代码在构造函数中递归调用自己的问题
     */
    public static List<Key> initLevelItems() {
        itemList.clear(); // 切换关卡时先清空之前的

        // 这里的路径对应 src/main/resources/images/ 目录下的文件
        itemList.add(new Key(64, 64, 200, 50, "/com/paperanne/paperanne/images/key_yellow.png"));

        return itemList;
    }

    /**
     * 获取当前存活的物品列表
     */
    public static List<Key> getItemList() {
        return itemList;
    }

}
//package com.paperanne.model;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * 游戏模型类
// */
//public class GameModel {
//    // 角色数据
//    private double playerX = 100, playerY = 300;
//    private final double playerSpeed = 5;
//
//    // 纸片数据（这里简化处理，实际可创建 PaperModel 类）
//    private final List<PaperModel> papers = new ArrayList<>();
//
//    public static class PaperData {
//        public double x, y, width, height, rotation;
//        public String type;
//
//        public PaperData(double x, double y, double w, double h, double r, String type) {
//            this.x = x;
//            this.y = y;
//            this.width = w;
//            this.height = h;
//            this.rotation = r;
//            this.type = type;
//        }
//    }
//
//    public GameModel() {
//        // 初始化初始关卡数据
//        papers.add(new PaperModel(200, 400, 150, 30, 0, "platform"));
//        papers.add(new PaperModel(400, 300, 80, 100, 90, "wall"));
//        papers.add(new PaperModel(500, 350, 120, 20, 0, "bridge"));
//        papers.add(new PaperModel(600, 200, 100, 25, 0, "movingPlatform"));
//    }
//
//    // Getters and Setters
//    public double getPlayerX() {
//        return playerX;
//    }
//
//    public void setPlayerX(double x) {
//        this.playerX = x;
//    }
//
//    public double getPlayerY() {
//        return playerY;
//    }
//
//    public void setPlayerY(double y) {
//        this.playerY = y;
//    }
//
//    public double getPlayerSpeed() {
//        return playerSpeed;
//    }
//
//    public List<PaperModel> getPapers() {
//        return papers;
//    }
//}
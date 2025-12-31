package com.paperanne.model;

// LevelInfo.java - 关卡信息
public class LevelInfo {
    private final int levelId;
    private final String levelName;
    private final String description;
    private final String imagePath;     // 关卡预览图
    private boolean isUnlocked;   // 是否已解锁
    private int bestScore;        // 最高分
    private int stars;           // 获得星数(0-3)

    public LevelInfo(int levelId, String levelName, String description,
                     boolean isUnlocked) {
        this.levelId = levelId;
        this.levelName = levelName;
        this.description = description;
        this.isUnlocked = isUnlocked;
        this.bestScore = 0;
        this.stars = 0;
        this.imagePath = String.format("/images/level%d.png", levelId);
    }

    public void updateProgress(int score, int stars) {
        if (score > this.bestScore) {
            this.bestScore = score;
        }
        // 只有获得更多星时才更新
        if (stars > this.stars) {
            this.stars = stars;
        }
    }

    public void setUnlocked(boolean unlocked) {
        this.isUnlocked = unlocked;
    }

    public String getDescription() {
        return description;
    }

    public String getLevelName() {
        return levelName;
    }

    public int getLevelId() {
        return levelId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public int getStars() {
        return stars;
    }

    public int getBestScore() {
        return bestScore;
    }
    public void setBestScore(int bestScore) {
        this.bestScore = bestScore;
    }
    public void setStars(int stars) {
        this.stars = stars;
    }
}
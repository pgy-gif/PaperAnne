package com.paperanne.model;

import java.util.Arrays;
import java.util.List;

// MenuModel.java - 菜单状态
public class MenuModel {
    private static MenuModel instance;

    private final List<LevelInfo> levels;
    private LevelInfo selectedLevel;

    private MenuModel() {
        // 初始化4个关卡
        levels = Arrays.asList(
                new LevelInfo(1, "爱丽丝篇", "暂无", true),
                new LevelInfo(2, "锡兵篇", "暂无", false),
                new LevelInfo(3, "小红帽篇", "暂无", false),
                new LevelInfo(4, "丑小鸭篇", "暂无！", false)
        );
        selectedLevel = levels.get(0); // 默认选中第一关
    }

    public static MenuModel getInstance() {
        if (instance == null) {
            instance = new MenuModel();
        }
        return instance;
    }

    public List<LevelInfo> getLevels() { return levels; }
    public LevelInfo getSelectedLevel() { return selectedLevel; }
    public void setSelectedLevel(LevelInfo level) {
        this.selectedLevel = level;
    }

    // 解锁关卡

    public void unlockLevel(int levelId) {
        for (LevelInfo level : levels) {
            if (level.getLevelId() == levelId) {
                level.setUnlocked(true);
                System.out.println("成功解锁关卡: " + level.getLevelName());
                break;
            }
        }
    }
}

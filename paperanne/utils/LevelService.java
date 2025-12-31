package com.paperanne.utils;

import com.paperanne.model.MenuModel;

import java.util.HashMap;
import java.util.Map;

// LevelService.java - 关卡服务
public class LevelService {

    public Map<Integer, Integer> loadProgress() {
        // 加载关卡进度
        Map<Integer, Integer> progress = new HashMap<>();
        // 这里可以从文件读取
        return progress;
    }

    // 处理通关后的业务逻辑：保存数据 + 解锁下一关
    public void handleLevelPass(int currentLevelId, int stars) {
        // 1. 解锁下一关 (调用单例 Model)
        int nextLevelId = currentLevelId + 1;
        MenuModel.getInstance().unlockLevel(nextLevelId);

        // 2. 保存进度 (例如保存到本地文件或数据库，这里先打印)
        saveProgress(currentLevelId, stars);
    }

    private void saveProgress(int levelId, int stars) {
        System.out.println("数据持久化：关卡 " + levelId + " 获得星数 " + stars);
        // 这里可以写写入文件的逻辑
    }
}

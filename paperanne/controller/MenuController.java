package com.paperanne.controller;

import com.paperanne.model.LevelInfo;
import com.paperanne.model.MenuModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import java.util.HashMap;
import java.util.Map;

public class MenuController {
    @FXML private Button btnLevel1, btnLevel2, btnLevel3, btnLevel4;

    private MenuModel model;
    private Map<Integer, Button> buttonMap = new HashMap<>();

    @FXML
    public void initialize() {
        // 1. 初始化数据模型
        model = MenuModel.getInstance();

        // 2. 将按钮存入 Map，方便根据 ID 快速查找
        buttonMap.put(1, btnLevel1);
        buttonMap.put(2, btnLevel2);
        buttonMap.put(3, btnLevel3);
        buttonMap.put(4, btnLevel4);

        // 3. 根据 Model 的初始状态刷新 UI
        refreshAllButtons();
    }

    @FXML
    private void handleButtonClick(ActionEvent event) {
        // 获取点击的具体是哪个按钮对象
        Button btn = (Button) event.getSource();

        // 弹窗提示
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("关卡选择");
        alert.setHeaderText(null);
        alert.setContentText("系统确认：正在进入 " + btn.getId());
        alert.showAndWait();
    }
    /*@FXML
    private void handleButtonClick(ActionEvent event) {
        Button sourceBtn = (Button) event.getSource();
        // 从 FXML 的 userData 获取关卡 ID
        String levelIdStr = (String) sourceBtn.getUserData();
        int levelId = Integer.parseInt(levelIdStr);

        System.out.println("准备进入关卡: " + levelId);
        // TODO:跳转到具体的游戏场景
    }
*/
    /**
     * 刷新所有按钮的状态（是否禁用、透明度）
     */
    public void refreshAllButtons() {
        for (LevelInfo info : model.getLevels()) {
            Button btn = buttonMap.get(info.getLevelId());
            if (btn != null) {
                // 如果已解锁，enable 按钮并恢复不透明度；否则禁用并半透明
                boolean unlocked = info.isUnlocked();
                btn.setDisable(!unlocked);
                btn.setOpacity(unlocked ? 1.0 : 0.5);
            }
        }
    }

    public void handleGameResult(int completedLevelId, int starsEarned) {
        // 1. 在 Controller 层执行业务规则判断
        if (starsEarned >= 1) {
            System.out.println("判定通过：获得 " + starsEarned + " 星，准许解锁下一关");
            // 2. 调用 handleLevelUnlocked 解锁
            handleLevelUnlocked(completedLevelId);
        } else {
            System.out.println("判定失败：星数不足，无法解锁下一关");
        }
    }
    /**
     * 处理关卡解锁的入口
     * 当游戏结算（一星通关）后，调用此方法
     */
    public void handleLevelUnlocked(int completedLevelId) {
        int nextLevelId = completedLevelId + 1;

        // 修改 Model 中的数据
        model.unlockLevel(nextLevelId);

        // 刷新 UI
        refreshAllButtons();
    }

    // MenuController.java 中的刷新方法示例
    private void updateButtonUI(int levelId, boolean unlocked) {
        Button targetButton = null;
        // 根据 ID 匹配按钮
        switch(levelId) {
            case 1: targetButton = btnLevel1; break;
            case 2: targetButton = btnLevel2; break;
            case 3: targetButton = btnLevel3; break;
            case 4: targetButton = btnLevel4; break;
        }

        if (targetButton != null && unlocked) {
            targetButton.setDisable(false); // 允许点击
            targetButton.setOpacity(1.0);   // 恢复亮度
        }
    }


}
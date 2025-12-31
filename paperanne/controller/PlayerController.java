package com.paperanne.controller;

import com.paperanne.model.PaperModel;
import com.paperanne.model.PlayerModel;
import com.paperanne.view.GameView_level3;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Set;

public class PlayerController {
    private final double SCREEN_WIDTH = 800; // 对应 Scene 的宽度

    boolean isJumpPressed = false;  // 跳跃按键锁状态
    private boolean canTeleport = true; // 是否可以传送门
    private boolean isEPressed = false; // 按键E锁状态



    public void update(PlayerModel m, ImageView v, Set<KeyCode> keys, double groundY, List<ImageView> papers,
                       ImageView cake, Text tip, ImageView potion,
                       ImageView portalA, ImageView portalB,
                       ImageView door, Image doorOpenImg, ImageView keyUI,
                       ImageView lever, Image leverLeft, Image leverRight,
                       PaperModel targetPaper) {

        boolean showTip = false; // 是否显示提示文字
        boolean currentEKey = keys.contains(KeyCode.E);// 检测本帧 E 键是否被按下

        // 左右移动
        if (keys.contains(KeyCode.A) || keys.contains(KeyCode.LEFT)) {
            m.x -= m.speed;
            v.setScaleX(1);
        }
        if (keys.contains(KeyCode.D) || keys.contains(KeyCode.RIGHT)) {
            m.x += m.speed;
            v.setScaleX(-1);
        }
        // 跳跃逻辑
        boolean currentJumpKey = keys.contains(KeyCode.W) || keys.contains(KeyCode.UP) || keys.contains(KeyCode.SPACE);

        // 只有当：按下了跳跃键 AND 之前没按着(锁是开的) AND 角色不在跳跃中
        if (currentJumpKey && !isJumpPressed && !m.isJumping) {
            m.velY = m.jumpForce;
            m.isJumping = true;
            isJumpPressed = true; // 触发跳跃后，立刻上锁
        }

        // 当玩家松开跳跃键时，重置锁，允许下次跳跃
        if (!currentJumpKey) {
            isJumpPressed = false;
        }

        // --- 传送门/屏幕环绕逻辑开始 ---
        // 如果角色完全走出右边缘 (x > 800)
        if (m.x > SCREEN_WIDTH) {
            m.x = -v.getFitWidth(); // 从左边滑入
        }
        // 如果角色完全走出左边缘 (x < -宽度)
        else if (m.x < -v.getFitWidth()) {
            m.x = SCREEN_WIDTH; // 从右边滑入
        }
        // --- 传送门/屏幕环绕逻辑结束 ---

        // 2. 重力与物理
        m.velY += m.gravity;
        m.y += m.velY;
        // 碰撞检测
        boolean onSurface = false;
        for (ImageView paper : papers) {
            double playerBottom = m.y + v.getFitHeight();
            double paperTop = paper.getY();
            if (v.getBoundsInParent().intersects(paper.getBoundsInParent())) {
                if (m.velY >= 0 && playerBottom >= paperTop && playerBottom - m.velY <= paperTop + 20) {
                    m.y = paperTop - v.getFitHeight();
                    m.velY = 0;
                    m.isJumping = false;
                    onSurface = true;
                    break;
                }
            }
        }

        if (!onSurface && m.y + v.getFitHeight() >= groundY) {
            m.y = groundY - v.getFitHeight();
            m.velY = 0;
            m.isJumping = false;
            onSurface = true;
        }

        if (!onSurface) m.isJumping = true;

        // 检查蛋糕
        if (cake != null && cake.isVisible() && v.getBoundsInParent().intersects(cake.getBoundsInParent())) {
            showTip = true;
            if (keys.contains(KeyCode.E) && m.sizeState < 1 && !isEPressed) {  //不是最大状态
                m.sizeState++;
                m.y -= 35;
                m.jumpForce = -8;
            }

        }

        // 检查药水
        if (potion != null && potion.isVisible() && v.getBoundsInParent().intersects(potion.getBoundsInParent())) {
            showTip = true;
            if (keys.contains(KeyCode.E) && m.sizeState > -1 && !isEPressed) {  //不是最小状态
                m.sizeState--;
                m.jumpForce = -5;
            }
        }

        // 传送门逻辑
        boolean inPortalA = portalA != null && v.getBoundsInParent().intersects(portalA.getBoundsInParent());
        boolean inPortalB = portalB != null && v.getBoundsInParent().intersects(portalB.getBoundsInParent());

        if (inPortalA || inPortalB) {
            showTip = true; // 站在传送门上也显示提示

            if (keys.contains(KeyCode.E) && canTeleport) {
                if (inPortalA) {
                    m.x = portalB.getX();
                    m.y = portalB.getY() + (portalB.getFitHeight() - v.getFitHeight());
                } else {
                    m.x = portalA.getX();
                    m.y = portalA.getY() + (portalA.getFitHeight() - v.getFitHeight());
                }
                canTeleport = false; // 触发后暂时锁定，防止连续传送

            }
        }

        // 离开传送门后重置冷却锁
        if (!inPortalA && !inPortalB) {
            canTeleport = true;
        }

        // --- 门交互逻辑 ---
        if (door != null && v.getBoundsInParent().intersects(door.getBoundsInParent())) {
            // 只有当玩家身上有钥匙时，才显示交互提示
            if (m.hasKey) {
                showTip = true;
                if (currentEKey && !isEPressed) {
                    // 1. 换成开门的图片
                    door.setImage(doorOpenImg);
                    // 2. 消耗钥匙
                    m.hasKey = false;
                    // 3. 右下角图标设为不可见
                    if (keyUI != null) {
                        keyUI.setVisible(false);
                    }
                    //12/28 1637 添加过关后更新level
                    GameController.updateLevel();
                    System.out.println(GameController.checkLevel());
                }
            }
        }

        // --- 拉杆交互逻辑 ---
        if (lever != null && v.getBoundsInParent().intersects(lever.getBoundsInParent())) {
            showTip = true;
            if (currentEKey && !isEPressed) {
                if (lever.getImage() == leverLeft) {
                    lever.setImage(leverRight);
                    // 状态A -> 状态B：纸片长高
                    if (targetPaper != null) {
                        targetPaper.setHeight(160);
                        targetPaper.setY(groundY - 160); // 向上生长，确保底部贴地
                    }
                } else {
                    lever.setImage(leverLeft);
                    // 状态B -> 状态A：纸片缩回
                    if (targetPaper != null) {
                        targetPaper.setHeight( 80);
                        targetPaper.setY(groundY - 80);
                    }
                }
            }
        }

        isEPressed = currentEKey; // 更新 E 键的记录状态

        // 统一设置提示状态和位置
        if (showTip) {
            tip.setX(m.x - 10);
            tip.setY(m.y - 20);
//            tip.setX(m.x + (v.getFitWidth() / 2) - 10); // 居中显示
//            tip.setY(m.y - 30);                         // 始终在玩家头顶上方
            tip.setVisible(true);
            tip.toFront();
        } else {
            tip.setVisible(false);
        }
        // 根据 sizeState 统一刷新视图尺寸
        applySizeState(m, v);
        v.toFront();
    }


    // 尺寸统一刷新方法
    private void applySizeState(PlayerModel m, ImageView v) {
        if (m.sizeState == 1) {        // 大
            v.setFitWidth(60);
            v.setFitHeight(90);
        } else if (m.sizeState == -1) { // 小
            v.setFitWidth(20);
            v.setFitHeight(30);
        } else {                       // 正常 (0)
            v.setFitWidth(40);
            v.setFitHeight(60);
        }


        // 最后同步视图
        v.setX(m.x);
        v.setY(m.y);
    }
}
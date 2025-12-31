package com.paperanne.controller;

import com.paperanne.model.GameLever;
import com.paperanne.model.Ladder;
import com.paperanne.model.PaperModel;
import com.paperanne.model.PlayerModel;
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
                       PaperModel targetPaper,List<Ladder> ladders,
                       List<GameLever> levers) {

        boolean showTip = false; // 是否显示提示文字
        boolean currentEKey = keys.contains(KeyCode.E);// 检测本帧 E 键是否被按下

        boolean inLadder = false;
        Ladder activeLadder = null;

        // --- 1. 左右移动逻辑（带碰撞检测） ---
        // --- 1. 左右移动逻辑（带碰撞检测） ---
        double dx = 0;
        if (keys.contains(KeyCode.A) || keys.contains(KeyCode.LEFT)) {
            dx = -m.speed;
            v.setScaleX(1);
        }
        if (keys.contains(KeyCode.D) || keys.contains(KeyCode.RIGHT)) {
            dx = m.speed;
            v.setScaleX(-1);
        }

        if (dx != 0) {
            double nextX = m.x + dx;
            boolean willHitWall = false;

            for (ImageView paper : papers) {
                Object data = paper.getUserData();
                if (!"SPECIAL_WALL".equals(paper.getId())) {
                    continue;
                }

                // 使用 getBoundsInLocal 并手动偏移，比 intersects 更精准
                double pL = nextX;
                double pR = nextX + v.getFitWidth();
                double pT = m.y + 10; // 顶部收缩，防止碰到平台底部也算撞墙
                double pB = m.y + v.getFitHeight() - 10; // 底部收缩，防止碰到地板也算撞墙

                double wL = paper.getX();
                double wR = paper.getX() + paper.getFitWidth();
                double wT = paper.getY();
                double wB = paper.getY() + paper.getFitHeight();

                if (pR > wL && pL < wR && pB > wT && pT < wB) {
                    willHitWall = true;
                    System.out.println("Blocked by Wall!"); // 调试信息
                    break;
                }
            }

            if (!willHitWall) {
                m.x = nextX;
                // 核心修复：在这里立刻更新视图，或者确保后面 applySizeState 使用的是正确的 m.x
            } else {
                // 如果撞墙了，把 m.x 修正回 View 的当前位置，防止抖动
                m.x = v.getX();
            }
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

        // 只有当列表不为空时才进行检测
        if (ladders != null && !ladders.isEmpty()) {
            for (Ladder ladder : ladders) {
                if (v.getBoundsInParent().intersects(ladder.getView().getBoundsInParent())) {
                    inLadder = true;
                    break;
                }
            }
        }

        // 1. 检测是否在任何一个梯子范围内
        // 1. 梯子范围检测（保持不变）
        for (Ladder ladder : ladders) {
            if (v.getBoundsInParent().intersects(ladder.getView().getBoundsInParent())) {
                inLadder = true;
                activeLadder = ladder;
                break;
            }
        }

        // 2. 爬梯位移逻辑
        if (inLadder) {
            m.velY = 0;
            m.isJumping = false;
            if (keys.contains(KeyCode.W) || keys.contains(KeyCode.UP)) {
                m.y -= m.speed;
            }
            if (keys.contains(KeyCode.S) || keys.contains(KeyCode.DOWN)) {
                m.y += m.speed; // 这里虽然增加了y，但会被后面的碰撞逻辑弹回来
            }
        } else {
            m.velY += m.gravity;
            m.y += m.velY;
        }

        // 3. 平台碰撞检测逻辑
        boolean onSurface = false;

        // 判断是否想要通过梯子向下“穿透”平台
        boolean wantToClimbDown = inLadder && (keys.contains(KeyCode.S) || keys.contains(KeyCode.DOWN));

        // 如果正在向下爬梯子，我们直接跳过所有平台碰撞检测
        if (!wantToClimbDown) {
            for (ImageView paper : papers) {
                double playerBottom = m.y + v.getFitHeight();
                double paperTop = paper.getY();

                if (v.getBoundsInParent().intersects(paper.getBoundsInParent())) {
                    // 落地/踩踏检测
                    if (m.velY >= 0 && playerBottom >= paperTop && playerBottom - m.velY <= paperTop + 10) {

                        // 额外判定：如果你在梯子上，且离平台顶端很近，
                        // 为了防止“穿透效果”让你爬梯子时由于inLadder=true而无法站在平台上，
                        // 我们只在按下 S 时才允许穿透。
                        if (inLadder && !keys.contains(KeyCode.S) && !keys.contains(KeyCode.DOWN)) {
                            // 在梯子上但没按S，依然可以站在平台上
                            m.y = paperTop - v.getFitHeight();
                            m.velY = 0;
                            m.isJumping = false;
                            onSurface = true;
                            break;
                        } else if (!inLadder) {
                            // 正常的非梯子状态落地
                            m.y = paperTop - v.getFitHeight();
                            m.velY = 0;
                            m.isJumping = false;
                            onSurface = true;
                            break;
                        }
                    }
                }
            }

            // 地面(GROUND_Y)检测
            if (!onSurface && m.y + v.getFitHeight() >= groundY) {
                m.y = groundY - v.getFitHeight();
                m.velY = 0;
                m.isJumping = false;
                onSurface = true;
            }
        }

        if (!onSurface && !inLadder) m.isJumping = true;

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
            }

        }

        // 检查药水
        if (potion != null && potion.isVisible() && v.getBoundsInParent().intersects(potion.getBoundsInParent())) {
            showTip = true;
            if (keys.contains(KeyCode.E) && m.sizeState > -1 && !isEPressed) {  //不是最小状态
                m.sizeState--;
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
        // PlayerController.update 中替换原来的拉杆逻辑
        for (GameLever leverObj : levers) {
            ImageView lv = leverObj.getView();
            if (lv == null) {
                System.err.println("警告: 发现一个没有视图的拉杆对象！");
                continue;
            }
            // 距离检测
            if (v.getBoundsInParent().intersects(lv.getBoundsInParent())) {
                showTip = true;
                if (currentEKey && !isEPressed) {
                    leverObj.toggle(); // 一句话搞定所有联动！
                }
            }
        }

        isEPressed = currentEKey; // 更新 E 键的记录状态

        // 统一设置提示状态和位置
        if (showTip) {
            tip.setX(m.x - 10);
            tip.setY(m.y - 20);
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
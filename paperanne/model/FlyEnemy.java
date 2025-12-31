package com.paperanne.model;

import javafx.scene.image.Image;

public class FlyEnemy extends Enemy {
    private enum State { SLEEP, PATROL, CHASE, RETURN }
    private State currentState = State.SLEEP;
    private double homeX, homeY;
    private double chaseRange = 250.0;
    private double attackRange = 30.0; // 伤害范围
//计时器
    private int patrolTicks = 0;
    private final int PATROL_DURATION = 300;

    public FlyEnemy(double startX, double startY, double minX, double maxX, PaperModel platform) {
        super(startX, startY, minX, maxX, platform);
        this.homeX = startX;
        this.homeY = startY;
        this.getImg().setImage(new Image(getClass().getResourceAsStream("/com/paperanne/images/fly_move.png")));
        this.getImg().setOpacity(0.5); // 睡眠时半透明
    }

    @Override
    public void update(PlayerModel player) {
        double distToPlayer = Math.sqrt(Math.pow(getX() - player.x, 2) + Math.pow(getY() - player.y, 2));

        switch (currentState) {
            case SLEEP:
                if (BugEnemy.isAlerted) {
                    currentState = State.PATROL;
                    this.getImg().setOpacity(1.0);
                    this.patrolTicks = 0; // 重置计时
                }
                break;

            case PATROL:
                // 在本层来回巡逻
                super.moveAround();
                this.patrolTicks++;

                // 逻辑A：如果发现安妮，进入追击
                if (distToPlayer < 250) {
                    currentState = State.CHASE;
                }
                // 逻辑B：如果巡逻时间够了，且警报已解除（或强制回巢），则准备睡觉
                else if (this.patrolTicks > PATROL_DURATION) {
                    currentState = State.RETURN;
                }
                break;

            case CHASE:
                moveTowards(player.x, player.y, 1.5);
                // 丢掉目标：距离过远
                if (distToPlayer > 400) {
                    currentState = State.PATROL; // 先回本层巡逻一会儿，而不是直接回巢
                    this.patrolTicks = 0;
                }
                break;

            case RETURN:
                // 飞回初始点
                moveTowards(homeX, homeY, 1.0);
                // 到达初始点附近
                if (Math.abs(getX() - homeX) < 5 && Math.abs(getY() - homeY) < 5) {
                    currentState = State.SLEEP;
                    this.getImg().setOpacity(0.5);
                    // 只有苍蝇安全回巢后，才考虑重置全局警报（可选）
                    BugEnemy.isAlerted = false;
                }
                break;
        }
    }

    // 只有追击时才判定为“同一平面”以造成伤害
    @Override
    public boolean isSamePlane() {
        // 只有在巡逻或追击时，且安妮在附近时才触发碰撞（防止飞回巢穴时无意撞死玩家）
        if (currentState == State.CHASE) return true;
        if (currentState == State.PATROL) return super.isSamePlane();
        return false;
    }

    private void moveTowards(double tx, double ty, double speedMult) {
        double dx = tx - x;
        double dy = ty - y;
        double angle = Math.atan2(dy, dx);
        x += Math.cos(angle) * speedMult;
        y += Math.sin(angle) * speedMult;
        // 飞行单位不需要 antiChoke 物理检测，直接移动
        getImg().setScaleX(dx > 0 ? 1 : -1);
    }

}
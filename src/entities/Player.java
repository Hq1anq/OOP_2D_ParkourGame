package entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import main.Game;
import static utilz.Constants.PlayerConstants.*;
import static utilz.HelpMethods.*;
import utilz.LoadSave;
import utilz.Point;

public class Player extends Entity {

    private BufferedImage[][] animations;
    private int aniTick, aniIndex, aniSpeed = 15;
    private int playerAction = IDLE;
    private boolean moving = false, isFacingLeft = false, canMove = true;
    private boolean left, up, right, down, jump, crouch;
    private float playerSpeed = 3.0f;
    private int[][] levelData;
    private float xDrawOffset = 3 * 2 * Game.SCALE;
    private float yDrawOffset = 0 * 2 * Game.SCALE;

    // Jumping
    private float airSpeed = 0f;
    private float gravity = 0.04f * Game.SCALE;
    private float jumpSpeed = -3.0f * Game.SCALE;
    private float fallSpeedScale = 2.0f;
    private boolean inAir = false;

    // Direction flip
    private int flipX = 0;
    private int flipW = 1;

    // Ledge climbing
    private float ledgeClimbXOffset = 2;
    private float ledgeClimbYOffset = 5;

    // Double jump

    public Player(float x, float y, int width, int height) {
        super(x, y, width, height);
        loadAnimation();
        initHitbox(x, y, 22 * 2 * Game.SCALE, 31 * 2 * Game.SCALE);
    }

    public void update() {
        updatePos();
        updateAnimationTick();
        setAnimation();
        // if (IsTouchingLedge(hitbox, levelData, true)) System.out.println("1");
        // if (IsTouchingLedge(hitbox, levelData, false)) System.out.println("2");
    }

    public void render(Graphics g, int xLevelOffset) {
        if (playerAction == LEDGE_CLIMB) {
            // g.drawLine(0, 0, (int) (hitbox.x - xLevelOffset), (int) (hitbox.y - yDrawOffset));
            if (isFacingLeft)
                g.drawImage(animations[playerAction][aniIndex],
                    (int) (hitbox.x - xLevelOffset) + (int) hitbox.width + 2 * Game.TILE_SIZE - 21,
                    (int) (hitbox.y - yDrawOffset) - 2,
                    -2 * width, 2 * height, null);
            else
                g.drawImage(animations[playerAction][aniIndex],
                    (int) (hitbox.x - xLevelOffset) - Game.TILE_SIZE - 27,
                    (int) (hitbox.y - yDrawOffset) - 2,
                    2 * width, 2 * height, null);
        } else
            if (isFacingLeft)
                g.drawImage(animations[playerAction][aniIndex],
                    (int) (hitbox.x + hitbox.width + xDrawOffset) - xLevelOffset,
                    (int) (hitbox.y - yDrawOffset),
                    width * flipW, height, null);
            else
                g.drawImage(animations[playerAction][aniIndex],
                    (int) (hitbox.x - xDrawOffset) - xLevelOffset,
                    (int) (hitbox.y - yDrawOffset),
                    width * flipW, height, null);
        // g.drawLine(0, 0, 100, (int) ((hitbox.y + airSpeed) / Game.TILE_SIZE) * Game.TILE_SIZE);
        // drawHitbox(g, xLevelOffset);
    }

    private void updateAnimationTick() {
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= GetSpriteAmount(playerAction)) {
                if (playerAction == FALL)
                    aniIndex --;
                else if (playerAction == LEDGE_CLIMB) {
                    canMove = true;
                    aniIndex = 0;
                } else
                    aniIndex = 0;
            }
        }
    }

    private void setAnimation() {
        int startAnimation = playerAction;

        if (!canMove) {
            playerAction = LEDGE_CLIMB;
        } else if (inAir) {
            if (airSpeed < 0)
                playerAction = JUMP;
            else
                playerAction = FALL;
        } else if (moving) {
            playerAction = WALKING;
        } else if (crouch) {
            playerAction = CROUCH;
        } else playerAction = IDLE;

        if (startAnimation != playerAction) {
            resetAnimationTick();
        }
    }

    private void resetAnimationTick() {
        aniTick = 0;
        aniIndex = 0;
    }

    private void updatePos() {
        moving = false;

        if (jump)
            jump();

        if (!inAir)
            if((!left && !right) || (left && right))
                return;
    
        float xSpeed = 0;
        if (canMove) {
            if (left) {
                isFacingLeft = true;
                xSpeed -= playerSpeed;
                flipX = width;
                flipW = -1;
            }
            if (right) {
                isFacingLeft = false;
                xSpeed += playerSpeed;
                flipX = 0;
                flipW = 1;
            }
        }
        if (!inAir) {
            if (!IsEntityOnFloor(hitbox, levelData)) {
                inAir = true;
            }
        }
        if (inAir) {
            Point ledgePos = GetEntityWhenLedgeClimb(hitbox, levelData, isFacingLeft, ledgeClimbXOffset, ledgeClimbYOffset);
            if (ledgePos != null) {
                hitbox.x = ledgePos.x;
                hitbox.y = ledgePos.y;
                canMove = false;
            }
            if (canMove)
                if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, levelData)) {
                    hitbox.y += airSpeed;
                    if (airSpeed > 0)
                        airSpeed += gravity * fallSpeedScale;
                    else airSpeed += gravity;
                    updateXPos(xSpeed);
                } else {
                    hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
                    if (airSpeed > 0)
                        resetInAir();
                    else
                        airSpeed += gravity * fallSpeedScale;
                    updateXPos(xSpeed);
                }
        } else {
            updateXPos(xSpeed);
        }
        moving = true;
    }

    private void crouch() {

    }
    private void jump() {
        if (inAir) return;
        inAir = true;
        airSpeed = jumpSpeed;
    }

    private void updateXPos(float xSpeed) {
        if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, levelData)) {
            hitbox.x += xSpeed;
        } else {
            hitbox.x = GetEntityXPosNextToWall(hitbox, xSpeed);
        }
    }
    
    private void resetInAir() {
        inAir = false;
        airSpeed = 0;
        // hitbox.y = (int) (hitbox.y / Game.TILE_SIZE) * Game.TILE_SIZE; // Align with the tile grid
    }

    private void loadAnimation() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);
        animations = new BufferedImage[12][8];
        // for (int i = 0; i < animations.length; i++) {
        //     for (int j = 0; j < animations[i].length; j++) {
        //         animations[i][j] = img.getSubimage(j * 32, i * 32, 32, 32);
        //     }
        // }
        for (int i = 0; i < animations.length - 1; i++) {
            for (int j = 0; j < animations[i].length; j++) {
                animations[i][j] = img.getSubimage(j * 32, i * 32, 32, 32);
            }
        }
        // Animation Ledge Climb
        for (int i = 0; i < 8; i++)
            animations[11][i] = img.getSubimage(i * 64, 11 * 32, 64, 64);
    }

    public void loadLevelData(int[][] levelData) {
        this.levelData = levelData;
        if (!IsEntityOnFloor(hitbox, levelData))
            inAir = true;
    }

    public void resetDirectionBooleans() {
        left = false;
        right = false;
        up = false;
        down = false;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public void setJump(boolean jump) {
        this.jump = jump;
    }
    public void setCrouch(boolean crouch) {
        this.crouch = crouch;
    }
}

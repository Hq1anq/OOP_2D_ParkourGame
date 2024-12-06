package entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import main.Game;
import static utilz.Constants.PlayerConstants.*;
import static utilz.HelpMethods.*;
import utilz.LoadSave;
import utilz.Point;

public class Player extends Entity {

    // LOGIC STATISTICS
    private boolean moving = false, isFacingLeft = false, canMove = true;
    private boolean left, up, right, down, jump, crouch;
    private float playerSpeed = 3.0f;
    private int[][] levelData;
    // FOR ANIMATIONS
    private float xDrawOffset = 3 * 2 * Game.SCALE;
    private float yDrawOffset = 0 * 2 * Game.SCALE;
    private int aniTick, aniIndex, aniSpeed = 15;
    private BufferedImage[][] animations;
    private int playerAction = IDLE;

    // Jumping
    private float airSpeed = 0f;                    // falling speed: less than 0 if jumping, greater than 0 if falling
    private float gravity = 0.04f * Game.SCALE;
    private float jumpSpeed = -3.0f * Game.SCALE;   // starting jump speed
    private float fallSpeedScale = 2.0f;            // multiple to make falling faster than jumping
    private boolean inAir = false;
    private int countJump = 0;

    private float timeSinceGrounded = 0;
    private float coyoteTime = 0.1f;

    // Direction flip
    // For drawing
    private int flipX = 0;
    private int flipW = 1;

    // Ledge climbing
    private float ledgeClimbXOffset = 2;
    private float ledgeClimbYOffset = 5;

    public Player(float x, float y, int width, int height) {
        super(x, y, width, height);
        loadAnimation();
        initHitbox(x, y, 22 * 2 * Game.SCALE, 31 * 2 * Game.SCALE);
    }

    public void update() {
        // UPDATE POSITION AND STATISTICS FOR DRAWING ANIMATION

        updatePos();
        updateAnimationTick();
        setAnimation();
        // if (IsTouchingLedge(hitbox, levelData, true)) System.out.println("1");
        // if (IsTouchingLedge(hitbox, levelData, false)) System.out.println("2");
    }

    public void render(Graphics g, int xLevelOffset) {
        // DRAW PLAYER

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
                    airSpeed = 0;
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

    // ***IMPORTANT
    private void updatePos() {
        //PLAYER MOVING LOGIC

        moving = false;

        // jump handling
        if (jump)
            jump();

        // not in air but nothing being pressed -> do nothing
        if (!inAir)
            if((!left && !right) || (left && right))
                return;
    
        // normal moving
        // d -> move right, a -> move left
        float xSpeed = 0;   // prefer as delta x : to add to player position x (horizontal)
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

        // check if player is still on the floor
        // (go to the end of the floor = fall down)
        if (!inAir) {
            timeSinceGrounded = 0;
            if (!IsEntityOnFloor(hitbox, levelData)) {
                inAir = true;
            }
        } else timeSinceGrounded += 1.0 / Game.UPS_SET;

        // jumping/falling handling
        if (inAir) {
            // edge climbing handling : detect when climbable and do climbing
            Point ledgePos = GetEntityWhenLedgeClimb(hitbox, levelData, isFacingLeft, ledgeClimbXOffset, ledgeClimbYOffset);
            if (ledgePos != null) {
                hitbox.x = ledgePos.x;
                hitbox.y = ledgePos.y;
                canMove = false;
            }

            // no climbing
            // changing vertical position when jumping/falling
            if (canMove)
                if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, levelData)) {
                    hitbox.y += airSpeed;
                    if (airSpeed > 0)
                        airSpeed += gravity * fallSpeedScale;
                    else airSpeed += gravity;
                    // making falling faster than jumping
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
        if (inAir && timeSinceGrounded > coyoteTime) return;
        inAir = true;
        airSpeed = jumpSpeed;
        timeSinceGrounded = coyoteTime;
    }

    private void updateXPos(float xSpeed) {
        // ADD player position x += xSpeed | xSpeed := delta x
        
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
        // USE IN GAME.JAVA
        // LOAD LEVEL DATA TO DETECT COLLISION

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

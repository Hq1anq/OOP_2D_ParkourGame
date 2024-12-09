package entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import main.Game;
import static utilz.Constants.PlayerConstants.*;
import static utilz.HelpMethods.*;
import utilz.LoadSave;
import utilz.Point;

@SuppressWarnings({"FieldMayBeFinal", "unused"})
public class Player extends Entity {

    // LOGIC STATISTICS
    // Normal moving
    private boolean moving = false, isFacingLeft = false; 
    private boolean left, up, right, down, crouch, jump, climb, isSuper;
    private boolean performCondition = false;       // handle keypress problem
    // private float playerSpeed = 3.0f;            // no more use after added friction and acceleration
    private int[][] levelData;

    // Accelaration and friction (testing / might have bugs)
    private float accelaration = 0.1f;
    private float friction = 0.05f;
    private float currentSpeed = 0f;
    private float maxSpeed = 3f;

    // Jumping
    private float airSpeed = 0f;                    // falling speed: less than 0 if jumping, greater than 0 if falling
    private float gravity = 0.04f * Game.SCALE;
    private float jumpSpeed = -3.0f * Game.SCALE;   // starting jump speed
    private boolean inAir = false;
    private int countJump = 0;
    private boolean preWallKick = false;

    // FALLING
    private boolean floorSmash = false;
    private float fallSpeedForSmash = 0;
    private float fallSpeedScale = 2.0f;            // multiple to make falling faster than jumping

    // Double jump
    private final int maxNumberOfJumps = 2;

    // Climbing
    private boolean climbing = false;               // true if player is climbing, false otherwise
    private boolean firstClimb = true;              // true if player is climbing for the first time, false otherwise
    private boolean isClimbingLeft = false;         // true if player is climbing left, false otherwise
    private float climbingSpeed = 1f;               // prefer as delta x, the amount will change of player position
    private float climbOffset = 2 * Game.TILE_SIZE;
    
    // Ledge climbing
    private boolean ledgeClimbing = false;          // true if player can climb ledge, false otherwise
    private boolean canMove = true;                 // true if player is not climbing legde, false otherwise (prefer as !climbingLedge)
    private float ledgeClimbXOffset = 2;
    private float ledgeClimbYOffset = 5;

    // CoyoteTime
    private float timeSinceGrounded = 0;            // count time while not on floor/ground
    private float coyoteTime = 0.1f;                // could be final, 0.15f might be more optimize

    // FOR ANIMATIONS
    // Direction flip
    private int flipX = 0;
    private int flipW = 1;
    // For drawing
    private float xDrawOffset = 3 * 2 * Game.SCALE;
    private float yDrawOffset = 0 * 2 * Game.SCALE;
    private int aniTick, aniIndex, aniSpeed = 15;
    private BufferedImage[][] animations;
    private int playerAction = IDLE;

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

    public void render(Graphics g, int xLevelOffset, int yLevelOffset) {
        // DRAW PLAYER

        if (playerAction == LEDGE_CLIMB) {
            // g.drawLine(0, 0, (int) (hitbox.x - xLevelOffset), (int) (hitbox.y - yDrawOffset));
            if (isFacingLeft)
                g.drawImage(animations[playerAction][aniIndex],
                    (int) (hitbox.x - xLevelOffset) + (int) hitbox.width + 2 * Game.TILE_SIZE - 21,
                    (int) (hitbox.y - yDrawOffset - yLevelOffset) - 2,
                    -2 * width, 2 * height, null);
            else
                g.drawImage(animations[playerAction][aniIndex],
                    (int) (hitbox.x - xLevelOffset) - Game.TILE_SIZE - 27,
                    (int) (hitbox.y - yDrawOffset - yLevelOffset) - 2,
                    2 * width, 2 * height, null);
        } else if (playerAction == WALL_CLIMB) {
            if (firstClimb) aniIndex = GetSpriteAmount(WALL_CLIMB) - 1;
            if (isClimbingLeft)
                g.drawImage(animations[playerAction][aniIndex],
                    (int) (hitbox.x + hitbox.width + xDrawOffset) - xLevelOffset,
                    (int) (hitbox.y - yDrawOffset - yLevelOffset),
                    width * flipW, 2 * height, null);
            else
                g.drawImage(animations[playerAction][aniIndex],
                    (int) (hitbox.x - xDrawOffset) - xLevelOffset,
                    (int) (hitbox.y - yDrawOffset - yLevelOffset),
                    width * flipW, 2 * height, null);
        } else
            if (isFacingLeft)
                g.drawImage(animations[playerAction][aniIndex],
                    (int) (hitbox.x + hitbox.width + xDrawOffset) - xLevelOffset,
                    (int) (hitbox.y - yDrawOffset - yLevelOffset),
                    width * flipW, height, null);
            else
                g.drawImage(animations[playerAction][aniIndex],
                    (int) (hitbox.x - xDrawOffset) - xLevelOffset,
                    (int) (hitbox.y - yDrawOffset - yLevelOffset),
                    width * flipW, height, null);
        // g.drawLine(0, 0, 100, (int) ((hitbox.y + airSpeed) / Game.TILE_SIZE) * Game.TILE_SIZE);
        // drawHitbox(g, xLevelOffset);
    }

    private void loadAnimation() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);
        animations = new BufferedImage[15][8];
        for (int i = 0; i < animations.length - 2; i++) {
            for (int j = 0; j < animations[i].length; j++) {
                animations[i][j] = img.getSubimage(j * 32, i * 32, 32, 32);
            }
        }
        // Animation > 32 pixels
        for (int i = 0; i < 8; i++) {
            animations[WALL_CLIMB][i] = img.getSubimage(i * 32, WALL_CLIMB * 32, 32, 64); // CLIMB 64x32
            animations[LEDGE_CLIMB][i] = img.getSubimage(i * 64, (LEDGE_CLIMB + 1) * 32, 64, 64); // LEDGE CLIMB 64x64
        }
    }

    private void updateAnimationTick() {
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= GetSpriteAmount(playerAction)) {
                switch (playerAction) {
                    case FALL -> aniIndex --;
                    case FLOOR_SMASH -> {
                        floorSmash = false;
                        canMove = true;
                        aniIndex = 0;
                    }
                    case LEDGE_CLIMB -> {
                        canMove = true;
                        ledgeClimbing = false;
                        aniIndex = 0;
                        airSpeed = 0;
                    }
                    case WALL_CLIMB -> {
                        canMove = true;
                        aniIndex --;
                        airSpeed = 0;
                    }
                    default -> aniIndex = 0;
                }
            }
        }
    }

    private void setAnimation() {
        int startAnimation = playerAction;
        if (ledgeClimbing) {
            playerAction = LEDGE_CLIMB;
        } else if (climbing) {
            if (!preWallKick)
                playerAction = WALL_CLIMB;
            else playerAction = WALL_KICK;
        } else if (inAir) {
            if (countJump == 2)
                playerAction = AIR_FLIP;
            else if (airSpeed < 0)
                playerAction = JUMP;
            else
                playerAction = FALL;
        } else {
            if (floorSmash) {
                playerAction = FLOOR_SMASH;
            } else if (moving) {
                playerAction = WALKING;
            } else if (crouch) {
                playerAction = CROUCH;
            } else playerAction = IDLE;
        }
        
        if (startAnimation != playerAction) {
            resetAnimationTick();
            if (startAnimation == LEDGE_CLIMB)
                playerAction = IDLE;
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
        if (!jump && !climb) {
            performCondition = true;
        }
        if (performCondition && jump) {
            performCondition = false;
            if ((!climbing && countJump == 0 && timeSinceGrounded <= coyoteTime) ||
                (!climbing && countJump > 0 && countJump < maxNumberOfJumps) ||
                preWallKick || (climbing && !left && !right)) {
                    jump();
            }
        }
        if (performCondition && climb && !jump) {
            performCondition = false;
            if (climbing) climbing = false;
            else if (canClimb()) {
                climbing = true;
                countJump = 0;
                firstClimb = true;
                isClimbingLeft = isFacingLeft;
            }
        }
        if (canMove && climbing) {
            preWallKick = (left && !isClimbingLeft) || (right && isClimbingLeft);
            if (!preWallKick) firstClimb = true;
        }
        if (canMove && climbing && !preWallKick && jump && (left | right)) {
            climb();
        }
        // not in air but nothing being pressed -> do nothing
        if (!inAir){
            countJump = 0;
            timeSinceGrounded = 0;
        }
            
        if ((left && !right) || (!left && right)) moving = true;
    
        float xSpeed = 0;   // prefer as delta x : to add to player position x (horizontal)
        if (canMove) {
            if(moving == true && !climbing){
                currentSpeed += accelaration;
                if(currentSpeed > maxSpeed) currentSpeed = maxSpeed;

                if(left){
                    isFacingLeft = true;
                    flipX = width;
                    flipW = -1;
                } else {
                    isFacingLeft = false;
                    flipX = 0;
                    flipW = 1;
                }

            } else {
                // not moving
                currentSpeed -= friction;
                if(currentSpeed < 0) currentSpeed = 0;
            }

            if(isFacingLeft)    xSpeed = -currentSpeed;
            else                xSpeed = currentSpeed;
        }

        // check if player is still on the floor
        // (go to the end of the floor = fall down)
        if (!inAir) {
            if (!IsEntityOnFloor(hitbox, levelData)) {
                inAir = true;
            }
        } else timeSinceGrounded += 1.0 / Game.UPS_SET;

        // jumping/falling handling
        if (inAir) {
            // edge climbing handling : detect when climbable and do climbing
            Point ledgePos = GetEntityWhenLedgeClimb(hitbox, levelData, isFacingLeft, ledgeClimbXOffset, ledgeClimbYOffset);
            if (ledgePos != null) {
                currentSpeed = 0;
                hitbox.x = ledgePos.x;
                hitbox.y = ledgePos.y;
                canMove = false;
                ledgeClimbing = true;
                climbing = false;
            }

            // wall climbing handling
            if(climbing == true){
                airSpeed = 0;
            }

            // if player is not climbing than update position after jumping/falling
            else

            // no climbing
            // changing vertical position when jumping/falling
                if (canMove) {
                    if (down) {
                        if (airSpeed < 0) {
                            airSpeed = 0;
                        }
                        fallSpeedScale = 2.5f;
                    } else fallSpeedScale = 2f;
                    if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, levelData)) {
                        hitbox.y += airSpeed;
                        if (airSpeed > 0)
                            airSpeed += gravity * fallSpeedScale;
                        else airSpeed += gravity;
                        // making falling faster than jumping
                        updateXPos(xSpeed);
                    } else {
                        hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
                        if (airSpeed > 0) {
                            floorSmash = (airSpeed > 9);
                            resetInAir();
                        }
                        else
                            airSpeed += gravity * fallSpeedScale;
                        updateXPos(xSpeed);
                    }
                }
        } else {
            updateXPos(xSpeed);
        }
        
    }

    private void crouch() {

    }

    public void jump(){
        countJump++;
        inAir = true;
        airSpeed = jumpSpeed;
        timeSinceGrounded = coyoteTime;
        climbing = false;
        preWallKick = false;
    }

    public void climb(){
        float nextHeight = hitbox.y - climbOffset;
        boolean climbed = false;
        while (hitbox.y > nextHeight &&
                CanMoveHere(hitbox.x, hitbox.y - climbingSpeed,
                            hitbox.width, hitbox.height, levelData)) {
            hitbox.y -= climbingSpeed;
            climbed = true;
            if (GetEntityWhenLedgeClimb(hitbox, levelData, isFacingLeft, ledgeClimbXOffset, ledgeClimbYOffset) != null) {
                canMove = false;
                ledgeClimbing = true;
                climbing = false;
                return;
            }
        }
        if (CanMoveHere(hitbox.x, hitbox.y - climbingSpeed, hitbox.width, hitbox.height, levelData))
            aniIndex = 0;
        else if (climbed) aniIndex = GetSpriteAmount(WALL_CLIMB) - 4;
        else aniIndex = GetSpriteAmount(WALL_CLIMB) - 1;
        firstClimb = false;
        canMove = false;
        climbing = true;
        countJump = 0;
    }

    private boolean canClimb() {
        return ((isFacingLeft &&
                IsSolid(hitbox.x - 3, hitbox.y, levelData) &&
                IsSolid(hitbox.x - 3, hitbox.y + hitbox.height - 3, levelData))
            ||  (!isFacingLeft &&
                IsSolid(hitbox.x + hitbox.width + 3, hitbox.y, levelData) &&
                IsSolid(hitbox.x + hitbox.width + 3, hitbox.y + hitbox.height - 3, levelData)));
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

    public void setSuper(boolean isSuper) {
        this.isSuper = isSuper;
    }

    public void setJump(boolean jump) {
        this.jump = jump;
    }

    public void setClimb(boolean climb) {
        this.climb = climb;
    }
    
    public void setCrouch(boolean crouch) {
        this.crouch = crouch;
    }

    public void setClimbing(boolean climbing){
        this.climbing = climbing;
    }
}

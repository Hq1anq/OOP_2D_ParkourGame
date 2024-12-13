package entities;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import main.Game;
import static main.Game.TILE_SIZE;
import static main.Game.camera;
import static utilz.Constants.PlayerConstants.*;
import utilz.HelpMethods;
import static utilz.HelpMethods.*;
import utilz.LoadSave;
import utilz.Point;

@SuppressWarnings({"FieldMayBeFinal", "unused"})
public class Player extends Entity {

    // SYSTEM
    private Game game;                              // need this to play sound effect

    // LOGIC STATISTICS
    // Normal moving
    private boolean moving = false, isFacingLeft = false; 
    private boolean left, up, right, down, crouch, jump, climb;
    private boolean performCondition = false;       // handle keypress problem
    // private float playerSpeed = 2.0f;            // no more use after added friction and acceleration
    private int[][] levelData;

    // Accelaration and friction
    private float accelaration = 0.5f;
    private float friction = 0.05f;
    private float leftCurrentSpeed = 0f;
    private float rightCurrentSpeed = 0f;
    private float maxSpeed = 3f;
    private final float DEFAULT_ACCELARATION = 1f;
    private final float DEFAULT_FRICTION = 0.5f;
    private final float ON_ICE_ACCELARATION = 0.04f;
    private final float ON_ICE_FRICTION = 0.02f;
    private final float ON_MUD_ACCELARATION = 0.1f;
    private final float ON_MUD_FRICTION = 0.07f;

    // Jumping
    private float airSpeed = 0f;                    // falling speed: less than 0 if jumping, greater than 0 if falling
    private float gravity = 0.04f * Game.SCALE;
    private float jumpSpeed = -2.5f * Game.SCALE;   // starting jump speed
    private boolean inAir = false;
    private int countJump = 0;
    private boolean preWallKick = false;

    // FALLING
    private boolean floorSmash = false;
    private float fallSpeedForSmash = 0;
    private float fallSpeedScale = 1.5f;            // multiple to make falling faster than jumping

    // Double jump
    private final int maxNumberOfJumps = 2;

    // Climbing
    private boolean climbing = false;               // true if player is climbing, false otherwise
    private boolean firstClimb = true;              // true if player is climbing for the first time, false otherwise
    private boolean isClimbingLeft = false;         // true if player is climbing left, false otherwise
    private float climbingSpeed = 1f;               // prefer as delta x, the amount will change of player position
    private float climbOffset = 2 * Game.TILE_SIZE;
    
    private boolean ladderClimb = false;
    private boolean toggleLadderClimb = true;
    private int climbFPS = 20;
    private float climbCountAni = 0;
    
    // Ledge climbing
    private boolean ledgeClimbing = false;          // true if player can climb ledge, false otherwise
    private boolean canMove = true;                 // true if player is not climbing legde, false otherwise (prefer as !climbingLedge)
    private float ledgeClimbXOffset = 2;
    private float ledgeClimbYOffset = 5;

    // CoyoteTime
    private float timeSinceGrounded = 0;            // count time while not on floor/ground
    private float coyoteTime = 0.1f;                // could be final, 0.15f might be more optimize

    // Dash
    private boolean dash = false;                   // true if user is pressing dash, false otherwise
    private boolean dashing = false;                // true if player is dashing , false otherwise
    private long dashCoolDown = 1500;               // time between dashes (ms)
    private long startDashTime = 0;                 // get the time when start dashing
    private float positionXAfterDash = 0;
    private float dashSpeed = 4f;
    private float dashDistance = 6 * Game.TILE_SIZE;
    private float preY;

    // Health
    private int currentHealth = 5;
    private boolean unvulerable = false;            // turn true when just got hit, false after unvulerableTime
    private long timeSinceLastUnvulerable = 0;
    private long unvulerableTime = 2000;            // (ms)
    private boolean winning = false;

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
    private File file;
    private BufferedImage[] healthImages = new BufferedImage[2];
    private long timeSinceLastShake = 0;
    private int delta_shaking = 0;

    // SOUND EFFECT
    private long timeSinceLastPlayedFootstepsSoundEffect = 0;

    public Player(float x, float y, int width, int height, Game game) {
        super(x, y, width, height);
        loadAnimation();
        initHitbox(x, y, 22 * 2 * Game.SCALE, 31 * 2 * Game.SCALE);

        healthImages[0] = LoadSave.GetSpriteAtlas(LoadSave.EMPTY_HEART);
        healthImages[1] = LoadSave.GetSpriteAtlas(LoadSave.FULL_HEART);

        this.game = game;
    }

    public void update() {
        // UPDATE POSITION AND STATISTICS FOR DRAWING ANIMATION

        updatePos();
        updateAnimationTick();
        setAnimation();
    }

    public void render(Graphics2D g2, int xLevelOffset, int yLevelOffset) {
        // DRAW PLAYER
        if (unvulerable && currentHealth > 0){
            float alphaValue;
            long now = System.currentTimeMillis();
            now = (now - timeSinceLastUnvulerable) % 1000;
            if(now < 64)   alphaValue = 0f;
            else if(now < 31 || now > 968)  alphaValue = 1f;
            else if(now < 64 || now > 935)  alphaValue = 0.9f;
            else if(now < 97 || now > 903)  alphaValue = 0.8f;
            else if(now < 131 || now > 869) alphaValue = 0.7f;
            else if(now < 167 || now > 833) alphaValue = 0.6f;
            else if(now < 205 || now > 795) alphaValue = 0.5f;
            else if(now < 247 || now > 753) alphaValue = 0.4f;
            else if(now < 295 || now > 704) alphaValue = 0.3f;
            else if(now < 356 || now > 644) alphaValue = 0.2f;
            else                            alphaValue = 0.1f;

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaValue));
        }

        if (playerAction == LEDGE_CLIMB) {
            // g.drawLine(0, 0, (int) (hitbox.x - xLevelOffset), (int) (hitbox.y - yDrawOffset));
            if (isFacingLeft)
                g2.drawImage(HelpMethods.syncWithUnvulerable(animations[playerAction][aniIndex], g2, unvulerable, timeSinceLastUnvulerable),
                    (int) (hitbox.x - xLevelOffset) + (int) hitbox.width + 2 * Game.TILE_SIZE - 21,
                    (int) (hitbox.y - yDrawOffset - yLevelOffset) - 2,
                    -2 * width, 2 * height, null);
            else
                g2.drawImage(HelpMethods.syncWithUnvulerable(animations[playerAction][aniIndex], g2, unvulerable, timeSinceLastUnvulerable),
                    (int) (hitbox.x - xLevelOffset) - Game.TILE_SIZE - 27,
                    (int) (hitbox.y - yDrawOffset - yLevelOffset) - 2,
                    2 * width, 2 * height, null);
        } else if (playerAction == WALL_CLIMB) {
            if (firstClimb) aniIndex = GetSpriteAmount(WALL_CLIMB) - 1;
            if (isClimbingLeft)
                g2.drawImage(HelpMethods.syncWithUnvulerable(animations[playerAction][aniIndex], g2, unvulerable, timeSinceLastUnvulerable),
                    (int) (hitbox.x + hitbox.width + xDrawOffset) - xLevelOffset,
                    (int) (hitbox.y - yDrawOffset - yLevelOffset),
                    width * flipW, 2 * height, null);
            else
                g2.drawImage(HelpMethods.syncWithUnvulerable(animations[playerAction][aniIndex], g2, unvulerable, timeSinceLastUnvulerable),
                    (int) (hitbox.x - xDrawOffset) - xLevelOffset,
                    (int) (hitbox.y - yDrawOffset - yLevelOffset),
                    width * flipW, 2 * height, null);
        } else if (playerAction == START_DASH) {
            if (isFacingLeft)
                g2.drawImage(HelpMethods.syncWithUnvulerable(animations[playerAction][aniIndex], g2, unvulerable, timeSinceLastUnvulerable),
                    (int) (hitbox.x + hitbox.width + xDrawOffset) - xLevelOffset,
                    (int) (hitbox.y - yDrawOffset - yLevelOffset) - TILE_SIZE,
                    width * flipW, height, null);
            else
                g2.drawImage(HelpMethods.syncWithUnvulerable(animations[playerAction][aniIndex], g2, unvulerable, timeSinceLastUnvulerable),
                    (int) (hitbox.x - xDrawOffset) - xLevelOffset,
                    (int) (hitbox.y - yDrawOffset - yLevelOffset) - TILE_SIZE,
                    width * flipW, height, null);
        } else
            if (isFacingLeft)
                g2.drawImage(HelpMethods.syncWithUnvulerable(animations[playerAction][aniIndex], g2, unvulerable, timeSinceLastUnvulerable),
                    (int) (hitbox.x + hitbox.width + xDrawOffset) - xLevelOffset,
                    (int) (hitbox.y - yDrawOffset - yLevelOffset),
                    width * flipW, height, null);
            else
                g2.drawImage(HelpMethods.syncWithUnvulerable(animations[playerAction][aniIndex], g2, unvulerable, timeSinceLastUnvulerable),
                    (int) (hitbox.x - xDrawOffset) - xLevelOffset,
                    (int) (hitbox.y - yDrawOffset - yLevelOffset),
                    width * flipW, height, null);
        // g.drawLine(0, 0, 100, (int) ((hitbox.y + airSpeed) / Game.TILE_SIZE) * Game.TILE_SIZE);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        // drawHitbox(g2, xLevelOffset, yLevelOffset);
    }

    private void loadAnimation() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);
        animations = new BufferedImage[16][16];
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
            if (playerAction == LADDER_CLIMB) {
                if (aniIndex >= GetSpriteAmount(playerAction))
                    aniIndex = 0;
                else return;
            }
            aniIndex++;
            if (aniIndex >= GetSpriteAmount(playerAction)) {
                switch (playerAction) {
                    case FALL -> aniIndex --;
                    case WALKING -> aniIndex -= 8;
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
                    case FINISH_DASH -> {
                        playerAction = IDLE;
                    }
                    case WIN_POSE -> {
                        aniIndex = 0;
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
        } else if (ladderClimb && toggleLadderClimb) {
            playerAction = LADDER_CLIMB;
        } else if (inAir) {
            if (countJump == 2)
                playerAction = AIR_FLIP;
            else if (airSpeed < 0)
                playerAction = JUMP;
            else
                playerAction = FALL;
        } else if (winning) {
            playerAction = WIN_POSE;
            canMove = false;
        } else {
            if (dashing) {
                playerAction = START_DASH;
            } else if (startAnimation != FINISH_DASH) {
                if (floorSmash) {
                    playerAction = FLOOR_SMASH;
                } else if (moving) {
                    playerAction = WALKING;
                } else if (crouch) {
                    playerAction = CROUCH;
                } else
                    playerAction = IDLE;
            }
        }
        
        if (startAnimation != playerAction) {
            resetAnimationTick();
            if (startAnimation == LEDGE_CLIMB) {
                playerAction = IDLE;
                aniIndex = GetSpriteAmount(IDLE) - 1;
            }
            if (startAnimation == START_DASH) playerAction = FINISH_DASH;
        }
    }

    private void resetAnimationTick() {
        aniTick = 0;
        aniIndex = 0;
    }

    // ***IMPORTANT
    private void updatePos() {
        //PLAYER MOVING LOGIC
        if(HitTrap(hitbox, levelData) && !unvulerable)  gotHit();

        if(System.currentTimeMillis() - timeSinceLastUnvulerable > unvulerableTime)
            unvulerable = false;

        if(dashing){
            dash();
            return;
        }
        
        if(dash && canMove && !climbing && !inAir && System.currentTimeMillis() - startDashTime > dashCoolDown){
            dash();
            return;
        }

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
        if (canMove && performCondition && climb && !jump) {
            performCondition = false;
            if (climbing) climbing = false;
            else if (canClimb()) {
                climbing = true;
                countJump = 0;
                firstClimb = true;
                isClimbingLeft = isFacingLeft;
            }
            if (ladderClimb) toggleLadderClimb = false;
        }
        if (canMove && climbing) {
            preWallKick = (left && !isClimbingLeft) || (right && isClimbingLeft);
            if (!preWallKick) firstClimb = true;
        }
        if (canMove && climbing && !preWallKick && jump && (left | right)) {
            climb();
        }
        if (canMove && toggleLadderClimb && IsInLadder(hitbox, levelData)) {
            ladderClimb = true;
            airSpeed = 0;
            hitbox.x = (int) (hitbox.x / TILE_SIZE) * TILE_SIZE  + xDrawOffset;
            if (down) {
                hitbox.y += climbingSpeed;
                climbCountAni += 1.0/climbFPS;
                if (climbCountAni > 1) {
                    climbCountAni = 0;
                    aniIndex++;
                }
            }
            if (jump) {
                if (!left && !right) {
                    hitbox.y -= climbingSpeed;
                    climbCountAni += 1.0/climbFPS;
                    if (climbCountAni > 1) {
                        climbCountAni = 0;
                        aniIndex++;
                    }
                    if (!IsLadder(hitbox.x, hitbox.y - 3, levelData)) {
                        toggleLadderClimb = false;
                        ladderClimb = false;
                        jump();
                    }
                }
                else {
                    toggleLadderClimb = false;
                    jump();
                }
            }

        }
        if (!IsInLadder(hitbox, levelData)) {
            toggleLadderClimb = true;
            ladderClimb = false;
        }
        // not in air but nothing being pressed -> do nothing
        if (!inAir){
            countJump = 0;
            timeSinceGrounded = 0;
        }

        if(IsOnIce(hitbox, levelData)){
            setOnIceAccelarations();
        }
        else if(IsOnMud(hitbox, levelData)){
            setOnMudAccelarations();
        }
        else
            setDefaultAccelarations();
            
        if (canMove && ((left && !right) || (!left && right))) moving = true;
    
        float xSpeed = 0;   // prefer as delta x : to add to player position x (horizontal)
        if (canMove && !climbing) {
            if(moving == true){
                if(left){
                    leftCurrentSpeed += accelaration;
                    if(leftCurrentSpeed > maxSpeed) leftCurrentSpeed = maxSpeed;
                    isFacingLeft = true;
                    flipX = width;
                    flipW = -1;
                }
                else {
                    rightCurrentSpeed += accelaration;
                    if(rightCurrentSpeed > maxSpeed) rightCurrentSpeed = maxSpeed;
                    isFacingLeft = false;
                    flipX = 0;
                    flipW = 1;
                }

                if(System.currentTimeMillis() - timeSinceLastPlayedFootstepsSoundEffect >= 500 && IsEntityOnFloor(hitbox, levelData)){
                    game.playSoundEffect(10);
                    timeSinceLastPlayedFootstepsSoundEffect = System.currentTimeMillis();
                }

            }
        }

        leftCurrentSpeed -= friction / (inAir ? 3: 1);
        rightCurrentSpeed -= friction / (inAir ? 3: 1);
        if(leftCurrentSpeed < 0) leftCurrentSpeed = 0;
        if(rightCurrentSpeed < 0) rightCurrentSpeed = 0;

        if(canMove)
        xSpeed = - leftCurrentSpeed + rightCurrentSpeed;
        else
        xSpeed = 0;

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
                hitbox.x = ledgePos.x;
                hitbox.y = ledgePos.y;
                canMove = false;
                ledgeClimbing = true;
                climbing = false;
                inAir = false;
                countJump = 0;
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

    public void dash(){
        if(dashing == false){
            if(isFacingLeft){
                positionXAfterDash = hitbox.x - dashDistance;
                while(!CanMoveHere(positionXAfterDash, hitbox.y, hitbox.width, hitbox.height, levelData))
                    positionXAfterDash++;
            }
            else{
                positionXAfterDash = hitbox.x + dashDistance;
                while(!CanMoveHere(positionXAfterDash, hitbox.y, hitbox.width, hitbox.height, levelData))
                    positionXAfterDash--;
            }
            hitbox.y += TILE_SIZE + 5;
            hitbox.height -= TILE_SIZE + 5;
            dashing = true;
            canMove = false;
            startDashTime = System.currentTimeMillis();
            game.playSoundEffect(8);
        }
        else {
            if(isFacingLeft){
                if(hitbox.x > positionXAfterDash){
                    if(CanMoveHere(hitbox.x - dashSpeed, hitbox.y, hitbox.width, hitbox.height, levelData)){
                        hitbox.x -= dashSpeed;
                    }
                    else {
                        while(CanMoveHere(hitbox.x - 1, hitbox.y, hitbox.width, hitbox.height, levelData)){
                            hitbox.x--;
                        }
                        dashing = false;
                        canMove = true;
                        hitbox.y -= TILE_SIZE + 5;
                        hitbox.height += TILE_SIZE + 5;
                    }
                }
                else{
                    hitbox.x = positionXAfterDash;
                    dashing = false;
                    canMove = true;
                    hitbox.y -= TILE_SIZE + 5;
                    hitbox.height += TILE_SIZE + 5;
                }
            }
            else {
                if(hitbox.x < positionXAfterDash){
                    if(CanMoveHere(hitbox.x + dashSpeed, hitbox.y, hitbox.width, hitbox.height, levelData)){
                        hitbox.x += dashSpeed;
                        canMove = false;
                    }
                    else {
                        while(CanMoveHere(hitbox.x + 1, hitbox.y, hitbox.width, hitbox.height, levelData)){
                            hitbox.x++;
                        }
                        dashing = false;
                        canMove = true;
                        hitbox.y -= TILE_SIZE + 5;
                        hitbox.height += TILE_SIZE + 5;
                    }
                }
                else{
                    hitbox.x = positionXAfterDash;
                    dashing = false;
                    canMove = true;
                    hitbox.y -= TILE_SIZE + 5;
                    hitbox.height += TILE_SIZE + 5;
                }
            }
        }
    }

    public void jump(){
        countJump++;
        inAir = true;
        airSpeed = jumpSpeed;
        timeSinceGrounded = coyoteTime;
        climbing = false;
        preWallKick = false;
        game.playSoundEffect(7);
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
        game.playSoundEffect(11);
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

    private void setDefaultAccelarations(){
        accelaration = DEFAULT_ACCELARATION;
        friction = DEFAULT_FRICTION;
        maxSpeed = 3f;
    }

    private void setOnIceAccelarations(){
        accelaration = ON_ICE_ACCELARATION;
        friction = ON_ICE_FRICTION;
        maxSpeed = 4f;
    }

    private void setOnMudAccelarations(){
        accelaration = ON_MUD_ACCELARATION;
        friction = ON_MUD_FRICTION;
        maxSpeed = 1f;
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

    public int getHealth(){
        return currentHealth;
    }

    public void resetAll() {
        currentHealth = 5;
        unvulerable = false;
        airSpeed = 0;
        dashing = false;
        climbing = false;
        winning = false;
        canMove = true;
        playerAction = IDLE;
    }

    public void resetLevel1Statistics(){
        hitbox.x = 4800;
        hitbox.y = 100;
        camera.x = 4800;
        camera.y = 100;
        // hitbox.x = 1970;
        // hitbox.y = 192;
        // camera.x = 1970;
        // camera.y = 192;
        resetAll();
    }

    public void resetLevel2Statistics(){
        hitbox.x = 126;
        hitbox.y = 624;
        camera.x = 126;
        camera.y = 624;
        resetAll();
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public void setDown(boolean down) {
        this.down = down;
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

    public void setDash(boolean dash){
        this.dash = dash;
    }

    public void setWinning(boolean winning){
        this.winning = winning;
    }

    public void drawHealth(Graphics2D g2){
        int x = 10, y = 10;
        int temp = 1;

        if(unvulerable){
            if(System.currentTimeMillis() - timeSinceLastShake >= 100){
                delta_shaking = delta_shaking == 5 ? 0 : 5;
                timeSinceLastShake = System.currentTimeMillis();
            }
        }

        else delta_shaking = 0;

        for(int i = 0; i < 5; i++){
            g2.drawImage(healthImages[0], x + i * 32, y + delta_shaking * temp, 32, 32, null);
            if(unvulerable) temp *= -1;
        }

        temp = 1;

        for(int i = 0; i < currentHealth; i++){
            g2.drawImage(healthImages[1], x + i * 32, y + delta_shaking * temp, 32, 32, null);
            if(unvulerable) temp *= -1;
        }
    }

    public void gotHit(){
        unvulerable = true;
        timeSinceLastUnvulerable = System.currentTimeMillis();
        currentHealth--;
        game.startCameraShake();
        game.playSoundEffect(9);
    }

    public void showDetail() {
        System.out.println("Player position: " + hitbox.x + " " + hitbox.y);
    }

    public void activateWin(){
        winning = true;
    }
}
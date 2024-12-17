package inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import main.GamePanel;
import utilz.Constants.GameState;

@SuppressWarnings("FieldMayBeFinal")

public class Keyboard implements KeyListener {

    
    private GamePanel gamePanel;
    public Keyboard(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }
    public int upButton = KeyEvent.VK_K;
    public int downButton = KeyEvent.VK_S;
    public int leftButton = KeyEvent.VK_A;
    public int rightButton = KeyEvent.VK_D;
    public int climbButton = KeyEvent.VK_J;
    public int dashButton = KeyEvent.VK_L;

    @Override
    public void keyTyped(KeyEvent e) {}
    
    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if(code == upButton)    gamePanel.getGame().getPlayer().setJump(false);
        if(code == downButton)  gamePanel.getGame().getPlayer().setDown(false);
        if(code == leftButton)  gamePanel.getGame().getPlayer().setLeft(false);
        if(code == rightButton) gamePanel.getGame().getPlayer().setRight(false);
        if(code == climbButton) gamePanel.getGame().getPlayer().setClimb(false);
        if(code == dashButton)  gamePanel.getGame().getPlayer().setDash(false);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if(gamePanel.getGame().changingButton == true){
            if((code >= KeyEvent.VK_A && code <= KeyEvent.VK_Z) || code == KeyEvent.VK_SPACE){
                if(gamePanel.getGame().gameState == GameState.SETTING){
                    switch (gamePanel.getGame().selectedOptions) {
                        case 3 -> {
                            if(appearTwiceOrMore(code, gamePanel.getGame().selectedOptions)) return;
                            upButton = code;
                        }
                        case 4 -> {
                            if(appearTwiceOrMore(code, gamePanel.getGame().selectedOptions)) return;
                            leftButton = code;
                        }
                        case 5 -> {
                            if(appearTwiceOrMore(code, gamePanel.getGame().selectedOptions)) return;
                            dashButton = code;
                        }
                        case 6 -> {
                            if(appearTwiceOrMore(code, gamePanel.getGame().selectedOptions)) return;
                            downButton = code;
                        }
                        case 7 -> {
                            if(appearTwiceOrMore(code, gamePanel.getGame().selectedOptions)) return;
                            rightButton = code;
                        }
                        case 8 -> {
                            if(appearTwiceOrMore(code, gamePanel.getGame().selectedOptions)) return;
                            climbButton = code;
                        }
                        default -> {}
                    }

                    gamePanel.getGame().playSoundEffect(6);
                }

                else if(gamePanel.getGame().gameState == GameState.PLAYING){
                    switch (gamePanel.getGame().selectedOptions) {
                        case 0 -> {
                            if(appearTwiceOrMore(code, gamePanel.getGame().selectedOptions)) return;
                            upButton = code;
                        }
                        case 1 -> {
                            if(appearTwiceOrMore(code, gamePanel.getGame().selectedOptions)) return;
                            leftButton = code;
                        }
                        case 2 -> {
                            if(appearTwiceOrMore(code, gamePanel.getGame().selectedOptions)) return;
                            dashButton = code;
                        }
                        case 3 -> {
                            if(appearTwiceOrMore(code, gamePanel.getGame().selectedOptions)) return;
                            downButton = code;
                        }
                        case 4 -> {
                            if(appearTwiceOrMore(code, gamePanel.getGame().selectedOptions)) return;
                            rightButton = code;
                        }
                        case 5 -> {
                            if(appearTwiceOrMore(code, gamePanel.getGame().selectedOptions)) return;
                            climbButton = code;
                        }
                        default -> {
                        }
                    }

                    gamePanel.getGame().playSoundEffect(6);
                }

                gamePanel.getGame().changingButton = false;
            }

            return;
        }

        // handling every game state input
        switch (gamePanel.getGame().gameState) {
            case GameState.START_MENU -> updateStartingMenuState(code);
            case GameState.PLAYING -> updatePlayingState(code);
            case GameState.SETTING -> updateSettingState(code);
            case GameState.GUIDES -> updateGuidesState(code);
            case GameState.EXIT -> updateExitState(code);
            case GameState.CHOOSING_LEVEL -> updateChoosingLevelState(code);
            default -> {
            }
        }
        
    }

    private void updateStartingMenuState(int code){
        // HANDLING INPUT LOGIC IN STARTING MENU STATE
        if(code == KeyEvent.VK_W || code == KeyEvent.VK_UP){
            gamePanel.getGame().selectedOptions--;
            if(gamePanel.getGame().selectedOptions < 0)
                gamePanel.getGame().selectedOptions = 3;
            gamePanel.getGame().playSoundEffect(6);
        }

        if(code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN){
            gamePanel.getGame().selectedOptions++;
            if(gamePanel.getGame().selectedOptions > 3)
                gamePanel.getGame().selectedOptions = 0;
            gamePanel.getGame().playSoundEffect(6);
        }

        if(code == KeyEvent.VK_ENTER){
            switch (gamePanel.getGame().selectedOptions) {
                case GameState.PLAYING:
                    gamePanel.getGame().gameState = GameState.CHOOSING_LEVEL;
                    break;
                case GameState.SETTING:
                    gamePanel.getGame().gameState = GameState.SETTING;
                    break;
                case GameState.GUIDES:
                    gamePanel.getGame().gameState = GameState.GUIDES;
                    gamePanel.setGifVisible(0);
                    break;
                case GameState.EXIT:
                    gamePanel.getGame().gameState = GameState.EXIT;
                    break;
                default:
                    break;
            }
            gamePanel.getGame().selectedOptions = 0;
            gamePanel.getGame().playSoundEffect(6);
        }
    }

    private void updatePlayingState(int code){
        if(gamePanel.getGame().winning){
            if(code == KeyEvent.VK_ENTER){
                gamePanel.getGame().selectedOptions = 0;
                gamePanel.getGame().winning = false;

                if(gamePanel.getGame().currentLevel == 1){
                    gamePanel.getGame().playingLevel1 = false;
                    gamePanel.getGame().finishedLevel1 = true;
                    gamePanel.getGame().getPlayer().resetLevel1Statistics();
                }
                else if(gamePanel.getGame().currentLevel == 2){
                    gamePanel.getGame().playingLevel2 = false;
                    gamePanel.getGame().finishedLevel2 = true;
                    gamePanel.getGame().getPlayer().resetLevel2Statistics();
                }

                gamePanel.getGame().selectedOptions = 0;
                gamePanel.getGame().stopMusic();
                gamePanel.getGame().playMusic(0);
                gamePanel.getGame().playSoundEffect(6);
                gamePanel.getGame().menu.resetAnimationTick();
                gamePanel.getGame().menu.startingMenuTexts[0] = "New Game";
                gamePanel.getGame().gameState = GameState.START_MENU;
            }

            return;
        }

        if(gamePanel.getGame().adjustingKeyInGame){
            if(code == KeyEvent.VK_ESCAPE){
                gamePanel.getGame().selectedOptions = 0;
                gamePanel.getGame().adjustingKeyInGame = false;
                gamePanel.getGame().playSoundEffect(6);
            }

            if(code == KeyEvent.VK_ENTER){
                gamePanel.getGame().changingButton = true;
                gamePanel.getGame().playSoundEffect(6);
            }

            if(code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT){
                if(gamePanel.getGame().selectedOptions <= 2)
                    gamePanel.getGame().selectedOptions += 3;
                else    gamePanel.getGame().selectedOptions -= 3;
                gamePanel.getGame().playSoundEffect(6);
            }

            if(code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT){
                if(gamePanel.getGame().selectedOptions > 2)
                    gamePanel.getGame().selectedOptions -= 3;
                else    gamePanel.getGame().selectedOptions += 3;
                gamePanel.getGame().playSoundEffect(6);
            }

            if(code == KeyEvent.VK_W || code == KeyEvent.VK_UP){
                if(gamePanel.getGame().selectedOptions != 0 && gamePanel.getGame().selectedOptions != 3)
                    gamePanel.getGame().selectedOptions--;
                else
                    gamePanel.getGame().selectedOptions += 2;
                gamePanel.getGame().playSoundEffect(6);
            }

            if(code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN){
                if(gamePanel.getGame().selectedOptions != 2 && gamePanel.getGame().selectedOptions != 5)
                    gamePanel.getGame().selectedOptions++;
                else
                    gamePanel.getGame().selectedOptions -= 2;
                gamePanel.getGame().playSoundEffect(6);
            }

            return;
        }
        
        if(gamePanel.getGame().gameOver){
            if(code == KeyEvent.VK_ENTER){
                gamePanel.getGame().gameOver = false;
                if(gamePanel.getGame().currentLevel == 1){
                    gamePanel.getGame().playingLevel1 = false;
                    gamePanel.getGame().getPlayer().resetLevel1Statistics();
                }
                else {
                    gamePanel.getGame().playingLevel2 = false;
                    gamePanel.getGame().getPlayer().resetLevel2Statistics();
                }

                gamePanel.getGame().selectedOptions = 0;
                gamePanel.getGame().stopMusic();
                gamePanel.getGame().playMusic(0);
                gamePanel.getGame().playSoundEffect(6);
                gamePanel.getGame().menu.resetAnimationTick();
                gamePanel.getGame().menu.startingMenuTexts[0] = "New Game";
                gamePanel.getGame().gameState = GameState.START_MENU;
            }

            return;
        }

        // HANDLING INPUT LOGIC IN PLAYING STATE
        if(gamePanel.getGame().paused == false){

            if(code == upButton)    gamePanel.getGame().getPlayer().setJump(true);
            if(code == downButton)  gamePanel.getGame().getPlayer().setDown(true);
            if(code == leftButton)  gamePanel.getGame().getPlayer().setLeft(true);
            if(code == rightButton) gamePanel.getGame().getPlayer().setRight(true);
            if(code == climbButton) gamePanel.getGame().getPlayer().setClimb(true);
            if(code == dashButton)  gamePanel.getGame().getPlayer().setDash(true);
            if(code == KeyEvent.VK_Q)   gamePanel.getGame().getPlayer().showDetail();
            if(code == KeyEvent.VK_ESCAPE)  {
                gamePanel.getGame().paused = true;
                gamePanel.getGame().playSoundEffect(6);
            }

        }
        else {
            if(code == KeyEvent.VK_ESCAPE)  {
                gamePanel.getGame().paused = false;
                gamePanel.getGame().playSoundEffect(6);
            }

            if(code == KeyEvent.VK_W || code == KeyEvent.VK_UP){
                gamePanel.getGame().selectedOptions--;
                if(gamePanel.getGame().selectedOptions < 0) gamePanel.getGame().selectedOptions = 5;
                gamePanel.getGame().playSoundEffect(6);
            }
    
            if(code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN){
                gamePanel.getGame().selectedOptions++;
                if(gamePanel.getGame().selectedOptions > 5) gamePanel.getGame().selectedOptions = 0;
                gamePanel.getGame().playSoundEffect(6);
            }

            if(code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT){
                if(gamePanel.getGame().selectedOptions == 2){
                    if(gamePanel.getGame().musicVolume < 5)
                        gamePanel.getGame().musicVolume++;
                    gamePanel.getGame().sound.updateMusic();
                    gamePanel.getGame().playSoundEffect(6);
                }
                else if(gamePanel.getGame().selectedOptions == 3){
                    if(gamePanel.getGame().soundEffectVolume < 5)
                        gamePanel.getGame().soundEffectVolume++;
                    gamePanel.getGame().sound.updateSoundEffect();
                    gamePanel.getGame().playSoundEffect(6);
                }
            }

            if(code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT){
                if(gamePanel.getGame().selectedOptions == 2){
                    if(gamePanel.getGame().musicVolume > 0)
                        gamePanel.getGame().musicVolume--;
                    gamePanel.getGame().sound.updateMusic();
                    gamePanel.getGame().playSoundEffect(6);
                }
                else if(gamePanel.getGame().selectedOptions == 3){
                    if(gamePanel.getGame().soundEffectVolume > 0)
                        gamePanel.getGame().soundEffectVolume--;
                    gamePanel.getGame().sound.updateSoundEffect();
                    gamePanel.getGame().playSoundEffect(6);
                }
            }

            if(code == KeyEvent.VK_ENTER){
                switch (gamePanel.getGame().selectedOptions) {
                    case 0:
                        gamePanel.getGame().paused = false;
                        break;
                    case 1:
                        gamePanel.getGame().showFPS = !gamePanel.getGame().showFPS;
                        gamePanel.getGame().playSoundEffect(6);
                        break;
                    case 4:
                        gamePanel.getGame().adjustingKeyInGame = true;
                        gamePanel.getGame().selectedOptions = 0;
                        gamePanel.getGame().playSoundEffect(6);
                        break;
                    case 5:
                        gamePanel.getGame().selectedOptions = 0;
                        gamePanel.getGame().paused = false;
                        gamePanel.getGame().gameState = GameState.START_MENU;
                        gamePanel.getGame().stopMusic();
                        gamePanel.getGame().playMusic(0);
                        gamePanel.getGame().playSoundEffect(6);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void updateSettingState(int code){
        // HANDLING INPUT LOGIC IN SETTING MENU STATE
        if(code == KeyEvent.VK_ESCAPE){
            gamePanel.getGame().gameState = GameState.START_MENU;
            gamePanel.getGame().selectedOptions = 1;
            gamePanel.getGame().playSoundEffect(6);
        }

        if(code == KeyEvent.VK_W || code == KeyEvent.VK_UP){
            switch (gamePanel.getGame().selectedOptions) {
                case 0:
                    gamePanel.getGame().selectedOptions = 5;
                    break;
                case 6:
                    gamePanel.getGame().selectedOptions = 2;
                    break;
                default:
                    gamePanel.getGame().selectedOptions--;
                    break;
            }
            gamePanel.getGame().playSoundEffect(6);
        }

        if(code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN){
            if(gamePanel.getGame().selectedOptions == 5 || gamePanel.getGame().selectedOptions == 8)
                gamePanel.getGame().selectedOptions = 0;
            else
                gamePanel.getGame().selectedOptions++;
            gamePanel.getGame().playSoundEffect(6);
        }

        if(code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT){
            if(gamePanel.getGame().selectedOptions == 1){
                if(gamePanel.getGame().musicVolume > 0)
                    gamePanel.getGame().musicVolume--;
                gamePanel.getGame().sound.updateMusic();
                gamePanel.getGame().playSoundEffect(6);
            }
            else if(gamePanel.getGame().selectedOptions == 2){
                if(gamePanel.getGame().soundEffectVolume > 0)
                    gamePanel.getGame().soundEffectVolume--;
                gamePanel.getGame().sound.updateSoundEffect();
                gamePanel.getGame().playSoundEffect(6);
            }
            else if(gamePanel.getGame().selectedOptions >= 6){
                gamePanel.getGame().selectedOptions -= 3;
                gamePanel.getGame().playSoundEffect(6);
            } 
            else if(gamePanel.getGame().selectedOptions >= 3 && gamePanel.getGame().selectedOptions <= 5){
                gamePanel.getGame().selectedOptions += 3;
                gamePanel.getGame().playSoundEffect(6);
            }
        }

        if(code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT){
            if(gamePanel.getGame().selectedOptions == 1){
                if(gamePanel.getGame().musicVolume < 5)
                    gamePanel.getGame().musicVolume++;
                gamePanel.getGame().sound.updateMusic();
                gamePanel.getGame().playSoundEffect(6);
            }
            else if(gamePanel.getGame().selectedOptions == 2){
                if(gamePanel.getGame().soundEffectVolume < 5)
                    gamePanel.getGame().soundEffectVolume++;
                gamePanel.getGame().sound.updateSoundEffect();
                gamePanel.getGame().playSoundEffect(6);
            }
            else if(gamePanel.getGame().selectedOptions >= 3 && gamePanel.getGame().selectedOptions <= 5){
                gamePanel.getGame().selectedOptions += 3;
                gamePanel.getGame().playSoundEffect(6);
            } 
            else if(gamePanel.getGame().selectedOptions >= 6){
                gamePanel.getGame().selectedOptions -= 3;
                gamePanel.getGame().playSoundEffect(6);
            }
        }

        if(code == KeyEvent.VK_ENTER){
            if(gamePanel.getGame().selectedOptions == 0){
                gamePanel.getGame().showFPS = !gamePanel.getGame().showFPS;
                gamePanel.getGame().playSoundEffect(6);
            }
            else if(gamePanel.getGame().selectedOptions >= 2 && gamePanel.getGame().selectedOptions <= 7){
                gamePanel.getGame().changingButton = true;
                gamePanel.getGame().playSoundEffect(6);
            }
        }
    }

    private void updateGuidesState(int code){
        // HANDLING INPUT LOGIC IN GUIDES MENU STATE
        switch (code) {
            case KeyEvent.VK_ESCAPE -> {
                gamePanel.getGame().gameState = GameState.START_MENU;
                gamePanel.getGame().selectedOptions = 2;
                gamePanel.setAllGifUnvisible();
                gamePanel.getGame().playSoundEffect(6);
            }
            case KeyEvent.VK_UP, KeyEvent.VK_W -> {
                gamePanel.getGame().selectedOptions--;
                if(gamePanel.getGame().selectedOptions < 0) gamePanel.getGame().selectedOptions = 6;
                gamePanel.setGifVisible(gamePanel.getGame().selectedOptions);
                gamePanel.getGame().playSoundEffect(6);
            }
            case KeyEvent.VK_DOWN, KeyEvent.VK_S -> {
                gamePanel.getGame().selectedOptions++;
                if(gamePanel.getGame().selectedOptions > 6) gamePanel.getGame().selectedOptions = 0;
                gamePanel.setGifVisible(gamePanel.getGame().selectedOptions);
                gamePanel.getGame().playSoundEffect(6);
            }
            default -> {
            }
        }
    }

    private void updateExitState(int code){
        // HANDLING INPUT LOGIC IN EXITING STATE
        if(code == KeyEvent.VK_ESCAPE){
            gamePanel.getGame().gameState = GameState.START_MENU;
            gamePanel.getGame().selectedOptions = 3;
            gamePanel.getGame().playSoundEffect(6);
        }

        if(code == KeyEvent.VK_ENTER){
            System.exit(0);
        }
    }

    private void updateChoosingLevelState(int code){
        if(gamePanel.getGame().warning){
            if(code == KeyEvent.VK_ESCAPE){
                gamePanel.getGame().warning = false;
                gamePanel.getGame().selectedOptions = 0;
                gamePanel.getGame().playSoundEffect(6);
            }

            else if(code == KeyEvent.VK_ENTER){
                if(gamePanel.getGame().selectedOptions == 0 && gamePanel.getGame().playingLevel2){
                    gamePanel.getGame().playingLevel1 = true;
                    gamePanel.getGame().playingLevel2 = false;
                    gamePanel.getGame().getPlayer().resetLevel1Statistics();
                    gamePanel.getGame().currentLevel = 1;
                    gamePanel.getGame().stopMusic();
                    gamePanel.getGame().playMusic(1);
                }

                else if(gamePanel.getGame().selectedOptions == 1 && gamePanel.getGame().playingLevel1){
                    gamePanel.getGame().playingLevel1 = false;
                    gamePanel.getGame().playingLevel2 = true;
                    gamePanel.getGame().getPlayer().resetLevel2Statistics();
                    gamePanel.getGame().currentLevel = 2;
                    gamePanel.getGame().stopMusic();
                    gamePanel.getGame().playMusic(2);
                }

                gamePanel.getGame().loadLevel();
                gamePanel.getGame().warning = false;
                gamePanel.getGame().selectedOptions = 0;
                gamePanel.getGame().menu.startingMenuTexts[0] = "Continue";
                gamePanel.getGame().gameState = GameState.PLAYING;
            }

            return;
        }

        switch (code) {
            case KeyEvent.VK_ESCAPE -> {
                gamePanel.getGame().selectedOptions = 0;
                gamePanel.getGame().gameState = GameState.START_MENU;
                gamePanel.getGame().playSoundEffect(6);
            }
            case KeyEvent.VK_UP, KeyEvent.VK_W, KeyEvent.VK_DOWN, KeyEvent.VK_S -> {
                if(gamePanel.getGame().finishedLevel1){
                    gamePanel.getGame().selectedOptions = 1 - gamePanel.getGame().selectedOptions;
                    gamePanel.getGame().playSoundEffect(6);
                }
            }
            case KeyEvent.VK_ENTER -> {
                if((gamePanel.getGame().selectedOptions == 0 && gamePanel.getGame().playingLevel2)
                        || (gamePanel.getGame().selectedOptions == 1 && gamePanel.getGame().playingLevel1)){
                    gamePanel.getGame().warning = true;
                    gamePanel.getGame().playSoundEffect(6);
                    return;
                }   gamePanel.getGame().stopMusic();
                gamePanel.getGame().currentLevel = gamePanel.getGame().selectedOptions + 1;
                if(gamePanel.getGame().currentLevel == 1){
                    if(gamePanel.getGame().playingLevel1 == false){
                        gamePanel.getGame().loadLevel();
                    }
                    gamePanel.getGame().playingLevel1 = true;
                    gamePanel.getGame().playingLevel2 = false;
                    gamePanel.getGame().playMusic(1);
                }
                else if(gamePanel.getGame().currentLevel == 2){
                    if(gamePanel.getGame().playingLevel2 == false){
                        gamePanel.getGame().loadLevel();
                    }
                    gamePanel.getGame().playingLevel1 = false;
                    gamePanel.getGame().playingLevel2 = true;
                    gamePanel.getGame().playMusic(2);
                }   gamePanel.getGame().selectedOptions = 0;
                gamePanel.getGame().menu.startingMenuTexts[0] = "Continue";
                gamePanel.getGame().gameState = GameState.PLAYING;
            }
            default -> {
            }
        }
    }

    private boolean appearTwiceOrMore(int code, int selectedOptions){
        // check if the code is already in the code set
        int[] temp = {upButton, leftButton, dashButton, downButton, rightButton, climbButton};
        if(gamePanel.getGame().gameState == GameState.SETTING)
            temp[selectedOptions - 2] = code;
        else if(gamePanel.getGame().gameState == GameState.PLAYING)
            temp[selectedOptions] = code;
        int appear = 0;

        for(int i : temp)   if(i == code)   appear++;

        return appear >= 2;
    }
}

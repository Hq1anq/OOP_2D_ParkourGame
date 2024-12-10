package inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import main.GamePanel;

public class Keyboard implements KeyListener {

    private GamePanel gamePanel;
    public Keyboard(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }
    public int upButton = KeyEvent.VK_W;
    public int downButton = KeyEvent.VK_S;
    public int leftButton = KeyEvent.VK_A;
    public int rightButton = KeyEvent.VK_D;
    public int jumpButton = KeyEvent.VK_SPACE;
    public int climbButton = KeyEvent.VK_J;
    public int dashButton = KeyEvent.VK_L;
    public int runButton = KeyEvent.VK_U;

    @Override
    public void keyTyped(KeyEvent e) {}
    
    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if(code == upButton)    gamePanel.getGame().getPlayer().setUp(false);
        if(code == downButton)  gamePanel.getGame().getPlayer().setDown(false);
        if(code == leftButton)  gamePanel.getGame().getPlayer().setLeft(false);
        if(code == rightButton) gamePanel.getGame().getPlayer().setRight(false);
        if(code == jumpButton)  gamePanel.getGame().getPlayer().setJump(false);
        if(code == climbButton) gamePanel.getGame().getPlayer().setClimb(false);
        if(code == dashButton)  gamePanel.getGame().getPlayer().setDash(false);
        if(code == runButton) gamePanel.getGame().getPlayer().setSuper(false);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if(gamePanel.getGame().changingButton == true){
            if((code >= KeyEvent.VK_A && code <= KeyEvent.VK_Z) || code == KeyEvent.VK_SPACE){
                if(gamePanel.getGame().gameState == gamePanel.getGame().settingState){
                    switch (gamePanel.getGame().selectedOptions) {
                        case 2 -> {
                            if(appearTwiceOrMore(code, gamePanel.getGame().selectedOptions)) return;
                            upButton = code;
                        }
                        case 3 -> {
                            if(appearTwiceOrMore(code, gamePanel.getGame().selectedOptions)) return;
                            downButton = code;
                        }
                        case 4 -> {
                            if(appearTwiceOrMore(code, gamePanel.getGame().selectedOptions)) return;
                            leftButton = code;
                        }
                        case 5 -> {
                            if(appearTwiceOrMore(code, gamePanel.getGame().selectedOptions)) return;
                            rightButton = code;
                        }
                        case 6 -> {
                            if(appearTwiceOrMore(code, gamePanel.getGame().selectedOptions)) return;
                            jumpButton = code;
                        }
                        case 7 -> {
                            if(appearTwiceOrMore(code, gamePanel.getGame().selectedOptions)) return;
                            climbButton = code;
                        }
                        case 8 -> {
                            if(appearTwiceOrMore(code, gamePanel.getGame().selectedOptions)) return;
                            dashButton = code;
                        }
                        case 9 -> {
                            if(appearTwiceOrMore(code, gamePanel.getGame().selectedOptions)) return;
                            runButton = code;
                        }
                        default -> {
                        }
                    }
                }

                else if(gamePanel.getGame().gameState == gamePanel.getGame().playingState){
                    switch (gamePanel.getGame().selectedOptions) {
                        case 0 -> {
                            if(appearTwiceOrMore(code, gamePanel.getGame().selectedOptions)) return;
                            upButton = code;
                        }
                        case 1 -> {
                            if(appearTwiceOrMore(code, gamePanel.getGame().selectedOptions)) return;
                            downButton = code;
                        }
                        case 2 -> {
                            if(appearTwiceOrMore(code, gamePanel.getGame().selectedOptions)) return;
                            leftButton = code;
                        }
                        case 3 -> {
                            if(appearTwiceOrMore(code, gamePanel.getGame().selectedOptions)) return;
                            rightButton = code;
                        }
                        case 4 -> {
                            if(appearTwiceOrMore(code, gamePanel.getGame().selectedOptions)) return;
                            jumpButton = code;
                        }
                        case 5 -> {
                            if(appearTwiceOrMore(code, gamePanel.getGame().selectedOptions)) return;
                            climbButton = code;
                        }
                        case 6 -> {
                            if(appearTwiceOrMore(code, gamePanel.getGame().selectedOptions)) return;
                            dashButton = code;
                        }
                        case 7 -> {
                            if(appearTwiceOrMore(code, gamePanel.getGame().selectedOptions)) return;
                            runButton = code;
                        }
                        default -> {
                        }
                    }
                }

                gamePanel.getGame().changingButton = false;
            }

            return;
        }

        // handling every game state input
        if(gamePanel.getGame().gameState == gamePanel.getGame().startingMenuState){
            updateStartingMenuState(code);
        } 
        else if(gamePanel.getGame().gameState == gamePanel.getGame().playingState){
            updatePlayingState(code);
        } 
        else if(gamePanel.getGame().gameState == gamePanel.getGame().settingState){
            updateSettingState(code);
        } 
        else if(gamePanel.getGame().gameState == gamePanel.getGame().guidesState){
            updateGuidesState(code);
        } 
        else if(gamePanel.getGame().gameState == gamePanel.getGame().exitState){
            updateExitState(code);
        }
        
    }

    private void updateStartingMenuState(int code){
        // HANDLING INPUT LOGIC IN STARTING MENU STATE
        if(code == KeyEvent.VK_W || code == KeyEvent.VK_UP){
            gamePanel.getGame().selectedOptions--;
            if(gamePanel.getGame().selectedOptions < 0)
                gamePanel.getGame().selectedOptions = 3;
        }

        if(code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN){
            gamePanel.getGame().selectedOptions++;
            if(gamePanel.getGame().selectedOptions > 3)
                gamePanel.getGame().selectedOptions = 0;
        }

        if(code == KeyEvent.VK_ENTER){
            if(gamePanel.getGame().selectedOptions == gamePanel.getGame().playingState){
                gamePanel.getGame().gameState = gamePanel.getGame().playingState;
                gamePanel.getGame().menu.startingMenuTexts[0] = "Continue";
            }
            else if(gamePanel.getGame().selectedOptions == gamePanel.getGame().settingState){
                gamePanel.getGame().gameState = gamePanel.getGame().settingState;
            }
            else if(gamePanel.getGame().selectedOptions == gamePanel.getGame().guidesState){
                gamePanel.getGame().gameState = gamePanel.getGame().guidesState;
            }
            else if(gamePanel.getGame().selectedOptions == gamePanel.getGame().exitState){
                gamePanel.getGame().gameState = gamePanel.getGame().exitState;
            }
            gamePanel.getGame().selectedOptions = 0;
        }
    }

    private void updatePlayingState(int code){
        if(gamePanel.getGame().adjustingKeyInGame){
            if(code == KeyEvent.VK_ESCAPE){
                gamePanel.getGame().selectedOptions = 0;
                gamePanel.getGame().adjustingKeyInGame = false;
            }

            if(code == KeyEvent.VK_ENTER){
                gamePanel.getGame().changingButton = true;
            }

            if(code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT){
                if(gamePanel.getGame().selectedOptions <= 3)
                    gamePanel.getGame().selectedOptions += 4;
            }

            if(code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT){
                if(gamePanel.getGame().selectedOptions > 3)
                    gamePanel.getGame().selectedOptions -= 4;
            }

            if(code == KeyEvent.VK_W || code == KeyEvent.VK_UP){
                if(gamePanel.getGame().selectedOptions != 0 && gamePanel.getGame().selectedOptions != 4)
                    gamePanel.getGame().selectedOptions--;
                else
                    gamePanel.getGame().selectedOptions += 3;
            }

            if(code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN){
                if(gamePanel.getGame().selectedOptions != 3 && gamePanel.getGame().selectedOptions != 7)
                    gamePanel.getGame().selectedOptions++;
                else
                    gamePanel.getGame().selectedOptions -= 3;
            }

            return;
        }

        // HANDLING INPUT LOGIC IN PLAYING STATE
        if(gamePanel.getGame().paused == false){

            if(code == upButton)    gamePanel.getGame().getPlayer().setUp(true);
            if(code == downButton)  gamePanel.getGame().getPlayer().setDown(true);
            if(code == leftButton)  gamePanel.getGame().getPlayer().setLeft(true);
            if(code == rightButton) gamePanel.getGame().getPlayer().setRight(true);
            if(code == jumpButton)  gamePanel.getGame().getPlayer().setJump(true);
            if(code == climbButton) gamePanel.getGame().getPlayer().setClimb(true);
            if(code == dashButton)  gamePanel.getGame().getPlayer().setDash(true);
            if(code == runButton) gamePanel.getGame().getPlayer().setSuper(true);
            if(code == KeyEvent.VK_ESCAPE)  gamePanel.getGame().paused = true;

        }
        else {
            if(code == KeyEvent.VK_ESCAPE)  gamePanel.getGame().paused = false;

            if(code == KeyEvent.VK_W || code == KeyEvent.VK_UP){
                gamePanel.getGame().selectedOptions--;
                if(gamePanel.getGame().selectedOptions < 0) gamePanel.getGame().selectedOptions = 4;
            }
    
            if(code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN){
                gamePanel.getGame().selectedOptions++;
                if(gamePanel.getGame().selectedOptions > 4) gamePanel.getGame().selectedOptions = 0;
            }

            if(code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT){
                if(gamePanel.getGame().selectedOptions == 2){
                    if(gamePanel.getGame().volume < 5)
                    gamePanel.getGame().volume++;
                }
            }

            if(code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT){
                if(gamePanel.getGame().selectedOptions == 2){
                    if(gamePanel.getGame().volume > 0)
                    gamePanel.getGame().volume--;
                }
            }

            if(code == KeyEvent.VK_ENTER){
                switch (gamePanel.getGame().selectedOptions) {
                    case 0:
                        gamePanel.getGame().paused = false;
                        break;
                    case 1:
                        gamePanel.getGame().showFPS = !gamePanel.getGame().showFPS;
                        break;
                    case 3:
                        gamePanel.getGame().adjustingKeyInGame = true;
                        gamePanel.getGame().selectedOptions = 0;
                        break;
                    case 4:
                        gamePanel.getGame().selectedOptions = 0;
                        gamePanel.getGame().paused = false;
                        gamePanel.getGame().gameState = gamePanel.getGame().startingMenuState;
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
            gamePanel.getGame().gameState = gamePanel.getGame().startingMenuState;
            gamePanel.getGame().selectedOptions = 1;
        }

        if(code == KeyEvent.VK_W || code == KeyEvent.VK_UP){
            if(gamePanel.getGame().selectedOptions == 0)
                gamePanel.getGame().selectedOptions = 5;
            else if(gamePanel.getGame().selectedOptions == 6)
                gamePanel.getGame().selectedOptions = 1;
            else
                gamePanel.getGame().selectedOptions--;
        }

        if(code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN){
            if(gamePanel.getGame().selectedOptions == 5 || gamePanel.getGame().selectedOptions == 9)
                gamePanel.getGame().selectedOptions = 0;
            else
                gamePanel.getGame().selectedOptions++;
        }

        if(code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT){
            if(gamePanel.getGame().selectedOptions == 1){
                if(gamePanel.getGame().volume > 0)
                gamePanel.getGame().volume--;
            }
            else if(gamePanel.getGame().selectedOptions >= 6){
                gamePanel.getGame().selectedOptions -= 4;
            }
        }

        if(code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT){
            if(gamePanel.getGame().selectedOptions == 1){
                if(gamePanel.getGame().volume < 5)
                gamePanel.getGame().volume++;
            }
            else if(gamePanel.getGame().selectedOptions >= 2 && gamePanel.getGame().selectedOptions <= 5){
                gamePanel.getGame().selectedOptions += 4;
            }
        }

        if(code == KeyEvent.VK_ENTER){
            if(gamePanel.getGame().selectedOptions == 0)
                gamePanel.getGame().showFPS = !gamePanel.getGame().showFPS;
            else if(gamePanel.getGame().selectedOptions >= 2 && gamePanel.getGame().selectedOptions <= 9)
                gamePanel.getGame().changingButton = true;
        }
    }

    private void updateGuidesState(int code){
        // HANDLING INPUT LOGIC IN GUIDES MENU STATE
        if(code == KeyEvent.VK_ESCAPE){
            gamePanel.getGame().gameState = gamePanel.getGame().startingMenuState;
            gamePanel.getGame().selectedOptions = 2;
        }
    }

    private void updateExitState(int code){
        // HANDLING INPUT LOGIC IN EXITING STATE
        if(code == KeyEvent.VK_ESCAPE){
            gamePanel.getGame().gameState = gamePanel.getGame().startingMenuState;
            gamePanel.getGame().selectedOptions = 3;
        }

        if(code == KeyEvent.VK_ENTER){
            System.exit(0);
        }
    }

    private boolean appearTwiceOrMore(int code, int selectedOptions){
        // check if the code is already in the code set
        int[] temp = {upButton, downButton, leftButton, rightButton, jumpButton, climbButton, dashButton, runButton};
        if(gamePanel.getGame().gameState == gamePanel.getGame().settingState)
            temp[selectedOptions - 2] = code;
        else if(gamePanel.getGame().gameState == gamePanel.getGame().playingState)
            temp[selectedOptions] = code;
        int appear = 0;

        for(int i : temp)   if(i == code)   appear++;

        return appear >= 2;
    }
}

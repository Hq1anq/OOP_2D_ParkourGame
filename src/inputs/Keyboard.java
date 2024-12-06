package inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import main.GamePanel;

public class Keyboard implements KeyListener {

    private GamePanel gamePanel;
    public Keyboard(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }
    @Override
    public void keyTyped(KeyEvent e) {}
    
    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        switch (code) {
            case KeyEvent.VK_W -> System.out.println("UP");
            case KeyEvent.VK_A -> gamePanel.getGame().getPlayer().setLeft(false);
            case KeyEvent.VK_S -> gamePanel.getGame().getPlayer().setCrouch(false);
            case KeyEvent.VK_D -> gamePanel.getGame().getPlayer().setRight(false);
            case KeyEvent.VK_K -> gamePanel.getGame().getPlayer().setJump(false);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // handling every game state input
        if(gamePanel.getGame().gameState == gamePanel.getGame().startingMenuState){
            updateStartingMenuState(code);
        } else if(gamePanel.getGame().gameState == gamePanel.getGame().playingState){
            updatePlayingState(code);
        } else if(gamePanel.getGame().gameState == gamePanel.getGame().settingState){
            updateSettingState(code);
        } else if(gamePanel.getGame().gameState == gamePanel.getGame().guidesState){
            updateGuidesState(code);
        } else if(gamePanel.getGame().gameState == gamePanel.getGame().exitState){
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
        }
    }

    private void updatePlayingState(int code){
        // HANDLING INPUT LOGIC IN PLAYING STATE
        switch (code) {
            case KeyEvent.VK_W -> gamePanel.getGame().getPlayer().setUp(true);
            case KeyEvent.VK_A -> gamePanel.getGame().getPlayer().setLeft(true);
            case KeyEvent.VK_S -> gamePanel.getGame().getPlayer().setCrouch(true);
            case KeyEvent.VK_D -> gamePanel.getGame().getPlayer().setRight(true);
            case KeyEvent.VK_K -> gamePanel.getGame().getPlayer().setJump(true);
            case KeyEvent.VK_ESCAPE -> gamePanel.getGame().gameState = gamePanel.getGame().startingMenuState;
        }
    }

    private void updateSettingState(int code){
        // HANDLING INPUT LOGIC IN SETTING MENU STATE
        if(code == KeyEvent.VK_ESCAPE){
            gamePanel.getGame().gameState = gamePanel.getGame().startingMenuState;
        }
    }

    private void updateGuidesState(int code){
        // HANDLING INPUT LOGIC IN GUIDES MENU STATE
        if(code == KeyEvent.VK_ESCAPE){
            gamePanel.getGame().gameState = gamePanel.getGame().startingMenuState;
        }
    }

    private void updateExitState(int code){
        // HANDLING INPUT LOGIC IN EXITING STATE
        if(code == KeyEvent.VK_ESCAPE){
            gamePanel.getGame().gameState = gamePanel.getGame().startingMenuState;
        }

        if(code == KeyEvent.VK_ENTER){
            System.exit(0);
        }
    }
}

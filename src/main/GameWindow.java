package main;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import javax.swing.JFrame;

public class GameWindow {
    private final JFrame jframe;
    public GameWindow(GamePanel gamePanel) {
        jframe = new JFrame();

        jframe.setResizable(false);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.add(gamePanel);
        jframe.pack(); // Thay đổi kích thước của frame dượt vào những phần tử trong nó (trong TH này là 1 gamePanel)
        jframe.setLocationRelativeTo(null); // căn vị trí cửa số ở chính giữa bàn hình (thay vì góc trên bên phải)
        // jframe.setVisible(true);
        jframe.addWindowFocusListener(new WindowFocusListener() {

            @Override
            public void windowLostFocus(WindowEvent e) {
                gamePanel.getGame().windowFocusLost();
            }

            @Override
            public void windowGainedFocus(WindowEvent e) {
                
            }
            
        });
    }

    public void activateVisible(){
        jframe.setVisible(true);
    }
}

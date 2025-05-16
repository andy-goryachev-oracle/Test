package goryachev.research;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Panel;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 */
public class JTextArea_Egyptian {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            new JTextArea_Egyptian().startEDT();
        });
    }

    private void startEDT() {
        // 𓅂𓁹𓁋𓁨
        JTextArea ta = new JTextArea("\ud80c\udd42\ud80c\udc79\ud80c\udc4b\ud80c\udc68");
        JTextField tf = new JTextField("\ud80c\udd42\ud80c\udc79\ud80c\udc4b\ud80c\udc68");
        
        Panel p = new Panel(new BorderLayout());
        p.add(tf, BorderLayout.NORTH);
        p.add(ta, BorderLayout.CENTER);

        JFrame f = new JFrame();
        f.setTitle("Ancient Egyptian 𓅂𓁹𓁋𓁨");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setSize(400, 300);
        f.add(p);
        f.setVisible(true);
    }
}

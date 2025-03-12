package goryachev.research;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 */
public class RTF_Editor_Swing {
    public static void main(String[] args) {
        start();
    }
    
    public static void start() {
        EventQueue.invokeLater(() -> {
            new RTF_Editor_Swing().startEDT();
        });
    }

    private void startEDT() {
        JEditorPane ed = new JEditorPane("text/rtf", "");

        JPanel p = new JPanel(new BorderLayout());
        p.add(ed);

        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setSize(400, 300);
        f.add(p);
        f.setVisible(true);
    }
}

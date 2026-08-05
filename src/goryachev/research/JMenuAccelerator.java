package goryachev.research;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

// ⌨ = \u2328 
public class JMenuAccelerator {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JMenu m = new JMenu("JMenu");
            
            Object[] items = {
                KeyEvent.VK_ADD, "KeyEvent.VK_ADD",
                KeyEvent.VK_DIVIDE, "KeyEvent.VK_DIVIDE",
                KeyEvent.VK_MULTIPLY, "KeyEvent.VK_MULTIPLY",
                KeyEvent.VK_SUBTRACT, "KeyEvent.VK_SUBTRACT",
                KeyEvent.VK_DECIMAL, "KeyEvent.VK_DECIMAL",
                KeyEvent.VK_NUMPAD0, "KeyEvent.VK_NUMPAD0",
                KeyEvent.VK_NUMPAD1, "KeyEvent.VK_NUMPAD1",
                KeyEvent.VK_NUMPAD2, "KeyEvent.VK_NUMPAD2",
                KeyEvent.VK_NUMPAD3, "KeyEvent.VK_NUMPAD3",
                KeyEvent.VK_NUMPAD4, "KeyEvent.VK_NUMPAD4",
                KeyEvent.VK_NUMPAD5, "KeyEvent.VK_NUMPAD5",
                KeyEvent.VK_NUMPAD6, "KeyEvent.VK_NUMPAD6",
                KeyEvent.VK_NUMPAD7, "KeyEvent.VK_NUMPAD7",
                KeyEvent.VK_NUMPAD8, "KeyEvent.VK_NUMPAD8",
                KeyEvent.VK_NUMPAD9, "KeyEvent.VK_NUMPAD9",
            };
            
            for(int i=0; i<items.length; ) {
                int key = (Integer)items[i++];
                String name = (String)items[i++];
                
                JMenuItem mi = new JMenuItem(name);
                mi.setAccelerator(KeyStroke.getKeyStroke(key, 0));
                mi.addActionListener(e -> System.out.println(name));
                m.add(mi);
            }

            JMenuBar mb = new JMenuBar();
            mb.add(m);
            
            JFrame f = new JFrame("JMenuAccelerator");
            f.setJMenuBar(mb);
            f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            f.setSize(500, 300);
            f.setVisible(true);
        });
    }
}

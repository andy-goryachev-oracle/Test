package goryachev.research;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.KeyboardFocusManager;
import java.awt.Panel;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextField;

/**
 *
 */
public class JComboBox_Events {
    public static void main(String[] args) {
        start();
    }
    
    public static void start() {
        EventQueue.invokeLater(() -> {
            new JComboBox_Events().startEDT();
        });
    }

    private void startEDT() {
        JComboBox cb = new JComboBox();
        cb.addItem("yo");
        cb.addItem("yo2");
        cb.addItem("yo3");
        cb.addKeyListener(li("combobox"));
        cb.addMouseListener(mi("combobox"));
        cb.setEditable(true);
        Object x = cb.getEditor().getEditorComponent();
        if(x instanceof JTextField f) {
            f.addKeyListener(li("editor"));
            f.addMouseListener(mi("editor"));
        }

        Panel p = new Panel(new FlowLayout());
        p.setFocusable(true);
        p.addKeyListener(li("panel"));
        p.addMouseListener(mi("panel"));
        p.add(cb);

        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setSize(400, 300);
        f.add(p);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", (ev) -> {
            System.out.println("focusOwner=" + ev.getNewValue());
        });
        f.setVisible(true);
    }
    
    private static KeyListener li(String name) {
        return new KeyListener() {
            @Override
            public void keyTyped(KeyEvent ev) {
                p(name, ev);
            }

            @Override
            public void keyPressed(KeyEvent ev) {
                p(name, ev);
            }

            @Override
            public void keyReleased(KeyEvent ev) {
                p(name, ev);
            }
        };
    }
    
    private static MouseListener mi(String name) {
        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent ev) {
                p(name, ev);
            }

            @Override
            public void mousePressed(MouseEvent ev) {
                p(name, ev);
            }

            @Override
            public void mouseReleased(MouseEvent ev) {
            }

            @Override
            public void mouseEntered(MouseEvent ev) {
                p(name, ev);
            }

            @Override
            public void mouseExited(MouseEvent ev) {
                p(name, ev);
            }
        };
    }
    
    private static void p(String from, InputEvent ev) {
        String id;
        switch (ev.getID()) {
        case KeyEvent.KEY_PRESSED:
          id = "KEY_PRESSED";
          break;
        case KeyEvent.KEY_RELEASED:
          id = "KEY_RELEASED";
          break;
        case KeyEvent.KEY_TYPED:
          id = "KEY_TYPED";
          break;
        case MouseEvent.MOUSE_CLICKED:
            id = "MOUSE_CLICKED";
            break;
        case MouseEvent.MOUSE_ENTERED:
            id = "MOUSE_ENTERED";
            break;
        case MouseEvent.MOUSE_EXITED:
            id = "MOUSE_EXITED";
            break;
        case MouseEvent.MOUSE_PRESSED:
            id = "MOUSE_PRESSED";
            break;
        default:
          id = "?" + ev.getID();
          break;
      }
        System.out.println(
            from + ": " + id +
            " h=" + h(ev) +
            " " + (ev.isConsumed() ? "consumed??" : "")
        );
    }
    
    private static String h(Object x) {
        String s = String.valueOf(x.hashCode());
        return s.substring(s.length() - 2);
    }
}

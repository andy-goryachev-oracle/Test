package goryachev.research;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class ScrollPanel_ActionMap {
    public static void main(String[] args) throws Throwable {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                e.printStackTrace();
            }
        });
        SwingUtilities.invokeAndWait(ScrollPanel_ActionMap::createUI);
    }

    protected static void createUI() {
        int w = 2000;
        int h = 2000;
        int d = 6;
        BufferedImage im = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = im.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, w, h);
            g.setColor(Color.BLACK);
            Random r = new Random();
            for(int i=0; i<1000; i++) {
                int x = r.nextInt(w);
                int y = r.nextInt(h);
                g.drawOval(x, y, d, d);
            }
        } finally {
            g.dispose();
        }

        //JLabel c = new JLabel(new ImageIcon(im));
        JTable c = new JTable(100, 100);
        
        JScrollPane scroll = new JScrollPane(c);
        scroll.setFocusable(true);
        scroll.requestFocus();
        
        JFrame f = new JFrame();
        f.setContentPane(scroll);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setSize(400, 400);
        f.setVisible(true);
    }
}
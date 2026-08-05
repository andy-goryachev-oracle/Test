package goryachev.research;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

// https://bugs.openjdk.org/browse/JDK-8387267
public class TestAutoResizeLastColumn {

    public static class ScrollableJTable extends JPanel {
        public ScrollableJTable() {
            setLayout(new BorderLayout());
            JTable table = new JTable(10, 2);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
            JScrollPane pane = new JScrollPane(table);
            add(pane, BorderLayout.CENTER);
        }
    }

    public static void main(String[] args) throws Exception {
        IO.println(System.getProperty("java.vm.version"));
        int n = 0;
        switch(n) {
        case 0:
            SwingUtilities.invokeAndWait(TestAutoResizeLastColumn::testLastColumnOnly);
            break;
        case 1:
            SwingUtilities.invokeLater(() -> {
                JPanel panel = new ScrollableJTable();
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setContentPane(panel);
                frame.pack();
                frame.setVisible(true);
            });
        }
    }

    private static void testLastColumnOnly() {
        JTable table = new JTable(3, 3);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        table.setGridColor(Color.GRAY);

        // uncomment this and all works in master
        //table.getTableHeader().setResizingColumn(null);

        TableColumnModel cm = table.getColumnModel();
        for (int i = 0; i < cm.getColumnCount(); i++) {
            TableColumn c = cm.getColumn(i);
            c.setHeaderValue("C" + i);
        }

        JFrame f = new JFrame();
        f.setSize(500, 300);
        f.getContentPane().add(new JScrollPane(table));
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
}
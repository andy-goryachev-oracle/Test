package goryachev.research;
import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class JTableTest extends JFrame {
    protected JTable table;
    protected JCheckBox showCheckbox;
    
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            new JTableTest().setVisible(true);
        });
    }
    
    public JTableTest() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(400, 300);
        
        Object[][] rows = {
            new Object[] {
                1, 2, 3, 4
            },
            new Object[] {
                5, 6, 7, 8
            },
            new Object [] {
                9, 10, 11, 12
            }
        };
        
        Object[] columns = {
            "Col1", "Col2", "Col3", "Col4"
        };
        
        showCheckbox = new JCheckBox("show last column");
        showCheckbox.setSelected(true);
        showCheckbox.addActionListener((ev) -> update());
        
        table = new JTable(rows, columns);
        table.setCellSelectionEnabled(true);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        JPanel bp = new JPanel(new BorderLayout());
        bp.add(new JScrollPane(table));
        bp.add(showCheckbox, BorderLayout.SOUTH);
        getContentPane().add(bp);
    }
    
    
    protected void update() {
        TableColumnModel m = table.getTableHeader().getColumnModel();
        boolean on = showCheckbox.isSelected();
        if(on) {
            m.addColumn(new TableColumn(m.getColumnCount()));
        }
        else {
            m.removeColumn(m.getColumn(m.getColumnCount() - 1));
        }
    }
}

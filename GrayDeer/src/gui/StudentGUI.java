package gui;

/*
 * SimpleTableDemo.java requires no other files.
 */
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class StudentGUI extends JPanel {

    private boolean DEBUG = false;

    public StudentGUI() {
        super(new GridLayout(1, 0));

        String[] columnNames = {"Assignment Name",
            "Status",
            "Actions",
            "Grade"
        };

        Object[][] data = {
            {"Homework 1", "Homework Uploaded",
                "See Notes", new Double(5.0)},
            {"Homework 2", "Deadline: 14/04/2013",
                "Upload", new Double(0.0)},
            {"Homework 3", "TBA",
                "-", new Double(0.0)}
        };

        final JTable table = new JTable(data, columnNames) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        ;
        };
                
                
       
		table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);


        if (DEBUG) {
            table.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    printDebugData(table);
                }
            });
        }

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);

        //Add the scroll pane to this panel.
        add(scrollPane);
        
        //createAndShowGUI();
    }

    private void printDebugData(JTable table) {
        int numRows = table.getRowCount();
        int numCols = table.getColumnCount();
        javax.swing.table.TableModel model = table.getModel();

        System.out.println("Value of data: ");
        for (int i = 0; i < numRows; i++) {
            System.out.print("    row " + i + ":");
            for (int j = 0; j < numCols; j++) {
                System.out.print("  " + model.getValueAt(i, j));
            }
            System.out.println();
        }
        System.out.println("--------------------------");
    }

    /**
     * Create the GUI and show it. For thread safety, this method should be
     * invoked from the event-dispatching thread.
     */
    public static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("GrayDeer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.setSize(600, 500);

        //Create and set up the content pane.
        StudentGUI newContentPane = new StudentGUI();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);


        //Menubar exampless
        JMenuBar mb = new JMenuBar();
        frame.setJMenuBar(mb);
        JMenu m = new JMenu("File");
        mb.add(m);

        addItem(m, "Open", KeyEvent.VK_O);
        addItem(m, "Save", KeyEvent.VK_S);
        addItem(m, "Save As", KeyEvent.VK_A);
        addItem(m, "Import", KeyEvent.VK_I);
        addItem(m, "Export", KeyEvent.VK_E);
    
        
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    private static void addItem(JMenu m, String name, int accelerator) {
        JMenuItem mi = new JMenuItem(name);
        mi.setAccelerator(KeyStroke.getKeyStroke(accelerator,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        m.add(mi);
    }

    

}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prisonGUI;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author mjgaj
 */
public class SettingsPanel extends JPanel{
    private final int WIDTH = 900;
    private final int HEIGHT = 900;
    
    public SettingsPanel(){
        setFocusable(true);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        createButton("Back", RIGHT_ALIGNMENT, TOP_ALIGNMENT, event -> {
            setVisible(false);
            displayPrisonGeneratorPanel();
        });
    }
    
    private void createButton(String title, float xAlignment, float yAlignment, ActionListener listener) {
        JButton button = new JButton(title);
        button.setAlignmentX(xAlignment);
        button.setAlignmentY(yAlignment);
        button.addActionListener(listener);
        add(button);
    }
    
    
    public void displayPrisonGeneratorPanel() {

        setVisible(false);
        EventQueue.invokeLater(()
                -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            PrisonGeneratorPanel prisonPanel = new PrisonGeneratorPanel();
            frame.add(prisonPanel);
            prisonPanel.requestFocus();
            frame.pack();
        });

    }
    
}

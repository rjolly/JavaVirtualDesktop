 /* Modified to be an InternalFrame by Scott Griswold 1/98 */

import java.awt.*;
import java.awt.event.*;
import com.sun.java.swing.*;
import com.sun.java.swing.preview.*;

/**
 * An application that displays a JButton and several JRadioButtons.
 * The JRadioButtons determine the look and feel used by the application.
 */
public class SetLnF extends JInternalFrame {

    static JInternalFrame frame;
    static String jlf = "Java";
    static String windows = "Windows";
    static String motif = "Motif";
    
    JRadioButton jlfButton, windowsButton, motifButton;

    public SetLnF() {
    	
    	JLabel label1 = new JLabel("Look and Feel");
			
			JVDDisplay parent = (JVDDisplay)getDesktopPane();
			
			// Create the buttons.
			jlfButton = new JRadioButton(jlf);
      //jlfButton.setMnemonic('j'); 
			jlfButton.setActionCommand(parent.JAVALF);

			windowsButton = new JRadioButton(windows);
      //windowsButton.setMnemonic('b'); 
			windowsButton.setActionCommand(parent.WINDOWSLF);

			motifButton = new JRadioButton(motif);
      //motifButton.setMnemonic('m'); 
			motifButton.setActionCommand(parent.MOTIFLF);

			// Group the radio buttons.
			ButtonGroup group = new ButtonGroup();
			group.add(jlfButton);
			group.add(windowsButton);
			group.add(motifButton);

        // Register a listener for the radio buttons.
			RadioListener myListener = new RadioListener();
			jlfButton.addActionListener(myListener);
			windowsButton.addActionListener(myListener);
			motifButton.addActionListener(myListener);

			// put this on a panel and put the panel in an internalframe
			JPanel panel1 = new JPanel();
			panel1.add(label1);
			panel1.add(jlfButton);
			panel1.add(windowsButton);
			panel1.add(motifButton);
			

			getContentPane().setLayout(new BorderLayout());		
			getContentPane().add("Center", panel1);

			//Color color = JColorChooser.showDialog(SwingSet.this, "Color Chooser", getBackground());
			
			setTitle("Set Look and Feel");
			setSize(400,100);
			setMaximizable(false);
			setIconifiable(false);	// drag onto desktop to make dissappear
			setResizable(true);	
			setClosable(true);
			setVisible(true);      
			updateButtonState();
    }

		public Dimension getMinimumSize(JComponent c) {
			return new Dimension(400,100);
		}
  
		public Dimension getPreferredSize(JComponent c) {
			return new Dimension(400,100);
		}

		public void updateButtonState() {
	 		String lnfName = UIManager.getLookAndFeel().getClass().getName();
	 		if (lnfName.indexOf(jlf) >= 0) {
	     jlfButton.setSelected(true);
	 		} else if (lnfName.indexOf(windows) >= 0) {
	     windowsButton.setSelected(true);
	 		} else if (lnfName.indexOf(motif) >= 0) {
	     motifButton.setSelected(true);
	 		} else {
	     System.err.println("SetLnF if using an unknown L&F: "+ lnfName);
	 		}
    }
    
		
    /** An ActionListener that listens to the radio buttons. */
 		class RadioListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
	    	String lnfName = e.getActionCommand();

      	try {
					UIManager.setLookAndFeel(lnfName);
					// update entire desktop pane which is parent of this
					SwingUtilities.updateComponentTreeUI(getDesktopPane());
					((JVDDisplay)getDesktopPane()).resetManager();
      	} 
	    	catch (Exception exc) {
					JRadioButton button = (JRadioButton)e.getSource();
					button.setEnabled(false);
					updateButtonState();
      		System.err.println("Could not load LookAndFeel: " + lnfName);
      	}
			}
  	}
  	
	  public static void main(String s[]) {
	 		SetLnF panel = new SetLnF();      
   }
}
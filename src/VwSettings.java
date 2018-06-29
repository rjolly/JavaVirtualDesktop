import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/* Utility for changing desktop manager and virtual window features

 */
public class VwSettings extends JInternalFrame {

    static JInternalFrame frame;
    
    JRadioButton jlfButton, windowsButton, motifButton;

    public VwSettings() {
    	
			JVDDisplay parent = (JVDDisplay)getDesktopPane();
			
			// put this on a panel and put the panel in an internalframe
			JLabel label2 = new JLabel("virtual window scale");
			JComboBox scalecombo = new JComboBox();
			scalecombo.addItem("5%");
			scalecombo.addItem("10%");
			scalecombo.setSelectedItem("10%");
			ScaleListener sl = new ScaleListener();
			scalecombo.addItemListener(sl);
			
			JPanel panel2 = new JPanel();
			panel2.add(label2);		
			panel2.add(scalecombo);
			
			JLabel label3 = new JLabel("virtual window location");
			JComboBox locationcombo = new JComboBox();
			locationcombo.addItem("top left");
			locationcombo.addItem("top right");
			locationcombo.addItem("bottom left");
			locationcombo.addItem("bottom right");
			locationcombo.setSelectedItem("top left");

			JPanel panel3 = new JPanel();
			LocationListener ll = new LocationListener();
			locationcombo.addItemListener(ll);
			
			panel3.add(label3);		
			panel3.add(locationcombo);
			
			JLabel label4 = new JLabel("number of logical screens");
			JComboBox screencombo = new JComboBox();
			screencombo.addItem("2");
			screencombo.addItem("4");
			screencombo.addItem("6");
			screencombo.addItem("8");
			screencombo.setSelectedItem("6");

			JPanel panel4 = new JPanel();
			ScreenListener scl = new ScreenListener();
			screencombo.addItemListener(scl);
			
			panel4.add(label4);		
			panel4.add(screencombo);
					
			JLabel label5 = new JLabel("Select Background Color");
			JButton colorButton = new JButton("choose");
			ButtonListener bl = new ButtonListener();
			colorButton.addActionListener(bl);
			
			JPanel panel5 = new JPanel();
			panel5.add(label5);
			panel5.add(colorButton);

			getContentPane().setLayout(new GridLayout(4,1));		
			getContentPane().add(panel2);
			getContentPane().add(panel3);
			getContentPane().add(panel4);
			getContentPane().add(panel5);

			//Color color = JColorChooser.showDialog(SwingSet.this, "Color Chooser", getBackground());
			
			setTitle("VW settings");
			setSize(400,280);
			setMaximizable(false);
			setIconifiable(false);	// drag onto desktop to make dissappear
			setResizable(false);	
			setClosable(true);
			setVisible(true);      
		}
		
  	class ScaleListener implements ItemListener {
			public void itemStateChanged(ItemEvent e) {
	    	String scale = (String)e.getItem();
	    	if(e.getStateChange() == ItemEvent.SELECTED) {	    		
	    		try {
	    			int value = Integer.parseInt(scale.substring(0,scale.length()-1));	    		
						((JVDDisplay)getDesktopPane()).setVwScale(value);
					}
					catch(NumberFormatException nfe) {;}// no action 
	    	}
	    }
	  }
	    
    class LocationListener implements ItemListener {
			public void itemStateChanged(ItemEvent e) {
	    	String location = (String)e.getItem();
	    	if(e.getStateChange() == ItemEvent.SELECTED) {	    		
	    		((JVDDisplay)getDesktopPane()).setVwLocation(location);
				}
	    }
	  }
	  
  	class ScreenListener implements ItemListener {
			public void itemStateChanged(ItemEvent e) {
	    	String screen = (String)e.getItem();
	    	if(e.getStateChange() == ItemEvent.SELECTED) {	    		
	    		try {
	    			int value = Integer.parseInt(screen);	    		
						((JVDDisplay)getDesktopPane()).setVwNumberOfScreens(value);
					}
					catch(NumberFormatException nfe) {;}// no action 
	    	}
	    }
	  }	  
	  
	  class ButtonListener implements ActionListener {
	  	public void actionPerformed(ActionEvent e) {
    		if(e.getActionCommand() == "choose")  {   				
   				((JVDDisplay)getDesktopPane()).setBackgroundColor(JColorChooser.showDialog(VwSettings.this, "Color Chooser", getBackground()));
   				((JVDDisplay)getDesktopPane()).resetManager();
   				//bug repaints component at 0,0, this corrects that
   				VwSettings.this.setLocation(VwSettings.this.getLocation());
    		}
    	}
  	}	  
	  
	  public static void main(String s[]) {
	 		VwSettings panel = new VwSettings();      
   }
}
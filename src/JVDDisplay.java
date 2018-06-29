import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.lang.*;
import java.io.*;
import java.beans.*;
import javax.swing.*;

// Scott Griswold  4/98
// Java Portable Desktop Manager
// this handles most of the actions for the desktop manager
// make an virtual desktop display to go into a JFrame

public class JVDDisplay extends JDesktopPane implements MouseListener, ActionListener {

	static String WINDOWSLF = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
	static String MOTIFLF = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
	static String JAVALF = "com.sun.java.swing.plaf.metal.MetalLookAndFeel";
		
	static int APPLAYER = 5;
	static int VWLAYER = 10;
	static int ICONLAYER = 15;
		
	private int initial_offset = 10;
	private int initial_x = 0;
	private int initial_y = 0;
	private int scale = 10; // a multipler 100/scale = % 
	
	private String[] programs;
	private JVDDesktopManager desktopmanager;

	private JPopupMenu popup;
		
	private Vector app_vector = null;	// initialized in constructor
	private IconManager icon_manager;
	private Class parms[]= null;
	
	private JVDSetup jvdsetup;	
	
	private JVDVwData vwdata;
	private JVDVirtualWindow vw;
	
	private JFrame parentframe;
	
	private VwIcon ico;
		
	public JVDDisplay(JFrame f) {
    	
  	parentframe = f;
		app_vector = new Vector(); // holds application objects created when load applications	  	
		icon_manager = new IconManager(this);
	  
		// create the virtual window to display on the desktop
  	vwdata = new JVDVwData();
		setVwLocation(vwdata.TOP_LEFT);
		vw = vwdata.getVirtualWindow();
		scale = vwdata.getGridScale();	  		     
		desktopmanager =new JVDDesktopManager(icon_manager, vwdata);	    
		vw.setParent(this);
		
		vw.add(createPopup());	//component popup appears over    	
		add(vw, new Integer(10));
	  
	  setInitialLocation();
	  			  	
		setBackground(SystemColor.desktop);	//jdk1.1 feature to match OS
		setOpaque(true);	//needed if set background color
	    	    
		enableEvents(AWTEvent.MOUSE_EVENT_MASK); // component handling popup actions			
 		addMouseListener(this);	 		 			
	} // end constructor
    
  private JPopupMenu createPopup() {
		// Create the popup menu using a loop.  Note the separation of menu
    popup = new JPopupMenu();           // Create the menu
    jvdsetup = new JVDSetup();
    // this should throw exception if file is not found and put up a filedialog
    //programs = jvdsetup.readFile(filename);
 		String filename = "jvd.txt";
 		Vector p = jvdsetup.readFile(filename);
    programs = new String[p.size()];
    for(int j=0;j<p.size();j++)
    	programs[j] = ((String)p.elementAt(j));
  	boolean done = true;
		boolean submenu_done = false;
		String main_item = null;
		String sub_item = null;
		int n=0;
		if(programs.length !=0) {
			done = false;
			main_item = programs[n++];	
		}
		while(!done) {
			if(main_item == null) 
				done = true;
			else {
				if(!main_item.endsWith(":")) { //main menu item
					JMenuItem mi = new JMenuItem(main_item);
					mi.setActionCommand(main_item);
					mi.addActionListener(this);
					popup.add(mi);			
					if(n<programs.length) 
						main_item = programs[n++];	
					else done = true;
				}
				else { // sub menu item
					submenu_done = false;
					JMenu mu = new JMenu(main_item);
					mu.setActionCommand(main_item);
					mu.addActionListener(this);
					popup.add(mu);
					// look for all items to add to submenu
					if(n<programs.length) {
						sub_item = programs[n++];			
						while(!submenu_done) { // add submenu items
							JMenuItem mi = new JMenuItem(sub_item);
							mi.setActionCommand(sub_item);
							mi.addActionListener(this);  
							mu.add(mi); 
							if(n<programs.length) {
								sub_item = programs[n++];			
								if(sub_item.compareTo("end")==0) {
									submenu_done = true;
									if(n<programs.length) 
										main_item = programs[n++];	
									else done = true;
								}
							}
							else 	{ // no more items
								submenu_done = true;
								done = true;
							}
						}
					}
					else done = true;				
				}
			}
		}
		return(popup);
	}
	  
	private void setDefaultLnF() {
			
		String lnfDefault = null;
		String osName = System.getProperty("os.name");
			
		if ((osName != null) && osName.indexOf("Windows") != -1) {
	    	lnfDefault = WINDOWSLF;
		}
		else if ((osName != null) && osName.indexOf("Unix") != -1) {
				lnfDefault = MOTIFLF;
			}		
			else //if ((osName != null) && osName.indexOf("Macintosh") != -1) {
	    		lnfDefault = JAVALF; // should work for all
		//	}
			try {
				UIManager.setLookAndFeel(lnfDefault);
				// update entire desktop pane which is parent of this
				SwingUtilities.updateComponentTreeUI(this); //needed to work
				resetManager();
			}
			catch (Exception exc) {
		  		System.err.println("Could not load LookAndFeel: " + lnfDefault);
			}			
		}

		// this sets size for this frame
	public Dimension getPreferredSize() {
	 		return(new Dimension(vwdata.getScreenWidth(),vwdata.getScreenHeight()));
  		//return(new Dimension(800,400));
 	}
 		
 	public Dimension getMinimumSize() {
  		return(vwdata.getMinimumSize());
 	}
 		
 	public Dimension getMaximumSize() {
 		return(new Dimension(vwdata.getScreenWidth(),vwdata.getScreenHeight()));
 	}
 		
 	public void setBackgroundColor(Color c) {
 		setBackground(c);
 	}
 	
 	public void resetManager() {	//used by SetLnF object
  	setDesktopManager(desktopmanager);
  	repaint();
	}

	public DesktopManager getDesktopManager() {
		return desktopmanager;
	}	
	// set location of virtual window
	public void setVwLocation(int location) {
		vwdata.setGridLocation(location);			
	}

	public int getVwLocation() {
		return 1;
	}

	// set location of virtual window, will not change scale
	public void setVwLocation(String location) {
		int loc = 1;
		Point old_vw_origin = vwdata.getGridOrigin();
		if(location.startsWith("top left")) {
			loc = vwdata.TOP_LEFT;
		} else if(location.startsWith("top right")) {
			loc = vwdata.TOP_RIGHT;
		} else if(location.startsWith("bottom left")) {
			loc = vwdata.BOTTOM_LEFT;
		}else if(location.startsWith("bottom right")) {
			loc = vwdata.BOTTOM_RIGHT;
		}
		vwdata.setGridLocation(loc);
		setAllIconLocation(old_vw_origin, scale);
		setInitialLocation();
	}
		
 	public void setVwNumberOfScreens(int num) {
		Point old_vw_origin = vwdata.getGridOrigin();
 		vwdata.setNumberOfScreens(num);
		setAllIconLocation(old_vw_origin, scale);			
	}

	// set the scale of the virtual window in percent of monitor size
	// this will cause virtual window origin to change also
	public void setVwScale(int value) {
		int oldscale = scale;
		scale = 100/value;
		Point old_vw_origin = vwdata.getGridOrigin();
		vwdata.setGridScale(scale);
		setAllIconLocation(old_vw_origin, oldscale);
		setInitialLocation();	
	}

	public void setInitialLocation() {
		Rectangle r = vw.getBounds();
		if(r.x<100)
			initial_x = r.x + r.width + initial_offset;
		else 
			initial_x = initial_offset;
		initial_y = initial_offset;	  
	}
 	  
 	// reposition icons in response to virtual window size or location change
 	public void setAllIconLocation(Point old_vw_origin, int oldscale) {
 		JInternalFrame[] app_list = icon_manager.getAllApps();
		for(int i=0;i<app_list.length;i++) {
			// set location of all icons relative to grid clicked
		  Point app_origin = app_list[i].getLocation();
			Point cell_origin = vwdata.getCellInFocusOrigin();
		  Point vw_origin = vwdata.getGridOrigin();
		  JComponent icon = icon_manager.getIconObject(app_list[i]);
		  icon.setLocation(app_origin.x/scale+cell_origin.x, app_origin.y/scale+cell_origin.y);
		}
 		repaint();
	}
		
	public Rectangle getIconForFrame(JInternalFrame f) {
		if(f !=null) {
			Point app_origin = f.getLocation();		  
		  Point cell_origin = vwdata.getCellInFocusOrigin();
			Dimension d = f.getSize();
		  return new Rectangle(app_origin.x/scale + cell_origin.x, app_origin.y/scale + cell_origin.y, d.width/scale, d.height/scale);
		}
		return new Rectangle(0,0,0,0);
	}
	
	public void processMouseEvent(MouseEvent e) {
		if (e.isPopupTrigger()) //&& e.getX()<vwdata.width && e.getY()<vwdata.height) 
    	  // If popup trigger over jvd, pop up the menu.
     		popup.show(this, e.getX(), e.getY());
 		else super.processMouseEvent(e);  // Pass other event types on.
	}		
			    		   	    					 	 
	public void popupMenu(MouseEvent e) {
		popup.show(this, e.getX(), e.getY());
 	}		
 		
 	public void mousePressed(MouseEvent e) {}
	 	    
 	public void mouseEntered(MouseEvent me) {}
 	public void mouseExited(MouseEvent me) {}
	public void mouseClicked(MouseEvent me) {}
	public void mouseReleased(MouseEvent me) {}

 	 /* This is the ActionListener method invoked by the menu items */
  public void actionPerformed(ActionEvent event) {
   	// Get the "action command" of the event, and dispatch based on that.
   	// This method calls a lot of the interesting methods in this class.
   	//String command = event.getActionCommand();
   	// set programs initial location on screen
		Point cell_origin = vwdata.getCellInFocusOrigin();
		Point vw_origin = vwdata.getGridOrigin();
		// uses class loader to load file from it's name					
		// assumed to be JInternalFrame class
		try {	
		Class c = Class.forName(event.getActionCommand());
		JInternalFrame fr = (JInternalFrame)c.newInstance(); 
      // set manager properties					
			fr.setLocation(initial_x, initial_y);
     	fr.setVisible(true);    		
			add(fr, new Integer(APPLAYER)); 
		 	try { fr.setSelected(true); } 
			catch (java.beans.PropertyVetoException e2) {}			
     	// create icon 
			Point p = fr.getLocation(); 
			Dimension d = new Dimension(fr.getSize());				      		
	  	ico = new VwIcon(fr, vw);
			ico.setBounds(p.x/scale+cell_origin.x, p.y/scale+cell_origin.y, d.width/scale, d.height/scale);
			add(ico, new Integer(ICONLAYER));
			icon_manager.addEntry(fr, ico);
			repaint();
		}
		catch(ReflectiveOperationException e) {
     	System.out.println("loading internal frame exception " + e);
     	JOptionPane.showInternalMessageDialog(this, "Could not load class", "Desktop Manager", JOptionPane.INFORMATION_MESSAGE);
    }
		//catch (java.lang.IllegalAccessException e)  {}          	
    //catch (java.lang.InstantiationException e) {
    //	JOptionPane.showInternalMessageDialog(this, "information", "information", JOptionPane.INFORMATION_MESSAGE);
		//}           			
 	} // end method
  	
	// location of frame that this is in. Used to determine when apps are on grid
	public Point getFrameOrigin() {
		return parentframe.getLocation();		
	}
		
	// adds an object to be managed
	public void startProgram(JInternalFrame fr) {
		// set programs initial location on screen
		if(fr == null)
			JOptionPane.showInternalMessageDialog(this, "Could not load class", "Desktop Manager", JOptionPane.INFORMATION_MESSAGE);
		else {
		// set manager parameters
		fr.setLocation(initial_x, initial_y);
		fr.setVisible(true);
		add(fr, new Integer(APPLAYER));					      		
	 	try { fr.setSelected(true); } 
		catch (java.beans.PropertyVetoException e2) {}			
  	// set icon parameters
		Point p = fr.getLocation(); 
		Dimension d = new Dimension(fr.getSize());				      		
		ico = new VwIcon(fr, vw);
		Point cell_origin = vwdata.getCellInFocusOrigin();
		Point vw_origin = vwdata.getGridOrigin();
		ico.setBounds(p.x/scale+cell_origin.x, p.y/scale+cell_origin.y, d.width/scale, d.height/scale);
		add(ico, new Integer(ICONLAYER));	  	
		icon_manager.addEntry(fr, ico);
		repaint();			
		}
	}	
	
} // end class
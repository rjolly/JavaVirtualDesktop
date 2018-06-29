import com.sun.java.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

// Scott Griswold  4/98
// Java Portable Desktop Manager
// this handles all the events of moving, resizing, ect. either an application
// or an icon since both are InternalFrames on DesktopPane

public class JVDDesktopManager extends DefaultDesktopManager {

	private IconManager icon_manager = null;
	private JVDVwData vwdata = null;
	private JDesktopPane parent = null;

	public JVDDesktopManager(IconManager a, JVDVwData b) {
		super();
		icon_manager = a;
		vwdata = b;
	}

	public void activateFrame(JInternalFrame f) {
		JDesktopPane dtp = (JDesktopPane)f.getDesktopPane();
		if(dtp == null)
			System.out.println("Desktop Manager activateFrame - desktopPane is null"); // always get null
		else {
			//activate(f);		
			JInternalFrame frames[] = dtp.getAllFrames();
			int i = 0;;
			for(i=0;i<frames.length;i++) {
				dtp.setLayer(frames[i], 5); //bug fix reset layers from JVDDisplay
			}
		}
		drawVw();
		super.activateFrame(f);
	}
	
	public void activate(JInternalFrame frame) {
	}	

	public void dragFrame(JComponent j, int x, int y) {
		//JComponent should be the internal frame that was dragged
		//j.getDesktopPane() should be the DesktopPane that owns the internalframe
		JInternalFrame f = (JInternalFrame)j;
		JDesktopPane dtp = (JDesktopPane)f.getDesktopPane();
		if(dtp == null)
			System.out.println("dragFrame - desktop null");
		else {
			drag(f, new Point(x,y));
		}		
		super.dragFrame(j,x,y);
	}

	public void drag(JInternalFrame frame, Point p) {
	 	//always use app location and cell origin to determine moves
	 	int scale = vwdata.getGridScale();
	  Point app_origin = frame.getLocation();
		Point cell_origin = vwdata.getCellInFocusOrigin();
	  Point vw_origin = vwdata.getGridOrigin();
	  JComponent icon = icon_manager.getIconObject(frame);
	  icon.setLocation(app_origin.x/scale+cell_origin.x, app_origin.y/scale+cell_origin.y);
		drawVw();
	}

public void dragIcon(JComponent icon, Point p) {
	 	JInternalFrame f = null;
	 	int scale = vwdata.getGridScale();
	  f = icon_manager.getAppObject(icon);
	  Rectangle icon_origin = icon.getBounds();
		Point cell_origin = vwdata.getCellInFocusOrigin();
		Point vw_origin = vwdata.getGridOrigin();
		f.setLocation(new Point(((icon_origin.x-vw_origin.x)-(cell_origin.x-vw_origin.x))*scale, ((icon_origin.y-vw_origin.y)-(cell_origin.y-vw_origin.y))*scale));
		drawVw();
	}
	
		// paint cell in focus and all app on that cell
	public void setAllIconLocation(MouseEvent e) {
	 	int scale = vwdata.getGridScale();
		JComponent[] icon_list = icon_manager.getAllIcons();
		for(int i=0;i<icon_list.length;i++) {
			// set location of all apps relative to grid clicked
			// this will display the apps in the cell selected
			if(icon_list[i] != null) {
  			Point icon_origin = icon_list[i].getLocation();
				Point cell_origin = vwdata.getCellInFocusOrigin();
				icon_manager.getAppObject(icon_list[i]).setLocation((icon_origin.x-cell_origin.x)*scale, (icon_origin.y-cell_origin.y)*scale);
  		}
	  }
	  drawVw();	
	}	  		
	  

	public void resizeFrame(JComponent j, int x, int y, int w, int h) {
		JInternalFrame f = (JInternalFrame)j;
		JDesktopPane dtp = (JDesktopPane)f.getDesktopPane();
		if(dtp == null)
			System.out.println("resize - desktop null");
		else {
			resize(f,new Dimension(w,h));
		}		
		super.resizeFrame(j,x,y,w,h);
	}
	
	public void maximizeFrame(JInternalFrame f) {
		super.maximizeFrame(f); // size change after call super	
		JDesktopPane dtp = (JDesktopPane)f.getDesktopPane();
		if(dtp == null)
			System.out.println("resize - desktop null");
		else {
			resize(f,f.getSize());
		}		
	}
	
	public void minimizeFrame(JInternalFrame f) {
		super.minimizeFrame(f); // size change after call super	
		JDesktopPane dtp = (JDesktopPane)f.getDesktopPane();
		if(dtp == null)
			System.out.println("resize - desktop null");
		else {
			resize(f,f.getSize());
		}		
	}
	
	public void resize(JInternalFrame frame, Dimension d) {
		drawVw();		
	}	
	
	public void closeFrame(JInternalFrame f) {		
		// remove icon and frame
		parent = (JDesktopPane)f.getDesktopPane();
		//check if dialog box without an icon
		if(icon_manager.getIconObject(f) != null)
			parent.remove(icon_manager.getIconObject(f));
		icon_manager.removeAppEntry(f);
		System.out.println("closed "+f);
		drawVw();
		super.closeFrame(f);
	}
	
	
	public void drawVw() {
		vwdata.getVirtualWindow().repaint();
	}
	
}
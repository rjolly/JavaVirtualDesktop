import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

// Scott Griswold  4/98
// Java Portable Desktop Manager
// class for icons on virtual desktop

class VwIcon extends JComponent implements MouseListener, MouseMotionListener { // I dont think this worked when it was a JPanel
	
	private JInternalFrame parent;
	private JVDVirtualWindow vw;
	private boolean transparent = true;
	private int icon_layer;
	private int x=0, y=0;
	Rectangle start_position;
	  
  public VwIcon(JInternalFrame p) {
  	parent = p;
		String title = p.getTitle();
  	if(title == null)
  		title = "no title";
  	setToolTipText(title);
  	start_position = new Rectangle(0,0,0,0); //default
		addMouseListener(this);
		addMouseMotionListener(this);
	}						

  public VwIcon(JInternalFrame p, JVDVirtualWindow v) {
  	this(p);
  	vw = v;
  }						
	
	public void setOpaque(boolean b) {
		if(b)
			transparent = true;
		else
			transparent = false;
	}

  public void mousePressed(MouseEvent e) {
		//all points relative to this component
		start_position = getBounds();
		Point p = SwingUtilities.convertPoint((Component)e.getSource(), e.getX(), e.getY(), null);
		x=p.x;
		y=p.y;		
	}
	
	public void mouseClicked(MouseEvent e) { ;}

	public void mouseReleased(MouseEvent e) { 
		x=0;
		y=0;
	}

	public void mouseEntered(MouseEvent e) { ;}

	public void mouseExited(MouseEvent e) { ;}
  
	public void mouseMoved(MouseEvent e) { ;	}
	
	// check that icon is not dragged off virtual window
	public void mouseDragged(MouseEvent e) {	  
		Point p = SwingUtilities.convertPoint((Component)e.getSource(), e.getX(), e.getY(), null);
		Rectangle parent_bounds = vw.getBounds();
		parent_bounds.setLocation(parent_bounds.x,parent_bounds.y+25); //adds height of title bar
		if(p.x<parent_bounds.x)
			p.x = parent_bounds.x;
		if(p.x>parent_bounds.x+parent_bounds.width)
			p.x = parent_bounds.x+parent_bounds.width;
		if(p.y<parent_bounds.y)
			p.y = parent_bounds.y;
		if(p.y>parent_bounds.y+parent_bounds.height)
			p.y=parent_bounds.y+parent_bounds.height;
		setLocation(start_position.x-(x-p.x), start_position.y-(y-p.y));
		vw.iconDragged(this, new Point(start_position.x-(x-p.x), start_position.y-(y-p.y)));
	}			 	

}
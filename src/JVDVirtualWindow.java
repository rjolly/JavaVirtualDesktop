import com.sun.java.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.lang.*;

// Scott Griswold  4/98
// Java Portable Desktop Manager
// class to draw virtual window and icons

class JVDVirtualWindow extends JComponent implements MouseListener { 
	
	private JVDDisplay parent = null;
	private JVDDesktopManager dtm;
	private int cell_in_focus = 0;
	private int cells_wide;
	private int cells_high;
	private int cell_width;
	private int cell_height;	
	private int title_offset = 8;
	private Color line_color;
	private Color cell_color;
	private Color background_color;
	private Color focus_color = new Color(155,155,155);
	private Color text_focus_color = Color.white;
					      
  public JVDVirtualWindow() {
  	line_color = Color.black;
		cell_color = Color.gray;
		background_color = Color.white;
		cells_wide = 2;	//defaults will be set by JVDDisplay
		cells_high = 3;
		cell_width = 80;
		cell_height = 60;	
		addMouseListener(this);			
	}
				
  // using mouselistner here prevents JVDDisplay from sensing popup menu events
  // if don't use it them cannot detect click on virtual window to change cell
  // when there is an application over it
  
	 	public void mouseReleased(MouseEvent e) {
	 		// check to be sure the cell clicked is not already in focus
	 		// starts at first cell and checks if point x, y is inside of it
			// first time it is satisfied it breaks out of it
			// Event not AWTEvent contains information about mouse keys
			// this must be set at a lower level
			if(!e.isMetaDown()) { // don't respond to right click
				cell_in_focus = -1;
				boolean cell_found = false;
				int x = e.getX();
				int y = e.getY();
				for(int i=0;i<cells_high;i++) {
					for(int j=0;j<cells_wide;j++) {
						if(x<(j+1)*cell_width && y<(i+1)*cell_height && !cell_found) {
							cell_in_focus = i*cells_wide + j;
							cell_found = true;
							repaint();
							break;
						}
					}
				}
				Point p = getLocation();		
				dtm.setAllIconLocation(e); // set apps and icon positions
			}
		}	  		
	 	//});
		 	
	public void mouseClicked(MouseEvent e) { ;}
	public void mousePressed(MouseEvent e) { ;}
	public void mouseEntered(MouseEvent e) { ;}
	public void mouseExited(MouseEvent e) { ;}

	public void iconDragged(JComponent c, Point p) {
		dtm.dragIcon(c, p);
	}

  public void setParent(JVDDisplay p) {
  	parent = p;
  	dtm = ((JVDDesktopManager) parent.getDesktopManager());
  }
  
	public void paintCellInFocus(int j) {
		cell_in_focus = j;
		repaint();
	}
	
	public void setCellWidth(int w) {
		cell_width = w;
	}

	public void setCellHeight(int h) {
		cell_height = h;
	}

	public void setCellsWide(int w) {
		cells_wide = w;
	}

	public void setCellsHigh(int h) {
		cells_high = h;
	}

	public int getCellInFocus() {
		return cell_in_focus;
	}
	
	public Dimension getGridDimension() {
		return new Dimension(cell_width*cells_wide, cell_height*cells_high);
	}
	
	// trims title to fit in icon
	public String trimTitle(String t, int w) {
		if(t.length() < w/5)
			return t;
		return t.substring(0,w/5);
	}
	
	public void paint(Graphics g) {
		g.setColor(line_color);   
		for(int y=0; y<cells_high;y++) {
			for(int x=0; x<cells_wide;x++)	{		  
				if(cell_in_focus == (y*cells_wide + x)) 
					g.setColor(cell_color);
				else 
					g.setColor(background_color);
				g.fillRect(cell_width*x, cell_height*y, cell_width, cell_height);
				g.setColor(line_color); // reset color
				g.drawRect(cell_width*x, cell_height*y, cell_width, cell_height);
			}
		}
		if(parent != null) { 	// draw icons for applications
			//JInternalFrame[] list = parent.getAllFramesInLayer(parent.APPLAYER);
			// did not work, submit as bug.
			JInternalFrame[] list = parent.getAllFrames();
			Point p = getLocation();
			g.setColor(line_color); // reset color
			Font titleFont = new Font("Dialog", Font.BOLD, 10);
			g.setFont(titleFont);
			for(int i = 0; i<list.length; i++) {				
				Rectangle r = parent.getIconForFrame(list[i]);
				String title = trimTitle(list[i].getTitle(), r.width);
				if(list[i].isSelected()) {
					g.setColor(focus_color);
					g.fillRect(r.x-p.x,r.y-p.y,r.width,r.height);
					g.setColor(text_focus_color);
					g.drawString(title, r.x-p.x, r.y-p.y+title_offset);	
					g.setColor(line_color);
				}
				else {
					g.drawRect(r.x-p.x,r.y-p.y,r.width,r.height);			
					g.drawString(title, r.x-p.x, r.y-p.y+title_offset);
				}
			}
		}		
	}
	
} // end class
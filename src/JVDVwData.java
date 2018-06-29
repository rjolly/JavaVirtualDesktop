import java.awt.*;
import java.lang.*;

// Scott Griswold  4/98
// Java Portable Desktop Manager
// class that contains data for virtual window
// virtual window consists of cells that can be selected to be
// the current desktop

class JVDVwData {
	
	public static final int TOP_LEFT = 1;
	public static final int TOP_RIGHT = 2;
	public static final int BOTTOM_LEFT = 3;
	public static final int BOTTOM_RIGHT = 4;
	
	private int width;
	private int height;
	private int cell_in_focus;
	private int cell_width;
	private int cell_height;
	private int num_of_cells;
	private int cells_wide;
	private int cells_high;
	private int scale;
	private int gridlocation;
	private int titlebar_height;
	private int screenres;
	
	private Dimension screensize;	
	private Cell[] cell = new Cell[6];
	private Toolkit tkit;
	private JVDVirtualWindow jvw;
	
  public JVDVwData() {
	
		jvw = new JVDVirtualWindow();
		tkit = Toolkit.getDefaultToolkit();
		screensize = tkit.getScreenSize();
  	screenres = tkit.getScreenResolution();  		  
		scale = 10;
  	cell_in_focus = 0;
		cells_wide = 2;
		cells_high = 3;
		num_of_cells = cells_wide*cells_high;
		cell = new Cell[num_of_cells];		
		cell_width = screensize.width/scale;
		cell_height = screensize.height/scale;
		width = cells_wide*cell_width;
		height = cells_high*cell_height;
		gridlocation = TOP_LEFT; //default
		jvw.setCellWidth(cell_width);
		jvw.setCellHeight(cell_height);
		jvw.setCellsWide(cells_wide);
		jvw.setCellsHigh(cells_high);		
		titlebar_height = 20;	// need to query Jframe for this
		
		for(int i=0;i<cells_high;i++) {
			for(int j=0;j<cells_wide;j++) {
				cell[i*cells_wide + j] = new Cell(new	Point(j*screensize.width, i*screensize.height));
			}
		}
  }
  
	public int getCellInFocus() {
		cell_in_focus = jvw.getCellInFocus();
		return cell_in_focus;
  }	
  
  public void setCellInFocus(int x, int y) {
	// starts at first cell and checks if point x, y is inside of it
	// first time it is satisfied it breaks out of it
		Point q = getGridOrigin();
		int x_offset = x-q.x;
		int y_offset = y-q.y;
		boolean cell_found = false;
		for(int i=0;i<cells_high;i++) {
			for(int j=0;j<cells_wide;j++) {
				if(x_offset<(j+1)*cell_width && y_offset<(i+1)*cell_height) {
					cell_in_focus = i*cells_wide + j;
					cell_found = true;
					return;
				}
			}
		}
  }	
  
  public void setCellInFocus(Point p) {
	// normalizes point p so that if it were at origin of grid it would be 0, 0
	// starts at first cell and checks if point x, y is inside of it
	// first time it is satisfied it breaks out of it, else leaves existing cell in focus
		Point q = getGridOrigin();
		int x = p.x-q.x;
		int y = p.y-q.y;
		for(int i=0;i<cells_high;i++) {
			for(int j=0;j<cells_wide;j++) {
				if(x<(j+1)*cell_width && y<(i+1)*cell_height) {
					cell_in_focus = i*cells_wide + j;
					return;
				}
			}
		}
  }	
  
  public Point getCellInFocusOrigin() {
		Point p = getGridOrigin();
		cell_in_focus = jvw.getCellInFocus();
		int x_add = 0;
		int y_add = 0;
		y_add = (cell_in_focus/cells_wide)*cell_height;
		if(cell_in_focus%cells_wide == 1)
			x_add = cell_width;			
		Point q = new Point(p.x+x_add, p.y+y_add);
		return q;
  }	
  
  public Dimension getMinimumSize() {
  	return(new Dimension(cells_wide*cell_width, cells_high*cell_height));
  }
  
  public int getIconInFocusPoint(Point p) {
		// starts at first cell and checks if point x, y is inside of it
		// first time it is satisfied it breaks out of it
		for(int i=0;i<cells_high;i++) {
			for(int j=0;j<cells_wide;j++) {
				if(p.x<(i+1)*cell_width && p.y<(j+1)*cell_height) {
					return i*cells_wide + j;
				}
			}
		}
		return -1; // no icons	
  }
  
	public void setGridLocation(int location) { 
  		switch(location) {
  			case 1:
    			gridlocation = location;
    			jvw.setBounds(0, 0, width, height); //top left
    			break;
    		case 2:
    			gridlocation = location;
    			jvw.setBounds(getScreenWidth()-width, 0, width, height); //top right
    			break;
    		case 3:
    			gridlocation = location;
    			jvw.setBounds(0, getScreenHeight()-height-titlebar_height, width, height);  //bottom left
    			break;
    		case 4:
    			gridlocation = location;
    			jvw.setBounds(getScreenWidth()-width, getScreenHeight()-height-titlebar_height, width, height); //bottom right
    			break;
    	}
  }

	public void setGridScale(int value) {
  	scale = value;
  	cell_width = screensize.width/scale;
		cell_height = screensize.height/scale;
		width = cells_wide*cell_width;
		height = cells_high*cell_height;
		setGridLocation(gridlocation);
		jvw.setCellWidth(cell_width);
		jvw.setCellHeight(cell_height);
		jvw.repaint();
  }
	
  public int getGridScale() {
		if (scale != 0)
			return scale;
		else
			return 10;	//default value
  }			    
  
  public Point getGridOrigin() {
  	return jvw.getLocation();
  }
  
  public int getHeight() {
  	return(height);
  }
  
  public int getWidth() {
  	return(width);
  }
  
  public int getNumberOfScreens() {
  	return(num_of_cells);
  }
  
  public void setNumberOfScreens(int num) {
  	if(num%2 != 0)	//simple check
  		return;
  	cells_high = num/2;
  	cells_wide = 2;
  	num_of_cells = num;
		jvw.setCellsWide(cells_wide);
		jvw.setCellsHigh(cells_high);
  	setGridScale(scale);
  } 	
  
  public int getScreenWidth() {
		return screensize.width;
  }
  
  public int getScreenHeight() {	
		return screensize.height;
  }	  
  
  public JVDVirtualWindow getVirtualWindow() {
		return jvw;
	}
	
  public boolean isOnCellInFocus(Point p) {
		int cell_found = -1;
		test: for(int i=0;i<cells_high;i++) {
			for(int j=0;j<cells_wide;j++) {
				if(p.x<(i+1)*cell_width && p.y<(j+1)*cell_height) {
					cell_found = i*cells_wide + j;
					break test;
				}

			}
		}		
		if(cell_in_focus == cell_found)
			return true;
		return false;
	}
  
  public boolean onGrid(Point p) {
 		Point q = getGridOrigin(); 
  	if(p.x>q.x && p.x<q.x+width && p.y>q.y && p.y<q.y+height)	
  		return true;
  	else return false;
  }			    
	
	// determine if p is on q
	public boolean onGrid(Point p, Point q) {
  	if(p.x>q.x && p.x<q.x+width && p.y>q.y && p.y<q.y+height)	
  		return true;
  	else return false;
  }			    
    
	public void updateCellInFocus(Point p) {
  	setCellInFocus(p);
  	jvw.paintCellInFocus(cell_in_focus);
  	return;
  }
  
}// end class

class Cell {
	private int origin_x;
	private int origin_y;
	
	public Cell(Point p) {
		origin_x = p.x;
		origin_y= p.y;
	}
	
	public Point getOrigin() {
		return(new Point(origin_x, origin_y));
	}
	
}
	
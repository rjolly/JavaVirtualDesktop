import java.lang.*;
import java.util.*;
import com.sun.java.swing.JInternalFrame;
import com.sun.java.swing.JComponent;

// Scott Griswold  4/98
// Java Portable Desktop Manager
// class that maps application to their icons

public class IconManager {

	private Object app_obj = null;	
	private Object icon_obj;
	private JVDDisplay parent;
	private Vector vector = null;	// initialized in constructor
	
	public IconManager(JVDDisplay p) {
		parent = p;
		vector = new Vector(); // holds application objects created when load applications	  	
	}

	public void addEntry(JInternalFrame app, JComponent icon) {
		Map mapping = new Map(app, icon);
		vector.addElement(mapping);
	}

	public void removeAppEntry(JInternalFrame app) {
		for(int i=0;i<vector.size();i++) {
	  	JInternalFrame target = ((Map)vector.elementAt(i)).App;
			if(app.equals(target)) {
				vector.removeElementAt(i);
				return;
			}
		}
		return;
	}

 public JInternalFrame getAppObject(JComponent icon) {
  	for(int i=0;i<vector.size();i++) {
		JComponent target = ((Map)vector.elementAt(i)).Icon;
			if(icon.equals(target)) {
 					return ((Map)vector.elementAt(i)).App;
  		}
  	}
  	return null;
  }
  
  public JComponent getIconObject(JInternalFrame app) {
  	for(int i=0;i<vector.size();i++) {
			JInternalFrame target = ((Map)vector.elementAt(i)).App;
			if(app.equals(target)) {
				return ((Map)vector.elementAt(i)).Icon;
  		}
  	}
  	return null;
  }
  
  public JInternalFrame[] getAllApps() {
  	int size = vector.size();
  	JInternalFrame[] list = new JInternalFrame[size];
  	for(int i=0;i<size;i++) {
			list[i] = ((Map)vector.elementAt(i)).App; 
		}  	
  	return list;
  }

  public JComponent[] getAllIcons() {
  	int size = vector.size();
  	JComponent[] list = new JComponent[size];
  	for(int i=0;i<size;i++) {
			list[i] = ((Map)vector.elementAt(i)).Icon; 
		}  	
  	return list;
  }


} // end class

// contains both the application and icon objects
class Map {
	public JInternalFrame App;
	public JComponent Icon;
	
	public Map(JInternalFrame f, JComponent c) {
		App = f;
		Icon = c;
	}

}
	
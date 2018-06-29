import java.awt.*;
import java.awt.event.*;
import java.lang.*;
import java.io.*;
import java.util.Vector;
import com.sun.java.swing.*;
import com.sun.java.swing.event.*;
import com.sun.java.swing.tree.*;
import com.sun.java.swing.border.*;
import java.rmi.*;
import java.rmi.server.*;
import java.net.*;
   
// JVD file manager window layout  7/17/97

public class FileManager extends JInternalFrame {

	private static String WINDOWSLF = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
	private static String MOTIFLF = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
	private static String JAVALF = "com.sun.java.swing.plaf.metal.MetalLookAndFeel";
	
	private String WINOS = "Windows";
	private String UNIXOS = "Unix";	
	private String os = null;
	
	private JScrollPane scrollpane1, scrollpane2;	  
	private JList jlist;	
	private DefaultListModel filemodel;
	final File filetest = new File(System.getProperty("user.dir"));  		

  public FileManager() {
      	
  	JMenuBar  menuBar = constructMenuBar();
		setMenuBar(menuBar);
	      		
  	MyDefaultMutableTreeNode root = new MyDefaultMutableTreeNode("Files");
		root.setParentFrame(this);	// required for Dialog
  	MyTreeModel tm = new MyTreeModel(root);		  	
		JTree tree = new JTree(tm);
		tree.setEditable(true);
		tree.setCellRenderer(new MyTreeCellRenderer());

		// detects click on tree and expands it
		tree.addTreeSelectionListener(new TreeSelectionListener() {
    	public void valueChanged(TreeSelectionEvent e) {
        int i, len = e.getPaths().length;
        for (i = 0; i < len; i++) {
           if (e.isAddedPath(e.getPaths()[i])) {
              MyDefaultMutableTreeNode anode = (MyDefaultMutableTreeNode)e.getPaths()[i].getLastPathComponent();
              if(anode.getUserObject().getClass().isInstance(filetest)) {
								// only local nodes are files
	              showFiles((File)anode.getUserObject());                
		          }
		          else anode.showContents();
  	          break;
           }
         } 		    
    	}
		});
		
		JPanel mainpanel = new JPanel(true); // double buffer    
	  mainpanel.setLayout(new GridLayout(1,2));    
		
		// scroll pane for file tree
		scrollpane1 = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); //default scroll layout
		scrollpane1.setPreferredSize(new Dimension(250, 420));
		// scrollpane1.setPreferredSize(ftp.getSize()); //do not use or don't see tree 
		scrollpane1.getViewport().add(tree);
		JPanel ftp = new JPanel(); // default flow layout  
		ftp.setBorder(new LineBorder(Color.black));
		ftp.add(scrollpane1);
		mainpanel.add(ftp);
		
		// listing of files
		filemodel = new DefaultListModel();        
		jlist = new JList(filemodel);
 		
 		// scroll pane for file contents tree		
  	scrollpane2 = new JScrollPane();
		scrollpane2.getViewport().setView(jlist);
		scrollpane2.setPreferredSize(new Dimension(250, 420));
		JPanel lp = new JPanel();
		lp.setBorder(new LineBorder(Color.black));
		lp.add(scrollpane2);
		mainpanel.add(lp);	
		
		getContentPane().add("Center", mainpanel);	// only put components on content pane
		
		setClosable(true);
		setMaximizable(true);
		setIconifiable(false);	// drag onto desktop to make dissappear
		setResizable(true);	
		setTitle("File Manager");		
		setSize(500,500);			

		if(System.getProperty("os.name").compareTo(UNIXOS) == 0)
			os = new String(UNIXOS);
		else os = new String(WINOS);

		try {UIManager.setLookAndFeel(WINDOWSLF);
  			//SwingUtilities.updateComponentTreeUI(this);		
    }
		catch (UnsupportedLookAndFeelException lf) {
       System.err.println("unsupported factory in constructor ");  }
		catch(IllegalAccessException iae) {;}
		catch(InstantiationException ie) {;}	
		catch(ClassNotFoundException cnfe) {;}
		
	} // end constructor
 	
 	// displays contents of directory in scrollpane
 	// for local files which are file objects, see below for remote files	
 	public void showFiles(File file) throws NullPointerException {			
		Vector file_vector = new Vector();
		Vector dir_vector = new Vector();
		String[] list_all = null;
		File directory = null;
		File eachfile = null;	
	 	try {directory = new File(file, File.separator); }
	 	catch(NullPointerException e) {System.out.println("newFile " + e);}

	 	if(directory.isDirectory()) {	// list only directories
			try { 
     		list_all = directory.list(); 
			}
			catch(SecurityException e) {
       System.out.println("file.list() SecurityException");
       return; //nothing to list
     }
     if(list_all == null && System.getProperty("os.name").startsWith("Windows 95")) { //bug cannot have trailing \
     		File foo = new File(file.toString());
       list_all = foo.list(); 
     }
     if(list_all == null) {
     		//System.out.println("showFiles list_all is null");
       return;
     }
	  	filemodel.removeAllElements();
			for(int i=0; i<list_all.length;i++) {		
				// create a separate string of directories for tree				
				try {eachfile = new File(file, list_all[i]);}
				catch(NullPointerException e) {System.out.println("newFile " + e);}
		
				if(eachfile.isDirectory()) { // list directories first
		     	// if add "dir:" then have to take off in ItemStateChanged()
		     	StringBuffer temp = new StringBuffer(list_all[i]);
		     	if(os.compareTo(WINOS)==0)
		     		temp.append('\\');	// identify as directory
		     	else 
		     		temp.append('/');	
		     	dir_vector.addElement(temp.toString());//new String(list_all[i]));
		    }
				else {
					file_vector.addElement(new String(list_all[i]));
				}					
	  	}
	  	for(int i=0; i<dir_vector.size();i++) 		
				filemodel.addElement((String)dir_vector.elementAt(i));
	  	for(int i=0; i<file_vector.size();i++) 		
				filemodel.addElement((String)file_vector.elementAt(i));
	  }
	  scrollpane2.validate();
  } 
	
	// displays contents of directory in scrollpane
	// for remote files which are strings, not file objects	
  public void showFiles(String[] list) {
		filemodel.removeAllElements();
		for(int i=0; i<list.length;i++) {		
			filemodel.addElement(list[i]);
		}
		scrollpane2.validate();
  } 
	
	private JMenuBar constructMenuBar() {		
		
		JMenuBar menuBar = new JMenuBar();
		
		JMenu filemenu = (JMenu) menuBar.add(new JMenu("File"));
		filemenu.setMnemonic('f');		

		JMenuItem menuItem;
		menuItem = filemenu.add(new JMenuItem("Exit"));
		menuItem.addActionListener(new ActionListener() {
	  	public void actionPerformed(ActionEvent e) { 
	  		try {setClosed(true); }
	  		catch(java.beans.PropertyVetoException pve) {} //do nothing
	  		}
		});
		
		JMenu view = (JMenu) menuBar.add(new JMenu("View"));
    view.setMnemonic('v');

		ButtonGroup group = new ButtonGroup();
		ToggleUIListener toggleUIListener = new ToggleUIListener();
 		JCheckBoxMenuItem cb;
		cb = (JCheckBoxMenuItem) view.add(new JCheckBoxMenuItem("Windows Look and Feel"));
		cb.setSelected(true);
		group.add(cb);
		cb.addItemListener(toggleUIListener);
    
		cb = (JCheckBoxMenuItem) view.add(new JCheckBoxMenuItem("Motif Look and Feel"));
		group.add(cb);
		cb.addItemListener(toggleUIListener);  

		cb = (JCheckBoxMenuItem) view.add(new JCheckBoxMenuItem("Java Look and Feel"));
		group.add(cb);
		cb.addItemListener(toggleUIListener);  
		
		return menuBar;
  }

	
 class ToggleUIListener implements ItemListener {
 		String currentUI;
		public void itemStateChanged(ItemEvent e) {
	    JCheckBoxMenuItem cb = (JCheckBoxMenuItem) e.getSource();
	    if(cb.isSelected() && cb.getText().equals("Windows Look and Feel")) {
				currentUI = FileManager.WINDOWSLF;
				try { 
	    		UIManager.setLookAndFeel(currentUI); 
	    		SwingUtilities.updateComponentTreeUI(FileManager.this);
				}	    
				catch (UnsupportedLookAndFeelException lf1) {
            System.err.println("could not load factory: "  + currentUI);
				}
				catch(IllegalAccessException iae) {;}
				catch(InstantiationException ie) {;}	
				catch(ClassNotFoundException cnf) {;}
			} 
		  if(cb.isSelected() && cb.getText().equals("Motif Look and Feel")) {
				currentUI = FileManager.MOTIFLF;
	    	try { 
	    		UIManager.setLookAndFeel(currentUI); 
	    		SwingUtilities.updateComponentTreeUI(FileManager.this);
       	}	    
				catch (UnsupportedLookAndFeelException lf2) {					
       		System.err.println("could not load factory: "  + currentUI);
	    	}
				catch(IllegalAccessException iae) {;}
				catch(InstantiationException ie) {;}	
				catch(ClassNotFoundException cnf) {;}		
			}
			if(cb.isSelected() && cb.getText().equals("Java Look and Feel")) {
				currentUI = FileManager.JAVALF;
	    	try { 
	    		UIManager.setLookAndFeel(currentUI); 
	    		SwingUtilities.updateComponentTreeUI(FileManager.this);
				}    
				catch (UnsupportedLookAndFeelException lf3) {					
       		System.err.println("could not load factory: "  + currentUI);
	    	}
	    	catch(IllegalAccessException iae) {;}
				catch(InstantiationException ie) {;}	
				catch(ClassNotFoundException cnf) {;}		
			}			
		}
	} // end ToggelUIListener class
} // end class JVDfmswing

class MyDefaultMutableTreeNode extends DefaultMutableTreeNode {	
	// creates a new tree of file directories
	private	MyDefaultMutableTreeNode newnode = null;
	private boolean hasLoaded;		
	static DefaultMutableTreeNode local_node = new MyDefaultMutableTreeNode(new String("local"));
  static DefaultMutableTreeNode remote_node = new MyDefaultMutableTreeNode(new String("remote"));
  private static String host;            
  private static String server = "rmiFileServer";
  private static FileManager parentframe = null;	
  private static JDesktopPane parentdesktop = null;	
	private String drive_letters;
	private String WINOS = "Windows";
	private String UNIXOS = "Unix";	
	private String os = null;
	  
  public MyDefaultMutableTreeNode(Object o) { 
   	super(o);   	
		drive_letters = new String("CD");// not a Java method
		if(System.getProperty("os.name").compareTo(UNIXOS) == 0)
			os = new String(UNIXOS);
		else os = new String(WINOS);
	}

	public boolean isLeaf() {
		// if a directory is does not have subdirectories then it is a leaf
		return false; // dynamically loading children
	}
	
	public void setParentFrame(FileManager f) { //used for dialog boxes
		parentframe = f;
		//parentdesktop = f.getDesktopPane(); // not initialized until displayed
	}
	
	public  JDesktopPane getParentDesktop() { //used for progress bar
		return parentframe.getDesktopPane();
	}
	
 	public int getChildCount() {	//called by getChildCount
		if(!hasLoaded) {
	    loadChildren();
		}
		return super.getChildCount();
  }
	
	protected void loadChildren() { // dynamically displays tree when called
		// figure out if called from root or from a directory
		File file = null;
		//System.out.println("loadChildren sucess " + this.toString());
		int position = 0;	
		//getParent() returns treenode so have to cast to MyDefaultMutableTreeNode
		// 4 possibilites root, local_node, remote_node, or string from remote node
		if(this.parent == null) { // called by root 
			insert(local_node, position++);
		  insert(remote_node, position++);
		  hasLoaded = true;
		  return;
		}    
		if(this.equals(this.local_node)) { //local node, list drives
			boolean is_drive = false;
			if(os.compareTo(UNIXOS)==0) { // get root of current directory
		  	String root = System.getProperty("user.dir");
				String parent = null;
				File f = new File(root);
				parent = f.getParent();
				while(parent != null) { //keep moving up one directory
					root = parent;
					f = new File(root);
					parent = f.getParent();						
				}
				try { file = new File(root);}
				catch(NullPointerException e) { is_drive = false;}
				try{ is_drive = file.exists(); }
				catch(SecurityException e) {;}
				if(is_drive) {			// add all the drives to the tree				
					// add all directories for each drive to the tree
			  	newnode = new MyDefaultMutableTreeNode(file); 
					insert(newnode, position++);
				}
				hasLoaded = true;
				return;		
			}
			else 	if(os.compareTo(WINOS)==0) {
				String drive_letter;  		
  			// drive_letters for Windows set in constructor
				// if try new File("a:\\") will get msg box no disk in drive if ignore 
				// catch the NullPointerException and try a different drive
				for(int i=0;i<drive_letters.length();i++) {	// try all possible drive letters
					StringBuffer sb = new StringBuffer();
					sb.append(drive_letters.substring(i,i+1));
					sb.append(":");
					drive_letter = new String(sb.toString());
					try { file = new File(drive_letter);}
					catch(NullPointerException e) { is_drive = false;}
					try{ is_drive = file.exists(); }
					catch(SecurityException e) {;}
					if(is_drive) {	// add all the drives to the tree				
						// add all directories for each drive to the tree
			  			newnode = new MyDefaultMutableTreeNode(file); 
						insert(newnode, position++);
					}
				}
				hasLoaded = true;
				return;
			}
			else return; // could not determine drive structure
		}										
		if(this.equals(this.remote_node)) { //remote node	request URL, list remote drives
                        host = JOptionPane.showInternalInputDialog(parentframe, "enter URL of remote host", "dialog", JOptionPane.QUESTION_MESSAGE);
			String newfile = null;
			boolean rmi_error = false;
			//ProgressBar pbf = null;
			if(host != null) {
				try {
					//pbf = new ProgressBar(getParentDesktop());
					// only do this first time do Naming.lookup
       		if(System.getSecurityManager() == null)
      			System.setSecurityManager(new RMISecurityManager());
      		rmiFile rmiLink = (rmiFile)Naming.lookup("rmi://" + host + "/" + server);
      		newfile = rmiLink.getRoot();
        	//pbf.stopBar();
				}
      	catch (SecurityException se) {
      		rmi_error = true;				
				}         
    		catch(RemoteException re) {
      		rmi_error = true;				
				}
    		catch(NotBoundException nbe) {
					rmi_error = true;				
				}
				catch(MalformedURLException me) {
     			rmi_error = true;				
					JOptionPane.showInternalMessageDialog(parentframe, "URL not found", "warning", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				finally { 
					//pbf.stopBar();
     			if(rmi_error) {
     	  		JOptionPane.showInternalMessageDialog(parentframe, "RMI error cannot connect", "warning", JOptionPane.INFORMATION_MESSAGE);
						//hasLoaded = true;
        		return; // without adding any nodes
					}
				}
     		if(newfile != null) {	
					newnode = new MyDefaultMutableTreeNode(newfile); 
					insert(newnode, position++);
					//System.out.println("remote file is " + newfile);
					hasLoaded = true;
					return;
       }
     }
     else { //host name not entered
     	//hasLoaded = true;
			return;
     }
		}
		if(this.getUserObject().getClass().isInstance(new String())) { // remote node string
				// list remote directories
				String listing[] = null;
				try {
           rmiFile rmiLink = (rmiFile)Naming.lookup("rmi://" + host + "/" + server);
           listing = rmiLink.getDirectory(this.toString());
				}
        catch (Throwable e) {
          System.err.println("rmiLink exception: " + e);
          hasLoaded = true;
					return;
        }         
				for(int i=0;i<listing.length;i++) {	
					newnode = new MyDefaultMutableTreeNode(listing[i]); 
					insert(newnode, position++);
				}
				hasLoaded = true;
				return;
    }
		else {	// local node string, list directories
				String list[] = null;
				File eachfile = null;
				// parent is DefaultMutableTreeNode not File
				String parent_file = this.toString();
				if(System.getProperty("os.name").startsWith("Windows 95")) { //bug cannot have trailing \
					try {file = new File(parent_file);}
					catch(NullPointerException e) {System.out.println("newFile " + e);}			
				}
				else {
					try {file = new File(parent_file, File.separator);}
					catch(NullPointerException e) {System.out.println("newFile " + e);}
				}
				//if(file.isDirectory()) {	//windows 95 fails this test
				// check that this is a directory why?
				try { list = file.list(); }
				catch(SecurityException e) {
					System.out.println("file.list() SecurityException");}	 
				if(list != null) {
					for(int k=0;k<list.length;k++) {		
						try {eachfile = new File(parent_file, list[k]);}
						catch(NullPointerException e) {
							System.out.println("loadChildren error " + e);	}					
						if(eachfile.isDirectory()) {						
							//newnode = new MyDefaultMutableTreeNode(parent_file + "/" + list[k]); // add this directory to tree
							newnode = new MyDefaultMutableTreeNode(eachfile); // add this directory to tree					
							insert(newnode, position++);						
						}
					}
				} //end if		
		} // end else
		hasLoaded = true;
		return;
	}// end method
 	
 	// displays contents of a directory in a scrollpane	
 	public void showContents() {
 		// if this is a string object for remote files get listing of remote files
		if(this.getUserObject().getClass().isInstance(new String())) {
			MyDefaultMutableTreeNode parent = (MyDefaultMutableTreeNode)this.getParent();
			while((parent != null) && !(parent.equals(this.remote_node)))
				parent = (MyDefaultMutableTreeNode)parent.getParent();	// look for parent node
			if(parent == null)
				return;
			if(parent.equals(this.remote_node)) { 
				String contents[] = null;
				try {
           rmiFile rmiLink = (rmiFile)Naming.lookup("rmi://" + host + "/" + server);
           contents = rmiLink.getListing(this.toString());
           parentframe.showFiles(contents);
				}
        catch (Throwable e) { 
        	JOptionPane.showInternalMessageDialog(parentframe, "RMI error cannot connect", "warning", JOptionPane.INFORMATION_MESSAGE);
        	System.err.println("show contents exception: " + e); }
      }
		}
	}
		           			
} // end class
	
class MyTreeModel extends DefaultTreeModel {

	public MyTreeModel(TreeNode newRoot) {
		super(newRoot);
	}
    
 	public void valueForPathChanged(TreePath path, Object newValue) {
		/* Used for editing filename when user triple clicks */
		MyDefaultMutableTreeNode      aNode = (MyDefaultMutableTreeNode)path.getLastPathComponent();
		File  file = (File)(aNode.getUserObject());		

		try{ 
			if(file.renameTo(new File((String)newValue))) {
				//System.out.println("renamed " + file.toString()); // still has old name
				aNode.setUserObject(new File((String)newValue));	
			}
		}
		catch(SecurityException e) {System.out.println("security execption file name");}

		nodeChanged(aNode); // notifies tree of change
 	} 	
}

class MyTreeCellRenderer extends FMTreeCellRenderer {
		// looks like an icon and text which is what a JLabel is	    

    /** Whether or not the item that was last configured is selected. */
   	protected boolean selected;
		
		/** Icons to use */
  	static protected ImageIcon collapsedIcon;
    static protected ImageIcon expandedIcon;

    /**
      * This is messaged from JTree whenever it needs to get the size
      * of the component or it wants to draw it.
      * This attempts to set the font based on value, which will be
      * a TreeNode.
      */
   public Component getTreeCellRendererComponent(JTree tree, Object value,
		boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			String  stringValue = tree.convertValueToText(value, selected,
			expanded, leaf, row, hasFocus);
		// if want to change text displayed need to modify stringValue
		// if want to remote all path names and leave just the file name then need to
		// parse and remove everything before the last "/" or use value.getParent() and
		// remove that string from the stringValue
			
			if(expanded)
	    	setIcon(this.getOpenIcon());
			else if(!leaf)
	    	setIcon(this.getClosedIcon());
			else
	    	setIcon(null);

			 String parentstring =((MyDefaultMutableTreeNode)value).getUserObject().toString();			
			// need to remove path and display just file or directory name
			// two cases on with / the other with \
			// should really get separator from remote host and use that 	
			int location = stringValue.lastIndexOf("\\");			
			if(location < 1) 	// try other separator
				location = stringValue.lastIndexOf("/");
			/* Set the text. */
			if(location >3) // needed for drive letters
				setText(stringValue.substring(location + 1)); //drop / from name
			else 
				setText(stringValue);
			
			/* Update the selected flag for the next paint. */
			this.selected = selected;

			return this;
  }
}	

class FMTreeCellRenderer extends JLabel implements TreeCellRenderer {
    /** Is the value currently selected. */
    protected boolean selected;

    // Icons
    /** Icon used to show non-leaf nodes that aren't expanded. */
    transient protected Icon closedIcon;

    /** Icon used to show leaf nodes. */
    transient protected Icon leafIcon;

    /** Icon used to show non-leaf nodes that are expanded. */
    transient protected Icon openIcon;

    // Colors
    /** Color to use for the foreground for selected nodes. */
    protected Color textSelectionColor;

    /** Color to use for the foreground for non-selected nodes. */
    protected Color textNonSelectionColor;

    /** Color to use for the background when a node is selected. */
    protected Color backgroundSelectionColor;

    /** Color to use for the background when the node isn't selected. */
    protected Color backgroundNonSelectionColor;

    /** Color to use for the background when the node isn't selected. */
    protected Color borderSelectionColor;

    /**
      * Returns a new instance of BasicTreeCellRenderer.  Alignment is
      * set to left aligned.
      */
    public FMTreeCellRenderer() {
	setHorizontalAlignment(JLabel.LEFT);

	setLeafIcon(UIManager.getIcon("Tree.leafIcon"));
	setClosedIcon(UIManager.getIcon("Tree.closedIcon"));
	setOpenIcon(UIManager.getIcon("Tree.openIcon"));

	setTextSelectionColor(UIManager.getColor("Tree.textSelectionColor"));
	setTextNonSelectionColor(UIManager.getColor("Tree.textNonSelectionColor"));
	setBackgroundSelectionColor(UIManager.getColor("Tree.backgroundSelectionColor"));
	setBackgroundNonSelectionColor(UIManager.getColor("Tree.backgroundNonSelectionColor"));
	setBorderSelectionColor(UIManager.getColor("Tree.borderSelectionColor"));
    }
	
    /**
      * Returns the default icon used to represent non-leaf nodes that are expanded.
      */
    public Icon getDefaultOpenIcon() {
	return openIcon;
    }

    /**
      * Returns the default icon used to represent non-leaf nodes that are not
      * expanded.
      */
    public Icon getDefaultClosedIcon() {
	return closedIcon;
    }

    /**
      * Returns the default icon used to represent leaf nodes.
      */
    public Icon getDefaultLeafIcon() {
	return leafIcon;
    }

    /**
      * Sets the icon used to represent non-leaf nodes that are expanded.
      */
    public void setOpenIcon(Icon newIcon) {
	openIcon = newIcon;
    }

    /**
      * Returns the icon used to represent non-leaf nodes that are expanded.
      */
    public Icon getOpenIcon() {
	return openIcon;
    }

    /**
      * Sets the icon used to represent non-leaf nodes that are not expanded.
      */
    public void setClosedIcon(Icon newIcon) {
	closedIcon = newIcon;
    }

    /**
      * Returns the icon used to represent non-leaf nodes that are not
      * expanded.
      */
    public Icon getClosedIcon() {
	return closedIcon;
    }

    /**
      * Sets the icon used to represent leaf nodes.
      */
    public void setLeafIcon(Icon newIcon) {
	leafIcon = newIcon;
    }

    /**
      * Returns the icon used to represent leaf nodes.
      */
    public Icon getLeafIcon() {
	return leafIcon;
    }

    /**
      * Sets the color the text is drawn with when the node is selected.
      */
    public void setTextSelectionColor(Color newColor) {
	textSelectionColor = newColor;
    }

    /**
      * Returns the color the text is drawn with when the node is selected.
      */
    public Color getTextSelectionColor() {
	return textSelectionColor;
    }

    /**
      * Sets the color the text is drawn with when the node isn't selected.
      */
    public void setTextNonSelectionColor(Color newColor) {
	textNonSelectionColor = newColor;
    }

    /**
      * Returns the color the text is drawn with when the node isn't selected.
      */
    public Color getTextNonSelectionColor() {
	return textNonSelectionColor;
    }

    /**
      * Sets the color to use for the background if node is selected.
      */
    public void setBackgroundSelectionColor(Color newColor) {
	backgroundSelectionColor = newColor;
    }


    /**
      * Returns the color to use for the background if node is selected.
      */
    public Color getBackgroundSelectionColor() {
	return backgroundSelectionColor;
    }

    /**
      * Sets the background color to be used for non selected nodes.
      */
    public void setBackgroundNonSelectionColor(Color newColor) {
	backgroundNonSelectionColor = newColor;
    }

    /**
      * Returns the background color to be used for non selected nodes.
      */
    public Color getBackgroundNonSelectionColor() {
	return backgroundNonSelectionColor;
    }

    /**
      * Sets the color to use for the border.
      */
    public void setBorderSelectionColor(Color newColor) {
	borderSelectionColor = newColor;
    }

    /**
      * Returns the color the border is drawn.
      */
    public Color getBorderSelectionColor() {
	return borderSelectionColor;
    }


    /**
      * Configures the renderer based on the passed in components.
      * The value is set from messaging value with toString().
      * The foreground color is set based on the selection and the icon
      * is set based on on leaf and expanded.
      */
    public Component getTreeCellRendererComponent(JTree tree, Object value,
						  boolean sel,
						  boolean expanded,
						  boolean leaf, int row,
						  boolean hasFocus) {
	String         stringValue = tree.convertValueToText(value, sel,
					  expanded, leaf, row, hasFocus);

	setText(stringValue);
	if(sel)
	    setForeground(getTextSelectionColor());
	else
	    setForeground(getTextNonSelectionColor());
	if (leaf) {
	    setIcon(getLeafIcon());
	} else if (expanded) {
	    setIcon(getOpenIcon());
	} else {
	    setIcon(getClosedIcon());
	}
	    
	selected = sel;

	return this;
    }

    /**
      * Paints the value.  The background is filled based on selected.
      */
    public void paint(Graphics g) {
	Color bColor;

	if(selected) {
	    bColor = getBackgroundSelectionColor();
	} else {
	    bColor = getBackgroundNonSelectionColor();
	    if(bColor == null)
		bColor = getBackground();
	}
	if(bColor != null) {
	    Icon currentI = getIcon();

	    g.setColor(bColor);
	    if(currentI != null && getText() != null) {
		int offset = (currentI.getIconWidth() + getIconTextGap());

		g.fillRect(offset, 0, getWidth() - 1 - offset,
			   getHeight() - 1);
	    } else {
		g.fillRect(0, 0, getWidth()-1, getHeight()-1);
	    }
	}
	if (selected) {
	    g.setColor(getBorderSelectionColor());
	    g.drawRect(0, 0, getWidth()-1, getHeight()-1);
	}
	super.paint(g);
    }

    public Dimension getPreferredSize() {
	Dimension        retDimension = super.getPreferredSize();

	if(retDimension != null)
	    retDimension = new Dimension(retDimension.width + 3,
					 retDimension.height);
	return retDimension;
    }
}
// Scott Griswold  4/98
// Java Portable Desktop Manager
// Class to load other Java classes

import java.awt.*;
import java.awt.event.*;
import com.sun.java.swing.*;
import com.sun.java.swing.preview.*;
import java.util.*;

public class AllClassLoader extends JInternalFrame {
	private JTextArea filetext;
	private JTextField directorytext;
	private JButton bfile, bnet;
	private JLabel filelabel;
	private JPanel p1, p2, p3;
	
	private	Object o; 
	private FileClassLoader fcl = null;
	private NetworkClassLoader ncl = null;
	
	private Component parent;  	
		
 	public AllClassLoader() {
	
		parent = this;	
		Container c = getContentPane();
	 	c.setLayout(new GridLayout(4,1)); 
	 	
	 	JLabel mainlabel = new JLabel("Enter Information to load class");
	 	
	 	JLabel filelabel = new JLabel("Enter filename:"); 
		filetext = new JTextArea(5,20);
	 	
	 	JLabel directorylabel = new JLabel("Enter file directory or Network URL:"); 
	 	directorytext = new JTextField(20);
	 	
	 	bfile = new JButton("file");
		bfile.addActionListener(new ActionListener() {  
  		public void actionPerformed(ActionEvent e) {
       	if(e.getActionCommand() == "file") {  
       		if(filetext.getText().length() == 0 || directorytext.getText().length() == 0) {
       			JOptionPane.showInternalMessageDialog(parent, "enter name and directory", "Class Loader", JOptionPane.INFORMATION_MESSAGE);
       		}
       		else {
       			//if(!directorytext.getText().endsWith("\") || !directorytext.getText().endsWith("//"))
			   		//append file separator onto directory;
			   		if(fcl == null)
			   			fcl = new FileClassLoader(); 
		     		try { 
	    			  Class cl = fcl.loadClass(filetext.getText(),directorytext.getText());
		   			  JInternalFrame f = (JInternalFrame)(cl.newInstance());
		   			  ((JVDDisplay)getDesktopPane()).startProgram(f);
							setClosed(true);
				 		} catch (Exception ee) { 
            System.out.println(getTitle() +" exception: "+ ee); 
            JOptionPane.showInternalMessageDialog(parent, "Could not load class", "Class Loader", JOptionPane.INFORMATION_MESSAGE);       			
         	} 			
         }
       }
 			}
 		});
 		
  	bnet = new JButton("network");
		bnet.addActionListener(new ActionListener() {  
  		public void actionPerformed(ActionEvent e) {
       	if(e.getActionCommand() == "network") {
          if(filetext.getText().length() == 0 || directorytext.getText().length() == 0) {
       			JOptionPane.showInternalMessageDialog(parent, "enter name and directory", "Class Loader", JOptionPane.INFORMATION_MESSAGE);
       		}
       		else {
	  				if(ncl == null)
          		ncl = new NetworkClassLoader(); 
				  	try { 
	    		  	Class cl = ncl.loadClass(filetext.getText(),directorytext.getText());
		   		  	JInternalFrame f = (JInternalFrame)(cl.newInstance());
		   		  	((JVDDisplay)getDesktopPane()).startProgram(f);
							setClosed(true);
						} catch (Exception ee) { 
            System.out.println(getTitle() +" exception: "+ ee); 
            JOptionPane.showInternalMessageDialog(parent, "Could not load class", "Class Loader", JOptionPane.INFORMATION_MESSAGE);
        		}
        	}         	
       }
 			}
 		});
 		
 		p1 = new JPanel();
		p1.setLayout(new GridLayout(2,1));
		p1.add(filelabel);
		p1.add(filetext);
	 
		p2 = new JPanel();
 		p2.setLayout(new GridLayout(2,1));
		p2.add(directorylabel);
		p2.add(directorytext);
	
		p3 = new JPanel();
 		p3.setLayout(new FlowLayout());
		p3.add(bfile);
		p3.add(bnet);
	  
		c.add(mainlabel);
		c.add(p1);  	
 		c.add(p2);
 		c.add(p3);
  		
		filetext.setEditable(true);
		filetext.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {}
  	});
    	
		setClosable(true);
		setMaximizable(true);
		setIconifiable(false);	// drag onto desktop to make dissappear
		setResizable(true);
		setTitle("AllClassLoader");
 		setSize(200,200);   	
	}
    	     	  
} // end class
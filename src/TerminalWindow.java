import java.io.*;
import java.awt.*;
import java.awt.event.*;
import com.sun.java.swing.*;

// Scott Griswold  4/98
// Java Portable Desktop Manager

// The way to exec in Java - you can't use a command directly, it needs to be in a bat file. 
// Then you need to specify the entire path of the bat file

// DOS commands are hidden in command.com and are not accessible individually like dir.exe
// looks like Java receives STDOUT from command line and then prints it to the screen

// DOES NOT WORK FOR cls because there is not screen output for that command
// also for commands that require user feedback it may not work, because you don't know when a line
// is waiting for a response or just an output line.

public class TerminalWindow extends JInternalFrame implements KeyListener {
	 	
 	private JTextArea textout;
 	private JScrollPane scrollpane1;
 	private String s;		
	private Process my_process = null;
	private StringBuffer command_string = null;
	private ThreadReader thread_reader_out = null;
	private ThreadReaderErr thread_reader_err = null;
	private BufferedWriter wout = null;
	private ProcessMonitor monitor = null;
	public String prompt;
		
	private static int place;
		
 	public TerminalWindow() {
		
		place = 0;
  	String dir = System.getProperty("user.dir");
  	prompt = new String(dir+">");
		command_string = new StringBuffer();		
  	
	  JMenuBar  menuBar = constructMenuBar();
		setMenuBar(menuBar);	      		

		textout = new JTextArea(10,40);	 			
		textout.addKeyListener(this);
		textout.setCaretPosition(0);		
		appendPrompt();
		scrollpane1 = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); //default scroll layout
		scrollpane1.getViewport().add(textout);
		
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		c.add(scrollpane1,"Center");
		
		setClosable(true);
		setMaximizable(true);
		setIconifiable(false);	// drag onto desktop to make dissappear
		setResizable(true);
		setTitle("TerminalWindow");
 		setSize(700,400);    
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
		
		return menuBar;
	}

 	public void appendOut(String s) {
			textout.append(s);
	}

	public void appendPrompt() {
		appendOut(prompt);
 	}
	
	public void setPosition() {
  	JScrollBar b = scrollpane1.getVerticalScrollBar();
		b.setValue(b.getMaximum());
 	}

 	public void execCommand(String cmd) {
 	 	// create a *.bat file with the user entered command and then exec that *.bat file				
		// *.bat is win 95 specific need to modify for unix and chmod to executable
    		
    // need some error checking when creating file
    FileCreate fc = new FileCreate(System.getProperty("user.dir"), "jvdcmd.bat", cmd);
    String cmdname = fc.getfilename();
    		
    try {                    
    	my_process = Runtime.getRuntime().exec(cmdname);
    	// Process input stream is connected to the stdout of the process
    	BufferedReader in = new BufferedReader(new InputStreamReader(my_process.getInputStream()));
 			BufferedReader err = new BufferedReader(new InputStreamReader(my_process.getErrorStream()));
 			// if caps lock is on when start always types caps and vice versa			  
			textout.append("\n");
			// reading stdout and stderr need to be a thread so do not block
			if(monitor != null) {
				monitor.stop();
				monitor = null;
			}
			monitor = new ProcessMonitor(my_process);
    	monitor.start();
    	
    	if(thread_reader_out != null) {
    		thread_reader_out.stop();
    		thread_reader_out = null;
    	}
    	thread_reader_out = new ThreadReader(this, in, monitor);
			thread_reader_out.start();					
			
			if(thread_reader_err != null) {
				thread_reader_err.stop();
				thread_reader_err = null;
			}
			thread_reader_err = new ThreadReaderErr(this, err, monitor);
			thread_reader_err.start();	
		
			if(wout != null)
				wout = null;	
		}
		
		catch (IOException io) {
    	this.appendOut("exec error: " + io);
     	my_process = null;
		}     		
	}
		 	
 	public void outputCommand(String command) {
 		StringBuffer s = new StringBuffer(command);
		s.append('\n');
		String sin = new String(s);
		// want to send to stdin of the process which should be getOutputStream()
		try {
			if(wout == null)
				wout = new BufferedWriter(new OutputStreamWriter(my_process.getOutputStream()));
			wout.write(sin,0,sin.length());
			wout.flush();
		}
 		catch(IOException io) { 
 				System.out.println("output IOExcep. " + io);
 			//	execCommand(command); // works for NT only
 		}
 		catch(NullPointerException np) {
 			System.out.println("output NullPointer " + np);
 			//	execCommand(command); // works for NT only
 		}
	}
							 		  	 
 	public void keyTyped(KeyEvent e) {}
  
  public void keyPressed(KeyEvent e) {
		if(e.getKeyChar() == '\n') {				
    		String command = command_string.toString();
       if(command.compareTo("quit")==0) {
         appendOut("quit trapped");
         endProcess();
       }

				if(monitor != null && !monitor.isExit()) {
					outputCommand(command);
				}
				else {       
					execCommand(command);
				}         
				command_string = new StringBuffer();
				place = 0;
				return;
		}	
		
		if(e.getKeyChar() != e.CHAR_UNDEFINED){
			if(e.getKeyChar() == e.VK_BACK_SPACE)
				place --;
			else {
				int len = command_string.length();
				command_string.insert(place++, e.getKeyChar());
			}
			command_string.setLength(place);
			return;
		}
				
	}
	
	public void keyReleased(KeyEvent e) {}

  public void endProcess() {
  	if (my_process != null) {
  		my_process.destroy();
  		my_process = null;
  	}
  	return;
  }

	public static void main(String args[]) {
			
		// create a frame to dislay in
		JFrame f = new JFrame();
		TerminalWindow tw = new TerminalWindow();
	 		
		// create a layered pane which can display multiple internal frames	  	
		JDesktopPane desktop = new JDesktopPane();
		desktop.setOpaque(false);	
	  desktop.add(tw, new Integer(10)); //specify layer number higher covers lower

		// finally display the layered pane in the JFrame
		Container c = f.getContentPane();      
   	c.add(desktop);	//add the internalframe
   	c.add("Center", desktop);
		f.setSize(800,400);
		f.setVisible(true);
  }

} // end class TerminalWindow
  
  class ProcessMonitor extends Thread {
  	
  	Process p = null;
  	boolean exited = false;
  	
  	public ProcessMonitor(Process p) {
  		this.p = p;
  		exited = false;
  	}
  	
  	private synchronized void setExit(boolean b) {
  		exited = b;
  	}
  
  	public boolean isExit() {
  		return exited;
  	}
  	
  	public void run() {
  		try{	
  			p.waitFor();
  			setExit(true);
  		}
			catch(InterruptedException ie) {
				System.out.println("Process Monitor Interrupted: "+ie);
			}
		}
	}

	class ThreadReader extends Thread {
  	 
  	TerminalWindow obj = null;
  	BufferedReader in = null;
  	ProcessMonitor monitor;
  	char buf[] = new char[80]; // 80 is one line
		boolean start = false;
		boolean done = false;
  	
  	public ThreadReader(TerminalWindow obj, BufferedReader in, ProcessMonitor m) {
	  		this.obj = obj;
	  		this.in = in;
	  		monitor = m;
	  }
  	
  	public void run() {
  		int i, tries;
			tries = 0;
	 		try {
				while(!done) {
					if(in.ready()) {
						start = true;
						if((i = in.read(buf, 0, 80)) == -1)
							done = true;
						else { 
							try{
								obj.appendOut(new String(buf, 0, i));
							}
							catch(StringIndexOutOfBoundsException se) {
								obj.appendOut("Thread out of bounds exeception");
							}
						}
					}
					else {
						if(start) {
							if(monitor.isExit() && tries++>6){ // prompt after process exits
								done=true;
								obj.appendPrompt();
								obj.setPosition();
							}
						}		
					}					
			  	try {sleep(100);}
			  	catch(InterruptedException t) {;}				  	
     		}
     		//NT only obj.(new String("\n"));
     		//obj.appendOut(obj.prompt);
     		}
    	catch (IOException io) {
    		obj.appendOut("TerminalWindowError: " + io);
    	}
   	}       
   	
  }// end class ThreadReader

	class ThreadReaderErr extends Thread {
  	 
  	TerminalWindow obj = null;
  	BufferedReader in = null;
  	ProcessMonitor monitor;
  	char buf[] = new char[80]; // 80 is one line
		boolean start = false;
		boolean done = false;
  	
  	public ThreadReaderErr(TerminalWindow obj, BufferedReader in, ProcessMonitor m) {
	  		this.obj = obj;
	  		this.in = in;
	  		monitor = m;
	  }
  	
  	public void run() {
  		int i, tries;
			tries = 0;
	 		try {
				while(!done) {
					if(in.ready()) {
						start = true;
						if((i = in.read(buf, 0, 80)) == -1)
							done = true;
						else { 
							try{
								obj.appendOut(new String(buf, 0, i));
							}
							catch(StringIndexOutOfBoundsException se) {
								obj.appendOut("Thread out of bounds exeception");
							}
						}
					}
					else {
						if(start) {
							if(monitor.isExit() && tries++>6){ // prompt after process exits
								done=true;
								obj.setPosition();
							}
						}		
					}					
			  	try {sleep(100);}
			  	catch(InterruptedException t) {;}				  	
     		}
     		//NT only obj.(new String("\n"));
     		//obj.appendOut(obj.prompt);
     		}
    	catch (IOException io) {
    		obj.appendOut("TerminalWindowError: " + io);
    	}
   	}       
   	
  }// end class ThreadReaderErr

  // create a file to store commands the use want to execute.
  // jvd.bat on Windows, shell on UNIX 
  class FileCreate  {
   	
   	private File file = null;
   	private FileOutputStream os = null;
   	private String filename;
   	 	
   	public FileCreate(String dir, String name, String data) {
   		filename = name;
   		try { file = new File(new File(dir), name); }
   		catch (NullPointerException e) {System.out.println("File create error");}
   		
   		try {	os = new FileOutputStream(file); }
   		catch(IOException e) {System.out.println("Filewriter create error");}
   		
   		byte[] b = data.getBytes();
   		try{	
   			os.write(b, 0, data.length());
   		  os.close();
   		}
   		catch(IOException e) {System.out.println("File write error");} 
		}
   
		public String getfilename() {
			if(file.exists())
				return(filename);
			else 
				return null;
		}
		
  }	// end class FileCreate
 
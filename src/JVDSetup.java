import java.awt.*;
import java.io.*;
import java.util.*;
import java.lang.*;

// Scott Griswold  4/98
// Java Portable Desktop Manager
/* read a file that contains names of programs to be run by Java Desktop Manger.
 * programs must all be java class files, this will be appended to name and loaded
 * to run them.
 */

public class JVDSetup {
	
	private String[] programs;
	private String default_filename;
	private Vector program_list = new Vector();
	
	public JVDSetup() {
		default_filename = new String("jvd.txt");
	}
	
	public Vector readFile(String filename) {

		try {
	  	String inputline;
	  	String directory = System.getProperty("user.dir");
			File inputFile = new File(directory, filename);
			if(!inputFile.exists() || !inputFile.isFile()) {
				Frame f = new Frame();
    		FileDialog d = new FileDialog(f, "JVD set up file not found");
				d.setDirectory(directory);
    		d.setFile(default_filename);
    		d.show();
    		// need error checking for directory and file
    		inputFile = new File(new File(d.getDirectory()), d.getFile());    		
				//if(inputFile == null)
					//throw new FileCopyException("jvd.set not found\n");
			}
			if(!inputFile.canRead()) {
				//throw new FileCopyException("jvd.set not readable\n");
			}
			FileInputStream inputstream	= new FileInputStream(inputFile);
			BufferedReader fis = new BufferedReader(new InputStreamReader(inputstream));
 		           
			// parse looking for end of line
			int i =0;
			for(;;) {				
				inputline = fis.readLine();
				if(inputline == null)
					break;				
	 			program_list.addElement(inputline);
			}
  		fis.close();
		} catch (FileNotFoundException e) {
			programs = null;
			System.out.println("JVDsetup file not found: " + e);
		} catch (IOException e) {
			System.out.println("JVDSetup io exception: " + e);
		}
		return(program_list);
	}

} // end class
import java.io.*; 
import java.rmi.*; 
import java.rmi.server.*;
import java.util.*;

public class rmiFileServer extends UnicastRemoteObject implements rmiFile {

	private String WINOS = "Windows";
	private String UNIXOS = "Unix";	
	private String os = null;
	
	public rmiFileServer() throws RemoteException { ; }

		// returns the root of the current drive
		// would like a listing of all drives, but Java does not support
		// with a platform independent method
		public String getRoot() throws RemoteException {
			String root = System.getProperty("user.dir");
			String parent = null;
			File f = new File(root);
			parent = f.getParent();
			while(parent != null) { //keep moving up one directory
				root = parent;
				f = new File(root);
				parent = f.getParent();						
			}
			return(root);
		}
		
		// returns listing of all directories
		public String[] getDirectory(String dir) throws RemoteException {
    	File file = null;
    	File eachfile = null;
    	String final_list[] = null;
    	String dir_list[] = null;
    	Vector dir_vector = new Vector();
			try { file = new File(dir, File.separator); }
			catch(NullPointerException e) { file = null; }
			if(file.isDirectory()) {	// list only directories
				try { dir_list = file.list(); }
				catch(SecurityException e) {System.out.println("getDirectory() SecurityException");}
	  		for(int i=0; i<dir_list.length;i++) {		
					// create a separate string of directories for tree				
					try {eachfile = new File(file, dir_list[i]);}
					catch(NullPointerException e) {System.out.println("newFile " + e);}			
					if(eachfile.isDirectory()) // list directories first
		      	dir_vector.addElement(file.getAbsolutePath()+dir_list[i]);
				}
	  		final_list = new String[dir_vector.size()];
				for (int i = 0; i < final_list.length; i++)
	   				final_list[i] = (String) dir_vector.elementAt(i);
				return final_list;
			}
			return final_list;			
		}
    		
		// returns a listing of files in the directory
		public String[] getListing(String dir) throws RemoteException {
			File file = null;
    	File eachfile = null;
    	String final_list[] = null;
    	String list_all[] = null;
    	Vector dir_vector = new Vector();
    	Vector file_vector = new Vector();
			if(System.getProperty("os.name").compareTo(UNIXOS) == 0)
				os = UNIXOS;
			else os = new String(WINOS);

			try { file = new File(dir, File.separator); }
			catch(NullPointerException e) { file = null; }
			if(file.isDirectory()) {	// list only directories
				try { list_all = file.list(); }
				catch(SecurityException e) {System.out.println("file.list() SecurityException");}
	  		for(int i=0; i<list_all.length;i++) {		
					// create a separate string of directories for tree				
					try {eachfile = new File(file, list_all[i]);}
					catch(NullPointerException e) {System.out.println("newFile " + e);}			
					if(eachfile.isDirectory()) { // list directories first
		     		StringBuffer temp = new StringBuffer(list_all[i]);
			     	if(os.compareTo(WINOS)==0)
			     		temp.append('\\');	// identify as directory
			     	else 
		  	   		temp.append('/');	
		    	 	dir_vector.addElement(temp.toString());
		    	}
					else file_vector.addElement(list_all[i]);//file.getAbsolutePath()+list_all[i]);
				}
				
	  		final_list = new String[dir_vector.size() + file_vector.size()];
	  		int i;
				for (i = 0; i < dir_vector.size(); i++)
	   				final_list[i] = (String) dir_vector.elementAt(i);
				i = dir_vector.size();
				for (int j = 0; j < file_vector.size(); j++)
	   				final_list[i++] = (String) file_vector.elementAt(j);
				return final_list;
			}
			return final_list;			
		}
    		
 		public static void main(String args[]){
			try {
				RMISecurityManager security = new RMISecurityManager();
				System.setSecurityManager(security);
				rmiFileServer server = new rmiFileServer();
				Naming.rebind("rmiFileServer", server);
				System.out.println("rmiFileServer ready...");
			}
			catch (Throwable e) {
				System.err.println("exception: " + e);
				System.exit(1);
			}
		}
}
import java.io.*; 
import java.rmi.*; 
import java.rmi.server.*;
import java.util.*;

// server for remote class loading
public class rmiClassLoaderServer extends UnicastRemoteObject implements rmiClassLoader {

	public rmiClassLoaderServer() throws RemoteException { ; }

	// should add capability of selecting remote directory
	public byte[] loadRemoteClass(String classname) throws RemoteException {
		// read bytes from file on remote machine that contains classes
	  try{
			File f = new File(classname + ".class");
			FileInputStream fis = new FileInputStream(f);
			byte bytes[] = new byte[(int)(f.length())];
			if(fis.read(bytes) != bytes.length)
				return null; //check that all bytes were read
			return(bytes);
	  }
		catch (FileNotFoundException fnfe) {}
		catch (IOException ioe) {}
		return null;
	}
		    		
  public static void main(String args[]){
    try {
  		RMISecurityManager security = new RMISecurityManager();
  		System.setSecurityManager(security);
  		rmiClassLoaderServer server = new rmiClassLoaderServer();
  		Naming.rebind("rmiClassLoaderServer", server);
  		System.out.println("rmiClassLoaderServer ready...");
  	}
    catch (Throwable e) {
    	System.err.println("rmiClassLoaderServer Exception: " + e);
    }
  }
  
}
// Scott Griswold  4/98
// Loads classes from local directory
// Class loader to load from Network using RMI
// The following are required for the remote implementation
// rmiClassLoader.java, rmiClassLoaderServer.java

import java.util.Hashtable;
import java.io.*;
import java.lang.System;
import java.rmi.*;
import java.rmi.server.*;
import java.util.*;
import java.lang.*;

public class NetworkClassLoader extends ClassLoader {
	private Hashtable classes = new Hashtable();
	private String host;
	private String fullhostname;
		
  public NetworkClassLoader() { ; }

  	// reading class from a remote object
  	private byte getClassFromRMI(String className)[] {
  		byte result[];
  		try {
  			if(host == null)
  				host = new String("localhost"); //vain attempt 
  			if(System.getSecurityManager() == null)
  				System.setSecurityManager(new RMISecurityManager());
  			String server = "rmiClassLoaderServer";
  			// change localhost to remote host in next line
  			fullhostname = "rmi://" + host + "/" + server;
  			rmiClassLoader rmiLoader = (rmiClassLoader)Naming.lookup(fullhostname);
  			result = rmiLoader.loadRemoteClass(className);
  			return result;
  		}
  		catch (Throwable e) {}
  		return null;
  	}
		
	private void setHost(String d) {
		host = new String(d);
	}
          
  public Class loadClass(String className, String host) throws ClassNotFoundException {
  	setHost(host);
  	return (loadClass(className, true));
  }

  public synchronized Class loadClass(String className, boolean resolveIt)
  	throws ClassNotFoundException {
  	Class result;
  	byte  classData[];

	 // Check local cache of classes 
  	result = (Class)classes.get(className);
  	if (result != null) {
  		return result;
  	}

  	// Check system classes 
  	try {
  		result = super.findSystemClass(className);
  		return result;
  	} 
  	catch (ClassNotFoundException e) {}

  	classData = getClassFromRMI(className);
  	if (classData == null) {
  		throw new ClassNotFoundException();
  	}

  	result = defineClass(className, classData, 0, classData.length);
  	if (result == null) {
  		throw new ClassFormatError();
  	}

  	if (resolveIt) {
  		resolveClass(result);
  	}

  	classes.put(className, result);
 		return result;
 	}
}
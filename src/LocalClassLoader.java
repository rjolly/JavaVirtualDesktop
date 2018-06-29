// Scott Griswold  4/98
// Java Portable Desktop Manager
// Loads classes from local directory

import java.util.Hashtable;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.lang.System;

public class LocalClassLoader extends ClassLoader {
	private Hashtable classes = new Hashtable();
	private String directory;

  public LocalClassLoader() { ; }

  // reading class from a file
  public Class loadClass(String className) {
   	try {
   		Class c = loadClass(className, true);
   		return c;
   	}
   	catch(ClassNotFoundException e) { ; }  
   	return null;      
  }

  public synchronized Class loadClass(String className, boolean resolveIt)
  	throws ClassNotFoundException {
  		Class c;        
			//System.out.println(">>>>>> Load class : "+className);

     /* Check our local cache of classes */
     c = (Class)classes.get(className);
     if (c != null) {
     		//System.out.println(">>>>>> returning cached result.");
     		return c;
    	}

     /* Check with the primordial class loader */
     try {
       c = super.findSystemClass(className);
       //System.out.println(">>>>>> returning system class (in CLASSPATH).");
       return c;
     } catch (ClassNotFoundException e) {
     		//System.out.println(">>>>>> Not a system class.");
     }
			return null;
  }
}
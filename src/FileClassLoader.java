// Scott Griswold  4/98
// Java Portable Desktop Manager
// Loads classes from a file

import java.util.Hashtable;
import java.io.*;
import java.lang.System;

public class FileClassLoader extends ClassLoader {
  private Hashtable classes = new Hashtable();
	private String directory;

  public FileClassLoader() { ; }

  	// reading class from a file
  	private byte getClassFromFile(String className)[] {
  	byte result[];
  	try {
  		if(directory == null)
  			directory = new String(System.getProperty("user.dir")+"\\");
  		FileInputStream fi = new FileInputStream(directory + className + ".class");
  		result = new byte[fi.available()];
  		fi.read(result);
			return result;
  	}
  	catch (Exception e) {
    	return null;
  	}
  }
		
	private void setDirectory(String d) {
		directory = new String(d);
	}
         
  public Class loadClass(String className) {
   	try {
   		Class c = loadClass(className, true);
   		return c;
   	}
   	catch(ClassNotFoundException e) { ; }  
  	 	return null;      
   }

   public Class loadClass(String className, String directory) {
   		setDirectory(directory);
     try {
   			Class c = loadClass(className, true);
    		return c;
    	}
    	catch(ClassNotFoundException e) { ; }       
    		return null;
   }

   public synchronized Class loadClass(String className, boolean resolveIt) throws ClassNotFoundException {
   		Class result;
   		byte  classData[];

   // Check local cache of classes 
   		result = (Class)classes.get(className);
   		if (result != null) {
   			return result;
   		}

   // Check default class loader
   		try {
   			result = super.findSystemClass(className);
   			return result;
   		} 
   		catch (ClassNotFoundException e) {;	}

   		// Try to load from a file
   		classData = getClassFromFile(className);
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
   
} // end class
Scott Griswold  4/98
A Java Implementation of a Portable Desktop Manger


DISK DIRECTORIES AND FILES

The root directory contains:
.class files for the program
.gif files used for TicTacToe.class
.properties files are used for JNotepad.class
compile.bat to facilitate compiling of the source code on MS Windows 
jvd.jar is a jar file of the contents of the root directory
README.txt - this file

images/ - contains images for JNotepad.class program

source/ - contains all the *.java files


JAVA NOTES

These Java programs were compiled using jdk 1.1.5 and swing-1.0.1.
Set your classpath to include jdk1.1.5/lib/classes.zip and swing-1.0.1/swingall.jar to compile or execute these programs.
Both are available from Sun Microsystems, Inc. at http://java.sun.com/products/


EXPLAINATION

These programs create a desktop manager program that is capable of managing other Java programs (not Applets).
From the command line type "java JVD" which will start the desktop manager and present an blank desktop with a virtual window in the upper left. Pressing the right mouse button anywhere, except on the virtual window, will display the pop up menu of programs to load. Left clicking on any logical window on the virtual desktop will make that logical window the current desktop.


POP UP MENU
The Pop up menu is created from jvd.txt and the syntax is as follows:
Names should be name of the Java class to be loaded.
Example if Clock is entered on the menu will load Clock.class.

Any line that ends in a colon (:) will start a sub menu. That item should not be a class, but all sub menu entries should be classes. The last entry of a sub class should be "end".
Example 
Games:
TicTacToe
end


RMI - REMOTE METHOD INVOCATION

The AllClassLoader and the FileManager programs are able to access remote objects using Java's RMI. The classes are provided. All the rmi*.class files should be put on the server, then type "rmiregistry" to start the rmi daemon.
Type "java rmiFileServer" or "java rmiClassLoaderServer" to start the individual program servers.
Use rmic to compile rmiFileServer and rmiClassLoaderServer if changes are made to them.


CLASS LISTING

Desktop manager - these classes create the desktop manager and virtual window
JVD
JVDDesktopManager
JVDDisplay
JVDSetup
JVDVirtualWindow
JVDVwData
VwIcon
IconManager

These two classes are separate classes that allow customizing the setting of the desktop.
VwSettings
SetLnF

Class loader - these classes allow loading of local and remote Java classes to be managed by the desktop manager.
AllClassLoader
LocalClassLoader
FileClassLoader
NetworkClassLoader
rmiClassLoader
rmiClassLoaderServer

This utility displays local and remote files.
FileManager
rmiFile
rmiFileServer
rmiFileClient

This utility allows the user to execute commands from a command line interface.
TerminalWindow

Miscelleanous utilties that should be self explanitory. Some were taken from examples that came with jdk documentation, but modified to be sub classes of InternalFrame class.
Clock
Calculator
JNotepad
TicTacToe


KNOWN BUGS

If the remote host cannot be found for the FileManager, the program has to be closed and started again in order to access remote files.

Running the TerminalManager on Windows 95 the user is not able to respond to commands. For example if the command "date" is entered and the user responds with 04/22/98, this information is not passed to the operating system. Type ^C and enter to clear the command so that another command can be entered.

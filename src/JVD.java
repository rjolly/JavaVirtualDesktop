// JVD Virtual Desktop Scott Griswold 9/4/97
 
import java.awt.*;
import java.awt.event.*;
import com.sun.java.swing.JFrame;

// creates a frame and puts the JVD panel into it - JVD does all the work
public class JVD extends JFrame {
	  
	 public JVD() {
			JFrame f = new JFrame();
			JVDDisplay jvd = new JVDDisplay(f);
			f.getContentPane().add(jvd);
			f.setTitle("Java Desktop Manager");
    	f.setSize(jvd.getPreferredSize()); // this sets size
    	f.setVisible(true);
	  	f.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
            System.exit(0);
  			}  
   		});
		}
		
	  public static void main(String[] args) {    	
    	JVD j = new JVD();
    }    

		public Dimension getMinimumSize() {
			return(new Dimension(160,120));
		}
	
} //end class
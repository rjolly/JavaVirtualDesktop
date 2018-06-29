/*
 * @(#)Clock2.java	1.8 97/01/24
 *
 * Copyright (c) 1994-1996 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

// author: Rachel Gollub, 1995
// modified 96/04/24 Jim Hagen : use getBackground()
// modified 96/05/29 Rachel Gollub : add garbage collecting
// modified 96/10/22 Rachel Gollub : add bgcolor, fgcolor1, fgcolor2 params
// modified 96/12/05 Rachel Gollub : change copyright and Date methods
// Time!

// Modified for Swing to work with Java Desktop Manger
// Scott Griswold 4/98

import java.util.*;
import java.awt.*;
import java.applet.*;
import java.text.*;
import javax.swing.*;

public class Clock extends JInternalFrame {
	public Clock() {
		ClockPanel p = new ClockPanel();		
		getContentPane().add(p);
		setSize(200,170);
		setClosable(true);
		setMaximizable(true);
		setIconifiable(false);	// drag onto desktop to make dissappear
		setResizable(true);	
		setTitle("Clock");
		setVisible(true);
	}
	
	public static void main(String args[]) {
		JFrame f = new JFrame("Clock");
		Clock c = new Clock();
		JLayeredPane p = new JLayeredPane();
		p.add(c, new Integer(10));
		f.getContentPane().add(p);
		f.setSize(200,170);
		f.setVisible(true);
	}	

class ClockPanel extends JPanel implements Runnable {
  Thread timer = null;
  int lastxs=0, lastys=0, lastxm=0, lastym=0, lastxh=0, lastyh=0;
  Date dummy = new Date();
  GregorianCalendar cal = new GregorianCalendar();
  SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
  String lastdate = df.format(dummy);
  Font F = new Font("TimesRoman", Font.PLAIN, 14);
  Date dat = null;
  Color fgcol = Color.blue;
  Color fgcol2 = Color.darkGray;

	public ClockPanel() {
	 start();
	}

  // Plotpoints allows calculation to only cover 45 degrees of the circle,
  // and then mirror

	public void plotpoints(int x0, int y0, int x, int y, Graphics g) {

  	g.drawLine(x0+x,y0+y,x0+x,y0+y);
  	g.drawLine(x0+y,y0+x,x0+y,y0+x);
  	g.drawLine(x0+y,y0-x,x0+y,y0-x);
  	g.drawLine(x0+x,y0-y,x0+x,y0-y);
  	g.drawLine(x0-x,y0-y,x0-x,y0-y);
  	g.drawLine(x0-y,y0-x,x0-y,y0-x);
  	g.drawLine(x0-y,y0+x,x0-y,y0+x);
  	g.drawLine(x0-x,y0+y,x0-x,y0+y);
	}	

  // Circle is just Bresenham's algorithm for a scan converted circle

	public void circle(int x0, int y0, int r, Graphics g) {
  	int x =0;
  	int y=r;
  	float d=5/4-r;
  	plotpoints(x0,y0,x,y,g);
  	while (y>x){
    	if (d<0) {
      	d=d+2*x+3;
      	x++;
    	}
    	else {
      	d=d+2*(x-y)+5;
      	x++;
      	y--;
    	}
    	plotpoints(x0,y0,x,y,g);
  	}
	}

  // Paint is the main part of the program

	public void paint(Graphics g) {
  	int xh, yh, xm, ym, xs, ys, s, m, h, xcenter, ycenter;
  	String today;

  	dat = new Date();
  	cal.setTime(dat);
  	//  cal.computeFields(); Not needed anymore
  	s = (int)cal.get(Calendar.SECOND);
  	m = (int)cal.get(Calendar.MINUTE);
  	h = (int)cal.get(Calendar.HOUR_OF_DAY);
  	today = df.format(dat);
  	xcenter=80;
  	ycenter=55;
  
  	// a= s* pi/2 - pi/2 (to switch 0,0 from 3:00 to 12:00)
  	// x = r(cos a) + xcenter, y = r(sin a) + ycenter
  
  	xs = (int)(Math.cos(s * 3.14f/30 - 3.14f/2) * 45 + xcenter);
  	ys = (int)(Math.sin(s * 3.14f/30 - 3.14f/2) * 45 + ycenter);
  	xm = (int)(Math.cos(m * 3.14f/30 - 3.14f/2) * 40 + xcenter);
  	ym = (int)(Math.sin(m * 3.14f/30 - 3.14f/2) * 40 + ycenter);
  	xh = (int)(Math.cos((h*30 + m/2) * 3.14f/180 - 3.14f/2) * 30 + xcenter);
  	yh = (int)(Math.sin((h*30 + m/2) * 3.14f/180 - 3.14f/2) * 30 + ycenter);
  
  	// Draw the circle and numbers
  	//g.setColor(Color.lightGray); look and feel will select color
  	g.setColor(getBackground());
		g.fillRect(0,0,getSize().width,getSize().height);
  	g.setFont(F);
  	g.setColor(fgcol);
  	circle(xcenter,ycenter,50,g);
  	g.setColor(fgcol2);
  	g.drawString("9",xcenter-45,ycenter+3); 
  	g.drawString("3",xcenter+40,ycenter+3);
  	g.drawString("12",xcenter-5,ycenter-37);
  	g.drawString("6",xcenter-3,ycenter+45);

  	// Erase if necessary, and redraw
  
  	g.setColor(getBackground());
  	if (xs != lastxs || ys != lastys) {
    	g.drawLine(xcenter, ycenter, lastxs, lastys);
    	//g.drawString(lastdate, 5, 125);
    	g.drawString(dat.toString(), 5, 125);
  	}
  	if (xm != lastxm || ym != lastym) {
    	g.drawLine(xcenter, ycenter-1, lastxm, lastym);
    	g.drawLine(xcenter-1, ycenter, lastxm, lastym); 
    }
  	if (xh != lastxh || yh != lastyh) {
    	g.drawLine(xcenter, ycenter-1, lastxh, lastyh);
    	g.drawLine(xcenter-1, ycenter, lastxh, lastyh); 
    }
  	g.setColor(fgcol2);
  	g.drawString(dat.toString(), 5, 125); //today time is 3 hours off 
  	g.drawLine(xcenter, ycenter, xs, ys);
  	g.setColor(fgcol);
  	g.drawLine(xcenter, ycenter-1, xm, ym);
  	g.drawLine(xcenter-1, ycenter, xm, ym);
  	g.drawLine(xcenter, ycenter-1, xh, yh);
  	g.drawLine(xcenter-1, ycenter, xh, yh);
  	lastxs=xs; lastys=ys;
  	lastxm=xm; lastym=ym;
  	lastxh=xh; lastyh=yh;
  	lastdate = today;
  	dat=null;
	}

	public void start() {
  	if(timer == null) {
      	timer = new Thread(this);
    	  timer.start();
  	  }
	}

	public void stop() {
  	timer = null;
	}

	public void run() {
  	while (timer != null) {
    	try {Thread.sleep(100);} catch (InterruptedException e){}
    	repaint();
  	}
  	timer = null;
	}

	public void update(Graphics g) {
  	paint(g);
	}
}

}
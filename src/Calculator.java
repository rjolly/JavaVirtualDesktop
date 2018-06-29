import java.awt.*;
import java.awt.event.*;
import com.sun.java.swing.*;


// Scott Griswold  4/98
// Java Portable Desktop Manager
// calculator for Java Desktop Manager

public class Calculator extends JInternalFrame implements ActionListener {

	private String[] num_key = {"1","2","3","4","5","6","7","8","9","0","."};
	private String[] operator_key = {"+", "-", "*", "/", "=", "x^y"};
	private String[] unary_key = {"Sin", "Cos", "Tan", "Log", "Ln" , "Exp", "SQRT", "PI", "x^2", "x^n", "C", "+//-", "1//x", "10^x"};
	private int max_digits;

	// Registers required for calculations
	private double Operand1 = 0.0;
	private double Operand2 = 0.0;
	private int operator = 0;

	// Control flags
	private boolean newOperand = true;
	
	private JTextField textarea;
	public JCheckBox Cinv;
	public JCheckBox Cdeg;


	public Calculator() {

		max_digits = 8; //maximun numbers for calculation	

		// Calculator checkboxes for mode selection
		JCheckBox Cinv = new JCheckBox("Inv", false);
		JCheckBox Cdeg = new JCheckBox("Deg", false);		
	
		// Calculator buttons
		JButton B1 = new JButton("1");
		B1.addActionListener(this);	
		JButton B2 = new JButton("2");
		B2.addActionListener(this);	
		JButton B3 = new JButton("3");
		B3.addActionListener(this);
		JButton B4 = new JButton("4");
		B4.addActionListener(this);
		JButton B5 = new JButton("5");
		B5.addActionListener(this);
		JButton B6 = new JButton("6");
		B6.addActionListener(this);
		JButton B7 = new JButton("7");
		B7.addActionListener(this);
		JButton B8 = new JButton("8");
		B8.addActionListener(this);
		JButton B9 = new JButton("9");
		B9.addActionListener(this);
		JButton B0 = new JButton("0");
		B0.addActionListener(this);
		JButton Bplus = new JButton("+");
		Bplus.addActionListener(this);
		JButton Bminus = new JButton("-");
		Bminus.addActionListener(this);
		JButton Bmul = new JButton("*");
		Bmul.addActionListener(this);
		JButton Bdiv = new JButton("/");
		Bdiv.addActionListener(this);
		JButton Bequ = new JButton("=");
		Bequ.addActionListener(this);
		JButton Bdec = new JButton(".");
		Bdec.addActionListener(this);
		JButton Bxy = new JButton("x^y");
		Bxy.addActionListener(this);
		JButton Bclr = new JButton("C");
		Bclr.addActionListener(this);
		JButton Bsgn = new JButton("+//-");
		Bsgn.addActionListener(this);
		JButton B1x = new JButton("1//x");
		B1x.addActionListener(this);
		JButton Bsin = new JButton("Sin");
		Bsin.addActionListener(this);
		JButton Bcos = new JButton("Cos");
		Bcos.addActionListener(this);
		JButton Btan = new JButton("Tan");
		Btan.addActionListener(this);
		JButton Blog = new JButton("Log");
		Blog.addActionListener(this);
		JButton B10x = new JButton("10^x");
		B10x.addActionListener(this);
		JButton Bexp = new JButton("Exp");
		Bexp.addActionListener(this);
		JButton Bln = new JButton("Ln");
		Bln.addActionListener(this);
		JButton Bx2 = new JButton("x^2");
		Bx2.addActionListener(this);
		JButton Bsqrt = new JButton("SQRT");
		Bsqrt.addActionListener(this);
		JButton Bpi = new JButton("PI");
		Bpi.addActionListener(this);

		JPanel buttonPanel = new JPanel();

		buttonPanel.setLayout(new GridLayout(8,4));
		buttonPanel.add(Cinv);
		buttonPanel.add(Bsin);
		buttonPanel.add(Bsqrt);
		buttonPanel.add(Bexp);
		buttonPanel.add(Cdeg);
		buttonPanel.add(Bcos);
		buttonPanel.add(Bx2);
		buttonPanel.add(Bln);
		buttonPanel.add(Bpi);
		buttonPanel.add(Btan);
		buttonPanel.add(B10x);
		buttonPanel.add(Blog);
		buttonPanel.add(Bclr);
		buttonPanel.add(Bsgn);
		buttonPanel.add(B1x);
		buttonPanel.add(Bxy);
		buttonPanel.add(B1);
		buttonPanel.add(B2);
		buttonPanel.add(B3);
		buttonPanel.add(Bplus);
		buttonPanel.add(B4);
		buttonPanel.add(B5);
		buttonPanel.add(B6);
		buttonPanel.add(Bminus);
		buttonPanel.add(B7);
		buttonPanel.add(B8);
		buttonPanel.add(B9);
		buttonPanel.add(Bmul);
		buttonPanel.add(B0);
		buttonPanel.add(Bdec);
		buttonPanel.add(Bequ);
		buttonPanel.add(Bdiv);


		// Calculator text field
		textarea = new JTextField(30);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add("North",buttonPanel);
		getContentPane().add("South",textarea);	

		setTitle("Calculator");
		setClosable(true);
		setMaximizable(true);
		setIconifiable(false);	// drag onto desktop to make dissappear
		setResizable(true);	
		setSize(300,350);
		setVisible(true);
	}

	// specify which component had action, by its string 
	public void actionPerformed(ActionEvent e) {
		for(int i=0;i<num_key.length;i++) {
			if(e.getActionCommand() == num_key[i]) {
    				if(addDigit(num_key[i])==true) 
    					return;
			}
		}

		for(int i=0;i<operator_key.length;i++) {
			if(e.getActionCommand().startsWith(operator_key[i])) {
				if(handleOperator(operator_key[i])==true)
					return;	
			}
		}

		for(int i=0;i<unary_key.length;i++) {
			if(e.getActionCommand().startsWith(unary_key[i])) {
	    		if(handleUnaryOperator(unary_key[i])==true) 
	    			return;
			}
		}

  }

// Private Functions

// Add next digit to text

private boolean addDigit(String key) {

	boolean flag = false;

	if(textarea.getText().length() >= max_digits){
		// may want an message to user
		flag = false;
	}
	for(int i=0;i<num_key.length;i++) {
		if (key.startsWith(num_key[i])) {
			concatText(num_key[i]);		
			flag=true;
			break;
		}
	}	
	return flag;
}

// Concat text to textarea

private void concatText(String s) {

	if(newOperand) {
		textarea.setText("");
		newOperand = false;
	}
	textarea.setText(textarea.getText().concat(s));
}

// Handle the operator

private boolean handleOperator(String key) {

	boolean flag = false;

	// Transfer number displayed to register
	Operand2 = Double.valueOf(textarea.getText()).doubleValue();

	// Do operation
	switch(operator) {
	case 0:
		Operand1 = Operand2;
		break;
	case 1:
		Operand1 += Operand2;
		break;
	case 2:
		Operand1 -= Operand2;
		break;
	case 3:
		Operand1 *= Operand2;
		break;
	case 4:
		Operand1 /= Operand2;
		break;
	case 5:
		Operand1 = Operand2;
		break;
	case 6:
		Operand1 = Math.pow(Operand1, Operand2);
		break;
	}

	// Check new operator

	if(key.startsWith("+")) {
		operator = 1;
		flag = true;
	}
	else if(key.startsWith("-")) {
		operator = 2;
		flag = true;
	}
	else if(key.startsWith("*")) {
		operator = 3;
		flag = true;
	}
	else if(key.startsWith("/")) {
		operator = 4;
		flag = true;
	}
	else if(key.startsWith("=")) {
		operator = 5;
		flag = true;
	}
	else if(key.startsWith("x^y")) {
		operator = 6;
		flag = true;
	}
	if(flag)  {
		newOperand = true;
		textarea.setText((new Double(Operand1)).toString());
	}
	return flag;
}


// Handle unary operators

private boolean handleUnaryOperator(String key) {

	boolean flag = false;
	double x = 0;
	double y = 0;
	boolean Invert = false; //Cinv.isSelected();
	boolean Degree = false; //Cdeg.isSelected();
	
	try {
		y = Double.valueOf(textarea.getText()).doubleValue();
	}
	catch (Exception e) {
		// Do nothing
	}
	if(key.startsWith("+//-")) {
		x = -1 * y;
		flag = true;
	}
	else if (key.startsWith("PI")) {
		x = Math.PI;
		flag = true;
	}
	else if (key.startsWith("C")) {
		textarea.setText("");
		flag = true;
	}
	else if (key.startsWith("1//x")) {
		x = 1/y;
		flag = true;
	}
	else if (key.startsWith("Log")) {
		x = Math.log(y)/Math.log(10);
		flag = true;
	}
	else if (key.startsWith("10^x")) {
		x = Math.pow(10,y);
		flag = true;
	}
	else if (key.startsWith("Ln")) {
		x = Math.log(y);
		flag = true;
	}
	else if (key.startsWith("Exp")) {
		x = Math.exp(y);
		flag = true;
	}
	else if (key.startsWith("x^2")) {
		x = y*y;
		flag = true;
	}
	else if (key.startsWith("SQRT")) {
		x = Math.sqrt(y);
		flag = true;
	}
	else if (key.startsWith("Sin")) {
		if(!Invert) {
			x = Math.sin(Degree?Math.PI*y/180:y);
		}
		else {
			x = Degree?180*Math.asin(y)/Math.PI:Math.asin(y);
		}
		flag = true;
	}
	else if (key.startsWith("Cos")) {
		if(!Invert) {
			x = Math.cos(Degree?Math.PI*y/180:y);
		}
		else {
			x = Degree?180*Math.acos(y)/Math.PI:Math.acos(y);
		}
		flag = true;
	}
	else if (key.startsWith("Tan")) {
		if(!Invert) {
			x = Math.tan(Degree?Math.PI*y/180:y);
		}
		else {
			x = Degree?180*Math.atan(y)/Math.PI:Math.atan(y);
		}
		flag = true;
	}
	if(flag) {
		newOperand = true;
		if(x==0.0) {
			textarea.setText(" ");
		}
		else {
			textarea.setText(String.valueOf(x));
		}
	}
	return flag;
 }

}
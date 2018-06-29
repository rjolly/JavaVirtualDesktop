/*
 * @(#)BasicTreeCellRenderer.java	1.13 97/12/14
 * 
 * Copyright (c) 1997 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 */

import com.sun.java.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.util.*;
import com.sun.java.swing.tree.*;

/**
 * @version 1.13 12/14/97
 * @author Rob Davis
 * @author Ray Ryan
 * @author Scott Violet
 */

public class FMTreeCellRenderer extends JLabel implements TreeCellRenderer
{
    /** Is the value currently selected. */
    protected boolean selected;

    // Icons
    /** Icon used to show non-leaf nodes that aren't expanded. */
    transient protected Icon closedIcon;

    /** Icon used to show leaf nodes. */
    transient protected Icon leafIcon;

    /** Icon used to show non-leaf nodes that are expanded. */
    transient protected Icon openIcon;

    // Colors
    /** Color to use for the foreground for selected nodes. */
    protected Color textSelectionColor;

    /** Color to use for the foreground for non-selected nodes. */
    protected Color textNonSelectionColor;

    /** Color to use for the background when a node is selected. */
    protected Color backgroundSelectionColor;

    /** Color to use for the background when the node isn't selected. */
    protected Color backgroundNonSelectionColor;

    /** Color to use for the background when the node isn't selected. */
    protected Color borderSelectionColor;

    /**
      * Returns a new instance of BasicTreeCellRenderer.  Alignment is
      * set to left aligned.
      */
    public FMTreeCellRenderer() {
	setHorizontalAlignment(JLabel.LEFT);

	setLeafIcon(UIManager.getIcon("Tree.leafIcon"));
	setClosedIcon(UIManager.getIcon("Tree.closedIcon"));
	setOpenIcon(UIManager.getIcon("Tree.openIcon"));

	setTextSelectionColor(UIManager.getColor("Tree.textSelectionColor"));
	setTextNonSelectionColor(UIManager.getColor("Tree.textNonSelectionColor"));
	setBackgroundSelectionColor(UIManager.getColor("Tree.backgroundSelectionColor"));
	setBackgroundNonSelectionColor(UIManager.getColor("Tree.backgroundNonSelectionColor"));
	setBorderSelectionColor(UIManager.getColor("Tree.borderSelectionColor"));
    }
	
    /**
      * Returns the default icon used to represent non-leaf nodes that are expanded.
      */
    public Icon getDefaultOpenIcon() {
	return openIcon;
    }

    /**
      * Returns the default icon used to represent non-leaf nodes that are not
      * expanded.
      */
    public Icon getDefaultClosedIcon() {
	return closedIcon;
    }

    /**
      * Returns the default icon used to represent leaf nodes.
      */
    public Icon getDefaultLeafIcon() {
	return leafIcon;
    }

    /**
      * Sets the icon used to represent non-leaf nodes that are expanded.
      */
    public void setOpenIcon(Icon newIcon) {
	openIcon = newIcon;
    }

    /**
      * Returns the icon used to represent non-leaf nodes that are expanded.
      */
    public Icon getOpenIcon() {
	return openIcon;
    }

    /**
      * Sets the icon used to represent non-leaf nodes that are not expanded.
      */
    public void setClosedIcon(Icon newIcon) {
	closedIcon = newIcon;
    }

    /**
      * Returns the icon used to represent non-leaf nodes that are not
      * expanded.
      */
    public Icon getClosedIcon() {
	return closedIcon;
    }

    /**
      * Sets the icon used to represent leaf nodes.
      */
    public void setLeafIcon(Icon newIcon) {
	leafIcon = newIcon;
    }

    /**
      * Returns the icon used to represent leaf nodes.
      */
    public Icon getLeafIcon() {
	return leafIcon;
    }

    /**
      * Sets the color the text is drawn with when the node is selected.
      */
    public void setTextSelectionColor(Color newColor) {
	textSelectionColor = newColor;
    }

    /**
      * Returns the color the text is drawn with when the node is selected.
      */
    public Color getTextSelectionColor() {
	return textSelectionColor;
    }

    /**
      * Sets the color the text is drawn with when the node isn't selected.
      */
    public void setTextNonSelectionColor(Color newColor) {
	textNonSelectionColor = newColor;
    }

    /**
      * Returns the color the text is drawn with when the node isn't selected.
      */
    public Color getTextNonSelectionColor() {
	return textNonSelectionColor;
    }

    /**
      * Sets the color to use for the background if node is selected.
      */
    public void setBackgroundSelectionColor(Color newColor) {
	backgroundSelectionColor = newColor;
    }


    /**
      * Returns the color to use for the background if node is selected.
      */
    public Color getBackgroundSelectionColor() {
	return backgroundSelectionColor;
    }

    /**
      * Sets the background color to be used for non selected nodes.
      */
    public void setBackgroundNonSelectionColor(Color newColor) {
	backgroundNonSelectionColor = newColor;
    }

    /**
      * Returns the background color to be used for non selected nodes.
      */
    public Color getBackgroundNonSelectionColor() {
	return backgroundNonSelectionColor;
    }

    /**
      * Sets the color to use for the border.
      */
    public void setBorderSelectionColor(Color newColor) {
	borderSelectionColor = newColor;
    }

    /**
      * Returns the color the border is drawn.
      */
    public Color getBorderSelectionColor() {
	return borderSelectionColor;
    }


    /**
      * Configures the renderer based on the passed in components.
      * The value is set from messaging value with toString().
      * The foreground color is set based on the selection and the icon
      * is set based on on leaf and expanded.
      */
    public Component getTreeCellRendererComponent(JTree tree, Object value,
						  boolean sel,
						  boolean expanded,
						  boolean leaf, int row,
						  boolean hasFocus) {
	String         stringValue = tree.convertValueToText(value, sel,
					  expanded, leaf, row, hasFocus);

	setText(stringValue);
	if(sel)
	    setForeground(getTextSelectionColor());
	else
	    setForeground(getTextNonSelectionColor());
	if (leaf) {
	    setIcon(getLeafIcon());
	} else if (expanded) {
	    setIcon(getOpenIcon());
	} else {
	    setIcon(getClosedIcon());
	}
	    
	selected = sel;

	return this;
    }

    /**
      * Paints the value.  The background is filled based on selected.
      */
    public void paint(Graphics g) {
	Color bColor;

	if(selected) {
	    bColor = getBackgroundSelectionColor();
	} else {
	    bColor = getBackgroundNonSelectionColor();
	    if(bColor == null)
		bColor = getBackground();
	}
	if(bColor != null) {
	    Icon currentI = getIcon();

	    g.setColor(bColor);
	    if(currentI != null && getText() != null) {
		int offset = (currentI.getIconWidth() + getIconTextGap());

		g.fillRect(offset, 0, getWidth() - 1 - offset,
			   getHeight() - 1);
	    } else {
		g.fillRect(0, 0, getWidth()-1, getHeight()-1);
	    }
	}
	if (selected) {
	    g.setColor(getBorderSelectionColor());
	    g.drawRect(0, 0, getWidth()-1, getHeight()-1);
	}
	super.paint(g);
    }

    public Dimension getPreferredSize() {
	Dimension        retDimension = super.getPreferredSize();

	if(retDimension != null)
	    retDimension = new Dimension(retDimension.width + 3,
					 retDimension.height);
	return retDimension;
    }

}
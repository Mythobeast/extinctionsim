package com.beastware.simbed;

import java.awt.*;
import javax.swing.*;

public class SimBed extends JPanel 
{
	
	public SimBed(JFrame iFrame)
	{
		mFrame = iFrame;
		mApplet = null;
		mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mFrame.add(this);
	}
	
	public SimBed(JApplet iApplet)
	{
		mFrame = null;
		mApplet = iApplet;
		mApplet.add(this);
	}
	
	public SimBed(String[] args) throws Exception
	{
		if ( args.length < 1 )
			throw new Exception("You must specify Application or Applet when creating a SimBed");
		mFrame = new JFrame();
		mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mFrame.add(this);
	}
	
	public void open(int iXSize, int iYSize, boolean iVisible )
	{
		if ( mFrame != null )
		{
			mFrame.setVisible(iVisible);
			Insets lInsets = mFrame.getInsets();
			System.out.print("Insets = " + lInsets.left + "," +
					lInsets.right + "," +
					lInsets.top + "," +
					lInsets.bottom + "\n");
			
			mFrame.setSize(iXSize + lInsets.left + lInsets.right,
					iYSize + lInsets.top + lInsets.bottom);
		}		
		if ( mApplet != null)
		{
			mApplet.setVisible(iVisible);
			Insets lInsets = mApplet.getInsets();
			System.out.print("Insets = " + lInsets.left + "," +
					lInsets.right + "," +
					lInsets.top + "," +
					lInsets.bottom + "\n");
			
			mApplet.setSize(iXSize + lInsets.left + lInsets.right,
					iYSize + lInsets.top + lInsets.bottom);
		
		}
	}
	
	public void paintComponent(Graphics g)
	{
		mSimContainer.draw(g);
	}
	public void setContainer(SimContainer iValue)
	{
		mSimContainer = iValue;
	}
	
	public void addComponent(Component iValue)
	{
		if ( mFrame != null )
		{
			mFrame.add(iValue);
		}
		if ( mApplet != null)
		{
			mApplet.add(iValue);
		}
	}
	
	JFrame mFrame;
	JApplet mApplet;

	SimContainer mSimContainer;

	static final long serialVersionUID = 0;
}

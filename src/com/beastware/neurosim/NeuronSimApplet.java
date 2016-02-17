package com.beastware.neurosim;

import javax.swing.JApplet;
import java.awt.*;

public class NeuronSimApplet extends JApplet 
{
	/**
	 * 
	 */
	public void init()
	{
		mTheSim = new NeuroSim(this);
	}
	
	public void start()
	{
		mTheSim.run();
	}
	
	public void paint(Graphics g)
	{
		mTheSim.draw(g);
	}
	
	NeuroSim mTheSim;
	private static final long serialVersionUID = 5556142307085207585L;
}

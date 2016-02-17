package com.beastware.neurosim;

import javax.swing.JButton;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PauseButtonListener implements ActionListener 
{

	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		NeuroSim.getSimHead().togglePause();
		if ( NeuroSim.getSimHead().isSimPaused() )
			mButton.setText("Unpause");
		else
			mButton.setText("Pause");

	}

	public void setButton(JButton iValue)
	{
		mButton = iValue;
	}

	static public JButton getButton(int iXLoc, int iYLoc)
	{
		// Create the pause toggle button
		JButton lButton = new JButton("Pause");
		PauseButtonListener lListener = new PauseButtonListener(); 
		lButton.addActionListener(lListener);
		lButton.setLocation(iXLoc, iYLoc);
		lButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		lListener.setButton(lButton);
		return lButton;
	}

	JButton mButton;

}

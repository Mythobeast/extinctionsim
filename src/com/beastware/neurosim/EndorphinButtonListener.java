package com.beastware.neurosim;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import javax.swing.JButton;

public class EndorphinButtonListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if ( NeuroSim.getSimHead().isEndorphinBlocked() )
		{
			NeuroSim.getSimHead().unblockEndorphin();
			mButton.setText("Block Endorphin");
		}
		else
		{
			NeuroSim.getSimHead().blockEndorphin();
			mButton.setText("Unblock Endorphin");
		}

	}
	static public JButton getButton(int iXLoc, int iYLoc)
	{
		// Create the pause toggle button
		JButton lButton = new JButton(" Block Endorphin ");
		EndorphinButtonListener lListener = new EndorphinButtonListener(); 
		lButton.addActionListener(lListener);
		lButton.setLocation(iXLoc, iYLoc);
		lButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		lListener.setButton(lButton);
		return lButton;
	}
	
	public void setButton(JButton iValue)
	{
		mButton = iValue;
	}
	
	JButton mButton;

}

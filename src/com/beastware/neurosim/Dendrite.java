package com.beastware.neurosim;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;

import com.beastware.simbed.ClickTrack;

public class Dendrite  implements Comparable<Dendrite> 
{
	public Dendrite(Neuron iParent, Neuron iListenTo)
	{
		mID = gDendriteId;
		gDendriteId++;

		mOwner = iParent;
		mConnection = iListenTo;
		mWeight = 50.0;
		mTriggered = -100;

		iListenTo.addListener(this);
		
		mStartX = iListenTo.getX() + (int)Neuron.gAxiomLength;
		mStartY = iListenTo.getY();
		mEndX = iParent.getX();
		mEndY = iParent.getY();

	}
	
	public Dendrite ( Dendrite iSource )
	{

		mWeight = iSource.mWeight;
		
		// SyncStep value when last triggered
		mTriggered = iSource.mTriggered; 
		
		mConnection = iSource.mConnection;
		mOwner = iSource.mOwner;
		
		mStartX = iSource.mStartX; 
		mStartY = iSource.mStartY; 
		mEndX   = iSource.mEndX; 
		mEndY   = iSource.mEndY;

		mID = gDendriteId;
		gDendriteId++;
	}
	
	public int trigger()
	{
		int lRetVal = 0;

		lRetVal = mOwner.stimulate(getStimStrength(),this);
		if ( lRetVal >= 2 || mConnection.isTrigger() )
			mCausedFiring = ClickTrack.getClicks();
		if( mWeight > ( gMinWeight + 1.0) )
		{
			mWeight-= 1.0;
		}
		mTriggered = ClickTrack.getClicks();
		return lRetVal;
	}
	
	public double getStimStrength()
	{
		// lRecency is a quick imitation of the Rest Principle
		int lRecency = ClickTrack.getClicks() - mTriggered;

		if ( lRecency < NeuroSim.gRestTaper.getLength())
		{
			return mWeight * NeuroSim.gRestTaper.get(NeuroSim.gRestTaper.getLength() - lRecency);
		}
		else
		{
			return mWeight;
		}
	}
	
	public void addEndorphin(double iWeight)
	{
		int lRecency = ClickTrack.getClicks() - mCausedFiring;

		
		if ( lRecency > NeuroSim.gEndorphinTaper.getLength() )
		{
			return;
		}
		
		double lRecencyFactor = NeuroSim.gEndorphinTaper.get(lRecency);
		double lSaturationFactor = (Neuron.gStimThreshold-mWeight)/Neuron.gStimThreshold;
		
//		if ( mWeight > 60 )
//		{
//			System.out.print("D:" + mID + " " + mWeight + " + ("
//				+ iWeight + " * "
//				+ lSaturationFactor + " * "
//				+ lRecencyFactor + ") = ");
//		}

		double lAddedWeight = iWeight * lSaturationFactor * lRecencyFactor;
		mWeight += lAddedWeight;
		
		if ( mWeight > Neuron.gStimThreshold)
			mWeight = Neuron.gStimThreshold;
		if ( mWeight < gMinWeight)
			mWeight = gMinWeight;
//		if ( mWeight > 60 )
//		{
//			System.out.print( mWeight + "\n");
//		}
	}

	public void draw(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g; 

		double lStimRange = Neuron.gStimThreshold - gMinWeight;
		
		int lWidth = (int)(((mWeight - gMinWeight)/lStimRange) * 5);
		
		Color lColor = null;
		
		if ( mTriggered >= ClickTrack.getClicks() )
		{
			lColor = Color.yellow;
//			g2.setStroke(new BasicStroke(3));
//			g2.setColor(Color.gray);
//			g.drawLine( mStartX, mStartY, mEndX, mEndY );
//			
//			g2.setStroke(new BasicStroke(1));
//			g2.setColor(Color.yellow);
//			g.drawLine( mStartX, mStartY, mEndX, mEndY );
		}
		else
		{
			lColor = Color.black;
//			g2.setStroke(new BasicStroke(3));
//			
//			float lRed = (float)((mWeight - gMinWeight)/Neuron.gStimThreshold);
//			Color lDendriteCharge = new Color(lRed,0.0f,0.0f);
//			
//			g2.setColor(lDendriteCharge);
//			g.drawLine( mStartX, mStartY, mEndX, mEndY );
		}
		g2.setStroke(new BasicStroke(lWidth));
		g2.setColor(lColor);
		g.drawLine( mStartX, mStartY, mEndX, mEndY );
	}

	
	/**
	 * @return the mWeight
	 */
	public double getWeight() {
		return mWeight;
	}
	
	/**
	 * @param weight the mWeight to set
	 */
	public void setWeight(double weight) {
		if ( weight > Neuron.gStimThreshold)
			weight = Neuron.gStimThreshold;
		if ( weight < gMinWeight)
			weight = gMinWeight;
		
		mWeight = weight;
	}
	/**
	 * @return the mConnection
	 */
	public Neuron getConnection() {
		return mConnection;
	}
	/**
	 * @param connection the mConnection to set
	 */
	public void setConnection(Neuron connection) {
		mConnection = connection;
	}
	/**
	 * @return the mOwner
	 */
	public Neuron getOwner() {
		return mOwner;
	}
	/**
	 * @param owner the mOwner to set
	 */
	public void setOwner(Neuron owner) {
		mOwner = owner;
	}

    @Override
    public int compareTo(Dendrite s1)
    {
    	if ( mID < s1.mID )
    		return -1;
    	if ( mID > s1.mID )
    		return 1;
    	return 0;
    }

    @Override 
    public int hashCode()
    {
    	return mID;
    }

    public double calculateCraving()
    {
//    	java.text.DecimalFormat df = new java.text.DecimalFormat("###.####");

    	double lHoldCrave = mOwner.calculateCraving(); 
//    	System.out.print("D:" + mID + " craving = " + df.format(lHoldCrave) + " * (" 
//    			+ df.format(mWeight) + "/" + Neuron.gStimThreshold + ") = ");

//    	lHoldCrave *= mWeight/Neuron.gStimThreshold;
    	lHoldCrave *= getStimStrength()/Neuron.gStimThreshold;

//    	System.out.print(df.format(lHoldCrave) + "\n");
    	return lHoldCrave;
    }

	private double mWeight;
	static private double gMinWeight =  50;
//	static private double gStrengthRange = 150;
	
	// SyncStep value when last triggered
	private int mTriggered;
	private int mCausedFiring;
	
	private Neuron mConnection;
	private Neuron mOwner;
	
	private int mStartX, mStartY, mEndX, mEndY;

	private int mID;
	static private int gDendriteId;
}

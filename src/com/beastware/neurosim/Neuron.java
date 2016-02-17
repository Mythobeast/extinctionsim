package com.beastware.neurosim;

import com.beastware.simbed.*;

import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

import java.awt.*;
import java.awt.geom.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
/*
 * This class represents a neuron
 * 
 * Note: this class has a natural ordering that is inconsistent with equals.
 */

public class Neuron implements Comparable<Neuron> 
{
	public Neuron(int iMaxX, int iMaxY, int iOffsetX, int iOffsetY)
	{
		mXLoc = (int)(Math.random() * iMaxX) + iOffsetX;
		mYLoc = (int)(Math.random() * iMaxY) + iOffsetY;
		mIsTrigger = false;
		mIsTerminal = false;
		mFiredClick = -1;
		mStimulationLevel = Math.random() * gStimThreshold;
		mListeners = new ArrayList<Dendrite>();
		mUpstream = new ArrayList<Dendrite>();
		mID = gNeuronId;
		gNeuronId++;
	}

	public Neuron(int iX, int iY, double iStimLevel)
	{
		mID = gNeuronId;
		gNeuronId++;

		mXLoc = iX;
		mYLoc = iY;
		mIsTrigger = false;
		mIsTerminal = false;
		mFiredClick = -1;
		mStimulationLevel = Math.random() * gStimThreshold;
		mListeners = new ArrayList<Dendrite>();
		mUpstream = new ArrayList<Dendrite>();
	}

	public Neuron(Neuron iSource)
	{
		mID = gNeuronId;
		gNeuronId++;
	    
		mXLoc = iSource.mXLoc;
		mYLoc = iSource.mYLoc;
		
		mIsTrigger = iSource.mIsTrigger;

		mFiredClick = iSource.mFiredClick;
		mStimulationLevel = iSource.mStimulationLevel;
		
		mIsTerminal = iSource.mIsTerminal;
		mEndStrength = iSource.mEndStrength;
		
		mListeners = iSource.mListeners;
		mUpstream = iSource.mUpstream;
	
	}

	public boolean isTrigger()
	{
		return mIsTrigger;
	}

	public boolean isTerminal()
	{
		return mIsTerminal;
	}
	
	public void setTrigger()
	{
		mIsTrigger = true;
	}

	public void setTerminal()
	{
		mIsTerminal = true;
		mEndStrength = 0;
	}
	
	public void setEndorphinStrength(double iValue)
	{
		mEndStrength = iValue;
	}
	
	public boolean hasNoListeners()
	{
		return mListeners.isEmpty();
	}
	
	public void draw(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g; 
		
		if ( mIsTrigger )
		{
			g2.setStroke(new BasicStroke(1));
			
			g2.setPaint(Color.white);
			Shape bar = new Rectangle2D.Float( 0.0f, (float)mYLoc-6, (float)mXLoc+12, (float)12.0f); 
			g2.draw(bar);
			g2.fill(bar);
			
			g2.setPaint(Color.green);
			Shape circle = new Ellipse2D.Float((float)mXLoc, (float)mYLoc-5, (float)gAxiomLength, 10.0f);

		    g2.draw(circle);
		    g2.fill(circle);
		    return;
		}
		
		if ( mIsTerminal )
		{
			g2.setStroke(new BasicStroke(1));
			if ( mEndStrength < 0.0001 )
			{
				g2.setColor(Color.black);
			}
			else
			{
				g2.setColor(Color.blue);
			}
			Shape circle = new Ellipse2D.Float((float)mXLoc, (float)mYLoc-5, 10,10);
		    g2.draw(circle);
			if ( !NeuroSim.getSimHead().isEndorphinBlocked() || mEndStrength < 0.0001 )
			{
				g2.fill(circle);
			}
			return;
		}
		Color lNeuronColor = null;
		if ( mStimulationLevel == 0.0 &&
			mFiredClick == ClickTrack.getClicks() )
		{
			lNeuronColor = Color.yellow;
		}
		else
		{
			// color moves from min = 200,200,0
			// to max = 250,0,0
			float lColorPoint = (float)(mStimulationLevel / gStimThreshold);
			if ( lColorPoint > 1 )
				lColorPoint = 1;

			float lRedBase = 0;
			float lGreenBase = 0;
			float lBlueBase = 0;
			
			float lRedRange = 250;
			float lGreenRange = 0;
			float lBlueRange = 0;
			
			int lRed   = (int)(lRedBase   + lRedRange   * lColorPoint);
			int lGreen = (int)(lGreenBase + lGreenRange * lColorPoint);
			int lBlue  = (int)(lBlueBase  + lBlueRange  * lColorPoint);
			
			if ( lRed > 255 || lGreen > 255 || lBlue > 255)
			{
				System.out.print("Color Overflow" + lRed + ", " + lGreen + ", " + lBlue + ", ");
			}
			if ( lRed < 0 || lGreen < 0 || lBlue < 0)
			{
				System.out.print("Color Underflow" + lRed + ", " + lGreen + ", " + lBlue + ", ");
			}
			

			lNeuronColor = new Color(lRed,lGreen,lBlue);
		}
		g2.setColor(lNeuronColor);
		g2.setStroke(new BasicStroke(1));

		Shape circle = new Ellipse2D.Float((float)mXLoc-5, (float)mYLoc-5, 10,10);
//	    g2.draw(circle);
	    g2.fill(circle);
		g2.setStroke(new BasicStroke(3));
		g.drawLine(mXLoc, mYLoc, mXLoc+(int)gAxiomLength, mYLoc);

	}

	@Override 
	public boolean equals(Object o) 
	{
        if (!(o instanceof Neuron))
            return false;
        Neuron iRight = (Neuron)o;

        return ( iRight.mXLoc == mXLoc && iRight.mYLoc == mYLoc);
	}

	public boolean isNear(Object o) 
	{
        if (!(o instanceof Neuron))
            return false;
        Neuron iRight = (Neuron)o;
        
        // Definition describes a boundary around each neuron
        
        if ( ( iRight.mYLoc > mYLoc + gNeuronSpacingY) ||
        	( iRight.mYLoc < mYLoc - gNeuronSpacingY) )
        	return false;
        
        if ( ( iRight.mXLoc > mXLoc + (gAxiomLength + gNeuronSpacingX) ) ||
            	( iRight.mXLoc < mXLoc - (gAxiomLength + gNeuronSpacingX) ) )
            	return false;
        
        return true;
    }
	
	public int stimulate(double iWeight,Dendrite iStimulator)
	{
		int lRetVal = 0;
		mStimulationLevel += iWeight;
		
		if ( mIsTerminal )
		{
			if (mEndStrength > 0.0001)
			{
				if ( mFiredClick != ClickTrack.getClicks())
				{
					NeuroSim.getSimHead().addEndorphin(mEndStrength);
//					System.out.print("N:" + mID + " just released strength " + mEndStrength + " endorphin flush\n");
					mFiredClick = ClickTrack.getClicks();
					NeuroSim.getSimHead().addHit(mFiredClick);
				}
				lRetVal = 2;
			}
		} else if ( mStimulationLevel > gStimThreshold )
		{
			lRetVal = overflow();
		}
//		System.out.print("N:" + mID + ", stimulate exit: mStimulationLevel = " + mStimulationLevel + "\n");
		return lRetVal;
	}
	
	public int overflow()
	{
		int lRetVal = 0;
		if ( mFiredClick != ClickTrack.getClicks())
		{
			int lDepth;
			for ( Dendrite lDendrite : mListeners)
			{
				lDepth = lDendrite.trigger();
				if ( lDepth > lRetVal)
					lRetVal = lDepth;
			}
			mStimulationLevel = 0.0;
			mFiredClick = ClickTrack.getClicks();
		}
		return lRetVal+1;
	}

    @Override 
    public int hashCode()
    {
    	return mID;
//    	return mXLoc * 65536 + mYLoc;
    }
    
    @Override
    public int compareTo(Neuron s1)
    {
    	if ( s1.mXLoc < mXLoc )
    		return 1;
    	if ( s1.mXLoc > mXLoc )
    		return -1;
    	if ( s1.mYLoc < mYLoc )
    		return 1;
    	if ( s1.mYLoc > mYLoc )
    		return -1;
    	return 0;
    }
    
    public void randomize(int iMaxX, int iMaxY, int iOffsetX, int iOffsetY)
    {
		mXLoc = (int)(Math.random() * iMaxX) + iOffsetX;
		mYLoc = (int)(Math.random() * iMaxY) + iOffsetY;
    }
    
    public Dendrite extendDendrite(Neuron iTarget)
    {
    	Dendrite mNewDendrite = new Dendrite(this,iTarget);
    	mUpstream.add(mNewDendrite);
    	return mNewDendrite;
    }
    
    public void addListener(Dendrite iListener)
    {
    	mListeners.add(iListener);
    }
    
    public double calculateCraving()
    {
    	if ( mIsTerminal )
    	{
//    		if ( mEndStrength > gNormalMaxTS )
    			return 1;
//    		else
//    			return 0;
    	}

    	double lRetVal = 0.0f;
    	double lNextCraving = 0.0f;
    	double lHoldSum;

//    	java.text.DecimalFormat df = new java.text.DecimalFormat("###.####");
    	for ( Dendrite lListener : mListeners )
    	{
    		lNextCraving = lListener.calculateCraving();
    		double lOverlap = lNextCraving * lRetVal;
    		lHoldSum = lNextCraving + lRetVal - lOverlap;
    		/*if ( lRetVal > 0.0001 && lNextCraving > 0.0001)
    		{
    			System.out.print("N:" + mID + " next = " + df.format(lNextCraving) 
        			+ "\nCalc:" + df.format(lRetVal) + "+" + df.format(lNextCraving) 
        			+ " - " + df.format(lOverlap) + " = " + df.format(lHoldSum) 
        			+ "\n");
    		} */
    		lRetVal = lHoldSum;
    	}
    	return lRetVal;
    }
    
	int mXLoc;
	int mYLoc;
	
	private boolean mIsTrigger;

	private int mFiredClick;
	private double mStimulationLevel;
	
	private boolean mIsTerminal;
	private double mEndStrength;
	
	private List<Dendrite> mListeners; 
	private List<Dendrite> mUpstream;
	

	public int getX()           {  return mXLoc; }
	public void setX(int loc)   {  mXLoc = loc;  }
	public int getY()           {  return mYLoc; }
	public void setY(int loc)   {  mYLoc = loc;  }

	static public double gStimThreshold = 200.0;
	
	static public final int gAxiomLength = 10;
	static public int gNeuronSpacingY = 20;
	static public int gNeuronSpacingX = 20;
	
	private int mID;
	static private int gNeuronId;

}

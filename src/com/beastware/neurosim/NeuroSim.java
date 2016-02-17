package com.beastware.neurosim;

import com.beastware.simbed.*;

import java.util.*;
import java.awt.*;

import javax.swing.*;


import javax.swing.BoxLayout;

public class NeuroSim implements SimContainer {

	
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		NeuroSim.getSimHead(new JFrame()).run();
	}

	public NeuroSim(JFrame iFrame)
	{
		NeuroSim.setSimHead(this);

		// Create container
		mBed = new SimBed(iFrame);
		initSim();
	}

	public NeuroSim(JApplet iApplet)
	{
		NeuroSim.setSimHead(this);
		// Create container
		mBed = new SimBed(iApplet);
		initSim();
	}
	
	public void initSim()
	{
		// Create container
		mBed.setContainer(this);
		mBed.setLayout(new BoxLayout(mBed, BoxLayout.PAGE_AXIS));

		mBed.add(EndorphinButtonListener.getButton(200,200));
		mBed.add(PauseButtonListener.getButton(200,300));

		mEndorphinFlush = 0.0f;
		mEndorphinBlocked = false;
		mSimPaused = false;
		mLastTopCrave = 0.0f;
		mLastEndorphinFire = 0;

		// Initialize globals
		gRestTaper = new LogarithmicTaper(10);
		gEndorphinTaper = new LogarithmicTaper(5);

		mHundredTopCrave = new ArrayList<Float>();
		mEndorphinHistory = new TreeSet<Integer>();
		mHitTracker = new PeriodTracker(0,0,150,74);
		mCravingChart = new SmallChart(340,0,150,74);
		mCravingChart.setText("Highest Craving Index");

		do
		{
			createNetwork();
		}
		while ( confirmNetwork() == false);
	}
	
	public void createNetwork()
	{
		// Initialize Lists
		mNeuronList = new TreeSet<Neuron>();
		mTriggerList = new Vector<Neuron>();
		mTerminalList = new TreeSet<Neuron>();
		mDendriteList = new TreeSet<Dendrite>();

		populateNeuronList();
		wireUpNeurons(mNeuronList);
		reverseWireNeurons(mNeuronList);
		setEndorphinRelease();
	}
	
	public boolean confirmNetwork()
	{
		for( Neuron lOne : mTriggerList )
		{
			if (lOne.mXLoc > 100)
				return false;
		}
		for( Neuron lOne : mTerminalList )
		{
			if (lOne.mXLoc < 300)
				return false;
		}
		return true;
	}

	public void run()
	{
		gFullSimWidth  = (int)(gSimXExtents+gCravingBarWidth+10 
				+ Neuron.gAxiomLength + Neuron.gNeuronSpacingX); 
		gFullSimHeight = 	gSimYExtents + gStatusBarHeight + 10 + Neuron.gNeuronSpacingY;
		
		mBed.open( gFullSimWidth, gFullSimHeight, true);

		long lNow =  System.currentTimeMillis();
		long lNextExecution = lNow + 100;
		
		int lTriggerCount = mTriggerList.size();
		mCycleCount = 0;
		
		while ( mCycleCount < 500000 )
		{
			while ( mSimPaused )
			{
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
				
			ClickTrack.bumpClicks();

			double lDelay = mLastTopCrave * 200;
			if ( lDelay < 10.0f)
				lDelay = 10.0f;
			else if ( lDelay > 100.0f )
				lDelay = 100.0f;
			
			lNextExecution = System.currentTimeMillis() + (int)lDelay;
			
			float lFireChance = 2.0f/(float)lTriggerCount;
			
			for( Neuron lOneTrigger : mTriggerList )
			{
				if ( lFireChance > Math.random())
					lOneTrigger.overflow();
			}
			releaseEndorphin();

			
			
			mCycleCount++;
			mBed.repaint();
			lNow = System.currentTimeMillis();
			if ( lNow < lNextExecution )
			{
				try 
				{
					Thread.sleep( lNextExecution - lNow );
				}
				catch(  InterruptedException e )
				{
					System.exit(0);
				}
			}
		}
	}

	
	public void draw(Graphics g)
	{
//		System.out.print("******TRIGGERING REDRAW******\n");
		Graphics2D g2 = (Graphics2D)g; 

		g2.setColor(Color.gray);
		g.fillRect( 0, 0, gFullSimWidth, gFullSimHeight);

		g2.setColor(new Color(200,200,200));
		g.fillRect(0,0, gFullSimWidth, gStatusBarHeight);
		g.fillRect(0,0, gCravingBarWidth, gFullSimHeight);

		g2.setStroke(new BasicStroke(2));
		g2.setColor(Color.black);
		g.drawLine( 0, gStatusBarHeight, gFullSimWidth, gStatusBarHeight );
		
		for ( Neuron lOne : mNeuronList )
		{
			lOne.draw(g);
		}
		for ( Dendrite lOther : mDendriteList )
		{
			lOther.draw(g);
		}
		
		float lMaxCrave = 0;
		float lThisCrave = 0.0f;
		java.text.DecimalFormat df = new java.text.DecimalFormat("###.####");
		for ( Neuron lTrigger : mTriggerList)
		{
			lThisCrave = (float)lTrigger.calculateCraving();
			if ( lThisCrave > lMaxCrave)
				lMaxCrave = lThisCrave;
			g.drawString(df.format(lThisCrave),5,lTrigger.mYLoc+5);
		}
		addMaxCrave(lMaxCrave);
		
		g2.setStroke(new BasicStroke(1));
		g2.setColor(Color.black);
		g.drawLine( gCravingBarWidth, gStatusBarHeight, gCravingBarWidth, gFullSimHeight );
		
		mHitTracker.draw(g);
		mCravingChart.draw(g);
		g2.setColor(Color.black);
		Font f = new Font("Dialog", Font.PLAIN, 14);
		g.setFont(f);
		g.drawString("Cycle Count: " + mCycleCount, 190, 70);
	
	}

	public void populateNeuronList()
	{
//		int X, Y;
		
//		for ( Y = 20; Y < 100; Y+= 20  )
//		{
//			for ( X = 20; X < 150; X+= 40)
//			{
//				mNeuronList.add(new Neuron(X,Y));
//			}
//		}
		
		boolean lMatchFound = false;
		Neuron lHolder = null;
	
		int lFailCount = 0;
		lHolder = new Neuron(gSimXExtents,gSimYExtents, gCravingBarWidth+10, gStatusBarHeight+10);
		
		while ( lFailCount < 50 )
		{
			mNeuronList.add(lHolder);
			lHolder = new Neuron(gSimXExtents,gSimYExtents,gCravingBarWidth+10,gStatusBarHeight+10);
	
			lFailCount = 0;
			lMatchFound = true;
	
			while ( lMatchFound == true && lFailCount < 50 )
			{
				lHolder.randomize(gSimXExtents,gSimYExtents,gCravingBarWidth+10,gStatusBarHeight+10);
				lMatchFound = false;
				for ( Neuron lTester : mNeuronList )
				{
					if ( lTester.isNear(lHolder) )
					{
						lMatchFound = true;
						lFailCount++;
						break;
					}
				}
			}
		}
	}

	
	private void wireUpNeurons(SortedSet<Neuron> iNeuronList)
	{
		Neuron lNearNeurons[];
		int lNeuronCount = 0;
		
		for ( Neuron lCurrentNeuron : iNeuronList )
		{
			lNearNeurons = new Neuron[100];
			lNeuronCount = 0;

			for ( Neuron lPredicessor : iNeuronList )
			{
				if ( lPredicessor.getX() < lCurrentNeuron.getX() - (Neuron.gAxiomLength + gDendriteReachX*1.5) )
					continue;
				if ( lPredicessor.getX() > lCurrentNeuron.getX() - (Neuron.gAxiomLength +  7) )
					break;
				
				if (  (lPredicessor.getY() > lCurrentNeuron.getY() - gDendriteReachY && 
						lPredicessor.getY() < lCurrentNeuron.getY() + gDendriteReachY ) )
				{
					lNearNeurons[lNeuronCount] = lPredicessor;
					lNeuronCount++;
				}
			}
			if ( lNeuronCount == 0 )
			{
				lCurrentNeuron.setTrigger();
				mTriggerList.add(lCurrentNeuron);
			}
			else
				
				
//			{
//				int lSelection = (int)Math.floor( Math.random() * lNeuronCount);
//
//				Dendrite lHoldDendrite = lCurrentNeuron.extendDendrite(lNearNeurons[lSelection]);
//				mDendriteList.add(lHoldDendrite);
//			}
				
				
				
			if ( lNeuronCount <= 3 )
			{
				
				for( int lTrak = 0; lTrak < lNeuronCount; lTrak++ )
				{
					Dendrite lHoldDendrite = lCurrentNeuron.extendDendrite(lNearNeurons[lTrak]);
					mDendriteList.add(lHoldDendrite);
				}
			}
			else
			{
				int lNeuronsToWire = lNeuronCount/2;
				int lCursor = 0;
				while ( lNeuronsToWire > 0 )
				{
					while ( lNearNeurons[lCursor] == null )
					{
						if ( lCursor >= lNeuronCount )
							lCursor = 0;
						else
							lCursor++;
					}
					if ( Math.random() > 0.5 )
					{
						Dendrite lHoldDendrite = lCurrentNeuron.extendDendrite(lNearNeurons[lCursor]); 
						mDendriteList.add(lHoldDendrite);
						System.out.print("Connecting N:" + lCurrentNeuron.hashCode() 
								+ " to N:" + lNearNeurons[lCursor].hashCode()  
								+ " via D:" + lHoldDendrite.hashCode() + "\n"  );
						lNearNeurons[lCursor] = null;
						lNeuronsToWire--;
					}
					lCursor++;
				}
			}
		}
	}

	private void reverseWireNeurons(SortedSet<Neuron> iNeuronList)
	{
		Neuron lNearNeurons[];
		int lNeuronCount = 0;
		
		for ( Neuron lCurrentNeuron : iNeuronList )
		{
			if ( !lCurrentNeuron.hasNoListeners() )
				continue;

			lNearNeurons = new Neuron[100];
			lNeuronCount = 0;

			for ( Neuron lFollower : iNeuronList )
			{
				if ( lFollower.getX() < lCurrentNeuron.getX() + (Neuron.gAxiomLength + 7) )
					continue;
				if ( lFollower.getX() > lCurrentNeuron.getX() + (Neuron.gAxiomLength + gDendriteReachX + 7) )
					break;
				
				if ( (lFollower.getY() > lCurrentNeuron.getY() - gDendriteReachY && 
						lFollower.getY() < lCurrentNeuron.getY() + gDendriteReachY ) )
				{
					lNearNeurons[lNeuronCount] = lFollower;
					lNeuronCount++;
				}
			}
			if ( lNeuronCount == 0 )
			{
				lCurrentNeuron.setTerminal();
				mTerminalList.add(lCurrentNeuron);
			}
			else
			{
				int lNewConnection = (int)Math.floor((Math.random() * lNeuronCount));
				Dendrite lHoldDendrite = lNearNeurons[lNewConnection].extendDendrite(lCurrentNeuron);
				mDendriteList.add(lHoldDendrite);
			}
		}
		
	}
	
	public void setEndorphinRelease()
	{
		int lTerminalId = (int)Math.floor(Math.random() * mTerminalList.size());
		
		for( Neuron lOne : mTerminalList )
		{
			if ( lTerminalId < 1 )
			{
				lOne.setEndorphinStrength(20.0f);
				return;
			}
			lTerminalId--;
		}
	}
	
	static public NeuroSim getSimHead()
	{
		if ( gSimHead == null)
		{
			gSimHead = new NeuroSim(new JFrame());
		}
		return gSimHead;
	}
	
	static public void setSimHead(NeuroSim iValue)
	{
		gSimHead = iValue;
	}

	static public NeuroSim getSimHead(JFrame iFrame)
	{
		if ( gSimHead == null)
		{
			String lTempArgs[] = new String[1];
			lTempArgs[0] = new String("NeuroSim");
			gSimHead = new NeuroSim(iFrame);
		}
		return gSimHead;
	}

	
	public void addEndorphin(double iStrength)
	{
		if ( !mEndorphinBlocked )
		{
			mEndorphinFlush += iStrength;
		}
		mLastEndorphinFire = ClickTrack.getClicks();
		mEndorphinHistory.add(mLastEndorphinFire);
	}
	
	public void releaseEndorphin()
	{
		if ( mEndorphinFlush > 0.00001f )
		{
			for ( Dendrite lOneDendrite : mDendriteList)
			{
				lOneDendrite.addEndorphin(mEndorphinFlush);
			}
		}
		mEndorphinFlush = 0.0f;
	}
	
	public boolean isEndorphinBlocked()
	{
		return mEndorphinBlocked;
	}
	public boolean isSimPaused()
	{
		return mSimPaused;
	}
	
	public void blockEndorphin()
	{
		mEndorphinBlocked = true;
	}
	public void unblockEndorphin()
	{
		mEndorphinBlocked = false;
	}
	
	public void addHit(int lClick)
	{
		mHitTracker.add(lClick);
	}
	public void togglePause()
	{
		if ( mSimPaused )
			mSimPaused = false;
		else
			mSimPaused = true;
	}
	
	public void addMidCrave(double iValue)
	{
		mMidCrave.add((float)iValue);
	}
	
	
	public void addMaxCrave(double iValue)
	{
		mLastTopCrave = iValue;
		mHundredTopCrave.add((float)iValue);
		if ( mHundredTopCrave.size() > 199 )
		{
			float lSum = 0.0f;
			
			for( float lValue : mHundredTopCrave)
				lSum += lValue;
			
			lSum /= mHundredTopCrave.size();
			mCravingChart.add((int)(lSum*1000));
			mHundredTopCrave = new ArrayList<Float>();
		}
	}
	
	private SimBed mBed;
	
	private SortedSet<Neuron> mNeuronList;
	private Vector<Neuron> mTriggerList;
	private SortedSet<Neuron> mTerminalList;
	
	private SortedSet<Dendrite> mDendriteList;
	private SortedSet<Integer> mEndorphinHistory;

	private double mEndorphinFlush;
	private boolean mEndorphinBlocked;
	private boolean mSimPaused;
	private int mCycleCount;
	private int mLastEndorphinFire;
	
	private PeriodTracker mHitTracker;

	private ArrayList<Float> mMidCrave;
	private ArrayList<Float> mHundredTopCrave;
	private SmallChart mCravingChart;
	
	private double mLastTopCrave;
	
	// Static members	
	static private NeuroSim gSimHead;
	
	static public int gSimXExtents = 400;
	static public int gSimYExtents = 400;
	static public int gCravingBarWidth = 50;
	static public int gStatusBarHeight = 75;

	static public int gFullSimHeight;
	static public int gFullSimWidth;

	static public int gDendriteReachY = 40;
	static public int gDendriteReachX = 43;
	
	static public LogarithmicTaper gRestTaper;
	static public LogarithmicTaper gEndorphinTaper;
}

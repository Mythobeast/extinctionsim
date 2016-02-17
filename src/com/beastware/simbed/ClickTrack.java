package com.beastware.simbed;

public class ClickTrack {
	
	static public int getClicks()
	{
		return mClicks;
	}
	static public void bumpClicks()
	{
		mClicks++;
	}
	
/*	static public double getTaper(int iClicks)
	{
		return mTaper[iClicks];
	}
	
	static public int getTaperLength()
	{
		return mTaperLength;
	}
	
	public static void initTaper(int iMaxClicks)
	{
		mTaperLength = iMaxClicks;
		double lTaperRatio = determineRatio(iMaxClicks);
		double lTaperTrack = 1;
		mTaper = new double[iMaxClicks+1];
		mTaper[0] = 1;
		for ( int lIter = 1; lIter < iMaxClicks; lIter++)
		{
			lTaperTrack = lTaperTrack * lTaperRatio;
			mTaper[lIter] = lTaperTrack;
		}
		
	}
	
	static private double determineRatio(int iSteps)
	{
		double lLowEnd = 0.0;
		double lHighEnd = 1.0;
		double lCurrentRatio = (lLowEnd + lHighEnd)/2;
		
		int lTestResult = testRatio(lCurrentRatio);
		
		while (lTestResult != iSteps)
		{
			if ( lTestResult > iSteps)
			{
				lHighEnd = lCurrentRatio;
			}
			else
			{
				lLowEnd = lCurrentRatio;
			}
			lCurrentRatio = (lLowEnd + lHighEnd)/2;
			lTestResult = testRatio(lCurrentRatio);
		}
		return lCurrentRatio;
	}

	static private int testRatio(double iRatio)
	{
		double iValue = 1000.0;
		int lRetVal = 0;
		
		while (iValue > 1.0)
		{
			iValue *= iRatio;
			lRetVal++;
		}
		return lRetVal;
	}
	*/
	static int mClicks = 0;
	
	static int mTaperLength = 20;
	static double mTaper[];

}

package com.beastware.simbed;

public class LogarithmicTaper {
	
	public LogarithmicTaper(int iLength)
	{
		mLength = iLength;
		double lTaperRatio = determineRatio(mLength);
		double lTaperTrack = 1;
		mTaper = new double[mLength+1];
		mTaper[0] = 1;
		for ( int lIter = 1; lIter < mLength; lIter++)
		{
			lTaperTrack = lTaperTrack * lTaperRatio;
			mTaper[lIter] = lTaperTrack;
		}
		
	}
	
	public double get(int iClicks)
	{
		return mTaper[iClicks];
	}
	
	public int getLength()
	{
		return mLength;
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
	
	
	private int mLength;
	private double mTaper[];
}

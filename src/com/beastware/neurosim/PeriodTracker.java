package com.beastware.neurosim;

import java.util.ArrayList;
import java.awt.*;

public class PeriodTracker {
	
	PeriodTracker(int iXLoc, int iYLoc, int iWidth, int iHeight)
	{
		mXLoc = iXLoc;
		mYLoc = iYLoc;
		mWidth = iWidth;
		mHeight = iHeight;
		mPoints = new ArrayList<Integer>();
	}
	
	public void add(int iValue)
	{
		mPoints.add(iValue);
	}
	
	public void draw(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
		
		int lMaxDraw = (mWidth-1)/2;
		int lStartDraw = 0;
		int lIter = 0;
		
		if ( lMaxDraw < mPoints.size())
		{
			lStartDraw = mPoints.size() - lMaxDraw;
		}
		
		//Draw background square
		g2.setStroke(new BasicStroke(1));
		g2.setColor(Color.white);
		g.fillRect( mXLoc, mYLoc, mWidth, mHeight);

		Font f = new Font("Dialog", Font.PLAIN, 10);
		g.setFont(f);
		FontMetrics lTheMetrics = g.getFontMetrics();
		int lWordsSize = lTheMetrics.getAscent() + lTheMetrics.getDescent()+2;
		int lStartY = mYLoc + lWordsSize;

		String lMostRecent;

		int lMaxPeriod=0;
		int lThisPeriod=0;
		int lPreviousPoint=0;
		int lThisPoint=0;

		if( mPoints.size() < 2 )
		{
			lMostRecent = "Period between last hits: ----";
			if ( mPoints.size() > 0 )
				lMaxPeriod = mPoints.get(0);
			else
				lMaxPeriod = 1;
		}
		else
		{
			if ( lStartDraw == 0)
				lPreviousPoint = 0;
			else
				lPreviousPoint = mPoints.get(lStartDraw-1);
			for ( lIter = lStartDraw; lIter < mPoints.size(); lIter++ )
			{
				lThisPoint = mPoints.get(lIter);
				lThisPeriod = lThisPoint - lPreviousPoint;
				if ( lThisPeriod > lMaxPeriod)
					lMaxPeriod = lThisPeriod;
				lPreviousPoint = lThisPoint;
			}
			lMostRecent = "Period between last hits: " + lThisPeriod;
		}
		g2.setColor(Color.black);
		g.drawString(lMostRecent,mXLoc+5, mYLoc+lTheMetrics.getAscent());

		if ( mPoints.size() > 0 )
		{
			int lDrawX = mXLoc + 1;
			int lBottomY = mYLoc + mHeight - 1;
			int lMaxLength = lBottomY - lStartY;
			g2.setStroke(new BasicStroke(1));
			g2.setColor(Color.red);

			lPreviousPoint = 0;
			for ( lIter = lStartDraw; lIter < mPoints.size(); lIter++ )
			{
				lThisPoint = mPoints.get(lIter);
				lThisPeriod = lThisPoint - lPreviousPoint;
				int lLineLength = (lThisPeriod * lMaxLength)/lMaxPeriod;
				if (lLineLength < 1)
					lLineLength = 1;
				int lTopY = lBottomY - lLineLength;
				g.drawLine(lDrawX, lTopY, lDrawX, lBottomY);
				lDrawX += 2;
				lPreviousPoint = lThisPoint;
			}
		}
	}
	
	int mXLoc, mYLoc;
	int mWidth, mHeight;
	ArrayList<Integer> mPoints;
}

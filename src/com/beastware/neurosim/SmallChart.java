package com.beastware.neurosim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class SmallChart 
{
	SmallChart(int iXLoc, int iYLoc, int iWidth, int iHeight)
	{
		mXLoc = iXLoc;
		mYLoc = iYLoc;
		mWidth = iWidth;
		mHeight = iHeight;
		mPoints = new ArrayList<Integer>();
		mMyFont = new Font("Dialog", Font.PLAIN, 10);
	}
	
	public void add(int iValue)
	{
		mPoints.add(iValue);
	}
	
	public void setText(String iValue)
	{
		mTitle = iValue;
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
		g2.setColor(Color.white);
		g.fillRect( mXLoc, mYLoc, mWidth, mHeight);

		g.setFont(mMyFont);
		FontMetrics lTheMetrics = g.getFontMetrics();
		g2.setColor(Color.black);
		g.drawString(mTitle,mXLoc+5, mYLoc+lTheMetrics.getAscent());

		int lWordsSize = lTheMetrics.getAscent() + lTheMetrics.getDescent()+2;
		int lStartY = mYLoc + lWordsSize;

		if ( mPoints.size() > 0 )
		{
			int lMaxValue = 0;
			int lThisValue= 0;

			for ( lIter = lStartDraw; lIter < mPoints.size(); lIter++ )
			{
				lThisValue = mPoints.get(lIter);
				if ( lThisValue > lMaxValue)
					lMaxValue = lThisValue;
			}

			int lDrawX = mXLoc + 1;
			int lBottomY = mYLoc + mHeight - 1;
			int lMaxLength = lBottomY - lStartY;
			g2.setStroke(new BasicStroke(1));
			g2.setColor(Color.red);

			for ( lIter = lStartDraw; lIter < mPoints.size(); lIter++ )
			{
				lThisValue = mPoints.get(lIter);
				int lLineLength = (lThisValue * lMaxLength)/lMaxValue;
				if (lLineLength < 1)
					lLineLength = 1;
				int lTopY = lBottomY - lLineLength;
				g.drawLine(lDrawX, lTopY, lDrawX, lBottomY);
				lDrawX += 2;
			}
		}
	}
	
	int mXLoc, mYLoc;
	int mWidth, mHeight;
	String mTitle;
	Font mMyFont;
	ArrayList<Integer> mPoints;
}

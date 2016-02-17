package com.beastware.simbed;

public class Vector2D {
	
	Vector2D(double iX, double iY)
	{
		mX = iX;
		mY = iY;
	}
	
	Vector2D add(Vector2D iRight)
	{
		return new Vector2D(iRight.mX + mX, iRight.mY + mY);
	}

	double mX,mY;
	
}

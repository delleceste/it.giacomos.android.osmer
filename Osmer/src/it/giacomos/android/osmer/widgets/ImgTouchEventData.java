package it.giacomos.android.osmer.widgets;

import android.os.Bundle;

public class ImgTouchEventData 
{
	public ImgTouchEventData()
	{
		touchPointNormalizedX = touchPointNormalizedY = downPointNormalizedX = downPointNormalizedY = -1.0f;
		longPressed = false;
		zoneId = 0;
	}
	
	public boolean downPointValid()
	{
		return downPointNormalizedX >= 0.0f && downPointNormalizedY >= 0.0f;
	}
	
	public void invalidateTouchedPoint()
	{
		touchPointNormalizedX = touchPointNormalizedY = -1.0f;
	}

	public void saveState(Bundle outState) 
	{
		if(outState == null)
			return;
		outState.putBoolean("LongPressed", longPressed);
		outState.putFloat("touchNormX", this.touchPointNormalizedX);
		outState.putFloat("touchNormY", this.touchPointNormalizedY);
		outState.putFloat("downNormX", this.downPointNormalizedX);
		outState.putFloat("downNormY", this.downPointNormalizedY);
		outState.putInt("zoneId", zoneId);
	}
	
	public void restoreState(Bundle inState)
	{
		if(inState == null)
			return;
		touchPointNormalizedX = inState.getFloat("touchNormX");
		touchPointNormalizedY = inState.getFloat("touchNormY");
		downPointNormalizedX = inState.getFloat("downNormX");
		downPointNormalizedY = inState.getFloat("downNormY");
		longPressed = inState.getBoolean("LongPressed");
		zoneId = inState.getInt("zoneId");
	}
	
	public String repr()
	{
		String rep = "touch X (normalized [0-1]): " + touchPointNormalizedX + " touched Y " +
				touchPointNormalizedY + " long press " + longPressed + " down X (normalized [0-1]) " +  
				downPointNormalizedX + " down Y " + downPointNormalizedY + " zoneId " + zoneId;
		return rep;
	} 
	
//	private void saveState(Context ctx)
//	{
//		Settings s = new Settings(this.getContext());
//		s.setCurrentTouchedPoint(touchPointNormalizedX, touchPointNormalizedY);	
//		s.setCurrentDownPoint(downPointNormalizedX, downPointNormalizedY);
//		s.setOnLongPress(longPressed);
//		Log.e("MapWith...", "saving long press " + longPressed + " x " + touchedPointNormalizedX + 
//				"y: " + touchedPointNormalizedY);
//	}
	
//	private void loadTouchState()
//	{
//		Settings s = new Settings(this.getContext());
//		mCurrentTouchedPointNormalized.x = s.getCurrentXTouchedPointNormalized();
//		mCurrentTouchedPointNormalized.y = s.getCurrentYTouchedPointNormalized();
//		mDownPointNormalized.x = s.getCurrentXDownPointNormalized();
//		mDownPointNormalized.y = s.getCurrentYDownPointNormalized();
//		mOnLongPress = s.getOnLongPressed(); 
//
//		if(mDownPointNormalized.x >= 0.0f && mDownPointNormalized.y >= 0.0f && mOnLongPress)
//			longClickAction();
//		Log.e("MapWith...", "restoring long press " + mOnLongPress + ", x " + mCurrentTouchedPointNormalized.x + ", y " + mCurrentTouchedPointNormalized.y
//				+ " W " + getWidth() + " h " + getHeight());
//	}
	
	public float touchPointNormalizedX, touchPointNormalizedY, downPointNormalizedX, downPointNormalizedY;
	public boolean longPressed;
	public int zoneId;
	
}

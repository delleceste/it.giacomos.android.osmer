package it.giacomos.android.osmer.pro.observations;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * class used to store data inside the SituationImage widgets class
 * @author giacomo
 *
 */
public class SituationImageObservationData {
	public SituationImageObservationData(String location, String time, Bitmap bmp, String temp, 
			String waterTemp, String snow, String rain)
	{
		setSnow(snow);
		setWaterTemp(waterTemp);
		setTemp(temp);
		mRain = rain;
		mIcon = bmp;
		setLocation(location);
		setTime(time);
	}
	
	public String getSnow() {
		return mSnow;
	}
	public void setSnow(String mSnow) {
		this.mSnow = mSnow;
	}

	public String getWaterTemp() {
		return mWaterTemp;
	}

	public void setWaterTemp(String mWaterTemp) {
		this.mWaterTemp = mWaterTemp;
	}

	public String getTemp() {
		return mTemp;
	}

	public Bitmap getIcon()
	{
		return mIcon;
	}
	
	public void setTemp(String mTemp) {
		this.mTemp = mTemp;
	}

	public boolean hasIcon()
	{
		return mIcon != null;
	}
	
	public boolean hasSnow()
	{
		return mSnow != null && !mSnow.contains("---");
	}
	
	public boolean hasTemp()
	{
		return mTemp != null && !mTemp.contains("---");
	}
	
	public boolean hasWaterTemp()
	{
		return mWaterTemp != null && !mWaterTemp.contains("---");
	}
	
	public boolean hasRain() {
		return mRain != null && !mRain.contains("---");
	}
	
	public String getRain()
	{
		return mRain;
	}
	
	public String getLocation() {
		return mLocation;
	}

	public void setLocation(String mLocation) {
		this.mLocation = mLocation;
	}

	public String getTime() {
		return mTime;
	}

	public void setTime(String mTime) {
		this.mTime = mTime;
	}

	private String mSnow, mTemp, mWaterTemp , mLocation, mTime, mRain;
	Bitmap mIcon;


}

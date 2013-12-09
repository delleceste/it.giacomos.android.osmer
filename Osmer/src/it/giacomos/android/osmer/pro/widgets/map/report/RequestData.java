package it.giacomos.android.osmer.pro.widgets.map.report;

import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.pro.service.sharedData.ReportRequestNotification;
import android.content.Context;
import android.content.res.Resources;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class RequestData implements DataInterface
{
	private double latitude, longitude;
	private String writable;
	public String datetime, username, locality;
	private Marker mMarker;
	private MarkerOptions mMarkerOptions;
	private boolean mIsPublished;

	/** Builds a RequestData object
	 * 
	 * @param d the date time in string format
	 * @param user the user name
	 * @param local the locality associated to the request
	 * @param la the latitude
	 * @param lo the longitude
	 * @param wri writable attribute: if true: the user owns the request.
	 * @param isSatisfied if true: the request has been published on the database
	 */
	public RequestData(String d, String user, String local, 
			double la, double lo, String wri, boolean isPublished)
	{
		latitude = la;
		longitude = lo;
		writable = wri;
		username = user;
		datetime = d;
		locality = local;
		mIsPublished = isPublished;
	}

	public boolean isWritable()
	{
		return (writable.compareTo("w") == 0);
	}

	@Override
	public double getLatitude() {
		return latitude;
	}

	@Override
	public double getLongitude() {
		return longitude;
	}

	@Override
	public int getType() {
		return DataInterface.TYPE_REQUEST;
	}

	@Override
	public String getLocality() {
		return locality;
	}

	@Override
	public MarkerOptions buildMarkerOptions(Context ctx) 
	{
		Resources res = ctx.getResources();
		String  title;

		MarkerOptions reqMarkerO = new MarkerOptions();
		reqMarkerO.position(new LatLng(latitude, longitude));
		if(!isWritable())
		{
			title = res.getString(R.string.reportRequested);
			reqMarkerO.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
		}
		else
		{
			title = res.getString(R.string.reportRequest);
			reqMarkerO.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
			reqMarkerO.draggable(true);
		}

		reqMarkerO.title(title);
		reqMarkerO.snippet(mMakeSnippet(locality, ctx));

		return mMarkerOptions;
	}

	public void setMarker(Marker m)
	{
		mMarker = m;
	}

	@Override
	public Marker getMarker() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MarkerOptions getMarkerOptions() {
		return mMarkerOptions;
	}

	public boolean isPublished()
	{
		return mIsPublished;
	}
	
	public void setPublished(boolean published)
	{
		mIsPublished = published;
	}

	public void updateWithLocality(String locality, Context applicationContext) 
	{
		if(mMarker != null)
		{
			mMarker.setSnippet(mMakeSnippet(locality, applicationContext));
		}
	}

	private String mMakeSnippet(String locality, Context ctx)
	{
		Resources res = ctx.getResources();
		String snippet = username;
		if(locality.length() > 0)
			snippet += "\n" + res.getString(R.string.reportLocality) + ": " + locality;
		snippet += "\nlat. " + latitude + ", long. " + longitude;

		if(isWritable())
		{
			if(!mIsPublished)
				snippet += "\n" + res.getString(R.string.reportTouchBaloonToRequest);
			else
				snippet += "\n" + res.getString(R.string.reportTouchBaloonToCancel);
		}
		return snippet;
	}
}

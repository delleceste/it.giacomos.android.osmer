package it.giacomos.android.osmer.PROva.locationUtils;

public interface LocationServiceAddressUpdateListener 
{
	public abstract void onLocalityChanged(String locality, String subLocality, String address);
}

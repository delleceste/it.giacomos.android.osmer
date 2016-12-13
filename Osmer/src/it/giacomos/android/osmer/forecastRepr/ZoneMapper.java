package it.giacomos.android.osmer.forecastRepr;

import it.giacomos.android.osmer.widgets.LocationToImgPixelMapper;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Region;
import android.widget.ImageView;

public class ZoneMapper 
{
	private ImageView mImageView;
	private PointF tl1, br1, tl2, br2, tl3, br3, tl4, br4, endOfNorthernPlainLatitude;
	PointF dtl, dbr; /* define top left and bottom right of southern plain difference rect */
	PointF ctl1, cbr1, cbl2, cbr2; /* coast points defining rects */
	private Rect rMountains1, rMountains2, rMountains3, rMountAndNorthernPlain, rSouthernPlain;

	public ZoneMapper(ImageView v)
	{
		float eastMostLongitude = 13.783722f;
		mImageView = v;

		LocationToImgPixelMapper l2p = new LocationToImgPixelMapper();

		/* first three point couples for "Monti" */
		tl1 = l2p.mapToPoint(mImageView, 46.666654, 12.323914);
		br1 = l2p.mapToPoint(mImageView, 46.307525, eastMostLongitude);		

		tl2 = new PointF(tl1.x, br1.y);
		br2 = l2p.mapToPoint(mImageView, 46.136613,12.632904);

		/* same latitude as br1, longitude between faedis and cividale */
		tl3 = l2p.mapToPoint(mImageView, 46.307525, 13.395081);
		br3 = l2p.mapToPoint(mImageView, 46.136613, eastMostLongitude);


		rMountains1 = new Rect(Math.round(tl1.x), Math.round(tl1.y), Math.round(br1.x), Math.round(br1.y));
		rMountains2 = new Rect(Math.round(tl2.x), Math.round(tl2.y), Math.round(br2.x), Math.round(br2.y));
		rMountains3 = new Rect(Math.round(tl3.x), Math.round(tl3.y), Math.round(br3.x), Math.round(br3.y));

		/* Risano is taken as boundary between northern and southern plain (latitude) */
		endOfNorthernPlainLatitude = l2p.mapToPoint(mImageView, 45.972756, 13.248138);

		/* calculate the rectangle from top left north to endOfNorthernPlainLatitude y  */
		rMountAndNorthernPlain = new Rect(rMountains1.left, rMountains1.top, 
				rMountains1.right, Math.round(endOfNorthernPlainLatitude.y));

		/* southern plain */
		tl4 = new PointF(tl3.x, endOfNorthernPlainLatitude.y);
		/* latitude of aquileia as bottom, right of  br1 latitude for right */
		br4 = l2p.mapToPoint(mImageView, 45.768305, eastMostLongitude);
		rSouthernPlain = new Rect(Math.round(tl1.x), Math.round(tl4.y), Math.round(br4.x), Math.round(br4.y));

		/* southest part of non coast in the west of the region (near Annone Veneto and via Postumia */
		dtl = l2p.mapToPoint(mImageView, 45.793979,12.670069);
		dtl.set(tl1.x, dtl.y); /* x = x of tl1 */
		/* near S.Michele al Tagliamento */
		dbr = l2p.mapToPoint(mImageView, 45.750069, 12.940603);
		dbr.set(dbr.x, br4.y);
		/* latitude of br4, longitude just a little little bet eastern */
		ctl1 = l2p.mapToPoint(mImageView, 45.768305, 12.97142);
		/* latitude on the imaginary line connecting the coast of Lignano across Trieste 
		 * longitude on the coast with the slovenian park Debeli rtic 
		 */ 
		cbr1 = l2p.mapToPoint(mImageView, 45.63379, 13.702011);
		
		/* ctl2 easily calculated inside getAreaPath */
		cbr2 = l2p.mapToPoint(mImageView, 45.584795,13.989803);
	}

	public Region getAreaRegion(String zoneId)
	{
		Region reg = new Region();
		if(zoneId.compareTo("Z1") == 0 || zoneId.compareTo("Z2") == 0) /* Monti or northern plain */
		{
			reg.op(rMountains1, Region.Op.UNION);
			reg.op(rMountains2, Region.Op.UNION);
			reg.op(rMountains3, Region.Op.UNION);
			if(zoneId.compareTo("Z2") == 0)
			{
				reg.set(rMountAndNorthernPlain);
				reg.op(rMountains1, Region.Op.DIFFERENCE);
				reg.op(rMountains2, Region.Op.DIFFERENCE);
				reg.op(rMountains3, Region.Op.DIFFERENCE);
			}
		}
		else if(zoneId.compareTo("Z3") == 0)
		{
			reg.set(rSouthernPlain);
			reg.op(new Rect(Math.round(dtl.x),  Math.round(dtl.y), Math.round(dbr.x), Math.round(dbr.y)), Region.Op.DIFFERENCE);
		}
		else if(zoneId.compareTo("Z4") == 0) /* coast */
		{
			Rect cr1 = new Rect(Math.round(ctl1.x),  Math.round(ctl1.y), Math.round(cbr1.x), Math.round(cbr1.y));
			reg.set(cr1);
			PointF ctl2 = new PointF(cr1.right, cr1.top);
			reg.op(new Rect(Math.round(ctl2.x), Math.round(ctl2.y), Math.round(cbr2.x), Math.round(cbr2.y)), Region.Op.UNION);
		}
		return reg;
	}
	
	/** Returns a closed path enclosing the are representing the zone identified by zoneId
	 * 
	 * @param zoneId the string representing the id of the zone (Z1, Z2, Z3 or Z4)
	 * @return A closed path that encloses the region corresponding to the desired zoneId
	 * 
	 */
	public Path getAreaPath(String zoneId)
	{
		Path path = null;
		Region reg = getAreaRegion(zoneId);
		if(!reg.isEmpty())
		{
			path = reg.getBoundaryPath();
			path.close();
		}
		return path;
	}
}

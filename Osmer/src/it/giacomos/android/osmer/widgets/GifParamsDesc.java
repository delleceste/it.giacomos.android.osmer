package it.giacomos.android.osmer.widgets;

/** 
 * Descriptive parameters of the region inside the gif provided by the 
 * Osmer web site.
 * 
 * @note these parameters depend on the size of the image. Should they change
 *       in the future, it's important to update the values. 
 * 
 * @author giacomo
 *
 */
public class GifParamsDesc {
	
	/* fvg_background */
	// public static final float width = 240;
	
	// public static final float height = 225;
	
	/* fvg_background2 */
	public static final float width = 240;
	
	public static final float height = 231;
	
	/**
	 * first pixel of the FVG region starting from the top
	 * @note changing the gif/png image implies adjusting this value!
	 */
	public static final float regionTop = 11; // fvg_background: 11;
	
	/** 
	 * first pixel belonging to the fvg region starting from the left
	 * @note changing the gif/png image implies adjusting this value!
	 */
	public static final float regionLeft= 5; // fvg_background:  = 5;
	
	/**
	 * last pixel of the region starting from the top
	 * @note changing the gif/png image implies adjusting this value!
	 */
	public static final float regionBottom= 231; // fvg_background:  = 214;
	
	// public static final float regionBottom= 214; // fvg_background:  = 214;
	
	/** last pixel of the region inside the image starting from the left.
	 * @note changing the gif/png image implies adjusting this value!
	 * 
	 */
	public static final float regionRight = 228; // fvg_background:  = 228;
	
	/**
	 * at this percentage of the image starting from the top we find the
	 * topmost point of the region.
	 * The value is calculated knowing that the topmost point is at pixel 11
	 * from the top: 225 : 11 = 100 : x => x = 100 * 11 / 225 = 4.889
	 */
	public static float regionTopPercent = regionTop / height;
	
	/**
	 * at this percentage of the image starting from the left we find the
	 * leftmost point of the region.
	 * The value is calculated knowing that the topmost point is at pixel 5
	 * from the left.
	 */
	public static float regionLeftPercent = regionLeft / width;
	
	/**
	 * at this percentage of the image starting from the top we find the
	 * bottom most point of the region.
	 * The value is calculated knowing that the topmost point is at pixel 214
	 * from the top.
	 */
	public static float regionBottomPercent = regionBottom / height;

	/**
	 * at this percentage of the image starting from the left we find the
	 * rightmost point of the region.
	 * The value is calculated knowing that the topmost point is at pixel 228
	 * from the left.
	 */
	public static float regionRightPercent = regionRight / width;
}

package it.giacomos.android.osmer.rainAlert.gridAlgo;

import android.util.Log;
import it.giacomos.android.osmer.rainAlert.RainDetectResult;
import it.giacomos.android.osmer.rainAlert.interfaces.ImgCompareI;
import it.giacomos.android.osmer.rainAlert.interfaces.ImgOverlayInterface;
import it.giacomos.android.osmer.rainAlert.interfaces.ImgParamsInterface;


public class ImgCompareGrids implements ImgCompareI {

	@Override
	public RainDetectResult compare(ImgOverlayInterface lastGridI,
			ImgOverlayInterface prevGridI, 
			ImgParamsInterface img_params_i) 
	{
		RainDetectResult res = new RainDetectResult();
		
		ImgOverlayGrid glast = (ImgOverlayGrid) lastGridI;
		ImgOverlayGrid gprev = (ImgOverlayGrid) prevGridI;
		
		Grid lastGrid = glast.getGrid();
		Grid prevGrid = gprev.getGrid();
		
		int lr = glast.getGrid().nrows; /* number of rows of last grid */
		int lc = glast.getGrid().ncols;
		
		int pr = gprev.getGrid().nrows; /* previous grid */
		int pc = gprev.getGrid().ncols; 
		
		int ret = 0;
		
		float last_intensity = 0.0f;
		
		if(lr == pr && lc == pc) /* grids must be same size */
		{	
			double lastDbzInCenter = lastGrid.get((int)Math.floor(lr/2), (int)Math.floor(lc/2)).intensity;
			boolean rainAlready = (lastDbzInCenter > img_params_i.getThreshold());
			/* start from the border squares of the last grid: row 0, col 0, rowN, colN */
			/* first row */
			int r = 0;
			int  c;
			
			Element pe; /* previous grid element */
			Element lastLinkedEl; /* an element of the previous grid, linked to pe */
			double linkedElWeight; /* the weight factor of the element linked to pe in the grid */
			float lastElintensity, prevElintensity; /* the intensity value of the last && previous element in the grid */
			double requiredIntensity; /* the value of intensity above which we can consider the rain is approaching */
			boolean bigIncrease; /* if true, a big increase of the intensity towards the center has been detected */
			boolean increase; /* if true, an increase of the intensity value has been detected while moving towards the center */
		
			/* initialize matrix result */
			// res.deltas_matrix = new float[pr][pc];
			// res.last_dbz_matrix = new float[pr][pc];
			
			for(c = 0; c < lc; c++)
			{
				pe = prevGrid.get(r, c);
				for(ContiguousElementData contiguousElement : pe.getLinks())
				{
					lastLinkedEl = lastGrid.idxGet(contiguousElement.index);
					linkedElWeight = contiguousElement.weight;
					lastElintensity = lastLinkedEl.intensity;
					prevElintensity = pe.intensity;
					
					requiredIntensity = prevElintensity * linkedElWeight;
					
					bigIncrease = (rainAlready && (lastElintensity >= requiredIntensity + img_params_i.getBigIncreaseValue()));
					
					/* if it already rains set increase to true only if a big increase is foreseen.
					 * Otherwise, if it already rains, no need to warn.
					 * lastElintensity >= requiredDbz means that there has been an increase in the dbZ value between the
					 * previous value (requiredDbz) && the current one (lastElintensity).
					 */
					increase = ( (!rainAlready && (lastElintensity > img_params_i.getThreshold() && lastElintensity >= requiredIntensity) )
							|| bigIncrease);
					
					if(increase)
						lastLinkedEl.setHasIncreased(increase);
					
					/* save max intensity value only if the element is relevant */
					if(increase && lastElintensity > last_intensity)
						last_intensity = lastElintensity;
					
//					Log.e("ImgCompareGrids", "[" + r + "," + c + "]: ("  + prevElintensity + ") -> [" +
//							contiguousElement.index.i  + "," +contiguousElement.index.j + "] (" + lastElintensity + ")"
//					 + ", increase " + increase + ", big incr" + bigIncrease + " RAIN " + ret);
					 
					if(increase)
						ret++;
				}
			}
			
			r = lr - 1; /* last row */
			for(c = 0; c < lc; c++)
			{
				pe = prevGrid.get(r, c);
				for(ContiguousElementData contiguousElement : pe.getLinks())
				{
					lastLinkedEl = lastGrid.idxGet(contiguousElement.index);
					linkedElWeight = contiguousElement.weight;
					lastElintensity = lastLinkedEl.intensity;
					prevElintensity = pe.intensity;
					
					requiredIntensity = prevElintensity * linkedElWeight;
					
					bigIncrease = (rainAlready && (lastElintensity >= requiredIntensity + img_params_i.getBigIncreaseValue()));
					
					/* if it already rains set increase to true only if a big increase is foreseen.
					 * Otherwise, if it already rains, no need to warn
					 */
					increase = ( (!rainAlready && (lastElintensity > img_params_i.getThreshold() && lastElintensity >= requiredIntensity) )
							|| bigIncrease);
					
					if(increase)
						lastLinkedEl.setHasIncreased(increase);
						
					if(increase && lastElintensity > last_intensity)
						last_intensity = lastElintensity;
					
//					Log.e("ImgCompareGrids", "[" + r + "," + c + "]: ("  + prevElintensity + ")  -> [" +
//							contiguousElement.index.i  + "," +contiguousElement.index.j + "] (" + lastElintensity + ")"
//					 + ", increase " + increase + ", big incr" + bigIncrease + " RAIN " + ret);

					if(increase)
						ret++;
					
				}
			}
			
			c = 0; /* first column */
			for(r = 0; r < lr; r++)
			{
				pe = prevGrid.get(r, c);
				for(ContiguousElementData contiguousElement : pe.getLinks())
				{
					lastLinkedEl = lastGrid.idxGet(contiguousElement.index);
					linkedElWeight = contiguousElement.weight;
					lastElintensity = lastLinkedEl.intensity;
					prevElintensity = pe.intensity;
					
					requiredIntensity = prevElintensity * linkedElWeight;
					
					bigIncrease = (rainAlready && (lastElintensity >= requiredIntensity + img_params_i.getBigIncreaseValue()));
					
					/* if it already rains set increase to true only if a big increase is foreseen.
					 * Otherwise, if it already rains, no need to warn
					 */
					increase = ( (!rainAlready && (lastElintensity > img_params_i.getThreshold() && lastElintensity >= requiredIntensity) )
							|| bigIncrease);
					
					if(increase)
						lastLinkedEl.setHasIncreased(increase);
						
					if(increase && lastElintensity > last_intensity)
						last_intensity = lastElintensity;
						
//					Log.e("ImgCompareGrids", "[" + r + "," + c + "]: (" + prevElintensity + ") -> [" +
//							contiguousElement.index.i  + "," +contiguousElement.index.j + "] (" + lastElintensity + ")"
//					 + ", increase " + increase + ", big incr" + bigIncrease + " RAIN " + ret);

					if(increase)
						ret++;
					
				}
			}
			c = lc - 1; /* first column */
			for(r = 0; r < lr; r++)
			{
				pe = prevGrid.get(r, c);
				for(ContiguousElementData contiguousElement : pe.getLinks())
				{
					lastLinkedEl = lastGrid.idxGet(contiguousElement.index);
					linkedElWeight = contiguousElement.weight;
					lastElintensity = lastLinkedEl.intensity;
					prevElintensity = pe.intensity;
					
					requiredIntensity = prevElintensity * linkedElWeight;
					
					bigIncrease = (rainAlready && (lastElintensity >= requiredIntensity + img_params_i.getBigIncreaseValue()));
					
					/* if it already rains set increase to true only if a big increase is foreseen.
					 * Otherwise, if it already rains, no need to warn
					 */
					increase = ( (!rainAlready && (lastElintensity > img_params_i.getThreshold() && lastElintensity >= requiredIntensity) )
							|| bigIncrease);
					
					if(increase)
						lastLinkedEl.setHasIncreased(increase);
						
					if(increase && lastElintensity > last_intensity)
						last_intensity = lastElintensity;
					
//					Log.e("ImgCompareGrids", "[" + r + "," + c + "]: ("  + prevElintensity + ") ->  [" +
//							contiguousElement.index.i  + "," +contiguousElement.index.j + "] (" + lastElintensity + ")"
//					 + ", increase " + increase + ", big incr" + bigIncrease + " RAIN " + ret);
					
					if(increase)
						ret++;
				}
			}			
		}
		else
			Log.e("ImgCompareGrids", "grid dimensions differ");
			
		res.willRain = (ret > 0);
		res.dbz = last_intensity;
		/* detected an increase towards the center (and not already raining)? */
		return res;
	
	}

}

package it.giacomos.android.osmer.rainAlert.gridAlgo;

import java.util.ArrayList;

import it.giacomos.android.osmer.rainAlert.interfaces.ImgParamsInterface;
import android.graphics.Bitmap;
import android.util.Log;

public class Element {

	public Index index;
	public double xend = 0.0, xstart = 0.0, ystart = 0.0, yend = 0.0;
	public double dbz;
	private boolean increased;
	
	public ArrayList<ContiguousElementData> contiguousElementDataList; /* contiguousElementData */
	
	public Element(int nrows, int ncols) {
		index = new Index(nrows, ncols);
		contiguousElementDataList = new ArrayList<ContiguousElementData>();
	}

	public void calculateDbz(Bitmap image, double xc, double yc,
			ImgParamsInterface imgParams) 
	{
		
		
	}

	public void setHasIncreased(boolean has)
	{
		increased = has;
	}
	
	public boolean hasIncreased()
	{
		return increased;
	}
	
	public void initFromString(String section) 
	{
		int commaCnt = section.length() - section.replace(",", "").length();
		if(commaCnt > 1) /* at least one contiguous Element to look for */
		{
			String [] contigs = section.split(";");
			for(String contig : contigs)
			{
				String [] parts = contig.split(",");
				if(parts.length > 2)
				{
					this.addContiguousData(new ContiguousElementData(Integer.parseInt(parts[0]),
							Integer.parseInt(parts[1]), Double.parseDouble(parts[2])));
				}
			}
		}
	}

	private void addContiguousData(ContiguousElementData contiguousElementData) {
		if(contiguousElementData.isValid())
			this.contiguousElementDataList.add(contiguousElementData);
		else
			Log.e("Element.addContiguousData","Element.addContiguousData: element not valid");
	}

	public ArrayList<ContiguousElementData> getLinks() 
	{
		return this.contiguousElementDataList;
	}
	
	

}

package it.giacomos.android.osmer.rainAlert.gridAlgo;

public class ContiguousElementData {

	public Index index;
	public double weight;
	
	public ContiguousElementData(int i, int j, double wei)
	{
		index = new Index(i, j);
		weight = wei;
	}
	
	public boolean isValid() {
		return this.index.i >= 0 &&  this.index.j >= 0 && this.weight >= 0;
	}

}

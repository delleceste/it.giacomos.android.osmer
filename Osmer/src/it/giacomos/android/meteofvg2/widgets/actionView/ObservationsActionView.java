package it.giacomos.android.meteofvg2.widgets.actionView;

import it.giacomos.android.meteofvg2.R;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

public class ObservationsActionView extends ActionProvider
{

	public ObservationsActionView(Context context) 
	{
		super(context);
		mContext = context;
		// TODO Auto-generated constructor stub
	}

	@Override
	public View onCreateActionView() 
	{
		Log.i("onCreateActionView", "inflating");
		LayoutInflater li = LayoutInflater.from(mContext);
		View v = li.inflate(R.layout.dailyobs_spinner, null);
		
		String [] observationTypes = mContext.getResources().getStringArray(R.array.observation_types);

		Spinner spinner = (Spinner) v;
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext,
		        R.array.observation_types, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		
		
		return v;
	}

	private Context mContext;
}

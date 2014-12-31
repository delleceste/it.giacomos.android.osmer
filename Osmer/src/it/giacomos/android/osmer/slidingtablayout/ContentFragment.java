package it.giacomos.android.osmer.slidingtablayout;

/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.network.state.ViewType;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Simple Fragment used to display some meaningful content for each page in the sample's
 * {@link android.support.v4.view.ViewPager}.
 */
public class ContentFragment extends Fragment {

    private static final String KEY_TITLE = "title";
    private static final String KEY_INDICATOR_COLOR = "indicator_color";
    private static final String KEY_DIVIDER_COLOR = "divider_color";
    private static final String VIEW_TYPE = "view_type_as_int";

    /**
     * @param mViewType 
     * @return a new instance of {@link ContentFragment}, adding the parameters into a bundle and
     * setting them as arguments.
     */
    public static ContentFragment newInstance(CharSequence title, ViewType viewType, int indicatorColor,
            int dividerColor) {
        Bundle bundle = new Bundle();
        int viewT = viewType.ordinal();
        bundle.putCharSequence(KEY_TITLE, title);
        bundle.putInt(KEY_INDICATOR_COLOR, indicatorColor);
        bundle.putInt(KEY_DIVIDER_COLOR, dividerColor);
        bundle.putInt(VIEW_TYPE, viewT);

        ContentFragment fragment = new ContentFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) 
    {
    	Log.e("ContentFragment.onCreateView", " creating view ");
    	Bundle args = getArguments();
    	View view = null;
    	int type = args.getInt(VIEW_TYPE);
    	

    	Log.e("ContentFragment.onCreateView", " creating view " + type);
    	
    	if(type == ViewType.HOME.ordinal())
    		view = inflater.inflate(R.layout.home, container, false);
    	else if(type == ViewType.TODAY.ordinal())
    		view =  inflater.inflate(R.layout.today, container, false);
    	else if(type == ViewType.TOMORROW.ordinal())
    		view =  inflater.inflate(R.layout.tomorrow, container, false);
    	else if(type == ViewType.TWODAYS.ordinal())
    		view =  inflater.inflate(R.layout.twodays, container, false);
    	else if(type == ViewType.THREEDAYS.ordinal())
    		view =  inflater.inflate(R.layout.threedays, container, false);
    	else
    		view =  inflater.inflate(R.layout.fourdays, container, false);
    	
    	Log.e("ContentFragment.onCreateView", " created " + view + " type " + type);
    	
    	return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();

        if (args != null) 
        {
//            int indicatorColor = args.getInt(KEY_INDICATOR_COLOR);
//            TextView indicatorColorView = (TextView) view.findViewById(R.id.item_indicator_color);
//            indicatorColorView.setText("Indicator: #" + Integer.toHexString(indicatorColor));
//            indicatorColorView.setTextColor(indicatorColor);
//
//            int dividerColor = args.getInt(KEY_DIVIDER_COLOR);
//            TextView dividerColorView = (TextView) view.findViewById(R.id.item_divider_color);
//            dividerColorView.setText("Divider: #" + Integer.toHexString(dividerColor));
//            dividerColorView.setTextColor(dividerColor);
        }
    }
}

package it.giacomos.android.osmer.pro.widgets.map.report.tutorialActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class ScenarioContent {

	/**
	 * An array of sample (dummy) items.
	 */
	public List<ScenarioItem> items = new ArrayList<ScenarioItem>();

	/**
	 * A map of sample (dummy) items, by ID.
	 */
	public Map<String, ScenarioItem> itemMap = new HashMap<String, ScenarioItem>();


	public void addItem(ScenarioItem item) {
		items.add(item);
		itemMap.put(item.id, item);
	}

}

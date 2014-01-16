package it.giacomos.android.osmer.pro.widgets.map.report.tutorialActivity;

public class ScenarioItem {
	/**
	 * An item representing a piece of content.
	 */
		public String id;
		public String content;

		public ScenarioItem(String id, String content) {
			this.id = id;
			this.content = content;
		}

		@Override
		public String toString() 
		{
			return content;
		}
}

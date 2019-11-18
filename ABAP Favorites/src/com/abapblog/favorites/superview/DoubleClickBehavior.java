package com.abapblog.favorites.superview;

/**
 * Behavior for Tree Node Double click and Double click + Ctrl
 *
 * @author stockbal
 *
 */
public enum DoubleClickBehavior {
	OPEN_IN_CURRENT_PROJECT("Use the current Project"),
	OPEN_VIA_PROJECT_DIALOG("Use Project from Project Selection Dialog");

	private String description;

	private DoubleClickBehavior(final String description) {
		this.description = description;
	}

	/**
	 * Creates key/value pair array from enum name and description
	 *
	 * @return key/value pair array from enum name and description
	 */
	public static String[][] toNamesAndKeys() {
		final DoubleClickBehavior[] types = DoubleClickBehavior.values();
		final String[][] keyValue = new String[types.length][2];
		for (int i = 0; i < types.length; i++) {
			keyValue[i][0] = types[i].description;
			keyValue[i][1] = types[i].name();
		}
		return keyValue;
	}
}

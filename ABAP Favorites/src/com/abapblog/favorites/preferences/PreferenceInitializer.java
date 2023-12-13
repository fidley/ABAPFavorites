package com.abapblog.favorites.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.abapblog.favorites.Activator;
import com.abapblog.favorites.superview.DoubleClickBehavior;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.P_HIDE_PROJECT_DEP_FOLDERS, true);
		store.setDefault(PreferenceConstants.P_DOUBLE_CLICK_BEHAVIOR,
				DoubleClickBehavior.OPEN_IN_CURRENT_PROJECT.name());
		store.setDefault(PreferenceConstants.P_DOUBLE_CLICK_CTRL_BEHAVIOR,
				DoubleClickBehavior.OPEN_VIA_PROJECT_DIALOG.name());
		store.setDefault(PreferenceConstants.P_KEEP_THE_EXPANDED_FOLDERS_AT_START, false);
		store.setDefault(PreferenceConstants.P_NAVIGATE_TO_ECLIPSE_FOR_SUPPORTED_DEV_OBJECTS, true);
		store.setDefault(PreferenceConstants.P_SHOW_LINKED_TO_COLUMN, false);
		store.setDefault(PreferenceConstants.P_SHOW_LONG_TEXT_COLUMN, false);
	}

}

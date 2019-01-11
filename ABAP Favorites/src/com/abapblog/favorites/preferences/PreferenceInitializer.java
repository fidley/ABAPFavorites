package com.abapblog.favorites.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.abapblog.favorites.Activator;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.P_HIDE_PROJECT_DEP_FOLDERS, true);
		store.setDefault(PreferenceConstants.P_KEEP_THE_EXPANDED_FOLDERS_AT_START, false);
	}



}

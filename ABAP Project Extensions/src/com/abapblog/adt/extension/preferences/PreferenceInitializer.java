package com.abapblog.adt.extension.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import org.eclipse.jface.preference.IPreferenceStore;

import com.abapblog.adt.extension.Activator;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.doAutomaticLogonAtStart, true);
		store.setDefault(PreferenceConstants.AutomaticLogonOnlyForStoredPasswords, true);
		store.setDefault(PreferenceConstants.doAutomaticLogonAtExpandOfProject, true);
		store.setDefault(PreferenceConstants.askForPasswordAtProjectCreation, true);

	}

}

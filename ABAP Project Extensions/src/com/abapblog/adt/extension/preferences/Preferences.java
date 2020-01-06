package com.abapblog.adt.extension.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.abapblog.adt.extension.Activator;

public class Preferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	private final IPreferenceStore store;
	private Boolean doAutomaticLogonAtStart = false;
	private Boolean AutomaticLogonOnlyForStoredPasswords = false;

	public Preferences() {
		super(GRID);
		this.store = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(this.store);
		setDescription("Settings for ABAP Favorites plugin");
	}

	@Override
	public void createFieldEditors() {
		addField(new BooleanFieldEditor(PreferenceConstants.doAutomaticLogonAtStart,
				"&Logon Automatically at Eclipse Start?", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.AutomaticLogonOnlyForStoredPasswords,
				"&Logon Automatically only to Systems with stored password?", getFieldEditorParent()));

	}

	@Override
	public void init(final IWorkbench workbench) {
		this.doAutomaticLogonAtStart = this.store.getBoolean(PreferenceConstants.doAutomaticLogonAtStart);
		this.AutomaticLogonOnlyForStoredPasswords = this.store
				.getBoolean(PreferenceConstants.AutomaticLogonOnlyForStoredPasswords);
	}

	@Override
	protected void performApply() {
		super.performApply();
	}

//Apply&Close
	@Override
	public boolean performOk() {

		final Boolean ApplyClose = super.performOk();
		return ApplyClose;
	}

}
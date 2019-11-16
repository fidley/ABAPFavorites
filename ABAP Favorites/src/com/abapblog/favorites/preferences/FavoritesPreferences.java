package com.abapblog.favorites.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.abapblog.favorites.Activator;
import com.abapblog.favorites.superview.DoubleClickBehavior;
import com.abapblog.favorites.superview.Superview;

public class FavoritesPreferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	private final IPreferenceStore store;
	private Boolean hideProjectDepFolders;

	public FavoritesPreferences() {
		super(GRID);
		this.store = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(this.store);
		setDescription("Settings for ABAP Favorites plugin");
	}

	@Override
	public void createFieldEditors() {
		addField(new BooleanFieldEditor(PreferenceConstants.P_HIDE_PROJECT_DEP_FOLDERS,
				"&Hide project dependent folders", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_KEEP_THE_EXPANDED_FOLDERS_AT_START,
				"&Save state of folder expansion", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_NAVIGATE_TO_ECLIPSE_FOR_SUPPORTED_DEV_OBJECTS,
				"&Navigate to eclipse for supported development object", getFieldEditorParent()));

		addField(new RadioGroupFieldEditor(PreferenceConstants.P_DOUBLE_CLICK_BEHAVIOR,
				"Project to be used during double click", 1, DoubleClickBehavior.toNamesAndKeys(),
				getFieldEditorParent(), true));
		addField(new RadioGroupFieldEditor(PreferenceConstants.P_DOUBLE_CLICK_CTRL_BEHAVIOR,
				"Project to be used during double click with pressed Ctrl key", 1, DoubleClickBehavior.toNamesAndKeys(),
				getFieldEditorParent(), true));

	}

	@Override
	public void init(final IWorkbench workbench) {
		this.hideProjectDepFolders = this.store.getBoolean(PreferenceConstants.P_HIDE_PROJECT_DEP_FOLDERS);
	}

	@Override
	protected void performApply() {
		super.performApply();
		checkFavoritesNeedRefresh();
	}

//Apply&Close
	@Override
	public boolean performOk() {

		final Boolean ApplyClose = super.performOk();
		checkFavoritesNeedRefresh();
		return ApplyClose;
	}

	private void checkFavoritesNeedRefresh() {
		final Boolean _hideProjectDepFolders = this.store.getBoolean(PreferenceConstants.P_HIDE_PROJECT_DEP_FOLDERS);
		if (_hideProjectDepFolders != this.hideProjectDepFolders) {
			this.hideProjectDepFolders = _hideProjectDepFolders;
			Superview.refreshActiveViews();

		}
	}

}
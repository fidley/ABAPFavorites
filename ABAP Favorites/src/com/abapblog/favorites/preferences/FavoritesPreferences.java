package com.abapblog.favorites.preferences;

import org.eclipse.jface.preference.*;

import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import com.abapblog.favorites.Activator;
import com.abapblog.favorites.superview.Superview;

public class FavoritesPreferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	private IPreferenceStore store;
	private Boolean hideProjectDepFolders;
	public FavoritesPreferences() {
		super(GRID);
		store = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(store);
		setDescription("Settings for ABAP Favorites plugin");
	}

	public void createFieldEditors() {
		addField(new BooleanFieldEditor(PreferenceConstants.P_HIDE_PROJECT_DEP_FOLDERS,
				"&Hide project dependent folders", getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {
		hideProjectDepFolders = store.getBoolean(PreferenceConstants.P_HIDE_PROJECT_DEP_FOLDERS);
	}

	@Override
	protected void performApply() {
		super.performApply();
		checkFavoritesNeedRefresh();
	}

//Apply&Close
	@Override
	public boolean performOk() {

		Boolean ApplyClose = super.performOk();
		checkFavoritesNeedRefresh();
		return ApplyClose;
	}

	private void checkFavoritesNeedRefresh() {
		Boolean _hideProjectDepFolders = store.getBoolean(PreferenceConstants.P_HIDE_PROJECT_DEP_FOLDERS);
		if (_hideProjectDepFolders != hideProjectDepFolders)
		{
			hideProjectDepFolders = _hideProjectDepFolders;
			Superview.refreshActiveViews();

		}
	}

}
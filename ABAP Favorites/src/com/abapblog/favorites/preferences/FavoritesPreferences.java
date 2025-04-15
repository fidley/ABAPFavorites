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
	private Boolean showLinkedToColumn;
	private Boolean showLongTextColumn;

	public FavoritesPreferences() {
		super(GRID);
		this.store = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(this.store);
		setDescription("Settings for ABAP Favorites plugin");
	}

	@Override
	public void createFieldEditors() {
		addField(new BooleanFieldEditor(PreferenceConstants.P_HIDE_PROJECT_DEP_FOLDERS,
				"&Hide project or working set dependent folders", getFieldEditorParent()));
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

		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_LINKED_TO_COLUMN,
				"&Show Linked To column in the views", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_SHOW_LONG_TEXT_COLUMN,
				"&Show Long Description column in the views", getFieldEditorParent()));

	}

	@Override
	public void init(final IWorkbench workbench) {
		hideProjectDepFolders = store.getBoolean(PreferenceConstants.P_HIDE_PROJECT_DEP_FOLDERS);
		showLinkedToColumn = store.getBoolean(PreferenceConstants.P_SHOW_LINKED_TO_COLUMN);
		showLongTextColumn = store.getBoolean(PreferenceConstants.P_SHOW_LONG_TEXT_COLUMN);

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

		if (hideProjectDepFolders != store.getBoolean(PreferenceConstants.P_HIDE_PROJECT_DEP_FOLDERS)
				|| showLinkedToColumn != store.getBoolean(PreferenceConstants.P_SHOW_LINKED_TO_COLUMN)
				|| showLongTextColumn != store.getBoolean(PreferenceConstants.P_SHOW_LONG_TEXT_COLUMN)) {
			hideProjectDepFolders = store.getBoolean(PreferenceConstants.P_HIDE_PROJECT_DEP_FOLDERS);
			showLinkedToColumn = store.getBoolean(PreferenceConstants.P_SHOW_LINKED_TO_COLUMN);
			showLongTextColumn = store.getBoolean(PreferenceConstants.P_SHOW_LONG_TEXT_COLUMN);
			Superview.refreshActiveViews();

		}
	}

}
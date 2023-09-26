package com.abapblog.favorites.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class OpenPreferences implements IHandler {

	private static final String ORG_ECLIPSE_UI_PREFERENCE_PAGES_KEYS = "org.eclipse.ui.preferencePages.Keys";

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		PreferencesUtil.createPreferenceDialogOn(null, "com.abapblog.favorites.preferences.FavoritesPreferences",
				new String[] { "com.abapblog.favorites.preferences.FavoritesPreferences",
						ORG_ECLIPSE_UI_PREFERENCE_PAGES_KEYS },
				null).open();
		return null;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isHandled() {
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

}

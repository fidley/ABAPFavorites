package com.abapblog.favorites.commands.sapgui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.PlatformUI;

import com.sap.adt.tools.core.ui.dialogs.AbapProjectSelectionDialog;

public class ImportFavoritesFromSAPGui extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IProject project = getProjectFromProjectChooserDialog();
		if (project == null)
			return null;
		FavoritesFromGUIRaw favoritesRaw = new GuiFavoritesImporter().getFavoritesFromBackend(project);
		if (favoritesRaw != null)
			favoritesRaw.addFavoritesToNewFolder("GUI Favorites");
		return null;
	}

	private IProject getProjectFromProjectChooserDialog() {
		return AbapProjectSelectionDialog.open(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), null);
	}

}

package com.abapblog.favorites.commands;

import org.eclipse.core.commands.AbstractHandler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.window.*;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorInput;
import com.abapblog.favorites.common.CommonTypes;
import com.abapblog.favorites.dialog.NameDialog;
import com.abapblog.favorites.superview.Superview;
import com.abapblog.favorites.ui.AbapEditorPathParser;
import com.abapblog.favorites.ui.SelectFolderDialog;
import com.abapblog.favorites.xml.XMLhandler;

public class AddToFavoritesHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent executionEvent) throws ExecutionException {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {

			IWorkbenchPage page = window.getActivePage();
			IEditorPart editor = page.getActiveEditor();
			IEditorInput input = editor.getEditorInput();

			CommonTypes.TypeOfEntry objectType = AbapEditorPathParser.getType(input.toString());
			String objectName = AbapEditorPathParser.getObjectName(input.toString());
			//In case of objects in own namespace we have to convert %2f to / character to have correct name
			objectName = objectName.replaceAll("%2f", "/");

			SelectFolderDialog selectFolderDialog = new SelectFolderDialog(null, objectType, objectName);
			if (selectFolderDialog.open() == Window.OK) {
				NameDialog newObjectDialog = new NameDialog(null, selectFolderDialog.getTypeOfEntry(),
						selectFolderDialog.getObjectName().toUpperCase());
				if (newObjectDialog.open() == Window.OK) {
					XMLhandler.addObjectToXML(selectFolderDialog.getTypeOfEntry(), newObjectDialog.getName(),
							newObjectDialog.getDescription(), newObjectDialog.getLongDescription(),"",
							selectFolderDialog.getFolderID(), selectFolderDialog.getFolderType());
					Superview.refreshActiveViews();
				}

			}

		}

		return null;

	}
}

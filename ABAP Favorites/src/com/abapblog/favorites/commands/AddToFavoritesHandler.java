package com.abapblog.favorites.commands;

import org.eclipse.core.commands.AbstractHandler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorInput;
import com.abapblog.favorites.common.CommonTypes;
import com.abapblog.favorites.ui.AbapEditorPathParser;
import com.abapblog.favorites.ui.SelectFolderDialog;


public class AddToFavoritesHandler extends AbstractHandler {


	@Override
	public Object execute(ExecutionEvent executionEvent) throws ExecutionException {
		Object context = executionEvent.getApplicationContext();
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {

			IWorkbenchPage page = window.getActivePage();
			IEditorPart editor = page.getActiveEditor();
			IEditorInput input = editor.getEditorInput();

			CommonTypes.TypeOfEntry objectType = AbapEditorPathParser.getType(input.toString());
			String objectName = AbapEditorPathParser.getObjectName(input.toString());
			//System.out.println(objectType.toString());
			//System.out.println(objectName);

			SelectFolderDialog selectFolderDialog = new SelectFolderDialog(null,objectType,objectName);
			selectFolderDialog.open();

			}

		return null;
	}


}

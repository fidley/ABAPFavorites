package com.abapblog.favorites.commands;

import org.eclipse.core.commands.AbstractHandler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.window.*;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.abapblog.favorites.common.Common;
import com.abapblog.favorites.common.CommonTypes.TypeOfEntry;
import com.abapblog.favorites.dialog.NameDialog;
import com.abapblog.favorites.superview.Superview;
import com.abapblog.favorites.ui.SelectFolderDialog;
import com.abapblog.favorites.xml.XMLhandler;
import com.sap.adt.tools.core.AdtObjectReference;

@SuppressWarnings("restriction")
public class AddToFavoritesProjectExplorerHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent executionEvent) throws ExecutionException {
		SelectFolderDialog selectFolderDialog = null;
		Boolean FolderSelected = false;
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
			Object[] Items = ((IStructuredSelection) selection).toArray();
			for (int i = 0; i < Items.length; i++) {
				IAdaptable item = (IAdaptable) Items[i];
				if (item instanceof IAdaptable) {
					try {
						AdtObjectReference AdtRef = ((IAdaptable) item).getAdapter(AdtObjectReference.class);
						String objectType = AdtRef.getType();
						String objectName = AdtRef.getName();
						TypeOfEntry typeOfEntry = Common.getTypeOfEntryFromSAPType(objectType);

						if (Items.length == 1 || FolderSelected == false) {
							selectFolderDialog = new SelectFolderDialog(null, typeOfEntry, objectName);
							if (selectFolderDialog.open() == Window.OK) {
								FolderSelected = true;
								if (Items.length == 1) {
									NameDialog newObjectDialog = new NameDialog(null,
											selectFolderDialog.getTypeOfEntry(),
											selectFolderDialog.getObjectName().toUpperCase());
									if (newObjectDialog.open() == Window.OK) {
										XMLhandler.addObjectToXML(selectFolderDialog.getTypeOfEntry(),
												newObjectDialog.getName(), newObjectDialog.getDescription(),
												newObjectDialog.getLongDescription(),"", selectFolderDialog.getFolderID(),
												selectFolderDialog.getFolderType());
									}
								} else {
									XMLhandler.addObjectToXML(typeOfEntry, objectName.toUpperCase(), "", "",
											selectFolderDialog.getFolderID(),"",selectFolderDialog.getFolderType());
								}
							}

						} else {
							XMLhandler.addObjectToXML(typeOfEntry, objectName.toUpperCase(), "", "", "",
									selectFolderDialog.getFolderID(), selectFolderDialog.getFolderType());
						}

					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

		}
		Superview.refreshActiveViews();
		return null;

	}

	@Override
	public boolean isEnabled() {

		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
			Object firstElement = selection.getFirstElement();
			if (firstElement instanceof IAdaptable) {
				try {
					AdtObjectReference AdtRef = ((IAdaptable) firstElement).getAdapter(AdtObjectReference.class);
					String objectType = AdtRef.getType();
					TypeOfEntry typeOfEntry = Common.getTypeOfEntryFromSAPType(objectType);
					if (typeOfEntry != null) {
						return true;
					}
				} catch (Exception e) {
					return false;
				}
			}
		}

		return false;
	}

}

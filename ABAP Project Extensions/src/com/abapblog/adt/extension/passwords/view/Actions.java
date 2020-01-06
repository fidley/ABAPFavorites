package com.abapblog.adt.extension.passwords.view;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;

import com.abablog.adt.extension.passwords.SecureStorage;
import com.abapblog.adt.extension.dialogs.ChangePasswordDialog;
import com.abapblog.adt.extension.passwords.tree.TreeObject;



public class Actions {
	public Action doubleClick;
	
	public void createDoubleClick(final TreeViewer viewer) {
		this.doubleClick = new Action() {
			@Override
			public void run() {
				if (viewer.getSelection() instanceof IStructuredSelection) {
					final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

					final TreeObject object = (TreeObject) selection.getFirstElement();
					if (object instanceof TreeObject) {
						ChangePasswordDialog passwordDialog = new ChangePasswordDialog(null);
						passwordDialog.create();
						if (passwordDialog.open() == Window.OK) {
							SecureStorage secureStorage = new SecureStorage();
							secureStorage.changePasswordForUser(object.getProject(), object.getClient(), object.getUser(), passwordDialog.getPassword(), passwordDialog.getEncryptValue());
						}
					}

				}
			}
		};
	}

}

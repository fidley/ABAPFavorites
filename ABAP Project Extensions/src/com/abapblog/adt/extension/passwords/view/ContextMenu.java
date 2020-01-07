package com.abapblog.adt.extension.passwords.view;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.DrillDownAdapter;

import com.abapblog.adt.extension.passwords.tree.TreeObject;
import com.abapblog.adt.extension.passwords.tree.TreeParent;

public class ContextMenu {
	private TreeViewer viewer;
	protected DrillDownAdapter drillDownAdapter;
	private Actions actions;

	public ContextMenu(TreeViewer viewer) {
		this.viewer = viewer;
		this.drillDownAdapter = new DrillDownAdapter(this.viewer);
		actions = new Actions();
		actions.createAddNewClient(viewer);
		actions.createAddNewUser(viewer);
		actions.createLogonWithSelectedUser(viewer);
		actions.createDeleteClient(viewer);
		actions.createDeleteUser(viewer);
	}

	protected void fillContextMenu(final IMenuManager manager) {
		if (this.viewer.getSelection() instanceof IStructuredSelection) {
			final IStructuredSelection selection = (IStructuredSelection) this.viewer.getSelection();

			try {

				final TreeObject object = (TreeObject) selection.getFirstElement();

				if (object instanceof TreeParent) {
					final TreeParent parent = (TreeParent) object;
					if (parent.getType().equals(TreeParent.TypeOfFolder.Client)) {
						manager.add(actions.addNewUser);
						manager.add(actions.deleteClient);
					} else if (parent.getType().equals(TreeParent.TypeOfFolder.Project)) {
						manager.add(actions.addNewClient);
					}
					manager.add(new Separator());
					manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
					this.drillDownAdapter.addNavigationActions(manager);
				} else if (object instanceof TreeObject) {

					// Other plug-ins can contribute there actions here
					manager.add(actions.logonWithSelectedUser);
					manager.add(actions.deleteUser);
					manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
					this.drillDownAdapter.addNavigationActions(manager);
				}

			} catch (final Exception e) {
				// TODO: handle exception
				showMessage(e.toString());
			}
		}
	}
	protected void showMessage(final String message) {
		MessageDialog.openInformation(this.viewer.getControl().getShell(), "Passwords", message);
	}
}

package com.abapblog.adt.extension.passwords.view;

import javax.swing.JOptionPane;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import com.abablog.adt.extension.passwords.ILogonService;
import com.abablog.adt.extension.passwords.LogonServiceFactory;
import com.abablog.adt.extension.passwords.SecureStorage;
import com.abapblog.adt.extension.dialogs.ChangePasswordDialog;
import com.abapblog.adt.extension.dialogs.ClientDialog;
import com.abapblog.adt.extension.dialogs.UserDialog;
import com.abapblog.adt.extension.passwords.tree.TreeObject;
import com.abapblog.adt.extension.passwords.tree.TreeParent;
import com.sap.adt.tools.core.project.AdtProjectServiceFactory;

public class Actions {
	public Action doubleClick;
	public Action addNewClient;
	public Action addNewUser;
	public Action deleteClient;
	public Action deleteUser;
	public Action logonWithSelectedUser;
	private SecureStorage secureStorage;
	private ILogonService logonService;

	public Actions() {
		secureStorage = new SecureStorage();
		logonService = LogonServiceFactory.create();
	}

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
							secureStorage.changePasswordForUser(object.getProject(), object.getClient(),
									object.getUser(), passwordDialog.getPassword(), passwordDialog.getEncryptValue());
							PasswordView.refreshViewer(viewer);
						}
					}

				}
			}
		};
	}

	public void createAddNewClient(final TreeViewer viewer) {
		this.addNewClient = new Action() {
			@Override
			public void run() {
				if (viewer.getSelection() instanceof IStructuredSelection) {
					final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

					final TreeObject object = (TreeObject) selection.getFirstElement();
					if (object instanceof TreeParent) {
						if (((TreeParent) object).getType().equals(TreeParent.TypeOfFolder.Project)) {
							ClientDialog clientDialog = new ClientDialog(null, true);
							clientDialog.create();
							if (clientDialog.open() == Window.OK) {
								secureStorage.createNodeForClient(object.getProject(), clientDialog.getClient());
								secureStorage.createNodeForUser(object.getProject(), clientDialog.getClient(),
										logonService.getUserForProject(Actions.getProjectByName(object.getProject())),
										secureStorage.EmptyPassword, false);
								PasswordView.refreshViewer(viewer);
							}
						}
					}

				}
			}
		};
		this.addNewClient.setText("Add New Client");
		this.addNewClient.setToolTipText("Add New Client");
	}

	public void createAddNewUser(final TreeViewer viewer) {
		this.addNewUser = new Action() {
			@Override
			public void run() {
				if (viewer.getSelection() instanceof IStructuredSelection) {
					final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

					final TreeObject object = (TreeObject) selection.getFirstElement();
					if (object instanceof TreeParent) {
						UserDialog userDialog = new UserDialog(null, true);
						userDialog.create();
						if (userDialog.open() == Window.OK) {
							secureStorage.createNodeForUser(object.getProject(), object.getClient(),
									userDialog.getUser(), secureStorage.EmptyPassword, false);
							PasswordView.refreshViewer(viewer);
						}
					}

				}
			}
		};
		this.addNewUser.setText("Add New User");
		this.addNewUser.setToolTipText("Add New User");
	}

	public void createDeleteClient(final TreeViewer viewer) {
		this.deleteClient = new Action() {
			@Override
			public void run() {
				if (viewer.getSelection() instanceof IStructuredSelection) {
					final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

					final TreeObject object = (TreeObject) selection.getFirstElement();
					if (object instanceof TreeParent) {
						if (((TreeParent) object).getType().equals(TreeParent.TypeOfFolder.Client)) {
							secureStorage.deleteNodeForClient(object.getProject(), object.getClient());
							PasswordView.refreshViewer(viewer);
						}
					}

				}
			}
		};
		this.deleteClient.setText("Delete Client");
		this.deleteClient.setToolTipText("Delete Client");
	}

	public void createDeleteUser(final TreeViewer viewer) {
		this.deleteUser = new Action() {
			@Override
			public void run() {
				if (viewer.getSelection() instanceof IStructuredSelection) {
					final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

					final TreeObject object = (TreeObject) selection.getFirstElement();
					if (object instanceof TreeObject) {
						secureStorage.deleteNodeForUser(object.getProject(), object.getClient(), object.getUser());
						PasswordView.refreshViewer(viewer);
					}
				}
			}
		};
		this.deleteUser.setText("Delete User");
		this.deleteUser.setToolTipText("Delete User");
	}

	public void createLogonWithSelectedUser(final TreeViewer viewer) {
		this.logonWithSelectedUser = new Action() {
			@Override
			public void run() {
				if (viewer.getSelection() instanceof IStructuredSelection) {
					final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
					final TreeObject object = (TreeObject) selection.getFirstElement();
					if (object instanceof TreeObject) {
						IProject project = Actions.getProjectByName(object.getProject());
						if (logonService.checkCanLogonWithSecureStorage(project, object.getUser(), object.getClient()))
							if (logonService.isAlreadyLoggedOn(project)) {
								if (projectShouldBeClosed()) {
									try {
										project.close(null);
										project.open(null);
									} catch (CoreException e) {
										e.printStackTrace();
									}
									logonService.LogonToProject(project, object.getUser(), object.getClient());
								}
							} else {
								logonService.LogonToProject(project, object.getUser(), object.getClient());
							}

					}
				}
			}
		};
		this.logonWithSelectedUser.setText("Logon with selected user");
		this.logonWithSelectedUser.setToolTipText("Logon with selected user");
	}

	private static IProject getProjectByName(String projectName) {
		try {
			for (IProject project : AdtProjectServiceFactory.createProjectService().getAvailableAdtCoreProjects()) {
				if (project.getName().equals(projectName))
					return project;
			}
			return null;
		} catch (Exception e) {
			return null;
		}

	}

	private Boolean projectShouldBeClosed() {
		if (JOptionPane.showConfirmDialog(null, "Do you want to continue? It will close all tabs of current project.",
				"You are already logged into this project", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE) == Window.OK) {
			return true;
		} else {
			return false;
		}
	}
}

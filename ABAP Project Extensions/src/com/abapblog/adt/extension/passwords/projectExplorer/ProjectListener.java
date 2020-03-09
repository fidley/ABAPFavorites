package com.abapblog.adt.extension.passwords.projectExplorer;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.abablog.adt.extension.passwords.ILogonService;
import com.abablog.adt.extension.passwords.LogonServiceFactory;
import com.abablog.adt.extension.passwords.SecureStorage;
import com.abapblog.adt.extension.Activator;
import com.abapblog.adt.extension.dialogs.ChangePasswordDialog;
import com.abapblog.adt.extension.passwords.view.PasswordView;
import com.abapblog.adt.extension.preferences.PreferenceConstants;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

public class ProjectListener implements IResourceChangeListener {

	public void resourceChanged(IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
			try {
				List<IProject> projects = getProjects(event.getDelta());
				// do something with new projects
				for (IProject project : projects) {
					ILogonService logonService = LogonServiceFactory.create();
					if (logonService.isAdtProject(project)) {
						if (Activator.getDefault().getPreferenceStore()
								.getBoolean(PreferenceConstants.askForPasswordAtProjectCreation))
						showPasswordDialog(project, logonService);
					}
				}
			} catch (Exception e) {

			}
		}
	}

	private void showPasswordDialog( IProject project, ILogonService logonService) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				SecureStorage secureStorage = new SecureStorage();
				ChangePasswordDialog passwordDialog = new ChangePasswordDialog(getShell());
				passwordDialog.create();
				if (passwordDialog.open() == Window.OK) {
					secureStorage.createNodesForSAPProjects();
					secureStorage.changePasswordForUser(logonService.getProjectName(project),
							logonService.getClientForProject(project), logonService.getUserForProject(project),
							passwordDialog.getPassword(), passwordDialog.getEncryptValue());
					PasswordView.refreshViewer();
				}
			}
		});
	}

	private List<IProject> getProjects(IResourceDelta delta) {
		final List<IProject> projects = new ArrayList<IProject>();
		try {
			delta.accept(new IResourceDeltaVisitor() {
				public boolean visit(IResourceDelta delta) throws CoreException {
					if (delta.getKind() == IResourceDelta.ADDED && delta.getResource().getType() == IResource.PROJECT) {
						IProject project = (IProject) delta.getResource();
						if (project.isAccessible()) {
							projects.add(project);
						}
					}
					// only continue for the workspace root
					return delta.getResource().getType() == IResource.ROOT;
				}
			});
		} catch (CoreException e) {
			// handle error
		}
		return projects;
	}

	private Shell getShell() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	}
}
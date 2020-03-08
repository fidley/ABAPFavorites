package com.abapblog.adt.extension.passwords.projectExplorer;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ITreeViewerListener;

import com.abablog.adt.extension.passwords.ILogonService;
import com.abablog.adt.extension.passwords.LogonServiceFactory;
import com.sap.adt.project.IAdtCoreProject;

public class TreeExpansionListener implements ITreeViewerListener {

	@Override
	public void treeCollapsed(org.eclipse.jface.viewers.TreeExpansionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void treeExpanded(org.eclipse.jface.viewers.TreeExpansionEvent arg0) {
		// TODO Auto-generated method stub
		if (arg0.getElement() instanceof IProject) {
			IProject project = (IProject) arg0.getElement();
			try {
				IAdtCoreProject AdtProject = project.getAdapter(IAdtCoreProject.class);
				if (AdtProject != null) {
					ILogonService logonService = LogonServiceFactory.create();
					if (logonService.checkCanLogonWithSecureStorage(project))
						logonService.LogonToProject(project);
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}

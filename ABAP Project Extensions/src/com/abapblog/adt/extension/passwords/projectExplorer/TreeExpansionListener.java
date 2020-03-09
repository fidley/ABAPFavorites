package com.abapblog.adt.extension.passwords.projectExplorer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.navigator.CommonViewer;

import com.abablog.adt.extension.passwords.ILogonService;
import com.abablog.adt.extension.passwords.LogonServiceFactory;
import com.sap.adt.project.IAdtCoreProject;

public class TreeExpansionListener implements ITreeViewerListener {

	@Override
	public void treeCollapsed(org.eclipse.jface.viewers.TreeExpansionEvent collapseEvent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void treeExpanded(org.eclipse.jface.viewers.TreeExpansionEvent expansionEvent) {
		// TODO Auto-generated method stub
		if (expansionEvent.getElement() instanceof IProject) {
			IProject project = (IProject) expansionEvent.getElement();
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

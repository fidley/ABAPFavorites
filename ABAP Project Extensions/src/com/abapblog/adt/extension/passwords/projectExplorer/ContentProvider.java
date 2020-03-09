package com.abapblog.adt.extension.passwords.projectExplorer;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ITreeContentProvider;

import com.abablog.adt.extension.passwords.ILogonService;
import com.abablog.adt.extension.passwords.LogonServiceFactory;
import com.abapblog.adt.extension.Activator;
import com.abapblog.adt.extension.preferences.PreferenceConstants;

//Dummy Content Provider to do automatic logon to SAP systems
//before the standard SAP logon popup appears.
public class ContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getChildren(Object treeObject) {
		if (Activator.getDefault().getPreferenceStore()
				.getBoolean(PreferenceConstants.doAutomaticLogonAtExpandOfProject)) {
			if (treeObject instanceof IProject) {
				IProject project = (IProject) treeObject;
				ILogonService logonService = LogonServiceFactory.create();
				if (logonService.checkCanLogonWithSecureStorage(project))
					logonService.LogonToProject(project);
			}
		}
		return null;
	}

	@Override
	public Object[] getElements(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getParent(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

}

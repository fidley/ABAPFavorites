package com.abapblog.adt.extension;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IStartup;

import com.abapblog.adt.extension.commands.LogToAllSAPSystemsHandler;
import com.abapblog.adt.extension.passwords.projectExplorer.ProjectListener;
import com.abapblog.adt.extension.preferences.PreferenceConstants;

public class Startup implements IStartup {

	@Override
	public void earlyStartup() {
		
		addProjectListener();
		logonToAllAdtSystems();

	}

	private void logonToAllAdtSystems() {
		LogToAllSAPSystemsHandler logToAllSystems = new LogToAllSAPSystemsHandler();
		try {
			if (Activator.getDefault().getPreferenceStore()
						.getBoolean(PreferenceConstants.doAutomaticLogonAtStart))
			logToAllSystems.execute(new ExecutionEvent());
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void addProjectListener() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.addResourceChangeListener(new ProjectListener());
	}
}

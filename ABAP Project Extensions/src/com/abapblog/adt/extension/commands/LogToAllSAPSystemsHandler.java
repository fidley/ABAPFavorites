package com.abapblog.adt.extension.commands;

import java.util.LinkedList;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;

import com.abablog.adt.extension.passwords.LogonServiceFactory;
import com.abablog.adt.extension.passwords.LogonWithJob;
import com.abapblog.adt.extension.Activator;
import com.sap.adt.destinations.logon.AdtLogonServiceFactory;
import com.sap.adt.destinations.logon.IAdtLogonService;
import com.sap.adt.destinations.ui.logon.AdtLogonServiceUIFactory;
import com.sap.adt.destinations.ui.logon.IAdtLogonServiceUI;
import com.sap.adt.tools.core.project.AdtProjectServiceFactory;
import com.abapblog.adt.extension.preferences.*;

public class LogToAllSAPSystemsHandler extends AbstractHandler {


	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IAdtLogonService logonService = AdtLogonServiceFactory.createLogonService();
		IAdtLogonServiceUI logonServiceUI = AdtLogonServiceUIFactory.createLogonServiceUI();

		logonToAdtProjects(logonService, logonServiceUI);
		return null;
	}

	private void logonToAdtProjects(IAdtLogonService logonService, IAdtLogonServiceUI logonServiceUI) {
		for (IProject AdtProject : AdtProjectServiceFactory.createProjectService().getAvailableAdtCoreProjects()) {
			try {
				if (logonService.isLoggedOn(AdtProject.getName()) == false) {

					if (LogonServiceFactory.create().checkCanLogonWithSecureStorage(AdtProject)) {
						LogonWithJob logonWithJob = new LogonWithJob();
						logonWithJob.logon(AdtProject);
					} else if (doAutomaticLogonForAllSystems()) {
						logonServiceUI.ensureLoggedOn((IAdaptable) AdtProject);
					}
				} else {

					logonServiceUI.ensureLoggedOn((IAdaptable) AdtProject);

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private boolean doAutomaticLogonForAllSystems() {
		if (Activator.getDefault().getPreferenceStore()
				.getBoolean(PreferenceConstants.AutomaticLogonOnlyForStoredPasswords) == false) {
			return true;
		} else {
			return false;
		}
	}


}
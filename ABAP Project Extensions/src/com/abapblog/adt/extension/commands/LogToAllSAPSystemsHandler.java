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
	private static final String ADT_PROJECT_SAP_BW_NATURE = "com.sap.bw.nature";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IAdtLogonService logonService = AdtLogonServiceFactory.createLogonService();
		IAdtLogonServiceUI logonServiceUI = AdtLogonServiceUIFactory.createLogonServiceUI();

		logonToABAPProjects(logonService, logonServiceUI);
		logonToBWProjects(logonService, logonServiceUI);
		return null;
	}

	private void logonToABAPProjects(IAdtLogonService logonService, IAdtLogonServiceUI logonServiceUI) {
		for (IProject ABAPProject : AdtProjectServiceFactory.createProjectService().getAvailableAbapProjects()) {
			try {
				if (logonService.isLoggedOn(ABAPProject.getName()) == false) {

					if (LogonServiceFactory.create().checkCanLogonWithSecureStorage(ABAPProject)) {
						LogonWithJob logonWithJob = new LogonWithJob();
						logonWithJob.logon(ABAPProject);
					} else if (doAutomaticLogonForAllSystems()) {
						logonServiceUI.ensureLoggedOn((IAdaptable) ABAPProject);
					}
				} else {

					logonServiceUI.ensureLoggedOn((IAdaptable) ABAPProject);

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

	private void logonToBWProjects(IAdtLogonService logonService, IAdtLogonServiceUI logonServiceUI) {
		for (IProject BWProject : getBWModelProjects()) {
			try {
				if (logonService.isLoggedOn(BWProject.getName()) == false) {
					if (LogonServiceFactory.create().checkCanLogonWithSecureStorage(BWProject)) {
						LogonWithJob logonWithJob = new LogonWithJob();
						logonWithJob.logon(BWProject);
					} else if (doAutomaticLogonForAllSystems()) {
						logonServiceUI.ensureLoggedOn((IAdaptable) BWProject);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public static List<IProject> getBWModelProjects() {
		List<IProject> projectList = new LinkedList<IProject>();

		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projects = workspaceRoot.getProjects();
		for (int i = 0; i < projects.length; i++) {
			IProject project = projects[i];
			try {
				if (project.hasNature(ADT_PROJECT_SAP_BW_NATURE)) {
					projectList.add(project);
				}
			} catch (CoreException ce) {
				ce.printStackTrace();
			}
		}
		return projectList;
	}

}
package com.abablog.adt.extension.passwords;

import org.eclipse.core.resources.IProject;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.sap.adt.destinations.logon.AdtLogonServiceFactory;
import com.sap.adt.destinations.logon.IAdtLogonService;
import com.sap.adt.destinations.model.IAuthenticationToken;
import com.sap.adt.destinations.model.IDestinationData;
import com.sap.adt.destinations.model.IDestinationDataWritable;
import com.sap.adt.destinations.model.internal.AuthenticationToken;
import com.sap.adt.destinations.ui.logon.AdtLogonServiceUIFactory;
import com.sap.adt.destinations.ui.logon.IAdtLogonServiceUI;
import com.sap.adt.project.IAdtCoreProject;

public class LogonService implements ILogonService {
	private SecureStorage secureStorage;
	private IAdtLogonService adtLogonService;
	private IAdtLogonServiceUI adtLogonServiceUI;

	public LogonService() {
		secureStorage = new SecureStorage();
		adtLogonService = AdtLogonServiceFactory.createLogonService();
		adtLogonServiceUI = AdtLogonServiceUIFactory.createLogonServiceUI();
	}

	@Override
	public Boolean checkCanLogonWithSecureStorage(IProject project) {
		if (isAdtProject(project)) {
			if (secureStorage.getPassword(project).equals(SecureStorage.EmptyPassword)) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public void LogonToProject(IProject project) {
		if (isAdtProject(project)) {
			if (checkCanLogonWithSecureStorage(project)) {
				IAdtCoreProject AdtProject = project.getAdapter(IAdtCoreProject.class);
				IDestinationData DestinationData = AdtProject.getDestinationData();
				IAuthenticationToken AuthenticationToken = new AuthenticationToken();
				AuthenticationToken.setPassword(secureStorage.getPassword(project));
				adtLogonService.ensureLoggedOn(DestinationData, AuthenticationToken, new NullProgressMonitor());
			} else {
				adtLogonServiceUI.ensureLoggedOn((IAdaptable) project);
			}
		}
	}

	@Override
	public void LogonToProject(IProject project, String user, String client) {
		if (isAdtProject(project)) {
			if (user == "" && client == "") {
				LogonToProject(project);
			} else {

				IAdtCoreProject AdtProject = project.getAdapter(IAdtCoreProject.class);
				IDestinationData DestinationData = AdtProject.getDestinationData();
				IDestinationDataWritable DestinationDataWritable = DestinationData.getWritable();
				if (user != "")
					DestinationDataWritable.setUser(user);
				if (client != "")
					DestinationDataWritable.setClient(client);
				IDestinationData newDestinationData = DestinationDataWritable.getReadOnlyClone();
				IAuthenticationToken AuthenticationToken = new AuthenticationToken();
				String password = secureStorage.getPassword(project.getName(), client, user);
				AuthenticationToken.setPassword(password);
				adtLogonService.ensureLoggedOn(newDestinationData, AuthenticationToken, new NullProgressMonitor());
			}
		}
	}

	public Boolean isAlreadyLoggedOn(IProject project) {
		if (isAdtProject(project)) {
			return adtLogonService.isLoggedOn(project.getName());
		} else {
			return false;
		}
	}

	@Override
	public Boolean checkCanLogonWithSecureStorage(IProject project, String user, String client) {
		if (isAdtProject(project)) {
			if (user == "" && client == "") {
				return checkCanLogonWithSecureStorage(project);
			} else {
				if (secureStorage.getPassword(project.getName(), client, user).equals(SecureStorage.EmptyPassword)) {
					return false;
				} else {
					return true;
				}
			}
		} else {
			return false;
		}
	}

	public String getUserForProject(IProject project) {
		IAdtCoreProject AdtProject = project.getAdapter(IAdtCoreProject.class);
		IDestinationData DestinationData = AdtProject.getDestinationData();
		return DestinationData.getUser();
	}

	@Override
	public Boolean isAdtProject(IProject project) {
		try {
			IAdtCoreProject AdtProject = project.getAdapter(IAdtCoreProject.class);
			if (AdtProject != null) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

}

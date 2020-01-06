package com.abablog.adt.extension.passwords;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.window.Window;

import com.abapblog.adt.extension.dialogs.UserDialog;
import com.sap.adt.destinations.logon.AdtLogonServiceFactory;
import com.sap.adt.destinations.logon.IAdtLogonService;
import com.sap.adt.destinations.model.IAuthenticationToken;
import com.sap.adt.destinations.model.IDestinationData;
import com.sap.adt.destinations.model.IDestinationDataWritable;
import com.sap.adt.destinations.model.internal.AuthenticationToken;
import com.sap.adt.destinations.ui.logon.AdtLogonServiceUIFactory;
import com.sap.adt.destinations.ui.logon.IAdtLogonServiceUI;
import com.sap.adt.tools.core.project.IAbapProject;

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
		if (secureStorage.getPassword(project).equals(SecureStorage.EmptyPassword)) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void LogonToProject(IProject project) {
		if (checkCanLogonWithSecureStorage(project)) {
			IAbapProject ABAPProject = project.getAdapter(IAbapProject.class);
			IDestinationData DestinationData = ABAPProject.getDestinationData();
			IAuthenticationToken AuthenticationToken = new AuthenticationToken();
			AuthenticationToken.setPassword(secureStorage.getPassword(project));
			adtLogonService.ensureLoggedOn(DestinationData, AuthenticationToken, new NullProgressMonitor());
		} else {
			adtLogonServiceUI.ensureLoggedOn((IAdaptable) project);
		}
	}

	@Override
	public void LogonToProject(IProject project, String user, String client) {
		if (user == "" && client == "") {
			LogonToProject(project);
		} else {
			IAbapProject ABAPProject = project.getAdapter(IAbapProject.class);
			IDestinationData DestinationData = ABAPProject.getDestinationData();
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

	@Override
	public Boolean checkCanLogonWithSecureStorage(IProject project, String user, String client) {
		if (user == "" && client == "") {
			return checkCanLogonWithSecureStorage(project);
		} else {
			if (secureStorage.getPassword(project.getName(), client, user).equals(SecureStorage.EmptyPassword)) {
				return false;
			} else {
				return true;
			}
		}
	}

}

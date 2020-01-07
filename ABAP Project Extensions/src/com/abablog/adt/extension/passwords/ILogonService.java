package com.abablog.adt.extension.passwords;

import org.eclipse.core.resources.IProject;

public interface ILogonService {

	public Boolean checkCanLogonWithSecureStorage(IProject project);
	public Boolean checkCanLogonWithSecureStorage(IProject project,String user, String client);
	public void LogonToProject(IProject project);
	public void LogonToProject(IProject project, String user, String client);
	public String getUserForProject(IProject project);
	public Boolean isAlreadyLoggedOn(IProject project);
	
	
}

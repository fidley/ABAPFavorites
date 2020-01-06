package com.abablog.adt.extension.passwords;

import java.util.ArrayList;
import org.eclipse.core.resources.IProject;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;

import com.sap.adt.destinations.model.IDestinationData;
import com.sap.adt.destinations.model.IDestinationDataWritable;
import com.sap.adt.tools.core.project.AdtProjectServiceFactory;
import com.sap.adt.tools.core.project.IAbapProject;

public class SecureStorage {
	public static final String EmptyPassword = "";
	private static final String SecureStragePreferencesNode = "com.abapblog.adt.extension.passwords";
	private ISecurePreferences pluginRoot;

	public SecureStorage() {
		ISecurePreferences preferences = SecurePreferencesFactory.getDefault();
		pluginRoot = preferences.node(SecureStragePreferencesNode);
	}

	public void changePasswordForUser(String project, String client, String user, String password, Boolean encrypt) {
		ISecurePreferences projectRoot = pluginRoot.node(project);
		ISecurePreferences clientRoot = projectRoot.node(client);
		try {
			clientRoot.put(user, password, encrypt);
		} catch (StorageException e) {
			e.printStackTrace();
		}
	}

	public void removePasswordForUser(String project, String client, String user) {
		changePasswordForUser(project, client, user, EmptyPassword, false);
	}

	public void createNodesForSAPProjects() {

		CreateNodesForABAPProjects(pluginRoot);

	}

	private void CreateNodesForABAPProjects(ISecurePreferences pluginRoot) {
		for (IProject ABAPProject : AdtProjectServiceFactory.createProjectService().getAvailableAbapProjects()) {
			createNodeForABAPProject(pluginRoot, ABAPProject);
		}
	}

	private void createNodeForABAPProject(ISecurePreferences pluginRoot, IProject ABAPProject) {
		try {
			ISecurePreferences projectRoot = pluginRoot.node(getProjectName(ABAPProject));
			ISecurePreferences clientRoot = projectRoot.node(getProjectClient(ABAPProject));

			try {
				String user = clientRoot.get(getProjectUser(ABAPProject), EmptyPassword);
				if (user == EmptyPassword) {
					clientRoot.put(getProjectUser(ABAPProject), EmptyPassword, false);
				}
			} catch (StorageException e1) {
				clientRoot.put(getProjectUser(ABAPProject), EmptyPassword, false);
				e1.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getProjectName(IProject ABAPProject) {
		return ABAPProject.getName();

	}

	public String getProjectClient(IProject project) {
		IAbapProject ABAPProject = project.getAdapter(IAbapProject.class);
		IDestinationData DestinationData = ABAPProject.getDestinationData();
		IDestinationDataWritable DestinationDataWritable = DestinationData.getWritable();
		return DestinationDataWritable.getClient();
	}

	public String getProjectUser(IProject project) {
		IAbapProject ABAPProject = project.getAdapter(IAbapProject.class);
		IDestinationData DestinationData = ABAPProject.getDestinationData();
		IDestinationDataWritable DestinationDataWritable = DestinationData.getWritable();
		return DestinationDataWritable.getUser();
	}

	public String getPassword(IProject project) {
		String client = getProjectClient(project);
		String user = getProjectUser(project);
		String projectName = getProjectName(project);
		if (pluginRoot.nodeExists(projectName)) {
			ISecurePreferences projectNode = pluginRoot.node(projectName);
			if (projectNode.nodeExists(client)) {
				ISecurePreferences clientNode = projectNode.node(client);
				try {
					return clientNode.get(user, EmptyPassword);
				} catch (StorageException e) {
					e.printStackTrace();
					return EmptyPassword;
				}
			}
		}
		return EmptyPassword;
	}
	
	public String getPassword(String project, String client, String user) {
		if (pluginRoot.nodeExists(project)) {
			ISecurePreferences projectNode = pluginRoot.node(project);
			if (projectNode.nodeExists(client)) {
				ISecurePreferences clientNode = projectNode.node(client);
				try {
					return clientNode.get(user, EmptyPassword);
				} catch (StorageException e) {
					e.printStackTrace();
					return EmptyPassword;
				}
			}
		}
		return EmptyPassword;
	}

	public ArrayList<Password> getPasswords() {
		ArrayList<Password> passwordList = new ArrayList<>();
		for (String projectName : pluginRoot.childrenNames()) {
			ISecurePreferences projectNode = pluginRoot.node(projectName);
			for (String clientName : projectNode.childrenNames()) {
				ISecurePreferences clientNode = projectNode.node(clientName);
				for (String userName : clientNode.keys()) {
					Password password = new Password();
					password.client = clientName;
					password.project = projectName;
					password.user = userName;
					try {
						password.encrypted = clientNode.isEncrypted(userName);
						password.password = clientNode.get(userName, EmptyPassword);
					} catch (StorageException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					passwordList.add(password);
				}
			}
		}
		return passwordList;
	}
}

package com.abablog.adt.extension.passwords;

import java.util.ArrayList;
import org.eclipse.core.resources.IProject;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;

import com.sap.adt.destinations.model.IDestinationData;
import com.sap.adt.destinations.model.IDestinationDataWritable;
import com.sap.adt.project.IAdtCoreProject;
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
		if (pluginRoot.nodeExists(project)) {
			ISecurePreferences projectRoot = pluginRoot.node(project);
			if (projectRoot.nodeExists(client)) {
				ISecurePreferences clientRoot = projectRoot.node(client);
				try {
					clientRoot.put(user, password, encrypt);
				} catch (StorageException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void removePasswordForUser(String project, String client, String user) {
		changePasswordForUser(project, client, user, EmptyPassword, false);
	}

	public void createNodesForSAPProjects() {

		createNodesForAdtProjects(pluginRoot);

	}

	private void createNodesForAdtProjects(ISecurePreferences pluginRoot) {
		for (IProject AdtProject : AdtProjectServiceFactory.createProjectService().getAvailableAdtCoreProjects()) {
			createNodeForAdtProject(pluginRoot, AdtProject);
		}
	}

	private void createNodeForAdtProject(ISecurePreferences pluginRoot, IProject adtProject) {
		try {
			ISecurePreferences projectRoot = pluginRoot.node(getProjectName(adtProject));
			ISecurePreferences clientRoot = projectRoot.node(getProjectClient(adtProject));

			try {
				String user = clientRoot.get(getProjectUser(adtProject), EmptyPassword);
				if (user == EmptyPassword) {
					clientRoot.put(getProjectUser(adtProject), EmptyPassword, false);
				}
			} catch (StorageException e1) {
				clientRoot.put(getProjectUser(adtProject), EmptyPassword, false);
				e1.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getProjectName(IProject adtProject) {
		return adtProject.getName();

	}

	public String getProjectClient(IProject project) {
		IAdtCoreProject AdtProject = project.getAdapter(IAdtCoreProject.class);
		IDestinationData DestinationData = AdtProject.getDestinationData();
		IDestinationDataWritable DestinationDataWritable = DestinationData.getWritable();
		return DestinationDataWritable.getClient();
	}

	public String getProjectUser(IProject project) {
		IAdtCoreProject AdtProject = project.getAdapter(IAdtCoreProject.class);
		IDestinationData DestinationData = AdtProject.getDestinationData();
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
				int test = clientNode.keys().length;
				if (test == 0) {
					Password password = new Password();
					password.client = clientName;
					password.project = projectName;
					passwordList.add(password);
				}
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

	public void createNodeForClient(String project, String client) {
		if (pluginRoot.nodeExists(project))
			pluginRoot.node(project).node(client);
	}

	public void deleteNodeForClient(String project, String client) {
		if (pluginRoot.nodeExists(project))
			pluginRoot.node(project).node(client).removeNode();
	}

	public void deleteNodeForUser(String project, String client, String user) {
		if (pluginRoot.nodeExists(project)) {
			if (pluginRoot.node(project).nodeExists(client)) {
				pluginRoot.node(project).node(client).remove(user);
			}
		}

	}

	public void createNodeForUser(String project, String client, String user, String password,
			Boolean encryptPassword) {
		changePasswordForUser(project, client, user, password, encryptPassword);
	}
}

package com.abapblog.adt.extension.passwords.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;

import com.abablog.adt.extension.passwords.Password;
import com.abablog.adt.extension.passwords.SecureStorage;
import com.abapblog.adt.extension.passwords.tree.TreeObject;
import com.abapblog.adt.extension.passwords.tree.TreeParent;
import com.sap.adt.tools.core.project.AdtProjectServiceFactory;

public class ViewContentProvider implements ITreeContentProvider {
	private TreeParent invisibleRoot;
	public IPath stateLoc;
	private IViewSite viewSite;
	private Composite container;

	public ViewContentProvider(IViewSite viewSite) {
		super();
		this.viewSite = viewSite;
	}

	@Override
	public Object[] getElements(Object parent) {
		if ((viewSite != null && parent.equals(viewSite)) || (viewSite == null && parent.equals(container))) {
			if (invisibleRoot == null)
				initialize();
			return getChildren(invisibleRoot);
		}
		return getChildren(parent);
	}

	@Override
	public Object getParent(Object child) {
		if (child instanceof TreeObject) {
			return ((TreeObject) child).getParent();
		}
		return null;
	}

	@Override
	public Object[] getChildren(Object parent) {
		if (parent instanceof TreeParent) {
			return ((TreeParent) parent).getChildren();
		}
		return new Object[0];
	}

	@Override
	public boolean hasChildren(Object parent) {
		if (parent instanceof TreeParent)
			return ((TreeParent) parent).hasChildren();
		return false;
	}

	public void initialize() {
		try {

			invisibleRoot = createInvisibleRoot();
			createTreeNodes(invisibleRoot);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private TreeParent createInvisibleRoot() {
		return new TreeParent(null, "root", TreeParent.TypeOfFolder.Root);
	}

	private void createTreeNodes(TreeParent root) {
		ArrayList<IProject> availableProjects = new ArrayList<>(
				Arrays.asList(AdtProjectServiceFactory.createProjectService().getAvailableAdtCoreProjects()));
		String project = "";
		String client = "";
		TreeParent projectNode = null;
		TreeParent clientNode = null;
		SecureStorage secureStorage = new SecureStorage();
		for (Password password : secureStorage.getPasswords()) {
			if (!project.equals(password.project)) {
				if (getProjectByName(password.project) == null)
					continue;
				projectNode = new TreeParent(root, password.project, TreeParent.TypeOfFolder.Project);
				root.addChild(projectNode);
				project = password.project;
				client = "";
			}
			if (!client.equals(password.client)) {
				clientNode = new TreeParent(projectNode, password.client, TreeParent.TypeOfFolder.Client);

				projectNode.addChild(clientNode);
				client = password.client;
			}

			if (!password.user.equals("")) {
				TreeObject userNode = new TreeObject(password.user, maskPassword(password.encrypted, password.password),
						password.encrypted, project, client, clientNode);
				clientNode.addChild(userNode);
			}

		}
	}

	private IProject getProjectByName(String projectName) {
		for (IProject project : AdtProjectServiceFactory.createProjectService().getAvailableAdtCoreProjects()) {
			if (project.getName().equals(projectName))
				return project;
		}
		return null;
	}

	private String maskPassword(Boolean encrypted, String password) {
		if (encrypted == true) {
			return "*******";
		}
		return password;
	}
}
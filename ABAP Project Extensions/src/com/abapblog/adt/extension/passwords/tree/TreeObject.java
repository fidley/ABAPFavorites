package com.abapblog.adt.extension.passwords.tree;

import org.eclipse.core.runtime.IAdaptable;

public class TreeObject implements IAdaptable {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((encrypted == null) ? 0 : encrypted.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result + ((project == null) ? 0 : project.hashCode());
		result = prime * result + ((client == null) ? 0 : client.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TreeObject other = (TreeObject) obj;
		if (encrypted == null) {
			if (other.encrypted != null)
				return false;
		}
		else if (!encrypted.equals(other.encrypted))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		}
		else if (!user.equals(other.user))
			return false;
		
		if (project == null) {
			if (other.project != null)
				return false;
		}
		else if (!project.equals(other.project))
			return false;
		
		if (client == null) {
			if (other.client != null)
				return false;
		}
		else if (!client.equals(other.client))
			return false;
		
		if (password == null) {
			if (other.password != null)
				return false;
		}
		else if (!password.equals(other.password))
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		}
		else if (!parent.equals(other.parent))
			return false;
		return true;
	}

	private TreeParent parent;
	private String user;
	private String password;
	protected String project;
	protected String client;
	private Boolean encrypted;

	public TreeObject(String user, String password, Boolean encrypted, String project, String client, TreeParent parent ) {

		this.user = user;
		this.client = client;
		this.project = project;
		setPassword(password);
		setEncryption(encrypted);
		setParent(parent);

	}

	public String getUser() {
		return user;
	}

	public void setParent(TreeParent parent) {
		this.parent = parent;
	}

	public TreeParent getParent() {
		return parent;
	}

	public String toString() {
		return getUser();
	}

	public <T> T getAdapter(Class<T> key) {
		return null;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getEncryption() {
		return encrypted;
	}

	public void setEncryption(Boolean encrypted) {
		this.encrypted = encrypted;
	}


	public String getProject() {
		return project;
	}

	public String getClient() {
		return client;
	}
}

package com.abapblog.adt.extension.passwords.tree;


import java.util.ArrayList;


public class TreeParent extends TreeObject {
	private ArrayList<TreeObject> children;
	  public enum TypeOfFolder{
	        Project, Client, Root
	    }
	private TypeOfFolder type;
	private TreeParent parent;

	public TreeParent(TreeParent parent, String name, TypeOfFolder type) {
		super( "","",false, "","", parent);
		setType(type);
		setName(name);
		children = new ArrayList<>();
	}

	public void setType(TypeOfFolder type) {
		this.type = type;
	}
	public TypeOfFolder getType() {
		return type;
	}
	public TreeParent getParent() {
		return parent;
	}
	public void addChild(TreeObject child) {
		children.add(child);
		child.setParent(this);
	}

	public void removeChild(TreeObject child) {
		children.remove(child);
		child.setParent(null);
	}

	public TreeObject[] getChildren() {
		return children.toArray(new TreeObject[children.size()]);
	}

	public boolean hasChildren() {
		return !children.isEmpty();
	}
	public String getName() {
	  return this.toString();
	}
	
	public String toString() {
		if (type == TypeOfFolder.Client) {
			return getClient();
		}
		else
		{
			return getProject();
		}
	}
	@Override
	public String getProject() {
		return project;
	}
	@Override
	public String getClient() {
		return client;
	}	
	private void setName(String name) {
		if (type == TypeOfFolder.Client) {
			client = name;
		}
		else
		{
			project = name;
		}
	}

}
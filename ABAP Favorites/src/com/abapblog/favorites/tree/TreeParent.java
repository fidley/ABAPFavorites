package com.abapblog.favorites.tree;

import java.util.ArrayList;
import java.util.UUID;

import com.abapblog.favorites.common.CommonTypes.TypeOfEntry;
import com.abapblog.favorites.common.CommonTypes.TypeOfXMLNode;
import com.abapblog.favorites.superview.IFavorites;
import com.abapblog.favorites.views.Favorites;

public class TreeParent extends TreeObject {
	private ArrayList<TreeObject> children;
	private boolean independent;
	private boolean projectDependent;
	private boolean workingSetDependent;
	private boolean devObjProject;
	private String project;
	private TypeOfXMLNode typeOfFolder;
	private String folderID;
	private String urlToOpen;
	private String workingSet;

	public TreeParent(String name, String description, boolean independent, String project, String longDescription,
			IFavorites favorite, boolean devObjProj, String folderID, String urlToOpen, boolean projectDependent,
			boolean workingSetDependent, String workingSet) {
		super(name, TypeOfEntry.Folder, description, "", longDescription, favorite, "");
		children = new ArrayList<>();
		this.independent = independent;
		this.setProject(project);
		this.setDevObjProject(devObjProj);
		if (favorite instanceof Favorites) {
			setTypeOfFolder(TypeOfXMLNode.folderNode);
		} else {
			setTypeOfFolder(TypeOfXMLNode.folderDONode);
		}
		this.setFolderID(folderID);
		if (this.getFolderID().equals("")) {
			this.setFolderID(UUID.randomUUID().toString());
		}
		this.setUrlToOpen(urlToOpen);
		this.setProjectDependent(projectDependent);
		this.setWorkingSetDependent(workingSetDependent);
		this.setWorkingSet(workingSet);
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

	public boolean getDevObjProject() {
		return devObjProject;
	}

	public void setDevObjProject(boolean devObjProject) {
		this.devObjProject = devObjProject;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public TypeOfXMLNode getTypeOfFolder() {
		return typeOfFolder;
	}

	public void setTypeOfFolder(TypeOfXMLNode typeOfFolder) {
		this.typeOfFolder = typeOfFolder;
	}

	public Boolean getIndependent() {
		return independent;
	}

	public String getFolderID() {
		return folderID;
	}

	public void setFolderID(String folderID) {
		this.folderID = folderID;
	}

	public String getUrlToOpen() {
		return urlToOpen;
	}

	public void setUrlToOpen(String urlToOpen) {
		this.urlToOpen = urlToOpen;
	}

	public boolean getProjectDependent() {
		return projectDependent;
	}

	public void setProjectDependent(boolean projectDependent) {
		this.projectDependent = projectDependent;
	}

	public boolean getWorkingSetDependent() {
		return workingSetDependent;
	}

	public void setWorkingSetDependent(boolean worskspaceDependent) {
		this.workingSetDependent = worskspaceDependent;
	}

	public String getWorkingSet() {
		return workingSet;
	}

	public void setWorkingSet(String workspace) {
		this.workingSet = workspace;
	}
}
package com.abapblog.favorites.common;

import java.util.ArrayList;

import com.abapblog.favorites.common.CommonTypes.TypeOfEntry;
import com.abapblog.favorites.common.CommonTypes.TypeOfXMLNode;
import com.abapblog.favorites.views.Favorites;

public class TreeParent extends TreeObject {
	private ArrayList children;
	private boolean ProjectIndependent;
	private boolean DevObjProject;
	private String Project;
	private TypeOfXMLNode TypeOfFolder;

	public TreeParent(String name, String description, boolean ProjectIndependent, String Project, Object Favorite,
			boolean DevObjProj) {
		super(name, TypeOfEntry.Folder, description, "", Favorite);
		children = new ArrayList();
		this.ProjectIndependent = ProjectIndependent;
		this.setProject(Project);
		this.setDevObjProject(DevObjProj);
		if (Favorite instanceof Favorites) {
			setTypeOfFolder(TypeOfXMLNode.folderNode);
		} else {
			setTypeOfFolder(TypeOfXMLNode.folderDONode);
		}
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
		return (TreeObject[]) children.toArray(new TreeObject[children.size()]);
	}

	public boolean hasChildren() {
		return children.size() > 0;
	}

	public boolean getDevObjProject() {
		return DevObjProject;
	}

	public void setDevObjProject(boolean devObjProject) {
		DevObjProject = devObjProject;
	}

	public String getProject() {
		return Project;
	}

	public void setProject(String project) {
		Project = project;
	}

	public TypeOfXMLNode getTypeOfFolder() {
		return TypeOfFolder;
	}

	public void setTypeOfFolder(TypeOfXMLNode typeOfFolder) {
		TypeOfFolder = typeOfFolder;
	}

	public Boolean getProjectIndependent() {
		return ProjectIndependent;
	}
}
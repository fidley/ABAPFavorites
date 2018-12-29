package com.abapblog.favorites.tree;

import java.util.ArrayList;
import java.util.UUID;

import com.abapblog.favorites.common.CommonTypes;
import com.abapblog.favorites.common.CommonTypes.TypeOfEntry;
import com.abapblog.favorites.common.CommonTypes.TypeOfXMLNode;
import com.abapblog.favorites.superview.IFavorites;
import com.abapblog.favorites.views.Favorites;
import com.abapblog.favoritesDO.views.FavoritesDO;

public class TreeParent extends TreeObject {
	private ArrayList children;
	private boolean ProjectIndependent;
	private boolean DevObjProject;
	private String Project;
	private TypeOfXMLNode TypeOfFolder;
	private String FolderID;
	private IFavorites favorite;

	public TreeParent(String name, String description, boolean ProjectIndependent, String Project,
			String LongDescription, IFavorites Favorite, boolean DevObjProj, String FolderID) {
		super(name, TypeOfEntry.Folder, description, "", LongDescription, Favorite);
		children = new ArrayList();
		this.ProjectIndependent = ProjectIndependent;
		this.setProject(Project);
		this.setDevObjProject(DevObjProj);
		if (Favorite instanceof Favorites) {
			setTypeOfFolder(TypeOfXMLNode.folderNode);
		} else {
			setTypeOfFolder(TypeOfXMLNode.folderDONode);
		}
		favorite = Favorite;
		this.setFolderID(FolderID);
		if (this.getFolderID() == "") {
			this.setFolderID(UUID.randomUUID().toString());
		}
	}

	public void addChild(TreeObject child) {
		children.add(child);
		child.setParent(this);
		if (favorite instanceof Favorites) {
			Favorites fav = (Favorites) favorite;
		} else {
			FavoritesDO fav = (FavoritesDO) favorite;
		}

		// fav.dndSource.addDragListener(new DragSourceListener() {
		// public void dragStart(DragSourceEvent event) {
		// TreeItem[] selection = tree.getSelection();
		// if (selection.length > 0 && selection[0].getItemCount() == 0) {
		// event.doit = true;
		// dragSourceItem[0] = selection[0];
		// } else {
		// event.doit = false;
		// }
		// };
		//
		// public void dragSetData(DragSourceEvent event) {
		// event.data = dragSourceItem[0].getText();
		// }
		//
		// public void dragFinished(DragSourceEvent event) {
		// if (event.detail == DND.DROP_MOVE)
		// dragSourceItem[0].dispose();
		// dragSourceItem[0] = null;
		// }
		// });

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

	public String getFolderID() {
		return FolderID;
	}

	public void setFolderID(String folderID) {
		FolderID = folderID;
	}
}
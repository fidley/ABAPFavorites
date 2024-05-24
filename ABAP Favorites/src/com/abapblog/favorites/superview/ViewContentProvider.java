package com.abapblog.favorites.superview;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;

import com.abapblog.favorites.common.Common;
import com.abapblog.favorites.common.CommonTypes.TypeOfXMLNode;
import com.abapblog.favorites.tree.TreeObject;
import com.abapblog.favorites.tree.TreeParent;

public class ViewContentProvider implements ITreeContentProvider {
	private TreeParent invisibleRoot;
	public IPath stateLoc;
	private Common Utils;
	private TypeOfXMLNode folderNode;
	public Superview favorite;
	private IViewSite viewSite;
	private Composite container;
	private Boolean selectFolderDialog = false;
	public IProject TempLinkedProject;

	public ViewContentProvider(TypeOfXMLNode folderNode, Superview favorite, IViewSite viewSite) {
		super();
		this.folderNode = folderNode;
		this.favorite = favorite;
		this.viewSite = viewSite;
	}

	public ViewContentProvider(TypeOfXMLNode folderNode, IFavorites favorite, Composite container) {
		super();
		this.folderNode = folderNode;
		this.container = container;
		this.favorite = (Superview) favorite;
		this.selectFolderDialog = true;
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

	public TreeParent getFolderById(String folderID) {
		return getFolderByIdFromChildren(invisibleRoot, folderID);
	}

	private TreeParent getFolderByIdFromChildren(TreeParent parent, String folderID) {
		for (int i = 0; i < parent.getChildren().length; i++) {
			if (parent.getChildren()[i] instanceof TreeParent) {
				TreeParent folder = (TreeParent) parent.getChildren()[i];
				if (folder.getFolderID().equals(folderID)) {
					return folder;
				}
				folder = getFolderByIdFromChildren(folder, folderID);
				if (folder != null) {
					return folder;
				}
			}
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object parent) {
		if (parent instanceof TreeParent)
			return ((TreeParent) parent).hasChildren();
		return false;
	}

	public void initialize() {
		try {
			invisibleRoot = null;
			invisibleRoot = Superview.createTreeNodes(folderNode, favorite, selectFolderDialog);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TreeObject getRoot() {
		return invisibleRoot;
	}

}
package com.abapblog.favorites.common;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.ui.IViewSite;

import com.abapblog.favorites.common.CommonTypes.TypeOfXMLNode;
import com.abapblog.favorites.superview.IFavorites;

public class ViewContentProvider implements ITreeContentProvider {
	private TreeParent invisibleRoot;
	public IPath stateLoc;
	private Common Utils;
	private TypeOfXMLNode folderNode;
	public IFavorites favorite;
	private IViewSite viewSite;

	public ViewContentProvider(TypeOfXMLNode folderNode, IFavorites favorite, IViewSite viewSite) {
		super();
		this.folderNode = folderNode;
		this.favorite = favorite;
		this.viewSite = viewSite;
		Utils = new Common(folderNode);
	}

	@Override
	public Object[] getElements(Object parent) {
		if (parent.equals(viewSite)) {
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
			invisibleRoot = Utils.createTreeNodes(folderNode, favorite);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
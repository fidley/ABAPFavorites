package com.abapblog.favorites.superview;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.ui.IViewSite;

import com.abapblog.favorites.common.Common;
import com.abapblog.favorites.common.CommonTypes;
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

	public ViewContentProvider(TypeOfXMLNode folderNode, Superview favorite, IViewSite viewSite) {
		super();
		this.folderNode = folderNode;
		this.favorite = favorite;
		this.viewSite = viewSite;
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
			invisibleRoot = favorite.createTreeNodes(folderNode, favorite);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
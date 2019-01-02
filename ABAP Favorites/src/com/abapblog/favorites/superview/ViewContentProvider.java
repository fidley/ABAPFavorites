package com.abapblog.favorites.superview;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.widgets.Composite;
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
	private Composite container;
	private Boolean selectFolderDialog = false;

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
		if ( (viewSite != null && parent.equals(viewSite))|| (viewSite == null && parent.equals(container))) {
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

			invisibleRoot = Superview.createTreeNodes(folderNode, favorite,selectFolderDialog);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
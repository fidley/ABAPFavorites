package com.abapblog.favorites.tree;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.events.TreeAdapter;
import org.eclipse.swt.events.TreeEvent;

import com.abapblog.favorites.superview.IFavorites;

public class TreeExpansionListener extends TreeAdapter {
	private IFavorites favorite;

	public TreeExpansionListener(IFavorites Favorite) {
		this.favorite = Favorite;
	}

	public void treeExpanded(TreeEvent event) {

		TreeParent node = (TreeParent) event.item.getData();
		if (!favorite.getExpandedNodes().contains(node.getFolderID()))
			favorite.getExpandedNodes().add(node.getFolderID());
		if (!favorite.getExpandedParentNodes().contains(node))
			favorite.getExpandedParentNodes().add(node);

	}

	public void treeCollapsed(TreeEvent event) {
		TreeParent node = (TreeParent) event.item.getData();
		favorite.getExpandedNodes().remove(node.getFolderID());
		favorite.getExpandedParentNodes().remove(node);

	}

}

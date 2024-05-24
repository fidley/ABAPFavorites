package com.abapblog.favorites.tree;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import com.abapblog.favorites.superview.IFavorites;

public class AFFilteredTree extends FilteredTree {
	public AFFilteredTree(Composite parent, int treeStyle, PatternFilter filter, boolean useNewLook,
			boolean useFastHashLookup, IFavorites favorite) {
		super(parent, treeStyle, filter, useNewLook, useFastHashLookup);

		this.favorite = favorite;
	}

	private IFavorites favorite;

	public IFavorites getFavorite() {
		return favorite;
	}

}

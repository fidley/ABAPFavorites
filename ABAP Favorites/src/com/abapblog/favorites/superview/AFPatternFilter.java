package com.abapblog.favorites.superview;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.dialogs.PatternFilter;

import com.abapblog.favorites.tree.TreeObject;

public class AFPatternFilter extends PatternFilter {
	@Override
	protected boolean isLeafMatch(final Viewer viewer, final Object element) {

		boolean isMatch = false;
		if (element instanceof TreeObject) {
			TreeObject leaf = (TreeObject) element;
			isMatch |= wordMatches(leaf.getName());
			if (isMatch == false) {
				isMatch |= wordMatches(leaf.getDescription());
			}
		}
		return isMatch;
	}

}
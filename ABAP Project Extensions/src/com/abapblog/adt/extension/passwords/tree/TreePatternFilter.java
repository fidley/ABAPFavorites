package com.abapblog.adt.extension.passwords.tree;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.dialogs.PatternFilter;


public class TreePatternFilter extends PatternFilter {
		@Override
		protected boolean isLeafMatch(final Viewer viewer, final Object element) {

			boolean isMatch = false;
			if (element instanceof TreeObject) {
				TreeObject leaf = (TreeObject) element;
				isMatch |= wordMatches(leaf.toString());
			}
			return isMatch;
		}

	}
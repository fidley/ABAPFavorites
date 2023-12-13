package com.abapblog.favorites.superview.labelproviders;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;

import com.abapblog.favorites.tree.TreeObject;

public class LongDecriptionCellLabelProvider extends StyledCellLabelProvider {
	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		if (element instanceof TreeObject) {
			TreeObject node = (TreeObject) element;
			StyledString styledString = new StyledString(node.getLongDescription(), StyledString.QUALIFIER_STYLER);
			cell.setText(styledString.toString());
			cell.setStyleRanges(styledString.getStyleRanges());
		}

		super.update(cell);

	}
}

package com.abapblog.favorites.superview.labelproviders;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;

import com.abapblog.favorites.tree.TreeObject;
import com.abapblog.favorites.tree.TreeParent;

public class LinkedToCellLabelProvider extends StyledCellLabelProvider {

	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		if (element instanceof TreeObject) {
			TreeObject node = (TreeObject) element;

			String linkedTo = "";
			if (element instanceof TreeParent) {
				TreeParent parent = ((TreeParent) element);
				if (parent.getProjectIndependent() == false)
					linkedTo = ((TreeParent) element).getProject().toString();
			} else if (element instanceof TreeObject) {
				TreeObject object = ((TreeObject) element);
				if (object.getParent().getProjectIndependent() == false)
					linkedTo = object.getParent().getProject().toString();
			}

			if (!linkedTo.equals(""))
				linkedTo = "[" + linkedTo + "]";
			StyledString styledString = new StyledString(linkedTo, StyledString.DECORATIONS_STYLER);
			cell.setText(styledString.toString());
			cell.setStyleRanges(styledString.getStyleRanges());

		}

		super.update(cell);

	}

}

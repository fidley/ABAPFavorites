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
				if (parent.getProjectDependent() == true)
					linkedTo = ((TreeParent) element).getProject().toString();
				if (parent.getWorkingSetDependent() == true)
					linkedTo = ((TreeParent) element).getWorkingSet();
			} else if (element instanceof TreeObject) {
				TreeObject object = ((TreeObject) element);
				if (object.getParent().getProjectDependent() == true)
					linkedTo = object.getParent().getProject().toString();
				if (object.getParent().getWorkingSetDependent() == true)
					linkedTo = object.getParent().getWorkingSet();
			}

			if (linkedTo != null && !linkedTo.equals(""))
				linkedTo = "[" + linkedTo + "]";
			StyledString styledString = new StyledString(linkedTo, StyledString.DECORATIONS_STYLER);
			cell.setText(styledString.toString());
			cell.setStyleRanges(styledString.getStyleRanges());

		}

		super.update(cell);

	}

}

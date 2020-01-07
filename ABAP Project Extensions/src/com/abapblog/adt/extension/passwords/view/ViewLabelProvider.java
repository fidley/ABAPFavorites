package com.abapblog.adt.extension.passwords.view;



import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.abapblog.adt.extension.icons.Icons;
import com.abapblog.adt.extension.passwords.tree.TreeObject;
import com.abapblog.adt.extension.passwords.tree.TreeParent;


public class ViewLabelProvider implements ITableLabelProvider {
	private Icons icons = new Icons();
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		switch (columnIndex) {
		case 0:
			String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
			if (element instanceof TreeParent) {
				if (((TreeParent) element).getType().equals(TreeParent.TypeOfFolder.Project))
				{
					return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_PROJECT);
				}
				else
				{
					return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
				} 
				}
			if (element instanceof TreeObject) {
				return icons.getIcon(Icons.ICON_USER_NODE);
				}

			return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
		case 1:
			return null;
		}
		return null;
	}
	
	

	@Override
	public String getColumnText(Object element, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return element.toString();
		case 1:
			if (element instanceof TreeObject)
				return ((TreeObject) element).getPassword();
		case 2:
			if (element instanceof TreeObject)
				return ((TreeObject) element).getEncryption().toString();
		}
		return null;
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}

}

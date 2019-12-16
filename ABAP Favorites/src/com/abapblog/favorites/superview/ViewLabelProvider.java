package com.abapblog.favorites.superview;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.abapblog.favorites.common.AFIcons;
import com.abapblog.favorites.tree.TreeObject;
import com.abapblog.favorites.tree.TreeParent;

public class ViewLabelProvider implements ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		AFIcons AFIcons = new AFIcons();
		switch (columnIndex) {
		case 0:
			String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
			if (element instanceof TreeParent)
				if (((TreeParent) element).getDevObjProject() == true) {
					return AFIcons.getFodlerDevObjIcon();
				} else {
					return AFIcons.getFolderIcon();
				}

			if (element instanceof TreeObject) {
				TreeObject Node = (TreeObject) element;
				switch (Node.getType()) {
				case Transaction:
					return AFIcons.getTransactionIcon();
				case URL:
					return AFIcons.getURLIcon();
				case Program:
					return AFIcons.getProgramIcon();
				case Class:
					return AFIcons.getClassIcon();
				case Interface:
					return AFIcons.getInterfaceIcon();
				case Include:
					return AFIcons.getProgramIncludeIcon();
				case FunctionGroup:
					return AFIcons.getFunctionGroupIcon();
				case FunctionModule:
					return AFIcons.getFunctionModuleIcon();
				case Folder:
					return AFIcons.getFolderIcon();
				case FolderDO:
					return AFIcons.getFodlerDevObjIcon();
				case MessageClass:
					return AFIcons.getMessageClassIcon();
				case View:
					return AFIcons.getViewIcon();
				case Table:
					return AFIcons.getTableIcon();
				case SearchHelp:
					return AFIcons.getSearchHelpIcon();
				case ADTLink:
					return AFIcons.getADTLinkIcon();
				case CDSView:
					return AFIcons.getCDSViewIcon();
				case AMDP:
					return AFIcons.getAMDPIcon();
				case Package:
					return AFIcons.getPackageIcon();
				default:
					break;
				}

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
				return ((TreeObject) element).getDescription();
		case 2:
			if (element instanceof TreeParent)
				return ((TreeParent) element).getFolderID();
		case 3:
			if (element instanceof TreeParent)
				return ((TreeParent) element).getTypeOfFolder().toString();
		case 4:
			if (element instanceof TreeParent)
				if (((TreeParent) element).getDevObjProject() == true)
					return "true";
		case 5:
			if (element instanceof TreeParent)
				{ TreeParent parent = ((TreeParent) element);
				if (parent.getProjectIndependent() == false)
				return ((TreeParent) element).getProject().toString();
				}
			else if (element instanceof TreeObject)
			{
				TreeObject object = ((TreeObject) element);
				if (object.getParent().getProjectIndependent() == false)
					return object.getParent().getProject().toString();
			}
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

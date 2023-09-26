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
	private static final AFIcons afIcons = AFIcons.getInstance();

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		switch (columnIndex) {
		case 0:
			String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
			if (element instanceof TreeParent)
				if (((TreeParent) element).getDevObjProject() == true) {
					return afIcons.getFodlerDevObjIcon();
				} else {
					return afIcons.getFolderIcon();
				}

			if (element instanceof TreeObject) {
				TreeObject Node = (TreeObject) element;
				switch (Node.getType()) {
				case Transaction:
					return afIcons.getTransactionIcon();
				case URL:
					return afIcons.getURLIcon();
				case Program:
					return afIcons.getProgramIcon();
				case Class:
					return afIcons.getClassIcon();
				case Interface:
					return afIcons.getInterfaceIcon();
				case Include:
					return afIcons.getProgramIncludeIcon();
				case FunctionGroup:
					return afIcons.getFunctionGroupIcon();
				case FunctionModule:
					return afIcons.getFunctionModuleIcon();
				case Folder:
					return afIcons.getFolderIcon();
				case FolderDO:
					return afIcons.getFodlerDevObjIcon();
				case MessageClass:
					return afIcons.getMessageClassIcon();
				case View:
					return afIcons.getViewIcon();
				case Table:
					return afIcons.getTableIcon();
				case SearchHelp:
					return afIcons.getSearchHelpIcon();
				case ADTLink:
					return afIcons.getADTLinkIcon();
				case CDSView:
					return afIcons.getCDSViewIcon();
				case AMDP:
					return afIcons.getAMDPIcon();
				case Package:
					return afIcons.getPackageIcon();
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
			if (element instanceof TreeParent) {
				TreeParent parent = ((TreeParent) element);
				if (parent.getProjectIndependent() == false)
					return ((TreeParent) element).getProject().toString();
			} else if (element instanceof TreeObject) {
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

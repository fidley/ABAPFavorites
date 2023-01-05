package com.abapblog.favorites.superview;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.abapblog.favorites.common.AFIcons;
import com.abapblog.favorites.tree.TreeObject;
import com.abapblog.favorites.tree.TreeParent;

public class ViewLabelProvider implements ITableLabelProvider {
	private final List<Image> imagesToBeDisposed = new ArrayList<>();
	private static final AFIcons afIcons = AFIcons.getInstance();

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		switch (columnIndex) {
		case 0:
			String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
			if (element instanceof TreeParent)
				if (((TreeParent) element).getDevObjProject() == true) {
					return addToBeDisposed(afIcons.getFodlerDevObjIcon());
				} else {
					return addToBeDisposed(afIcons.getFolderIcon());
				}

			if (element instanceof TreeObject) {
				TreeObject Node = (TreeObject) element;
				switch (Node.getType()) {
				case Transaction:
					return addToBeDisposed(afIcons.getTransactionIcon());
				case URL:
					return addToBeDisposed(afIcons.getURLIcon());
				case Program:
					return addToBeDisposed(afIcons.getProgramIcon());
				case Class:
					return addToBeDisposed(afIcons.getClassIcon());
				case Interface:
					return addToBeDisposed(afIcons.getInterfaceIcon());
				case Include:
					return addToBeDisposed(afIcons.getProgramIncludeIcon());
				case FunctionGroup:
					return addToBeDisposed(afIcons.getFunctionGroupIcon());
				case FunctionModule:
					return addToBeDisposed(afIcons.getFunctionModuleIcon());
				case Folder:
					return addToBeDisposed(afIcons.getFolderIcon());
				case FolderDO:
					return addToBeDisposed(afIcons.getFodlerDevObjIcon());
				case MessageClass:
					return addToBeDisposed(afIcons.getMessageClassIcon());
				case View:
					return addToBeDisposed(afIcons.getViewIcon());
				case Table:
					return addToBeDisposed(afIcons.getTableIcon());
				case SearchHelp:
					return addToBeDisposed(afIcons.getSearchHelpIcon());
				case ADTLink:
					return addToBeDisposed(afIcons.getADTLinkIcon());
				case CDSView:
					return addToBeDisposed(afIcons.getCDSViewIcon());
				case AMDP:
					return addToBeDisposed(afIcons.getAMDPIcon());
				case Package:
					return addToBeDisposed(afIcons.getPackageIcon());
				default:
					break;
				}

			}

			return addToBeDisposed(PlatformUI.getWorkbench().getSharedImages().getImage(imageKey));
		case 1:
			return null;
		}
		return null;
	}

	private Image addToBeDisposed(Image image) {
		if (image != null && !imagesToBeDisposed.contains(image)) {
			imagesToBeDisposed.add(image);
		}
		return image;
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
		for (Image image : imagesToBeDisposed) {
			image.dispose();
		}
		imagesToBeDisposed.clear();
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}

}

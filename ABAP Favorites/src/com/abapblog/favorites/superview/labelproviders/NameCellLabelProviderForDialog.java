package com.abapblog.favorites.superview.labelproviders;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.abapblog.favorites.Activator;
import com.abapblog.favorites.common.AFIcons;
import com.abapblog.favorites.preferences.PreferenceConstants;
import com.abapblog.favorites.tree.TreeObject;
import com.abapblog.favorites.tree.TreeParent;

public class NameCellLabelProviderForDialog extends StyledCellLabelProvider {
	private static final AFIcons afIcons = AFIcons.getInstance();
	private static final IPreferenceStore store = Activator.getDefault().getPreferenceStore();

	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		if (element instanceof TreeObject) {
			TreeObject node = (TreeObject) element;
			StyledString styledString = new StyledString(node.getName());
			addDescription(node, styledString);
			cell.setText(styledString.toString());
			cell.setStyleRanges(styledString.getStyleRanges());
			try {
				cell.setImage(getImage(node));
			} catch (Exception e) {
				System.out.println(e.getLocalizedMessage());
			}
		}

		super.update(cell);

	}

	private void addDescription(TreeObject node, StyledString styledString) {
		if (!node.getDescription().equals("")) {
			styledString.append(" " + node.getDescription(), StyledString.DECORATIONS_STYLER);
		}
	}

	@Override
	public String getToolTipText(Object element) {
		if (store.getBoolean(PreferenceConstants.P_SHOW_LONG_TEXT_COLUMN))
			return null;
		String tooltip = null;
		if (element instanceof TreeObject) {
			TreeObject node = (TreeObject) element;
			tooltip = node.getLongDescription();
		}
		if (tooltip != null && !tooltip.equals(""))
			return tooltip;
		return null;
	}

	public Image getImage(Object element) {
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

	}
}

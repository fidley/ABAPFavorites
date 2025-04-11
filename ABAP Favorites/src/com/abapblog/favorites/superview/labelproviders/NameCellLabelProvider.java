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

public class NameCellLabelProvider extends StyledCellLabelProvider {
	private static final AFIcons afIcons = AFIcons.getInstance();
	private static final IPreferenceStore store = Activator.getDefault().getPreferenceStore();

	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		if (element instanceof TreeObject) {
			TreeObject node = (TreeObject) element;
			StyledString styledString = new StyledString(node.getName());
			addCounter(node, styledString);
			addDescription(node, styledString);
			addLinkedTo(element, styledString);
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

	private void addCounter(TreeObject node, StyledString styledString) {
		if (node instanceof TreeParent) {
			String counter = "";
			counter = " " + addNumberOfChildrenToDecoration(counter, (TreeParent) node);
			styledString.append(counter, StyledString.COUNTER_STYLER);
		}
	}

	private void addDescription(TreeObject node, StyledString styledString) {
		if (!node.getDescription().equals("")) {
			styledString.append(" " + node.getDescription(), StyledString.DECORATIONS_STYLER);
		}
	}

	private void addLinkedTo(Object element, StyledString styledString) {
		if (store.getBoolean(PreferenceConstants.P_SHOW_LINKED_TO_COLUMN))
			return;

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

		if (!linkedTo.equals("")) {
			linkedTo = " [" + linkedTo + "]";
			styledString.append(linkedTo, StyledString.QUALIFIER_STYLER);
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

	private String addNumberOfChildrenToDecoration(String decoration, TreeParent parent) {

		return "(" + getElementChilderNumber(parent) + ")" + decoration;
	}

	private int getElementChilderNumber(TreeParent parent) {
		int numberOfElements = 0;
		for (TreeObject child : parent.getChildren()) {
			try {
				if (!(child instanceof TreeParent)) {
					++numberOfElements;
				}
			} catch (Exception e) {

			}
		}

		for (TreeObject child : parent.getChildren()) {
			try {
				numberOfElements = numberOfElements + getElementChilderNumber((TreeParent) child);
			} catch (Exception e) {

			}
		}
		return numberOfElements;

	}

	public static Image getImage(Object element) {
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

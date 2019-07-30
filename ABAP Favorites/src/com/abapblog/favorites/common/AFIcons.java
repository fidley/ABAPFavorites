package com.abapblog.favorites.common;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class AFIcons {

	public ImageDescriptor getImageDescriptor(String Name) {
		Bundle bundle = FrameworkUtil.getBundle(getClass());
		URL url = FileLocator.find(bundle, new Path("icons/" + Name), null);
		return ImageDescriptor.createFromURL(url);
	}

	public Image getIcon(String Name) {

		Bundle bundle = FrameworkUtil.getBundle(getClass());
		URL url = FileLocator.find(bundle, new Path("icons/" + Name), null);
		ImageDescriptor imageDcr = ImageDescriptor.createFromURL(url);
		return imageDcr.createImage();
	}

	public Image getTransactionIcon() {
		return getIcon("transaction.png");
	}

	public Image getRenameIcon() {
		return getIcon("rename-box.png");
	}

	public Image getURLIcon() {
		return getIcon("url.png");
	}

	public Image getProgramIcon() {
		return getIcon("program.png");
	}

	public Image getFolderIcon() {
		return getIcon("folder.png");
	}

	public Image getClassIcon() {
		return getIcon("class.png");
	}

	public Image getInterfaceIcon() {
		return getIcon("interface.png");
	}

	public Image getFunctionGroupIcon() {
		return getIcon("functiongroup.png");
	}

	public Image getFunctionGroupIncludeIcon() {
		return getIcon("fg_include.png");
	}

	public Image getFunctionModuleIcon() {
		return getIcon("fm.png");
	}

	public Image getProgramIncludeIcon() {
		return getIcon("program_include.png");
	}

	public Image getFodlerDevObjIcon() {
		return getIcon("folderDev.png");
	}

	public Image getViewIcon() {
		return getIcon("view.png");
	}

	public Image getTableIcon() {
		return getIcon("table.png");
	}

	public Image getMessageClassIcon() {
		return getIcon("message_class.png");
	}

	public Image getADTLinkIcon() {
		return getIcon("adt_link.png");
	}

	public Image getSearchHelpIcon() {
		return getIcon("search_help.png");
	}

	public Image getCDSViewIcon() {
		return getIcon("cds.png");
	}

	public Image getAMDPIcon() {
		return getIcon("search_help.png");
	}

	public ImageDescriptor getTransactionImgDescr() {
		return getImageDescriptor("transaction.png");
	}

	public ImageDescriptor getCDSViewImgDescr() {
		return getImageDescriptor("cds.png");
	}

	public ImageDescriptor getAMDPImgDescr() {
		return getImageDescriptor("transaction.png");
	}

	public ImageDescriptor getADTLinkImgDescr() {
		return getImageDescriptor("adt_link.png");
	}

	public ImageDescriptor getURLIconImgDescr() {
		return getImageDescriptor("url.png");
	}

	public ImageDescriptor getProgramIconImgDescr() {
		return getImageDescriptor("program.png");
	}

	public ImageDescriptor getFolderIconImgDescr() {
		return getImageDescriptor("folder.png");
	}

	public ImageDescriptor getClassIconImgDescr() {
		return getImageDescriptor("class.png");
	}

	public ImageDescriptor getInterfaceIconImgDescr() {
		return getImageDescriptor("interface.png");
	}

	public ImageDescriptor getFunctionGroupIconImgDescr() {
		return getImageDescriptor("functiongroup.png");
	}

	public ImageDescriptor getRenameIconImgDescr() {
		return getImageDescriptor("rename-box.png");
	}

	public ImageDescriptor getFunctionGroupIncludeIconImgDescr() {
		return getImageDescriptor("fg_include.png");
	}

	public ImageDescriptor getFunctionModuleIconImgDescr() {
		return getImageDescriptor("fm.png");
	}

	public ImageDescriptor getProgramIncludeIconImgDescr() {
		return getImageDescriptor("program_include.png");
	}

	public ImageDescriptor getFodlerDevObjIconImgDescr() {
		return getImageDescriptor("folderDev.png");
	}

	public ImageDescriptor getViewIconImgDescr() {
		return getImageDescriptor("view.png");
	}

	public ImageDescriptor getTableIconImgDescr() {
		return getImageDescriptor("table.png");
	}

	public ImageDescriptor getMessageClassIconImgDescr() {
		return getImageDescriptor("message_class.png");
	}

	public ImageDescriptor getSearchHelpIconImgDescr() {
		return getImageDescriptor("search_help.png");
	}
	
	public ImageDescriptor getCopyToClipboardImgDescr() {
		return getImageDescriptor("clipboard.png");
	}

}

package com.abapblog.favorites.common;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class AFIcons {

	private static AFIcons singleton;
	public static final String ICON_TRANSACTION = "TRANSACTION";
	public static final String ICON_RENAME = "RENAME";
	public static final String ICON_URL = "URL";
	public static final String ICON_PACKAGE = "PACKAGE";
	public static final String ICON_PROGRAM = "PROGRAM";
	public static final String ICON_FOLDER = "FOLDER";
	public static final String ICON_CLASS = "CLASS";
	public static final String ICON_INTERFACE = "INTERFACE";
	public static final String ICON_FUNCTION_GROUP = "FUNCTION_GROUP";
	public static final String ICON_FUNCTION_GROUP_INCLUDE = "FUNCTION_GROUP_INCLUDE";
	public static final String ICON_FUNCTION_MODULE = "FUNCTION_MODULE";
	public static final String ICON_PROGRAM_INCLUDE = "PROGRAM_INCLUDE";
	public static final String ICON_FOLDER_DEV = "FOLDER_DEV";
	public static final String ICON_VIEW = "VIEW";
	public static final String ICON_TABLE = "TABLE";
	public static final String ICON_MESSAGE_CLASS = "MESSAGE_CLASS";
	public static final String ICON_ADT_LINK = "ADT_LINK";
	public static final String ICON_SEARCH_HELP = "SEARCH_HELP";
	public static final String ICON_CDS = "CDS";
	public static final String ICON_AMDP = "AMDP";

	private static final ImageRegistry registry = new ImageRegistry();

	private AFIcons() {
		if (singleton == null)
			singleton = this;
		registerIcons();
	}

	private void registerIcons() {
		registry.put(ICON_TRANSACTION, getTransactionImgDescr().createImage());
		registry.put(ICON_RENAME, getRenameIconImgDescr().createImage());
		registry.put(ICON_URL, getURLIconImgDescr().createImage());
		registry.put(ICON_PACKAGE, getPackageIconImgDescr().createImage());
		registry.put(ICON_PROGRAM, getProgramIconImgDescr().createImage());
		registry.put(ICON_FOLDER, getFolderIconImgDescr().createImage());
		registry.put(ICON_CLASS, getClassIconImgDescr().createImage());
		registry.put(ICON_INTERFACE, getInterfaceIconImgDescr().createImage());
		registry.put(ICON_FUNCTION_GROUP, getFunctionGroupIconImgDescr().createImage());
		registry.put(ICON_FUNCTION_GROUP_INCLUDE, getFunctionGroupIncludeIconImgDescr().createImage());
		registry.put(ICON_FUNCTION_MODULE, getFunctionModuleIconImgDescr().createImage());
		registry.put(ICON_PROGRAM_INCLUDE, getProgramIncludeIconImgDescr().createImage());
		registry.put(ICON_FOLDER_DEV, getFodlerDevObjIconImgDescr().createImage());
		registry.put(ICON_VIEW, getViewIconImgDescr().createImage());
		registry.put(ICON_TABLE, getTableIconImgDescr().createImage());
		registry.put(ICON_MESSAGE_CLASS, getMessageClassIconImgDescr().createImage());
		registry.put(ICON_ADT_LINK, getADTLinkImgDescr().createImage());
		registry.put(ICON_SEARCH_HELP, getSearchHelpIconImgDescr().createImage());
		registry.put(ICON_CDS, getCDSViewImgDescr().createImage());
		registry.put(ICON_AMDP, getAMDPImgDescr().createImage());
	}

	public static AFIcons getInstance() {
		if (singleton == null)
			singleton = new AFIcons();
		return singleton;
	}

	public ImageDescriptor getImageDescriptor(String Name) {
		Bundle bundle = FrameworkUtil.getBundle(getClass());
		URL url = FileLocator.find(bundle, new Path("icons/" + Name), null);
		return ImageDescriptor.createFromURL(url);
	}

	public Image getTransactionIcon() {
		return registry.get(ICON_TRANSACTION);
	}

	public Image getRenameIcon() {
		return registry.get(ICON_RENAME);
	}

	public Image getURLIcon() {
		return registry.get(ICON_URL);
	}

	public Image getPackageIcon() {
		return registry.get(ICON_PACKAGE);
	}

	public Image getProgramIcon() {
		return registry.get(ICON_PROGRAM);
	}

	public Image getFolderIcon() {
		return registry.get(ICON_FOLDER);
	}

	public Image getClassIcon() {

		return registry.get(ICON_CLASS);

	}

	public Image getInterfaceIcon() {
		return registry.get(ICON_INTERFACE);
	}

	public Image getFunctionGroupIcon() {
		return registry.get(ICON_FUNCTION_GROUP);

	}

	public Image getFunctionGroupIncludeIcon() {
		return registry.get(ICON_FUNCTION_GROUP_INCLUDE);

	}

	public Image getFunctionModuleIcon() {
		return registry.get(ICON_FUNCTION_MODULE);
	}

	public Image getProgramIncludeIcon() {
		return registry.get(ICON_PROGRAM_INCLUDE);
	}

	public Image getFodlerDevObjIcon() {
		return registry.get(ICON_FOLDER_DEV);
	}

	public Image getViewIcon() {
		return registry.get(ICON_VIEW);
	}

	public Image getTableIcon() {
		return registry.get(ICON_TABLE);
	}

	public Image getMessageClassIcon() {
		return registry.get(ICON_MESSAGE_CLASS);
	}

	public Image getADTLinkIcon() {
		return registry.get(ICON_ADT_LINK);
	}

	public Image getSearchHelpIcon() {
		return registry.get(ICON_SEARCH_HELP);
	}

	public Image getCDSViewIcon() {
		return registry.get(ICON_CDS);
	}

	public Image getAMDPIcon() {
		return registry.get(ICON_AMDP);
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

	public ImageDescriptor getPackageIconImgDescr() {
		return getImageDescriptor("package.png");
	}

	public void dispose() {

	}
}

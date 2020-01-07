package com.abapblog.adt.extension.icons;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class Icons {
	
	public final static String ICON_PASSWORDS = "passwords.png";
	public final static String ICON_USER_NODE = "user.png";
	
	public Image getIcon(String Name) {

		Bundle bundle = FrameworkUtil.getBundle(getClass());
		URL url = FileLocator.find(bundle, new Path("icons/" + Name), null);
		ImageDescriptor imageDcr = ImageDescriptor.createFromURL(url);
		return imageDcr.createImage();
	}
	
	public ImageDescriptor getImageDescriptor(String Name) {
		Bundle bundle = FrameworkUtil.getBundle(getClass());
		URL url = FileLocator.find(bundle, new Path("icons/" + Name), null);
		return ImageDescriptor.createFromURL(url);
	}

}

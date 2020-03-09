package com.abapblog.adt.extension.passwords.projectExplorer;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

@SuppressWarnings("restriction")
public class ActionProvider extends CommonActionProvider {

	public void init(ICommonActionExtensionSite actionSite) {
		super.init(actionSite);
		ICommonViewerSite site = actionSite.getViewSite();
		if (site instanceof ICommonViewerWorkbenchSite) {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			workspace.addResourceChangeListener(new ProjectListener());

		}

	}
}
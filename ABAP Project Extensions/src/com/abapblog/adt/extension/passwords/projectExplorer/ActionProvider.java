package com.abapblog.adt.extension.passwords.projectExplorer;


import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

	@SuppressWarnings("restriction")
	public class ActionProvider  extends CommonActionProvider {

		public void init(ICommonActionExtensionSite actionSite) {
			super.init(actionSite);
			ICommonViewerSite site = actionSite.getViewSite();
			if (site instanceof ICommonViewerWorkbenchSite) {
				StructuredViewer viewer = actionSite.getStructuredViewer();
				if (viewer instanceof CommonViewer) {
					CommonViewer commonViewer = (CommonViewer) viewer;
					commonViewer.addTreeListener(new TreeExpansionListener());
				}
			}
		}

	
	}
package com.abapblog.adt.extension.commands;

import org.eclipse.core.commands.AbstractHandler;


import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.abapblog.adt.extension.dialogs.LanguageDialog;
import com.sap.adt.destinations.model.IDestinationData;
import com.sap.adt.destinations.model.IDestinationDataWritable;
import com.sap.adt.project.IAdtCoreProject;
import com.sap.adt.tools.core.project.IAbapProject;

public class ChangeLogonLanguageHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {

		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
			Object firstElement = selection.getFirstElement();
			if (firstElement instanceof IAdaptable) {

				IProject project = (IProject) ((IAdaptable) firstElement).getAdapter(IProject.class);
				IAdtCoreProject AdtProject = project.getAdapter(IAdtCoreProject.class);

				IDestinationData DestinationData = AdtProject.getDestinationData();
				IDestinationDataWritable DestinationDataWritable = DestinationData.getWritable();
				LanguageDialog LanguageDialog = new LanguageDialog(null);
				LanguageDialog.create();
				if (LanguageDialog.open() == Window.OK) {
					DestinationDataWritable.setLanguage(LanguageDialog.getLanguage());
					IDestinationData newDestinationData = DestinationDataWritable.getReadOnlyClone();
					AdtProject.setDestinationData(newDestinationData);

					try {
						project.close(null);
						project.open(null);
					       } catch (Exception e) {
						e.printStackTrace();
										}
				}
			}
		}
		return null;
	}

}

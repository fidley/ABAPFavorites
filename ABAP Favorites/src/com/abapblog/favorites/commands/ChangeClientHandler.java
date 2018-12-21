package com.abapblog.favorites.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.abapblog.favorites.common.Common;
import com.abapblog.favorites.common.FolderDialog;
import com.sap.adt.destinations.model.IDestinationData;
import com.sap.adt.destinations.model.IDestinationDataWritable;
import com.sap.adt.tools.core.project.IAbapProject;

public class ChangeClientHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {


		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
			Object firstElement = selection.getFirstElement();
			if (firstElement instanceof IAdaptable) {

				IProject project = (IProject) ((IAdaptable) firstElement).getAdapter(IProject.class);
				IAbapProject ABAPProject = project.getAdapter(IAbapProject.class);

				IDestinationData DestinationData = ABAPProject.getDestinationData();
				IDestinationDataWritable DestinationDataWritable = DestinationData.getWritable();
				ChangeClientDialog ClientDialog = new ChangeClientDialog(null);
				ClientDialog.create();
				if (ClientDialog.open() == Window.OK) {
					DestinationDataWritable.setClient(ClientDialog.getClient());
					IDestinationData newDestinationData = DestinationDataWritable.getReadOnlyClone();
					ABAPProject.setDestinationData(newDestinationData);

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

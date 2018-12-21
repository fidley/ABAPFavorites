package com.abapblog.favorites.commands;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.sap.adt.destinations.model.IDestinationData;
import com.sap.adt.destinations.model.IDestinationDataWritable;
import com.sap.adt.project.AdtCoreProjectServiceFactory;
import com.sap.adt.project.IAdtCoreProjectService;
import com.sap.adt.tools.core.project.AdtProjectServiceFactory;
import com.sap.adt.tools.core.project.IAbapProject;
import com.sap.adt.tools.core.project.IAbapProjectService;
public class ChangeUserHandler extends AbstractHandler {

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
				ChangeUserDialog UserDialog = new ChangeUserDialog(null);
				UserDialog.create();
				if (UserDialog.open() == Window.OK) {
					DestinationDataWritable.setUser(UserDialog.getUser());
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

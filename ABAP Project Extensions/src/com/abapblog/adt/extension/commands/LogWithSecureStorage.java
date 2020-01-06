package com.abapblog.adt.extension.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.abablog.adt.extension.passwords.ILogonService;
import com.abablog.adt.extension.passwords.LogonServiceFactory;
import com.abablog.adt.extension.passwords.LogonWithJob;


public class LogWithSecureStorage extends AbstractHandler {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
    	

    	IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
			Object firstElement = selection.getFirstElement();
			if (firstElement instanceof IAdaptable) {

				IProject project = (IProject) ((IAdaptable) firstElement).getAdapter(IProject.class);
				LogonWithJob logonWithJob = new LogonWithJob();
				logonWithJob.logon(project);
			}
		}

        return null;
    }

	

}
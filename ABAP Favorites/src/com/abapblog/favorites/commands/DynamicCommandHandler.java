package com.abapblog.favorites.commands;

import java.util.HashMap;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.abapblog.favorites.Activator;
import com.abapblog.favorites.common.Common;
import com.abapblog.favorites.common.CommonTypes.TypeOfEntry;
import com.abapblog.favorites.preferences.PreferenceConstants;
import com.abapblog.favorites.superview.AdtObjectHandler;
import com.abapblog.favorites.tree.TreeObject;
import com.abapblog.favorites.tree.TreeParent;
import com.sap.adt.project.IAdtCoreProject;
import com.sap.adt.project.ui.util.ProjectUtil;
import com.sap.adt.tools.core.ui.dialogs.AbapProjectSelectionDialog;

public class DynamicCommandHandler implements IHandler {
	public static final String COM_ABAPBLOG_FAVORITES_COMMANDS_COMMAND9 = "com.abapblog.favorites.commands.command9";
	public static final String COM_ABAPBLOG_FAVORITES_COMMANDS_COMMAND8 = "com.abapblog.favorites.commands.command8";
	public static final String COM_ABAPBLOG_FAVORITES_COMMANDS_COMMAND7 = "com.abapblog.favorites.commands.command7";
	public static final String COM_ABAPBLOG_FAVORITES_COMMANDS_COMMAND6 = "com.abapblog.favorites.commands.command6";
	public static final String COM_ABAPBLOG_FAVORITES_COMMANDS_COMMAND5 = "com.abapblog.favorites.commands.command5";
	public static final String COM_ABAPBLOG_FAVORITES_COMMANDS_COMMAND4 = "com.abapblog.favorites.commands.command4";
	public static final String COM_ABAPBLOG_FAVORITES_COMMANDS_COMMAND3 = "com.abapblog.favorites.commands.command3";
	public static final String COM_ABAPBLOG_FAVORITES_COMMANDS_COMMAND2 = "com.abapblog.favorites.commands.command2";
	public static final String COM_ABAPBLOG_FAVORITES_COMMANDS_COMMAND1 = "com.abapblog.favorites.commands.command1";
	public static final String COM_ABAPBLOG_FAVORITES_COMMANDS_COMMAND0 = "com.abapblog.favorites.commands.command0";
	public static HashMap<String, TreeObject> commandsLink = new HashMap<>();

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Boolean callSelectionDialog = Boolean
				.parseBoolean(event.getParameter("com.abapblog.favorites.commands.parameter.callSelectionDialog"));
		TreeObject treeObject = commandsLink.get(event.getCommand().getId());
		if (treeObject == null)
			return null;

		final boolean enableEclipseNavigation = Activator.getDefault().getPreferenceStore()
				.getBoolean(PreferenceConstants.P_NAVIGATE_TO_ECLIPSE_FOR_SUPPORTED_DEV_OBJECTS);
		IProject project = getProject(treeObject, callSelectionDialog);
		if (project == null & !treeObject.getType().equals(TypeOfEntry.URL))
			return null;
		AdtObjectHandler.executeTreeObject(treeObject, project, enableEclipseNavigation, true);

		return false;
	}

	private IProject getProject(TreeObject treeObject, Boolean callSelectionDialog) {
		if (treeObject instanceof TreeParent)
			return null;
		if (treeObject.getType().equals(TypeOfEntry.URL))
			return null;

		if (callSelectionDialog) {
			return getProjectPopup();
		}

		if (treeObject.getParent().getIndependent()) {
			final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			final IWorkbenchWindow window = page.getWorkbenchWindow();
			final ISelection selection = window.getSelectionService().getSelection();
			IProject project = ProjectUtil.getActiveAdtCoreProject(selection, null, null,
					IAdtCoreProject.ABAP_PROJECT_NATURE);
			if (project == null) {
				project = getProjectPopup();
			}
			return project;
		} else {
			return Common.getProjectByName(treeObject.getParent().getProject());
		}
	}

	private IProject getProjectPopup() {
		return AbapProjectSelectionDialog.open(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), null);
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isHandled() {
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

}

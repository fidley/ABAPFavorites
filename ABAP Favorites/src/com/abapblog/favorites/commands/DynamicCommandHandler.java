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
import com.abapblog.favorites.preferences.PreferenceConstants;
import com.abapblog.favorites.superview.AdtObjectHandler;
import com.abapblog.favorites.tree.TreeObject;
import com.abapblog.favorites.tree.TreeParent;
import com.sap.adt.project.IAdtCoreProject;
import com.sap.adt.project.ui.util.ProjectUtil;

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

		TreeObject treeObject = commandsLink.get(event.getCommand().getId());
		if (treeObject == null)
			return null;

		final boolean enableEclipseNavigation = Activator.getDefault().getPreferenceStore()
				.getBoolean(PreferenceConstants.P_NAVIGATE_TO_ECLIPSE_FOR_SUPPORTED_DEV_OBJECTS);

		AdtObjectHandler.executeTreeObject(treeObject, getProject(treeObject), enableEclipseNavigation, true);

		return false;
	}

	private IProject getProject(TreeObject treeObject) {
		if (treeObject instanceof TreeParent)
			return null;

		if (treeObject.getParent().getProjectIndependent()) {
			final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			final IWorkbenchWindow window = page.getWorkbenchWindow();
			final ISelection selection = window.getSelectionService().getSelection();
			return ProjectUtil.getActiveAdtCoreProject(selection, null, null, IAdtCoreProject.ABAP_PROJECT_NATURE);
		} else {
			return Common.getProjectByName(treeObject.getParent().getProject());
		}
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

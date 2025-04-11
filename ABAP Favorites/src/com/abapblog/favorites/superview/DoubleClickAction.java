package com.abapblog.favorites.superview;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.abapblog.favorites.Activator;
import com.abapblog.favorites.common.Common;
import com.abapblog.favorites.common.CommonTypes.TypeOfEntry;
import com.abapblog.favorites.preferences.PreferenceConstants;
import com.abapblog.favorites.tree.TreeObject;
import com.abapblog.favorites.tree.TreeParent;
import com.sap.adt.destinations.ui.logon.AdtLogonServiceUIFactory;
import com.sap.adt.destinations.ui.logon.IAdtLogonServiceUI;
import com.sap.adt.project.IAdtCoreProject;
import com.sap.adt.project.ui.util.ProjectUtil;
import com.sap.adt.tools.core.ui.dialogs.AbapProjectSelectionDialog;

/**
 * Open action for the Favorites Views.<br>
 * It get's triggered if a Mouse double click or an ENTER Key event is detected
 * on the Favorites Tree
 *
 * @author stockbal
 *
 */
public class DoubleClickAction extends Action implements ITreeNodeAction {

	private final IPreferenceStore prefStore;

	/**
	 * Creates new double click action for the Favorites TreeViewer
	 *
	 * @param superView the favorites view
	 */
	DoubleClickAction(final Superview superView) {
		this.prefStore = Activator.getDefault().getPreferenceStore();
	}

	@Override
	public void execute(final boolean isControlPressed, final ISelection selection) {
		if (selection == null || selection.isEmpty()) {
			return;
		}
		final DoubleClickBehavior doubleClickBehavior = getDoubleClickBehaviour(isControlPressed);
		final List<TreeObjectProxy> selectedObjects = getSelectedObjects(selection, doubleClickBehavior);
		if (selectedObjects == null || selectedObjects.isEmpty()) {
			return;
		}

		final List<IProject> distinctProjects = selectedObjects.stream()
				.filter(treeObj -> treeObj.needsProjectForExecution()).map(treeObj -> treeObj.getProject()).distinct()
				.collect(Collectors.toList());
		if (distinctProjects != null && !distinctProjects.isEmpty()) {
			final IAdtLogonServiceUI logonService = AdtLogonServiceUIFactory.createLogonServiceUI();
			for (final IProject project : distinctProjects) {
				if (!logonService.ensureLoggedOn(project).isOK()) {
					return;
				}
			}
		}

		final boolean enableEclipseNavigation = this.prefStore
				.getBoolean(PreferenceConstants.P_NAVIGATE_TO_ECLIPSE_FOR_SUPPORTED_DEV_OBJECTS);
		for (final TreeObjectProxy object : selectedObjects) {
			AdtObjectHandler.executeTreeObject(object.getTreeObject(), object.getProject(), enableEclipseNavigation,
					false);
		}
	}

	private List<TreeObjectProxy> getSelectedObjects(final ISelection selection,
			final DoubleClickBehavior doubleClickBehavior) {
		if (selection == null || selection.isEmpty()) {
			return null;
		}

		final List<TreeObjectProxy> treeObjects = new ArrayList<>();
		final Iterator<?> selIter = ((IStructuredSelection) selection).iterator();

		while (selIter.hasNext()) {
			final Object selObj = selIter.next();
			if (!(selObj instanceof TreeObject)) {
				continue;
			}
			final TreeObject treeObj = (TreeObject) selObj;
			switch (treeObj.getType()) {
			case Folder:
			case FolderDO:
				continue;
			default:
				break;
			}
			final TreeObjectProxy treeObjProxy = new TreeObjectProxy(treeObj, null);

			if (treeObjProxy.needsProjectForExecution()) {
				IProject projectForAll = null;
				IProject currentProject = null;
				if (doubleClickBehavior == DoubleClickBehavior.OPEN_VIA_PROJECT_DIALOG) {
					projectForAll = getProjectFromProjectChooserDialog();
					if (projectForAll == null) {
						return null;
					}
				} else {
					currentProject = getCurrentAbapProject();
					if (currentProject == null) {
						currentProject = getProjectFromProjectChooserDialog();
					}
				}
				if (projectForAll != null) {
					treeObjProxy.setProject(projectForAll);
				} else {
					final TreeParent nodeParent = treeObj.getParent();
					if (nodeParent.getProjectDependent()) {
						treeObjProxy.setProject(Common.getProjectByName(nodeParent.getProject()));
					} else {
						treeObjProxy.setProject(currentProject);
					}
				}
			}
			if (treeObjProxy.canBeExecuted()) {
				treeObjects.add(treeObjProxy);
			}
		}
		return treeObjects;
	}

	private IProject getCurrentAbapProject() {
		final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		final IWorkbenchWindow window = page.getWorkbenchWindow();
		final ISelection selection = window.getSelectionService().getSelection();
		return ProjectUtil.getActiveAdtCoreProject(selection, null, null, IAdtCoreProject.ABAP_PROJECT_NATURE);
	}

	/*
	 * Retrieves the current double click behavior
	 */
	private DoubleClickBehavior getDoubleClickBehaviour(final boolean isControlPressed) {
		DoubleClickBehavior doubleClickBehavior = null;
		String storedDoubleClickBehavior = null;
		if (isControlPressed) {
			storedDoubleClickBehavior = this.prefStore.getString(PreferenceConstants.P_DOUBLE_CLICK_CTRL_BEHAVIOR);
		} else {
			storedDoubleClickBehavior = this.prefStore.getString(PreferenceConstants.P_DOUBLE_CLICK_BEHAVIOR);
		}
		try {
			doubleClickBehavior = DoubleClickBehavior.valueOf(storedDoubleClickBehavior);
		} catch (final IllegalArgumentException exc) {
			doubleClickBehavior = DoubleClickBehavior.OPEN_IN_CURRENT_PROJECT;
		}
		return doubleClickBehavior;
	}

	/*
	 * Returns project from ABAP project selection dialog
	 */
	private IProject getProjectFromProjectChooserDialog() {
		return AbapProjectSelectionDialog.open(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), null);
	}

	private class TreeObjectProxy {
		private final TreeObject treeObject;
		private IProject project;

		public TreeObjectProxy(final TreeObject treeObject, final IProject project) {
			this.treeObject = treeObject;
			this.project = project;
		}

		public TreeObject getTreeObject() {
			return this.treeObject;
		}

		public boolean needsProjectForExecution() {
			return this.treeObject.getType() != TypeOfEntry.URL;
		}

		public boolean canBeExecuted() {
			if (needsProjectForExecution()) {
				return this.project != null;
			}
			return true;
		}

		public IProject getProject() {
			return this.project;
		}

		public void setProject(final IProject project) {
			this.project = project;
		}

	}
}

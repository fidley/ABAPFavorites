package com.abapblog.favorites.superview;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.abapblog.favorites.Activator;
import com.abapblog.favorites.common.Common;
import com.abapblog.favorites.common.CommonTypes.TypeOfEntry;
import com.abapblog.favorites.preferences.PreferenceConstants;
import com.abapblog.favorites.tree.TreeObject;
import com.abapblog.favorites.tree.TreeParent;
import com.sap.adt.destinations.ui.logon.AdtLogonServiceUIFactory;
import com.sap.adt.ris.search.AdtRisQuickSearchFactory;
import com.sap.adt.ris.search.RisQuickSearchNotSupportedException;
import com.sap.adt.sapgui.ui.editors.AdtSapGuiEditorUtilityFactory;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;
import com.sap.adt.tools.core.ui.navigation.AdtNavigationServiceFactory;
import com.sap.adt.tools.core.wbtyperegistry.WorkbenchAction;

/**
 * Handles the execution (open or run) for an ADT object
 *
 * @author stockbal
 *
 */
public class AdtObjectHandler {

	/**
	 * Executes the ADT object behind the given tree object
	 *
	 * @param nodeObject      the tree object to be executed
	 * @param project         the target ABAP project
	 * @param checkLogonState if <code>true</code> the log on state of the given
	 *                        project will be ensured
	 */
	public static void executeTreeObject(final TreeObject nodeObject, final IProject project,
			final boolean checkLogonState) {
		executeTreeObject(nodeObject, project, Activator.getDefault().getPreferenceStore()
				.getBoolean(PreferenceConstants.P_NAVIGATE_TO_ECLIPSE_FOR_SUPPORTED_DEV_OBJECTS), checkLogonState);
	}

	/**
	 * Executes the ADT object behind the given tree object
	 *
	 * @param nodeObject        the tree object to be executed
	 * @param project           the target ABAP project
	 * @param navigateToEclipse if <code>true</code> eclipse navigation will be
	 *                          enabled for supported ADT objects
	 * @param checkLogonState   if <code>true</code> the log on state of the given
	 *                          project will be ensured, otherwise it is the
	 *                          responsibility of the caller
	 */
	public static void executeTreeObject(final TreeObject nodeObject, final IProject project,
			final boolean navigateToEclipse, final boolean checkLogonState) {
		try {
			if (checkLogonState) {
				if (!AdtLogonServiceUIFactory.createLogonServiceUI().ensureLoggedOn(project).isOK()) {
					return;
				}
			}

			final TreeParent nodeParent = nodeObject.getParent();
			switch (nodeObject.getType()) {
			case Folder:
			case FolderDO:
				break;
			case Transaction:
				AdtSapGuiEditorUtilityFactory.createSapGuiEditorUtility().openEditorAndStartTransaction(project,
						nodeObject.toString(), navigateToEclipse);
				break;
			case Program:
				if (nodeParent.getDevObjProject() == false) {
					AdtObjectHandler.runObject(project, nodeObject.getName(), nodeObject.getType());
					break;
				} else {
					AdtObjectHandler.openObject(project, nodeObject.getName(), nodeObject.getType());
					break;
				}
			case ADTLink:
				AdtObjectHandler.openAdtLink(project, nodeObject.getTechnicalName());
				break;
			case URL:
				try {
					PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser()
							.openURL(new URL(nodeObject.getTechnicalName()));
				} catch (PartInitException | MalformedURLException e) {
					e.printStackTrace();
				}
			default:
				if (nodeParent.getDevObjProject() == false) {
					AdtObjectHandler.runObject(project, nodeObject.getName(), nodeObject.getType());
					break;
				} else {
					AdtObjectHandler.openObject(project, nodeObject.getName(), nodeObject.getType());
					break;
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Runs the ADT object with the given name in the given project.
	 *
	 * @param project             the target ABAP Project
	 * @param technicalObjectName the technical name of the object
	 * @param type                the type of the object
	 */
	public static void runObject(final IProject project, final String technicalObjectName, final TypeOfEntry type) {
		final IAdtObjectReference adtObjectRef = lookupObjectReference(project, technicalObjectName, type);
		if (adtObjectRef == null) {
			return;
		}
		AdtSapGuiEditorUtilityFactory.createSapGuiEditorUtility().openEditorForObject(project, adtObjectRef,
				Activator.getDefault().getPreferenceStore()
						.getBoolean(PreferenceConstants.P_NAVIGATE_TO_ECLIPSE_FOR_SUPPORTED_DEV_OBJECTS),
				WorkbenchAction.EXECUTE.toString(), null, Collections.<String, String>emptyMap());
	}

	/**
	 * Opens the ADT object with the given name in the given project.
	 *
	 * @param project             the target ABAP Project
	 * @param technicalObjectName the technical name of the object
	 * @param type                the type of the object
	 */
	public static void openObject(final IProject project, final String technicalObjectName, final TypeOfEntry type) {
		final IAdtObjectReference adtObjectRef = lookupObjectReference(project, technicalObjectName, type);
		if (adtObjectRef == null) {
			return;
		}
		AdtNavigationServiceFactory.createNavigationService().navigate(project, adtObjectRef, true);
	}

	/**
	 * Opens the given ADT Link in the given project
	 *
	 * @param project the target ABAP Project
	 * @param adtLink the ADT Link to be executed
	 */
	public static void openAdtLink(final IProject project, String adtLink) {
		adtLink = adtLink.replace("(?<=\'/\'/)(.*?)(?=\'/)", project.getName().toString());
		AdtNavigationServiceFactory.createNavigationService().navigateWithExternalLink(adtLink, project);
		return;
	}

	/**
	 * Searches for an ADT object references in the given project and with the given
	 * name and type.
	 *
	 * @param project             the target ABAP Project for the search
	 * @param technicalObjectName the technical name of the object
	 * @param type                the type of the object
	 * @return the found ADT Object reference or <code>null</code> if none could be
	 *         found
	 */
	public static IAdtObjectReference lookupObjectReference(final IProject project, final String technicalObjectName,
			final TypeOfEntry type) {
		if (type.equals(TypeOfEntry.URL) || type.equals(TypeOfEntry.ADTLink))
			return null;
		String programName = "";
		List<IAdtObjectReference> res = null;
		try {
			res = AdtRisQuickSearchFactory.createQuickSearch(project, new NullProgressMonitor())
					.execute(technicalObjectName, 10);
		} catch (OperationCanceledException | RisQuickSearchNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
		for (final IAdtObjectReference ref : res) {
			if (Common.isSAPTypeHandled(ref.getType(), type)) {
				final Pattern regexPatern = Pattern.compile("^\\S*");
				final Matcher regexMatch = regexPatern.matcher(ref.getName());
				while (regexMatch.find()) {
					programName = regexMatch.group(0);
				}
				if (programName.equalsIgnoreCase(technicalObjectName)) {
					return ref;
				}
			}
		}
		return null;
	}
}

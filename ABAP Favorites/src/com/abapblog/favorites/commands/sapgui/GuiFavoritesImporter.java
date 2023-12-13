package com.abapblog.favorites.commands.sapgui;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import com.sap.adt.destinations.logon.AdtLogonServiceFactory;
import com.sap.adt.project.AdtCoreProjectServiceFactory;
import com.sap.adt.project.IAdtCoreProject;
import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;

public class GuiFavoritesImporter {

	public FavoritesFromGUIRaw getFavoritesFromBackend(IProject project) {
		try {

			if (Boolean.FALSE.equals(AdtLogonServiceFactory.createLogonService().isLoggedOn(project.getName()))) {
				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error",
						"Please logon to the project first!");
				return null;
			}

			String destinationId = AdtCoreProjectServiceFactory.createCoreProjectService().getDestinationId(project);
			JCoDestination destination = JCoDestinationManager.getDestination(destinationId);
			JCoFunction function = destination.getRepository().getFunction("BAPI_USER_WP_PERS_DATA_READ");
			if (function == null) {
				// Throw error
			}

			String sapUser = getSAPUser(project);

			function.getImportParameterList().getField("ID").setValue(sapUser);

			try {
				function.execute(destination);
				JCoTable favoritesMain = function.getTableParameterList().getTable("FAVO_TABC");
				JCoTable favoritesURL = function.getTableParameterList().getTable("FAVO_TABI");
				return new FavoritesFromGUIRaw(favoritesMain, favoritesURL, project.getName());
			} catch (AbapException e) {
				System.out.println(e.toString());
				return null;
			}
		} catch (

		JCoException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String getSAPUser(IProject project) {
		IAdtCoreProject AdtProject = project.getAdapter(IAdtCoreProject.class);
		return AdtProject.getDestinationData().getUser();
	}

}

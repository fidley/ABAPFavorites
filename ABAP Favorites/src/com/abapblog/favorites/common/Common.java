package com.abapblog.favorites.common;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.abapblog.favorites.common.CommonTypes.TypeOfEntry;
import com.abapblog.favorites.common.CommonTypes.TypeOfObject;
import com.abapblog.favorites.superview.IFavorites;
import com.sap.adt.logging.AdtLogging;
import com.sap.adt.project.IAdtCoreProject;
import com.sap.adt.project.ui.util.ProjectUtil;
import com.sap.adt.ris.search.AdtRisQuickSearchFactory;
import com.sap.adt.ris.search.RisQuickSearchNotSupportedException;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;

@SuppressWarnings("restriction")
public class Common {

	private static final String ADT_PROJECT_SAP_BW_NATURE = "com.sap.bw.nature";

	public static IFavorites Favorite;
	public static IFavorites FavoriteDO;

	public Common() {
	}

	public static TypeOfEntry getTypeOfEntryFromSAPType(String sapType) {
		final String amdpType = TypeOfObject.AMDPType.toString();
		final String cdsviewType = TypeOfObject.CDSViewType.toString();
		final String classType = TypeOfObject.classType.toString();
		final String fgType = TypeOfObject.FunctionGroupType.toString();
		final String fgIncludeTYpe = TypeOfObject.FunctionGroupIncludeType.toString();
		final String fmType = TypeOfObject.FunctionModuleType.toString();
		final String icludeType = TypeOfObject.includeType.toString();
		final String interfaceType = TypeOfObject.interfaceType.toString();
		final String messageClassType = TypeOfObject.MessageClassType.toString();
		final String searchHelpType = TypeOfObject.SearchHelpType.toString();
		final String programType = TypeOfObject.programType.toString();
		final String tableType = TypeOfObject.TableType.toString();
		final String transactionType = TypeOfObject.TransactionType.toString();
		final String viewType = TypeOfObject.ViewType.toString();

		if (sapType.equals(amdpType))
			return TypeOfEntry.AMDP;
		if (sapType.equals(cdsviewType))
			return TypeOfEntry.CDSView;
		if (sapType.equals(classType))
			return TypeOfEntry.Class;
		if (sapType.equals(fgType))
			return TypeOfEntry.FunctionGroup;
		if (sapType.equals(fgIncludeTYpe))
			return TypeOfEntry.Program;
		if (sapType.equals(fmType))
			return TypeOfEntry.FunctionModule;
		if (sapType.equals(icludeType))
			return TypeOfEntry.Include;
		if (sapType.equals(interfaceType))
			return TypeOfEntry.Interface;
		if (sapType.equals(messageClassType))
			return TypeOfEntry.MessageClass;
		if (sapType.equals(searchHelpType))
			return TypeOfEntry.SearchHelp;
		if (sapType.equals(programType))
			return TypeOfEntry.Program;
		if (sapType.equals(tableType))
			return TypeOfEntry.Table;
		if (sapType.equals(viewType))
			return TypeOfEntry.View;
		if (sapType.equals(transactionType))
			return TypeOfEntry.Transaction;

		return null;

	}

	public static String getProjectName() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IWorkbenchWindow window = page.getWorkbenchWindow();
		ISelection ADTselection = window.getSelectionService().getSelection();
		IProject project = ProjectUtil.getActiveAdtCoreProject(ADTselection, null, null,
				IAdtCoreProject.ABAP_PROJECT_NATURE);
		if (project != null) {
			return project.getName();
		} else {
			return "";
		}
	}

	public static IProject getProjectByName(String projectName) {
		try {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			return project;
		} catch (Exception e) {
			return null;
		}

	}

	public static List<IProject> getABAPProjects() {
		List<IProject> projectList = new LinkedList<IProject>();

		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projects = workspaceRoot.getProjects();
		for (int i = 0; i < projects.length; i++) {
			IProject project = projects[i];
			try {
				if (project.hasNature(IAdtCoreProject.ABAP_PROJECT_NATURE)) {
					projectList.add(project);
				}
			} catch (CoreException ce) {
				ce.printStackTrace();
			}
		}

		return projectList;
	}

	public static List<IProject> getBWModelProjects() {
		List<IProject> projectList = new LinkedList<IProject>();

		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projects = workspaceRoot.getProjects();
		for (int i = 0; i < projects.length; i++) {
			IProject project = projects[i];
			try {
				if (project.hasNature(ADT_PROJECT_SAP_BW_NATURE)) {
					projectList.add(project);
				}
			} catch (CoreException ce) {
				ce.printStackTrace();
			}
		}
		return projectList;
	}

	public static String getObjectName(TypeOfEntry ObjectType) {

		switch (ObjectType) {
		case Class:
			return "Class";
		case Include:
			return "Include";
		case FunctionGroup:
			return "Function group";
		case FunctionModule:
			return "Function module";
		case Interface:
			return "Interface";
		case Program:
			return "Program";
		case Transaction:
			return "Transaction";
		case View:
			return "View";
		case Table:
			return "Table";
		case MessageClass:
			return "Message class";
		case SearchHelp:
			return "Search Help";
		case ADTLink:
			return "ADT Link";
		case CDSView:
			return "CDS View";
		case Package:
			return "Package";
		default:
			return "object";
		}

	}

	public static boolean isSAPTypeHandled(String sapType, TypeOfEntry type) {

		if (sapType.equals(TypeOfObject.classType.toString()) && type == TypeOfEntry.Class)
			return true;

		if (sapType.equals(TypeOfObject.interfaceType.toString()) && type == TypeOfEntry.Interface)
			return true;

		if (sapType.equals(TypeOfObject.FunctionGroupIncludeType.toString()) && type == TypeOfEntry.Program)
			return true;

		if (sapType.equals(TypeOfObject.FunctionGroupType.toString()) && type == TypeOfEntry.FunctionGroup)
			return true;

		if (sapType.equals(TypeOfObject.FunctionModuleType.toString()) && type == TypeOfEntry.FunctionModule)
			return true;

		// if (sapType.equals(TypeOfObject.FunctionModuleRFCType.toString()) && type ==
		// TypeOfEntry.FunctionModule) {
		// return true;
		// }

		if (sapType.equals(TypeOfObject.includeType.toString()) && type == TypeOfEntry.Include)
			return true;

		if (sapType.equals(TypeOfObject.programType.toString()) && type == TypeOfEntry.Program)
			return true;

		if (sapType.equals(TypeOfObject.ViewType.toString()) && type == TypeOfEntry.View)
			return true;

		if (sapType.equals(TypeOfObject.TableType.toString()) && type == TypeOfEntry.Table)
			return true;

		if (sapType.equals(TypeOfObject.MessageClassType.toString()) && type == TypeOfEntry.MessageClass)
			return true;

		if (sapType.equals(TypeOfObject.SearchHelpType.toString()) && type == TypeOfEntry.SearchHelp)
			return true;

		if (sapType.equals(TypeOfObject.CDSViewType.toString()) && type == TypeOfEntry.CDSView)
			return true;

		if (sapType.equals(TypeOfObject.AMDPType.toString()) && type == TypeOfEntry.AMDP)
			return true;
		if (sapType.equals(TypeOfObject.PackageType.toString()) && type == TypeOfEntry.Package)
			return true;
		return false;
	}

	@SuppressWarnings("restriction")
	public static String getObjectDescription(IProject project, String objectName, TypeOfEntry type) {
		String Name = "";

		try {

			List<IAdtObjectReference> res = AdtRisQuickSearchFactory
					.createQuickSearch(project, new NullProgressMonitor()).execute(objectName, 10, false, false, null);
			for (IAdtObjectReference ref : res) {
				if (Common.isSAPTypeHandled(ref.getType(), type)) {

					Pattern regexPatern = Pattern.compile("^\\S*");
					Matcher regexMatch = regexPatern.matcher(ref.getName());
					while (regexMatch.find()) {
						Name = regexMatch.group(0);
					}
					if (Name.equalsIgnoreCase(objectName)) {
						return ref.getDescription();
					}
				}
			}
		} catch (OperationCanceledException | RisQuickSearchNotSupportedException e) {
			AdtLogging.getLogger(AdtRisQuickSearchFactory.class).error(e);
		}
		return "";
	}
}

package com.abapblog.favorites.superview;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import com.abapblog.favorites.common.AFIcons;
import com.abapblog.favorites.common.Common;
import com.abapblog.favorites.common.CommonTypes.TypeOfEntry;
import com.abapblog.favorites.common.CommonTypes.TypeOfXMLNode;
import com.abapblog.favorites.dialog.FolderDialog;
import com.abapblog.favorites.dialog.NameDialog;
import com.abapblog.favorites.dialog.URLDialog;
import com.abapblog.favorites.tree.TreeObject;
import com.abapblog.favorites.tree.TreeParent;
import com.abapblog.favorites.xml.XMLhandler;
import com.sap.adt.destinations.logon.AdtLogonServiceFactory;
import com.sap.adt.destinations.logon.IAdtLogonService;
import com.sap.adt.destinations.model.IDestinationData;
import com.sap.adt.destinations.model.IDestinationDataWritable;
import com.sap.adt.destinations.ui.logon.AdtLogonServiceUIFactory;
import com.sap.adt.destinations.ui.logon.IAdtLogonServiceUI;
import com.sap.adt.project.IAdtCoreProject;
import com.sap.adt.project.ui.util.ProjectUtil;
import com.sap.adt.ris.search.AdtRisQuickSearchFactory;
import com.sap.adt.ris.search.RisQuickSearchNotSupportedException;
import com.sap.adt.sapgui.ui.editors.AdtSapGuiEditorUtilityFactory;
import com.sap.adt.sapgui.ui.editors.IAdtSapGuiEditorUtility;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;
import com.sap.adt.tools.core.project.IAbapProject;
import com.sap.adt.tools.core.ui.navigation.AdtNavigationServiceFactory;
import com.sap.adt.tools.core.wbtyperegistry.WorkbenchAction;

public class Actions {

	public Action actAddADTLink;
	public Action actAddAMDP;
	public Action actAddCDS;
	public Action actAddClass;
	public Action actAddFolder;
	public Action actAddFunctionGroup;
	public Action actAddFunctionModule;
	public Action actAddInterface;
	public Action actAddMessageClass;
	public Action actAddProgram;
	public Action actAddRootFolder;
	public Action actAddSearchHelp;
	public Action actAddTable;
	public Action actAddTransaction;
	public Action actAddURL;
	public Action actAddView;
	public Action actDelete;
	public Action actDelFolder;
	public Action actEdit;
	public Action actExportFavorites;
	public Action actImportFavorites;
	public Action actDoubleClick;

	public void makeActions(Superview superview) {
		AFIcons AFIcon = new AFIcons();
		createAddFolderAction(superview);
		createAddRootFolderAction(superview);
		createAddTransactionAction(superview.viewer, AFIcon);
		createAddProgramAction(superview.viewer, AFIcon);
		createAddViewAction(superview.viewer, AFIcon);
		createAddTableAction(superview.viewer, AFIcon);
		createAddMessageClassAction(superview.viewer, AFIcon);
		createAddSeachHelpAction(superview.viewer, AFIcon);
		createAddUrlAction(superview.viewer, AFIcon);
		createAddAdtLinkAction(superview.viewer, AFIcon);
		createAddCDSViewAction(superview.viewer, AFIcon);
		createAddAMDPAction(superview.viewer, AFIcon);
		createAddClassAction(superview.viewer, AFIcon);
		createAddIterfaceAction(superview.viewer, AFIcon);
		createAddFGAction(superview.viewer, AFIcon);
		createAddFMAction(superview.viewer, AFIcon);
		createDelFolderAction(superview.viewer);
		createEditAction(superview.viewer, AFIcon);
		createDeleteAction(superview.viewer);
		createDoubleClickActions(superview);
		createExportFavoritesAction(superview.viewer);
		createImportFavoritesAction(superview.viewer);
	}

	private void createAddFolderAction(Superview superview) {
		actAddFolder = new Action() {
			@Override
			public void run() {
				Boolean FolderDO = false;
				switch (superview.FolderNode) {
				case folderNode:
					FolderDO = false;
					break;
				case folderDONode:
					FolderDO = true;
					break;
				}
				;
				FolderDialog FolderDialog = new FolderDialog(superview.viewer.getControl().getShell(), FolderDO);
				FolderDialog.create();
				if (FolderDialog.open() == Window.OK) {

					if (superview.viewer.getSelection() instanceof IStructuredSelection) {
						IStructuredSelection selection = (IStructuredSelection) superview.viewer.getSelection();

						TreeObject Folder = (TreeObject) selection.getFirstElement();

						if (Folder instanceof TreeParent) {
							XMLhandler.addFolderToXML(FolderDialog.getName(), FolderDialog.getDescription(),
									FolderDialog.getLongDescription(), FolderDialog.getPrjInd(),
									Common.getProjectName(), FolderDialog.getDevObjectFolder(),
									((TreeParent) Folder).getFolderID(), ((TreeParent) Folder).getTypeOfFolder());
							superview.refreshViewer(superview.viewer);
						}

					}

				}

			}
		};
		actAddFolder.setText("Add New Folder");
		actAddFolder.setToolTipText("Add New Folder");
		actAddFolder.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ADD));
	}

	private void createAddRootFolderAction(Superview superview) {
		actAddRootFolder = new Action() {
			@Override
			public void run() {
				Boolean FolderDO = false;
				switch (superview.FolderNode) {
				case folderNode:
					FolderDO = false;
					break;
				case folderDONode:
					FolderDO = true;
					break;
				}
				;
				FolderDialog FolderDialog = new FolderDialog(superview.viewer.getControl().getShell(), FolderDO);
				FolderDialog.create();
				if (FolderDialog.open() == Window.OK) {

					XMLhandler.addFolderToXML(FolderDialog.getName(), FolderDialog.getDescription(),
							FolderDialog.getLongDescription(), FolderDialog.getPrjInd(), Common.getProjectName(),
							FolderDialog.getDevObjectFolder(), "", superview.FolderNode);
					superview.refreshViewer(superview.viewer);
				}

			}

		};

		actAddRootFolder.setText("Add New Folder");
		actAddRootFolder.setToolTipText("Add New Folder");
		actAddRootFolder.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ADD));
	}

	private void createImportFavoritesAction(TreeViewer viewer) {
		actImportFavorites = new Action() {
			@Override
			public void run() {
				importFavorites(viewer);
			}

		};
		actImportFavorites.setText("Import Favorites");
		actImportFavorites.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
	}

	private void createExportFavoritesAction(TreeViewer viewer) {
		actExportFavorites = new Action() {
			@Override
			public void run() {
				exportFavorites(viewer);
			}

		};
		actExportFavorites.setText("Export Favorites");
		actExportFavorites.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_SAVEAS_EDIT));
	}

	private void createDoubleClickActions(Superview superview) {
		actDoubleClick = new Action() {
			@Override
			public void run() {

				ISelection selection = superview.viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();

				if (Superview.isHideOfDepProject() == false) {
					if (obj instanceof TreeObject) {
						TreeObject nodeObject = ((TreeObject) obj);
						TypeOfEntry NodeType = nodeObject.getType();
						TreeParent nodeParent = nodeObject.parent;
						if (nodeParent.getProjectIndependent()) {
							superview.TempLinkedProject = null;
						} else {
							superview.TempLinkedProject = Common.getProjectByName(nodeParent.getProject());
						}
					}

				}

				if (superview.TempLinkedProject == null) {
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					IWorkbenchWindow window = page.getWorkbenchWindow();
					ISelection ADTselection = window.getSelectionService().getSelection();
					superview.TempLinkedProject = ProjectUtil.getActiveAdtCoreProject(ADTselection, null, null,
							IAdtCoreProject.ABAP_PROJECT_NATURE);
					try {
						superview.TempLinkedProject.refreshLocal(IProject.DEPTH_INFINITE, new NullProgressMonitor());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				if (superview.TempLinkedProject != null) {
					superview.TempLinkedEditorProject = superview.TempLinkedProject.getName();

					try {
						IAdtLogonService logonService = AdtLogonServiceFactory.createLogonService();
						IAdtLogonServiceUI logonServiceUI = AdtLogonServiceUIFactory.createLogonServiceUI();

						if (logonService.isLoggedOn(superview.TempLinkedEditorProject) == false) {

							logonServiceUI.ensureLoggedOn((IAdaptable) superview.TempLinkedProject);
						}

						if (obj instanceof TreeObject) {

							TreeObject nodeObject = ((TreeObject) obj);
							TypeOfEntry NodeType = nodeObject.getType();
							TreeParent nodeParent = nodeObject.parent;
							switch (NodeType) {
							case Transaction:
								AdtSapGuiEditorUtilityFactory.createSapGuiEditorUtility().openEditorAndStartTransaction(
										superview.TempLinkedProject, obj.toString(), true);
								break;
							case Folder:
								break;
							case URL:
								try {
									PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser()
											.openURL(new URL(((TreeObject) obj).getTechnicalName()));
								} catch (PartInitException | MalformedURLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();

								}
								break;
							case ADTLink:
								openAdtLink(superview.TempLinkedProject,
										new String(((TreeObject) obj).getTechnicalName()));
								break;
							case Program:
								if (nodeParent.getDevObjProject() == false) {
									runObject(superview.TempLinkedProject, nodeObject.getName(), nodeObject.Type);
									break;
								} else {
									openObject(superview.TempLinkedProject, nodeObject.getName(), nodeObject.Type);
									break;
								}
							case Table:

							default:
								if (nodeParent.getDevObjProject() == false) {
									runObject(superview.TempLinkedProject, nodeObject.getName(), nodeObject.Type);
									break;
								} else {
									openObject(superview.TempLinkedProject, nodeObject.getName(), nodeObject.Type);
									break;
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {
					if (obj instanceof TreeObject) {
						TypeOfEntry NodeType = ((TreeObject) obj).getType();
						if (NodeType == TypeOfEntry.URL) {
							try {
								PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser()
										.openURL(new URL(((TreeObject) obj).getTechnicalName()));
							} catch (PartInitException | MalformedURLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}

			}
		};
	}

	private void createDeleteAction(TreeViewer viewer) {
		actDelete = new Action() {
			@Override
			public void run() {
				deleteObjectFromAction(viewer);
			}
		};
		actDelete.setText("Delete");
		actDelete.setToolTipText("Delete");
		actDelete.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
	}

	private void createEditAction(TreeViewer viewer, AFIcons AFIcon) {
		actEdit = new Action() {
			@Override
			public void run() {
				if (viewer.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

					TreeObject object = (TreeObject) selection.getFirstElement();
					editObjectFromAction(object.getType(),
							XMLhandler.getObjectXMLNode(object.getType()).isNameToUpper(), viewer);

				}
			}
		};
		actEdit.setText("Edit");
		actEdit.setToolTipText("Edit");
		actEdit.setImageDescriptor(AFIcon.getRenameIconImgDescr());
	}

	private void createDelFolderAction(TreeViewer viewer) {
		actDelFolder = new Action() {
			@Override
			public void run() {
				if (viewer.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

					TreeObject object = (TreeObject) selection.getFirstElement();

					if (object instanceof TreeParent) {
						XMLhandler.delFolderFromXML(((TreeParent) object).getFolderID(),
								((TreeParent) object).getTypeOfFolder());
						Superview.refreshViewer(viewer);
					}

				}
			}
		};
		actDelFolder.setText("Delete Folder");
		actDelFolder.setToolTipText("Folder");
		actDelFolder.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
	}

	private void createAddFMAction(TreeViewer viewer, AFIcons AFIcon) {
		actAddFunctionModule = new Action() {
			@Override
			public void run() {
				addObjectFromAction(TypeOfEntry.FunctionModule, true, viewer);
			}

		};
		actAddFunctionModule.setText("Add function module");
		actAddFunctionModule.setToolTipText("Function Module");
		actAddFunctionModule.setImageDescriptor(AFIcon.getFunctionModuleIconImgDescr());
	}

	private void createAddFGAction(TreeViewer viewer, AFIcons AFIcon) {
		actAddFunctionGroup = new Action() {
			@Override
			public void run() {
				addObjectFromAction(TypeOfEntry.FunctionGroup, true, viewer);
			}

		};
		actAddFunctionGroup.setText("Add function group");
		actAddFunctionGroup.setToolTipText("Function Group");
		actAddFunctionGroup.setImageDescriptor(AFIcon.getFunctionGroupIconImgDescr());
	}

	private void createAddIterfaceAction(TreeViewer viewer, AFIcons AFIcon) {
		actAddInterface = new Action() {
			@Override
			public void run() {
				addObjectFromAction(TypeOfEntry.Interface, true, viewer);
			}

		};
		actAddInterface.setText("Add interface");
		actAddInterface.setToolTipText("Interface");
		actAddInterface.setImageDescriptor(AFIcon.getInterfaceIconImgDescr());
	}

	private void createAddClassAction(TreeViewer viewer, AFIcons AFIcon) {
		actAddClass = new Action() {
			@Override
			public void run() {
				addObjectFromAction(TypeOfEntry.Class, true, viewer);
			}

		};
		actAddClass.setText("Add class");
		actAddClass.setToolTipText("Class");
		actAddClass.setImageDescriptor(AFIcon.getClassIconImgDescr());
	}

	private void createAddAMDPAction(TreeViewer viewer, AFIcons AFIcon) {
		actAddAMDP = new Action() {
			@Override
			public void run() {
				addObjectFromAction(TypeOfEntry.AMDP, true, viewer);
			}

		};
		actAddAMDP.setText("Add AMDP");
		actAddAMDP.setToolTipText("AMDP");
		actAddAMDP.setImageDescriptor(AFIcon.getAMDPImgDescr());
	}

	private void createAddCDSViewAction(TreeViewer viewer, AFIcons AFIcon) {

		actAddCDS = new Action() {
			@Override
			public void run() {
				addObjectFromAction(TypeOfEntry.CDSView, true, viewer);
			}

		};
		actAddCDS.setText("Add CDS View");
		actAddCDS.setToolTipText("CDS");
		actAddCDS.setImageDescriptor(AFIcon.getCDSViewImgDescr());
	}

	private void createAddAdtLinkAction(TreeViewer viewer, AFIcons AFIcon) {
		actAddADTLink = new Action() {
			@Override
			public void run() {
				URLDialog URLDialog = new URLDialog(viewer.getControl().getShell());
				URLDialog.create(TypeOfEntry.ADTLink, false);
				if (URLDialog.open() == Window.OK) {
					if (viewer.getSelection() instanceof IStructuredSelection) {
						IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

						TreeObject object = (TreeObject) selection.getFirstElement();

						if (object instanceof TreeParent) {
							String ADTLink = URLDialog.getURL();
							ADTLink = ADTLink.replace("(?<=\'/\'/)(.*?)(?=\'/)", "$system");
							XMLhandler.addURLToXML(URLDialog.getName(), URLDialog.getDescription(),
									URLDialog.getLongDescription(), ADTLink, ((TreeParent) object).getFolderID(),
									TypeOfXMLNode.ADTLinkNode, object.getParent().getTypeOfFolder());
							System.out.println(URLDialog.getName());
							System.out.println(URLDialog.getDescription());
							Superview.refreshViewer(viewer);
						}

					}
				}
			}
		};

		actAddADTLink.setText("Add ADT Link");
		actAddADTLink.setToolTipText("ADT Link");
		actAddADTLink.setImageDescriptor(AFIcon.getADTLinkImgDescr());
	}

	private void createAddTransactionAction(TreeViewer viewer, AFIcons AFIcon) {
		actAddTransaction = new Action() {
			@Override
			public void run() {
				addObjectFromAction(TypeOfEntry.Transaction, true, viewer);
			}

		};
		actAddTransaction.setText("Add Transaction");
		actAddTransaction.setToolTipText("Transaction");
		actAddTransaction.setImageDescriptor(AFIcon.getTransactionImgDescr());
	}

	private void createAddProgramAction(TreeViewer viewer, AFIcons AFIcon) {
		actAddProgram = new Action() {
			@Override
			public void run() {
				addObjectFromAction(TypeOfEntry.Program, true, viewer);
			}

		};
		actAddProgram.setText("Add Program");
		actAddProgram.setToolTipText("Program");
		actAddProgram.setImageDescriptor(AFIcon.getProgramIconImgDescr());
	}

	private void createAddViewAction(TreeViewer viewer, AFIcons AFIcon) {
		actAddView = new Action() {
			@Override
			public void run() {
				addObjectFromAction(TypeOfEntry.View, true, viewer);
			}

		};
		actAddView.setText("Add View");
		actAddView.setToolTipText("View");
		actAddView.setImageDescriptor(AFIcon.getViewIconImgDescr());
	}

	private void createAddTableAction(TreeViewer viewer, AFIcons AFIcon) {
		actAddTable = new Action() {
			@Override
			public void run() {
				addObjectFromAction(TypeOfEntry.Table, true, viewer);
			}

		};
		actAddTable.setText("Add Table");
		actAddTable.setToolTipText("Table");
		actAddTable.setImageDescriptor(AFIcon.getTableIconImgDescr());
	}

	private void createAddMessageClassAction(TreeViewer viewer, AFIcons AFIcon) {
		actAddMessageClass = new Action() {
			@Override
			public void run() {
				addObjectFromAction(TypeOfEntry.MessageClass, true, viewer);
			}

		};
		actAddMessageClass.setText("Add Message Class");
		actAddMessageClass.setToolTipText("Message Class");
		actAddMessageClass.setImageDescriptor(AFIcon.getMessageClassIconImgDescr());
	}

	private void createAddSeachHelpAction(TreeViewer viewer, AFIcons AFIcon) {
		actAddSearchHelp = new Action() {
			@Override
			public void run() {
				addObjectFromAction(TypeOfEntry.SearchHelp, true, viewer);
			}

		};
		actAddSearchHelp.setText("Add Search Help");
		actAddSearchHelp.setToolTipText("Search Help");
		actAddSearchHelp.setImageDescriptor(AFIcon.getSearchHelpIconImgDescr());
	}

	private void createAddUrlAction(TreeViewer viewer, AFIcons AFIcon) {
		actAddURL = new Action() {
			@Override
			public void run() {
				URLDialog URLDialog = new URLDialog(viewer.getControl().getShell());
				URLDialog.create(TypeOfEntry.URL, false);
				if (URLDialog.open() == Window.OK) {
					if (viewer.getSelection() instanceof IStructuredSelection) {
						IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

						TreeObject object = (TreeObject) selection.getFirstElement();

						if (object instanceof TreeParent) {

							XMLhandler.addURLToXML(URLDialog.getName(), URLDialog.getDescription(),
									URLDialog.getLongDescription(), URLDialog.getURL(),
									((TreeParent) object).getFolderID(), TypeOfXMLNode.urlNode,
									object.getParent().getTypeOfFolder());
							System.out.println(URLDialog.getName());
							System.out.println(URLDialog.getDescription());
							Superview.refreshViewer(viewer);
						}

					}
				}
			}
		};
		actAddURL.setText("Add URL");
		actAddURL.setToolTipText("URL");
		actAddURL.setImageDescriptor(AFIcon.getURLIconImgDescr());
	}

	@SuppressWarnings({ "restriction", "restriction" })
	public static void runObject(IProject project, String reportName, TypeOfEntry type) {
		String programName = "";
		try {
			List<IAdtObjectReference> res = AdtRisQuickSearchFactory
					.createQuickSearch(project, new NullProgressMonitor()).execute(reportName, 10);
			for (IAdtObjectReference ref : res) {
				if (Common.checkType(ref.getType(), type)) {
					Pattern regexPatern = Pattern.compile("^\\S*");
					Matcher regexMatch = regexPatern.matcher(ref.getName());
					while (regexMatch.find()) {
						programName = regexMatch.group(0);
					}
					if (programName.equalsIgnoreCase(reportName)) {
						IAbapProject AP = project.getAdapter(IAbapProject.class);
						IDestinationData DD = AP.getDestinationData();
						IDestinationDataWritable DDW = DD.getWritable();
						DDW.setLanguage("DE");
						IDestinationData DDN = DDW.getReadOnlyClone();
						AP.setDestinationData(DDN);
						AdtSapGuiEditorUtilityFactory.createSapGuiEditorUtility().openEditorForObject(project, ref,
								true, WorkbenchAction.EXECUTE.toString(), null, Collections.<String, String>emptyMap());
						return;
					}
				}
			}
		} catch (OperationCanceledException | RisQuickSearchNotSupportedException e) {
			// AdtLogging.getLogger(getClass()).error(e);
		}
	}

	public static void openObject(IProject project, String reportName, TypeOfEntry type) {
		String programName = "";
		try {
			List<IAdtObjectReference> res = AdtRisQuickSearchFactory
					.createQuickSearch(project, new NullProgressMonitor()).execute(reportName, 10);
			for (IAdtObjectReference ref : res) {
				if (Common.checkType(ref.getType(), type)) {

					Pattern regexPatern = Pattern.compile("^\\S*");
					Matcher regexMatch = regexPatern.matcher(ref.getName());
					while (regexMatch.find()) {
						programName = regexMatch.group(0);
					}
					if (programName.equalsIgnoreCase(reportName)) {
						AdtNavigationServiceFactory.createNavigationService().navigate(project, ref, true);
						return;
					}
				}
			}
		} catch (OperationCanceledException | RisQuickSearchNotSupportedException e) {
			// AdtLogging.getLogger(getClass()).error(e);
		}
	}

	public static void openAdtLink(IProject project, String adtLink) {
		adtLink = adtLink.replace("(?<=\'/\'/)(.*?)(?=\'/)", project.getName().toString());
		AdtNavigationServiceFactory.createNavigationService().navigateWithExternalLink(adtLink, project);
		return;
	}

	private static void importFavorites(TreeViewer viewer) {
		Shell shell = viewer.getControl().getShell();
		FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.setFilterNames(new String[] { "XML", "All Files (*.*)" });
		dialog.setFilterExtensions(new String[] { "*.xml", "*.*" });
		dialog.setFileName("favorites.xml");

		String ImportFileName = dialog.open();
		if (!ImportFileName.equals(""))
			XMLhandler.replaceFavFile(ImportFileName);
	}

	private static void exportFavorites(TreeViewer viewer) {
		Shell shell = viewer.getControl().getShell();
		FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setFilterNames(new String[] { "XML", "All Files (*.*)" });
		dialog.setFilterExtensions(new String[] { "*.xml", "*.*" }); // Windows
		// wild
		// cards
		// ColumnControlListener.dialog.setFilterPath("c:\\"); // Windows path
		dialog.setFileName("favorites.xml");
		// System.out.println("Save to: " + dialog.open());

		String ExportFileName = dialog.open();
		if (!ExportFileName.equals(""))
			XMLhandler.copyFavFile(ExportFileName);
	}

	public static void editObjectFromAction(TypeOfEntry Type, Boolean NameToUpper, TreeViewer viewer) {

		if (viewer.getSelection() instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

			TreeObject Object = (TreeObject) selection.getFirstElement();

			if (Object instanceof TreeParent) {
				TreeParent Folder = (TreeParent) Object;
				Boolean DevObjFolder = Folder.getDevObjProject();
				FolderDialog FoDialog = new FolderDialog(viewer.getControl().getShell(), DevObjFolder);
				FoDialog.create(true);
				String Name = Folder.getName();
				if (NameToUpper) {
					Name = Name.toUpperCase();
				}

				FoDialog.setName(Name);
				FoDialog.setDescription(Folder.getDescription());
				FoDialog.setPrjInd(Folder.getProjectIndependent());
				FoDialog.setDevObjectFolder(Folder.getDevObjProject());
				FoDialog.setLongDescription(Folder.getLongDescription());
				if (FoDialog.open() == Window.OK) {

					Name = FoDialog.getName();
					if (NameToUpper) {
						Name = Name.toUpperCase();
					}

					XMLhandler.editFolderInXML(Folder.getFolderID(), Name, FoDialog.getDescription(),
							FoDialog.getLongDescription(), FoDialog.getPrjInd(), Common.getProjectName(),
							FoDialog.getDevObjectFolder(), Folder.getTypeOfFolder());

					Superview.refreshViewer(viewer);
				}

			} else {

				switch (Object.getType()) {
				case URL:
					URLDialog UrlDialog = new URLDialog(viewer.getControl().getShell());
					UrlDialog.create(TypeOfEntry.URL, true);

					String Name = Object.getName();
					if (NameToUpper) {
						Name = Name.toUpperCase();
					}

					UrlDialog.setName(Name);
					UrlDialog.SetDescription(Object.getDescription());
					UrlDialog.setURL(Object.getTechnicalName());
					UrlDialog.setLongDescription(Object.getLongDescription());
					if (UrlDialog.open() == Window.OK) {
						Name = UrlDialog.getName();
						if (NameToUpper) {
							Name = Name.toUpperCase();
						}

						XMLhandler.delObjectFromXML(Type, Object.getName(), Object.getParent().getFolderID(),
								Object.getParent().getTypeOfFolder());
						XMLhandler.addURLToXML(Name, UrlDialog.getDescription(), UrlDialog.getLongDescription(),
								UrlDialog.getURL(), Object.getParent().getFolderID(), TypeOfXMLNode.urlNode,
								Object.getParent().getTypeOfFolder());

						Superview.refreshViewer(viewer);
					}
					break;
				case ADTLink:
					UrlDialog = new URLDialog(viewer.getControl().getShell());
					UrlDialog.create(TypeOfEntry.ADTLink, true);

					Name = Object.getName();
					if (NameToUpper) {
						Name = Name.toUpperCase();
					}

					UrlDialog.setName(Name);
					UrlDialog.SetDescription(Object.getDescription());
					UrlDialog.setURL(Object.getTechnicalName());
					UrlDialog.setLongDescription(Object.getLongDescription());

					if (UrlDialog.open() == Window.OK) {
						Name = UrlDialog.getName();
						if (NameToUpper) {
							Name = Name.toUpperCase();
						}

						XMLhandler.delObjectFromXML(Type, Object.getName(), Object.getParent().getFolderID(),
								Object.getParent().getTypeOfFolder());
						XMLhandler.addURLToXML(Name, UrlDialog.getDescription(), UrlDialog.getLongDescription(),
								UrlDialog.getURL(), Object.getParent().getFolderID(), TypeOfXMLNode.ADTLinkNode,
								Object.getParent().getTypeOfFolder());

						Superview.refreshViewer(viewer);
					}
					break;
				default:
					NameDialog NaDialog = new NameDialog(viewer.getControl().getShell(), Type);
					NaDialog.create(Type, true);
					Name = Object.getName();
					if (NameToUpper) {
						Name = Name.toUpperCase();
					}
					NaDialog.setName(Name);
					NaDialog.setDescription(Object.getDescription());
					NaDialog.setLongDescription(Object.getLongDescription());
					if (NaDialog.open() == Window.OK) {
						Name = NaDialog.getName();
						if (NameToUpper) {
							Name = Name.toUpperCase();
						}
						XMLhandler.delObjectFromXML(Type, Object.getName(), Object.getParent().getFolderID(),
								Object.getParent().getTypeOfFolder());
						XMLhandler.addObjectToXML(Type, Name, NaDialog.getDescription(), NaDialog.getLongDescription(),
								Object.getParent().getFolderID(), Object.getParent().getTypeOfFolder());
						Superview.refreshViewer(viewer);
					}
					break;
				}

			}
		}
	}

	public static void deleteObjectFromAction(TreeViewer viewer) {
		if (viewer.getSelection() instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

			TreeObject object = (TreeObject) selection.getFirstElement();

			if (object instanceof TreeObject) {
				TreeObject treeObj = (TreeObject) object;
				XMLhandler.delObjectFromXML(treeObj.Type, object.Name, object.parent.getFolderID(),
						object.parent.getTypeOfFolder());
				Superview.refreshViewer(viewer);
			}
		}
	}

	public static void addObjectFromAction(TypeOfEntry Type, Boolean NameToUpper, TreeViewer viewer) {
		NameDialog NaDialog = new NameDialog(viewer.getControl().getShell(), Type);
		NaDialog.create(Type, false);
		if (NaDialog.open() == Window.OK) {

			if (viewer.getSelection() instanceof IStructuredSelection) {
				IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

				TreeObject Folder = (TreeObject) selection.getFirstElement();

				if (Folder instanceof TreeParent) {
					String Name = NaDialog.getName();
					if (NameToUpper) {
						Name = Name.toUpperCase();
					}
					XMLhandler.addObjectToXML(Type, Name, NaDialog.getDescription(),
							((TreeParent) Folder).getLongDescription(), ((TreeParent) Folder).getFolderID(),
							((TreeParent) Folder).getTypeOfFolder());
					Superview.refreshViewer(viewer);
				}

			}
		}
	}

	public static void addOpenInProjectMenu(IMenuManager manager, TreeViewer viewer) {
		// sub-menu for projects
		MenuManager subMenu = new MenuManager("Open in project", null);

		for (IProject ABAPProject : Common.getABAPProjects()) {

			try {
				Action projectAction = new Action() {
					@Override
					public void run() {

						ISelection selection = viewer.getSelection();
						Object obj = ((IStructuredSelection) selection).getFirstElement();

						if (ABAPProject != null) {
							if (obj instanceof TreeObject) {

								TreeObject nodeObject = ((TreeObject) obj);
								TypeOfEntry NodeType = nodeObject.getType();
								TreeParent nodeParent = nodeObject.parent;
								switch (NodeType) {
								case Transaction:
									IAdtSapGuiEditorUtility SGEU = AdtSapGuiEditorUtilityFactory
											.createSapGuiEditorUtility();
									SGEU.openEditorAndStartTransaction(ABAPProject, obj.toString(), true);
									break;
								case Folder:
									break;
								case URL:
									try {
										PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser()
												.openURL(new URL(((TreeObject) obj).getTechnicalName()));
									} catch (PartInitException | MalformedURLException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();

									}
									break;
								case ADTLink:
									openAdtLink(ABAPProject, new String(((TreeObject) obj).getTechnicalName()));
									break;
								case Program:
									if (nodeParent.getDevObjProject() == false) {
										runObject(ABAPProject, nodeObject.getName(), nodeObject.Type);
										break;
									} else {
										openObject(ABAPProject, nodeObject.getName(), nodeObject.Type);
										break;
									}
								case Table:

								default:
									if (nodeParent.getDevObjProject() == false) {
										runObject(ABAPProject, nodeObject.getName(), nodeObject.Type);
										break;
									} else {
										openObject(ABAPProject, nodeObject.getName(), nodeObject.Type);
										break;
									}
								}
							}

						} else {
							if (obj instanceof TreeObject) {
								TypeOfEntry NodeType = ((TreeObject) obj).getType();
								if (NodeType == TypeOfEntry.URL) {
									try {
										PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser()
												.openURL(new URL(((TreeObject) obj).getTechnicalName()));
									} catch (PartInitException | MalformedURLException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}
						}
					}
				};
				projectAction.setText(ABAPProject.getName());
				projectAction.setToolTipText(ABAPProject.getName());
				projectAction.setImageDescriptor(
						PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_PROJECT));

				subMenu.add(projectAction);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();

			}
			manager.add(subMenu);

		}
	}

}

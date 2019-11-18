package com.abapblog.favorites.superview;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
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
import org.eclipse.ui.PlatformUI;

import com.abapblog.favorites.Activator;
import com.abapblog.favorites.common.AFIcons;
import com.abapblog.favorites.common.Common;
import com.abapblog.favorites.common.CommonTypes.TypeOfEntry;
import com.abapblog.favorites.dialog.FolderDialog;
import com.abapblog.favorites.dialog.NameDialog;
import com.abapblog.favorites.dialog.URLDialog;
import com.abapblog.favorites.preferences.PreferenceConstants;
import com.abapblog.favorites.tree.TreeObject;
import com.abapblog.favorites.tree.TreeParent;
import com.abapblog.favorites.xml.XMLhandler;
import com.sap.adt.destinations.ui.logon.AdtLogonServiceUIFactory;

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
	public ITreeNodeAction actDoubleClick;
	public Action actCopyToClipboard;

	public void makeActions(final Superview superview) {
		final AFIcons AFIcon = new AFIcons();
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
		this.actDoubleClick = new DoubleClickAction(superview);
		createExportFavoritesAction(superview.viewer);
		createImportFavoritesAction(superview.viewer);
		createCopyToClipboardAction(superview.viewer, AFIcon);
	}

	private void createAddFolderAction(final Superview superview) {
		this.actAddFolder = new Action() {
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

				final FolderDialog FolderDialog = new FolderDialog(superview.viewer.getControl().getShell(), FolderDO);
				FolderDialog.create();
				if (FolderDialog.open() == Window.OK) {

					if (superview.viewer.getSelection() instanceof IStructuredSelection) {
						final IStructuredSelection selection = (IStructuredSelection) superview.viewer.getSelection();

						final TreeObject Folder = (TreeObject) selection.getFirstElement();

						if (Folder instanceof TreeParent) {
							XMLhandler.addFolderToXML(FolderDialog.getName(), FolderDialog.getDescription(),
									FolderDialog.getLongDescription(), FolderDialog.getPrjInd(),
									Common.getProjectName(), FolderDialog.getDevObjectFolder(),
									((TreeParent) Folder).getFolderID(), ((TreeParent) Folder).getTypeOfFolder());
							Superview.refreshViewer(superview.viewer);
						}

					}

				}

			}
		};
		this.actAddFolder.setText("Add New Folder");
		this.actAddFolder.setToolTipText("Add New Folder");
		this.actAddFolder.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ADD));
	}

	private void createAddRootFolderAction(final Superview superview) {
		this.actAddRootFolder = new Action() {
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

				final FolderDialog FolderDialog = new FolderDialog(superview.viewer.getControl().getShell(), FolderDO);
				FolderDialog.create();
				if (FolderDialog.open() == Window.OK) {

					XMLhandler.addFolderToXML(FolderDialog.getName(), FolderDialog.getDescription(),
							FolderDialog.getLongDescription(), FolderDialog.getPrjInd(), Common.getProjectName(),
							FolderDialog.getDevObjectFolder(), "", superview.FolderNode);
					Superview.refreshViewer(superview.viewer);
				}

			}

		};

		this.actAddRootFolder.setText("Add New Folder");
		this.actAddRootFolder.setToolTipText("Add New Folder");
		this.actAddRootFolder.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ADD));
	}

	private void createImportFavoritesAction(final TreeViewer viewer) {
		this.actImportFavorites = new Action() {
			@Override
			public void run() {
				importFavorites(viewer);
			}

		};
		this.actImportFavorites.setText("Import Favorites");
		this.actImportFavorites.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
	}

	private void createExportFavoritesAction(final TreeViewer viewer) {
		this.actExportFavorites = new Action() {
			@Override
			public void run() {
				exportFavorites(viewer);
			}

		};
		this.actExportFavorites.setText("Export Favorites");
		this.actExportFavorites.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_SAVEAS_EDIT));
	}

	private void createDeleteAction(final TreeViewer viewer) {
		this.actDelete = new Action() {
			@Override
			public void run() {
				deleteObjectFromAction(viewer);
			}
		};
		this.actDelete.setText("Delete");
		this.actDelete.setToolTipText("Delete");
		this.actDelete.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
	}

	private void createEditAction(final TreeViewer viewer, final AFIcons AFIcon) {
		this.actEdit = new Action() {
			@Override
			public void run() {
				if (viewer.getSelection() instanceof IStructuredSelection) {
					final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

					final TreeObject object = (TreeObject) selection.getFirstElement();
					editObjectFromAction(object.getType(),
							XMLhandler.getObjectXMLNode(object.getType()).isNameToUpper(), viewer);

				}
			}
		};
		this.actEdit.setText("Edit");
		this.actEdit.setToolTipText("Edit");
		this.actEdit.setImageDescriptor(AFIcon.getRenameIconImgDescr());
	}

	private void createDelFolderAction(final TreeViewer viewer) {
		this.actDelFolder = new Action() {
			@Override
			public void run() {
				if (viewer.getSelection() instanceof IStructuredSelection) {
					final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

					final TreeObject object = (TreeObject) selection.getFirstElement();

					if (object instanceof TreeParent) {
						XMLhandler.delFolderFromXML(((TreeParent) object).getFolderID(),
								((TreeParent) object).getTypeOfFolder());
						Superview.refreshViewer(viewer);
					}

				}
			}
		};
		this.actDelFolder.setText("Delete Folder");
		this.actDelFolder.setToolTipText("Folder");
		this.actDelFolder.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
	}

	private void createAddFMAction(final TreeViewer viewer, final AFIcons AFIcon) {
		this.actAddFunctionModule = new Action() {
			@Override
			public void run() {
				addObjectFromAction(TypeOfEntry.FunctionModule, true, viewer);
			}

		};
		this.actAddFunctionModule.setText("Add function module");
		this.actAddFunctionModule.setToolTipText("Function Module");
		this.actAddFunctionModule.setImageDescriptor(AFIcon.getFunctionModuleIconImgDescr());
	}

	private void createAddFGAction(final TreeViewer viewer, final AFIcons AFIcon) {
		this.actAddFunctionGroup = new Action() {
			@Override
			public void run() {
				addObjectFromAction(TypeOfEntry.FunctionGroup, true, viewer);
			}

		};
		this.actAddFunctionGroup.setText("Add function group");
		this.actAddFunctionGroup.setToolTipText("Function Group");
		this.actAddFunctionGroup.setImageDescriptor(AFIcon.getFunctionGroupIconImgDescr());
	}

	private void createAddIterfaceAction(final TreeViewer viewer, final AFIcons AFIcon) {
		this.actAddInterface = new Action() {
			@Override
			public void run() {
				addObjectFromAction(TypeOfEntry.Interface, true, viewer);
			}

		};
		this.actAddInterface.setText("Add interface");
		this.actAddInterface.setToolTipText("Interface");
		this.actAddInterface.setImageDescriptor(AFIcon.getInterfaceIconImgDescr());
	}

	private void createAddClassAction(final TreeViewer viewer, final AFIcons AFIcon) {
		this.actAddClass = new Action() {
			@Override
			public void run() {
				addObjectFromAction(TypeOfEntry.Class, true, viewer);
			}

		};
		this.actAddClass.setText("Add class");
		this.actAddClass.setToolTipText("Class");
		this.actAddClass.setImageDescriptor(AFIcon.getClassIconImgDescr());
	}

	private void createAddAMDPAction(final TreeViewer viewer, final AFIcons AFIcon) {
		this.actAddAMDP = new Action() {
			@Override
			public void run() {
				addObjectFromAction(TypeOfEntry.AMDP, true, viewer);
			}

		};
		this.actAddAMDP.setText("Add AMDP");
		this.actAddAMDP.setToolTipText("AMDP");
		this.actAddAMDP.setImageDescriptor(AFIcon.getAMDPImgDescr());
	}

	private void createAddCDSViewAction(final TreeViewer viewer, final AFIcons AFIcon) {

		this.actAddCDS = new Action() {
			@Override
			public void run() {
				addObjectFromAction(TypeOfEntry.CDSView, true, viewer);
			}

		};
		this.actAddCDS.setText("Add CDS View");
		this.actAddCDS.setToolTipText("CDS");
		this.actAddCDS.setImageDescriptor(AFIcon.getCDSViewImgDescr());
	}

	private void createAddAdtLinkAction(final TreeViewer viewer, final AFIcons AFIcon) {
		this.actAddADTLink = new Action() {
			@Override
			public void run() {
				final URLDialog URLDialog = new URLDialog(viewer.getControl().getShell());
				URLDialog.create(TypeOfEntry.ADTLink, false);
				if (URLDialog.open() == Window.OK) {
					if (viewer.getSelection() instanceof IStructuredSelection) {
						final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

						final TreeObject object = (TreeObject) selection.getFirstElement();

						if (object instanceof TreeParent) {
							String ADTLink = URLDialog.getURL();
							ADTLink = ADTLink.replace("(?<=\'/\'/)(.*?)(?=\'/)", "$system");
							XMLhandler.addObjectToXML(TypeOfEntry.ADTLink, URLDialog.getName(),
									URLDialog.getDescription(), URLDialog.getLongDescription(), ADTLink,
									((TreeParent) object).getFolderID(), object.getParent().getTypeOfFolder());
							System.out.println(URLDialog.getName());
							System.out.println(URLDialog.getDescription());
							Superview.refreshViewer(viewer);
						}

					}
				}
			}
		};

		this.actAddADTLink.setText("Add ADT Link");
		this.actAddADTLink.setToolTipText("ADT Link");
		this.actAddADTLink.setImageDescriptor(AFIcon.getADTLinkImgDescr());
	}

	private void createAddTransactionAction(final TreeViewer viewer, final AFIcons AFIcon) {
		this.actAddTransaction = new Action() {
			@Override
			public void run() {
				addObjectFromAction(TypeOfEntry.Transaction, true, viewer);
			}

		};
		this.actAddTransaction.setText("Add Transaction");
		this.actAddTransaction.setToolTipText("Transaction");
		this.actAddTransaction.setImageDescriptor(AFIcon.getTransactionImgDescr());
	}

	private void createAddProgramAction(final TreeViewer viewer, final AFIcons AFIcon) {
		this.actAddProgram = new Action() {
			@Override
			public void run() {
				addObjectFromAction(TypeOfEntry.Program, true, viewer);
			}

		};
		this.actAddProgram.setText("Add Program");
		this.actAddProgram.setToolTipText("Program");
		this.actAddProgram.setImageDescriptor(AFIcon.getProgramIconImgDescr());
	}

	private void createAddViewAction(final TreeViewer viewer, final AFIcons AFIcon) {
		this.actAddView = new Action() {
			@Override
			public void run() {
				addObjectFromAction(TypeOfEntry.View, true, viewer);
			}

		};
		this.actAddView.setText("Add View");
		this.actAddView.setToolTipText("View");
		this.actAddView.setImageDescriptor(AFIcon.getViewIconImgDescr());
	}

	private void createAddTableAction(final TreeViewer viewer, final AFIcons AFIcon) {
		this.actAddTable = new Action() {
			@Override
			public void run() {
				addObjectFromAction(TypeOfEntry.Table, true, viewer);
			}

		};
		this.actAddTable.setText("Add Table");
		this.actAddTable.setToolTipText("Table");
		this.actAddTable.setImageDescriptor(AFIcon.getTableIconImgDescr());
	}

	private void createAddMessageClassAction(final TreeViewer viewer, final AFIcons AFIcon) {
		this.actAddMessageClass = new Action() {
			@Override
			public void run() {
				addObjectFromAction(TypeOfEntry.MessageClass, true, viewer);
			}

		};
		this.actAddMessageClass.setText("Add Message Class");
		this.actAddMessageClass.setToolTipText("Message Class");
		this.actAddMessageClass.setImageDescriptor(AFIcon.getMessageClassIconImgDescr());
	}

	private void createAddSeachHelpAction(final TreeViewer viewer, final AFIcons AFIcon) {
		this.actAddSearchHelp = new Action() {
			@Override
			public void run() {
				addObjectFromAction(TypeOfEntry.SearchHelp, true, viewer);
			}

		};
		this.actAddSearchHelp.setText("Add Search Help");
		this.actAddSearchHelp.setToolTipText("Search Help");
		this.actAddSearchHelp.setImageDescriptor(AFIcon.getSearchHelpIconImgDescr());
	}

	private void createAddUrlAction(final TreeViewer viewer, final AFIcons AFIcon) {
		this.actAddURL = new Action() {
			@Override
			public void run() {
				final URLDialog URLDialog = new URLDialog(viewer.getControl().getShell());
				URLDialog.create(TypeOfEntry.URL, false);
				if (URLDialog.open() == Window.OK) {
					if (viewer.getSelection() instanceof IStructuredSelection) {
						final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

						final TreeObject object = (TreeObject) selection.getFirstElement();

						if (object instanceof TreeParent) {

							XMLhandler.addObjectToXML(TypeOfEntry.URL, URLDialog.getName(), URLDialog.getDescription(),
									URLDialog.getLongDescription(), URLDialog.getURL(),
									((TreeParent) object).getFolderID(), object.getParent().getTypeOfFolder());
							System.out.println(URLDialog.getName());
							System.out.println(URLDialog.getDescription());
							Superview.refreshViewer(viewer);
						}

					}
				}
			}
		};
		this.actAddURL.setText("Add URL");
		this.actAddURL.setToolTipText("URL");
		this.actAddURL.setImageDescriptor(AFIcon.getURLIconImgDescr());
	}

	private static void importFavorites(final TreeViewer viewer) {
		final Shell shell = viewer.getControl().getShell();
		final FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.setFilterNames(new String[] { "XML", "All Files (*.*)" });
		dialog.setFilterExtensions(new String[] { "*.xml", "*.*" });
		dialog.setFileName("favorites.xml");

		final String ImportFileName = dialog.open();
		if (!ImportFileName.equals("")) {
			XMLhandler.replaceFavFile(ImportFileName);
		}
	}

	private static void exportFavorites(final TreeViewer viewer) {
		final Shell shell = viewer.getControl().getShell();
		final FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setFilterNames(new String[] { "XML", "All Files (*.*)" });
		dialog.setFilterExtensions(new String[] { "*.xml", "*.*" }); // Windows
		// wild
		// cards
		// ColumnControlListener.dialog.setFilterPath("c:\\"); // Windows path
		dialog.setFileName("favorites.xml");
		// System.out.println("Save to: " + dialog.open());

		final String ExportFileName = dialog.open();
		if (!ExportFileName.equals("")) {
			XMLhandler.copyFavFile(ExportFileName);
		}
	}

	public static void editObjectFromAction(final TypeOfEntry Type, final Boolean NameToUpper,
			final TreeViewer viewer) {

		if (viewer.getSelection() instanceof IStructuredSelection) {
			final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

			final TreeObject Object = (TreeObject) selection.getFirstElement();

			if (Object instanceof TreeParent) {
				final TreeParent Folder = (TreeParent) Object;
				final Boolean DevObjFolder = Folder.getDevObjProject();
				final FolderDialog FoDialog = new FolderDialog(viewer.getControl().getShell(), DevObjFolder);
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
						XMLhandler.addObjectToXML(TypeOfEntry.URL, Name, UrlDialog.getDescription(),
								UrlDialog.getLongDescription(), UrlDialog.getURL(), Object.getParent().getFolderID(),
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
						XMLhandler.addObjectToXML(TypeOfEntry.ADTLink, Name, UrlDialog.getDescription(),
								UrlDialog.getLongDescription(), UrlDialog.getURL(), Object.getParent().getFolderID(),
								Object.getParent().getTypeOfFolder());

						Superview.refreshViewer(viewer);
					}
					break;
				default:
					final NameDialog NaDialog = new NameDialog(viewer.getControl().getShell(), Type);
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
								"", Object.getParent().getFolderID(), Object.getParent().getTypeOfFolder());
						Superview.refreshViewer(viewer);
					}
					break;
				}

			}
		}
	}

	public static void deleteObjectFromAction(final TreeViewer viewer) {
		if (viewer.getSelection() instanceof IStructuredSelection) {
			final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

			final TreeObject object = (TreeObject) selection.getFirstElement();

			if (object instanceof TreeObject) {
				final TreeObject treeObj = object;
				XMLhandler.delObjectFromXML(treeObj.getType(), object.getName(), object.getParent().getFolderID(),
						object.getParent().getTypeOfFolder());
				Superview.refreshViewer(viewer);
			}
		}
	}

	public static void addObjectFromAction(final TypeOfEntry Type, final Boolean NameToUpper, final TreeViewer viewer) {
		final NameDialog NaDialog = new NameDialog(viewer.getControl().getShell(), Type);
		NaDialog.create(Type, false);
		if (NaDialog.open() == Window.OK) {

			if (viewer.getSelection() instanceof IStructuredSelection) {
				final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

				final TreeObject Folder = (TreeObject) selection.getFirstElement();

				if (Folder instanceof TreeParent) {
					String Name = NaDialog.getName();
					if (NameToUpper) {
						Name = Name.toUpperCase();
					}
					XMLhandler.addObjectToXML(Type, Name, NaDialog.getDescription(),
							((TreeParent) Folder).getLongDescription(), ((TreeParent) Folder).getTechnicalName(),
							((TreeParent) Folder).getFolderID(), ((TreeParent) Folder).getTypeOfFolder());
					Superview.refreshViewer(viewer);
				}

			}
		}
	}

	public static void addOpenInProjectMenu(final IMenuManager manager, final TreeViewer viewer) {
		// sub-menu for projects
		final MenuManager subMenu = new MenuManager("Open in project", null);

		for (final IProject ABAPProject : Common.getABAPProjects()) {

			try {
				final Action projectAction = new Action() {
					@Override
					public void run() {
						if (!AdtLogonServiceUIFactory.createLogonServiceUI().ensureLoggedOn(ABAPProject).isOK()) {
							return;
						}
						final boolean enableEclipseNavigation = Activator.getDefault().getPreferenceStore()
								.getBoolean(PreferenceConstants.P_NAVIGATE_TO_ECLIPSE_FOR_SUPPORTED_DEV_OBJECTS);
						final ISelection selection = viewer.getSelection();
						final Iterator<?> selIter = ((IStructuredSelection) selection).iterator();
						while (selIter.hasNext()) {
							final Object selObj = selIter.next();
							final TreeObject treeObject = (TreeObject) selObj;
							AdtObjectHandler.executeTreeObject(treeObject, ABAPProject, enableEclipseNavigation, false);
						}
					}
				};
				projectAction.setText(ABAPProject.getName());
				projectAction.setToolTipText(ABAPProject.getName());
				projectAction.setImageDescriptor(
						PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_PROJECT));

				subMenu.add(projectAction);
			} catch (final Exception e1) {
				e1.printStackTrace();

			}
			manager.add(subMenu);

		}
	}

	private void createCopyToClipboardAction(final TreeViewer viewer, final AFIcons AFIcon) {
		this.actCopyToClipboard = new Action() {
			@Override
			public void run() {
				final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				final TreeObject object = (TreeObject) viewer.getStructuredSelection().getFirstElement();
				clipboard.setContents(new StringSelection(object.getName()), null);
			}
		};
		this.actCopyToClipboard.setText("Copy to Clipboard");
		this.actCopyToClipboard.setToolTipText("Copy to Clipboard");
		this.actCopyToClipboard.setImageDescriptor(AFIcon.getCopyToClipboardImgDescr());
	}

}

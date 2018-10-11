package com.abapblog.favorites.superview;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

import com.abapblog.favorites.common.AFPatternFilter;
import com.abapblog.favorites.common.ColumnControlListener;
import com.abapblog.favorites.common.Common;
import com.abapblog.favorites.common.CommonTypes.TypeOfEntry;
import com.abapblog.favorites.common.CommonTypes.TypeOfXMLNode;
import com.abapblog.favorites.common.ILinkedWithEditorView;
import com.abapblog.favorites.common.LinkWithEditorPartListener;
import com.abapblog.favorites.common.TreeObject;
import com.abapblog.favorites.common.TreeParent;
import com.abapblog.favorites.common.ViewContentProvider;
import com.abapblog.favorites.common.ViewLabelProvider;
import com.abapblog.favorites.views.Favorites;
import com.sap.adt.project.IAdtCoreProject;
import com.sap.adt.project.ui.util.ProjectUtil;

public abstract class superview extends ViewPart implements ILinkedWithEditorView, IFavorites {
	public static String ID;

	protected String LinkedEditorProject = "";
	protected IProject LinkedProject;
	protected IPartListener2 linkWithEditorPartListener = new LinkWithEditorPartListener(this);
	protected Action linkWithEditorAction;
	protected boolean linkingActive = true;
	public TreeViewer viewer;
	protected Common Utils;
	protected DrillDownAdapter drillDownAdapter;
	protected DragSource dndSource;
	protected IStructuredSelection dndSourceSelection;

	public static String partName;

	public static void savePluginSettings(IFavorites Favorite) {
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(ID);

		prefs.putBoolean("linking_active", Favorite.getLinkingActive());
		prefs.put("linked_project", Favorite.getLinkedEditorProject());

		try {
			// prefs are automatically flushed during a plugin's "super.stop()".
			prefs.flush();
		} catch (org.osgi.service.prefs.BackingStoreException e) {
			// TODO write a real exception handler.
			e.printStackTrace();
		}
	}

	protected void setNewPartName() {
		if (linkingActive) {
			setPartName(partName);
		} else {
			setPartName(partName + " (" + getLinkedEditorProject() + ")");
		}
	}

	@Override
	public void createPartControl(Composite parent) {

		AFPatternFilter filter = new AFPatternFilter();
		FilteredTree filteredTree = new FilteredTree(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL, filter, true);
		ColumnControlListener columnListener = new ColumnControlListener();
		columnListener.setID(ID);

		partName = getPartName();

		viewer = filteredTree.getViewer();
		drillDownAdapter = new DrillDownAdapter(viewer);
		Tree tree = viewer.getTree();

		Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
		int operations = DND.DROP_MOVE;

		viewer.addDragSupport(operations, types, new DragSourceListener() {
			public void dragStart(DragSourceEvent event) {
				TreeItem[] selection = tree.getSelection();
				if (selection.length > 0) {
					event.doit = true;
				} else {
					event.doit = false;
				}
			};

			public void dragSetData(DragSourceEvent event) {
				dndSourceSelection = (IStructuredSelection) viewer.getSelection();
				event.data = "Data"; // To not cause org.eclipse.swt.SWTException: Data does not have correct format
										// for type

			}

			public void dragFinished(DragSourceEvent event) {

			}

		});

		viewer.addDropSupport(operations, types, new DropTargetListener() {
			public void dragEnter(DropTargetEvent event) {
				if (event.detail == DND.DROP_DEFAULT) {
					if ((event.operations & DND.DROP_COPY) != 0) {
						event.detail = DND.DROP_COPY;
					} else {
						event.detail = DND.DROP_NONE;
					}
				}
			}

			public void dragOver(DropTargetEvent event) {
				event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
			}

			public void dragOperationChanged(DropTargetEvent event) {

			}

			public void dragLeave(DropTargetEvent event) {

			}

			public void dropAccept(DropTargetEvent event) {
				if (event.detail == DND.DROP_MOVE) {
					TreeItem Target = (TreeItem) event.item;
					String ID = Target.getText(2);
					String FolderType = Target.getText(3);
					String DevObjProj = Target.getText(4);
					Boolean DevObjProjBool;
					TypeOfXMLNode ParentType = TypeOfXMLNode.folderNode;
					if (ID == "") {
						ID = Target.getParentItem().getText(2);
						FolderType = Target.getParentItem().getText(3);
						DevObjProj = Target.getParentItem().getText(4);
					}
					if (FolderType == TypeOfXMLNode.folderNode.toString()) {
						ParentType = TypeOfXMLNode.folderNode;
					} else {
						ParentType = TypeOfXMLNode.folderDONode;
					}

					if (DevObjProj == "true") {
						DevObjProjBool = true;

					} else {
						DevObjProjBool = false;
					}
					if (ID != "") {
						dndSourceSelection = (IStructuredSelection) viewer.getSelection();
						Object[] Items = dndSourceSelection.toArray();
						for (int i = 0; i < Items.length; i++) {
							TreeObject item = (TreeObject) Items[i];
							if (item instanceof TreeParent && !ID.equals(((TreeParent) item).getFolderID())) {
								TreeParent Folder = (TreeParent) item;
								Common.moveFolderInXML(Folder.getFolderID(), ID, Folder.getTypeOfFolder(), ParentType);
							} else {
								if (item.getParent().getDevObjProject() == DevObjProjBool
										&& !ID.equals(item.getParent().getFolderID())) {
									if (item.getType() == TypeOfEntry.URL) {
										Common.addURLToXML(item.getName(), item.getDescription(),
												item.getLongDescription(), item.getTechnicalName(), ID,
												TypeOfXMLNode.urlNode, ParentType);
									} else if (item.getType() == TypeOfEntry.ADTLink) {
										Common.addURLToXML(item.getName(), item.getDescription(),
												item.getLongDescription(), item.getTechnicalName(), ID,
												TypeOfXMLNode.ADTLinkNode, ParentType);
									} else {
										Common.addObjectToXML(item.getType(), item.getName(), item.getDescription(),
												item.getLongDescription(), ID, ParentType);

									}
									Common.delObjectFromXML(item.getType(), item.getName(),
											item.getParent().getFolderID(), item.getParent().getTypeOfFolder());
								}
							}
						}
						Common.refreshViewer(viewer);
						dndSourceSelection = null;
					}
				}
			}

			@Override
			public void drop(DropTargetEvent arg0) {
				// TODO Auto-generated method stub

			}

		});

		tree.setHeaderVisible(true);
		TreeColumn columnName = new TreeColumn(tree, SWT.LEFT);
		columnName.setText("Name");
		columnName.addControlListener(columnListener);
		loadColumnSettings(columnName);
		TreeColumn columnDescr = new TreeColumn(tree, SWT.LEFT);
		columnDescr.setText("Description");
		columnDescr.addControlListener(columnListener);
		loadColumnSettings(columnDescr);
		TreeColumn ColumnID = new TreeColumn(tree, SWT.LEFT);
		ColumnID.setText("ID");
		ColumnID.addControlListener(columnListener);
		ColumnID.setWidth(0);
		TreeColumn ColumnFolderType = new TreeColumn(tree, SWT.LEFT);
		ColumnFolderType.setText("FolderType");
		ColumnFolderType.addControlListener(columnListener);
		ColumnFolderType.setWidth(0);
		TreeColumn ColumnDevObj = new TreeColumn(tree, SWT.LEFT);
		ColumnDevObj.setText("DevObjects");
		ColumnDevObj.addControlListener(columnListener);
		ColumnDevObj.setWidth(0);
		viewer.setContentProvider(new ViewContentProvider(getFolderType(), this, getViewSite()));
		viewer.setInput(getViewSite());
		viewer.setLabelProvider(new ViewLabelProvider());

		loadPluginSettings();
		//
		if (this instanceof Favorites) {
			Common.ViewerFavorites = viewer;
		} else {

			Common.ViewerFavoritesDO = viewer;
		}

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), ID);
		getSite().setSelectionProvider(viewer);
		Utils.makeActions(viewer);
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();

		// Linking with editor
		linkWithEditorAction = new Action("Link with Editor", SWT.TOGGLE) {
			@Override
			public void run() {
				toggleLinking();
			}

		};
		linkWithEditorAction.setText("Link with Editor");
		linkWithEditorAction.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED));
		getViewSite().getActionBars().getToolBarManager().add(linkWithEditorAction);
		getSite().getPage().addPartListener(linkWithEditorPartListener);
		linkWithEditorAction.setChecked(linkingActive);

		setNewPartName();
		// set up comparisor to be used in tree
		sortTable();
		Common.refreshViewer(viewer);
	}

	@Override
	public void editorActivated(IEditorPart activeEditor) {
		if (linkingActive) { // && !getViewSite().getPage().isPartVisible(this))
								// {

			if (!getLinkedEditorProject().equals(Common.getProjectName())) {

				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				IWorkbenchWindow window = page.getWorkbenchWindow();
				ISelection ADTselection = window.getSelectionService().getSelection();
				LinkedProject = ProjectUtil.getActiveAdtCoreProject(ADTselection, null, null,
						IAdtCoreProject.ABAP_PROJECT_NATURE);
				if (LinkedProject != null) {
					setLinkedEditorProject(LinkedProject.getName());
					Common.refreshViewer(viewer);
				}
			}
			return;
		}

	}

	protected void toggleLinking() {
		if (linkingActive) {
			linkingActive = false;

		} else {
			linkingActive = true;
			editorActivated(getSite().getPage().getActiveEditor());
		}
		setNewPartName();
	}

	protected void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	protected void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	protected void fillLocalPullDown(IMenuManager manager) {
		manager.add(Utils.actExportFavorites);
		manager.add(Utils.actImportFavorites);

	}

	protected void fillContextMenu(IMenuManager manager) {
		if (viewer.getSelection() instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

			try {

				TreeObject object = (TreeObject) selection.getFirstElement();

				if (object instanceof TreeParent) {
					TreeParent parent = (TreeParent) object;
					if (parent.getDevObjProject() == true) {
						manager.add(Utils.actAddFolder);
						manager.add(Utils.actAddProgram);
						manager.add(Utils.actAddClass);
						manager.add(Utils.actAddInterface);
						manager.add(Utils.actAddFunctionGroup);
						manager.add(Utils.actAddFunctionModule);
						manager.add(Utils.actAddView);
						manager.add(Utils.actAddTable);
						manager.add(Utils.actAddMessageClass);
						manager.add(Utils.actAddSearchHelp);
						manager.add(Utils.actAddADTLink);
						manager.add(Utils.actAddCDS);
						manager.add(new Separator());
					} else {
						manager.add(Utils.actAddFolder);
						manager.add(Utils.actAddTransaction);
						manager.add(Utils.actAddProgram);
						manager.add(Utils.actAddURL);
						manager.add(new Separator());
					}
					manager.add(Utils.actDelFolder);
					manager.add(new Separator());
					manager.add(Utils.actEdit);
					manager.add(new Separator());
					drillDownAdapter.addNavigationActions(manager);
					// Other plug-ins can contribute there actions here
					manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
				} else if (object instanceof TreeObject) {

					manager.add(new Separator());
					manager.add(Utils.actDelete);
					manager.add(new Separator());
					manager.add(Utils.actEdit);
					manager.add(new Separator());

					Common.addOpenInProjectMenu(manager, viewer);
					drillDownAdapter.addNavigationActions(manager);
					// Other plug-ins can contribute there actions here
					manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
				}

			} catch (Exception e) {
				// TODO: handle exception
				showMessage(e.toString());
			}
		}
	}

	protected void fillLocalToolBar(IToolBarManager manager) {

		manager.add(Utils.actAddRootFolder);

		drillDownAdapter.addNavigationActions(manager);
	}

	public void sortTable() {

		viewer.setComparator(new ViewerComparator() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {

				if (e1 instanceof TreeParent && e2 instanceof TreeParent) {
					return ((TreeParent) e1).getName().compareToIgnoreCase(((TreeParent) e2).getName());
				} else if (e1 instanceof TreeParent && e2 instanceof TreeObject) {
					return (-1);
				} else if (e1 instanceof TreeObject && e2 instanceof TreeParent) {
					return (1);
				} else if (e1 instanceof TreeObject && e2 instanceof TreeObject) {
					return ((TreeObject) e1).getName().compareToIgnoreCase(((TreeObject) e2).getName());

				} else {
					throw new IllegalArgumentException("Not comparable: " + e1 + " " + e2);
				}
			}
		});
	}

	protected void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(), "Favorites", message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	protected void loadColumnSettings(TreeColumn Column) {
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(ID);
		Column.setWidth(prefs.getInt("column_width" + Column.getText(), 300));
	}

	protected void loadPluginSettings() {
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(ID);
		try {
			prefs.sync();
		} catch (org.osgi.service.prefs.BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		linkingActive = prefs.getBoolean("linking_active", true);
		setLinkedEditorProject(prefs.get("linked_project", ""));
		LinkedProject = Common.getProjectByName(getLinkedEditorProject());
	};

	protected void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				Utils.TempLinkedEditorProject = getLinkedEditorProject();
				Utils.TempLinkedProject = LinkedProject;
				Utils.doubleClickAction.run();
			}
		});
	}

	public String getLinkedEditorProject() {
		return LinkedEditorProject;
	}

	public boolean getLinkingActive() {
		return linkingActive;
	}

	public void setLinkedEditorProject(String linkedEditorProject) {
		LinkedEditorProject = linkedEditorProject;
	};
}

package com.abapblog.favorites.superview;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
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
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.abapblog.favorites.Activator;
import com.abapblog.favorites.commands.DynamicCommandHandler;
import com.abapblog.favorites.common.Common;
import com.abapblog.favorites.common.CommonTypes.TypeOfEntry;
import com.abapblog.favorites.common.CommonTypes.TypeOfXMLAttr;
import com.abapblog.favorites.common.CommonTypes.TypeOfXMLNode;
import com.abapblog.favorites.common.NameSorting;
import com.abapblog.favorites.preferences.PreferenceConstants;
import com.abapblog.favorites.superview.labelproviders.LinkedToCellLabelProvider;
import com.abapblog.favorites.superview.labelproviders.LongDecriptionCellLabelProvider;
import com.abapblog.favorites.superview.labelproviders.NameCellLabelProvider;
import com.abapblog.favorites.tree.AFFilteredTree;
import com.abapblog.favorites.tree.ColumnControlListener;
import com.abapblog.favorites.tree.TreeExpansionListener;
import com.abapblog.favorites.tree.TreeObject;
import com.abapblog.favorites.tree.TreeParent;
import com.abapblog.favorites.xml.XMLhandler;
import com.sap.adt.project.IAdtCoreProject;
import com.sap.adt.project.ui.util.ProjectUtil;
import com.sap.adt.tools.core.AdtObjectReference;

public abstract class Superview extends ViewPart implements ILinkedWithEditorView, IFavorites {
	private static final String EXPANDED_NODES = "expanded_nodes";
	private static final String LINKED_PROJECT = "linked_project";
	private static final String LINKING_ACTIVE = "linking_active";
	private static final String COLUMN_WIDTH = "column_width";
	private static final String OBJECT_SORT = "object_sort";

	@Override
	public void dispose() {
		getSite().getPage().removePartListener(this.linkWithEditorPartListener);
		activeViewers.remove(this.viewer);
		activeFavorites.remove(this);
		super.dispose();
	}

	private static IPreferenceStore store = Activator.getDefault().getPreferenceStore();
	protected String LinkedEditorProject = "";
	protected IProject LinkedProject;
	protected IPartListener2 linkWithEditorPartListener = new LinkWithEditorPartListener(this);
	protected Action linkWithEditorAction;
	private boolean linkingActive = true;
	private NameSorting objectSorting = NameSorting.objectName;
	public TreeViewer viewer;
	protected Common Utils;
	protected DrillDownAdapter drillDownAdapter;
	protected DragSource dndSource;
	protected IStructuredSelection dndSourceSelection;
	private final Actions actions = new Actions();
	public String TempLinkedEditorProject;
	public IProject TempLinkedProject;
	private TreeViewerColumn ColumnLinkedTo;
	private TreeViewerColumn ColumnLongDescription;
	private ViewContentProvider vcp;
	protected TypeOfXMLNode FolderNode;
	private static List<TreeViewer> activeViewers = new ArrayList<>();
	private static List<IFavorites> activeFavorites = new ArrayList<>();
	private ArrayList<String> expandedNodes = new ArrayList<>();
	private ArrayList<TreeParent> expandedParentNodes = new ArrayList<>();

	public static List<TreeViewer> getActiveTreeViewers() {
		return activeViewers;
	}

	public static void refreshActiveViews() {
		getActiveTreeViewers().forEach((viewer) -> {
			refreshViewer(viewer);
		});
		getActiveFavorites().forEach((favorite) -> {
			toggleLinkingOfEditor(favorite);
			favorite.showHideLongDescriptionColumn();
			favorite.showHideLinkedToColumn();
		});
	}

	public void setExpandedNodes(ArrayList<String> expandedNodes) {
		this.expandedNodes = expandedNodes;
	}

	@Override
	public ArrayList<TreeParent> getExpandedParentNodes() {

		if (this.expandedParentNodes == null) {
			this.expandedParentNodes = new ArrayList<>();
		}
		return this.expandedParentNodes;
	}

	@Override
	public ArrayList<String> getExpandedNodes() {
		if (this.expandedNodes == null) {
			this.expandedNodes = new ArrayList<>();
		}
		return this.expandedNodes;
	}

	@Override
	public TreeViewer getTreeViewer() {
		return this.viewer;
	}

	private static void toggleLinkingOfEditor(final IFavorites favorite) {
		if (isHideOfDepProject()) {
			favorite.enableLinkingOfEditor();
		} else {
			favorite.disableLinkingOfEditor();
		}
		((Superview) favorite).setNewViewName();
	}

	public String partName;
	protected boolean controlPressed;

	public static void savePluginSettings(final IFavorites Favorite) {
		final IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(Favorite.getID());

		prefs.putBoolean(LINKING_ACTIVE, Favorite.isLinkingActive());
		prefs.put(LINKED_PROJECT, Favorite.getLinkedEditorProject());
		prefs.put(OBJECT_SORT, Favorite.getObjectSortingType());

		if (Superview.isSaveFolderExpansionState()) {
			final String[] expNodes = Favorite.getExpandedNodes().toArray(new String[0]);
			prefs.put(EXPANDED_NODES, String.join(";", expNodes));
		} else {
			prefs.put(EXPANDED_NODES, "");
		}
		try {
			// prefs are automatically flushed during a plugin's "super.stop()".
			prefs.flush();
		} catch (final org.osgi.service.prefs.BackingStoreException e) {
			// TODO write a real exception handler.
			e.printStackTrace();
		}
	}

	protected void setNewViewName() {
		if (isLinkingActive() || isHideOfDepProject() == false) {
			setPartName(this.partName);
		} else {
			setPartName(this.partName + " (" + getLinkedEditorProject() + ")");
		}
	}

	@Override
	public void createPartControl(final Composite parent) {

		final AFPatternFilter filter = new AFPatternFilter();
		final AFFilteredTree filteredTree = new AFFilteredTree(parent,
				SWT.MULTI | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.V_SCROLL, filter, true, true, this);
		final ColumnControlListener columnListener = new ColumnControlListener();
		columnListener.setID(getID());

		this.partName = getPartName();
		this.viewer = filteredTree.getViewer();

		activeViewers.add(this.viewer);
		this.drillDownAdapter = new DrillDownAdapter(this.viewer);
		final Tree tree = this.viewer.getTree();

		addDragAndDropSupport(tree);

		setTreeColumns(columnListener, tree, this.viewer);
		vcp = new ViewContentProvider(getFolderType(), this, getViewSite());
		tree.addTreeListener(new TreeExpansionListener(this));
		loadPluginSettings();
		this.viewer.setContentProvider(vcp);
		this.viewer.setInput(getViewSite());
//		this.viewer.setLabelProvider(new ViewLabelProvider());

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this.viewer.getControl(), getID());
		getSite().setSelectionProvider(this.viewer);
		this.actions.makeActions(this);
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
		setLinkingWithEditor();
		createExpandAllCollapseAllActions();
		setNewViewName();
		sortTable();
		this.viewer.setExpandedElements(getExpandedParentNodes().toArray());

	}

	private void createExpandAllCollapseAllActions() {
		final Action actExpandAll = new Action("Expand All") {
			@Override
			public void run() {
				viewer.expandAll();
			}

		};
		actExpandAll.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.ui.editors",
				"icons/full/elcl16/expandall.png"));
		actExpandAll.setToolTipText("Expand All");

		final Action actCollapseAll = new Action("Collapse All") {
			@Override
			public void run() {
				viewer.collapseAll();
			}
		};
		actCollapseAll.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_COLLAPSEALL));
		actCollapseAll.setToolTipText("Collapse All");
		final IActionBars bars = getViewSite().getActionBars();
		bars.getToolBarManager().add(actExpandAll);
		bars.getToolBarManager().add(actCollapseAll);

	}

	private void setLinkingWithEditor() {
		// Linking with editor
		this.linkWithEditorAction = new Action("Link with Editor", SWT.TOGGLE) {
			@Override
			public void run() {
				toggleLinking();
			}

		};
		this.linkWithEditorAction.setText("Link with Editor");
		this.linkWithEditorAction.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED));
		getViewSite().getActionBars().getToolBarManager().add(this.linkWithEditorAction);
		getSite().getPage().addPartListener(this.linkWithEditorPartListener);
		this.linkWithEditorAction.setChecked(isLinkingActive());
		toggleLinkingOfEditor(this);
	}

	private void addDragAndDropSupport(final Tree tree) {
		final Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
		final int operations = DND.DROP_MOVE | DND.DROP_COPY;

		this.viewer.addDragSupport(operations, types, new DragSourceListener() {
			@Override
			public void dragStart(final DragSourceEvent event) {
				final TreeItem[] selection = tree.getSelection();
				if (selection.length > 0) {
					event.doit = true;
				} else {
					event.doit = false;
				}
			}

			@Override
			public void dragSetData(final DragSourceEvent event) {
				Superview.this.dndSourceSelection = (IStructuredSelection) Superview.this.viewer.getSelection();
				event.data = "Data"; // To not cause org.eclipse.swt.SWTException: Data does not have correct format
										// for type

			}

			@Override
			public void dragFinished(final DragSourceEvent event) {

			}

		});

		this.viewer.addDropSupport(operations, types, new DropTargetListener() {
			@Override
			public void dragEnter(final DropTargetEvent event) {
				if (event.detail == DND.DROP_DEFAULT) {
					if ((event.operations & DND.DROP_COPY) != 0) {
						event.detail = DND.DROP_COPY;
					} else {
						event.detail = DND.DROP_NONE;
					}
				}
			}

			@Override
			public void dragOver(final DropTargetEvent event) {
				event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL | DND.FEEDBACK_EXPAND;
			}

			@Override
			public void dragOperationChanged(final DropTargetEvent event) {

			}

			@Override
			public void dragLeave(final DropTargetEvent event) {

			}

			@Override
			public void dropAccept(final DropTargetEvent event) {
			}

			@Override
			public void drop(final DropTargetEvent event) {
				String ID = "";
				Boolean DevObjProjBool = false;
				TypeOfXMLNode FolderType = TypeOfXMLNode.folderNode;
				Boolean Copy = false;
				TreeObject Target;

				if (event.detail == DND.DROP_MOVE || event.detail == DND.DROP_COPY) {
					if (event.detail == DND.DROP_COPY) {
						Copy = true;
					}
					;
					try {
						Target = (TreeObject) event.item.getData();
					} catch (Exception e) {
						DropTarget dropTarget = (DropTarget) event.getSource();
						dropTarget.getControl();
						ViewContentProvider vcp = (ViewContentProvider) viewer.getContentProvider();
						Target = vcp.getRoot();
					}

					if (Target instanceof TreeParent) {
						TreeParent folder = (TreeParent) Target;
						ID = folder.getFolderID();
						DevObjProjBool = folder.getDevObjProject();
						FolderType = folder.getTypeOfFolder();
					}

//					if (ID != "" && ) {
//						// Means we are in the Top Node
//						return;
//					}

					if (ID.equals("")) {
						ID = Target.getParent().getFolderID();
						FolderType = Target.getParent().getTypeOfFolder();
						DevObjProjBool = Target.getParent().getDevObjProject();
					}

					if (DropItemsFromProjectExplorer(ID, FolderType) == false) {

					}
					{

						dropItemsFromFavorites(ID, DevObjProjBool, FolderType, Copy);
					}
				}

			}

			private Boolean DropItemsFromProjectExplorer(final String ID, final TypeOfXMLNode ParentType) {
				final LocalSelectionTransfer LST = LocalSelectionTransfer.getTransfer();
				final ISelection selection = LST.getSelection();
				if (selection instanceof IStructuredSelection) {
					final Object[] Items = ((IStructuredSelection) selection).toArray();
					for (final Object item2 : Items) {
						final IAdaptable item = (IAdaptable) item2;
						try {
							final AdtObjectReference AdtRef = item.getAdapter(AdtObjectReference.class);
							final String objectType = AdtRef.getType();
							final String objectName = AdtRef.getName();
							final TypeOfEntry typeOfEntry = Common.getTypeOfEntryFromSAPType(objectType);
							XMLhandler.addObjectToXML(typeOfEntry, objectName.toUpperCase(), "", "", "", ID, ParentType,
									"");

						} catch (final SecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (final IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					Superview.refreshActiveViews();
					return true;
				}
				return false;
			}

			private void dropItemsFromFavorites(final String ID, final Boolean DevObjProjBool,
					final TypeOfXMLNode ParentType, final Boolean Copy) {
				if (ID != "") {
					Superview.this.dndSourceSelection = (IStructuredSelection) Superview.this.viewer.getSelection();
					final Object[] Items = Superview.this.dndSourceSelection.toArray();
					for (final Object item2 : Items) {
						final TreeObject item = (TreeObject) item2;
						if (item instanceof TreeParent) {
							if (ID.equals(((TreeParent) item).getFolderID())) {
								break;
							}
							final TreeParent Folder = (TreeParent) item;
							if (Copy == true) {
								XMLhandler.copyFolderInXML(Folder.getFolderID(), ID, Folder.getTypeOfFolder(),
										ParentType);
							} else {
								XMLhandler.moveFolderInXML(Folder.getFolderID(), ID, Folder.getTypeOfFolder(),
										ParentType);
							}
						} else {
							if (item.getParent().getDevObjProject() == DevObjProjBool
									&& !ID.equals(item.getParent().getFolderID())) {
								XMLhandler.addObjectToXML(item.getType(), item.getName(), item.getDescription(),
										item.getLongDescription(), item.getTechnicalName(), ID, ParentType,
										item.getCommandID());
								if (Copy == false) {
									XMLhandler.delObjectFromXML(item.getType(), item.getName(),
											item.getParent().getFolderID(), item.getParent().getTypeOfFolder());
								}
							}
						}
					}
					Superview.refreshViewer(Superview.this.viewer);
					Superview.this.dndSourceSelection = null;
				}
			}

		});
	}

	@Override
	public void enableLinkingOfEditor() {
		if (this.linkWithEditorAction != null) {
			this.linkWithEditorAction.setEnabled(true);
		}
	}

	@Override
	public void disableLinkingOfEditor() {
		this.linkWithEditorAction.setEnabled(false);
	}

	private void setTreeColumns(final ColumnControlListener columnListener, final Tree tree, TreeViewer viewer) {
		tree.setHeaderVisible(true);
		TreeViewerColumn columnName = new TreeViewerColumn(viewer, SWT.LEFT);
		columnName.getColumn().setText("Name");
		columnName.getColumn().addControlListener(columnListener);
		columnName.getColumn().setMoveable(false);
		columnName.setLabelProvider(new NameCellLabelProvider());
		loadColumnSettings(columnName.getColumn());

		ColumnLinkedTo = new TreeViewerColumn(viewer, SWT.LEFT);
		ColumnLinkedTo.getColumn().setText("Linked To");
		ColumnLinkedTo.getColumn().addControlListener(columnListener);
		ColumnLinkedTo.getColumn().setMoveable(true);
		showHideLinkedToColumn();
		ColumnLinkedTo.setLabelProvider(new LinkedToCellLabelProvider());

		ColumnLongDescription = new TreeViewerColumn(viewer, SWT.LEFT);
		ColumnLongDescription.getColumn().setText("Long text");
		ColumnLongDescription.getColumn().addControlListener(columnListener);
		ColumnLongDescription.getColumn().setMoveable(true);
		showHideLongDescriptionColumn();
		ColumnLongDescription.setLabelProvider(new LongDecriptionCellLabelProvider());

		ColumnViewerToolTipSupport.enableFor(viewer);
	}

	@Override
	public void showHideLongDescriptionColumn() {
		loadColumnSettings(ColumnLongDescription.getColumn());
		if (store.getBoolean(PreferenceConstants.P_SHOW_LONG_TEXT_COLUMN)) {
			if (ColumnLongDescription.getColumn().getWidth() == 0)
				ColumnLongDescription.getColumn().setWidth(300);
			ColumnLongDescription.getColumn().setResizable(true);
		} else {
			ColumnLongDescription.getColumn().setWidth(0);
			ColumnLongDescription.getColumn().setResizable(false);
		}
	}

	@Override
	public void showHideLinkedToColumn() {
		loadColumnSettings(ColumnLinkedTo.getColumn());
		if (store.getBoolean(PreferenceConstants.P_SHOW_LINKED_TO_COLUMN)) {
			if (ColumnLinkedTo.getColumn().getWidth() == 0)
				ColumnLinkedTo.getColumn().setWidth(100);
			ColumnLinkedTo.getColumn().setResizable(true);
		} else {
			ColumnLinkedTo.getColumn().setWidth(0);
			ColumnLinkedTo.getColumn().setResizable(false);
		}
	}

	@Override
	public void editorActivated(final IEditorPart activeEditor) {

		if (isLinkingActive() && isHideOfDepProject()) {
			if (!getLinkedEditorProject().equals(Common.getProjectName())) {

				final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				final IWorkbenchWindow window = page.getWorkbenchWindow();
				final ISelection ADTselection = window.getSelectionService().getSelection();
				this.LinkedProject = ProjectUtil.getActiveAdtCoreProject(ADTselection, null, null,
						IAdtCoreProject.ABAP_PROJECT_NATURE);
				if (this.LinkedProject != null) {
					setLinkedEditorProject(this.LinkedProject.getName());
					Superview.refreshViewer(this.viewer);
					this.viewer.setExpandedElements(getExpandedParentNodes().toArray());
				}
			}
		}

	}

	public static boolean isHideOfDepProject() {
		return store.getBoolean(PreferenceConstants.P_HIDE_PROJECT_DEP_FOLDERS);
	}

	public static boolean isSaveFolderExpansionState() {
		return store.getBoolean(PreferenceConstants.P_KEEP_THE_EXPANDED_FOLDERS_AT_START);
	}

	protected void toggleLinking() {
		if (isLinkingActive()) {
			setLinkingActive(false);

		} else {
			setLinkingActive(true);
			editorActivated(getSite().getPage().getActiveEditor());
		}
		setNewViewName();
	}

	protected void hookContextMenu() {
		final MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(manager -> fillContextMenu(manager));
		final Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
		getSite().setSelectionProvider(viewer);
	}

	protected void contributeToActionBars() {
		final IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	protected void fillLocalPullDown(final IMenuManager manager) {
		manager.add(this.actions.actExportFavorites);
		manager.add(this.actions.actImportFavorites);
		manager.add(this.actions.sortObjectByName);
		manager.add(this.actions.sortObjectByDescription);

	}

	protected void fillContextMenu(final IMenuManager manager) {
		if (this.viewer.getSelection() instanceof IStructuredSelection) {
			final IStructuredSelection selection = (IStructuredSelection) this.viewer.getSelection();

			try {

				final TreeObject object = (TreeObject) selection.getFirstElement();

				if (object instanceof TreeParent) {
					final TreeParent parent = (TreeParent) object;
					if (parent.getDevObjProject()) {
						manager.add(this.actions.actAddFolder);
						manager.add(this.actions.actAddProgram);
						manager.add(this.actions.actAddClass);
						manager.add(this.actions.actAddInterface);
						manager.add(this.actions.actAddFunctionGroup);
						manager.add(this.actions.actAddFunctionModule);
						manager.add(this.actions.actAddView);
						manager.add(this.actions.actAddTable);
						manager.add(this.actions.actAddMessageClass);
						manager.add(this.actions.actAddSearchHelp);
						manager.add(this.actions.actAddADTLink);
						manager.add(this.actions.actAddCDS);
						manager.add(this.actions.actAddPackage);
						manager.add(new Separator());
						manager.remove(IWorkbenchActionConstants.MB_ADDITIONS);
					} else {
						manager.add(this.actions.actAddFolder);
						manager.add(this.actions.actAddTransaction);
						manager.add(this.actions.actAddProgram);
						manager.add(this.actions.actAddURL);
						manager.add(new Separator());
					}
					manager.add(this.actions.actDelFolder);
					if (parent.getParent().getFolderID() != "root")
						manager.add(this.actions.actMoveFolderToRoot);
					manager.add(this.actions.actMoveToFolder);
					manager.add(new Separator());
					manager.add(this.actions.actEdit);
					manager.add(new Separator());
					this.drillDownAdapter.addNavigationActions(manager);
					manager.remove(IWorkbenchActionConstants.MB_ADDITIONS);
				} else if (object instanceof TreeObject) {

					manager.add(new Separator());
					manager.add(this.actions.actDelete);
					manager.add(this.actions.actMoveToFolder);
					manager.add(new Separator());
					manager.add(this.actions.actEdit);
					manager.add(new Separator());
					manager.add(this.actions.actCopyToClipboard);
					manager.add(new Separator());

					Actions.addOpenInProjectMenu(manager, this.viewer);
					this.drillDownAdapter.addNavigationActions(manager);
					manager.remove(IWorkbenchActionConstants.MB_ADDITIONS);
				}

			} catch (final Exception e) {
				showMessage(e.toString());
			}
		}
	}

	protected void fillLocalToolBar(final IToolBarManager manager) {

		manager.add(this.actions.actAddRootFolder);

		this.drillDownAdapter.addNavigationActions(manager);
	}

	public void sortTable() {

		this.viewer.setComparator(new ViewerComparator() {
			@Override
			public int compare(final Viewer viewer, final Object e1, final Object e2) {

				if (e1 instanceof TreeParent && e2 instanceof TreeParent) {
					String comparisonString1 = ((TreeParent) e1).getName() + ' ' + ((TreeParent) e1).getDescription()
							+ ' ' + ((TreeParent) e1).getFolderID();
					String comparisonString2 = ((TreeParent) e2).getName() + ' ' + ((TreeParent) e2).getDescription()
							+ ' ' + ((TreeParent) e2).getFolderID();
					return comparisonString1.compareToIgnoreCase(comparisonString2);
				} else if (e1 instanceof TreeParent && e2 instanceof TreeObject) {
					return -1;
				} else if (e1 instanceof TreeObject && e2 instanceof TreeParent) {
					return 1;
				} else if (e1 instanceof TreeObject && e2 instanceof TreeObject) {
					String descriptionFirst = ((TreeObject) e1).getDescription();
					String descriptionSecond = ((TreeObject) e2).getDescription();
					String nameFirst = ((TreeObject) e1).getName();
					String nameSecond = ((TreeObject) e2).getName();
					switch (objectSorting) {
					case objectDescription:
						if (descriptionFirst != descriptionSecond) {
							return descriptionFirst.compareToIgnoreCase(descriptionSecond);
						} else {
							return nameFirst.compareToIgnoreCase(nameSecond);
						}
					case objectName:
						if (nameFirst != nameSecond) {
							return nameFirst.compareToIgnoreCase(nameSecond);
						} else {
							return descriptionFirst.compareToIgnoreCase(descriptionSecond);
						}
					default:
						if (nameFirst != nameSecond) {
							return nameFirst.compareToIgnoreCase(nameSecond);
						} else {
							return descriptionFirst.compareToIgnoreCase(descriptionSecond);
						}
					}

				} else {
					throw new IllegalArgumentException("Not comparable: " + e1 + " " + e2);
				}
			}
		});
	}

	protected void showMessage(final String message) {
		MessageDialog.openInformation(this.viewer.getControl().getShell(), "Favorites", message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		this.viewer.getControl().setFocus();
	}

	protected void loadColumnSettings(final TreeColumn Column) {
		final IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(getID());
		Column.setWidth(prefs.getInt(COLUMN_WIDTH + Column.getText(), 300));
	}

	protected void loadPluginSettings() {
		final IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(getID());
		try {
			prefs.sync();
		} catch (final org.osgi.service.prefs.BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setLinkingActive(prefs.getBoolean(LINKING_ACTIVE, true));
		setLinkedEditorProject(prefs.get(LINKED_PROJECT, ""));
		setSortingTypeOfObjects(prefs.get(OBJECT_SORT, ""));
		expandTreeNodes(prefs);
		this.LinkedProject = Common.getProjectByName(getLinkedEditorProject());
	}

	public void setSortingTypeOfObjects(String sortTypeString) {

		if (sortTypeString.equals(NameSorting.objectDescription.toString())) {
			this.objectSorting = NameSorting.objectDescription;

		} else if (sortTypeString.equals(NameSorting.objectName.toString())) {
			this.objectSorting = NameSorting.objectName;
		}
	}

	private void expandTreeNodes(final IEclipsePreferences prefs) {
		String expandedNodesString = "";
		String[] expNodes = null;
		expandedNodesString = prefs.get(EXPANDED_NODES, expandedNodesString);
		if (expandedNodesString != "") {
			expNodes = expandedNodesString.split(";");
		}
		if (expNodes != null) {
			this.expandedNodes = new ArrayList<>(Arrays.asList(expNodes));
		}
	}

	protected void hookDoubleClickAction() {
		/**
		 * addDoubleClickListener unfortunately does not supply the currently pressed
		 * modifier keys in the event object so as a work around we register Mouse and
		 * Keyboard listeners
		 */
		new TreeSelectionEventAdapter(this, this.actions.actDoubleClick);
	}

	@Override
	public String getLinkedEditorProject() {

		return this.LinkedEditorProject;

	}

	@Override
	public boolean isLinkingActive() {
		return this.linkingActive;
	}

	public void setLinkingActive(final boolean linkingActive) {
		this.linkingActive = linkingActive;
	}

	public void setLinkedEditorProject(final String linkedEditorProject) {
		this.LinkedEditorProject = linkedEditorProject;
	}

	public static TreeParent createTreeNodes(final TypeOfXMLNode FolderXMLNode, final IFavorites Favorite,
			final Boolean selectFolderDialog) {

		String LinkedEditorProject = "";
		DynamicCommandHandler.commandsLink.clear();
		Favorite.getExpandedParentNodes().clear();

		if (isHideOfDepProject() == true && Favorite != null) {
			LinkedEditorProject = Favorite.getLinkedEditorProject();
		}

		final TreeParent invisibleRoot = new TreeParent("", "", true, "", "", Favorite, false, "root");

		final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc;
			try {
				doc = dBuilder.parse(XMLhandler.getFavFile());

				doc.getDocumentElement().normalize();

				final NodeList nList = doc.getDocumentElement().getChildNodes();

				for (int temp = 0; temp < nList.getLength(); temp++) {

					final Node nNode = nList.item(temp);

					if (nNode.getNodeType() == Node.ELEMENT_NODE) {

						final Element eElement = (Element) nNode;
						createSubNodes(FolderXMLNode, eElement, invisibleRoot, LinkedEditorProject, Favorite,
								selectFolderDialog);
					}
				}

				final DOMSource source = new DOMSource(doc);

				final TransformerFactory transformerFactory = TransformerFactory.newInstance();
				final Transformer transformer = transformerFactory.newTransformer();
				final StreamResult result = new StreamResult(XMLhandler.getFavFile().getPath());
				transformer.transform(source, result);
				return invisibleRoot;
			} catch (final SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (final TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (final TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (final ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static void createSubNodes(final TypeOfXMLNode folderXMLNode, final Element subNode,
			final TreeParent subNodeParent, final String linkedEditorProject, final IFavorites favorite,
			final Boolean selectFolderDialog) {

		if (subNode.getNodeName().equalsIgnoreCase(folderXMLNode.toString())) {

			final TreeParent parent = new TreeParent(subNode.getAttribute(TypeOfXMLAttr.name.toString()),
					subNode.getAttribute(TypeOfXMLAttr.description.toString()),
					Boolean.parseBoolean(subNode.getAttribute(TypeOfXMLAttr.projectIndependent.toString())),
					subNode.getAttribute(TypeOfXMLAttr.project.toString()),
					subNode.getAttribute(TypeOfXMLAttr.longDescription.toString()), favorite,
					Boolean.parseBoolean(subNode.getAttribute(TypeOfXMLAttr.devObjFolder.toString())),
					subNode.getAttribute(TypeOfXMLAttr.folderID.toString()));

			String FolderID = "";
			FolderID = subNode.getAttribute(TypeOfXMLAttr.folderID.toString());
			if (FolderID == "") {
				FolderID = parent.getFolderID();
				subNode.setAttribute(TypeOfXMLAttr.folderID.toString(), FolderID);
			}

			if (favorite.getExpandedNodes().contains(FolderID)) {
				favorite.getExpandedParentNodes().add(parent);
			}

			final boolean projectIsIndependent = Boolean
					.parseBoolean(subNode.getAttribute(TypeOfXMLAttr.projectIndependent.toString()));
			if (projectIsIndependent == false && isHideOfDepProject() == true) {
				String ProjectName = linkedEditorProject;
				if (ProjectName.equals("")) {
					ProjectName = Common.getProjectName();
				}

				if (!parent.getProject().equals(ProjectName)) {
					return;
				}
			}

			subNodeParent.addChild(parent);

			final NodeList Children = subNode.getChildNodes();

			for (int tempChild = 0; tempChild < Children.getLength(); tempChild++) {

				final Node nNodeChild = Children.item(tempChild);

				if (nNodeChild.getNodeType() == Node.ELEMENT_NODE) {

					final Element eElementChild = (Element) nNodeChild;

					if (eElementChild.getNodeName().equalsIgnoreCase(folderXMLNode.toString())) {
						createSubNodes(folderXMLNode, eElementChild, parent, linkedEditorProject, favorite,
								selectFolderDialog);
					}

					else {
						if (selectFolderDialog == false) {
							String childName = eElementChild.getAttribute(TypeOfXMLAttr.name.toString());
							if (XMLhandler.isXMLNodeNameToUpper(eElementChild.getTagName())) {
								childName = childName.toUpperCase();
							}
							final TreeObject child = new TreeObject(childName,
									XMLhandler.getEntryTypeFromXMLNode(nNodeChild.getNodeName()),
									eElementChild.getAttribute(TypeOfXMLAttr.description.toString()),
									eElementChild.getAttribute(TypeOfXMLAttr.technicalName.toString()),
									eElementChild.getAttribute(TypeOfXMLAttr.longDescription.toString()), favorite,
									eElementChild.getAttribute(TypeOfXMLAttr.commandID.toString()));
							parent.addChild(child);
						}
					}
				}
			}
		}

	}

	public static void refreshViewer(final TreeViewer viewer) {

		if (viewer != null) {

			AFFilteredTree filteredTree = (AFFilteredTree) viewer.getControl().getParent().getParent();
			List<TreeParent> expandedFolders = filteredTree.getFavorite().getExpandedParentNodes();

			Object[] expandedElements = viewer.getExpandedElements();
			viewer.getControl().setRedraw(false);
			final Object ContentProvider = viewer.getContentProvider();
			try {
				final java.lang.reflect.Method initialize = ContentProvider.getClass().getMethod("initialize");
				initialize.invoke(ContentProvider);
			} catch (final Exception e) {

			}

			for (int i = 0; i < expandedElements.length; i++) {
				if (expandedElements[i] instanceof TreeParent) {
					TreeParent newFolderObject = ((ViewContentProvider) viewer.getContentProvider())
							.getFolderById(((TreeParent) expandedElements[i]).getFolderID());
					if (newFolderObject != null) {
						expandedFolders.add(newFolderObject);
					}
				}

			}
			;

			viewer.refresh();
			viewer.setExpandedElements(expandedFolders.toArray());
			viewer.getControl().setRedraw(true);
		}
	}

	public static List<IFavorites> getActiveFavorites() {
		return activeFavorites;
	}

	public static void addFavoritesToActive(final IFavorites Favorite) {
		activeFavorites.add(Favorite);
	}

	@Override
	public String getObjectSortingType() {
		return objectSorting.toString();
	}
}

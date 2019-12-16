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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.abapblog.favorites.Activator;
import com.abapblog.favorites.common.Common;
import com.abapblog.favorites.common.CommonTypes.TypeOfEntry;
import com.abapblog.favorites.common.CommonTypes.TypeOfXMLAttr;
import com.abapblog.favorites.common.CommonTypes.TypeOfXMLNode;
import com.abapblog.favorites.preferences.PreferenceConstants;
import com.abapblog.favorites.tree.ColumnControlListener;
import com.abapblog.favorites.tree.TreeExpansionListener;
import com.abapblog.favorites.tree.TreeObject;
import com.abapblog.favorites.tree.TreeParent;
import com.abapblog.favorites.xml.XMLhandler;
import com.sap.adt.project.IAdtCoreProject;
import com.sap.adt.project.ui.util.ProjectUtil;
import com.sap.adt.tools.core.AdtObjectReference;

public abstract class Superview extends ViewPart implements ILinkedWithEditorView, IFavorites {
	@Override
	public void dispose() {
		getSite().getPage().removePartListener(this.linkWithEditorPartListener);
		super.dispose();
	}

	private static IPreferenceStore store = Activator.getDefault().getPreferenceStore();
	protected String LinkedEditorProject = "";
	protected IProject LinkedProject;
	protected IPartListener2 linkWithEditorPartListener = new LinkWithEditorPartListener(this);
	protected Action linkWithEditorAction;
	private boolean linkingActive = true;
	public TreeViewer viewer;
	protected Common Utils;
	protected DrillDownAdapter drillDownAdapter;
	protected DragSource dndSource;
	protected IStructuredSelection dndSourceSelection;
	private final Actions actions = new Actions();
	public String TempLinkedEditorProject;
	public IProject TempLinkedProject;
	private ArrayList<String> expandedNodes;
	private ArrayList<TreeParent> expandedParentNodes;

	protected TypeOfXMLNode FolderNode;
	private static List<TreeViewer> activeViewers = new ArrayList<>();
	private static List<IFavorites> activeFavorites = new ArrayList<>();

	public static List<TreeViewer> getActiveTreeViewers() {
		return activeViewers;
	}

	public static void refreshActiveViews() {
		getActiveTreeViewers().forEach((viewer) -> {
			refreshViewer(viewer);
		});
		getActiveFavorites().forEach((favorite) -> {
			toggleLinkingOfEditor(favorite);
		});
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

		prefs.putBoolean("linking_active", Favorite.isLinkingActive());
		prefs.put("linked_project", Favorite.getLinkedEditorProject());

		if (Superview.isSaveFolderExpansionState()) {
			final String[] expNodes = Favorite.getExpandedNodes().toArray(new String[0]);
			prefs.put("expanded_nodes", String.join(";", expNodes));
		} else {
			prefs.put("expanded_nodes", "");
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
		final FilteredTree filteredTree = new FilteredTree(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL, filter,
				true);
		final ColumnControlListener columnListener = new ColumnControlListener();
		columnListener.setID(getID());

		this.partName = getPartName();
		this.viewer = filteredTree.getViewer();

		activeViewers.add(this.viewer);
		this.drillDownAdapter = new DrillDownAdapter(this.viewer);
		final Tree tree = this.viewer.getTree();
		tree.addTreeListener(new TreeExpansionListener(this));
		addDragAndDropSupport(tree);

		setTreeColumns(columnListener, tree);
		this.viewer.setContentProvider(new ViewContentProvider(getFolderType(), this, getViewSite()));
		this.viewer.setInput(getViewSite());
		this.viewer.setLabelProvider(new ViewLabelProvider());

		loadPluginSettings();
		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this.viewer.getControl(), getID());
		getSite().setSelectionProvider(this.viewer);
		this.actions.makeActions(this);
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
		setLinkingWithEditor();
		setNewViewName();
		sortTable();
		refreshViewer(this.viewer);
		this.viewer.setExpandedElements(getExpandedParentNodes().toArray());

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
		final int operations = DND.DROP_MOVE;

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
				event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
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
				if (event.detail == DND.DROP_MOVE) {

					final TreeItem Target = (TreeItem) event.item;
					String ID = Target.getText(2);
					String FolderType = Target.getText(3);
					String DevObjProj = Target.getText(4);
					Boolean DevObjProjBool;
					if (ID != "" && FolderType == ID) {
						// Means we are in the Top Node
						return;
					}
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

					if (DropItemsFromProjectExplorer(ID, ParentType) == false) {

					}
					{

						dropItemsFromFavorites(ID, DevObjProjBool, ParentType);
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
							XMLhandler.addObjectToXML(typeOfEntry, objectName.toUpperCase(), "", "", "", ID,
									ParentType);

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
					final TypeOfXMLNode ParentType) {
				if (ID != "") {
					Superview.this.dndSourceSelection = (IStructuredSelection) Superview.this.viewer.getSelection();
					final Object[] Items = Superview.this.dndSourceSelection.toArray();
					for (final Object item2 : Items) {
						final TreeObject item = (TreeObject) item2;
						if (item instanceof TreeParent && !ID.equals(((TreeParent) item).getFolderID())) {
							final TreeParent Folder = (TreeParent) item;
							XMLhandler.moveFolderInXML(Folder.getFolderID(), ID, Folder.getTypeOfFolder(), ParentType);
						} else {
							if (item.getParent().getDevObjProject() == DevObjProjBool
									&& !ID.equals(item.getParent().getFolderID())) {
//								if (item.getType() == TypeOfEntry.URL) {
//									XMLhandler.addURLToXML(item.getName(), item.getDescription(),
//											item.getLongDescription(), item.getTechnicalName(), ID,
//											TypeOfXMLNode.urlNode, ParentType);
//								} else if (item.getType() == TypeOfEntry.ADTLink) {
//									XMLhandler.addURLToXML(item.getName(), item.getDescription(),
//											item.getLongDescription(), item.getTechnicalName(), ID,
//											TypeOfXMLNode.ADTLinkNode, ParentType);
//								} else {
//									XMLhandler.addObjectToXML(item.getType(), item.getName(), item.getDescription(),
//											item.getLongDescription(), ID, ParentType);
//
//								}
								XMLhandler.addObjectToXML(item.getType(), item.getName(), item.getDescription(),
										item.getLongDescription(), item.getTechnicalName(), ID, ParentType);
								XMLhandler.delObjectFromXML(item.getType(), item.getName(),
										item.getParent().getFolderID(), item.getParent().getTypeOfFolder());
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

	private void setTreeColumns(final ColumnControlListener columnListener, final Tree tree) {
		tree.setHeaderVisible(true);
		final TreeColumn columnName = new TreeColumn(tree, SWT.LEFT);
		columnName.setText("Name");
		columnName.addControlListener(columnListener);
		loadColumnSettings(columnName);
		final TreeColumn columnDescr = new TreeColumn(tree, SWT.LEFT);
		columnDescr.setText("Description");
		columnDescr.addControlListener(columnListener);
		loadColumnSettings(columnDescr);
		final TreeColumn ColumnID = new TreeColumn(tree, SWT.LEFT);
		ColumnID.setText("ID");
		ColumnID.addControlListener(columnListener);
		ColumnID.setWidth(0);
		ColumnID.setResizable(false);
		final TreeColumn ColumnFolderType = new TreeColumn(tree, SWT.LEFT);
		ColumnFolderType.setText("FolderType");
		ColumnFolderType.addControlListener(columnListener);
		ColumnFolderType.setWidth(0);
		ColumnFolderType.setResizable(false);
		final TreeColumn ColumnDevObj = new TreeColumn(tree, SWT.LEFT);
		ColumnDevObj.setText("DevObjects");
		ColumnDevObj.addControlListener(columnListener);
		ColumnDevObj.setWidth(0);
		ColumnDevObj.setResizable(false);
		final TreeColumn ColumnLinkedTo = new TreeColumn(tree, SWT.LEFT);
		ColumnLinkedTo.setText("Linked To");
		ColumnLinkedTo.addControlListener(columnListener);
		loadColumnSettings(ColumnLinkedTo);
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
			return;
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
		final Menu menu = menuMgr.createContextMenu(this.viewer.getControl());
		this.viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, this.viewer);
	}

	protected void contributeToActionBars() {
		final IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	protected void fillLocalPullDown(final IMenuManager manager) {
		manager.add(this.actions.actExportFavorites);
		manager.add(this.actions.actImportFavorites);

	}

	protected void fillContextMenu(final IMenuManager manager) {
		if (this.viewer.getSelection() instanceof IStructuredSelection) {
			final IStructuredSelection selection = (IStructuredSelection) this.viewer.getSelection();

			try {

				final TreeObject object = (TreeObject) selection.getFirstElement();

				if (object instanceof TreeParent) {
					final TreeParent parent = (TreeParent) object;
					if (parent.getDevObjProject() == true) {
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
					} else {
						manager.add(this.actions.actAddFolder);
						manager.add(this.actions.actAddTransaction);
						manager.add(this.actions.actAddProgram);
						manager.add(this.actions.actAddURL);
						manager.add(new Separator());
					}
					manager.add(this.actions.actDelFolder);
					manager.add(new Separator());
					manager.add(this.actions.actEdit);
					manager.add(new Separator());
					this.drillDownAdapter.addNavigationActions(manager);
					// Other plug-ins can contribute there actions here
					manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
				} else if (object instanceof TreeObject) {

					manager.add(new Separator());
					manager.add(this.actions.actDelete);
					manager.add(new Separator());
					manager.add(this.actions.actEdit);
					manager.add(new Separator());
					manager.add(this.actions.actCopyToClipboard);
					manager.add(new Separator());

					Actions.addOpenInProjectMenu(manager, this.viewer);
					this.drillDownAdapter.addNavigationActions(manager);
					// Other plug-ins can contribute there actions here
					manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
				}

			} catch (final Exception e) {
				// TODO: handle exception
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
					return ((TreeParent) e1).getName().compareToIgnoreCase(((TreeParent) e2).getName());
				} else if (e1 instanceof TreeParent && e2 instanceof TreeObject) {
					return -1;
				} else if (e1 instanceof TreeObject && e2 instanceof TreeParent) {
					return 1;
				} else if (e1 instanceof TreeObject && e2 instanceof TreeObject) {
					return ((TreeObject) e1).getName().compareToIgnoreCase(((TreeObject) e2).getName());

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
		Column.setWidth(prefs.getInt("column_width" + Column.getText(), 300));
	}

	protected void loadPluginSettings() {
		final IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(getID());
		try {
			prefs.sync();
		} catch (final org.osgi.service.prefs.BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setLinkingActive(prefs.getBoolean("linking_active", true));
		setLinkedEditorProject(prefs.get("linked_project", ""));
		expandTreeNodes(prefs);
		this.LinkedProject = Common.getProjectByName(getLinkedEditorProject());
	}

	private void expandTreeNodes(final IEclipsePreferences prefs) {
		String expandedNodesString = "";
		String[] expNodes = null;
		expandedNodesString = prefs.get("expanded_nodes", expandedNodesString);
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
									eElementChild.getAttribute(TypeOfXMLAttr.longDescription.toString()), favorite);
							parent.addChild(child);
						}
					}
				}
			}
		}

	}

	public static void refreshViewer(final TreeViewer viewer) {

		if (viewer != null) {

			final Object ContentProvider = viewer.getContentProvider();
			try {
				final java.lang.reflect.Method initialize = ContentProvider.getClass().getMethod("initialize");
				initialize.invoke(ContentProvider);
			} catch (final Exception e) {

			}
			viewer.refresh();
		}
	}

	public static List<IFavorites> getActiveFavorites() {
		return activeFavorites;
	}

	public static void addFavoritesToActive(final IFavorites Favorite) {
		activeFavorites.add(Favorite);
	}
}

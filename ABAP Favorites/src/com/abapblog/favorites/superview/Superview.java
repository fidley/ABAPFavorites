package com.abapblog.favorites.superview;

import java.io.IOException;
import java.util.ArrayList;
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

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
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
import com.abapblog.favorites.tree.TreeObject;
import com.abapblog.favorites.tree.TreeParent;
import com.abapblog.favorites.xml.XMLhandler;
import com.sap.adt.project.IAdtCoreProject;
import com.sap.adt.project.ui.util.ProjectUtil;

public abstract class Superview extends ViewPart implements ILinkedWithEditorView, IFavorites {
 @Override
public void dispose() {
	 getSite().getPage().removePartListener(linkWithEditorPartListener);
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
	private Actions actions = new Actions();
	public String TempLinkedEditorProject;
	public IProject TempLinkedProject;

	protected TypeOfXMLNode FolderNode;
	private static List<TreeViewer> activeViewers = new ArrayList<TreeViewer>();
	private static List<IFavorites> activeFavorites = new ArrayList<IFavorites>();

	public static List<TreeViewer> getActiveTreeViewers() {
		return activeViewers;
	}

	public static void refreshActiveViews() {
		getActiveTreeViewers().forEach((viewer) -> {
			refreshViewer(viewer);
		});
		getActiveFavorites().forEach((favorite)-> {
			toggleLinkingOfEditor(favorite);
		});
	}

	private static void toggleLinkingOfEditor(IFavorites favorite) {
		if(isHideOfDepProject()) {
			favorite.enableLinkingOfEditor();
		}
		else
		{
			favorite.disableLinkingOfEditor();
		}
		((Superview) favorite).setNewViewName();
	}

	public String partName;

	public static void savePluginSettings(IFavorites Favorite) {
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(Favorite.getID());

		prefs.putBoolean("linking_active", Favorite.isLinkingActive());
		prefs.put("linked_project", Favorite.getLinkedEditorProject());

		try {
			// prefs are automatically flushed during a plugin's "super.stop()".
			prefs.flush();
		} catch (org.osgi.service.prefs.BackingStoreException e) {
			// TODO write a real exception handler.
			e.printStackTrace();
		}
	}

	protected void setNewViewName() {
		if (isLinkingActive()||isHideOfDepProject()==false) {
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
		columnListener.setID(getID());

		partName = getPartName();
		viewer = filteredTree.getViewer();

		activeViewers.add(viewer);
		drillDownAdapter = new DrillDownAdapter(viewer);
		Tree tree = viewer.getTree();

		addDragAndDropSupport(tree);

		setTreeColumns(columnListener, tree);
		viewer.setContentProvider(new ViewContentProvider(getFolderType(), this, getViewSite()));
		viewer.setInput(getViewSite());
		viewer.setLabelProvider(new ViewLabelProvider());

		loadPluginSettings();
		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), getID());
		getSite().setSelectionProvider(viewer);
		actions.makeActions(this);
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
		setLinkingWithEditor();
		setNewViewName();
		sortTable();
		refreshViewer(viewer);
	}

	private void setLinkingWithEditor() {
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
		linkWithEditorAction.setChecked(isLinkingActive());
		toggleLinkingOfEditor(this);
	}

	private void addDragAndDropSupport(Tree tree) {
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
								XMLhandler.moveFolderInXML(Folder.getFolderID(), ID, Folder.getTypeOfFolder(),
										ParentType);
							} else {
								if (item.getParent().getDevObjProject() == DevObjProjBool
										&& !ID.equals(item.getParent().getFolderID())) {
									if (item.getType() == TypeOfEntry.URL) {
										XMLhandler.addURLToXML(item.getName(), item.getDescription(),
												item.getLongDescription(), item.getTechnicalName(), ID,
												TypeOfXMLNode.urlNode, ParentType);
									} else if (item.getType() == TypeOfEntry.ADTLink) {
										XMLhandler.addURLToXML(item.getName(), item.getDescription(),
												item.getLongDescription(), item.getTechnicalName(), ID,
												TypeOfXMLNode.ADTLinkNode, ParentType);
									} else {
										XMLhandler.addObjectToXML(item.getType(), item.getName(), item.getDescription(),
												item.getLongDescription(), ID, ParentType);

									}
									XMLhandler.delObjectFromXML(item.getType(), item.getName(),
											item.getParent().getFolderID(), item.getParent().getTypeOfFolder());
								}
							}
						}
						Superview.refreshViewer(viewer);
						dndSourceSelection = null;
					}
				}
			}

			@Override
			public void drop(DropTargetEvent arg0) {
				// TODO Auto-generated method stub

			}

		});
	}

	@Override
	public void enableLinkingOfEditor() {
		linkWithEditorAction.setEnabled(true);
	}

	@Override
	public void disableLinkingOfEditor() {
		linkWithEditorAction.setEnabled(false);
	}

	private void setTreeColumns(ColumnControlListener columnListener, Tree tree) {
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
		ColumnID.setResizable(false);
		TreeColumn ColumnFolderType = new TreeColumn(tree, SWT.LEFT);
		ColumnFolderType.setText("FolderType");
		ColumnFolderType.addControlListener(columnListener);
		ColumnFolderType.setWidth(0);
		ColumnFolderType.setResizable(false);
		TreeColumn ColumnDevObj = new TreeColumn(tree, SWT.LEFT);
		ColumnDevObj.setText("DevObjects");
		ColumnDevObj.addControlListener(columnListener);
		ColumnDevObj.setWidth(0);
		ColumnDevObj.setResizable(false);
		TreeColumn ColumnLinkedTo = new TreeColumn(tree, SWT.LEFT);
		ColumnLinkedTo.setText("Linked To");
		ColumnLinkedTo.addControlListener(columnListener);
		loadColumnSettings(ColumnLinkedTo);
	}

	@Override
	public void editorActivated(IEditorPart activeEditor) {

		if (isLinkingActive() && isHideOfDepProject()) {
			if (!getLinkedEditorProject().equals(Common.getProjectName())) {

				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				IWorkbenchWindow window = page.getWorkbenchWindow();
				ISelection ADTselection = window.getSelectionService().getSelection();
				LinkedProject = ProjectUtil.getActiveAdtCoreProject(ADTselection, null, null,
						IAdtCoreProject.ABAP_PROJECT_NATURE);
				if (LinkedProject != null) {
					setLinkedEditorProject(LinkedProject.getName());
					Superview.refreshViewer(viewer);
				}
			}
			return;
		}

	}

	public static boolean isHideOfDepProject() {
		return store.getBoolean(PreferenceConstants.P_HIDE_PROJECT_DEP_FOLDERS);
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
		manager.add(actions.actExportFavorites);
		manager.add(actions.actImportFavorites);

	}

	protected void fillContextMenu(IMenuManager manager) {
		if (viewer.getSelection() instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

			try {

				TreeObject object = (TreeObject) selection.getFirstElement();

				if (object instanceof TreeParent) {
					TreeParent parent = (TreeParent) object;
					if (parent.getDevObjProject() == true) {
						manager.add(actions.actAddFolder);
						manager.add(actions.actAddProgram);
						manager.add(actions.actAddClass);
						manager.add(actions.actAddInterface);
						manager.add(actions.actAddFunctionGroup);
						manager.add(actions.actAddFunctionModule);
						manager.add(actions.actAddView);
						manager.add(actions.actAddTable);
						manager.add(actions.actAddMessageClass);
						manager.add(actions.actAddSearchHelp);
						manager.add(actions.actAddADTLink);
						manager.add(actions.actAddCDS);
						manager.add(new Separator());
					} else {
						manager.add(actions.actAddFolder);
						manager.add(actions.actAddTransaction);
						manager.add(actions.actAddProgram);
						manager.add(actions.actAddURL);
						manager.add(new Separator());
					}
					manager.add(actions.actDelFolder);
					manager.add(new Separator());
					manager.add(actions.actEdit);
					manager.add(new Separator());
					drillDownAdapter.addNavigationActions(manager);
					// Other plug-ins can contribute there actions here
					manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
				} else if (object instanceof TreeObject) {

					manager.add(new Separator());
					manager.add(actions.actDelete);
					manager.add(new Separator());
					manager.add(actions.actEdit);
					manager.add(new Separator());

					Actions.addOpenInProjectMenu(manager, viewer);
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

		manager.add(actions.actAddRootFolder);

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
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(getID());
		Column.setWidth(prefs.getInt("column_width" + Column.getText(), 300));
	}

	protected void loadPluginSettings() {
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(getID());
		try {
			prefs.sync();
		} catch (org.osgi.service.prefs.BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setLinkingActive(prefs.getBoolean("linking_active", true));
		setLinkedEditorProject(prefs.get("linked_project", ""));
		LinkedProject = Common.getProjectByName(getLinkedEditorProject());
	};

	protected void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				TempLinkedEditorProject = getLinkedEditorProject();
				if (isHideOfDepProject()) {
				TempLinkedProject = LinkedProject;
				}
				else
				{
					TempLinkedProject = null;
				}
				actions.actDoubleClick.run();
			}
		});
	}

	public String getLinkedEditorProject() {

		return LinkedEditorProject;

	}

	public boolean isLinkingActive() {
		return linkingActive;
	}

	public void setLinkingActive(boolean linkingActive) {
		this.linkingActive = linkingActive;
	}

	public void setLinkedEditorProject(String linkedEditorProject) {
		LinkedEditorProject = linkedEditorProject;
	}

	public static TreeParent createTreeNodes(TypeOfXMLNode FolderXMLNode, IFavorites Favorite, Boolean selectFolderDialog) {

		String LinkedEditorProject = "";

		if (isHideOfDepProject() == true && Favorite != null) {
			LinkedEditorProject = Favorite.getLinkedEditorProject();
		}

		TreeParent invisibleRoot = new TreeParent("", "", true, "", "", Favorite, false, "root");

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc;
			try {
				doc = dBuilder.parse(XMLhandler.favFile);

				doc.getDocumentElement().normalize();

				NodeList nList = doc.getDocumentElement().getChildNodes();

				for (int temp = 0; temp < nList.getLength(); temp++) {

					Node nNode = nList.item(temp);

					if (nNode.getNodeType() == Node.ELEMENT_NODE) {

						Element eElement = (Element) nNode;
						createSubNodes(FolderXMLNode, eElement, invisibleRoot, LinkedEditorProject, Favorite, selectFolderDialog);
					}
				}

				DOMSource source = new DOMSource(doc);

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				StreamResult result = new StreamResult(XMLhandler.favFile.getPath());
				transformer.transform(source, result);
				return invisibleRoot;
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static void createSubNodes(TypeOfXMLNode folderXMLNode, Element subNode, TreeParent subNodeParent,
			String linkedEditorProject, IFavorites favorite, Boolean selectFolderDialog) {

		if (subNode.getNodeName().equalsIgnoreCase(folderXMLNode.toString())) {

			TreeParent parent = new TreeParent(subNode.getAttribute(TypeOfXMLAttr.name.toString()),
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
			boolean projectIsIndependent = Boolean
					.parseBoolean(subNode.getAttribute(TypeOfXMLAttr.projectIndependent.toString()));
			if (projectIsIndependent == false
					&& isHideOfDepProject() == true) {
				String ProjectName = linkedEditorProject;
				if (ProjectName.equals(""))
					ProjectName = Common.getProjectName();

				if (!parent.getProject().equals(ProjectName)) {
					return;
				}
			}

			subNodeParent.addChild(parent);

			NodeList Children = subNode.getChildNodes();

			for (int tempChild = 0; tempChild < Children.getLength(); tempChild++) {

				Node nNodeChild = Children.item(tempChild);

				if (nNodeChild.getNodeType() == Node.ELEMENT_NODE) {

					Element eElementChild = (Element) nNodeChild;

					if (eElementChild.getNodeName().equalsIgnoreCase(folderXMLNode.toString())) {
						createSubNodes(folderXMLNode, eElementChild, parent, linkedEditorProject, favorite, selectFolderDialog);
					}

					else {
						if (selectFolderDialog) {
						String childName = eElementChild.getAttribute(TypeOfXMLAttr.name.toString());
						if (XMLhandler.isXMLNodeNameToUpper(eElementChild.getTagName())) {
							childName = childName.toUpperCase();
						}
						parent.addChild(new TreeObject(childName,
								XMLhandler.getEntryTypeFromXMLNode(nNodeChild.getNodeName()),
								eElementChild.getAttribute(TypeOfXMLAttr.description.toString()),
								eElementChild.getAttribute(TypeOfXMLAttr.technicalName.toString()),
								eElementChild.getAttribute(TypeOfXMLAttr.longDescription.toString()), favorite));
						}
					}
				}
			}
		}

	}

	public static void refreshViewer(TreeViewer viewer) {

		if (viewer != null) {

			Object ContentProvider = viewer.getContentProvider();
			try {
				java.lang.reflect.Method initialize = ContentProvider.getClass().getMethod("initialize");
				initialize.invoke(ContentProvider);
			} catch (Exception e) {

			}

			viewer.refresh();
		}
	}

	public static List<IFavorites> getActiveFavorites() {
		return activeFavorites;
	}
	public static void addFavoritesToActive(IFavorites Favorite) {
		activeFavorites.add(Favorite);
	}
}

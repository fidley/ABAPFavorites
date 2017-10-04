package com.abapblog.favoritesDO.views;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
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
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

import com.abapblog.favorites.common.AFIcons;
import com.abapblog.favorites.common.Common;
import com.abapblog.favorites.common.CommonTypes.TypeOfXMLNode;
import com.abapblog.favorites.common.TreeObject;
import com.abapblog.favorites.common.TreeParent;
import com.sap.adt.project.IAdtCoreProject;
import com.sap.adt.project.ui.util.ProjectUtil;

/**
 * Simple ABAP Favorites plug-in that was created on a base of sample TreeViewer
 * plug-in.
 *
 * For more go to ABAPBlog.com
 */

public class FavoritesDO extends ViewPart implements ILinkedWithEditorView {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.abapblog.favoritesDO.views.FavoritesDO";

	private static String LinkedEditorProject = "";
	private IProject LinkedProject;
	private IPartListener2 linkWithEditorPartListener = new LinkWithEditorPartListener(this);
	private Action linkWithEditorAction;
	private static boolean linkingActive = true;
	public TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private Common Utils;
	public static String partName;

	public FavoritesDO getFav() {
		return FavoritesDO.this;
	}

	public static void savePluginSettings() {
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(ID);

		prefs.putBoolean("linking_active", linkingActive);
		prefs.put("linked_project", LinkedEditorProject);

		try {
			// prefs are automatically flushed during a plugin's "super.stop()".
			prefs.flush();
		} catch (org.osgi.service.prefs.BackingStoreException e) {
			// TODO write a real exception handler.
			e.printStackTrace();
		}
	}

	public class ColumnControlListener implements ControlListener {

		@Override
		public void controlMoved(ControlEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void controlResized(ControlEvent arg0) {
			// TODO Auto-generated method stub
			IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(ID);
			TreeColumn column = (TreeColumn) arg0.getSource();
			prefs.putInt("column_width" + column.getText(), column.getWidth());
		}

	}

	public class ViewContentProvider implements ITreeContentProvider {
		private TreeParent invisibleRoot;
		public IPath stateLoc;

		@Override
		public Object[] getElements(Object parent) {
			if (parent.equals(getViewSite())) {
				if (invisibleRoot == null)
					initialize();
				return getChildren(invisibleRoot);
			}
			return getChildren(parent);
		}

		@Override
		public Object getParent(Object child) {
			if (child instanceof TreeObject) {
				return ((TreeObject) child).getParent();
			}
			return null;
		}

		@Override
		public Object[] getChildren(Object parent) {
			if (parent instanceof TreeParent) {
				return ((TreeParent) parent).getChildren();
			}
			return new Object[0];
		}

		@Override
		public boolean hasChildren(Object parent) {
			if (parent instanceof TreeParent)
				return ((TreeParent) parent).hasChildren();
			return false;
		}

		/*
		 * We will set up a dummy model to initialize tree heararchy. In a real code,
		 * you will connect to a real model and expose its hierarchy.
		 */

		// public void createTreeNodes() {
		//
		// invisibleRoot = new TreeParent("", "", true, "", getFav(), false);
		//
		// DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		// DocumentBuilder dBuilder;
		// try {
		// dBuilder = dbFactory.newDocumentBuilder();
		// Document doc;
		// try {
		// doc = dBuilder.parse(Common.favFile);
		//
		// doc.getDocumentElement().normalize();
		//
		// NodeList nList = doc.getDocumentElement().getChildNodes();
		//
		// for (int temp = 0; temp < nList.getLength(); temp++) {
		//
		// Node nNode = nList.item(temp);
		//
		// if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		//
		// Element eElement = (Element) nNode;
		//
		// if
		// (eElement.getNodeName().equalsIgnoreCase(TypeOfXMLNode.folderDONode.toString()))
		// {
		//
		// TreeParent parent = new
		// TreeParent(eElement.getAttribute(TypeOfXMLAttr.name.toString()),
		// eElement.getAttribute(TypeOfXMLAttr.description.toString()),
		// Boolean.parseBoolean(
		// eElement.getAttribute(TypeOfXMLAttr.projectIndependent.toString())),
		// eElement.getAttribute(TypeOfXMLAttr.project.toString()), getFav(), true);
		// boolean projectIsIndependent = Boolean.parseBoolean(
		// eElement.getAttribute(TypeOfXMLAttr.projectIndependent.toString()));
		// if (projectIsIndependent == false) {
		// String ProjectName = LinkedEditorProject;
		// if (ProjectName.equals(""))
		// ProjectName = Common.getProjectName();
		//
		// if (!parent.getProject().equals(ProjectName)) {
		// continue;
		// }
		// }
		//
		// invisibleRoot.addChild(parent);
		//
		// NodeList Children = eElement.getChildNodes();
		//
		// for (int tempChild = 0; tempChild < Children.getLength(); tempChild++) {
		//
		// Node nNodeChild = Children.item(tempChild);
		//
		// if (nNodeChild.getNodeType() == Node.ELEMENT_NODE) {
		//
		// Element eElementChild = (Element) nNodeChild;
		//
		// String childName = eElementChild.getAttribute(TypeOfXMLAttr.name.toString());
		// if (Common.isXMLNodeNameToUpper(eElementChild.getTagName())) {
		// childName = childName.toUpperCase();
		// }
		// parent.addChild(new TreeObject(childName,
		// Common.getEntryTypeFromXMLNode(nNodeChild.getNodeName()),
		// eElementChild.getAttribute(TypeOfXMLAttr.description.toString()),
		// eElementChild.getAttribute(TypeOfXMLAttr.technicalName.toString()),
		// getFav()));
		//
		// }
		// }
		// }
		// }
		// }
		//
		// } catch (SAXException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// } catch (ParserConfigurationException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }

		public void initialize() {
			invisibleRoot = Utils.createTreeNodes(TypeOfXMLNode.folderDONode, getFav(), LinkedEditorProject);
		}
	}

	class AFPatternFilter extends PatternFilter {
		@Override
		protected boolean isLeafMatch(final Viewer viewer, final Object element) {
			TreeViewer treeViewer = (TreeViewer) viewer;
			int numberOfColumns = treeViewer.getTree().getColumnCount();
			boolean isMatch = false;
			if (element instanceof TreeObject) {
				TreeObject leaf = (TreeObject) element;
				isMatch |= wordMatches(leaf.getName());
				if (isMatch == false) {
					isMatch |= wordMatches(leaf.getDescription());
				}
			}
			return isMatch;
		}

	}

	class ViewLabelProvider implements ITableLabelProvider {

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			AFIcons AFIcons = new AFIcons();
			switch (columnIndex) {
			case 0:
				String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
				if (element instanceof TreeParent)
					if (((TreeParent) element).getDevObjProject() == true) {
						return AFIcons.getFodlerDevObjIcon();
					} else {
						return AFIcons.getFolderIcon();
					}

				if (element instanceof TreeObject) {
					TreeObject Node = (TreeObject) element;
					switch (Node.Type) {
					case Transaction:
						return AFIcons.getTransactionIcon();
					case URL:
						return AFIcons.getURLIcon();
					case Program:
						return AFIcons.getProgramIcon();
					case Class:
						return AFIcons.getClassIcon();
					case Interface:
						return AFIcons.getInterfaceIcon();
					case Include:
						return AFIcons.getProgramIncludeIcon();
					case FunctionGroup:
						return AFIcons.getFunctionGroupIcon();
					case FunctionModule:
						return AFIcons.getFunctionModuleIcon();
					case MessageClass:
						return AFIcons.getMessageClassIcon();
					case View:
						return AFIcons.getViewIcon();
					case Table:
						return AFIcons.getTableIcon();
					case SearchHelp:
						return AFIcons.getSearchHelpIcon();
					case ADTLink:
						return AFIcons.getADTLinkIcon();
					}

				}

				return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
			case 1:
				return null;
			}
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			switch (columnIndex) {
			case 0:
				return element.toString();
			case 1:
				if (element instanceof TreeObject)
					return ((TreeObject) element).getDescription();

			}
			return null;
		}

		@Override
		public void addListener(ILabelProviderListener listener) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
		}
	}

	/**
	 * The constructor.
	 */
	public FavoritesDO() {
		Utils = new Common(TypeOfXMLNode.folderDONode);
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	private void setNewPartName() {
		if (linkingActive) {
			setPartName(partName);
		} else {
			setPartName(partName + " (" + LinkedEditorProject + ")");
		}
	}

	public String getProjectName() {
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

	@Override
	public void createPartControl(Composite parent) {

		AFPatternFilter filter = new AFPatternFilter();
		FilteredTree filteredTree = new FilteredTree(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL, filter, true);
		ColumnControlListener columnListener = new ColumnControlListener();
		partName = getPartName();

		viewer = filteredTree.getViewer();
		drillDownAdapter = new DrillDownAdapter(viewer);
		Tree tree = viewer.getTree();
		tree.setHeaderVisible(true);
		TreeColumn columnName = new TreeColumn(tree, SWT.LEFT);
		columnName.setText("Name");
		columnName.addControlListener(columnListener);
		loadColumnSettings(columnName);
		TreeColumn columnDescr = new TreeColumn(tree, SWT.LEFT);
		columnDescr.setText("Description");
		columnDescr.addControlListener(columnListener);
		loadColumnSettings(columnDescr);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setInput(getViewSite());
		viewer.setLabelProvider(new ViewLabelProvider());

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "com.abapblog.favoritesDO.viewer");
		getSite().setSelectionProvider(viewer);
		Utils.makeActions(viewer);
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();

		loadPluginSettings();

		//
		Common.ViewerFavoritesDO = viewer;

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
	}

	@Override
	public void editorActivated(IEditorPart activeEditor) {
		if (linkingActive) {

			if (!LinkedEditorProject.equals(getProjectName())) {

				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				IWorkbenchWindow window = page.getWorkbenchWindow();
				ISelection ADTselection = window.getSelectionService().getSelection();
				LinkedProject = ProjectUtil.getActiveAdtCoreProject(ADTselection, null, null,
						IAdtCoreProject.ABAP_PROJECT_NATURE);
				if (LinkedProject != null) {
					LinkedEditorProject = LinkedProject.getName();
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

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				FavoritesDO.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(Utils.actExportFavorites);
		manager.add(Utils.actImportFavorites);
	}

	private void fillContextMenu(IMenuManager manager) {
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

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(Utils.actAddRootFolder);
		// manager.add(actSortUP);
		// manager.add(actSortDown);
		manager.add(new Separator());
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

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				Utils.TempLinkedEditorProject = LinkedEditorProject;
				Utils.TempLinkedProject = LinkedProject;
				Utils.doubleClickAction.run();
			}
		});
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(), "Favorites", message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	private void loadColumnSettings(TreeColumn Column) {
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(ID);
		Column.setWidth(prefs.getInt("column_width" + Column.getText(), 300));
	}

	private void loadPluginSettings() {
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(ID);
		try {
			prefs.sync();
		} catch (org.osgi.service.prefs.BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		linkingActive = prefs.getBoolean("linking_active", true);
		LinkedEditorProject = prefs.get("linked_project", "");
		LinkedProject = Common.getProjectByName(LinkedEditorProject);
	}
}

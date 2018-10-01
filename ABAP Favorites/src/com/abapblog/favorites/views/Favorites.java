package com.abapblog.favorites.views;

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
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

import com.abapblog.favorites.common.AFPatternFilter;
import com.abapblog.favorites.common.ColumnControlListener;
import com.abapblog.favorites.common.Common;
import com.abapblog.favorites.common.ILinkedWithEditorView;
import com.abapblog.favorites.common.LinkWithEditorPartListener;
import com.abapblog.favorites.common.CommonTypes.TypeOfXMLNode;
import com.abapblog.favorites.common.TreeObject;
import com.abapblog.favorites.common.TreeParent;
import com.abapblog.favorites.common.ViewContentProvider;
import com.abapblog.favorites.common.ViewLabelProvider;
import com.sap.adt.project.IAdtCoreProject;
import com.sap.adt.project.ui.util.ProjectUtil;

/**
 * Simple ABAP Favorites plug-in that was created on a base of sample TreeViewer
 * plug-in.
 *
 * For more go to ABAPBlog.com
 */

public class Favorites extends ViewPart implements ILinkedWithEditorView {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.abapblog.favorites.views.Favorites";

	private static String LinkedEditorProject = "";
	private IProject LinkedProject;
	private IPartListener2 linkWithEditorPartListener = new LinkWithEditorPartListener(this);
	private Action linkWithEditorAction;
	private static boolean linkingActive = true;
	public static TreeViewer viewer;
	private Common Utils;
	private DrillDownAdapter drillDownAdapter;

	public static String partName;

	public Favorites getFav() {
		return Favorites.this;
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

	/**
	 * The constructor.
	 */
	public Favorites() {
		Utils = new Common(TypeOfXMLNode.folderNode);
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */

	private void setNewPartName() {
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
		tree.setHeaderVisible(true);
		TreeColumn columnName = new TreeColumn(tree, SWT.LEFT);
		columnName.setText("Name");
		columnName.addControlListener(columnListener);
		loadColumnSettings(columnName);
		TreeColumn columnDescr = new TreeColumn(tree, SWT.LEFT);
		columnDescr.setText("Description");
		columnDescr.addControlListener(columnListener);
		loadColumnSettings(columnDescr);
		viewer.setContentProvider(new ViewContentProvider(TypeOfXMLNode.folderNode, this, getViewSite()));
		viewer.setInput(getViewSite());
		viewer.setLabelProvider(new ViewLabelProvider());

		loadPluginSettings();
		//

		Common.ViewerFavorites = viewer;

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "com.abapblog.favorites.viewer");
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

	// protected void setPartName(String partName) {
	// if (linkingActive == false) {
	// partName = partName + " " + LinkedEditorProject;
	// } else {
	//
	// }
	// }

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
				Favorites.this.fillContextMenu(manager);
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

					Common.addOpenInProjectMenu(manager,viewer);
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
		setLinkedEditorProject(prefs.get("linked_project", ""));
		LinkedProject = Common.getProjectByName(getLinkedEditorProject());
	};

	private void hookDoubleClickAction() {
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

	public void setLinkedEditorProject(String linkedEditorProject) {
		LinkedEditorProject = linkedEditorProject;
	};
}

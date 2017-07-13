package com.abapblog.favoritesDO.views;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
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
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.abapblog.favorites.common.AFIcons;
import com.abapblog.favorites.common.Common;
import com.abapblog.favorites.common.CommonTypes.TypeOfEntry;
import com.abapblog.favorites.common.CommonTypes.TypeOfXMLAttr;
import com.abapblog.favorites.common.CommonTypes.TypeOfXMLNode;
import com.abapblog.favorites.common.FolderDialog;
import com.abapblog.favorites.common.TreeObject;
import com.abapblog.favorites.common.TreeParent;
import com.abapblog.favorites.common.URLDialog;
import com.sap.adt.logging.AdtLogging;
import com.sap.adt.project.IAdtCoreProject;
import com.sap.adt.project.ui.util.ProjectUtil;
import com.sap.adt.ris.search.AdtRisQuickSearchFactory;
import com.sap.adt.ris.search.RisQuickSearchNotSupportedException;
import com.sap.adt.sapgui.ui.SapGuiPlugin;
import com.sap.adt.sapgui.ui.editors.AdtSapGuiEditorUtilityFactory;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;
import com.sap.adt.tools.core.ui.navigation.AdtNavigationServiceFactory;
import com.sap.adt.tools.core.wbtyperegistry.WorkbenchAction;
import com.sap.adt.util.AdapterUtil;

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

	private String LinkedEditorProject = "";
	private IProject LinkedProject;
	private IPartListener2 linkWithEditorPartListener = new LinkWithEditorPartListener(this);
	private Action linkWithEditorAction;
	private boolean linkingActive = true;
	public TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private Action actAddFolder;
	private Action actAddClass;
	private Action actAddInterface;
	private Action actAddFunctionGroup;
	private Action actAddFunctionModule;
	private Action actAddTransaction;
	private Action actAddProgram;
	private Action actAddURL;
	private Action actDelFolder;
	private Action actEdit;
	private Action actDelete;
	private Action doubleClickAction;
	private AFIcons AFIcon;

	public FavoritesDO getFav() {
		return FavoritesDO.this;
	}

	public class ViewContentProvider implements ITreeContentProvider {
		private TreeParent invisibleRoot;
		public IPath stateLoc;

		public Object[] getElements(Object parent) {
			if (parent.equals(getViewSite())) {
				if (invisibleRoot == null)
					initialize();
				return getChildren(invisibleRoot);
			}
			return getChildren(parent);
		}

		public Object getParent(Object child) {
			if (child instanceof TreeObject) {
				return ((TreeObject) child).getParent();
			}
			return null;
		}

		public Object[] getChildren(Object parent) {
			if (parent instanceof TreeParent) {
				return ((TreeParent) parent).getChildren();
			}
			return new Object[0];
		}

		public boolean hasChildren(Object parent) {
			if (parent instanceof TreeParent)
				return ((TreeParent) parent).hasChildren();
			return false;
		}

		/*
		 * We will set up a dummy model to initialize tree heararchy. In a real
		 * code, you will connect to a real model and expose its hierarchy.
		 */

		public void createTreeNodes() {

			invisibleRoot = new TreeParent("", "", true, "", getFav(), false);

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			try {
				dBuilder = dbFactory.newDocumentBuilder();
				Document doc;
				try {
					doc = dBuilder.parse(Common.favFile);

					doc.getDocumentElement().normalize();

					NodeList nList = doc.getDocumentElement().getChildNodes();

					for (int temp = 0; temp < nList.getLength(); temp++) {

						Node nNode = nList.item(temp);

						if (nNode.getNodeType() == Node.ELEMENT_NODE) {

							Element eElement = (Element) nNode;

							if (eElement.getNodeName().equalsIgnoreCase(TypeOfXMLNode.folderDONode.toString())) {

								TreeParent parent = new TreeParent(eElement.getAttribute(TypeOfXMLAttr.name.toString()),
										eElement.getAttribute(TypeOfXMLAttr.description.toString()),
										Boolean.parseBoolean(
												eElement.getAttribute(TypeOfXMLAttr.projectIndependent.toString())),
										eElement.getAttribute(TypeOfXMLAttr.project.toString()), getFav(), true);
								boolean projectIsIndependent = Boolean.parseBoolean(
										eElement.getAttribute(TypeOfXMLAttr.projectIndependent.toString()));
								if (projectIsIndependent == false) {
									String ProjectName = getProjectName();

									if (!parent.getProject().equals(ProjectName)) {
										continue;
									}
								}

								invisibleRoot.addChild(parent);

								NodeList Children = eElement.getChildNodes();

								for (int tempChild = 0; tempChild < Children.getLength(); tempChild++) {

									Node nNodeChild = Children.item(tempChild);

									if (nNodeChild.getNodeType() == Node.ELEMENT_NODE) {

										Element eElementChild = (Element) nNodeChild;

										String childName = eElementChild.getAttribute(TypeOfXMLAttr.name.toString());
										if (Common.isXMLNodeNameToUpper(eElementChild.getTagName())) {
											childName = childName.toUpperCase();
										}
										parent.addChild(new TreeObject(childName,
												Common.getEntryTypeFromXMLNode(nNodeChild.getNodeName()),
												eElementChild.getAttribute(TypeOfXMLAttr.description.toString()),
												eElementChild.getAttribute(TypeOfXMLAttr.technicalName.toString()),
												getFav()));

									}
								}
							}
						}
					}

				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void initialize() {
			createTreeNodes();
		}
	}

	class AFPatternFilter extends PatternFilter {
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
					}

				}

				return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
			case 1:
				return null;
			}
			return null;
		}

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

		public void addListener(ILabelProviderListener listener) {
		}

		public void dispose() {
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
		}
	}

	/**
	 * The constructor.
	 */
	public FavoritesDO() {
		AFIcon = new AFIcons();
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
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

	public void createPartControl(Composite parent) {
		// viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL |
		// SWT.V_SCROLL);

		AFPatternFilter filter = new AFPatternFilter();
		FilteredTree filteredTree = new FilteredTree(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL, filter, true);

		viewer = filteredTree.getViewer();
		drillDownAdapter = new DrillDownAdapter(viewer);
		Tree tree = viewer.getTree();
		tree.setHeaderVisible(true);
		TreeColumn columnName = new TreeColumn(tree, SWT.LEFT);
		columnName.setText("Name");
		columnName.setWidth(200);
		TreeColumn columnDescr = new TreeColumn(tree, SWT.LEFT);
		columnDescr.setText("Description");
		columnDescr.setWidth(300);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setInput(getViewSite());
		viewer.setLabelProvider(new ViewLabelProvider());

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "com.abapblog.favorites.viewer");
		getSite().setSelectionProvider(viewer);
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();

		// Linking with editor
		linkWithEditorAction = new Action("Link with Editor", SWT.TOGGLE) {
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

		// set up comparisor to be used in tree
		sortTable();
	}

	@Override
	public void editorActivated(IEditorPart activeEditor) {
		if (linkingActive) { // && !getViewSite().getPage().isPartVisible(this))
								// {

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
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
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
		// manager.add(actAddFolder);
		// manager.add(new Separator());
		// manager.add(actAddTransaction);
	}

	private void fillContextMenu(IMenuManager manager) {
		if (viewer.getSelection() instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

			try {

				TreeObject object = (TreeObject) selection.getFirstElement();

				if (object instanceof TreeParent) {
					TreeParent parent = (TreeParent) object;
					if (parent.getDevObjProject() == true) {
						manager.add(actAddFolder);
						manager.add(actAddProgram);
						manager.add(actAddClass);
						manager.add(actAddInterface);
						manager.add(actAddFunctionGroup);
						manager.add(actAddFunctionModule);
						manager.add(new Separator());
					} else {
						manager.add(actAddFolder);
						manager.add(actAddTransaction);
						manager.add(actAddProgram);
						manager.add(actAddURL);
						manager.add(new Separator());
					}
					manager.add(actDelFolder);
					manager.add(new Separator());
					manager.add(actEdit);
					manager.add(new Separator());
					drillDownAdapter.addNavigationActions(manager);
					// Other plug-ins can contribute there actions here
					manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
				} else if (object instanceof TreeObject) {

					manager.add(new Separator());
					manager.add(actDelete);
					manager.add(new Separator());
					manager.add(actEdit);
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
		manager.add(actAddFolder);
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
				} else if (e1 instanceof TreeObject && e2 instanceof TreeObject) {
					return ((TreeObject) e1).getName().compareToIgnoreCase(((TreeObject) e2).getName());
				} else {
					throw new IllegalArgumentException("Not comparable: " + e1 + " " + e2);
				}
			}
		});
	}

	private void makeActions() {

		actAddFolder = new Action() {
			public void run() {
				FolderDialog FolderDialog = new FolderDialog(viewer.getControl().getShell(), true);
				FolderDialog.create();
				if (FolderDialog.open() == Window.OK) {
					Common.addFolderDOToXML(FolderDialog.getName(), FolderDialog.getDescription(),
							FolderDialog.getPrjInd(), getProjectName(), true);
					Common.refreshViewer(viewer);
				}

			}
		};
		actAddFolder.setText("Add New Folder");
		actAddFolder.setToolTipText("Add New Folder");
		actAddFolder.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ADD));

		actAddTransaction = new Action() {
			public void run() {
				Common.addObjectFromAction(TypeOfEntry.Transaction, true, viewer);
			}

		};
		actAddTransaction.setText("Add Transaction");
		actAddTransaction.setToolTipText("Transaction");
		actAddTransaction.setImageDescriptor(AFIcon.getTransactionImgDescr());

		actAddProgram = new Action() {
			public void run() {
				Common.addObjectFromAction(TypeOfEntry.Program, true, viewer);
			}

		};
		actAddProgram.setText("Add Program");
		actAddProgram.setToolTipText("Program");
		actAddProgram.setImageDescriptor(AFIcon.getProgramIconImgDescr());

		actAddURL = new Action() {
			public void run() {
				URLDialog URLDialog = new URLDialog(viewer.getControl().getShell());
				URLDialog.create();
				if (URLDialog.open() == Window.OK) {
					if (viewer.getSelection() instanceof IStructuredSelection) {
						IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

						TreeObject object = (TreeObject) selection.getFirstElement();

						if (object instanceof TreeParent) {

							Common.addURLToXML(URLDialog.getName(), URLDialog.getDescription(), URLDialog.getURL(),
									object.Name);
							System.out.println(URLDialog.getName());
							System.out.println(URLDialog.getDescription());
							Common.refreshViewer(viewer);
						}

					}
				}
			}
		};
		actAddURL.setText("Add URL");
		actAddURL.setToolTipText("URL");
		actAddURL.setImageDescriptor(AFIcon.getURLIconImgDescr());

		actAddClass = new Action() {
			public void run() {
				Common.addObjectFromAction(TypeOfEntry.Class, true, viewer);
			}

		};
		actAddClass.setText("Add class");
		actAddClass.setToolTipText("Class");
		actAddClass.setImageDescriptor(AFIcon.getClassIconImgDescr());

		actAddInterface = new Action() {
			public void run() {
				Common.addObjectFromAction(TypeOfEntry.Interface, true, viewer);
			}

		};
		actAddInterface.setText("Add interface");
		actAddInterface.setToolTipText("Interface");
		actAddInterface.setImageDescriptor(AFIcon.getInterfaceIconImgDescr());

		actAddFunctionGroup = new Action() {
			public void run() {
				Common.addObjectFromAction(TypeOfEntry.FunctionGroup, true, viewer);
			}

		};
		actAddFunctionGroup.setText("Add function group");
		actAddFunctionGroup.setToolTipText("Function Group");
		actAddFunctionGroup.setImageDescriptor(AFIcon.getFunctionGroupIconImgDescr());

		actAddFunctionModule = new Action() {
			public void run() {
				Common.addObjectFromAction(TypeOfEntry.FunctionModule, true, viewer);
			}

		};
		actAddFunctionModule.setText("Add function module");
		actAddFunctionModule.setToolTipText("Function Module");
		actAddFunctionModule.setImageDescriptor(AFIcon.getFunctionModuleIconImgDescr());

		actDelFolder = new Action() {
			public void run() {
				if (viewer.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

					TreeObject object = (TreeObject) selection.getFirstElement();

					if (object instanceof TreeParent) {
						Common.delFolderFromXML(object.Name, ((TreeParent) object).getTypeOfFolder());
						Common.refreshViewer(viewer);
					}

				}
			}
		};
		actDelFolder.setText("Delete Folder");
		actDelFolder.setToolTipText("Folder");
		actDelFolder.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));

		actEdit = new Action() {
			public void run() {
				if (viewer.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

					TreeObject object = (TreeObject) selection.getFirstElement();
					Common.editObjectFromAction(object.getType(),
							Common.getObjectXMLNode(object.getType()).isNameToUpper(), viewer);

				}
			}
		};
		actEdit.setText("Edit");
		actEdit.setToolTipText("Edit");
		actEdit.setImageDescriptor(AFIcon.getRenameIconImgDescr());

		actDelete = new Action() {
			public void run() {
				Common.deleteObjectFromAction(viewer);
			}
		};
		actDelete.setText("Delete");
		actDelete.setToolTipText("Delete");
		actDelete.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));

		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();

				if (LinkedProject == null) {
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					IWorkbenchWindow window = page.getWorkbenchWindow();
					ISelection ADTselection = window.getSelectionService().getSelection();
					LinkedProject = ProjectUtil.getActiveAdtCoreProject(ADTselection, null, null,
							IAdtCoreProject.ABAP_PROJECT_NATURE);
				}

				if (LinkedProject != null) {
					LinkedEditorProject = LinkedProject.getName();

					if (obj instanceof TreeObject) {

						TreeObject nodeObject = ((TreeObject) obj);
						TypeOfEntry NodeType = nodeObject.getType();
						TreeParent nodeParent = nodeObject.parent;
						switch (NodeType) {
						case Transaction:
							AdtSapGuiEditorUtilityFactory.createSapGuiEditorUtility()
									.openEditorAndStartTransaction(LinkedProject, obj.toString(), true);
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
						case Program:
							if (nodeParent.getDevObjProject() == false) {
								runObject(LinkedProject, nodeObject.getName(), nodeObject.Type);
								break;
							} else {
								openObject(LinkedProject, nodeObject.getName(), nodeObject.Type);
								break;
							}

						default:
							if (nodeParent.getDevObjProject() == false) {
								runObject(LinkedProject, nodeObject.getName(), nodeObject.Type);
								break;
							} else {
								openObject(LinkedProject, nodeObject.getName(), nodeObject.Type);
								break;
							}
						}
					}

				} else {
					if (obj instanceof TreeObject) {
						TypeOfEntry NodeType = ((TreeObject) obj).getType();
						switch (NodeType) {
						case URL:
							try {
								PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser()
										.openURL(new URL(((TreeObject) obj).getTechnicalName()));
							} catch (PartInitException | MalformedURLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							break;
						}
					}
				}
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	public void openObject(IProject project, String reportName, TypeOfEntry type) {
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
			AdtLogging.getLogger(getClass()).error(e);
		}
	}

	public void runObject(IProject project, String reportName, TypeOfEntry type) {
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
						SapGuiPlugin.getDefault().openEditorForObject(project,
								AdapterUtil.getAdapter(ref, com.sap.adt.tools.core.IAdtObjectReference.class), true,
								WorkbenchAction.EXECUTE.toString(), null, Collections.<String, String>emptyMap());
						return;
					}
				}
			}
		} catch (OperationCanceledException | RisQuickSearchNotSupportedException e) {
			AdtLogging.getLogger(getClass()).error(e);
		}
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(), "Favorites", message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}

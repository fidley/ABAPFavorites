package com.abapblog.favorites.views;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.win32.CREATESTRUCT;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.SWT;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.resources.IProject;
import com.sap.adt.project.IAdtCoreProject;
import com.sap.adt.project.ui.util.ProjectUtil;
import com.sap.adt.sapgui.ui.editors.AdtSapGuiEditorUtilityFactory;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class Favorites extends ViewPart implements ILinkedWithEditorView {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.abapblog.favorites.views.Favorites";

	public static enum TypeOfEntry {
		Folder, Transaction, URL
	};

	public static enum TypeOfXMLNode {

		folder {
			public String toString() {
				return "folder";
			}
		},
		transaction {
			public String toString() {
				return "transaction";
			}
		},
		url {
			public String toString() {
				return "url";
			}
		}
	}

	public static enum TypeOfXMLAttr {
		name {
			public String toString() {
				return "name";
			}
		},
		description {
			public String toString() {
				return "description";
			}
		},
		projectIndependent {
			public String toString() {
				return "projectIndependent";
			}
		},
		project {
			public String toString() {
				return "project";
			}
		},
		technicalName {
			public String toString() {
				return "technicalName";
			}
		}
	}

	private String LinkedEditorProject = "";
	private IProject LinkedProject;
	private IPartListener2 linkWithEditorPartListener = new LinkWithEditorPartListener(this);
	private Action linkWithEditorAction;
	private boolean linkingActive = true;
	private TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private Action actAddFolder;
	private Action actAddTransaction;
	private Action actAddURL;
	private Action actDelFolder;
	private Action actDelTransaction;
	private Action actDelURL;
	private Action doubleClickAction;
	public static final String favFileName = "favorites.xml";
	public File favFile;

	public void addFolderToXML(String Name, String Description, Boolean ProjectIndependent, String ProjectName) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc;
			try {
				doc = dBuilder.parse(favFile);

				doc.getDocumentElement().normalize();
				Element root = doc.getDocumentElement();

				Element FolderEl = doc.createElement(TypeOfXMLNode.folder.toString());
				FolderEl.setAttribute(TypeOfXMLAttr.name.toString(), Name);
				FolderEl.setAttribute(TypeOfXMLAttr.description.toString(), Description);
				FolderEl.setAttribute(TypeOfXMLAttr.projectIndependent.toString(), ProjectIndependent.toString());
				FolderEl.setAttribute(TypeOfXMLAttr.project.toString(), ProjectName);

				root.appendChild(FolderEl);

				DOMSource source = new DOMSource(doc);

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				StreamResult result = new StreamResult(favFile.getPath());
				transformer.transform(source, result);

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

	}

	public void addTransactionToXML(String Name, String Description, String Parent) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc;
			try {
				doc = dBuilder.parse(favFile);

				doc.getDocumentElement().normalize();
				NodeList folders = doc.getElementsByTagName(TypeOfXMLNode.folder.toString());

				for (int temp = 0; temp < folders.getLength(); temp++) {

					Node nNode = folders.item(temp);

					NamedNodeMap attributes = nNode.getAttributes();
					Node FolderName = attributes.getNamedItem(TypeOfXMLAttr.name.toString());

					if (FolderName.getNodeValue().contentEquals(Parent)) {

						Element TransactionEl = doc.createElement(TypeOfXMLNode.transaction.toString());
						TransactionEl.setAttribute(TypeOfXMLAttr.name.toString(), Name);
						TransactionEl.setAttribute(TypeOfXMLAttr.description.toString(), Description);

						nNode.appendChild(TransactionEl);

						DOMSource source = new DOMSource(doc);

						TransformerFactory transformerFactory = TransformerFactory.newInstance();
						Transformer transformer = transformerFactory.newTransformer();
						StreamResult result = new StreamResult(favFile.getPath());
						transformer.transform(source, result);
					}
				}
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

	}

	public void addURLToXML(String Name, String Description, String URL, String Parent) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc;
			try {
				doc = dBuilder.parse(favFile);

				doc.getDocumentElement().normalize();
				NodeList folders = doc.getElementsByTagName(TypeOfXMLNode.folder.toString());

				for (int temp = 0; temp < folders.getLength(); temp++) {

					Node nNode = folders.item(temp);

					NamedNodeMap attributes = nNode.getAttributes();
					Node FolderName = attributes.getNamedItem(TypeOfXMLAttr.name.toString());
					if (FolderName.getNodeValue().toString().equals(Parent)) {

						Element URLEl = doc.createElement(TypeOfXMLNode.url.toString());
						URLEl.setAttribute(TypeOfXMLAttr.name.toString(), Name);
						URLEl.setAttribute(TypeOfXMLAttr.description.toString(), Description);
						URLEl.setAttribute(TypeOfXMLAttr.technicalName.toString(), URL);

						nNode.appendChild(URLEl);

						DOMSource source = new DOMSource(doc);

						TransformerFactory transformerFactory = TransformerFactory.newInstance();
						Transformer transformer = transformerFactory.newTransformer();
						StreamResult result = new StreamResult(favFile.getPath());
						transformer.transform(source, result);
					}
				}
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

	}

	public void delTransactionFromXML(String Name, String Parent) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc;
			try {
				doc = dBuilder.parse(favFile);

				doc.getDocumentElement().normalize();
				NodeList folders = doc.getElementsByTagName(TypeOfXMLNode.folder.toString());

				for (int temp = 0; temp < folders.getLength(); temp++) {

					Node nNode = folders.item(temp);

					NamedNodeMap attributes = nNode.getAttributes();
					Node FolderName = attributes.getNamedItem(TypeOfXMLAttr.name.toString());
					if (FolderName.getNodeValue().equals(Parent)) {

						NodeList FolderEntries = nNode.getChildNodes();
						for (int tempfol = 0; tempfol < FolderEntries.getLength(); tempfol++) {

							Node nNodeFol = FolderEntries.item(tempfol);
							if (nNodeFol.getAttributes().getNamedItem(TypeOfXMLAttr.name.toString()).getNodeValue()
									.equals(Name)
									&& nNodeFol.getNodeName().toString().equals(TypeOfXMLNode.transaction.toString()))

							{
								nNode.removeChild(nNodeFol);
							}

						}

						DOMSource source = new DOMSource(doc);

						TransformerFactory transformerFactory = TransformerFactory.newInstance();
						Transformer transformer = transformerFactory.newTransformer();
						StreamResult result = new StreamResult(favFile.getPath());
						transformer.transform(source, result);
					}
				}
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

	}

	public void delURLFromXML(String Name, String Parent) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc;
			try {
				doc = dBuilder.parse(favFile);

				doc.getDocumentElement().normalize();
				NodeList folders = doc.getElementsByTagName(TypeOfXMLNode.folder.toString());

				for (int temp = 0; temp < folders.getLength(); temp++) {

					Node nNode = folders.item(temp);

					NamedNodeMap attributes = nNode.getAttributes();
					Node FolderName = attributes.getNamedItem(TypeOfXMLAttr.name.toString());
					if (FolderName.getNodeValue().equals(Parent)) {

						NodeList FolderEntries = nNode.getChildNodes();
						for (int tempfol = 0; tempfol < FolderEntries.getLength(); tempfol++) {

							Node nNodeFol = FolderEntries.item(tempfol);
							if (nNodeFol.getAttributes().getNamedItem(TypeOfXMLAttr.name.toString()).getNodeValue()
									.equals(Name)
									&& nNodeFol.getNodeName().toString().equals(TypeOfXMLNode.url.toString()))

							{
								nNode.removeChild(nNodeFol);
							}

						}

						DOMSource source = new DOMSource(doc);

						TransformerFactory transformerFactory = TransformerFactory.newInstance();
						Transformer transformer = transformerFactory.newTransformer();
						StreamResult result = new StreamResult(favFile.getPath());
						transformer.transform(source, result);
					}
				}
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

	}

	public void delFolderFromXML(String Name) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc;
			try {
				doc = dBuilder.parse(favFile);
				Element root = doc.getDocumentElement();
				doc.getDocumentElement().normalize();
				NodeList folders = doc.getElementsByTagName(TypeOfXMLNode.folder.toString());

				for (int temp = 0; temp < folders.getLength(); temp++) {

					Node nNode = folders.item(temp);

					NamedNodeMap attributes = nNode.getAttributes();
					Node FolderName = attributes.getNamedItem(TypeOfXMLAttr.name.toString());
					if (FolderName.getNodeValue().equals(Name)) {

						root.removeChild(nNode);

					}

					DOMSource source = new DOMSource(doc);

					TransformerFactory transformerFactory = TransformerFactory.newInstance();
					Transformer transformer = transformerFactory.newTransformer();
					StreamResult result = new StreamResult(favFile.getPath());
					transformer.transform(source, result);
				}

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

	}

	class TreeObject implements IAdaptable {
		private TreeParent parent;

		public String Name;
		private TypeOfEntry Type;
		private String TechnicalName;
		private String Description;

		public TreeObject(String Name, TypeOfEntry Type, String Description, String TechnicalName) {
			this.Name = Name;
			this.setType(Type);
			this.setDescription(Description);
			this.setTechnicalName(TechnicalName);
		}

		public String getName() {
			return Name;
		}

		public TypeOfEntry getType() {
			return Type;
		}

		public void setType(TypeOfEntry type) {
			Type = type;
		}

		public void setParent(TreeParent parent) {
			this.parent = parent;
		}

		public TreeParent getParent() {
			return parent;
		}

		public String toString() {
			return getName();
		}

		public <T> T getAdapter(Class<T> key) {
			return null;
		}

		public String getTechnicalName() {
			return TechnicalName;
		}

		public void setTechnicalName(String technicalName) {
			TechnicalName = technicalName;
		}

		public String getDescription() {
			return Description;
		}

		public void setDescription(String description) {
			Description = description;
		}
	}

	class TreeParent extends TreeObject {
		private ArrayList children;
		private boolean ProjectIndependent;
		private String Project;

		public TreeParent(String name, String description, boolean ProjectIndependent, String Project) {
			super(name, TypeOfEntry.Folder, description, "");
			children = new ArrayList();
			this.ProjectIndependent = ProjectIndependent;
			this.Project = Project;
		}

		public void addChild(TreeObject child) {
			children.add(child);
			child.setParent(this);
		}

		public void removeChild(TreeObject child) {
			children.remove(child);
			child.setParent(null);
		}

		public TreeObject[] getChildren() {
			return (TreeObject[]) children.toArray(new TreeObject[children.size()]);
		}

		public boolean hasChildren() {
			return children.size() > 0;
		}
	}

	class ViewContentProvider implements ITreeContentProvider {
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

			invisibleRoot = new TreeParent("", "", true, "");

			Bundle bundle = FrameworkUtil.getBundle(getClass());
			stateLoc = Platform.getStateLocation(bundle);

			favFile = new File(stateLoc.toFile(), favFileName);
			if (favFile.exists() == false) {
				try {
					favFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			try {
				dBuilder = dbFactory.newDocumentBuilder();
				Document doc;
				try {
					doc = dBuilder.parse(favFile);

					doc.getDocumentElement().normalize();

					System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

					NodeList nList = doc.getDocumentElement().getChildNodes();

					System.out.println("----------------------------");

					for (int temp = 0; temp < nList.getLength(); temp++) {

						Node nNode = nList.item(temp);

						System.out.println("\nCurrent Element :" + nNode.getNodeName());

						if (nNode.getNodeType() == Node.ELEMENT_NODE) {

							Element eElement = (Element) nNode;
							TreeParent parent = new TreeParent(eElement.getAttribute("name"),
									eElement.getAttribute("description"),
									Boolean.parseBoolean(eElement.getAttribute("projectIndependent")),
									eElement.getAttribute("project"));
							boolean projectIsIndependent = Boolean
									.parseBoolean(eElement.getAttribute("projectIndependent"));
							if (projectIsIndependent == false) {
								String ProjectName = getProjectName();

								if (!parent.Project.equals(ProjectName)) {
									continue;
								}
							}

							invisibleRoot.addChild(parent);

							NodeList Children = eElement.getChildNodes();

							for (int tempChild = 0; tempChild < Children.getLength(); tempChild++) {

								Node nNodeChild = Children.item(tempChild);

								System.out.println("\nCurrent Element :" + nNodeChild.getNodeName());

								if (nNodeChild.getNodeType() == Node.ELEMENT_NODE) {

									Element eElementChild = (Element) nNodeChild;
									switch (nNodeChild.getNodeName()) {
									case "transaction":
										parent.addChild(new TreeObject(eElementChild.getAttribute("name"),
												TypeOfEntry.Transaction, eElementChild.getAttribute("description"),
												eElementChild.getAttribute("technicalName")));
										break;
									case "url":
										parent.addChild(new TreeObject(eElementChild.getAttribute("name"),
												TypeOfEntry.URL, eElementChild.getAttribute("description"),
												eElementChild.getAttribute("technicalName")));
										break;
									}

								}
							}

							System.out.println("Name : " + eElement.getAttribute("name"));

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

		private void initialize() {
			createTreeNodes();
		}
	}

	class ViewLabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {

			switch (columnIndex) {
			case 0:
				String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
				if (element instanceof TreeParent)
					imageKey = ISharedImages.IMG_OBJ_FOLDER;

				if (element instanceof TreeObject)
				{
					TreeObject Node = (TreeObject)element;
					switch (Node.Type)
					{
					case Transaction:
						imageKey = ISharedImages.IMG_DEF_VIEW;
						break;
					case URL:
						imageKey = ISharedImages.IMG_OBJ_ELEMENT;
						break;
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
	public Favorites() {
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
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(viewer);
		Tree tree = viewer.getTree();
		tree.setHeaderVisible(true);
		TreeColumn columnName = new TreeColumn(tree, SWT.LEFT);
		columnName.setText("Name");
		columnName.setWidth(100);
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

			// linkWithEditorAction.setText("Link with Editor");
			// linkWithEditorAction.setImageDescriptor(IAction.AS_CHECK_BOX);

		};
		linkWithEditorAction.setText("Link with Editor");
		linkWithEditorAction.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED));
		linkWithEditorAction.setChecked(linkingActive);
		getViewSite().getActionBars().getToolBarManager().add(linkWithEditorAction);
		getSite().getPage().addPartListener(linkWithEditorPartListener);

	}

	@Override
	public void editorActivated(IEditorPart activeEditor) {
		if (linkingActive && !getViewSite().getPage().isPartVisible(this)) {

			if (!LinkedEditorProject.equals(getProjectName())) {

				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				IWorkbenchWindow window = page.getWorkbenchWindow();
				ISelection ADTselection = window.getSelectionService().getSelection();
				LinkedProject = ProjectUtil.getActiveAdtCoreProject(ADTselection, null, null,
						IAdtCoreProject.ABAP_PROJECT_NATURE);
				LinkedEditorProject = LinkedProject.getName();

				refreshViewer();
			}
			return;
		}

	}

	private void refreshViewer() {
		// showMessage(LinkedEditorProject);
		if (viewer != null) {
			ViewContentProvider ContentProvider = (ViewContentProvider) viewer.getContentProvider();
			ContentProvider.initialize();
			viewer.refresh();
		}
	}

	protected void toggleLinking() {
		// this.linkingActive = checked;
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
					manager.add(actAddFolder);
					manager.add(actAddTransaction);
					manager.add(actAddURL);
					manager.add(new Separator());
					manager.add(actDelFolder);
					manager.add(new Separator());
					drillDownAdapter.addNavigationActions(manager);
					// Other plug-ins can contribute there actions here
					manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
				} else if (object instanceof TreeObject) {

					manager.add(new Separator());

					switch (object.Type) {
					case Transaction:
						manager.add(actDelTransaction);
						break;
					case URL:
						manager.add(actDelURL);
						break;
					}

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
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
	}

	private void makeActions() {
		actAddFolder = new Action() {
			public void run() {
				FolderDialog FolderDialog = new FolderDialog(viewer.getControl().getShell());
				FolderDialog.create();
				if (FolderDialog.open() == Window.OK) {
					addFolderToXML(FolderDialog.getName(), FolderDialog.getDescription(), FolderDialog.getPrjInd(),
							getProjectName());
					System.out.println(FolderDialog.getName());
					System.out.println(FolderDialog.getDescription());
					refreshViewer();
				}

			}
		};
		actAddFolder.setText("Add New Folder");
		actAddFolder.setToolTipText("Add New Folder");
		actAddFolder.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ADD));

		actAddTransaction = new Action() {
			public void run() {
				TransactionDialog TrDialog = new TransactionDialog(viewer.getControl().getShell());
				TrDialog.create();
				if (TrDialog.open() == Window.OK) {

					if (viewer.getSelection() instanceof IStructuredSelection) {
						IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

						TreeObject object = (TreeObject) selection.getFirstElement();

						if (object instanceof TreeParent) {

							addTransactionToXML(TrDialog.getName(), TrDialog.getDescription(), object.Name);
							System.out.println(TrDialog.getName());
							System.out.println(TrDialog.getDescription());
							refreshViewer();
						}

					}
				}
			}
		};
		actAddTransaction.setText("Add Transaction");
		actAddTransaction.setToolTipText("Transaction");
		actAddTransaction.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ADD));

		actAddURL = new Action() {
			public void run() {
				URLDialog URLDialog = new URLDialog(viewer.getControl().getShell());
				URLDialog.create();
				if (URLDialog.open() == Window.OK) {
					if (viewer.getSelection() instanceof IStructuredSelection) {
						IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

						TreeObject object = (TreeObject) selection.getFirstElement();

						if (object instanceof TreeParent) {

							addURLToXML(URLDialog.getName(), URLDialog.getDescription(), URLDialog.getURL(),
									object.Name);
							System.out.println(URLDialog.getName());
							System.out.println(URLDialog.getDescription());
							refreshViewer();
						}

					}
				}
			}
		};
		actAddURL.setText("Add URL");
		actAddURL.setToolTipText("URL");
		actAddURL.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ADD));

		actDelURL = new Action() {
			public void run() {
				if (viewer.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

					TreeObject object = (TreeObject) selection.getFirstElement();

					if (object instanceof TreeObject) {
						delURLFromXML(object.Name, object.parent.Name);
						refreshViewer();
					}
				}
			}
		};
		actDelURL.setText("Delete URL");
		actDelURL.setToolTipText("URL");
		actDelURL.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));

		actDelFolder = new Action() {
			public void run() {
				if (viewer.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

					TreeObject object = (TreeObject) selection.getFirstElement();

					if (object instanceof TreeParent) {
						delFolderFromXML(object.Name);
						refreshViewer();
					}

				}
			}
		};
		actDelFolder.setText("Delete Folder");
		actDelFolder.setToolTipText("Folder");
		actDelFolder.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));

		actDelTransaction = new Action() {
			public void run() {
				if (viewer.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

					TreeObject object = (TreeObject) selection.getFirstElement();

					if (object instanceof TreeObject) {
						delTransactionFromXML(object.Name, object.parent.Name);
						refreshViewer();
					}

				}
			}
		};
		actDelTransaction.setText("Delete Transaction");
		actDelTransaction.setToolTipText("Transaction");
		actDelTransaction.setImageDescriptor(
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
					LinkedEditorProject = LinkedProject.getName();
				}

				if (LinkedProject != null) {
					if (obj instanceof TreeObject) {
						TypeOfEntry NodeType = ((TreeObject) obj).getType();
						switch (NodeType) {
						case Transaction:
							AdtSapGuiEditorUtilityFactory.createSapGuiEditorUtility()
									.openEditorAndStartTransaction(LinkedProject, obj.toString(), true);
							;
						case Folder:
						case URL:
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

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
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

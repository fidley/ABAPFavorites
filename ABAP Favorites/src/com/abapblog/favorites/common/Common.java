package com.abapblog.favorites.common;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
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
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.abapblog.favorites.common.CommonTypes.TypeOfEntry;
import com.abapblog.favorites.common.CommonTypes.TypeOfObject;
import com.abapblog.favorites.common.CommonTypes.TypeOfXMLAttr;
import com.abapblog.favorites.common.CommonTypes.TypeOfXMLNode;
import com.abapblog.favorites.views.Favorites;
import com.abapblog.favoritesDO.views.FavoritesDO;
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

public class Common {

	public static final String favFileName = "favorites.xml";
	public static File favFile;
	public static TreeViewer ViewerFavorites;
	public static TreeViewer ViewerFavoritesDO;

	public Action actAddFolder;
	public Action actAddRootFolder;
	public Action actAddClass;
	public Action actAddInterface;
	public Action actAddFunctionGroup;
	public Action actAddFunctionModule;
	public Action actAddTransaction;
	public Action actAddProgram;
	public Action actAddURL;
	public Action actAddADTLink;
	public Action actDelete;
	public Action actDelFolder;
	public Action actAddView;
	public Action actAddTable;
	public Action actAddCDS;
	public Action actAddAMDP;
	public Action actAddMessageClass;
	public Action actAddSearchHelp;
	public Action doubleClickAction;
	public Action actEdit;
	public Action actExportFavorites;
	public Action actImportFavorites;
	public String TempLinkedEditorProject;
	public IProject TempLinkedProject;
	private TypeOfXMLNode FolderNode;

	public Common(TypeOfXMLNode FolderNode) {
		this.FolderNode = FolderNode;
	}

	public static String getProjectName() {
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

	public static IProject getProjectByName(String projectName) {
		try {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			return project;
		} catch (Exception e) {
			return null;
		}

	}

	public static String getObjectName(TypeOfEntry ObjectType) {

		switch (ObjectType) {
		case Class:
			return "Class";
		case Include:
			return "Include";
		case FunctionGroup:
			return "Function group";
		case FunctionModule:
			return "Function module";
		case Interface:
			return "Interface";
		case Program:
			return "Program";
		case Transaction:
			return "Transaction";
		case View:
			return "View";
		case Table:
			return "Table";
		case MessageClass:
			return "Message class";
		case SearchHelp:
			return "Search Help";
		case ADTLink:
			return "ADT Link";
		default:
			return "object";
		}

	}

	public static Boolean isXMLNodeNameToUpper(String nodeName) {
		TypeOfEntry entryType = getEntryTypeFromXMLNode(nodeName);
		TypeOfXMLNode nodeType = getObjectXMLNode(entryType);

		return nodeType.isNameToUpper();

	}

	public static TypeOfXMLNode getObjectXMLNode(TypeOfEntry ObjectType) {

		switch (ObjectType) {
		case Class:
			return TypeOfXMLNode.classNode;
		case Include:
			return TypeOfXMLNode.includeNode;
		case FunctionGroup:
			return TypeOfXMLNode.functionGroupNode;
		case FunctionModule:
			return TypeOfXMLNode.functionModuleNode;
		case Interface:
			return TypeOfXMLNode.interfaceNode;
		case Program:
			return TypeOfXMLNode.programNode;
		case Transaction:
			return TypeOfXMLNode.transactionNode;
		case URL:
			return TypeOfXMLNode.urlNode;
		case Folder:
			return TypeOfXMLNode.folderNode;
		case FolderDO:
			return TypeOfXMLNode.folderDONode;
		case View:
			return TypeOfXMLNode.viewNode;
		case Table:
			return TypeOfXMLNode.tableNode;
		case MessageClass:
			return TypeOfXMLNode.messageClassNode;
		case SearchHelp:
			return TypeOfXMLNode.searchHelpNode;
		case ADTLink:
			return TypeOfXMLNode.ADTLinkNode;
		case CDSView:
			return TypeOfXMLNode.CDSViewNode;
		case AMDP:
			return TypeOfXMLNode.AMDPNode;
		default:
			return TypeOfXMLNode.programNode;
		}

	}

	public static void addObjectToXML(TypeOfEntry Type, String Name, String Description, String LongDescription,
			String Parent, TypeOfXMLNode ParentType) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc;
			try {
				doc = dBuilder.parse(favFile);

				doc.getDocumentElement().normalize();
				NodeList folders = doc.getElementsByTagName(ParentType.toString());

				for (int temp = 0; temp < folders.getLength(); temp++) {

					Node nNode = folders.item(temp);

					NamedNodeMap attributes = nNode.getAttributes();
					Node FolderID = attributes.getNamedItem(TypeOfXMLAttr.folderID.toString());

					if (FolderID.getNodeValue().contentEquals(Parent)) {

						Element ObjectElement = doc.createElement(getObjectXMLNode(Type).toString());
						ObjectElement.setAttribute(TypeOfXMLAttr.name.toString(), Name);
						ObjectElement.setAttribute(TypeOfXMLAttr.description.toString(), Description);
						ObjectElement.setAttribute(TypeOfXMLAttr.longDescription.toString(), LongDescription);

						nNode.appendChild(ObjectElement);

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

	public static void delObjectFromXML(TypeOfEntry Type, String Name, String ParentID, TypeOfXMLNode ParentNodeType) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc;
			try {
				doc = dBuilder.parse(favFile);

				doc.getDocumentElement().normalize();
				NodeList folders = doc.getElementsByTagName(ParentNodeType.toString());

				for (int temp = 0; temp < folders.getLength(); temp++) {

					Node nNode = folders.item(temp);

					NamedNodeMap attributes = nNode.getAttributes();
					Node FolderName = attributes.getNamedItem(TypeOfXMLAttr.folderID.toString());
					if (FolderName.getNodeValue().equals(ParentID)) {

						NodeList FolderEntries = nNode.getChildNodes();
						for (int tempfol = 0; tempfol < FolderEntries.getLength(); tempfol++) {

							Node nNodeFol = FolderEntries.item(tempfol);
							try {
								if (nNodeFol.getAttributes().getNamedItem(TypeOfXMLAttr.name.toString()).getNodeValue()
										.equals(Name)
										&& nNodeFol.getNodeName().toString().equals(getObjectXMLNode(Type).toString()))

								{
									nNode.removeChild(nNodeFol);

									DOMSource source = new DOMSource(doc);

									TransformerFactory transformerFactory = TransformerFactory.newInstance();
									Transformer transformer = transformerFactory.newTransformer();
									StreamResult result = new StreamResult(favFile.getPath());
									transformer.transform(source, result);
								}
							} catch (Exception e) {
								e.printStackTrace();
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

	public static void addFolderToXML(String Name, String Description, String LongDescription,
			Boolean ProjectIndependent, String ProjectName, Boolean DevObjFolder, String ParentID,
			TypeOfXMLNode ParentType) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc;
			try {
				doc = dBuilder.parse(favFile);

				doc.getDocumentElement().normalize();
				Element FolderEl = doc.createElement(ParentType.toString());
				FolderEl.setAttribute(TypeOfXMLAttr.name.toString(), Name);
				FolderEl.setAttribute(TypeOfXMLAttr.description.toString(), Description);
				FolderEl.setAttribute(TypeOfXMLAttr.longDescription.toString(), LongDescription);
				FolderEl.setAttribute(TypeOfXMLAttr.projectIndependent.toString(), ProjectIndependent.toString());
				FolderEl.setAttribute(TypeOfXMLAttr.project.toString(), ProjectName);
				FolderEl.setAttribute(TypeOfXMLAttr.devObjFolder.toString(), DevObjFolder.toString());
				FolderEl.setAttribute(TypeOfXMLAttr.folderID.toString(), UUID.randomUUID().toString());

				if (ParentID == "") {
					Element root = doc.getDocumentElement();
					root.appendChild(FolderEl);
				} else {
					NodeList folders = doc.getElementsByTagName(ParentType.toString());

					for (int temp = 0; temp < folders.getLength(); temp++) {

						Node nNode = folders.item(temp);

						NamedNodeMap attributes = nNode.getAttributes();
						Node FolderID = attributes.getNamedItem(TypeOfXMLAttr.folderID.toString());

						if (FolderID.getNodeValue().contentEquals(ParentID)) {
							nNode.appendChild(FolderEl);
							break;
						}
					}
				}

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

	public static void delFolderFromXML(String FolderId, TypeOfXMLNode ParentNodeType) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc;
			try {
				doc = dBuilder.parse(favFile);
				doc.getDocumentElement().normalize();
				NodeList folders = doc.getElementsByTagName(ParentNodeType.toString());

				for (int temp = 0; temp < folders.getLength(); temp++) {

					Node nNode = folders.item(temp);

					NamedNodeMap attributes = nNode.getAttributes();
					Node FolderName = attributes.getNamedItem(TypeOfXMLAttr.folderID.toString());
					if (FolderName.getNodeValue().equals(FolderId)) {
						Node parent = nNode.getParentNode();
						parent.removeChild(nNode);
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

	public static void editFolderInXML(String FolderID, String Name, String Description, String LongDescription,
			Boolean ProjectIndependent, String ProjectName, Boolean DevObjFolder, TypeOfXMLNode ParentNodeType) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc;
			try {
				doc = dBuilder.parse(favFile);
				// Element root = doc.getDocumentElement();
				doc.getDocumentElement().normalize();
				NodeList folders = doc.getElementsByTagName(ParentNodeType.toString());

				for (int temp = 0; temp < folders.getLength(); temp++) {

					Node nNode = folders.item(temp);

					NamedNodeMap attributes = nNode.getAttributes();
					Node FolderName = attributes.getNamedItem(TypeOfXMLAttr.folderID.toString());
					if (FolderName.getNodeValue().equals(FolderID)) {

						Element FolderEl = (Element) nNode;
						FolderEl.setAttribute(TypeOfXMLAttr.name.toString(), Name);
						FolderEl.setAttribute(TypeOfXMLAttr.description.toString(), Description);
						FolderEl.setAttribute(TypeOfXMLAttr.longDescription.toString(), LongDescription);
						FolderEl.setAttribute(TypeOfXMLAttr.projectIndependent.toString(),
								ProjectIndependent.toString());
						FolderEl.setAttribute(TypeOfXMLAttr.project.toString(), ProjectName);
						FolderEl.setAttribute(TypeOfXMLAttr.devObjFolder.toString(), DevObjFolder.toString());

						// root.removeChild(nNode);
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

	public static void addURLToXML(String Name, String Description, String LongDescription, String URL, String Parent,
			TypeOfXMLNode NodeType, TypeOfXMLNode ParentNodeType) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc;
			try {
				doc = dBuilder.parse(favFile);

				doc.getDocumentElement().normalize();
				NodeList folders = doc.getElementsByTagName(ParentNodeType.toString());

				for (int temp = 0; temp < folders.getLength(); temp++) {

					Node nNode = folders.item(temp);

					NamedNodeMap attributes = nNode.getAttributes();
					Node FolderID = attributes.getNamedItem(TypeOfXMLAttr.folderID.toString());
					if (FolderID.getNodeValue().contentEquals(Parent)) {

						Element URLEl = doc.createElement(NodeType.toString());
						URLEl.setAttribute(TypeOfXMLAttr.name.toString(), Name);
						URLEl.setAttribute(TypeOfXMLAttr.description.toString(), Description);
						URLEl.setAttribute(TypeOfXMLAttr.technicalName.toString(), URL);
						URLEl.setAttribute(TypeOfXMLAttr.longDescription.toString(), LongDescription);

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
					Common.addObjectToXML(Type, Name, NaDialog.getDescription(),
							((TreeParent) Folder).getLongDescription(), ((TreeParent) Folder).getFolderID(),
							((TreeParent) Folder).getTypeOfFolder());
					refreshViewer(viewer);
				}

			}
		}
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

					editFolderInXML(Folder.getFolderID(), Name, FoDialog.getDescription(),
							FoDialog.getLongDescription(), FoDialog.getPrjInd(), getProjectName(),
							FoDialog.getDevObjectFolder(), Folder.getTypeOfFolder());

					refreshViewer(viewer);
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

						Common.delObjectFromXML(Type, Object.getName(), Object.getParent().getFolderID(),
								Object.getParent().getTypeOfFolder());
						Common.addURLToXML(Name, UrlDialog.getDescription(), UrlDialog.getLongDescription(),
								UrlDialog.getURL(), Object.getParent().getFolderID(), TypeOfXMLNode.urlNode,
								Object.getParent().getTypeOfFolder());

						refreshViewer(viewer);
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

						Common.delObjectFromXML(Type, Object.getName(), Object.getParent().getFolderID(),
								Object.getParent().getTypeOfFolder());
						Common.addURLToXML(Name, UrlDialog.getDescription(), UrlDialog.getLongDescription(),
								UrlDialog.getURL(), Object.getParent().getFolderID(), TypeOfXMLNode.ADTLinkNode,
								Object.getParent().getTypeOfFolder());

						refreshViewer(viewer);
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
						Common.delObjectFromXML(Type, Object.getName(), Object.getParent().getFolderID(),
								Object.getParent().getTypeOfFolder());
						Common.addObjectToXML(Type, Name, NaDialog.getDescription(), NaDialog.getLongDescription(),
								Object.getParent().getFolderID(), Object.getParent().getTypeOfFolder());
						refreshViewer(viewer);
					}
					break;
				}

			}
		}
	}

	public static void refreshViewer(TreeViewer viewer) {
		// showMessage(LinkedEditorProject);
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

	public static TypeOfEntry getEntryTypeFromXMLNode(String nodeName) {
		if (nodeName.equals(TypeOfXMLNode.classNode.toString())) {
			return TypeOfEntry.Class;
		}

		if (nodeName.equals(TypeOfXMLNode.functionGroupNode.toString())) {
			return TypeOfEntry.FunctionGroup;
		}

		if (nodeName.equals(TypeOfXMLNode.functionModuleNode.toString())) {
			return TypeOfEntry.FunctionModule;
		}

		if (nodeName.equals(TypeOfXMLNode.includeNode.toString())) {
			return TypeOfEntry.Include;
		}
		if (nodeName.equals(TypeOfXMLNode.interfaceNode.toString())) {
			return TypeOfEntry.Interface;
		}
		if (nodeName.equals(TypeOfXMLNode.programNode.toString())) {
			return TypeOfEntry.Program;
		}

		if (nodeName.equals(TypeOfXMLNode.transactionNode.toString())) {
			return TypeOfEntry.Transaction;
		}

		if (nodeName.equals(TypeOfXMLNode.urlNode.toString())) {
			return TypeOfEntry.URL;
		}

		if (nodeName.equals(TypeOfXMLNode.viewNode.toString())) {
			return TypeOfEntry.View;
		}
		if (nodeName.equals(TypeOfXMLNode.tableNode.toString())) {
			return TypeOfEntry.Table;
		}
		if (nodeName.equals(TypeOfXMLNode.messageClassNode.toString())) {
			return TypeOfEntry.MessageClass;
		}
		if (nodeName.equals(TypeOfXMLNode.searchHelpNode.toString())) {
			return TypeOfEntry.SearchHelp;
		}
		if (nodeName.equals(TypeOfXMLNode.ADTLinkNode.toString())) {
			return TypeOfEntry.ADTLink;
		}
		if (nodeName.equals(TypeOfXMLNode.CDSViewNode.toString())) {
			return TypeOfEntry.CDSView;
		}
		if (nodeName.equals(TypeOfXMLNode.AMDPNode.toString())) {
			return TypeOfEntry.AMDP;
		}
		return null;
	}

	public static void deleteObjectFromAction(TreeViewer viewer) {
		if (viewer.getSelection() instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

			TreeObject object = (TreeObject) selection.getFirstElement();

			if (object instanceof TreeObject) {
				TreeObject treeObj = (TreeObject) object;
				delObjectFromXML(treeObj.Type, object.Name, object.parent.getFolderID(),
						object.parent.getTypeOfFolder());
				refreshViewer(viewer);
			}
		}
	}

	public static boolean checkType(String sapType, TypeOfEntry type) {

		if (sapType.equals(TypeOfObject.classType.toString()) && type == TypeOfEntry.Class) {
			return true;
		}

		if (sapType.equals(TypeOfObject.interfaceType.toString()) && type == TypeOfEntry.Interface) {
			return true;
		}

		if (sapType.equals(TypeOfObject.FunctionGroupIncludeType.toString()) && type == TypeOfEntry.Program) {
			return true;
		}
		if (sapType.equals(TypeOfObject.FunctionGroupType.toString()) && type == TypeOfEntry.FunctionGroup) {
			return true;
		}
		if (sapType.equals(TypeOfObject.FunctionModuleRFCType.toString()) && type == TypeOfEntry.FunctionModule) {
			return true;
		}

		if (sapType.equals(TypeOfObject.FunctionModuleType.toString()) && type == TypeOfEntry.FunctionModule) {
			return true;
		}

		if (sapType.equals(TypeOfObject.includeType.toString()) && type == TypeOfEntry.Program) {
			return true;
		}

		if (sapType.equals(TypeOfObject.programType.toString()) && type == TypeOfEntry.Program) {
			return true;
		}

		if (sapType.equals(TypeOfObject.ViewType.toString()) && type == TypeOfEntry.View) {
			return true;
		}

		if (sapType.equals(TypeOfObject.TableType.toString()) && type == TypeOfEntry.Table) {
			return true;
		}

		if (sapType.equals(TypeOfObject.MessageClassType.toString()) && type == TypeOfEntry.MessageClass) {
			return true;
		}

		if (sapType.equals(TypeOfObject.SearchHelpType.toString()) && type == TypeOfEntry.SearchHelp) {
			return true;
		}

		if (sapType.equals(TypeOfObject.CDSViewType.toString()) && type == TypeOfEntry.CDSView) {
			return true;
		}

		if (sapType.equals(TypeOfObject.AMDPType.toString()) && type == TypeOfEntry.AMDP) {
			return true;
		}

		return false;
	}

	public static void createFavFile() {
		Bundle bundle = FrameworkUtil.getBundle((com.abapblog.favorites.views.Favorites.class));
		IPath stateLoc = Platform.getStateLocation(bundle);

		Common.favFile = new File(stateLoc.toFile(), Common.favFileName);
		if (Common.favFile.exists() == false) {
			try {
				Common.favFile.createNewFile();

				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder;
				dBuilder = dbFactory.newDocumentBuilder();
				Document doc;
				doc = dBuilder.newDocument();
				Node root = doc.createElement("root");
				doc.appendChild(root);
				DOMSource source = new DOMSource(doc);

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				StreamResult result = new StreamResult(Common.favFile.getPath());
				transformer.transform(source, result);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		;
	}

	public static void copyFavFile(String Path) {
		Bundle bundle = FrameworkUtil.getBundle((com.abapblog.favorites.views.Favorites.class));
		IPath stateLoc = Platform.getStateLocation(bundle);

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();

			Document doc = null;
			Common.favFile = new File(stateLoc.toFile(), Common.favFileName);
			if (Common.favFile.exists() == false) {
				try {
					Common.favFile.createNewFile();
					doc = dBuilder.newDocument();
					Node root = doc.createElement("root");
					doc.appendChild(root);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				doc = dBuilder.parse(favFile);

			}
			;

			DOMSource source = new DOMSource(doc);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			StreamResult result = new StreamResult(Path);
			transformer.transform(source, result);

		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
	}

	public static void replaceFavFile(String Path) {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();

			Document doc = null;
			File SourceFile = new File(Path);
			if (SourceFile.exists() == false) {
				return;
			}

			doc = dBuilder.parse(SourceFile);
			DOMSource source = new DOMSource(doc);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			StreamResult result = new StreamResult(Common.favFile.getPath());
			transformer.transform(source, result);
			if (ViewerFavorites != null)
				refreshViewer(ViewerFavorites);
			if (ViewerFavoritesDO != null)
				refreshViewer(ViewerFavoritesDO);

		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
	};

	public void makeActions(TreeViewer viewer) {
		AFIcons AFIcon = new AFIcons();
		actAddFolder = new Action() {
			@Override
			public void run() {
				Boolean FolderDO = false;
				switch (FolderNode) {
				case folderNode:
					FolderDO = false;
					break;
				case folderDONode:
					FolderDO = true;
					break;
				}
				;
				FolderDialog FolderDialog = new FolderDialog(viewer.getControl().getShell(), FolderDO);
				FolderDialog.create();
				if (FolderDialog.open() == Window.OK) {

					if (viewer.getSelection() instanceof IStructuredSelection) {
						IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

						TreeObject Folder = (TreeObject) selection.getFirstElement();

						if (Folder instanceof TreeParent) {
							Common.addFolderToXML(FolderDialog.getName(), FolderDialog.getDescription(),
									FolderDialog.getLongDescription(), FolderDialog.getPrjInd(),
									Common.getProjectName(), FolderDialog.getDevObjectFolder(),
									((TreeParent) Folder).getFolderID(), ((TreeParent) Folder).getTypeOfFolder());
							Common.refreshViewer(viewer);
						}

					}

				}

			}
		};
		actAddFolder.setText("Add New Folder");
		actAddFolder.setToolTipText("Add New Folder");
		actAddFolder.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ADD));

		actAddRootFolder = new Action() {
			@Override
			public void run() {
				Boolean FolderDO = false;
				switch (FolderNode) {
				case folderNode:
					FolderDO = false;
					break;
				case folderDONode:
					FolderDO = true;
					break;
				}
				;
				FolderDialog FolderDialog = new FolderDialog(viewer.getControl().getShell(), FolderDO);
				FolderDialog.create();
				if (FolderDialog.open() == Window.OK) {

					Common.addFolderToXML(FolderDialog.getName(), FolderDialog.getDescription(),
							FolderDialog.getLongDescription(), FolderDialog.getPrjInd(), Common.getProjectName(),
							FolderDialog.getDevObjectFolder(), "", FolderNode);
					Common.refreshViewer(viewer);
				}

			}

		};

		actAddRootFolder.setText("Add New Folder");
		actAddRootFolder.setToolTipText("Add New Folder");
		actAddRootFolder.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ADD));

		actAddTransaction = new Action() {
			@Override
			public void run() {
				Common.addObjectFromAction(TypeOfEntry.Transaction, true, viewer);
			}

		};
		actAddTransaction.setText("Add Transaction");
		actAddTransaction.setToolTipText("Transaction");
		actAddTransaction.setImageDescriptor(AFIcon.getTransactionImgDescr());

		actAddProgram = new Action() {
			@Override
			public void run() {
				Common.addObjectFromAction(TypeOfEntry.Program, true, viewer);
			}

		};
		actAddProgram.setText("Add Program");
		actAddProgram.setToolTipText("Program");
		actAddProgram.setImageDescriptor(AFIcon.getProgramIconImgDescr());

		actAddView = new Action() {
			@Override
			public void run() {
				Common.addObjectFromAction(TypeOfEntry.View, true, viewer);
			}

		};
		actAddView.setText("Add View");
		actAddView.setToolTipText("View");
		actAddView.setImageDescriptor(AFIcon.getViewIconImgDescr());

		actAddTable = new Action() {
			@Override
			public void run() {
				Common.addObjectFromAction(TypeOfEntry.Table, true, viewer);
			}

		};
		actAddTable.setText("Add Table");
		actAddTable.setToolTipText("Table");
		actAddTable.setImageDescriptor(AFIcon.getTableIconImgDescr());

		actAddMessageClass = new Action() {
			@Override
			public void run() {
				Common.addObjectFromAction(TypeOfEntry.MessageClass, true, viewer);
			}

		};
		actAddMessageClass.setText("Add Message Class");
		actAddMessageClass.setToolTipText("Message Class");
		actAddMessageClass.setImageDescriptor(AFIcon.getMessageClassIconImgDescr());

		actAddSearchHelp = new Action() {
			@Override
			public void run() {
				Common.addObjectFromAction(TypeOfEntry.SearchHelp, true, viewer);
			}

		};
		actAddSearchHelp.setText("Add Search Help");
		actAddSearchHelp.setToolTipText("Search Help");
		actAddSearchHelp.setImageDescriptor(AFIcon.getSearchHelpIconImgDescr());

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

							Common.addURLToXML(URLDialog.getName(), URLDialog.getDescription(),
									URLDialog.getLongDescription(), URLDialog.getURL(),
									((TreeParent) object).getFolderID(), TypeOfXMLNode.urlNode,
									object.getParent().getTypeOfFolder());
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
							Common.addURLToXML(URLDialog.getName(), URLDialog.getDescription(),
									URLDialog.getLongDescription(), ADTLink, ((TreeParent) object).getFolderID(),
									TypeOfXMLNode.ADTLinkNode, object.getParent().getTypeOfFolder());
							System.out.println(URLDialog.getName());
							System.out.println(URLDialog.getDescription());
							Common.refreshViewer(viewer);
						}

					}
				}
			}
		};

		actAddADTLink.setText("Add ADT Link");
		actAddADTLink.setToolTipText("ADT Link");
		actAddADTLink.setImageDescriptor(AFIcon.getADTLinkImgDescr());

		actAddCDS = new Action() {
			@Override
			public void run() {
				Common.addObjectFromAction(TypeOfEntry.CDSView, true, viewer);
			}

		};
		actAddCDS.setText("Add CDS View");
		actAddCDS.setToolTipText("CDS");
		actAddCDS.setImageDescriptor(AFIcon.getCDSViewImgDescr());

		actAddAMDP = new Action() {
			@Override
			public void run() {
				Common.addObjectFromAction(TypeOfEntry.AMDP, true, viewer);
			}

		};
		actAddAMDP.setText("Add AMDP");
		actAddAMDP.setToolTipText("AMDP");
		actAddAMDP.setImageDescriptor(AFIcon.getAMDPImgDescr());

		actAddClass = new Action() {
			@Override
			public void run() {
				Common.addObjectFromAction(TypeOfEntry.Class, true, viewer);
			}

		};
		actAddClass.setText("Add class");
		actAddClass.setToolTipText("Class");
		actAddClass.setImageDescriptor(AFIcon.getClassIconImgDescr());

		actAddInterface = new Action() {
			@Override
			public void run() {
				Common.addObjectFromAction(TypeOfEntry.Interface, true, viewer);
			}

		};
		actAddInterface.setText("Add interface");
		actAddInterface.setToolTipText("Interface");
		actAddInterface.setImageDescriptor(AFIcon.getInterfaceIconImgDescr());

		actAddFunctionGroup = new Action() {
			@Override
			public void run() {
				Common.addObjectFromAction(TypeOfEntry.FunctionGroup, true, viewer);
			}

		};
		actAddFunctionGroup.setText("Add function group");
		actAddFunctionGroup.setToolTipText("Function Group");
		actAddFunctionGroup.setImageDescriptor(AFIcon.getFunctionGroupIconImgDescr());

		actAddFunctionModule = new Action() {
			@Override
			public void run() {
				Common.addObjectFromAction(TypeOfEntry.FunctionModule, true, viewer);
			}

		};
		actAddFunctionModule.setText("Add function module");
		actAddFunctionModule.setToolTipText("Function Module");
		actAddFunctionModule.setImageDescriptor(AFIcon.getFunctionModuleIconImgDescr());

		actDelFolder = new Action() {
			@Override
			public void run() {
				if (viewer.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

					TreeObject object = (TreeObject) selection.getFirstElement();

					if (object instanceof TreeParent) {
						Common.delFolderFromXML(((TreeParent) object).getFolderID(),
								((TreeParent) object).getTypeOfFolder());
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
			@Override
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
			@Override
			public void run() {
				Common.deleteObjectFromAction(viewer);
			}
		};
		actDelete.setText("Delete");
		actDelete.setToolTipText("Delete");
		actDelete.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));

		doubleClickAction = new Action() {
			@Override
			public void run() {

				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();

				if (TempLinkedProject == null) {
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					IWorkbenchWindow window = page.getWorkbenchWindow();
					ISelection ADTselection = window.getSelectionService().getSelection();
					TempLinkedProject = ProjectUtil.getActiveAdtCoreProject(ADTselection, null, null,
							IAdtCoreProject.ABAP_PROJECT_NATURE);
					try {
						TempLinkedProject.refreshLocal(IProject.DEPTH_INFINITE, new NullProgressMonitor());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				if (TempLinkedProject != null) {
					TempLinkedEditorProject = TempLinkedProject.getName();

					if (obj instanceof TreeObject) {

						TreeObject nodeObject = ((TreeObject) obj);
						TypeOfEntry NodeType = nodeObject.getType();
						TreeParent nodeParent = nodeObject.parent;
						switch (NodeType) {
						case Transaction:
							AdtSapGuiEditorUtilityFactory.createSapGuiEditorUtility()
									.openEditorAndStartTransaction(TempLinkedProject, obj.toString(), true);
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
							openAdtLink(TempLinkedProject, new String(((TreeObject) obj).getTechnicalName()));
							break;
						case Program:
							if (nodeParent.getDevObjProject() == false) {
								runObject(TempLinkedProject, nodeObject.getName(), nodeObject.Type);
								break;
							} else {
								openObject(TempLinkedProject, nodeObject.getName(), nodeObject.Type);
								break;
							}
						case Table:

						default:
							if (nodeParent.getDevObjProject() == false) {
								runObject(TempLinkedProject, nodeObject.getName(), nodeObject.Type);
								break;
							} else {
								openObject(TempLinkedProject, nodeObject.getName(), nodeObject.Type);
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
		actExportFavorites = new Action() {
			@Override
			public void run() {
				exportFavorites(viewer);
			}

		};
		actExportFavorites.setText("Export Favorites");
		actExportFavorites.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_SAVEAS_EDIT));
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
			Common.copyFavFile(ExportFileName);
	}

	private static void importFavorites(TreeViewer viewer) {
		Shell shell = viewer.getControl().getShell();
		FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.setFilterNames(new String[] { "XML", "All Files (*.*)" });
		dialog.setFilterExtensions(new String[] { "*.xml", "*.*" });
		dialog.setFileName("favorites.xml");

		String ImportFileName = dialog.open();
		if (!ImportFileName.equals(""))
			Common.replaceFavFile(ImportFileName);
	};

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

	@SuppressWarnings("restriction")
	public static String getObjectDescription(IProject project, String objectName, TypeOfEntry type) {
		String Name = "";

		try {

			List<IAdtObjectReference> res = AdtRisQuickSearchFactory
					.createQuickSearch(project, new NullProgressMonitor()).execute(objectName, 10, false, false, null);
			for (IAdtObjectReference ref : res) {
				if (Common.checkType(ref.getType(), type)) {

					Pattern regexPatern = Pattern.compile("^\\S*");
					Matcher regexMatch = regexPatern.matcher(ref.getName());
					while (regexMatch.find()) {
						Name = regexMatch.group(0);
					}
					if (Name.equalsIgnoreCase(objectName)) {
						return ref.getDescription();
					}
				}
			}
		} catch (OperationCanceledException | RisQuickSearchNotSupportedException e) {
			AdtLogging.getLogger(AdtRisQuickSearchFactory.class).error(e);
		}
		return "";
	}

	public static void openAdtLink(IProject project, String adtLink) {
		adtLink = adtLink.replace("(?<=\'/\'/)(.*?)(?=\'/)", project.getName().toString());
		AdtNavigationServiceFactory.createNavigationService().navigateWithExternalLink(adtLink, project);
		return;
	}

	@SuppressWarnings("restriction")
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
						SapGuiPlugin.getDefault().openEditorForObject(project, ref, true,
								WorkbenchAction.EXECUTE.toString(), null, Collections.<String, String>emptyMap());
						return;
					}
				}
			}
		} catch (OperationCanceledException | RisQuickSearchNotSupportedException e) {
			// AdtLogging.getLogger(getClass()).error(e);
		}
	}

	public TreeParent createTreeNodes(TypeOfXMLNode FolderXMLNode, Object Favorite) {

		String LinkedEditorProject;

		try {
			LinkedEditorProject = ((Favorites) Favorite).getLinkedEditorProject();
		} catch (Exception e) {
			LinkedEditorProject = ((FavoritesDO) Favorite).getLinkedEditorProject();
		}
		TreeParent invisibleRoot = new TreeParent("", "", true, "", "", Favorite, false, "root");

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
						Common.createSubNodes(FolderXMLNode, eElement, invisibleRoot, LinkedEditorProject, Favorite);
					}
				}

				DOMSource source = new DOMSource(doc);

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				StreamResult result = new StreamResult(favFile.getPath());
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
			String linkedEditorProject, Object favorite) {

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
			if (projectIsIndependent == false) {
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
						Common.createSubNodes(folderXMLNode, eElementChild, parent, linkedEditorProject, favorite);
					}

					else {

						String childName = eElementChild.getAttribute(TypeOfXMLAttr.name.toString());
						if (Common.isXMLNodeNameToUpper(eElementChild.getTagName())) {
							childName = childName.toUpperCase();
						}
						parent.addChild(new TreeObject(childName,
								Common.getEntryTypeFromXMLNode(nNodeChild.getNodeName()),
								eElementChild.getAttribute(TypeOfXMLAttr.description.toString()),
								eElementChild.getAttribute(TypeOfXMLAttr.technicalName.toString()),
								eElementChild.getAttribute(TypeOfXMLAttr.longDescription.toString()), favorite));
					}
				}
			}
		}

	}
}

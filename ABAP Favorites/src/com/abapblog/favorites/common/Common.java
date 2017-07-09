package com.abapblog.favorites.common;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
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

public class Common {

	public static final String favFileName = "favorites.xml";
	public static File favFile;

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
		default:
			return "object";
		}

	}

	public static Boolean isXMLNodeNameToUpper(String nodeName) {
		TypeOfEntry entryType = getEntryTypeFromXMLNode(nodeName);
		TypeOfXMLNode nodeType = getObjectXMLNode(entryType);

		return nodeType.isNameToUpper();

	}

	public static String getObjectInternalName(TypeOfEntry ObjectType) {

		switch (ObjectType) {
		case Class:
			return TypeOfObject.classType.toString();
		case Include:
			return TypeOfObject.includeType.toString();
		case FunctionGroup:
			return TypeOfObject.FunctionGroupType.toString();
		case FunctionModule:
			return TypeOfObject.FunctionModuleType.toString();
		case Interface:
			return TypeOfObject.interfaceType.toString();
		case Program:
			return TypeOfObject.programType.toString();
		default:
			return "object";
		}

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
		default:
			return TypeOfXMLNode.programNode;
		}

	}

	public static void addObjectToXML(TypeOfEntry Type, String Name, String Description, String Parent,
			TypeOfXMLNode ParentType) {
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
					Node FolderName = attributes.getNamedItem(TypeOfXMLAttr.name.toString());

					if (FolderName.getNodeValue().contentEquals(Parent)) {

						Element ObjectElement = doc.createElement(getObjectXMLNode(Type).toString());
						ObjectElement.setAttribute(TypeOfXMLAttr.name.toString(), Name);
						ObjectElement.setAttribute(TypeOfXMLAttr.description.toString(), Description);

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

	public static void delObjectFromXML(TypeOfEntry Type, String Name, String Parent) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc;
			try {
				doc = dBuilder.parse(favFile);

				doc.getDocumentElement().normalize();
				NodeList folders = doc.getElementsByTagName(TypeOfXMLNode.folderNode.toString());

				for (int temp = 0; temp < folders.getLength(); temp++) {

					Node nNode = folders.item(temp);

					NamedNodeMap attributes = nNode.getAttributes();
					Node FolderName = attributes.getNamedItem(TypeOfXMLAttr.name.toString());
					if (FolderName.getNodeValue().equals(Parent)) {

						NodeList FolderEntries = nNode.getChildNodes();
						for (int tempfol = 0; tempfol < FolderEntries.getLength(); tempfol++) {

							Node nNodeFol = FolderEntries.item(tempfol);
							try {
								if (nNodeFol.getAttributes().getNamedItem(TypeOfXMLAttr.name.toString()).getNodeValue()
										.equals(Name)
										&& nNodeFol.getNodeName().toString().equals(getObjectXMLNode(Type).toString()))

								{
									nNode.removeChild(nNodeFol);
								}
							} catch (Exception e) {
								e.printStackTrace();
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

	public static void addFolderToXML(String Name, String Description, Boolean ProjectIndependent, String ProjectName,
			Boolean DevObjFolder) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc;
			try {
				doc = dBuilder.parse(favFile);

				doc.getDocumentElement().normalize();
				Element root = doc.getDocumentElement();

				Element FolderEl = doc.createElement(TypeOfXMLNode.folderNode.toString());
				FolderEl.setAttribute(TypeOfXMLAttr.name.toString(), Name);
				FolderEl.setAttribute(TypeOfXMLAttr.description.toString(), Description);
				FolderEl.setAttribute(TypeOfXMLAttr.projectIndependent.toString(), ProjectIndependent.toString());
				FolderEl.setAttribute(TypeOfXMLAttr.project.toString(), ProjectName);
				FolderEl.setAttribute(TypeOfXMLAttr.devObjFolder.toString(), DevObjFolder.toString());

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

	public static void addFolderDOToXML(String Name, String Description, Boolean ProjectIndependent, String ProjectName,
			Boolean DevObjFolder) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc;
			try {
				doc = dBuilder.parse(favFile);

				doc.getDocumentElement().normalize();
				Element root = doc.getDocumentElement();

				Element FolderEl = doc.createElement(TypeOfXMLNode.folderDONode.toString());
				FolderEl.setAttribute(TypeOfXMLAttr.name.toString(), Name);
				FolderEl.setAttribute(TypeOfXMLAttr.description.toString(), Description);
				FolderEl.setAttribute(TypeOfXMLAttr.projectIndependent.toString(), ProjectIndependent.toString());
				FolderEl.setAttribute(TypeOfXMLAttr.project.toString(), ProjectName);
				FolderEl.setAttribute(TypeOfXMLAttr.devObjFolder.toString(), "true");

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

	public static void delFolderFromXML(String Name) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc;
			try {
				doc = dBuilder.parse(favFile);
				Element root = doc.getDocumentElement();
				doc.getDocumentElement().normalize();
				NodeList folders = doc.getElementsByTagName(TypeOfXMLNode.folderNode.toString());

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

	public static void addURLToXML(String Name, String Description, String URL, String Parent) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc;
			try {
				doc = dBuilder.parse(favFile);

				doc.getDocumentElement().normalize();
				NodeList folders = doc.getElementsByTagName(TypeOfXMLNode.folderNode.toString());

				for (int temp = 0; temp < folders.getLength(); temp++) {

					Node nNode = folders.item(temp);

					NamedNodeMap attributes = nNode.getAttributes();
					Node FolderName = attributes.getNamedItem(TypeOfXMLAttr.name.toString());
					if (FolderName.getNodeValue().toString().equals(Parent)) {

						Element URLEl = doc.createElement(TypeOfXMLNode.urlNode.toString());
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

	public static void addObjectFromAction(TypeOfEntry Type, Boolean NameToUpper, TreeViewer viewer) {
		NameDialog NaDialog = new NameDialog(viewer.getControl().getShell(), Type);
		NaDialog.create(Type);
		if (NaDialog.open() == Window.OK) {

			if (viewer.getSelection() instanceof IStructuredSelection) {
				IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

				TreeObject object = (TreeObject) selection.getFirstElement();

				if (object instanceof TreeParent) {
					String Name = NaDialog.getName();
					if (NameToUpper) {
						Name = Name.toUpperCase();
					}
					Common.addObjectToXML(Type, Name, NaDialog.getDescription(), object.Name,
							((TreeParent) object).getTypeOfFolder());
					refreshViewer(viewer);
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

		return null;
	}

	public static void deleteObjectFromAction(TreeViewer viewer) {
		if (viewer.getSelection() instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

			TreeObject object = (TreeObject) selection.getFirstElement();

			if (object instanceof TreeObject) {
				TreeObject treeObj = (TreeObject) object;
				delObjectFromXML(treeObj.Type, object.Name, object.parent.Name);
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
}

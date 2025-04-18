package com.abapblog.favorites.xml;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.abapblog.favorites.common.CommonTypes.TypeOfEntry;
import com.abapblog.favorites.common.CommonTypes.TypeOfXMLAttr;
import com.abapblog.favorites.common.CommonTypes.TypeOfXMLNode;
import com.abapblog.favorites.superview.Superview;

public class XMLhandler {
	private XMLhandler() {
		throw new IllegalStateException("Utility class");
	}

	public static String addFolderToXML(String name, String description, String longDescription, Boolean Independent,
			String projectName, Boolean devObjFolder, String parentID, TypeOfXMLNode parentType, String folderURL,
			String workingSet, Boolean ProjectDependent, Boolean WorksingSetDependent) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		String newFolderId = UUID.randomUUID().toString();
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc;
			try {
				doc = dBuilder.parse(getFavFile());

				doc.getDocumentElement().normalize();
				Element folder = doc.createElement(parentType.toString());
				folder.setAttribute(TypeOfXMLAttr.name.toString(), name);
				folder.setAttribute(TypeOfXMLAttr.description.toString(), description);
				folder.setAttribute(TypeOfXMLAttr.longDescription.toString(), longDescription);
				folder.setAttribute(TypeOfXMLAttr.independent.toString(), Independent.toString());
				folder.setAttribute(TypeOfXMLAttr.project.toString(), projectName);
				folder.setAttribute(TypeOfXMLAttr.devObjFolder.toString(), devObjFolder.toString());
				folder.setAttribute(TypeOfXMLAttr.folderID.toString(), newFolderId);
				folder.setAttribute(TypeOfXMLAttr.folderUrl.toString(), folderURL);
				folder.setAttribute(TypeOfXMLAttr.projectDependent.toString(), ProjectDependent.toString());
				folder.setAttribute(TypeOfXMLAttr.workingSetDependent.toString(), WorksingSetDependent.toString());
				folder.setAttribute(TypeOfXMLAttr.workingSet.toString(), workingSet);
				if (parentID.equals("")) {
					Element root = doc.getDocumentElement();
					root.appendChild(folder);
				} else {
					NodeList folders = doc.getElementsByTagName(parentType.toString());

					for (int temp = 0; temp < folders.getLength(); temp++) {

						Node nNode = folders.item(temp);

						NamedNodeMap attributes = nNode.getAttributes();
						Node FolderID = attributes.getNamedItem(TypeOfXMLAttr.folderID.toString());

						if (FolderID.getNodeValue().contentEquals(parentID)) {
							nNode.appendChild(folder);
							break;
						}
					}
				}

				DOMSource source = new DOMSource(doc);

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				StreamResult result = new StreamResult(getFavFile().getPath());
				transformer.transform(source, result);

			} catch (SAXException | IOException | TransformerException e) {
				e.printStackTrace();
				newFolderId = "";
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			newFolderId = "";
		}
		return newFolderId;

	}

	public static void addObjectToXML(TypeOfEntry type, String name, String description, String longDescription,
			String technicalName, String parent, TypeOfXMLNode parentType, String commandID) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			addObjectToXMLStream(type, name, description, longDescription, technicalName, parent, parentType, dBuilder,
					commandID);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

	}

	private static void addObjectToXMLStream(TypeOfEntry type, String name, String description, String longDescription,
			String technicalName, String parent, TypeOfXMLNode parentType, DocumentBuilder dBuilder, String commandID)
			throws TransformerFactoryConfigurationError {
		Document doc;
		try {
			doc = dBuilder.parse(getFavFile());
			doc.getDocumentElement().normalize();
			NodeList folders = doc.getElementsByTagName(parentType.toString());

			for (int temp = 0; temp < folders.getLength(); temp++) {

				Node nNode = folders.item(temp);

				NamedNodeMap attributes = nNode.getAttributes();
				Node FolderID = attributes.getNamedItem(TypeOfXMLAttr.folderID.toString());
				if (FolderID == null) {
					continue;
				}
				if (FolderID.getNodeValue().contentEquals(parent)) {

					Element ObjectElement = doc.createElement(getObjectXMLNode(type).toString());
					ObjectElement.setAttribute(TypeOfXMLAttr.name.toString(), name);
					ObjectElement.setAttribute(TypeOfXMLAttr.description.toString(), description);
					ObjectElement.setAttribute(TypeOfXMLAttr.longDescription.toString(), longDescription);
					ObjectElement.setAttribute(TypeOfXMLAttr.technicalName.toString(), technicalName);
					ObjectElement.setAttribute(TypeOfXMLAttr.commandID.toString(), commandID);
					nNode.appendChild(ObjectElement);

					DOMSource source = new DOMSource(doc);

					TransformerFactory transformerFactory = TransformerFactory.newInstance();
					Transformer transformer = transformerFactory.newTransformer();
					StreamResult result = new StreamResult(getFavFile().getPath());
					transformer.transform(source, result);
				}
			}
		} catch (SAXException | IOException | TransformerException e) {
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
				doc = dBuilder.parse(getFavFile());
				doc.getDocumentElement().normalize();
				NodeList folders = doc.getElementsByTagName(ParentNodeType.toString());

				for (int temp = 0; temp < folders.getLength(); temp++) {

					Node nNode = folders.item(temp);

					NamedNodeMap attributes = nNode.getAttributes();
					Node FolderName = attributes.getNamedItem(TypeOfXMLAttr.folderID.toString());
					if (FolderName == null) {
						continue;
					}
					if (FolderName.getNodeValue().equals(FolderId)) {
						Node parent = nNode.getParentNode();
						parent.removeChild(nNode);
						DOMSource source = new DOMSource(doc);

						TransformerFactory transformerFactory = TransformerFactory.newInstance();
						Transformer transformer = transformerFactory.newTransformer();
						StreamResult result = new StreamResult(getFavFile().getPath());
						transformer.transform(source, result);
						return;
					}

				}

			} catch (SAXException | IOException | TransformerException e) {
				e.printStackTrace();
			}

		} catch (ParserConfigurationException e) {
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
				doc = dBuilder.parse(getFavFile());

				doc.getDocumentElement().normalize();
				NodeList folders = doc.getElementsByTagName(ParentNodeType.toString());

				for (int temp = 0; temp < folders.getLength(); temp++) {

					Node nNode = folders.item(temp);

					NamedNodeMap attributes = nNode.getAttributes();
					Node FolderName = attributes.getNamedItem(TypeOfXMLAttr.folderID.toString());
					if (FolderName == null) {
						continue;
					}
					if (FolderName.getNodeValue().equals(ParentID)) {

						NodeList FolderEntries = nNode.getChildNodes();
						for (int tempfol = 0; tempfol < FolderEntries.getLength(); tempfol++) {

							Node nNodeFol = FolderEntries.item(tempfol);
							try {
								if (nNodeFol.getNodeName().toString().equals(getObjectXMLNode(Type).toString())
										&& nNodeFol.getAttributes() != null
										&& nNodeFol.getAttributes().getNamedItem(TypeOfXMLAttr.name.toString())
												.getNodeValue().toUpperCase().equals(Name.toUpperCase()))

								{
									nNode.removeChild(nNodeFol);

									DOMSource source = new DOMSource(doc);

									TransformerFactory transformerFactory = TransformerFactory.newInstance();
									Transformer transformer = transformerFactory.newTransformer();
									StreamResult result = new StreamResult(getFavFile().getPath());
									transformer.transform(source, result);
									break;
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					}
				}
			} catch (SAXException | IOException e) {
				e.printStackTrace();
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

	}

	public static void editFolderInXML(String FolderID, String Name, String Description, String LongDescription,
			Boolean Independent, String ProjectName, Boolean DevObjFolder, TypeOfXMLNode ParentNodeType,
			String folderURL, String workingSet, Boolean ProjectDependent, Boolean WorkingSetDependent) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc;
			try {
				doc = dBuilder.parse(getFavFile());
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
						FolderEl.setAttribute(TypeOfXMLAttr.independent.toString(), Independent.toString());
						FolderEl.setAttribute(TypeOfXMLAttr.projectDependent.toString(), ProjectDependent.toString());
						FolderEl.setAttribute(TypeOfXMLAttr.workingSetDependent.toString(),
								WorkingSetDependent.toString());
						FolderEl.setAttribute(TypeOfXMLAttr.project.toString(), ProjectName);
						FolderEl.setAttribute(TypeOfXMLAttr.devObjFolder.toString(), DevObjFolder.toString());
						FolderEl.setAttribute(TypeOfXMLAttr.folderUrl.toString(), folderURL);
						FolderEl.setAttribute(TypeOfXMLAttr.workingSet.toString(), workingSet);
						DOMSource source = new DOMSource(doc);

						TransformerFactory transformerFactory = TransformerFactory.newInstance();
						Transformer transformer = transformerFactory.newTransformer();
						StreamResult result = new StreamResult(getFavFile().getPath());
						transformer.transform(source, result);
						break;
					}

				}

			} catch (SAXException | IOException | TransformerException e) {
				e.printStackTrace();
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
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
		if (nodeName.equals(TypeOfXMLNode.Package.toString())) {
			return TypeOfEntry.Package;
		}
		return null;
	}

	public static void moveFolderInXML(String SourceFolderId, String TargetFolderId, TypeOfXMLNode SourceParentNodeType,
			TypeOfXMLNode TargetParentNodeType) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc;
			try {
				doc = dBuilder.parse(getFavFile());
				doc.getDocumentElement().normalize();
				NodeList sourceFolders = doc.getElementsByTagName(SourceParentNodeType.toString());

				for (int temp = 0; temp < sourceFolders.getLength(); temp++) {

					Node nNode = sourceFolders.item(temp);

					NamedNodeMap attributes = nNode.getAttributes();
					Node FolderName = attributes.getNamedItem(TypeOfXMLAttr.folderID.toString());
					if (FolderName == null) {
						continue;
					}
					if (FolderName.getNodeValue().equals(SourceFolderId)) {

						NodeList targetFolders = doc.getElementsByTagName(TargetParentNodeType.toString());
						for (int tempTarget = 0; tempTarget < targetFolders.getLength(); tempTarget++) {

							Node nNodeTarget = targetFolders.item(tempTarget);

							NamedNodeMap attributesTarget = nNodeTarget.getAttributes();
							Node FolderNameTarget = attributesTarget.getNamedItem(TypeOfXMLAttr.folderID.toString());

							if (TargetFolderId.equals("root")) {

								Element root = doc.getDocumentElement();
								nNode.getParentNode().removeChild(nNode);
								root.appendChild(nNode);
								DOMSource source = new DOMSource(doc);

								TransformerFactory transformerFactory = TransformerFactory.newInstance();
								Transformer transformer = transformerFactory.newTransformer();
								StreamResult result = new StreamResult(getFavFile().getPath());
								transformer.transform(source, result);
								break;
							}

							if (FolderNameTarget.getNodeValue().equals(TargetFolderId)) {
								nNode.getParentNode().removeChild(nNode);
								nNodeTarget.appendChild(nNode);

								DOMSource source = new DOMSource(doc);

								TransformerFactory transformerFactory = TransformerFactory.newInstance();
								Transformer transformer = transformerFactory.newTransformer();
								StreamResult result = new StreamResult(getFavFile().getPath());
								transformer.transform(source, result);
								break;
							}
						}
						break;

					}

				}

			} catch (SAXException | IOException | TransformerException e) {
				e.printStackTrace();
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

	}

	private static File favFile;

	public static TypeOfXMLNode getObjectXMLNode(TypeOfEntry ObjectType) {
		if (ObjectType == null) {
			return TypeOfXMLNode.programNode;
		}
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
		case Package:
			return TypeOfXMLNode.Package;
		default:
			return TypeOfXMLNode.programNode;
		}

	}

	public static Boolean isXMLNodeNameToUpper(String nodeName) {
		TypeOfEntry entryType = getEntryTypeFromXMLNode(nodeName);
		TypeOfXMLNode nodeType = getObjectXMLNode(entryType);

		return nodeType.isNameToUpper();

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
			StreamResult result = new StreamResult(getFavFile().getPath());
			transformer.transform(source, result);
			Superview.refreshActiveViews();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	public static void copyFavFile(String Path) {
		Bundle bundle = FrameworkUtil.getBundle((com.abapblog.favorites.views.Favorites.class));
		IPath stateLoc = Platform.getStateLocation(bundle);

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();

			Document doc = null;
			favFile = new File(stateLoc.toFile(), FAV_FILE_NAME);
			if (getFavFile().exists() == false) {
				try {
					getFavFile().createNewFile();
					doc = dBuilder.newDocument();
					Node root = doc.createElement("root");
					doc.appendChild(root);

				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				doc = dBuilder.parse(getFavFile());

			}
			;

			DOMSource source = new DOMSource(doc);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			StreamResult result = new StreamResult(Path);
			transformer.transform(source, result);

		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	public static void createFavFile() {
		Bundle bundle = FrameworkUtil.getBundle((com.abapblog.favorites.views.Favorites.class));
		IPath stateLoc = Platform.getStateLocation(bundle);

		favFile = new File(stateLoc.toFile(), FAV_FILE_NAME);
		if (getFavFile().exists() == false) {
			try {
				getFavFile().createNewFile();

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
				StreamResult result = new StreamResult(getFavFile().getPath());
				transformer.transform(source, result);

			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			}
		}
		;
	}

	public static File getFavFile() {
		return favFile;
	}

	public static final String FAV_FILE_NAME = "favorites.xml";

	public static void removeCommandIDFromObject(String commandID) {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc;
			try {
				doc = dBuilder.parse(getFavFile());
				doc.getDocumentElement().normalize();
				NodeList elements = doc.getElementsByTagName("*");

				for (int temp = 0; temp < elements.getLength(); temp++) {

					Node nNode = elements.item(temp);

					NamedNodeMap attributes = nNode.getAttributes();
					Node currentNode = attributes.getNamedItem(TypeOfXMLAttr.commandID.toString());
					if (currentNode == null)
						continue;
					if (currentNode.getNodeValue().equals(commandID)) {
						Element FolderEl = (Element) nNode;
						FolderEl.setAttribute(TypeOfXMLAttr.commandID.toString(), "");
						DOMSource source = new DOMSource(doc);

						TransformerFactory transformerFactory = TransformerFactory.newInstance();
						Transformer transformer = transformerFactory.newTransformer();
						StreamResult result = new StreamResult(getFavFile().getPath());
						transformer.transform(source, result);
					}

				}

			} catch (SAXException | IOException | TransformerException e) {
				e.printStackTrace();
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

	}

	public static void copyFolderInXML(String SourceFolderId, String TargetFolderId, TypeOfXMLNode SourceParentNodeType,
			TypeOfXMLNode TargetParentNodeType) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		Boolean changed = false;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc;
			try {
				doc = dBuilder.parse(getFavFile());
				doc.getDocumentElement().normalize();
				NodeList sourceFolders = doc.getElementsByTagName(SourceParentNodeType.toString());

				for (int temp = 0; temp < sourceFolders.getLength(); temp++) {

					Node nNode = sourceFolders.item(temp);

					NamedNodeMap attributes = nNode.getAttributes();
					Node FolderName = attributes.getNamedItem(TypeOfXMLAttr.folderID.toString());
					if (FolderName == null) {
						continue;
					}

					if (FolderName.getNodeValue().equals(SourceFolderId)) {

						Node copiedNode = doc.importNode(nNode, true);
						copiedNode.getAttributes().getNamedItem(TypeOfXMLAttr.folderID.toString())
								.setNodeValue(UUID.randomUUID().toString());
						updateFolderIdInCopiedFolders(copiedNode);

						if (TargetFolderId.equals("root")) {

							Element root = doc.getDocumentElement();
							root.appendChild(copiedNode);
							DOMSource source = new DOMSource(doc);

							TransformerFactory transformerFactory = TransformerFactory.newInstance();
							Transformer transformer = transformerFactory.newTransformer();
							StreamResult result = new StreamResult(getFavFile().getPath());
							transformer.transform(source, result);
							break;

						} else {
							NodeList targetFolders = doc.getElementsByTagName(TargetParentNodeType.toString());
							for (int tempTarget = 0; tempTarget < targetFolders.getLength(); tempTarget++) {

								Node nNodeTarget = targetFolders.item(tempTarget);

								NamedNodeMap attributesTarget = nNodeTarget.getAttributes();
								Node FolderNameTarget = attributesTarget
										.getNamedItem(TypeOfXMLAttr.folderID.toString());

								if (FolderNameTarget.getNodeValue().equals(TargetFolderId)) {
									nNodeTarget.appendChild(copiedNode);
									changed = true;
									break;

								}
							}
							break;
						}
					}

				}
				if (changed == true) {
					DOMSource source = new DOMSource(doc);

					TransformerFactory transformerFactory = TransformerFactory.newInstance();
					Transformer transformer = transformerFactory.newTransformer();
					StreamResult result = new StreamResult(getFavFile().getPath());
					transformer.transform(source, result);
				}
			} catch (SAXException | IOException e) {
				e.printStackTrace();
			}

		} catch (ParserConfigurationException | TransformerException e) {
			e.printStackTrace();
		}

	}

	private static void updateFolderIdInCopiedFolders(Node nNode) {
		for (int i = 0; i < nNode.getChildNodes().getLength(); i++) {
			Node nNodeChild = nNode.getChildNodes().item(i);
			if (nNodeChild.getNodeType() == Node.ELEMENT_NODE) {
				NamedNodeMap attributesChild = nNodeChild.getAttributes();
				Node childFolderId = attributesChild.getNamedItem(TypeOfXMLAttr.folderID.toString());
				if (childFolderId != null) {
					childFolderId.setNodeValue(UUID.randomUUID().toString());
				}
				updateFolderIdInCopiedFolders(nNodeChild);
			}
		}
	}

}

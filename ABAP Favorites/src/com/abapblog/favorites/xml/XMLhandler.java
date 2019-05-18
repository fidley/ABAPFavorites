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
					if (FolderID == null) {
						continue;
					}
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

	public static void moveFolderInXML(String SourceFolderId, String TargetFolderId, TypeOfXMLNode SourceParentNodeType,
			TypeOfXMLNode TargetParentNodeType) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc;
			try {
				doc = dBuilder.parse(favFile);
				doc.getDocumentElement().normalize();
				NodeList sourceFolders = doc.getElementsByTagName(SourceParentNodeType.toString());

				for (int temp = 0; temp < sourceFolders.getLength(); temp++) {

					Node nNode = sourceFolders.item(temp);

					NamedNodeMap attributes = nNode.getAttributes();
					Node FolderName = attributes.getNamedItem(TypeOfXMLAttr.folderID.toString());
					if (FolderName == null)
					{
						continue;
					}
					if (FolderName.getNodeValue().equals(SourceFolderId)) {

						NodeList targetFolders = doc.getElementsByTagName(TargetParentNodeType.toString());
						for (int tempTarget = 0; tempTarget < targetFolders.getLength(); tempTarget++) {

							Node nNodeTarget = targetFolders.item(tempTarget);

							NamedNodeMap attributesTarget = nNodeTarget.getAttributes();
							Node FolderNameTarget = attributesTarget.getNamedItem(TypeOfXMLAttr.folderID.toString());
							if (FolderNameTarget.getNodeValue().equals(TargetFolderId)) {

								// Node parent = nNode.getParentNode();
								nNodeTarget.appendChild(nNode);
								// parent.removeChild(nNode);
								DOMSource source = new DOMSource(doc);

								TransformerFactory transformerFactory = TransformerFactory.newInstance();
								Transformer transformer = transformerFactory.newTransformer();
								StreamResult result = new StreamResult(favFile.getPath());
								transformer.transform(source, result);
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

	public static File favFile;

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
			StreamResult result = new StreamResult(favFile.getPath());
			transformer.transform(source, result);
			Superview.refreshActiveViews();
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

	public static void copyFavFile(String Path) {
		Bundle bundle = FrameworkUtil.getBundle((com.abapblog.favorites.views.Favorites.class));
		IPath stateLoc = Platform.getStateLocation(bundle);

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();

			Document doc = null;
			favFile = new File(stateLoc.toFile(), favFileName);
			if (favFile.exists() == false) {
				try {
					favFile.createNewFile();
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

	public static void createFavFile() {
		Bundle bundle = FrameworkUtil.getBundle((com.abapblog.favorites.views.Favorites.class));
		IPath stateLoc = Platform.getStateLocation(bundle);

		favFile = new File(stateLoc.toFile(), favFileName);
		if (favFile.exists() == false) {
			try {
				favFile.createNewFile();

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
				StreamResult result = new StreamResult(favFile.getPath());
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

	public static final String favFileName = "favorites.xml";

}

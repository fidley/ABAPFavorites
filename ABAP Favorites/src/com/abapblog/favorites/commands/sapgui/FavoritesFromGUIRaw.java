package com.abapblog.favorites.commands.sapgui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.abapblog.favorites.common.CommonTypes.TypeOfEntry;
import com.abapblog.favorites.common.CommonTypes.TypeOfXMLNode;
import com.abapblog.favorites.superview.Superview;
import com.abapblog.favorites.xml.XMLhandler;
import com.sap.conn.jco.JCoTable;

public class FavoritesFromGUIRaw {

//Column reporttype   reporttype  char(2)  Report type
//Column report   extdreport  char(48)  Extended Program Name
//Column parent_id   menu_num_5  numc(5)  Counter for menu ID
//Column object_id   menu_num_5  numc(5)  Counter for menu ID
//Column menu_level   menu_num_2  numc(2)  Level in the menu
//Column book_info   color_info  char(12)  Structures: name of the structure
//Column attributes   menu_attr  char(10)  Attributes of the menu
//Column sort_order   menu_num_5  numc(5)  Counter for menu ID
//Column x_pos   menu_num_5  numc(5)  Counter for menu ID
//Column y_pos   menu_num_5  numc(5)  Counter for menu ID
//Column sap_guid   hier_guid  char(32)  Unique ID - 32 characters
//Column text   char100sm  char(100)  Character 100
//Column target_sys   rfcdest  char(32)  Logical destination (specified in function call)

	private JCoTable main;

//	Column object_id   menu_num_5  numc(5)  Counter for menu ID
//	Column link_type   url_type  char(1)  Customized ('C') or standard menu ('S') flag
//	Column url   agr_url  char(255)  URL for the SAP Portal
//	Column pers_mini   flag  char(1)  General Flag
//	Column pers_win

	private JCoTable urls;
	private GUIFavorite root;
	private String projectName;

	public FavoritesFromGUIRaw(JCoTable favoritesMain, JCoTable favoritesURLs, String favoritesProjectName) {
		main = favoritesMain;
		urls = favoritesURLs;
		projectName = favoritesProjectName;
		if (main == null || main.isEmpty())
			return;

		HashMap<String, GUIFavorite> guiFolders = new HashMap<>();
		root = new GUIFavorite(TypeOfEntry.Folder, "", "", "00001", "", "");
		guiFolders.put(root.getId(), root);

		addFolders(guiFolders);
		addFavorites(guiFolders);
	}

	public void addFavoritesToNewFolder(String newFolderName) {
		if (root == null)
			return;
		String rootId = XMLhandler.addFolderToXML(newFolderName, projectName + " GUI Favorites", "", false, projectName,
				false, "", TypeOfXMLNode.folderNode);
		if (rootId.isEmpty())
			return;

		addChildren(root.getChildren(), rootId);
		Superview.refreshActiveViews();
	}

	private void addChildren(ArrayList<GUIFavorite> children, String parentId) {
		Iterator iter = children.iterator();
		while (iter.hasNext()) {
			GUIFavorite favorite = (GUIFavorite) iter.next();

			switch (favorite.getType()) {
			case Folder:
				String newFolderId = XMLhandler.addFolderToXML(favorite.getText(), "", "", false, projectName, false,
						parentId, TypeOfXMLNode.folderNode);
				addChildren(favorite.getChildren(), newFolderId);
				break;

			case Transaction:
				XMLhandler.addObjectToXML(favorite.getType(), favorite.getReport(), favorite.getText(), "",
						favorite.getReport(), parentId, TypeOfXMLNode.folderNode, "");
				break;

			case URL:
				XMLhandler.addObjectToXML(favorite.getType(), favorite.getText(), "", "", favorite.getUrl(), parentId,
						TypeOfXMLNode.folderNode, "");
				break;
			default:
				break;
			}

		}

	}

	private void addFavorites(HashMap<String, GUIFavorite> guiFolders) {
		for (int i = 0; i < main.getNumRows(); i++) {
			main.setRow(i);
			String reportType = main.getString("REPORTTYPE");
			String report = main.getString("REPORT");
			String parentId = main.getString("PARENT_ID");
			String id = main.getString("OBJECT_ID");
			String text = main.getString("TEXT");
			addTransaction(guiFolders, reportType, report, parentId, id, text);
			addUrl(guiFolders, reportType, report, parentId, id, text);

		}
	}

	private void addTransaction(HashMap<String, GUIFavorite> guiFolders, String reportType, String report,
			String parentId, String id, String text) {
		if (reportType.equals("TR")) {
			GUIFavorite transaction = new GUIFavorite(TypeOfEntry.Transaction, report, parentId, id, text, "");
			guiFolders.get(parentId).addChild(transaction);
		}
	}

	private void addUrl(HashMap<String, GUIFavorite> guiFolders, String reportType, String report, String parentId,
			String id, String text) {
		if ((reportType.equals("OT") && report.equals("URL"))) {
			for (int i = 0; i < urls.getNumRows(); i++) {
				urls.setRow(i);
				String objectId = urls.getString("OBJECT_ID");
				if (objectId.equals(id)) {
					String favoriteUrl = urls.getString("URL");
					GUIFavorite url = new GUIFavorite(TypeOfEntry.URL, report, parentId, id, text, favoriteUrl);
					guiFolders.get(parentId).addChild(url);
					return;
				}
			}

		}
	}

	private void addFolders(HashMap<String, GUIFavorite> guiFolders) {
		ArrayList<GUIFavorite> toReprocess = new ArrayList<>();
		for (int i = 0; i < main.getNumRows(); i++) {
			main.setRow(i);
			String reportType = main.getString("REPORTTYPE");
			if (reportType.isEmpty()) {
				String parentId = main.getString("PARENT_ID");
				String id = main.getString("OBJECT_ID");
				String text = main.getString("TEXT");
				GUIFavorite newFolder = new GUIFavorite(TypeOfEntry.Folder, "", parentId, id, text, "");
				GUIFavorite parent = guiFolders.get(parentId);
				if (parent != null) {
					parent.addChild(newFolder);
				} else {
					toReprocess.add(newFolder);
				}
				guiFolders.put(id, newFolder);

			}
		}
		reprocessParentlessFolders(toReprocess, guiFolders);
	}

	private void reprocessParentlessFolders(ArrayList<GUIFavorite> toReprocess,
			HashMap<String, GUIFavorite> guiFolders) {
		ArrayList<GUIFavorite> toReprocessAgain = new ArrayList<>();
		Iterator iter = toReprocess.iterator();
		while (iter.hasNext()) {
			GUIFavorite folder = (GUIFavorite) iter.next();
			GUIFavorite parent = guiFolders.get(folder.getParentId());
			if (parent != null) {
				parent.addChild(folder);
			} else {
				toReprocessAgain.add(folder);
			}
		}
		if (!toReprocessAgain.isEmpty())
			reprocessParentlessFolders(toReprocessAgain, guiFolders);

	}

}

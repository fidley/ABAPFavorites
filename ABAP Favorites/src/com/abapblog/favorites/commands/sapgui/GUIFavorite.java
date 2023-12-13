package com.abapblog.favorites.commands.sapgui;

import java.util.ArrayList;

import com.abapblog.favorites.common.CommonTypes.TypeOfEntry;

public class GUIFavorite {
	private TypeOfEntry type;
	private String report = "";
	private String parentId = "";
	private String id = "";
	private String text = "";
	private String url = "";

	private ArrayList<GUIFavorite> children = new ArrayList<GUIFavorite>();

	public GUIFavorite(TypeOfEntry type, String report, String parentId, String id, String text, String url) {
		this.type = type;
		this.report = report;
		this.parentId = parentId;
		this.id = id;
		this.text = text;
		this.url = url;
	}

	public void addChild(GUIFavorite child) {
		children.add(child);
	}

	public ArrayList<GUIFavorite> getChildren() {
		return children;
	}

	public boolean isFolder() {
		if (getType().equals(""))
			return true;
		return false;
	}

	public TypeOfEntry getType() {
		return type;
	}

	public String getReport() {
		return report;
	}

	public String getParentId() {
		return parentId;
	}

	public String getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public String getUrl() {
		return url;
	}

}

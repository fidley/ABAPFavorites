package com.abapblog.favorites.superview;

import java.util.ArrayList;

import org.eclipse.jface.viewers.TreeViewer;

import com.abapblog.favorites.common.CommonTypes.TypeOfXMLNode;
import com.abapblog.favorites.tree.TreeParent;

public interface IFavorites {

	public String getID();

	public IFavorites getFavorites();

	public String getLinkedEditorProject();

	public boolean isLinkingActive();

	public String getObjectSortingType();

	public TypeOfXMLNode getFolderType();

	public void putFavToCommon();

	public void disableLinkingOfEditor();

	public void enableLinkingOfEditor();

	public TreeViewer getTreeViewer();

	public ArrayList<String> getExpandedNodes();

	public ArrayList<TreeParent> getExpandedParentNodes();

	public void showHideLinkedToColumn();

	public void showHideLongDescriptionColumn();

}

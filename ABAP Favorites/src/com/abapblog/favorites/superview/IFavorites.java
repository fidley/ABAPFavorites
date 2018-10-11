package com.abapblog.favorites.superview;

import com.abapblog.favorites.common.CommonTypes.TypeOfXMLNode;

public interface IFavorites {

	public IFavorites getFavorites();

	public String getLinkedEditorProject();

	public boolean getLinkingActive();

	public TypeOfXMLNode getFolderType();

	public void putFavToCommon();

}

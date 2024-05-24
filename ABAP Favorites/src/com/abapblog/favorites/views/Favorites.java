package com.abapblog.favorites.views;

import com.abapblog.favorites.common.Common;
import com.abapblog.favorites.common.CommonTypes.TypeOfXMLNode;
import com.abapblog.favorites.superview.IFavorites;
import com.abapblog.favorites.superview.Superview;

/**
 * Simple ABAP Favorites plug-in that was created on a base of sample TreeViewer
 * plug-in.
 *
 * For more go to ABAPBlog.com
 */

public class Favorites extends Superview {

	public final static String ID = "com.abapblog.favorites.views.Favorites";

	/**
	 * The constructor.
	 */
	public Favorites() {
		FolderNode = getFolderType();
		putFavToCommon();
		Superview.addFavoritesToActive(this);
	}

	@Override
	public IFavorites getFavorites() {
		return Favorites.this;
	}

	@Override
	public TypeOfXMLNode getFolderType() {
		return TypeOfXMLNode.folderNode;
	}

	@Override
	public void putFavToCommon() {
		Common.Favorite = this;

	}

	@Override
	public String getID() {
		return ID;
	}

}

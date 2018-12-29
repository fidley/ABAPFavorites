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

	/**
	 * The ID of the view as specified by the extension.
	 */

	/**
	 * The constructor.
	 */
	public Favorites() {
		FolderNode = getFolderType();
		ID = "com.abapblog.favorites.views.Favorites";
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

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */

}

package com.abapblog.favoritesDO.views;

import com.abapblog.favorites.common.Common;
import com.abapblog.favorites.common.CommonTypes.TypeOfXMLNode;
import com.abapblog.favorites.superview.IFavorites;
import com.abapblog.favorites.superview.superview;

/**
 * Simple ABAP Favorites plug-in that was created on a base of sample TreeViewer
 * plug-in.
 *
 * For more go to ABAPBlog.com
 */

public class FavoritesDO extends superview {

	/**
	 * The constructor.
	 */
	public FavoritesDO() {
		Utils = new Common(getFolderType());
		ID = "com.abapblog.favoritesDO.views.FavoritesDO";
	}

	@Override
	public IFavorites getFavorites() {
		return this;
	}

	@Override
	public TypeOfXMLNode getFolderType() {
		return TypeOfXMLNode.folderDONode;
	}

	@Override
	public void putFavToCommon() {
		Common.FavoriteDO = this;

	}

}

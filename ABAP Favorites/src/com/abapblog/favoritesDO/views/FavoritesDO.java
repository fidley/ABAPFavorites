package com.abapblog.favoritesDO.views;

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

public class FavoritesDO extends Superview {

	public static final String ID = "com.abapblog.favoritesDO.views.FavoritesDO";

	/**
	 * The constructor.
	 */
	public FavoritesDO() {
		FolderNode = getFolderType();

		putFavToCommon();
		Superview.addFavoritesToActive(this);
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

	@Override
	public String getID() {
		return ID;
	}

}

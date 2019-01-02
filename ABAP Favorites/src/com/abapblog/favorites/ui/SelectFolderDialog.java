package com.abapblog.favorites.ui;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.part.DrillDownAdapter;

import com.abapblog.favorites.common.Common;
import com.abapblog.favorites.common.CommonTypes.TypeOfXMLNode;
import com.abapblog.favorites.superview.AFPatternFilter;
import com.abapblog.favorites.superview.IFavorites;
import com.abapblog.favorites.superview.ViewContentProvider;
import com.abapblog.favorites.superview.ViewLabelProvider;
import com.abapblog.favorites.tree.ColumnControlListener;
import com.abapblog.favorites.tree.TreeObject;
import com.abapblog.favorites.tree.TreeParent;
import com.abapblog.favorites.views.Favorites;
import com.abapblog.favoritesDO.views.FavoritesDO;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.widgets.TabItem;

public class SelectFolderDialog extends Dialog {

	private static final String ID_FOLDER_FAVORITES_DO = "com.abapblog.favorites.ui.selectFolderDialog.FavoritesDO";
	private static final String ID_FOLDER_FAVORITES = "com.abapblog.favorites.ui.selectFolderDialog.Favorites";

	/**
	 * Create the dialog.
	 *
	 * @param parentShell
	 */
	public SelectFolderDialog(Shell parentShell) {
		super(parentShell);
	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Select folder to add the object to");
	}

	/**
	 * Create contents of the dialog.
	 *
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();

		TabFolder tabFolder = new TabFolder(container, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		if (Common.Favorite == null) {
			Common.Favorite = new Favorites();
		}

		createFavoritesTree(container, tabFolder);

		if (Common.FavoriteDO == null)
			Common.FavoriteDO = new FavoritesDO();
		createFavoritesDOTree(container, tabFolder);

		return container;
	}

	private void createFavoritesDOTree(Composite container, TabFolder tabFolder) {
		TabItem tbtmFavoritesDO = new TabItem(tabFolder, SWT.NONE);
		tbtmFavoritesDO.setText("FavoritesDO");

		AFPatternFilter filterDO = new AFPatternFilter();
		FilteredTree filteredTreeDO = new FilteredTree(tabFolder, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL, filterDO,
				true);
		ColumnControlListener columnListenerDO = new ColumnControlListener();
		columnListenerDO.setID(ID_FOLDER_FAVORITES_DO);

		TreeViewer treeViewerFavDO = filteredTreeDO.getViewer();
		Tree treeFavoritesFoldersDO = treeViewerFavDO.getTree();
		tbtmFavoritesDO.setControl(filteredTreeDO);

		setTreeColumns(columnListenerDO, treeFavoritesFoldersDO, ID_FOLDER_FAVORITES_DO);
		treeViewerFavDO
				.setContentProvider(new ViewContentProvider(TypeOfXMLNode.folderDONode, Common.FavoriteDO, container));
		// treeViewerFav.setInput(getViewSite());
		treeViewerFavDO.setInput(container);
		treeViewerFavDO.setLabelProvider(new ViewLabelProvider());
		sortTreeViewer(treeViewerFavDO);
	}

	private void createFavoritesTree(Composite container, TabFolder tabFolder) {
		TabItem tbtmFavorites = new TabItem(tabFolder, SWT.NONE);
		tbtmFavorites.setText("Favorites");

		AFPatternFilter filter = new AFPatternFilter();
		FilteredTree filteredTree = new FilteredTree(tabFolder, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL, filter, true);
		ColumnControlListener columnListener = new ColumnControlListener();
		columnListener.setID(ID_FOLDER_FAVORITES);

		TreeViewer treeViewerFav = filteredTree.getViewer();
		Tree treeFavoritesFolders = treeViewerFav.getTree();
		tbtmFavorites.setControl(filteredTree);

		setTreeColumns(columnListener, treeFavoritesFolders, ID_FOLDER_FAVORITES);
		treeViewerFav.setContentProvider(new ViewContentProvider(TypeOfXMLNode.folderNode, Common.Favorite, container));
		// treeViewerFav.setInput(getViewSite());
		treeViewerFav.setInput(container);
		treeViewerFav.setLabelProvider(new ViewLabelProvider());
		sortTreeViewer(treeViewerFav);
	}

	/**
	 * Create contents of the button bar.
	 *
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

	private void setTreeColumns(ColumnControlListener columnListener, Tree tree, String ID) {
		tree.setHeaderVisible(true);
		TreeColumn columnName = new TreeColumn(tree, SWT.LEFT);
		columnName.setText("Name");
		columnName.addControlListener(columnListener);
		loadColumnSettings(columnName, ID);
		TreeColumn columnDescr = new TreeColumn(tree, SWT.LEFT);
		columnDescr.setText("Description");
		columnDescr.addControlListener(columnListener);
		loadColumnSettings(columnDescr, ID);
		TreeColumn ColumnID = new TreeColumn(tree, SWT.LEFT);
		ColumnID.setText("ID");
		ColumnID.addControlListener(columnListener);
		ColumnID.setWidth(0);
		ColumnID.setResizable(false);
		TreeColumn ColumnFolderType = new TreeColumn(tree, SWT.LEFT);
		ColumnFolderType.setText("FolderType");
		ColumnFolderType.addControlListener(columnListener);
		ColumnFolderType.setWidth(0);
		ColumnFolderType.setResizable(false);
		TreeColumn ColumnDevObj = new TreeColumn(tree, SWT.LEFT);
		ColumnDevObj.setText("DevObjects");
		ColumnDevObj.addControlListener(columnListener);
		ColumnDevObj.setWidth(0);
		ColumnDevObj.setResizable(false);
		TreeColumn ColumnLinkedTo = new TreeColumn(tree, SWT.LEFT);
		ColumnLinkedTo.setText("Linked To");
		ColumnLinkedTo.addControlListener(columnListener);
		loadColumnSettings(ColumnLinkedTo, ID);
	}

	protected void loadColumnSettings(TreeColumn Column, String ID) {
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(ID);
		Column.setWidth(prefs.getInt("column_width" + Column.getText(), 300));
	}

	public static void saveDialogSettings(String ID) {
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(ID);

		try {
			// prefs are automatically flushed during a plugin's "super.stop()".
			prefs.flush();
		} catch (org.osgi.service.prefs.BackingStoreException e) {
			// TODO write a real exception handler.
			e.printStackTrace();
		}
	}

	public void sortTreeViewer(TreeViewer viewer) {

		viewer.setComparator(new ViewerComparator() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {

				if (e1 instanceof TreeParent && e2 instanceof TreeParent) {
					return ((TreeParent) e1).getName().compareToIgnoreCase(((TreeParent) e2).getName());
				} else if (e1 instanceof TreeParent && e2 instanceof TreeObject) {
					return (-1);
				} else if (e1 instanceof TreeObject && e2 instanceof TreeParent) {
					return (1);
				} else if (e1 instanceof TreeObject && e2 instanceof TreeObject) {
					return ((TreeObject) e1).getName().compareToIgnoreCase(((TreeObject) e2).getName());

				} else {
					throw new IllegalArgumentException("Not comparable: " + e1 + " " + e2);
				}
			}
		});
	}

	@Override
	protected void okPressed() {
		// TODO Auto-generated method stub
		saveDialogSettings(ID_FOLDER_FAVORITES);
		saveDialogSettings(ID_FOLDER_FAVORITES_DO);
		super.okPressed();
	}

	@Override
	protected void cancelPressed() {
		// TODO Auto-generated method stub
		saveDialogSettings(ID_FOLDER_FAVORITES);
		saveDialogSettings(ID_FOLDER_FAVORITES_DO);
		super.cancelPressed();
	}

}

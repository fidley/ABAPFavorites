package com.abapblog.adt.extension.passwords.view;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.part.ViewPart;

import com.abablog.adt.extension.passwords.SecureStorage;
import com.abapblog.adt.extension.passwords.tree.TreePatternFilter;





public class PasswordView extends ViewPart {
	private final String ID = "com.abapblog.adt.extension.passwords.view";
	private Actions actions = new Actions();
	private static TreeViewer viewer;
	public void createPartControl(Composite parent) {
		SecureStorage secureStorage = new SecureStorage();
		secureStorage.createNodesForSAPProjects();
       
		TreePatternFilter filter = new TreePatternFilter();
		final FilteredTree filteredTree = new FilteredTree(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL, filter,
				true);
		final ColumnControlListener columnListener = new ColumnControlListener();
		columnListener.setID(getID());
		viewer = filteredTree.getViewer();
		addDoubleClickAction(viewer);
		Tree tree = viewer.getTree();
		setTreeColumns(columnListener, tree);
		viewer.setContentProvider(new ViewContentProvider(getViewSite()));
		viewer.setInput(getViewSite());
		viewer.setLabelProvider(new ViewLabelProvider());
		hookContextMenu();
		refreshViewer(viewer);
		
		
    }
	
	public static void refreshViewer(final TreeViewer viewer) {

		if (viewer != null) {

			final Object ContentProvider = viewer.getContentProvider();
			try {
				final java.lang.reflect.Method initialize = ContentProvider.getClass().getMethod("initialize");
				initialize.invoke(ContentProvider);
			} catch (final Exception e) {

			}
			viewer.refresh();
		}
	}
 
    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus() {
    	
    }
    
    public String getID() {
    	return ID;
    }
   
	private void setTreeColumns(final ColumnControlListener columnListener, final Tree tree) {
		tree.setHeaderVisible(true);
		final TreeColumn columnUser = new TreeColumn(tree, SWT.LEFT);
		columnUser.setText("Project/Client/User");
		columnUser.addControlListener(columnListener);
		loadColumnSettings(columnUser);
		final TreeColumn columnPassword = new TreeColumn(tree, SWT.LEFT);
		columnPassword.setText("Password");
		columnPassword.addControlListener(columnListener);
		loadColumnSettings(columnPassword);
		final TreeColumn ColumnEncrypted = new TreeColumn(tree, SWT.LEFT);
		ColumnEncrypted.setText("Encrypted");
		ColumnEncrypted.addControlListener(columnListener);
		loadColumnSettings(ColumnEncrypted);
	}
	
	protected void hookContextMenu() {
		final MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		ContextMenu contextMenu = new ContextMenu(viewer);
		
		menuMgr.addMenuListener(manager -> contextMenu.fillContextMenu(manager));
		final Menu menu = menuMgr.createContextMenu(this.viewer.getControl());
		this.viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, this.viewer);
	}
	protected void loadColumnSettings(final TreeColumn Column) {
		final IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(getID());
		Column.setWidth(prefs.getInt("column_width" + Column.getText(), 300));
	}
	
	protected void addDoubleClickAction(TreeViewer viewer) {
		actions.createDoubleClick(viewer);
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				actions.doubleClick.run();
				refreshViewer(viewer);
			}
		});
	}
}
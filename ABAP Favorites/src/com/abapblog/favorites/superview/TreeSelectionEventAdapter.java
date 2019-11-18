package com.abapblog.favorites.superview;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;

/**
 * Selection adapter to handle events like "Mouse Double click" or "Pressed
 * Enter key" while also recognizing if the enter key was pressed. <br>
 * The
 * {@link TreeViewer#addDoubleClickListener(org.eclipse.jface.viewers.IDoubleClickListener)}
 * unfortunately does not return this information
 *
 * @author stockbal
 *
 */
public class TreeSelectionEventAdapter {

	private final TreeViewer treeViewer;
	private final Superview superView;
	private final ITreeNodeAction selectionAction;

	public TreeSelectionEventAdapter(final Superview superView, final ITreeNodeAction selectionAction) {
		this.superView = superView;
		this.treeViewer = superView.getTreeViewer();
		this.selectionAction = selectionAction;
		registerMouseEvent();
		registerKeyEvent();
	}

	private void registerKeyEvent() {
		this.treeViewer.getControl().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent e) {
				boolean ctrlPressed = false;
				if ((e.stateMask & SWT.CTRL) != 0) {
					ctrlPressed = e.stateMask == SWT.CTRL;
				}
				final boolean enterKeyPressed = e.keyCode == SWT.CR || e.keyCode == SWT.BREAK;

				if (!enterKeyPressed) {
					return;
				}
				handleSelection(ctrlPressed, null);
			}
		});
	}

	private void registerMouseEvent() {
		this.treeViewer.getControl().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(final MouseEvent e) {
				handleMouseDoubleClick(e);
			}
		});
	}

	private void handleMouseDoubleClick(final MouseEvent e) {
		this.superView.TempLinkedEditorProject = this.superView.getLinkedEditorProject();
		if (Superview.isHideOfDepProject()) {
			this.superView.TempLinkedProject = this.superView.LinkedProject;
		} else {
			this.superView.TempLinkedProject = null;
		}
		boolean ctrlPressed = false;
		if ((e.stateMask & SWT.CTRL) != 0) {
			ctrlPressed = e.stateMask == SWT.CTRL;
		}

		updateSelectionFromClickedNode(new Point(e.x, e.y));

		handleSelection(ctrlPressed, null);

	}

	private void updateSelectionFromClickedNode(final Point point) {
		try {
			final Method meth = ColumnViewer.class.getDeclaredMethod("getViewerRow", Point.class);
			meth.setAccessible(true);
			final Object returnObject = meth.invoke(this.treeViewer, point);
			if (returnObject != null && returnObject instanceof ViewerRow) {
				final ViewerRow viewerRow = (ViewerRow) returnObject;
				final ISelection selection = new StructuredSelection(viewerRow.getElement());
				this.treeViewer.setSelection(selection, false);
			}
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException exc) {
			exc.printStackTrace();
		}
	}

	/**
	 * Handles the actual node selection, i.e. the "Opening" of the selected node(s)
	 *
	 * @param ctrlPressed if <code>true</code> the ctrl key is pressed
	 * @param selection   the current tree selection
	 */
	private void handleSelection(final boolean ctrlPressed, ISelection selection) {
		if (selection == null) {
			selection = this.treeViewer.getSelection();
		}
		this.selectionAction.execute(ctrlPressed, selection);
	}

}

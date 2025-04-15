package com.abapblog.favorites.superview;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.abapblog.favorites.tree.TreeParent;

public class FolderDoubleClick implements IDoubleClickListener {

	@Override
	public void doubleClick(DoubleClickEvent event) {
		try {
			TreeSelection ts = (TreeSelection) event.getSelection();

			if (ts.getFirstElement() instanceof TreeParent) {
				TreeParent node = (TreeParent) ts.getFirstElement();
				if (node != null)
					if (node.getUrlToOpen() != null && !node.getUrlToOpen().isEmpty()) {
						try {
							PlatformUI.getWorkbench().getBrowserSupport().createBrowser(UUID.randomUUID().toString())
									.openURL(new URL(node.getUrlToOpen()));
						} catch (PartInitException | MalformedURLException e) {
							e.printStackTrace();
						}
					} else {
						TreeViewer tree = (TreeViewer) event.getViewer();
						tree.setExpandedState(node, !tree.getExpandedState(node));

					}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

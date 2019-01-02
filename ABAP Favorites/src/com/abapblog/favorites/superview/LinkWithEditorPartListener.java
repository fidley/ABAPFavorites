package com.abapblog.favorites.superview;

import org.eclipse.ui.IEditorPart;

import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPartReference;

public class LinkWithEditorPartListener implements IPartListener2 {
	  private final ILinkedWithEditorView view;

	  public LinkWithEditorPartListener(ILinkedWithEditorView view) {
	    this.view = view;
	  }

	  public void partActivated(IWorkbenchPartReference ref) {
	    if (ref.getPart(true) instanceof IEditorPart) {
	      view.editorActivated(view.getViewSite().getPage().getActiveEditor());
	    }
	  }

	  public void partBroughtToTop(IWorkbenchPartReference ref) {
	    if (ref.getPart(true) == view) {
	      view.editorActivated(view.getViewSite().getPage().getActiveEditor());
	    }
	  }

	  public void partOpened(IWorkbenchPartReference ref) {
	    if (ref.getPart(true) == view) {
	      view.editorActivated(view.getViewSite().getPage().getActiveEditor());
	    }
	  }

	  public void partVisible(IWorkbenchPartReference ref) {
	    if (ref.getPart(true) == view) {
	      IEditorPart editor = view.getViewSite().getPage().getActiveEditor();
	      if(editor!=null) {
	        view.editorActivated(editor);
	      }
	    }
	  }

	  public void partClosed(IWorkbenchPartReference ref) {
		  if (ref instanceof IViewReference) {
			    view.getViewSite().getPage().removePartListener(this);
			      }

	  }
	  public void partDeactivated(IWorkbenchPartReference ref) {}
	  public void partHidden(IWorkbenchPartReference ref) {
	  }
	  public void partInputChanged(IWorkbenchPartReference ref) {}
	}
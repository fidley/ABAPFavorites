package com.abapblog.favorites.common;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewSite;

public interface ILinkedWithEditorView {
	  /**
	   * Called when an editor is activated
	   * e.g. by a click from the user.
	   * @param The activated editor part.
	   */

	  void editorActivated(IEditorPart activeEditor);

	  /**
	   * @return The site for this view.
	   */
	  IViewSite getViewSite();
}

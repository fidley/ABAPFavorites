package com.abapblog.favorites.superview;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;

/**
 * Action that is executed on Tree Node
 *
 * @author stockbal
 *
 */
public interface ITreeNodeAction extends IAction {
	/**
	 * Executes the action
	 *
	 * @param isControlPressed <code>true</code> if the control key is pressed
	 * @param selection        current selection
	 */
	void execute(boolean isControlPressed, ISelection selection);
}

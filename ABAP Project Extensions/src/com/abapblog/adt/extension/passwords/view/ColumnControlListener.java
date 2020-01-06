package com.abapblog.adt.extension.passwords.view;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.TreeColumn;

public class ColumnControlListener implements ControlListener {
	private String id;

	@Override
	public void controlMoved(ControlEvent arg0) {
	}

	@Override
	public void controlResized(ControlEvent arg0) {
	IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(getID());
		TreeColumn column = (TreeColumn) arg0.getSource();
		prefs.putInt("column_width" + column.getText(), column.getWidth());
	}

	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}

}
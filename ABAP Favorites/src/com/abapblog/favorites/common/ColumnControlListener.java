package com.abapblog.favorites.common;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.TreeColumn;

public class ColumnControlListener implements ControlListener {
	private String ID;

	@Override
	public void controlMoved(ControlEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void controlResized(ControlEvent arg0) {
		// TODO Auto-generated method stub
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(getID());
		TreeColumn column = (TreeColumn) arg0.getSource();
		prefs.putInt("column_width" + column.getText(), column.getWidth());
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

}
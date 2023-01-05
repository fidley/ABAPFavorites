package com.abapblog.favorites.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.ResourceManager;

import swing2swt.layout.FlowLayout;

public class Buttons extends ViewPart {
	public Buttons() {
		setTitleImage(ResourceManager.getPluginImage("com.abapblog.favorites", "icons/favorite16.png"));
		setPartName("Favorite buttons");
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		// TODO Auto-generated method stub

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}

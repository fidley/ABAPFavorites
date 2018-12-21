package com.abapblog.favorites.commands;

import org.eclipse.jface.dialogs.TitleAreaDialog;

import org.eclipse.jface.dialogs.IMessageProvider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
public class ChangeUserDialog extends TitleAreaDialog {
	private Text txtUser;
	private String user;


	public ChangeUserDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Change User");
		setMessage("Please provide new User", IMessageProvider.INFORMATION);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		createUser(container);
		return area;
	}

	private void createUser(Composite container) {
		Label lbtUser = new Label(container, SWT.NONE);
		lbtUser.setText("User");

		GridData dataUser = new GridData();
		dataUser.grabExcessHorizontalSpace = true;
		dataUser.horizontalAlignment = GridData.BEGINNING;


		txtUser = new Text(container, SWT.BORDER);
		txtUser.setTextLimit(12);
		txtUser.setLayoutData(dataUser);

	}



	@Override
	protected boolean isResizable() {
		return true;
	}

	// save content of the Text fields because they get disposed
	// as soon as the Dialog closes
	private void saveInput() {
		user = txtUser.getText();
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
		txtUser.setText(user);
	}


}

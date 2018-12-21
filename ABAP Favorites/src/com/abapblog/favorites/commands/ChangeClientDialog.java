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
public class ChangeClientDialog extends TitleAreaDialog {
	private Text txtClient;
	private String client;


	public ChangeClientDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Change Client");
		setMessage("Please provide new client", IMessageProvider.INFORMATION);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		createClient(container);
		return area;
	}

	private void createClient(Composite container) {
		Label lbtClient = new Label(container, SWT.NONE);
		lbtClient.setText("Client");

		GridData dataClient = new GridData();
		dataClient.grabExcessHorizontalSpace = true;
		dataClient.horizontalAlignment = GridData.BEGINNING;


		txtClient = new Text(container, SWT.BORDER);
		txtClient.setTextLimit(3);
		txtClient.setLayoutData(dataClient);

	}



	@Override
	protected boolean isResizable() {
		return true;
	}

	// save content of the Text fields because they get disposed
	// as soon as the Dialog closes
	private void saveInput() {
		client = txtClient.getText();
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
		txtClient.setText(client);
	}


}

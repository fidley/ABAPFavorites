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
public class ChangeLanguageDialog extends TitleAreaDialog {
	private Text txtLanguage;
	private String language;


	public ChangeLanguageDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Change Language");
		setMessage("Please provide new Language", IMessageProvider.INFORMATION);
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
		Label lbtLanguage = new Label(container, SWT.NONE);
		lbtLanguage.setText("Language");

		GridData dataLanguage = new GridData();
		dataLanguage.grabExcessHorizontalSpace = true;
		dataLanguage.horizontalAlignment = GridData.BEGINNING;


		txtLanguage = new Text(container, SWT.BORDER);
		txtLanguage.setTextLimit(2);
		txtLanguage.setLayoutData(dataLanguage);

	}



	@Override
	protected boolean isResizable() {
		return true;
	}

	// save content of the Text fields because they get disposed
	// as soon as the Dialog closes
	private void saveInput() {
		language = txtLanguage.getText();
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
		txtLanguage.setText(language);
	}


}

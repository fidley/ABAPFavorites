package com.abapblog.adt.extension.dialogs;

import java.awt.Checkbox;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ChangePasswordDialog extends Dialog {
	private Text password;
	private Button encrypted;
	private Boolean encryptValue;
	private String passwordValue;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public ChangePasswordDialog(Shell parentShell) {
		super(parentShell);

	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Create/Change Password");
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();

		Composite composite = new Composite(container, SWT.NONE);
		GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_composite.widthHint = 285;
		gd_composite.heightHint = 90;
		composite.setLayoutData(gd_composite);
		composite.setLayout(new GridLayout(3, false));

		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setText("Create/Change Password");
		new Label(composite, SWT.NONE);

		password = new Text(composite, SWT.BORDER);
		GridData gd_password = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_password.widthHint = 100;
		password.setLayoutData(gd_password);
		
		encrypted = new Button(composite,SWT.CHECK);
		encrypted.setText("Encrypt Password?");
		

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(313, 200);
	}

	public String getPassword() {
		return passwordValue;
	}

	@Override
	protected void okPressed() {
		passwordValue = password.getText();
		setEncryptValue(encrypted.getSelection());
		super.okPressed();
	}

	public Boolean getEncryptValue() {
		return encryptValue;
	}

	private void setEncryptValue(Boolean encryptValue) {
		this.encryptValue = encryptValue;
	}
}
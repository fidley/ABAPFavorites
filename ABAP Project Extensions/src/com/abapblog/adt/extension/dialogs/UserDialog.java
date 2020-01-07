package com.abapblog.adt.extension.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class UserDialog extends Dialog {
	private Text user;
	private String userValue;
	private Boolean newUser = false;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public UserDialog(Shell parentShell) {
		super(parentShell);

	}

	public UserDialog(Shell parentShell, Boolean newUser) {
		super(parentShell);
		this.newUser = newUser;

	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		if (newUser == false) {
			shell.setText("Change User for project");
		} else {
			shell.setText("Add new user for client");
		}
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
		gd_composite.heightHint = 69;
		composite.setLayoutData(gd_composite);
		composite.setLayout(new GridLayout(3, false));

		Label lblNewLabel = new Label(composite, SWT.NONE);
		if (newUser == false) {
			lblNewLabel.setText("Change User");
		} else {
			lblNewLabel.setText("User");
		}
		new Label(composite, SWT.NONE);

		user = new Text(composite, SWT.BORDER);
		GridData gd_client = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_client.widthHint = 91;
		user.setLayoutData(gd_client);
		user.setTextLimit(12);

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
		return new Point(313, 175);
	}

	public String getUser() {
		return userValue.toUpperCase();
	}

	public void setUser(String user) {
		this.userValue = user;
		this.user.setText(user);
	}

	@Override
	protected void okPressed() {
		userValue = user.getText();
		super.okPressed();
	}
}

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

public class LanguageDialog extends Dialog {
	private Text language;
	private String languageValue;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public LanguageDialog(Shell parentShell) {
		super(parentShell);

	}

	   protected void configureShell(Shell shell) {
		      super.configureShell(shell);
		      shell.setText("Change Language for project");
		   }

	/**
	 * Create contents of the dialog.
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
				lblNewLabel.setText("Change Language");
				new Label(composite, SWT.NONE);

						language = new Text(composite, SWT.BORDER);
						GridData gd_client = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
						gd_client.widthHint = 24;
						language.setLayoutData(gd_client);
						language.setTextLimit(2);

		return container;
	}

	/**
	 * Create contents of the button bar.
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

	public String getLanguage() {
		return languageValue.toUpperCase();
	}

	public void setLanguage(String language) {
		this.languageValue = language;
		this.language.setText(language);
	}
	@Override
	protected void okPressed() {
		languageValue = language.getText();
		super.okPressed();
	}
}

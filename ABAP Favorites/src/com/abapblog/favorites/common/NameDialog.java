package com.abapblog.favorites.common;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.abapblog.favorites.common.CommonTypes.TypeOfEntry;

public class NameDialog extends TitleAreaDialog {

	private Text txtName;
	private Text txtDescription;

	private String ObjectName;
	private String Name;
	private String Description;
	private TypeOfEntry typeOfObject;

	public NameDialog(Shell parentShell) {
		super(parentShell);
	}

	public NameDialog(Shell shell, TypeOfEntry type) {
		super(shell);
		ObjectName = Common.getObjectName(type);
		typeOfObject = type;
	}

	@Override
	public void create() {
		super.create();
		setTitle("Add new object to list");
		setMessage("Please fill all fields", IMessageProvider.INFORMATION);
	}

	public void create(TypeOfEntry ObjectType, Boolean Edit) {
		super.create();
		typeOfObject = ObjectType;
		ObjectName = Common.getObjectName(ObjectType);
		if (Edit == false) {
			setTitle("Add new " + ObjectName + " to list");
		} else {
			setTitle("Change existing " + ObjectName);
		}
		setMessage("Please fill all fields", IMessageProvider.INFORMATION);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		createName(container);
		createDescription(container);
		return area;
	}

	private void createName(Composite container) {
		Label lbtFirstName = new Label(container, SWT.NONE);
		lbtFirstName.setText(ObjectName + " Name");

		GridData dataFirstName = new GridData();
		dataFirstName.grabExcessHorizontalSpace = true;
		dataFirstName.horizontalAlignment = GridData.FILL;

		txtName = new Text(container, SWT.BORDER);
		txtName.setLayoutData(dataFirstName);
	}

	private void createDescription(Composite container) {
		Label lbtLastName = new Label(container, SWT.NONE);
		lbtLastName.setText("Description");

		GridData dataLastName = new GridData();
		dataLastName.grabExcessHorizontalSpace = true;
		dataLastName.horizontalAlignment = GridData.FILL;
		txtDescription = new Text(container, SWT.BORDER);
		txtDescription.setLayoutData(dataLastName);

		// Button button = new Button(container, SWT.PUSH);
		// button.setLayoutData(new GridData(SWT.RIGHT, SWT.RIGHT, false, false));
		// button.setText("Get Description from SAP");
		// button.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// String SAPDescr =
		// Common.getObjectDescription(Common.getProjectByName(Common.getProjectName()),
		// txtName.getText(), typeOfObject);
		// txtDescription.setText(SAPDescr);
		// }
		// });
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	// save content of the Text fields because they get disposed
	// as soon as the Dialog closes
	private void saveInput() {
		Name = txtName.getText();
		Description = txtDescription.getText();
		// ProjectIndependent = butPrjInd.get

	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	public String getName() {
		return Name;
	}

	public String getDescription() {
		return Description;
	}

	public void setName(String Name) {
		this.Name = Name;
		txtName.setText(Name);
	}

	public void setDescription(String Description) {
		this.Description = Description;
		txtDescription.setText(Description);
	}
}
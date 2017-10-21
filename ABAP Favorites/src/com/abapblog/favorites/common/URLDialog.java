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

public class URLDialog extends TitleAreaDialog {

	private Text txtName;
	private Text txtDescription;
	private Text txtTechnicalName;
	private Text txtLongDescr;

	private String ObjectName;
	private String Name;
	private String Description;
	private String TechnicalName;
	private String LongDescription;
	private TypeOfEntry typeOfObject;

	public URLDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Add new URL to list");
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
		createURL(container);
		createLongDescr(container);

		return area;
	}

	private void createName(Composite container) {
		Label lbtFirstName = new Label(container, SWT.NONE);
		lbtFirstName.setText("Name");

		GridData dataFirstName = new GridData();
		dataFirstName.grabExcessHorizontalSpace = true;
		dataFirstName.horizontalAlignment = GridData.FILL;

		txtName = new Text(container, SWT.BORDER);
		txtName.setLayoutData(dataFirstName);
	}

	private void createURL(Composite container) {
		Label lbtURL = new Label(container, SWT.NONE);
		lbtURL.setText("URL");

		GridData dataTechnicalName = new GridData();
		dataTechnicalName.grabExcessHorizontalSpace = true;
		dataTechnicalName.horizontalAlignment = GridData.FILL;

		txtTechnicalName = new Text(container, SWT.BORDER);
		txtTechnicalName.setLayoutData(dataTechnicalName);
	}

	private void createLongDescr(Composite container) {
		Label lbtLongDescr = new Label(container, SWT.TOP);
		lbtLongDescr.setText("Long Description");

		GridData dataLongDescr = new GridData(GridData.FILL_BOTH);
		dataLongDescr.grabExcessHorizontalSpace = true;
		dataLongDescr.horizontalAlignment = GridData.FILL;
		dataLongDescr.verticalAlignment = GridData.FILL;
		dataLongDescr.minimumHeight = 100;

		txtLongDescr = new Text(container, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.RESIZE);
		txtLongDescr.setLayoutData(dataLongDescr);
	}

	private void createDescription(Composite container) {
		Label lbtLastName = new Label(container, SWT.NONE);
		lbtLastName.setText("Description");

		GridData dataLastName = new GridData();
		dataLastName.grabExcessHorizontalSpace = true;
		dataLastName.horizontalAlignment = GridData.FILL;
		txtDescription = new Text(container, SWT.BORDER);
		txtDescription.setLayoutData(dataLastName);
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
		TechnicalName = txtTechnicalName.getText();
		setLongDescription(txtLongDescr.getText());

	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	public String getName() {
		return Name;
	}

	public String getURL() {
		return TechnicalName;
	}

	public String getDescription() {
		return Description;
	}

	public void setName(String Name) {
		this.Name = Name;
		txtName.setText(Name);
	}

	public void setURL(String URL) {
		this.TechnicalName = URL;
		txtTechnicalName.setText(URL);
	}

	public void SetDescription(String Description) {
		this.Description = Description;
		txtDescription.setText(Description);
	}

	public String getLongDescription() {
		return LongDescription;
	}

	public void setLongDescription(String longDescription) {
		LongDescription = longDescription;
		txtLongDescr.setText(LongDescription);
	}

}
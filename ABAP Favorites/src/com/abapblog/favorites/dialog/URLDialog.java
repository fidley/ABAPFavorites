package com.abapblog.favorites.dialog;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.abapblog.favorites.commands.DynamicCommands;
import com.abapblog.favorites.common.Common;
import com.abapblog.favorites.common.CommonTypes.TypeOfEntry;

public class URLDialog extends TitleAreaDialog {

	private Text txtName;
	private Text txtDescription;
	private Text txtTechnicalName;
	private Text txtLongDescr;
	private Combo commandSelection;
	private String ObjectName;
	private String Name;
	private String Description;
	private String TechnicalName;
	private String LongDescription;
	private String commandID;
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
		createCommandsLists(container);
		return area;
	}

	private void createCommandsLists(Composite container) {
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setText("Command");

		commandSelection = new Combo(container, SWT.NONE);
		commandSelection.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		commandSelection.add("", 0);
		commandSelection.add(DynamicCommands.Favorite0.toString(), DynamicCommands.Favorite0.getComboIndex());
		commandSelection.add(DynamicCommands.Favorite1.toString(), DynamicCommands.Favorite1.getComboIndex());
		commandSelection.add(DynamicCommands.Favorite2.toString(), DynamicCommands.Favorite2.getComboIndex());
		commandSelection.add(DynamicCommands.Favorite3.toString(), DynamicCommands.Favorite3.getComboIndex());
		commandSelection.add(DynamicCommands.Favorite4.toString(), DynamicCommands.Favorite4.getComboIndex());
		commandSelection.add(DynamicCommands.Favorite5.toString(), DynamicCommands.Favorite5.getComboIndex());
		commandSelection.add(DynamicCommands.Favorite6.toString(), DynamicCommands.Favorite6.getComboIndex());
		commandSelection.add(DynamicCommands.Favorite7.toString(), DynamicCommands.Favorite7.getComboIndex());
		commandSelection.add(DynamicCommands.Favorite8.toString(), DynamicCommands.Favorite8.getComboIndex());
		commandSelection.add(DynamicCommands.Favorite9.toString(), DynamicCommands.Favorite9.getComboIndex());

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
		int selectionIndex = commandSelection.getSelectionIndex();
		if (selectionIndex >= 0)
			commandID = DynamicCommands.getByIndex(selectionIndex).getCommandID();

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

	public String getCommandID() {
		return commandID;

	}

	public void setCommandID(String commandID) {
		this.commandID = commandID;
		try {
			commandSelection.select(DynamicCommands.getByCommandID(commandID).getComboIndex());
		} catch (Exception e) {

		}

	}

}
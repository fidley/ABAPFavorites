package com.abapblog.favorites.dialog;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wb.swt.ResourceManager;

import com.abapblog.favorites.commands.DynamicCommands;
import com.abapblog.favorites.common.Common;
import com.abapblog.favorites.common.CommonTypes.TypeOfEntry;
import com.abapblog.favorites.superview.AdtObjectHandler;
import com.sap.adt.tools.core.ui.dialogs.AbapProjectSelectionDialog;

public class NameDialog extends TitleAreaDialog {

	private Text txtName;
	private Text txtDescription;
	private Text txtLongDescr;
	private Combo commandSelection;
	private String ObjectName;
	private String Name = "";
	private String Description = "";
	private String LongDescription;
	private String commandID;
	private TypeOfEntry typeOfObject;
	private Composite composite;

	/**
	 * @wbp.parser.constructor
	 */
	public NameDialog(Shell parentShell) {
		super(parentShell);
	}

	public NameDialog(Shell shell, TypeOfEntry type) {
		super(shell);
		ObjectName = Common.getObjectName(type);
		typeOfObject = type;
	}

	public NameDialog(Shell shell, TypeOfEntry type, String name) {
		super(shell);
		ObjectName = Common.getObjectName(type);
		typeOfObject = type;
		this.Name = name;
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
		composite = new Composite(area, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout gl_container_1 = new GridLayout(3, false);
		composite.setLayout(gl_container_1);

		createName(composite);
		createDescription(composite);
		createLongDescr(composite);
		createCommandsLists(composite);
		createEmptyLine(composite);
//		new Label(composite, SWT.NONE);
//		new Label(composite, SWT.NONE);
		return area;
	}

	private void createEmptyLine(Composite container) {
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

	}

	private void createCommandsLists(Composite container) {
		new Label(composite, SWT.NONE);
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
		lbtFirstName.setText(ObjectName + " Name");

		GridData dataFirstName = new GridData();
		dataFirstName.grabExcessHorizontalSpace = true;
		dataFirstName.horizontalAlignment = GridData.FILL;

		txtName = new Text(container, SWT.BORDER);
		txtName.setLayoutData(dataFirstName);
		txtName.setText(Name);

	}

	private void createLongDescr(Composite container) {

		Button btnReadDescrFromSystem = new Button(composite, SWT.NONE);
		btnReadDescrFromSystem
				.setImage(ResourceManager.getPluginImage("com.abapblog.favorites", "icons/importdir_wiz.png"));
		btnReadDescrFromSystem.setText("Import");
		btnReadDescrFromSystem.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(org.eclipse.swt.events.MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDown(org.eclipse.swt.events.MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseUp(org.eclipse.swt.events.MouseEvent e) {
				try {
					IProject project = AbapProjectSelectionDialog
							.open(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), null);
					setDescription(AdtObjectHandler.lookupObjectReference(project, txtName.getText(), typeOfObject)
							.getDescription());
				} catch (Exception e1) {
					e1.printStackTrace();
				}

			}
		});
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
		new Label(composite, SWT.NONE);
		Label lbtLastName = new Label(container, SWT.NONE);
		lbtLastName.setText("Description");

		GridData dataLastName = new GridData();
		dataLastName.grabExcessHorizontalSpace = true;
		dataLastName.horizontalAlignment = GridData.FILL;
		txtDescription = new Text(container, SWT.BORDER);
		txtDescription.setLayoutData(dataLastName);
		txtDescription.setText(Description);

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
		LongDescription = txtLongDescr.getText();
		int selectionIndex = commandSelection.getSelectionIndex();
		if (selectionIndex >= 1) {
			commandID = DynamicCommands.getByIndex(selectionIndex).getCommandID();
		} else {
			commandID = "";
		}

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
		try {
			txtDescription.setText(Description);
		} catch (Exception e) {
			// ignore
		}
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
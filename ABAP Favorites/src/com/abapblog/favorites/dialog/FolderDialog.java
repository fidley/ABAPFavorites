package com.abapblog.favorites.dialog;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class FolderDialog extends TitleAreaDialog {

	private Text txtName;
	private Text txtDescription;
	private Button butDevObj;
	private String Name;
	private String Description;
	private String LongDescription;
	private Text txtLongDescr;
	private Text txtUrlToOpen;
	private String UrlToOpen;
	private Button btnProjectDependent;
	private Button btnWorkingSetDependent;
	private Button btnIndependent;
	private boolean Independent = false;
	private boolean WorkingSetDependent = false;
	private boolean ProjectDependent = true;
	private boolean DevObjectFolder = false;

	/**
	 * @wbp.parser.constructor
	 */
	public FolderDialog(Shell parentShell) {
		super(parentShell);
	}

	public FolderDialog(Shell parentShell, Boolean DevObjectFolder) {
		super(parentShell);
		setDevObjectFolder(DevObjectFolder);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Add new folder to list");
		setMessage("Please fill all fields", IMessageProvider.INFORMATION);
	}

	public void create(Boolean Edit) {
		super.create();
		if (Edit == false) {
			setTitle("Add new folder to list");
		} else {
			setTitle("Change existing folder");
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
		createProjectIndependent(container);
		createURLToOpen(container);
		if (!getDevObjectFolder()) {
			createDevObjFolder(container);
		}
		createLongDescr(container);
		createEmptyLine(container);

		return area;
	}

	private void createURLToOpen(Composite container) {
		Label lbtUrl = new Label(container, SWT.NONE);
		lbtUrl.setText("URL to open on double click");

		GridData dataURL = new GridData();
		dataURL.grabExcessHorizontalSpace = true;
		dataURL.horizontalAlignment = GridData.FILL;

		txtUrlToOpen = new Text(container, SWT.BORDER);
		txtUrlToOpen.setLayoutData(dataURL);

	}

	private void createName(Composite container) {
		Label lbtFirstName = new Label(container, SWT.NONE);
		lbtFirstName.setText("Folder Name");

		GridData dataFirstName = new GridData();
		dataFirstName.grabExcessHorizontalSpace = true;
		dataFirstName.horizontalAlignment = GridData.FILL;

		txtName = new Text(container, SWT.BORDER);
		txtName.setLayoutData(dataFirstName);
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

	private void createProjectIndependent(Composite container) {
		Label lbtPrjInd = new Label(container, SWT.NONE);
		lbtPrjInd.setText("Folder Dependency");
//
//		GridData dataFirstName = new GridData();
//		dataFirstName.grabExcessHorizontalSpace = true;
//		dataFirstName.horizontalAlignment = GridData.FILL;
		// Create a group to hold the radio buttons
		Group radioGroup = new Group(container, SWT.NONE);
		// radioGroup.setText("Folder Dependency");
		radioGroup.setLayout(new GridLayout(1, false));
		radioGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		// Create radio buttons
		btnProjectDependent = new Button(radioGroup, SWT.RADIO);
		btnProjectDependent.setText("Project Dependent");
		btnProjectDependent.setSelection(true); // Set default selection

		btnWorkingSetDependent = new Button(radioGroup, SWT.RADIO);
		btnWorkingSetDependent.setText("Working Set Dependent");

		btnIndependent = new Button(radioGroup, SWT.RADIO);
		btnIndependent.setText("Independent");

		// Add selection listener to handle selection events
		SelectionAdapter selectionListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button selectedButton = (Button) e.getSource();
				if (selectedButton == btnProjectDependent) {
					Independent = false;
					setWorkingSetDependent(false);
					setProjectDependent(true);
				} else if (selectedButton == btnWorkingSetDependent) {
					Independent = false;
					setWorkingSetDependent(true);
					setProjectDependent(false);
				} else if (selectedButton == btnIndependent) {
					Independent = true;
					setWorkingSetDependent(false);
					setProjectDependent(false);
				}
			}
		};

		btnProjectDependent.addSelectionListener(selectionListener);
		btnWorkingSetDependent.addSelectionListener(selectionListener);
		btnIndependent.addSelectionListener(selectionListener);

	}

	private void createDevObjFolder(Composite container) {
		Label lbtDevObj = new Label(container, SWT.NONE);
		lbtDevObj.setText("Development Objects Folder?");

		GridData dataFirstName = new GridData();
		dataFirstName.grabExcessHorizontalSpace = true;
		dataFirstName.horizontalAlignment = GridData.FILL;

		butDevObj = new Button(container, SWT.CHECK);
		butDevObj.setLayoutData(dataFirstName);
		butDevObj.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				Button btn = (Button) event.getSource();
				DevObjectFolder = btn.getSelection();
			}
		});
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

	private void createEmptyLine(Composite container) {
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

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
		UrlToOpen = txtUrlToOpen.getText();
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	public String getName() {
		return Name;
	}

	public Boolean getIndependent() {
		return Independent;
	}

	public String getDescription() {
		return Description;
	}

	public void setName(String Name) {
		this.Name = Name;
		txtName.setText(Name);
	}

	public void setIndependent(Boolean PrjInd) {
		this.Independent = PrjInd;
		btnIndependent.setSelection(Independent);
	}

	public void setDescription(String Description) {
		this.Description = Description;
		txtDescription.setText(Description);
	}

	public boolean getDevObjectFolder() {
		return DevObjectFolder;
	}

	public void setDevObjectFolder(boolean devObjectProject) {
		DevObjectFolder = devObjectProject;
		if (butDevObj != null)
			butDevObj.setSelection(DevObjectFolder);
	}

	public String getLongDescription() {
		return LongDescription;
	}

	public void setLongDescription(String longDescription) {
		LongDescription = longDescription;
		txtLongDescr.setText(LongDescription);
	}

	public String getURLToOpen() {
		return UrlToOpen;
	}

	public void setURLToOpen(String urlToOpen) {
		UrlToOpen = urlToOpen;
		txtUrlToOpen.setText(UrlToOpen);
	}

	public boolean getWorkingSetDependent() {
		return WorkingSetDependent;
	}

	public void setWorkingSetDependent(boolean workspaceDependent) {
		WorkingSetDependent = workspaceDependent;
		btnWorkingSetDependent.setSelection(WorkingSetDependent);
	}

	public boolean getProjectDependent() {
		return ProjectDependent;
	}

	public void setProjectDependent(boolean projectDependent) {
		ProjectDependent = projectDependent;
		btnProjectDependent.setSelection(ProjectDependent);
	}

}
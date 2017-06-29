package com.abapblog.favorites.views;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;

public class FolderDialog  extends TitleAreaDialog {

    private Text txtName;
    private Text txtDescription;
    private Button butPrjInd;

    private String Name;
    private String Description;
    private boolean ProjectIndependent;


    public FolderDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    public void create() {
        super.create();
        setTitle("Add new folder to list");
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
        return area;
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

    private void createProjectIndependent(Composite container) {
        Label lbtPrjInd = new Label(container, SWT.NONE);
        lbtPrjInd.setText("Project Independent?");

        GridData dataFirstName = new GridData();
        dataFirstName.grabExcessHorizontalSpace = true;
        dataFirstName.horizontalAlignment = GridData.FILL;

        butPrjInd = new Button(container, SWT.CHECK);
        butPrjInd.setLayoutData(dataFirstName);
        butPrjInd.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                Button btn = (Button) event.getSource();
                ProjectIndependent = btn.getSelection();
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



    @Override
    protected boolean isResizable() {
        return true;
    }

    // save content of the Text fields because they get disposed
    // as soon as the Dialog closes
    private void saveInput() {
        Name = txtName.getText();
        Description = txtDescription.getText();
        //ProjectIndependent = butPrjInd.get

    }

    @Override
    protected void okPressed() {
        saveInput();
        super.okPressed();
    }

    public String getName() {
        return Name;
    }
    public Boolean getPrjInd() {
        return ProjectIndependent;
    }


    public String getDescription() {
        return Description;
    }
}
package com.abablog.adt.extension.passwords;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class LogonWithJob {

	public void logon(IProject project) {
		Job job = new Job("Logon to Project: " + project.getName()) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				ILogonService logonService = LogonServiceFactory.create();
				if (logonService.checkCanLogonWithSecureStorage(project)) {
					try {
						logonService.LogonToProject(project);
					} catch (NullPointerException e) {
						System.out.print("NullPointerException caught");
					}

					return Status.OK_STATUS;
				} else {
					return Status.CANCEL_STATUS;
				}
			}

		};
		job.setUser(true);
		job.schedule();
	}

	public void logon(IProject project, String user, String client) {
		Job job = new Job("Logon to Project: " + project.getName()) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				ILogonService logonService = LogonServiceFactory.create();
				if (logonService.checkCanLogonWithSecureStorage(project, user, client)) {
					try {
					logonService.LogonToProject(project, user, client);
					} catch (NullPointerException e) {
						System.out.print("NullPointerException caught");
					}
					return Status.OK_STATUS;
				} else {
					return Status.CANCEL_STATUS;
				}
			}

		};
		job.setUser(true);
		job.schedule();
	}
}

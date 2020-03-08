package com.abapblog.adt.extension.passwords.projectExplorer;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

public class ProjectListener implements IResourceChangeListener {

	public void resourceChanged(IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
			List<IProject> projects = getProjects(event.getDelta());
			// do something with new projects
		}
	}

	private List<IProject> getProjects(IResourceDelta delta) {
		final List<IProject> projects = new ArrayList<IProject>();
		try {
			delta.accept(new IResourceDeltaVisitor() {
				public boolean visit(IResourceDelta delta) throws CoreException {
					if (delta.getKind() == IResourceDelta.ADDED && delta.getResource().getType() == IResource.PROJECT) {
						IProject project = (IProject) delta.getResource();
						if (project.isAccessible()) {
							projects.add(project);
						}
					}
					// only continue for the workspace root
					return delta.getResource().getType() == IResource.ROOT;
				}
			});
		} catch (CoreException e) {
			// handle error
		}
		return projects;
	}
}
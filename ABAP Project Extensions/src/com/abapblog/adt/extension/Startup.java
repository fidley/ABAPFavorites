package com.abapblog.adt.extension;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IStartup;

import com.abapblog.adt.extension.commands.LogToAllSAPSystemsHandler;
import com.abapblog.adt.extension.preferences.PreferenceConstants;

public class Startup implements IStartup {

	@Override
	public void earlyStartup() {
		
		LogToAllSAPSystemsHandler logToAllSystems = new LogToAllSAPSystemsHandler();
		try {
			if (Activator.getDefault().getPreferenceStore()
						.getBoolean(PreferenceConstants.doAutomaticLogonAtStart))
			logToAllSystems.execute(new ExecutionEvent());
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

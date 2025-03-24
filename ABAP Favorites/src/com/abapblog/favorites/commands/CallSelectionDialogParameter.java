package com.abapblog.favorites.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.IParameterValues;

public class CallSelectionDialogParameter implements IParameterValues {

	@Override
	public Map getParameterValues() {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("true", "true");
		parameters.put("false", " false");
		return parameters;
	}

}

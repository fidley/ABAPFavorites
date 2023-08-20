package com.abapblog.favorites.commands;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class OpenGithub implements IHandler {

	private static final String GITHUB_PROJECT_WEB_STIE = "https://github.com/fidley/ABAPFavorites";

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
// Not needed at the moment
	}

	@Override
	public void dispose() {
// Not needed at the moment
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser()
					.openURL(new URI(GITHUB_PROJECT_WEB_STIE).toURL());
		} catch (PartInitException | MalformedURLException | URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isHandled() {
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
// Not needed at the moment
	}

}

package com.abapblog.favorites.commands;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;
import com.abapblog.favorites.common.*;

public class LogToAllSAPSystemsHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {

        Common.logonToAllSAPs();
        return null;
    }

}
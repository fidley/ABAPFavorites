
package com.abapblog.favorites.controls;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.bindings.Binding;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.themes.IThemeManager;

import com.abapblog.favorites.superview.labelproviders.NameCellLabelProvider;
import com.abapblog.favorites.tree.TreeObject;

public class FavoritesButton extends Composite {
	private static Color defaultHighlightColor = getDefaultHighlightColor();

	private static Color getDefaultHighlightColor() {
		IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
		ITheme currentTheme = themeManager.getCurrentTheme();
		ColorRegistry colorRegistry = currentTheme.getColorRegistry();
//		for (String key : colorRegistry.getKeySet()) {
//			System.out.println(key + " " + colorRegistry.get(key));
//		}
		return colorRegistry.get("org.eclipse.gef.color.list.selected.background");
	}

	private final class MouseAdapterExtension extends MouseAdapter {
		@Override
		public void mouseDown(MouseEvent e) {
			boolean callSelectionDialog = false;
			if (e.button == 1) {
				if ((e.stateMask & SWT.CTRL) != 0) {
					callSelectionDialog = true;
				}
				handleClick(callSelectionDialog);
			}
		}
	}

	private final class MouseTrackAdapterExtension extends MouseTrackAdapter {
		private Color originalBackground;

		@Override
		public void mouseEnter(MouseEvent e) {
			originalBackground = getBackground();
			setBackground(defaultHighlightColor);
			redraw();
		}

		@Override
		public void mouseExit(MouseEvent e) {
			setBackground(originalBackground);
			redraw();
		}

	}

	private final int BORDER_MARGIN = 8;
	private ResourceManager resourceManager;
	private StyledString keyBindingStyledString;
	private StyledString projectStyledString;
	private String favoriteName = "";
	private String keyBinding = "";
	private String keyBindingForDisplay = "";
	private Font arialFont;
	private Image buttonImage;
	private TreeObject linkedTreeObject;

	public FavoritesButton(Composite parent, TreeObject linkedTreeObject) {
		super(parent, SWT.NONE);
		this.linkedTreeObject = linkedTreeObject;
		fillButtonControls();
		addListeners();
	}

	private void fillButtonControls() {
		resourceManager = new LocalResourceManager(JFaceResources.getResources(), this);
		arialFont = resourceManager.createFont(FontDescriptor.createFrom("Arial", 8, SWT.NONE));
		buttonImage = NameCellLabelProvider.getImage(linkedTreeObject);
		setFavoriteName(linkedTreeObject.getName());
		setKeyBinding(getKeyBindingForCommand(linkedTreeObject.getCommandID()));
		setToolTipText(linkedTreeObject.getDescription());
		updateStyledStrings();
	}

	private void addListeners() {
		addPaintListener(paintEvent -> {
			GC gc = paintEvent.gc;
			paintStyledText(gc);
		});

		addMouseListener(new MouseAdapterExtension());
		addMouseTrackListener(new MouseTrackAdapterExtension());
	}

	private static String getKeyBindingForCommand(String commandId) {
		IBindingService bindingService = PlatformUI.getWorkbench().getService(IBindingService.class);
		if (bindingService != null) {
			String keyBinding = bindingService.getBestActiveBindingFormattedFor(commandId);
			if (keyBinding != null) {
				return keyBinding;
			}

			Binding[] bindings = bindingService.getBindings();
			for (int i = 0; i < (bindings.length + 1); i++) {
				Binding binding = bindings[i];
				try {
					if (binding.getParameterizedCommand().getCommand().getId().equals(commandId)) {
						return binding.getTriggerSequence().format();
					}
				} catch (Exception e) {

				}
			}

		}
		return " ";
	}

	private void updateStyledStrings() {
		keyBindingStyledString = new StyledString();
		if (keyBinding.length() > 0) {
			keyBindingStyledString.append(keyBindingForDisplay, StyledString.DECORATIONS_STYLER);
		}

		projectStyledString = new StyledString();
		if (linkedTreeObject.getParent().getProject() != null
				&& linkedTreeObject.getParent().getProjectIndependent() == false) {
			projectStyledString.append(" [" + linkedTreeObject.getParent().getProject() + "]",
					StyledString.COUNTER_STYLER);
		}

	}

	public String getFavoriteName() {
		checkWidget();
		return favoriteName;
	}

	public void setFavoriteName(String favoriteName) {
		checkWidget();
		this.favoriteName = favoriteName;
		updateStyledStrings();
		getParent().layout();
		redraw();
	}

	public String getKeyBinding() {
		checkWidget();
		return keyBinding;
	}

	public void setKeyBinding(String keyBinding) {
		checkWidget();
		this.keyBinding = keyBinding;
		keyBindingForDisplay = "(" + keyBinding + ")";
		updateStyledStrings();
		getParent().layout();
		redraw();
	}

	protected void paintStyledText(GC gc) {

		gc.setFont(arialFont);

		// Draw background and border
		final Point textExtent = gc.textExtent(
				favoriteName + " " + projectStyledString.getString() + "\n" + keyBindingStyledString.getString());

		// Draw the image
		int imageHeight = 0;
		int imageWidth = 0;
		if (buttonImage != null) {
			imageHeight = buttonImage.getBounds().height;
			imageWidth = buttonImage.getBounds().width + 2;
		}

		gc.fillRoundRectangle(3, 3, textExtent.x + BORDER_MARGIN + imageWidth + 2, textExtent.y + BORDER_MARGIN, 8, 8);
		gc.drawRoundRectangle(3, 3, textExtent.x + BORDER_MARGIN + imageWidth + 2, textExtent.y + BORDER_MARGIN, 8, 8);
		if (buttonImage != null) {
			gc.drawImage(buttonImage, BORDER_MARGIN, BORDER_MARGIN);
		}
		// Draw favoriteName on the first line
		int areaWidth = getClientArea().width;
		int favoriteNameWidth = gc.textExtent(favoriteName).x;
		int xFavoriteName = (areaWidth - favoriteNameWidth) / 2 + imageWidth / 2;
		gc.drawText(favoriteName, imageWidth + 2 + BORDER_MARGIN, BORDER_MARGIN, true);
		drawStyledString(gc, projectStyledString, favoriteNameWidth + imageWidth + 3 + BORDER_MARGIN, BORDER_MARGIN);
		int keyBindingWidth = gc.textExtent(keyBindingStyledString.getString()).x;
		int x = (areaWidth - keyBindingWidth) / 2;
		int y = BORDER_MARGIN + gc.getFontMetrics().getHeight();
		drawStyledString(gc, keyBindingStyledString, x, y);
	}

	private void drawStyledString(GC gc, StyledString styledString, int x, int y) {
		for (StyleRange range : styledString.getStyleRanges()) {
			if (range.fontStyle == SWT.BOLD) {
				gc.setFont(arialFont);
			}
			if (range.foreground != null) {
				gc.setForeground(range.foreground);
			}
			String text = styledString.getString().substring(range.start, range.start + range.length);
			Point extent = gc.textExtent(text);
			gc.drawText(text, x, y, true);
			x += extent.x;
		}
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {

		final Point size = super.computeSize(wHint, hHint, changed);
		final GC gc = new GC(this);
		gc.setFont(arialFont);

		final String multiLineText = favoriteName + " " + projectStyledString.getString() + "\n"
				+ keyBindingStyledString.getString();
		final Point multiLineTextSize = gc.textExtent(multiLineText, SWT.DRAW_DELIMITER);

		final String flatText = multiLineText.replace('\n', ' ');
		final Point flatTextSize = gc.textExtent(flatText);
		int imageHeight = 0;
		int imageWidth = 0;
		if (buttonImage != null) {
			imageHeight = buttonImage.getBounds().height;
			imageWidth = buttonImage.getBounds().width + 2;
		}

		gc.dispose();
		if (multiLineTextSize.x < flatTextSize.x) {
			size.x = multiLineTextSize.x + 2 * BORDER_MARGIN + imageWidth;
			size.y = multiLineTextSize.y + 2 * BORDER_MARGIN;
		} else {
			size.x = flatTextSize.x + 2 * BORDER_MARGIN + imageWidth;
			size.y = flatTextSize.y + 2 * BORDER_MARGIN;
		}

		return size;
	}

	private void handleClick(boolean callSelectionDialog) {
		String commandId = linkedTreeObject.getCommandID();

		if (commandId != null) {
			IHandlerService handlerService = PlatformUI.getWorkbench().getService(IHandlerService.class);
			ICommandService commandService = PlatformUI.getWorkbench().getService(ICommandService.class);

			try {
				Command command = commandService.getCommand(commandId);
				Map<String, String> parameters = new HashMap<>();
				parameters.put("com.abapblog.favorites.commands.parameter.callSelectionDialog",
						Boolean.toString(callSelectionDialog));

				ParameterizedCommand parameterizedCommand = ParameterizedCommand.generateCommand(command, parameters);
				handlerService.executeCommand(parameterizedCommand, null);
			} catch (ExecutionException | NotDefinedException | NotEnabledException | NotHandledException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void dispose() {
		super.dispose();
	}
}

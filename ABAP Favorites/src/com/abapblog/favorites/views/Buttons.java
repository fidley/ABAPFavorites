package com.abapblog.favorites.views;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.abapblog.favorites.commands.DynamicCommandHandler;
import com.abapblog.favorites.common.CommonTypes.TypeOfXMLNode;
import com.abapblog.favorites.controls.FavoritesButton;
import com.abapblog.favorites.superview.IFavorites;
import com.abapblog.favorites.superview.Superview;
import com.abapblog.favorites.tree.TreeObject;
import com.abapblog.favorites.tree.TreeParent;

public class Buttons extends ViewPart implements IFavorites {
	private static List<FavoritesButton> dynamicButtons = new ArrayList<>();
	private static Composite parent;
	private static ScrolledComposite scrolledComposite;

	public Buttons() {
		setPartName("Favorite buttons");
	}

	@Override
	public void createPartControl(Composite parent) {
		Superview.createTreeNodes(TypeOfXMLNode.folderNode, this, false);
		scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setMinWidth(50);
		scrolledComposite.setMinHeight(50);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		refreshButtons();
		scrolledComposite.setMinSize(getMinSize());

		// Add a ControlListener to handle resizing
		scrolledComposite.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				scrolledComposite.setMinSize(getMinSize());
			}
		});

	}

	private static void createButtons() {
		TreeMap<String, TreeObject> sortedMap = new TreeMap<>(new Comparator<String>() {
			@Override
			public int compare(String key1, String key2) {
				return key1.compareTo(key2);
			}
		});

		// Add all entries to the TreeMap
		sortedMap.putAll(DynamicCommandHandler.commandsLink);

		// Create buttons for each entry in the sorted map
		for (Map.Entry<String, TreeObject> entry : sortedMap.entrySet()) {
			FavoritesButton button = new FavoritesButton(parent, entry.getValue());
			dynamicButtons.add(button);
		}
	}

	public static void refreshButtons() {
		if (parent != null) {
			for (FavoritesButton button : dynamicButtons) {
				button.dispose();
			}
			parent.dispose();
		}
		parent = new Composite(scrolledComposite, SWT.NONE);
		RowLayout layout = new RowLayout();
		layout.wrap = true;
		layout.pack = true;
		Buttons.parent.setLayout(layout);
		scrolledComposite.setContent(Buttons.parent);

		dynamicButtons.clear();
		createButtons();
		scrolledComposite.setMinSize(getMinSize());
		scrolledComposite.layout(true, true);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	private static Point getMinSize() {
		int maxWidth = 0;
		int maxHeight = 0;
		int parentWidth = parent.getParent().getClientArea().width;
		int currentRowWidth = 0;
		int rowCount = 1;

		for (FavoritesButton button : dynamicButtons) {
			Point size = button.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			maxWidth = Math.max(maxWidth, size.x);
			maxHeight = Math.max(maxHeight, size.y);

			if (currentRowWidth + size.x > parentWidth) {
				rowCount++;
				currentRowWidth = size.x;
			} else {
				currentRowWidth += size.x;
			}
		}

		return new Point(maxWidth, rowCount * maxHeight + (rowCount - 1) * getRowMargin());
	}

	private static int getRowMargin() {
		if (parent.getLayout() instanceof RowLayout) {
			RowLayout layout = (RowLayout) parent.getLayout();
			return layout.marginTop + layout.marginBottom;
		}
		return 3; // Default margin if not using RowLayout
	}

	@Override
	public String getID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFavorites getFavorites() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLinkedEditorProject() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public boolean isLinkingActive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getObjectSortingType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeOfXMLNode getFolderType() {
		// TODO Auto-generated method stub
		return TypeOfXMLNode.folderNode;
	}

	@Override
	public void putFavToCommon() {
		// TODO Auto-generated method stub

	}

	@Override
	public void disableLinkingOfEditor() {
		// TODO Auto-generated method stub

	}

	@Override
	public void enableLinkingOfEditor() {
		// TODO Auto-generated method stub

	}

	@Override
	public TreeViewer getTreeViewer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<String> getExpandedNodes() {
		// TODO Auto-generated method stub
		return new ArrayList<>();
	}

	@Override
	public ArrayList<TreeParent> getExpandedParentNodes() {
		// TODO Auto-generated method stub
		return new ArrayList<>();
	}

	@Override
	public void showHideLinkedToColumn() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showHideLongDescriptionColumn() {
		// TODO Auto-generated method stub

	}
}

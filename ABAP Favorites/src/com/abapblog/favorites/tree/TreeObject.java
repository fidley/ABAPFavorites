package com.abapblog.favorites.tree;

import org.eclipse.core.runtime.IAdaptable;


import com.abapblog.favorites.common.CommonTypes.TypeOfEntry;
import com.abapblog.favorites.superview.IFavorites;

public class TreeObject implements IAdaptable {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getOuterType().hashCode();
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((technicalName == null) ? 0 : technicalName.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TreeObject other = (TreeObject) obj;
		if (!getOuterType().equals(other.getOuterType()))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		}
		else if (!description.equals(other.description))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		if (technicalName == null) {
			if (other.technicalName != null)
				return false;
		}
		else if (!technicalName.equals(other.technicalName))
			return false;
		if (type != other.type)
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		}
		else if (!parent.equals(other.parent))
			return false;
		return true;
	}

	private TreeParent parent;
	private IFavorites favorite;
	private String name;
	private TypeOfEntry type;
	private String technicalName;
	private String description;
	private String longDescription;

	public TreeObject(String name, TypeOfEntry type, String description, String technicalName, String longDescription,
			IFavorites favorite) {
		this.favorite = favorite;
		this.name = name;
		this.setType(type);
		this.setDescription(description);
		this.setTechnicalName(technicalName);
		this.setLongDescription(longDescription);

	}

	public String getName() {
		return name;
	}

	public TypeOfEntry getType() {
		return type;
	}

	public void setType(TypeOfEntry type) {
		this.type = type;
	}

	public void setParent(TreeParent parent) {
		this.parent = parent;
	}

	public TreeParent getParent() {
		return parent;
	}

	public String toString() {
		return getName();
	}

	public <T> T getAdapter(Class<T> key) {
		return null;
	}

	public String getTechnicalName() {
		return technicalName;
	}

	public void setTechnicalName(String technicalName) {
		this.technicalName = technicalName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	private Object getOuterType() {
		return favorite;
	}

	public String getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}
}

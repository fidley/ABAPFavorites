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
		result = prime * result + ((Description == null) ? 0 : Description.hashCode());
		result = prime * result + ((Name == null) ? 0 : Name.hashCode());
		result = prime * result + ((TechnicalName == null) ? 0 : TechnicalName.hashCode());
		result = prime * result + ((Type == null) ? 0 : Type.hashCode());
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
		if (Description == null) {
			if (other.Description != null)
				return false;
		} else if (!Description.equals(other.Description))
			return false;
		if (Name == null) {
			if (other.Name != null)
				return false;
		} else if (!Name.equals(other.Name))
			return false;
		if (TechnicalName == null) {
			if (other.TechnicalName != null)
				return false;
		} else if (!TechnicalName.equals(other.TechnicalName))
			return false;
		if (Type != other.Type)
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		return true;
	}

	public TreeParent parent;
	private IFavorites favorite;
	public String Name;
	public TypeOfEntry Type;
	private String TechnicalName;
	private String Description;
	private String LongDescription;

	public TreeObject(String Name, TypeOfEntry Type, String Description, String TechnicalName, String LongDescription,
			IFavorites Favorite) {
		this.favorite = Favorite;
		this.Name = Name;
		this.setType(Type);
		this.setDescription(Description);
		this.setTechnicalName(TechnicalName);
		this.setLongDescription(LongDescription);

	}

	public String getName() {
		return Name;
	}

	public TypeOfEntry getType() {
		return Type;
	}

	public void setType(TypeOfEntry type) {
		Type = type;
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
		return TechnicalName;
	}

	public void setTechnicalName(String technicalName) {
		TechnicalName = technicalName;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	private Object getOuterType() {
		return favorite;
	}

	public String getLongDescription() {
		return LongDescription;
	}

	public void setLongDescription(String longDescription) {
		LongDescription = longDescription;
	}
}

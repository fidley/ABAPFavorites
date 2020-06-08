package com.abapblog.favorites.common;

public enum NameSorting {

	objectName {
		@Override
		public String toString() {
			return "objectName";
		}
	},
	objectDescription {
		@Override
		public String toString() {
			return "objectDescription";
		}
	}

}

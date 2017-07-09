
package com.abapblog.favorites.common;

public class CommonTypes {

	public static enum TypeOfEntry {
		Folder, Transaction, URL, Program, Class, Interface, FunctionGroup, FunctionModule, Include
	};

	public static enum TypeOfXMLNode {

		folderNode {
			public String toString() {
				return "folder";
			}

			public Boolean isNameToUpper() {
				return false;
			}
		},
		folderDONode {
			public String toString() {
				return "folderDO";
			}

			public Boolean isNameToUpper() {
				return false;
			}
		},
		transactionNode {
			public String toString() {
				return "transaction";
			}

			public Boolean isNameToUpper() {
				return true;
			}
		},
		urlNode {
			public String toString() {
				return "url";
			}

			public Boolean isNameToUpper() {
				return false;
			}
		},
		programNode {
			public String toString() {
				return "program";
			}

			public Boolean isNameToUpper() {
				return true;
			}
		},
		classNode {
			public String toString() {
				return "class";
			}

			public Boolean isNameToUpper() {
				return true;
			}
		},
		interfaceNode {
			public String toString() {
				return "interface";
			}

			public Boolean isNameToUpper() {
				return true;
			}
		},
		includeNode {
			public String toString() {
				return "include";
			}

			public Boolean isNameToUpper() {
				return true;
			}
		},
		functionGroupNode {
			public String toString() {
				return "functionGroup";
			}

			public Boolean isNameToUpper() {
				return true;
			}
		},
		functionModuleNode {
			public String toString() {
				return "functionModule";
			}

			public Boolean isNameToUpper() {
				return true;
			}
		};

		public Boolean isNameToUpper() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	public static enum TypeOfObject {

		programType {
			public String toString() {
				return "PROG/P";
			}
		},
		includeType {
			public String toString() {
				return "PROG/I";
			}
		},
		classType {
			public String toString() {
				return "CLAS/OC";
			}
		},
		interfaceType {
			public String toString() {
				return "INTF/OI";
			}
		},
		FunctionModuleRFCType {
			public String toString() {
				return "SRFC";
			}
		},
		FunctionModuleType {
			public String toString() {
				return "FUGR/FF";
			}
		},
		FunctionGroupType {
			public String toString() {
				return "FUGR/F";
			}
		},
		FunctionGroupIncludeType {
			public String toString() {
				return "FUGR/I";
			}
		}
	}

	public static enum TypeOfXMLAttr {
		name {
			public String toString() {
				return "name";
			}
		},
		description {
			public String toString() {
				return "description";
			}
		},
		projectIndependent {
			public String toString() {
				return "projectIndependent";
			}
		},
		project {
			public String toString() {
				return "project";
			}
		},
		technicalName {
			public String toString() {
				return "technicalName";
			}
		},
		devObjFolder {
			public String toString() {
				return "devObjFolder";
			}
		}
	}

}

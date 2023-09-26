
package com.abapblog.favorites.common;

public class CommonTypes {

	public static enum TypeOfEntry {
		Folder, Transaction, URL, Program, Class, Interface, FunctionGroup, FunctionModule, Include, FolderDO, View,
		Table, MessageClass, SearchHelp, ADTLink, CDSView, AMDP, Package
	};

	public static enum TypeOfXMLNode {

		folderNode {
			@Override
			public String toString() {
				return "folder";
			}

			@Override
			public Boolean isNameToUpper() {
				return false;
			}
		},
		folderDONode {
			@Override
			public String toString() {
				return "folderDO";
			}

			@Override
			public Boolean isNameToUpper() {
				return false;
			}
		},
		transactionNode {
			@Override
			public String toString() {
				return "transaction";
			}

			@Override
			public Boolean isNameToUpper() {
				return true;
			}
		},
		urlNode {
			@Override
			public String toString() {
				return "url";
			}

			@Override
			public Boolean isNameToUpper() {
				return false;
			}
		},
		programNode {
			@Override
			public String toString() {
				return "program";
			}

			@Override
			public Boolean isNameToUpper() {
				return true;
			}
		},
		classNode {
			@Override
			public String toString() {
				return "class";
			}

			@Override
			public Boolean isNameToUpper() {
				return true;
			}
		},
		interfaceNode {
			@Override
			public String toString() {
				return "interface";
			}

			@Override
			public Boolean isNameToUpper() {
				return true;
			}
		},
		includeNode {
			@Override
			public String toString() {
				return "include";
			}

			@Override
			public Boolean isNameToUpper() {
				return true;
			}
		},
		viewNode {
			@Override
			public String toString() {
				return "view";
			}

			@Override
			public Boolean isNameToUpper() {
				return true;
			}
		},
		tableNode {
			@Override
			public String toString() {
				return "table";
			}

			@Override
			public Boolean isNameToUpper() {
				return true;
			}
		},
		messageClassNode {
			@Override
			public String toString() {
				return "messageClass";
			}

			@Override
			public Boolean isNameToUpper() {
				return true;
			}
		},
		searchHelpNode {
			@Override
			public String toString() {
				return "searchHelp";
			}

			@Override
			public Boolean isNameToUpper() {
				return true;
			}
		},
		functionGroupNode {
			@Override
			public String toString() {
				return "functionGroup";
			}

			@Override
			public Boolean isNameToUpper() {
				return true;
			}
		},
		functionModuleNode {
			@Override
			public String toString() {
				return "functionModule";
			}

			@Override
			public Boolean isNameToUpper() {
				return true;
			}

		},
		ADTLinkNode {
			@Override
			public String toString() {
				return "adtLink";
			}

			@Override
			public Boolean isNameToUpper() {
				return false;
			}

		},
		CDSViewNode {
			@Override
			public String toString() {
				return "cdsView";
			}

			@Override
			public Boolean isNameToUpper() {
				return false;
			}

		},
		Package {
			@Override
			public String toString() {
				return "package";
			}

			@Override
			public Boolean isNameToUpper() {
				return false;
			}

		},
		AMDPNode {
			@Override
			public String toString() {
				return "amdp";
			}

			@Override
			public Boolean isNameToUpper() {
				return false;
			}

		};

		public Boolean isNameToUpper() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	public static enum TypeOfObject {

		programType {
			@Override
			public String toString() {
				return "PROG/P";
			}
		},
		includeType {
			@Override
			public String toString() {
				return "PROG/I";
			}
		},
		classType {
			@Override
			public String toString() {
				return "CLAS/OC";
			}
		},
		interfaceType {
			@Override
			public String toString() {
				return "INTF/OI";
			}
		},
		FunctionModuleRFCType {
			@Override
			public String toString() {
				return "SRFC";
			}
		},
		FunctionModuleType {
			@Override
			public String toString() {
				return "FUGR/FF";
			}
		},
		FunctionGroupType {
			@Override
			public String toString() {
				return "FUGR/F";
			}
		},
		FunctionGroupIncludeType {
			@Override
			public String toString() {
				return "FUGR/I";
			}
		},
		ViewType {
			@Override
			public String toString() {
				return "VIEW/DV";
			}
		},
		TableType {
			@Override
			public String toString() {
				return "TABL/DT";
			}
		},
		MessageClassType {
			@Override
			public String toString() {
				return "MSAG/N";
			}
		},
		SearchHelpType {
			@Override
			public String toString() {
				return "SHLP/DH";
			}
		},
		CDSViewType {
			@Override
			public String toString() {
				return "DDLS/DF";
			}
		},
		AMDPType {
			@Override
			public String toString() {
				return "SHLP/DH";
			}
		},
		TransactionType {
			@Override
			public String toString() {
				return "TRAN/T";
			}
		},
		PackageType {
			@Override
			public String toString() {
				return "DEVC/K";
			}
		}
	}

	public static enum TypeOfXMLAttr {
		name {
			@Override
			public String toString() {
				return "name";
			}
		},
		description {
			@Override
			public String toString() {
				return "description";
			}
		},
		longDescription {
			@Override
			public String toString() {
				return "longDescription";
			}
		},
		projectIndependent {
			@Override
			public String toString() {
				return "projectIndependent";
			}
		},
		project {
			@Override
			public String toString() {
				return "project";
			}
		},
		technicalName {
			@Override
			public String toString() {
				return "technicalName";
			}
		},
		parentFolderID {
			@Override
			public String toString() {
				return "parentFolderID";
			}
		},
		folderID {
			@Override
			public String toString() {
				return "folderID";
			}
		},
		devObjFolder {
			@Override
			public String toString() {
				return "devObjFolder";
			}
		},
		commandID {
			@Override
			public String toString() {
				return "commandID";
			}
		}
	}

}
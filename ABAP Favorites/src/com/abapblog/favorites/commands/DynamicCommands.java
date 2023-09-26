package com.abapblog.favorites.commands;

public enum DynamicCommands {

	Favorite0(1, DynamicCommandHandler.COM_ABAPBLOG_FAVORITES_COMMANDS_COMMAND0) {

		@Override
		public String toString() {
			return "Favorite 0";
		}

	},

	Favorite1(2, DynamicCommandHandler.COM_ABAPBLOG_FAVORITES_COMMANDS_COMMAND1) {

		@Override
		public String toString() {
			return "Favorite 1";
		}

	},

	Favorite2(3, DynamicCommandHandler.COM_ABAPBLOG_FAVORITES_COMMANDS_COMMAND2) {

		@Override
		public String toString() {
			return "Favorite 2";
		}

	},
	Favorite3(4, DynamicCommandHandler.COM_ABAPBLOG_FAVORITES_COMMANDS_COMMAND3) {

		@Override
		public String toString() {
			return "Favorite 3";
		}

	},
	Favorite4(5, DynamicCommandHandler.COM_ABAPBLOG_FAVORITES_COMMANDS_COMMAND4) {

		@Override
		public String toString() {
			return "Favorite 4";
		}

	},
	Favorite5(6, DynamicCommandHandler.COM_ABAPBLOG_FAVORITES_COMMANDS_COMMAND5) {

		@Override
		public String toString() {
			return "Favorite 5";
		}

	},
	Favorite6(7, DynamicCommandHandler.COM_ABAPBLOG_FAVORITES_COMMANDS_COMMAND6) {

		@Override
		public String toString() {
			return "Favorite 6";
		}

	},
	Favorite7(8, DynamicCommandHandler.COM_ABAPBLOG_FAVORITES_COMMANDS_COMMAND7) {

		@Override
		public String toString() {
			return "Favorite 7";
		}

	},
	Favorite8(9, DynamicCommandHandler.COM_ABAPBLOG_FAVORITES_COMMANDS_COMMAND8) {

		@Override
		public String toString() {
			return "Favorite 8";
		}

	},

	Favorite9(10, DynamicCommandHandler.COM_ABAPBLOG_FAVORITES_COMMANDS_COMMAND9) {

		@Override
		public String toString() {
			return "Favorite 9";
		}

	};

	private final int comboIndex;
	private final String commandID;

	private DynamicCommands(final int comboIndex, final String commandID) {
		this.comboIndex = comboIndex;
		this.commandID = commandID;
	}

	public String getCommandID() {
		return commandID;
	}

	public int getComboIndex() {
		return comboIndex;
	}

	public static DynamicCommands getByIndex(int index) {
		switch (index) {
		case 1:
			return DynamicCommands.Favorite0;
		case 2:
			return DynamicCommands.Favorite1;
		case 3:
			return DynamicCommands.Favorite2;
		case 4:
			return DynamicCommands.Favorite3;
		case 5:
			return DynamicCommands.Favorite4;
		case 6:
			return DynamicCommands.Favorite5;
		case 7:
			return DynamicCommands.Favorite6;
		case 8:
			return DynamicCommands.Favorite7;
		case 9:
			return DynamicCommands.Favorite8;
		case 10:
			return DynamicCommands.Favorite9;
		default:
			return null;
		}

	}

	public static DynamicCommands getByCommandID(String commandID) {
		switch (commandID) {
		case DynamicCommandHandler.COM_ABAPBLOG_FAVORITES_COMMANDS_COMMAND0:
			return DynamicCommands.Favorite0;
		case DynamicCommandHandler.COM_ABAPBLOG_FAVORITES_COMMANDS_COMMAND1:
			return DynamicCommands.Favorite1;
		case DynamicCommandHandler.COM_ABAPBLOG_FAVORITES_COMMANDS_COMMAND2:
			return DynamicCommands.Favorite2;
		case DynamicCommandHandler.COM_ABAPBLOG_FAVORITES_COMMANDS_COMMAND3:
			return DynamicCommands.Favorite3;
		case DynamicCommandHandler.COM_ABAPBLOG_FAVORITES_COMMANDS_COMMAND4:
			return DynamicCommands.Favorite4;
		case DynamicCommandHandler.COM_ABAPBLOG_FAVORITES_COMMANDS_COMMAND5:
			return DynamicCommands.Favorite5;
		case DynamicCommandHandler.COM_ABAPBLOG_FAVORITES_COMMANDS_COMMAND6:
			return DynamicCommands.Favorite6;
		case DynamicCommandHandler.COM_ABAPBLOG_FAVORITES_COMMANDS_COMMAND7:
			return DynamicCommands.Favorite7;
		case DynamicCommandHandler.COM_ABAPBLOG_FAVORITES_COMMANDS_COMMAND8:
			return DynamicCommands.Favorite8;
		case DynamicCommandHandler.COM_ABAPBLOG_FAVORITES_COMMANDS_COMMAND9:
			return DynamicCommands.Favorite9;
		default:
			return null;
		}

	}
}

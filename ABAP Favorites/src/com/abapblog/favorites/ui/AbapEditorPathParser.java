package com.abapblog.favorites.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.abapblog.favorites.common.*;
import com.abapblog.favorites.common.CommonTypes.TypeOfEntry;
import com.abapblog.favorites.common.CommonTypes.TypeOfObject;

public class AbapEditorPathParser {


	private static final String ADT_CLASSLIB_CLASSES = "((?<=adt\\/classlib\\/classes\\/)(.*)(?=\\/))";
	private static final String ADT_CLASSLIB_INTERFACES = "(?<=adt\\/classlib\\/interfaces\\/)(.*)(?=\\/)";
	private static final String ADT_FUNCTIONS_GROUPS_FMODULES = "(?<=adt\\/functions\\/groups\\/)(\\w*\\/fmodules\\/)(\\w*)(?=\\/)";
	private static final String ADT_FUNCTIONS_GROUPS_INCLUDES = "(?<=adt\\/functions\\/groups\\/)(\\w*\\/includes\\/)(\\w*)(?=\\/)";
	private static final String ADT_FUNCTIONS_GROUPS = "(?<=adt\\/functions\\/groups\\/)(\\w*)";
	private static final String ADT_MC_MESSAGECLASSES = "(?<=adt\\/mc\\/messageclasses\\/)(.*)(?=\\/)";
	private static final String ADT_PROGRAMS_PROGRAMS = "(?<=adt\\/programs\\/programs\\/)(.*)(?=\\/)";
	private static final String ADT_PROGRAMS_INCLUDES = "(?<=adt\\/programs\\/includes\\/)(.*)(?=\\/)";
	private static final Pattern patternClasses = Pattern.compile(ADT_CLASSLIB_CLASSES);
	private static final Pattern patternInterfaces = Pattern.compile(ADT_CLASSLIB_INTERFACES);
	private static final Pattern patternFM = Pattern.compile(ADT_FUNCTIONS_GROUPS_FMODULES);
	private static final Pattern patternFGInclude = Pattern.compile(ADT_FUNCTIONS_GROUPS_INCLUDES);
	private static final Pattern patternFGroup = Pattern.compile(ADT_FUNCTIONS_GROUPS);
	private static final Pattern patternMessageClasses = Pattern.compile(ADT_MC_MESSAGECLASSES);
	private static final Pattern patternPrograms = Pattern.compile(ADT_PROGRAMS_PROGRAMS);
	private static final Pattern patternIncludes = Pattern.compile(ADT_PROGRAMS_INCLUDES);


	public static String getObjectName(CommonTypes.TypeOfEntry typeOfEntry, String path)
	{
		switch(typeOfEntry) {
		case AMDP:
			break;
		case CDSView:
			break;
		case Program:
			String ProgName = getName(path, patternFGInclude);
			if (ProgName == "")
				ProgName = getName(path, patternPrograms);
			return ProgName;
		case FunctionGroup:
			return getName(path, patternFGroup);
		case FunctionModule:
			return getName(path, patternFM);
		case MessageClass:
			return getName(path, patternMessageClasses);
		case SearchHelp:
			break;
		case Table:
			break;
		case View:
			break;
		case Class:
			return getName(path, patternClasses);
		case Include:
			return getName(path, patternIncludes);
		case Interface:
			return getName(path, patternInterfaces);
		default:
			break;

		}

		return "";
	}


	public static String getObjectName(String path)
	{
		return getObjectName(getType(path), path);
	}

	public static CommonTypes.TypeOfEntry getType(String path) {
		if (checkClass(path)) {
			return TypeOfEntry.Class;
		}
		else if (checkInterface(path)){
			return TypeOfEntry.Interface;
		}
		else if (checkFModule(path)){
			return TypeOfEntry.FunctionModule;
		}
		else if (checkFGInclude(path)){
			return TypeOfEntry.Program;
		}
		else if (checkFGroup(path)){
			return TypeOfEntry.FunctionGroup;
		}
		else if (checkMessageClass(path)){
			return TypeOfEntry.MessageClass;
		}
		else if (checkProgram(path)){
			return TypeOfEntry.Program;
		}
		else if (checkInclude(path)){
			return TypeOfEntry.Include;
		}
		else
		{
			return null;
		}
	}

	private static Boolean checkClass(String path) {
		if (checkRegex(path,patternClasses)) {
			return true;
		}
		else
		{
			return false;
		}
	}

	private static Boolean checkInterface(String path) {
		if (checkRegex(path,patternInterfaces)) {
			return true;
		}
		else
		{
			return false;
		}
	}

	private static Boolean checkFModule(String path) {
		if (checkRegex(path,patternFM)) {
			return true;
		}
		else
		{
			return false;
		}
	}

	private static Boolean checkFGInclude(String path) {
		if (checkRegex(path,patternFGInclude)) {
			return true;
		}
		else
		{
			return false;
		}
	}

	private static Boolean checkFGroup(String path) {
		if (checkRegex(path,patternFGroup)) {
			return true;
		}
		else
		{
			return false;
		}
	}

	private static Boolean checkMessageClass(String path) {
		if (checkRegex(path,patternMessageClasses)) {
			return true;
		}
		else
		{
			return false;
		}
	}

	private static Boolean checkProgram(String path) {
		if (checkRegex(path,patternPrograms)) {
			return true;
		}
		else
		{
			return false;
		}
	}

	private static Boolean checkInclude(String path) {
		if (checkRegex(path,patternIncludes)) {
			return true;
		}
		else
		{
			return false;
		}
	}

	private static Boolean checkRegex(String path, Pattern pattern) {
		Matcher m = pattern.matcher(path);
		if (m.find()){
			return true;
		}
		else
		{
			return false;
		}
	}

	private static String getName(String path, Pattern pattern) {
		Matcher m = pattern.matcher(path);
		if (m.find()){
			return m.group(m.groupCount());
		}
		else
		{
			return "";
		}
	}

}

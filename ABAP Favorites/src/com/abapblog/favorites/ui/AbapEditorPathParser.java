package com.abapblog.favorites.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.abapblog.favorites.common.*;
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


	public static String getObjectName(CommonTypes.TypeOfObject typeOfObject, String path)
	{
		switch(typeOfObject) {
		case AMDPType:
			break;
		case CDSViewType:
			break;
		case FunctionGroupIncludeType:
			return getName(path, patternFGInclude);
		case FunctionGroupType:
			return getName(path, patternFGroup);
		case FunctionModuleRFCType:
			break;
		case FunctionModuleType:
			return getName(path, patternFM);
		case MessageClassType:
			return getName(path, patternMessageClasses);
		case SearchHelpType:
			break;
		case TableType:
			break;
		case ViewType:
			break;
		case classType:
			return getName(path, patternClasses);
		case includeType:
			return getName(path, patternIncludes);
		case interfaceType:
			return getName(path, patternInterfaces);
		case programType:
			return getName(path, patternPrograms);
		default:
			break;

		}

		return "";
	}


	public static String getObjectName(String path)
	{
		return getObjectName(getType(path), path);
	}

	public static CommonTypes.TypeOfObject getType(String path) {
		if (checkClass(path)) {
			return TypeOfObject.classType;
		}
		else if (checkInterface(path)){
			return TypeOfObject.interfaceType;
		}
		else if (checkFModule(path)){
			return TypeOfObject.FunctionModuleType;
		}
		else if (checkFGInclude(path)){
			return TypeOfObject.FunctionGroupIncludeType;
		}
		else if (checkFGroup(path)){
			return TypeOfObject.FunctionGroupType;
		}
		else if (checkMessageClass(path)){
			return TypeOfObject.MessageClassType;
		}
		else if (checkProgram(path)){
			return TypeOfObject.programType;
		}
		else if (checkInclude(path)){
			return TypeOfObject.includeType;
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

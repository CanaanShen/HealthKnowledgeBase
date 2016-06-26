package knowledge.project.util;

import java.util.ArrayList;
import java.util.List;

public class ConfigUtil {
	
	public static String First_Indent = "    ";
	public static String Second_Indent = First_Indent + "    ";
	public static String Third_Indent = Second_Indent + "    ";
	public static String Fourth_Indent = Third_Indent + "    ";
	
	public static List<String> concernedTypeList = new ArrayList<String>();
	static {
		ConfigUtil.concernedTypeList.add("root");
		ConfigUtil.concernedTypeList.add("dobj");
		ConfigUtil.concernedTypeList.add("relcl");
		ConfigUtil.concernedTypeList.add("conj");
		ConfigUtil.concernedTypeList.add("ccomp");
		ConfigUtil.concernedTypeList.add("nsubj");
		ConfigUtil.concernedTypeList.add("amod");
		ConfigUtil.concernedTypeList.add("comod");
		ConfigUtil.concernedTypeList.add("nn");
		ConfigUtil.concernedTypeList.add("assmod");
		ConfigUtil.concernedTypeList.add("advmod");
	}

	public static List<String> containedExcludedList = new ArrayList<String>();
	static{
		ConfigUtil.containedExcludedList.add("�?");
		ConfigUtil.containedExcludedList.add("�?");
		ConfigUtil.containedExcludedList.add("�?");
		ConfigUtil.containedExcludedList.add("�?");
		ConfigUtil.containedExcludedList.add("�?");
		ConfigUtil.containedExcludedList.add("''");
		ConfigUtil.containedExcludedList.add("%");
		ConfigUtil.containedExcludedList.add("``");
		ConfigUtil.containedExcludedList.add("-");
		ConfigUtil.containedExcludedList.add("�?");
	}

	public static List<String> equalExcludedList = new ArrayList<String>();
	static{
		ConfigUtil.equalExcludedList.add("�?");
		ConfigUtil.equalExcludedList.add("�?");
		ConfigUtil.equalExcludedList.add("主要");
		ConfigUtil.equalExcludedList.add("引起");
		ConfigUtil.equalExcludedList.add("'");
		ConfigUtil.equalExcludedList.add("起到");
		ConfigUtil.equalExcludedList.add("直接");
		ConfigUtil.equalExcludedList.add("发生");
		ConfigUtil.equalExcludedList.add("之一");
		ConfigUtil.equalExcludedList.add("重要");
		ConfigUtil.equalExcludedList.add("具有");
		ConfigUtil.equalExcludedList.add("目的");
		ConfigUtil.equalExcludedList.add("认为");
		ConfigUtil.equalExcludedList.add("成分");
		ConfigUtil.equalExcludedList.add("作用");
		ConfigUtil.equalExcludedList.add("有解");
		ConfigUtil.equalExcludedList.add("物质");
		ConfigUtil.equalExcludedList.add("元素");
		ConfigUtil.equalExcludedList.add("发现");
		ConfigUtil.equalExcludedList.add("人员");
		ConfigUtil.equalExcludedList.add("选择");
		ConfigUtil.equalExcludedList.add("放入");	
		ConfigUtil.equalExcludedList.add("原理");	
		ConfigUtil.equalExcludedList.add("含量");
		ConfigUtil.equalExcludedList.add("卢仝");
		ConfigUtil.equalExcludedList.add(";");
		ConfigUtil.equalExcludedList.add(".");
		ConfigUtil.equalExcludedList.add(":");
		ConfigUtil.equalExcludedList.add("~");
	}
	
	public static List<String> unsuitAndSuitPOSStrList = new ArrayList<String>();
	public static List<String> unsuitAndSuitBlankSuffixList = new ArrayList<String>();
	public static List<String> unsuitAndSuitBlankPrefixList = new ArrayList<String>();
	public static List<String> unsuitAndSuitBlankExcludeList = new ArrayList<String>();
	
	static{
		ConfigUtil.unsuitAndSuitPOSStrList.add("PU");
		ConfigUtil.unsuitAndSuitPOSStrList.add("DEG");
		ConfigUtil.unsuitAndSuitPOSStrList.add("DEC");
		ConfigUtil.unsuitAndSuitPOSStrList.add("CC");
		ConfigUtil.unsuitAndSuitPOSStrList.add("DER");
		ConfigUtil.unsuitAndSuitPOSStrList.add("AS");
		ConfigUtil.unsuitAndSuitPOSStrList.add("ETC");
		ConfigUtil.unsuitAndSuitPOSStrList.add("P");
		ConfigUtil.unsuitAndSuitPOSStrList.add("VE");
	
		ConfigUtil.unsuitAndSuitBlankSuffixList.add("患�??");    //append two blank spaces behind
		ConfigUtil.unsuitAndSuitBlankSuffixList.add("适合");
		ConfigUtil.unsuitAndSuitBlankSuffixList.add("适宜");
		ConfigUtil.unsuitAndSuitBlankSuffixList.add("患有");
	
		ConfigUtil.unsuitAndSuitBlankPrefixList.add("忌食");	//append two blank spaces front
		ConfigUtil.unsuitAndSuitBlankPrefixList.add("慎服");
		ConfigUtil.unsuitAndSuitBlankPrefixList.add("慎食");
		ConfigUtil.unsuitAndSuitBlankPrefixList.add("患�??");
	
		ConfigUtil.unsuitAndSuitBlankExcludeList.add("及其");
		ConfigUtil.unsuitAndSuitBlankExcludeList.add("�?");
		ConfigUtil.unsuitAndSuitBlankExcludeList.add("同时");
		ConfigUtil.unsuitAndSuitBlankExcludeList.add("以及");
		ConfigUtil.unsuitAndSuitBlankExcludeList.add("(");
		ConfigUtil.unsuitAndSuitBlankExcludeList.add(")");
		ConfigUtil.unsuitAndSuitBlankExcludeList.add("�?�?");
		ConfigUtil.unsuitAndSuitBlankExcludeList.add("人食�?");
		ConfigUtil.unsuitAndSuitBlankExcludeList.add("食用");
		ConfigUtil.unsuitAndSuitBlankExcludeList.add("�?");
		ConfigUtil.unsuitAndSuitBlankExcludeList.add(".");
		ConfigUtil.unsuitAndSuitBlankExcludeList.add("1");
		ConfigUtil.unsuitAndSuitBlankExcludeList.add("2");
		ConfigUtil.unsuitAndSuitBlankExcludeList.add("3");
		ConfigUtil.unsuitAndSuitBlankExcludeList.add("4");
		ConfigUtil.unsuitAndSuitBlankExcludeList.add("5");
		ConfigUtil.unsuitAndSuitBlankExcludeList.add(";");
	}

}

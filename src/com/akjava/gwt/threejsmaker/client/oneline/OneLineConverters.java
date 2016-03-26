package com.akjava.gwt.threejsmaker.client.oneline;

import java.util.List;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.lib.common.utils.StringUtils;
import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

public class OneLineConverters {
	private OneLineConverters(){}
	public static class UniformsConverter extends OneLineConverter{
		RegExp regExp = RegExp.compile("\\.uniforms\\[(.*)\\].setValue\\((.*)\\)");
		@Override
		public String apply(String input) {
			if(input.isEmpty()){
				return "";
			}

			String line=input;
			boolean matchFound=false;
			do{
			MatchResult matcher = regExp.exec(line);
			matchFound = matcher != null;
			if(matchFound){
				 //0 is special
				 for (int i = 0; i < matcher.getGroupCount(); i++) {
				       String groupStr = matcher.getGroup(i);
				      
				    }
				 List<List<String>> lists=Lists.newArrayList();
				 lists.add(Lists.newArrayList(matcher.getGroup(0), ".getUniforms().set("+toJavaKeyName(matcher.getGroup(1).trim())+","+matcher.getGroup(2).trim()+")"));
				 line=StringUtils.replaceStrings(line,lists);
				 
			}
			}while(matchFound);
			
			
			
			
			
			
			return line;
		}

		@Override
		public String getName() {
			return "convert converted .uniform";
		}
	}
	private static String toJavaKeyName(String keyWithQuote){
		if(keyWithQuote.isEmpty()){
			return "";
		}
		
		if(keyWithQuote.length()>=2 && keyWithQuote.startsWith("'") && keyWithQuote.endsWith("'")){
			return "\""+keyWithQuote.substring(1,keyWithQuote.length()-1)+"\"";
		}else{
			return keyWithQuote;
		}
		
	}
	public static class Uniforms2Converter extends OneLineConverter{
		RegExp regExp = RegExp.compile("\\.uniforms\\[(.*)\\]\\.getValue\\(\\)\\.setRGB\\((.*)\\)");
		@Override
		public String apply(String input) {
			if(input.isEmpty()){
				return "";
			}

			String line=input;
			boolean matchFound=false;
			do{
			MatchResult matcher = regExp.exec(line);
			matchFound = matcher != null;
			if(matchFound){
				 //0 is special
				 for (int i = 0; i < matcher.getGroupCount(); i++) {
				       String groupStr = matcher.getGroup(i);
				      
				    }
				 List<List<String>> lists=Lists.newArrayList();
				 lists.add(Lists.newArrayList(matcher.getGroup(0), ".getUniforms().setRGB("+toJavaKeyName(matcher.getGroup(1).trim())+","+matcher.getGroup(2).trim()+")"));
				 line=StringUtils.replaceStrings(line,lists);
				 
			}
			}while(matchFound);
			
			
			
			
			
			
			return line;
		}

		@Override
		public String getName() {
			return "convert converted .uniform setRGB";
		}
	}
	public static class ForConverter extends OneLineConverter{
		RegExp regExp = RegExp.compile("for\\((.*?);");
		@Override
		public String apply(String input) {
			if(input.isEmpty()){
				return "";
			}

			String line=input;
			boolean matchFound=false;
			
			MatchResult matcher = regExp.exec(line);
			matchFound = matcher != null;
			if(matchFound){
				
				 String value=matcher.getGroup(1).trim();
				 //start with var
				 if(value.startsWith("var") && CharMatcher.WHITESPACE.matches(value.charAt(3))){
					 value=value.substring(3).trim();
				 }
				
				 List<List<String>> lists=Lists.newArrayList();
				 lists.add(Lists.newArrayList(matcher.getGroup(0), "for(int "+value+";"));
				 line=StringUtils.replaceStrings(line,lists);
				 
			}
			
			
			
			
			
			
			
			return line;
		}

		@Override
		public String getName() {
			return "for( to for(int ";
		}
	}
	
	public static class ArrayAccessConverter extends OneLineConverter{
		RegExp regExp = RegExp.compile("\\[(\\d+?)\\]");
		@Override
		public String apply(String input) {
			if(input.isEmpty()){
				return "";
			}

			String line=input;
			boolean matchFound=false;
			
			MatchResult matcher = regExp.exec(line);
			matchFound = matcher != null;
			if(matchFound){
				
				 String indexNumber=matcher.getGroup(1).trim();
				
				 List<List<String>> lists=Lists.newArrayList();
				 lists.add(Lists.newArrayList(matcher.getGroup(0), ".get("+indexNumber+")"));
				 line=StringUtils.replaceStrings(line,lists);
				 
			}
			
			
			
			
			
			
			
			return line;
		}

		@Override
		public String getName() {
			return "array access converter [0] >> .get(0)";
		}
	}
}

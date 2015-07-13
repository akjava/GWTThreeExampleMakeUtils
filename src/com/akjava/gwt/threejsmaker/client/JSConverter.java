package com.akjava.gwt.threejsmaker.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.akjava.lib.common.utils.TemplateUtils;
import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class JSConverter {

	public static String convertCode(String classBase,JSClass clazz){
		Map<String,String> map=new HashMap<String, String>();
		map.put("className", clazz.getName());
		map.put("package", clazz.getPackagePath().replace("/", "."));
		
		String methods="";
		//field getter
		List<String> getterSetter=Lists.transform(clazz.getFields(), new JSFieldToGetterSetterFunction());
		methods+=Joiner.on("\r\n").join(getterSetter);
		
		List<String> functions=Lists.transform(clazz.getFunctions(), new JSFunctionToTextFunction(clazz));
		methods+=Joiner.on("\r\n").join(functions);
		
		//js getter setter
		List<String> jsgetters=Lists.transform(clazz.getGetters(), new JSGetterToTextFunction(clazz));
		methods+=Joiner.on("\r\n").join(jsgetters);
		
		List<String> jssetters=Lists.transform(clazz.getGetters(), new JSSetterToTextFunction(clazz));
		methods+=Joiner.on("\r\n").join(jssetters);
		
		
		map.put("methods", methods);
		
		return TemplateUtils.createText(classBase, map);
	}
	
	public static class JSFunctionToTextFunction implements Function<JSFunction,String>{
		private JSClass clazz;
		public JSFunctionToTextFunction(JSClass clazz){
			this.clazz=clazz;
		}
		String functionBase="public ${static}final native ${return} ${functionName}(${args})/*-{"+"\r\n"+
				"${inside};"+"\r\n"+
				"}-*/;"+"\r\n";
		@Override
		public String apply(JSFunction function) {
			Map<String,String> map=new HashMap<String, String>();
			map.put("static", clazz.isStaticClass()?"static ":"");
			String ret=function.getReturnType();
			boolean doReturn=true;
			if(ret==null){
				ret="void";
				doReturn=false;
			}else if(ret.equals(JSClass.TYPE_UNKNOWN)){
				ret="Object";
			}
			map.put("return", ret);
			map.put("functionName", function.getName());
			List<JSField> args=function.getArgs();
			map.put("args", Joiner.on(",").join(Lists.transform(args, new JSFieldToArgText())));
			String inside=new JSFunctionToNative().apply(function);
			if(doReturn){
				inside="return "+inside;
			}
			map.put("inside",inside);
			return TemplateUtils.createAdvancedText(functionBase, map);
		}
		
	}
	
	public static class JSGetterToTextFunction implements Function<JSFunction,String>{
		private JSClass clazz;
		public JSGetterToTextFunction(JSClass clazz){
			this.clazz=clazz;
		}
		String functionBase="public ${static}final native ${return} ${functionName}()/*-{"+"\r\n"+
				"${inside};"+"\r\n"+
				"}-*/;"+"\r\n";
		@Override
		public String apply(JSFunction function) {
			Map<String,String> map=new HashMap<String, String>();
			map.put("static", clazz.isStaticClass()?"static ":"");
			map.put("return", "Object");
			map.put("functionName", "get"+ValuesUtils.toUpperCamel(function.getName()));
			List<JSField> args=function.getArgs();
			map.put("args", Joiner.on(",").join(Lists.transform(args, new JSFieldToArgText())));
			
			
			String inside="return this."+function.getName()+"";
			map.put("inside",inside);
			return TemplateUtils.createAdvancedText(functionBase, map);
		}
	}
	
	public static class JSSetterToTextFunction implements Function<JSFunction,String>{
		private JSClass clazz;
		public JSSetterToTextFunction(JSClass clazz){
			this.clazz=clazz;
		}
		String functionBase="public ${static}final native void ${functionName}(${args})/*-{"+"\r\n"+
				"${inside};"+"\r\n"+
				"}-*/;"+"\r\n";
		@Override
		public String apply(JSFunction function) {
			Map<String,String> map=new HashMap<String, String>();
			map.put("static", clazz.isStaticClass()?"static ":"");
			
			map.put("functionName", "set"+ValuesUtils.toUpperCamel(function.getName()));
			/**
			List<JSField> args=function.getArgs();
			map.put("args", Joiner.on(",").join(Lists.transform(args, new JSFieldToArgText())));
			*/
			map.put("args","Object value");
			
			String arg="value";
			if(function.getArgs().size()>0){
				arg=function.getArgs().get(0).getName();
			}
			String inside="this."+function.getName()+"="+arg+"";
			map.put("inside",inside);
			return TemplateUtils.createAdvancedText(functionBase, map);
		}
	}
	
	public static class JSFunctionToNative implements Function<JSFunction,String>{

		@Override
		public String apply(JSFunction function) {
			String args=Joiner.on(",").join(Lists.transform(function.getArgs(), new JSFieldToName()));
			return "this."+function.getName()+"("+args+")";
		}
		
	}
	public static class JSFieldToArgText implements Function<JSField,String>{

		@Override
		public String apply(JSField field) {
			String type=field.getType();
			if(field.getType().equals(JSClass.TYPE_UNKNOWN)){
				type="Object";
			}
			//List<String> args=new ArrayList<String>();
			//args.add(type+" "+field.getName());
			//return Joiner.on(",").join(args);
			return type+" "+field.getName();
		}
		
	}
	public static class JSFieldToName implements Function<JSField,String>{

		@Override
		public String apply(JSField field) {
			return field.getName();
		}
		
	}
	
	public static class JSFieldToGetterSetterFunction implements Function<JSField,String>{
		String setterBase="public final native void set${u+name}(${type} ${name})/*-{"+"\r\n"+
				"this.${name} = ${name};"+"\r\n"+
				"}-*/;"+"\r\n"+"\r\n";
		String getterBase="public final native ${type} get${u+name}()/*-{"+"\r\n"+
		"return this.${name};"+"\r\n"+
		"}-*/;"+"\r\n"+"\r\n";;
		
		@Override
		public String apply(JSField field) {
			String type=field.getType();
			if(type.equals(JSClass.TYPE_UNKNOWN)){
				type="Object";
			}
			String base=getterBase;
			if(type.equals("boolean")){
				base=base.replace("get", "is");
			}
			Map<String,String> map=new HashMap<String, String>();
			map.put("type", type);
			map.put("name",field.getName());
			
			String text="";
			//getter
			text+=TemplateUtils.createAdvancedText(base, map);
			//setter
			text+=TemplateUtils.createAdvancedText(setterBase, map);
			return text;
		}
		
	}
}

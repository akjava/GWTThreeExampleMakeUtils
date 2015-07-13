package com.akjava.gwt.threejsmaker.client;

import java.util.List;

import com.akjava.lib.common.form.StaticValidators;
import com.akjava.lib.common.form.Validators;
import com.akjava.lib.common.utils.ValuesUtils;

public class JSClassParser {
	private JSClassParser(){}
	/**
	 * can't parse WebGLShaders.js
	 * @param text
	 * @return
	 */
	public static JSClass parseJSClass(String text,String packageName){
		JSClass jsClass=null;
		List<String> lines=ValuesUtils.toListLines(text);
		
		JSFunction lastFuction=null;
		int classLineAt=0;
		int firstPrototype=0;
		
		for(int i=0;i<lines.size();i++){
			String line=lines.get(i);
			if(line.startsWith(" *")){
				continue;//comment for some Util classes
			}
			if(firstPrototype==0 && line.indexOf(".prototype")!=-1){
				firstPrototype=i;
			}
			int returnAt=line.indexOf("return");
			if(returnAt!=-1){
				if(lastFuction!=null){
					if(line.trim().endsWith("this;")){
						lastFuction.setReturnType(jsClass.getName());	
					}else{
					lastFuction.setReturnType(JSClass.TYPE_UNKNOWN);
					}
				}
				continue;
			}
			
			if(jsClass==null){//find class name
			int functionAt=line.indexOf("=");
			if(functionAt!=-1){
					String[] names=line.split("=");
					if(names.length==2){
						boolean isStatic=false;
						if(line.indexOf("function")==-1){
							isStatic=true;
						}
						String path=names[0].trim();
						int last=path.lastIndexOf(".");
						if(last!=-1){
							String name=path.substring(last+1);
							//String packagePath=path.substring(0,last);
							jsClass=new JSClass(name);
							jsClass.setPackagePath(packageName);
							if(isStatic){
								jsClass.setStaticClass(isStatic);
								//System.out.println("static:"+name);
							}
							classLineAt=i;
							
						}
						continue;
					}else{
						System.out.println("invalid class name:"+line);
						continue;
						//throw new RuntimeException("invalid class name:\n"+line+"\n\n"+text);
					}
				}
			
			}
			//find method
			int functionAt=line.indexOf("function");
			if(functionAt!=-1){
				String[] names=line.split("=");
				if(names.length==2){
					
					
					String path=names[0].trim();
					int last=path.lastIndexOf(".");
					if(last!=-1){
						String functionName=path.substring(last+1).trim();
						if(!StaticValidators.asciiNumberAndCharAndUnderbarOnly().validate(functionName)){
							//invalid name
							continue;
						}
						JSFunction function=parseFunction(functionName,names[1]);
						//System.out.println("add function:"+function);
						jsClass.addFunction(function);
						lastFuction=function;
						
						
						
					}else{
						if(line.indexOf("var ")!=-1){
							//maybe inner function
						}else{
						System.out.println("no . function:"+line);
						}
					}
					continue;
				}else{
					int  colon=line.indexOf(":");
					if(colon==-1){
						//System.out.println("invalid function:"+line);
						//maybe innner class ignore it
					}else{
						names=line.split(":");
						
						String path=names[0].trim();
						int last=path.lastIndexOf(".");
						if(last==-1){
							String functionName=path.trim();
							
							JSFunction function=parseFunction(functionName,names[1]);
							if(!StaticValidators.asciiNumberAndCharAndUnderbarOnly().validate(functionName)){
								//invalid name
								continue;
							}
							if(!functionName.startsWith("_")){
								//not add inner function
								if(function==null){
									continue;//some invalid case
								}
								jsClass.addFunction(function);
							}
							
							lastFuction=function;
						}else{
							System.out.println("have . function:"+line);
						}
						
					}
					//
					continue;
					//throw new RuntimeException("invalid class name:\n"+line+"\n\n"+text);
				}
			}else{
				//support getter and setter
				if(line.trim().startsWith("get ")||line.trim().startsWith("set ")){
					boolean getter=false;
					if(line.trim().startsWith("get ")){
						getter=true;
					}
					String line2=line.trim();
					int functionStart=line2.indexOf("(");
					if(functionStart!=-1){
						String functionName=line2.substring("get ".length(),functionStart).trim();
							JSFunction function=parseFunction(functionName,line2.substring(functionStart));
							if(getter){
								jsClass.addGetter(function);
							}else{
								jsClass.addSetter(function);
							}
							lastFuction=function;
					}
				}
			}
			
			
		}
		if(jsClass.getName()==null){
			throw new RuntimeException("invalid class name:\n"+text);
		}
		
		//parse class fields between 
		if(firstPrototype==0){
			//System.out.println(jsClass.getName());
		}
		//ignore static
		if(!jsClass.isStaticClass()){
			if(firstPrototype==0){
				firstPrototype=lines.size();
			}
			for(int i=classLineAt+1;i<firstPrototype;i++){
				String line=lines.get(i);
				int eqindex=line.indexOf("=");
				if(eqindex!=-1){
					String name_value[]=line.split("=");
					String name=name_value[0].trim();
					if(name.startsWith("var ")){
						continue;
					}
					if(name.startsWith("_")||name.startsWith("this._")){
						continue;
					}
					if(name.startsWith("this.")){
						name=name.substring(5);
					}
					String type=null;
					String value=name_value[1].trim();
					
				
					
					if(value.endsWith(";")){
						value=value.substring(0,value.length()-1);
					}
					
					
					
					if(value.equals("0")){
						type="double";//some time int
					}else if(value.equals("true") || value.equals("false")){
						type="boolean";//some time int
					}else if(value.startsWith("[")){
						type="JsArray<Object>";//TODO recognize type?
					}else if(value.startsWith("new THREE.")){
						int start="new THREE.".length();
						int last=value.indexOf("(");
						if(last!=-1){
							type=value.substring(start,last);
						}
						
					}else{
						double number=ValuesUtils.toDouble(value, 0);
						if(number!=0){
							type="double";
						}
					}
					
					if(!StaticValidators.asciiNumberAndCharAndUnderbarOnly().validate(name)){
						//invalid name
						continue;
					}
					
					JSField field=new JSField(name);
					if(type!=null){
						field.setType(type);
					}
					jsClass.addField(field);
				}
			}
		}
		
		return jsClass;
	}
	public static JSFunction parseFunction(String functionName,String functionLine){
		int open=functionLine.indexOf("(");
		int close=functionLine.indexOf(")");
		if(open==-1 || close==-1){
			return null;
		}else{
			String inside=functionLine.substring(open+1,close);
			String args[]=inside.split(",");
			
			JSFunction function=new JSFunction(functionName);
			//jsClass.addFunction(function);
			for(String arg:args){
				if(arg.isEmpty()){
					continue;
				}
				function.addArg(new JSField(arg.trim()));
			}
			//lastFuction=function;
			return function;
		}
	}
}

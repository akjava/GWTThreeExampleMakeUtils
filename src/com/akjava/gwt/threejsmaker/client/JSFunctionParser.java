package com.akjava.gwt.threejsmaker.client;

import com.akjava.lib.common.form.StaticValidators;

public class JSFunctionParser {

	public JSFunction parse(String line){
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
						return null;
					}
					JSFunction function=JSClassParser.parseFunction(functionName,names[1]);
					//System.out.println("add function:"+function);
					
					return function;
					
					
				}else{
					if(line.indexOf("var ")!=-1){
						//maybe inner function
					}else{
					System.out.println("no . function:"+line);
					}
				}
				return null;
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
						
						JSFunction function=JSClassParser.parseFunction(functionName,names[1]);
						if(!StaticValidators.asciiNumberAndCharAndUnderbarOnly().validate(functionName)){
							//invalid name
							return null;
						}
						if(!functionName.startsWith("_")){
							//not add inner function
							if(function==null){
								return null;//some invalid case
							}
							
						}
						
						return function;
					}else{
						System.out.println("have . function:"+line);
					}
					
				}
				//
				return null;
				//throw new RuntimeException("invalid class name:\n"+line+"\n\n"+text);
			}
		}
		return null;
	}
}

package com.akjava.gwt.threejsmaker.client;

import java.util.ArrayList;
import java.util.List;

public class JSFunction {

public JSFunction(String name){
	this.name=name;
}
private String name;
private String returnType;
public String getReturnType() {
	return returnType;
}
public void setReturnType(String returnType) {
	this.returnType = returnType;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public List<JSField> getArgs() {
	return args;
}
private List<JSField> args=new ArrayList<JSField>();
public void addArg(JSField arg){
	args.add(arg);
}

}

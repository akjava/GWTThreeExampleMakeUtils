package com.akjava.gwt.threejsmaker.client;

public class JSField {
	public JSField(String name){
		this.name=name;
	}	
	
public JSField(String name,String type){
	this.name=name;
	this.type=type;
}
private String name;
private String type=JSClass.TYPE_UNKNOWN;
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getType() {
	return type;
}
public void setType(String type) {
	this.type = type;
}
}

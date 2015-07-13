package com.akjava.gwt.threejsmaker.client;

import java.util.ArrayList;
import java.util.List;

public class JSClass {
	/*
	 * can't parse correctly with extras/curves/* & ShaderFlares?
	 */
	private boolean staticClass;
	private String packagePath;
	public String getPackagePath() {
		return packagePath;
	}
	public void setPackagePath(String packagePath) {
		this.packagePath = packagePath;
	}
	public boolean isStaticClass() {
		return staticClass;
	}
	public void setStaticClass(boolean staticMethod) {
		this.staticClass = staticMethod;
	}
	public JSClass(String name){
		this.name=name;
	}
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public static final String TYPE_UNKNOWN="unknown";
	List<JSFunction> functions=new ArrayList<JSFunction>();
	List<JSFunction> getters=new ArrayList<JSFunction>();
	public List<JSFunction> getGetters() {
		return getters;
	}
	public List<JSFunction> getSetters() {
		return setters;
	}
	List<JSFunction> setters=new ArrayList<JSFunction>();
	public List<JSFunction> getFunctions() {
		return functions;
	}
	public void addGetter(JSFunction function){
		getters.add(function);
	}
	public void addSetter(JSFunction function){
		setters.add(function);
	}
	public void addFunction(JSFunction function){
		functions.add(function);
	}
	List<JSField> fields=new ArrayList<JSField>();
	public List<JSField> getFields() {
		return fields;
	}
	public void addField(JSField field){
		fields.add(field);
	}
}

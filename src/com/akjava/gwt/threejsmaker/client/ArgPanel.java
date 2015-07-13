package com.akjava.gwt.threejsmaker.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueListBox;

public class ArgPanel extends HorizontalPanel {
private TextBox classBox;
private TextBox nameBox;
private ValueListBox<Arg> argList;
private CheckBox array;

public ArgPanel(){
	super();
	this.setVerticalAlignment(ALIGN_MIDDLE);
	add(new Label("Name"));
	nameBox = new TextBox();
	
	nameBox.setText("param");
	add(nameBox);
	classBox = new TextBox();
	classBox.setWidth("400px");
	classBox.setValue("java.lang.String");
	
	argList = new ValueListBox<Arg>(new Renderer<Arg>() {
		@Override
		public String render(Arg object) {
			// TODO Auto-generated method stub
			return object.getName();
		}

		@Override
		public void render(Arg object, Appendable appendable) throws IOException {
			// TODO Auto-generated method stub
		}
	});
	add(new Label("Type"));
	add(argList);
	
	array = new CheckBox("array");
	add(array);
	
	add(classBox);
	argList.addValueChangeHandler(new ValueChangeHandler<ArgPanel.Arg>() {
		@Override
		public void onValueChange(ValueChangeEvent<Arg> event) {
			classBox.setVisible(event.getValue().isClass());
		}
	});
	
	List<Arg> args=new ArrayList<ArgPanel.Arg>();
	args.add(new Arg("boolean",false,"Z"));
	args.add(new Arg("byte",false,"B"));
	args.add(new Arg("char",false,"C"));
	args.add(new Arg("short",false,"S"));
	args.add(new Arg("int",false,"I"));
	//args.add(new Arg("long",false,"J"));//not support use double
	//args.add(new Arg("float",false,"F"));//not support use double
	args.add(new Arg("double",false,"D"));
	args.add(new Arg("String",false,"Ljava/lang/String;"));
	args.add(new Arg("JavaScriptObject",false,"Lcom/google/gwt/core/client/JavaScriptObject;"));
	args.add(new Arg("JsArray",false,"Lcom/google/gwt/core/client/JsArray;"));
	args.add(new Arg("JsArrayNumber",false,"Lcom/google/gwt/core/client/JsArrayNumber;"));
	
	args.add(new Arg("Object",true,""));
	argList.setValue(args.get(7));
	classBox.setVisible(false);
	argList.setAcceptableValues(args);
	
}
public String getType(){
	Arg arg=argList.getValue();
	if(arg.isClass()){
		return classBox.getText();
	}else{
		return arg.getName();
	}
}
public String getArgName(){
	return nameBox.getText();
}
public String getSignature(){
	Arg arg=argList.getValue();
	String arrayText=array.getValue()?"[":"";
	
	if(arg.isClass()){
		return arrayText+"L"+classBox.getText().replace(".","/")+";";
	}else{
		return arrayText+arg.getValue();
	}
}

public static class Arg{
	private String name;
	private boolean clazz;
	private String value;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isClass() {
		return clazz;
	}
	public void setClass(boolean clazz) {
		this.clazz = clazz;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Arg(String name, boolean clazz, String value) {
		super();
		this.name = name;
		this.clazz = clazz;
		this.value = value;
	}
}

}

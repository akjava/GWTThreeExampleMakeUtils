package com.akjava.gwt.threejsmaker.client;


import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.lib.common.utils.ListUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class JSListenerFunctionMakePanel extends VerticalPanel{
private TextArea outputArea;
private TextBox packageAndName;
private TextBox methodName;
private List<ArgPanel> argList=new ArrayList<ArgPanel>();
private VerticalPanel argsPanel;
private TextBox javaMethod;
private TextBox jsMethod;
public JSListenerFunctionMakePanel(){
	super();
	this.setSize("100%", "300px");
	
	final TabLayoutPanel tab=new TabLayoutPanel(30, Unit.PX);
	tab.setSize("100%", "300px");
	tab.add(createInputPanel(),"input listener");
	tab.add(createOutputPanel(),"output text");
	this.add(tab);
	
	Button make=new Button("Make Listner",new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			doMake();
			tab.selectTab(1);
		}
	});
	this.add(make);
	
	test(new Test(){

		@Override
		public void onIt(String[] array) {
			for(String v:array){
				LogUtils.log(v);
			}
		}},new String[]{"hello","world"});
}

protected void doMake() {
	String result="";
	
	String shortName=null;
	int last=packageAndName.getText().lastIndexOf(".");
	if(last!=-1){
		shortName=packageAndName.getText().substring(last+1);
	}else{
		shortName=packageAndName.getText();
	}
	
	
	
	
	
	
	String methodArgs="";
	String paramSignatures="";
	String jsArgs="";
	String args="";
	for(ArgPanel arg:argList){
		boolean isArray=false;
		String signature=arg.getSignature();
		isArray=signature.startsWith("[");
		
		if(isArray){
			if(!methodArgs.isEmpty()){
				methodArgs+=",";
			}
			methodArgs+=arg.getType()+"[] "+arg.getArgName();
		}else{
			if(!jsArgs.isEmpty()){
				jsArgs+=",";
			}
			jsArgs+=arg.getArgName();
		}
		
		if(!args.isEmpty()){
			args+=",";
		}
		args+=arg.getArgName();
		paramSignatures+=signature;
	}
	
	String margs="";
	if(!methodArgs.isEmpty()){
		margs+=","+methodArgs;
	}
	result+=javaMethod.getText().replace("${listener}", shortName).replace("${args}", margs)+"/*-{\n";
	
	result+=jsMethod.getText()+"(\n";
	result+="function("+jsArgs+"){\n";
	
	//listener line
	result+="listener.@"+packageAndName.getText()+"::"+methodName.getText();
	result+="("+paramSignatures+")"+"("+args+");\n";
	
	result+="}\n";
	result+=");\n";
	result+="}-*/;\n";
	
	outputArea.setText(result);
}

private Panel createInputPanel() {
	VerticalPanel panel=new VerticalPanel();
	
	HorizontalPanel h0=new HorizontalPanel();
	panel.add(h0);
	h0.add(new Label("java-method"));
	javaMethod = new TextBox();
	javaMethod.setWidth("400px");
	javaMethod.setText("public final native void execute(${listener} listener${args})");
	h0.add(javaMethod);
	
	
	HorizontalPanel h3=new HorizontalPanel();
	panel.add(h3);
	h3.add(new Label("js-method"));
	jsMethod = new TextBox();
	jsMethod.setWidth("400px");
	jsMethod.setText("this.execute");
	h3.add(jsMethod);
	
	
	HorizontalPanel h1=new HorizontalPanel();
	panel.add(h1);
	h1.add(new Label("listener package+name"));
	packageAndName = new TextBox();
	packageAndName.setWidth("400px");
	packageAndName.setText("java.Dummy");
	h1.add(packageAndName);
	
	HorizontalPanel h2=new HorizontalPanel();
	h2.add(new Label("listener method-name"));
	methodName = new TextBox();
	methodName.setWidth("400px");
	methodName.setText("onIt");
	h2.add(methodName);
	panel.add(h2);
	
	Button addBt=new Button("Add",new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			argsPanel.add(createArgPanel());
			
		}
	});
	panel.add(addBt);
	argsPanel = new VerticalPanel();
	panel.add(argsPanel);
	
	argsPanel.add(createArgPanel());
	
	return panel;
}


public interface Test{
	public void onIt(String[] array);
}

public final native void test(Test listener,String[] vs)/*-{
$wnd.execute(
function(param2){
listener.@com.akjava.gwt.threejsmaker.client.JSListenerFunctionMakePanel$Test::onIt([Ljava/lang/String;)(vs);
}
);
}-*/;

private Panel createArgPanel(){
	final HorizontalPanel hpanel=new HorizontalPanel();
	
	final ArgPanel panel=new ArgPanel();
	//valuelist box to choose type
	argList.add(panel);
	
	
	Button up=new Button("up",new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			ListUtils.up(argList, panel);
			refreshArgPanel();
		}
	});
	hpanel.add(up);//remove first
	Button down=new Button("down",new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			ListUtils.down(argList, panel);
			refreshArgPanel();
		}
	});
	hpanel.add(down);//remove first
	Button remove=new Button("remove",new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			hpanel.removeFromParent();
			argList.remove(panel);
		}
	});
	
	hpanel.add(remove);//remove first
	
	panel.insert(hpanel, 0);
	
	
	
	return panel;
}

protected void refreshArgPanel() {
	argsPanel.clear();
	for(ArgPanel panel:argList){
		argsPanel.add(panel);
	}
}

private Panel createOutputPanel() {
	VerticalPanel panel=new VerticalPanel();
	HorizontalPanel h=new HorizontalPanel();
	panel.add(h);
	outputArea = new TextArea();
	outputArea.setReadOnly(true);
	outputArea.setSize("600px", "200px");
	panel.add(outputArea);
	
	Button selectAll=new Button("select all",new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			outputArea.selectAll();
		}
	});
	h.add(selectAll);
	
	
	return panel;
}


}

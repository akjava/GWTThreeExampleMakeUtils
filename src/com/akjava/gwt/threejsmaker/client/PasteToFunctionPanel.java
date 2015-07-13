package com.akjava.gwt.threejsmaker.client;

import com.akjava.gwt.lib.client.widget.PasteValueReceiveArea;
import com.akjava.gwt.threejsmaker.client.JSConverter.JSFunctionToTextFunction;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PasteToFunctionPanel extends VerticalPanel{

	private String lastLine="";
	private boolean hasReturn;
	private TextArea output;
	public PasteToFunctionPanel(){
		HorizontalPanel h1=new HorizontalPanel();
		add(h1);
		PasteValueReceiveArea input=new PasteValueReceiveArea();
		h1.add(input);
		input.setText("Paste a line[that.test = function( object )] here to generate java function");
		input.setSize("600px", "20px");
		
		CheckBox check=new CheckBox("has return");
		check.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				hasReturn=event.getValue();
				doConvert();
			}
		});
		h1.add(check);
		
		output = new TextArea();
		output.setSize("800px", "50px");
		add(output);
		input.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				lastLine=event.getValue();
				doConvert();
			}
		});
	}
	public void doConvert(){
		JSClass clazz=new JSClass("test");
		
		JSFunctionToTextFunction converter=new JSFunctionToTextFunction(clazz);
		JSFunction function=new JSFunctionParser().parse(lastLine);

		if(function!=null){
			if(hasReturn){
				function.setReturnType("Object");
			}
			String text=converter.apply(function);
			output.setText(text);
		}
	}
}

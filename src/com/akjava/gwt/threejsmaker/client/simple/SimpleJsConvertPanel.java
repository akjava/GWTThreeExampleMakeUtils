package com.akjava.gwt.threejsmaker.client.simple;

import java.util.List;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.threejsmaker.client.resources.Bundles;
import com.akjava.lib.common.utils.CSVUtils;
import com.akjava.lib.common.utils.TemplateUtils;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SimpleJsConvertPanel extends VerticalPanel{

	private TextArea output;
	private TextArea input;

	public SimpleJsConvertPanel(){
		add(new Label("Input"));
		input = new TextArea();
		input.setSize("800px", "300px");
		add(input);
		
		HorizontalPanel modes=new HorizontalPanel();
		add(modes);
		
		ValueChangeHandler<Boolean> handler=new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				
				if(event.getValue()){
					RadioButton bt=((RadioButton)event.getSource());
					
					onModeChange(bt.getText());
				}
			}
		};
		List<String> labels=Lists.newArrayList("wrap","wrapreturn","get","set","getset","jsclass");

		RadioButton firstOne=null;
		for(String label:labels){
			RadioButton radioBt=new RadioButton("mode", label);
			modes.add(radioBt);
			radioBt.addValueChangeHandler(handler);
			if(firstOne==null){
				firstOne=radioBt;
			}
		}
		
		Button convertBt=new Button("Convert",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doConvert();
			}
		});
		add(convertBt);
		
		firstOne.setValue(true);
		
		add(new Label("Output"));
		output = new TextArea();
		output.setSize("800px", "300px");
		add(output);
	}

	protected void doConvert() {
		String inputText=input.getText();
		if(inputText.isEmpty()){
			output.setText("");
			return;
		}
		
		List<String> lines=CSVUtils.splitLinesWithGuava(inputText);
		
		String template=null;
		if(mode.equals("wrap")){
			template=Bundles.INSTANCE.wrap().getText();
		}else if(mode.equals("wrapreturn")){
			template=Bundles.INSTANCE.wrapreturn().getText();
		}else if(mode.equals("get")){
			template=Bundles.INSTANCE.get().getText();
		}else if(mode.equals("set")){
			template=Bundles.INSTANCE.set().getText();
		}else if(mode.equals("getset")){
			template=Bundles.INSTANCE.getset().getText();
		}else if(mode.equals("jsclass")){
			template=Bundles.INSTANCE.jsclass().getText();
		}
		List<String> results=Lists.newArrayList();
		for(String line:lines){
			String result=TemplateUtils.createAdvancedText(template, line);
			results.add(result);
		}
		
		output.setText(Joiner.on("\n").join(results));
		
	}

	private String mode="wrap";
	
	protected void onModeChange(String name) {
		mode=name;
	}
}

package com.akjava.gwt.threejsmaker.client.oneline;

import java.util.List;
import java.util.Set;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.widget.PasteValueReceiveArea;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.akjava.lib.common.form.StaticValidators;
import com.akjava.lib.common.utils.StringUtils;
import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

public class OneLineConvertPanel extends DockLayoutPanel{

	
	final MultiSelectionModel<OneLineConverter> selectionModel = new MultiSelectionModel<OneLineConverter>();
	
	private List<OneLineConverter> datas;

	private SimpleCellTable<OneLineConverter> converterTable;

	private TextArea output;

	private PasteValueReceiveArea pasteArea;
	public List<OneLineConverter> getDatas() {
		return datas;
	}
	public void setDatas(List<OneLineConverter> datas) {
		this.datas = datas;
		converterTable.setData(datas);
	}
	public OneLineConvertPanel() {
		super(Unit.PX);
		
		VerticalPanel converters=new VerticalPanel();
		addWest(converters,300);
		
		//left list
		converterTable = new SimpleCellTable<OneLineConverter>() {
			@Override
			public void addColumns(CellTable<OneLineConverter> table) {
				Column<OneLineConverter, Boolean> checkColumn = new Column<OneLineConverter, Boolean>(
					    new CheckboxCell(true,false)) {
					  @Override
					  public Boolean getValue(OneLineConverter object) {
					    // Get the value from the selection model.
					    return selectionModel.isSelected(object);
					  }
					};
					table.addColumn(checkColumn);
					
				TextColumn<OneLineConverter> nameColumn=new TextColumn<OneLineConverter>() {

					@Override
					public String getValue(OneLineConverter object) {
						return object.getName();
					}
				};
				
				table.addColumn(nameColumn);
				
			}
		};
		converterTable.getPager().setVisible(false);
		
		//handle checkbox
		converterTable.getCellTable().setSelectionModel(selectionModel,
		        DefaultSelectionEventManager.<OneLineConverter> createCheckboxManager());
		
		HorizontalPanel buttons=new HorizontalPanel();
		converters.add(buttons);
		converters.add(converterTable);
		
		Button selectAll=new Button("SelectAll",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				selectAll();
				//TODO need fire event?
			}
		});
		buttons.add(selectAll);
		
		Button unselectAll=new Button("UnselectAll",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(OneLineConverter data:datas){
					selectionModel.setSelected(data, false);
				}
			}
		});
		buttons.add(unselectAll);
		
		//
		List<OneLineConverter> datas=Lists.newArrayList();
		
		
		datas.add(new SpecialReplaceConverter());
		datas.add(new VarToClassConverter());
		datas.add(new GetSetFieldConverter());
		datas.add(new JsParamConverter());
		datas.add(new CommentOutResizeListenerConverter());
		
		SetArrayDataConverter arrayConverter=new SetArrayDataConverter(false);
		datas.add(arrayConverter);
		SetArrayDataConverter arrayConverter2=new SetArrayDataConverter(true);
		datas.add(arrayConverter2);
		
		datas.add(new ArrayToNativeConverter());
		
		setDatas(datas);
		
		addSelectionListener(new SelectionListener() {
			
			@Override
			public void select(Set<OneLineConverter> selections) {
				updateOutput();
				
				converterTable.setData(OneLineConvertPanel.this.datas);
				for(OneLineConverter selection:selections){
					LogUtils.log(selection.getName());
				}
				//how to handle order? 
			}
		});
		
		//input
		VerticalPanel center=new VerticalPanel();
		add(center);
		
		center.add(new Label("[Input]"));
		pasteArea = new PasteValueReceiveArea();
		pasteArea.setText("paste here");
		center.add(pasteArea);
		pasteArea.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				pasteText=event.getValue();
				updateOutput();
			}
		});
		center.add(new Label("[Output]"));
		output = new TextArea();
		output.setSize("600px","200px");
		center.add(output);
		
		
		selectAll();
		selectionModel.setSelected(arrayConverter, false);//no need
		selectionModel.setSelected(arrayConverter2, false);//no need
	}
	protected void selectAll() {
		for(OneLineConverter data:datas){
			selectionModel.setSelected(data, true);
		}
	}
	private String pasteText="";
	private void updateOutput(){
		String inputText=pasteText;
		List<String> lines=ValuesUtils.toListLines(inputText);
		
		//default comment out
		lines=FluentIterable.from(lines).transform(new TrimConverter()).toList();//trim first
		
		List<OneLineConverter> converters=getSelection();
		
		
		
		List<String> commentsOut=FluentIterable.from(lines).transform(new ComentOutConverter()).toList();
		//convert
		for(OneLineConverter converter:converters){
			lines=FluentIterable.from(lines).transform(converter).toList();
		}
		
		List<String> finalLines=Lists.newArrayList();
		for(int i=0;i<lines.size();i++){
			String commentout=commentsOut.get(i);
			String converted=lines.get(i);
			if(("//"+converted).equals(commentout)){
				finalLines.add(converted);
			}else{
				finalLines.add(converted+commentout);
			}
			
		}
		
		String converted=Joiner.on("\n").join(finalLines);
		output.setText(converted);
	}
	
	public void addSelectionListener(final SelectionListener listener){
		selectionModel.addSelectionChangeHandler(new Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				
				listener.select(selectionModel.getSelectedSet());
			}
		});
	}
	
	private List<OneLineConverter> getSelection(){
		List<OneLineConverter> selections=Lists.newArrayList();
		for(OneLineConverter data:datas){
			if(selectionModel.isSelected(data)){
				selections.add(data);
			}
		}
		return selections;
	}
	
	public interface SelectionListener{
		public void select(Set<OneLineConverter> selections);
	}
	
	public class VarToClassConverter extends OneLineConverter{
		RegExp regExp = RegExp.compile("new THREE\\.(.*?)\\(");
		@Override
		public String apply(String input) {
			if(input.isEmpty()){
				return "";
			}

			String line=input;
			boolean matchFound=false;
			String firstType=null;
			do{
			MatchResult matcher = regExp.exec(line);
			matchFound = matcher != null;
			if(matchFound){
				 //0 is special
				 for (int i = 0; i < matcher.getGroupCount(); i++) {
				       // String groupStr = matcher.getGroup(i);
				        //LogUtils.log(i+","+groupStr);
				    }
				 List<List<String>> lists=Lists.newArrayList();
				 lists.add(Lists.newArrayList(matcher.getGroup(0), "THREE."+matcher.getGroup(1)+"("));//DO fore future?
				 line=StringUtils.replaceStrings(line,lists);
				 
				 if(firstType==null){
					 firstType=matcher.getGroup(1);
				 }
				//line= line.replaceAll(matcher.getGroup(0), "THREE."+matcher.getGroup(1)+"(");
			}
			}while(matchFound);
			
			
			//do it while mutching!
			
			if(line.startsWith("var ")){
				if(firstType!=null){//something THREE
					line=firstType+" "+line.substring(4);//after "var "
				}else{
					//try auto detect type
					int index=line.indexOf("=");
					if(index!=-1){
						for(String type:detectType(line.substring(index+1)).asSet()){
							line=type+" "+line.substring(4);//after "var "
						};
					}
				}
			}
			
			
			
			return line;
		}

		@Override
		public String getName() {
			return "convert ver type & new THREE.";
		}
	}
	
	public Optional<String> detectType(String line){
		if(line.endsWith(";")){
			line=line.substring(0,line.length()-1);//chop
		}
		line=line.trim();
		
		if(line.equals("true") || line.equals("false")){
			return Optional.of("boolean");
		}
		
		if(line.startsWith("'") && line.endsWith("'")){
			return Optional.of("String");
		}
		if(line.startsWith("\"") && line.endsWith("\"")){
			return Optional.of("String");
		}
		
		//isDigit?
		if(StaticValidators.decimalNumber().validate(line)){
			return Optional.of("double");//any number handle as double,on default
		}
		
		if(line.startsWith("[") && line.endsWith("]")){
			return Optional.of("JsArray<JavaScriptObject>");//ignore possible of number,string
		}
		//TODO convert param type { }
		
		return Optional.absent();
	}

	public static class GetSetFieldConverter extends OneLineConverter{
		RegExp regExp = RegExp.compile("\\.([a-z_][A-Za-z0-9_]*?)\\.");
		RegExp regExp2 = RegExp.compile("\\.([a-z_][A-Za-z0-9_]*?)\\s*\\)");

		
		@Override
		public String apply(String input) {
			if(input.isEmpty()){
				return "";
			}

			//replace getter
			String line=input;
			boolean matchFound=false;
			do{
			MatchResult matcher = regExp.exec(line);
			matchFound = matcher != null;
			if(matchFound){
				 //0 is special
				 for (int i = 0; i < matcher.getGroupCount(); i++) {
				       // String groupStr = matcher.getGroup(i);
				        //LogUtils.log(i+","+groupStr);
				    }
				 
				 //if start upper case maybe that is static
				 if(!Character.isUpperCase(matcher.getGroup(1).charAt(0))){
				 String method="get"+ValuesUtils.toUpperCamel(matcher.getGroup(1))+"()";
				 line=line.replace(matcher.getGroup(0), "."+method+".");
				 }
				 
				 
				//line= line.replaceAll(matcher.getGroup(0), "THREE."+matcher.getGroup(1)+"(");
			}
			}while(matchFound);
			
			
			//special
			boolean matchFound2=false;
			do{
			MatchResult matcher = regExp2.exec(line);
			matchFound = matcher != null;
			if(matchFound){
				
				
				 if(!Character.isUpperCase(matcher.getGroup(1).charAt(0))){
				 String method="get"+ValuesUtils.toUpperCamel(matcher.getGroup(1))+"()";
				 line=line.replace(matcher.getGroup(0), "."+method+")");
				 }
			}
			}while(matchFound2);
			
			
			
			int eq=line.indexOf("=");
			if(eq!=-1){//TODO multiple equal
				String before=line.substring(0,eq);
				before=before.trim();
				
				
				int point=before.lastIndexOf(".");
				if(point!=-1){
				String field=before.substring(point+1);
				if(StaticValidators.asciiNumberAndCharAndUnderbarOnly().validate(field)){
					String after=line.substring(eq+1);
					after=after.trim();
					String ends="";
					if(after.endsWith(";")){
						after=ValuesUtils.chomp(after);
						ends=";";
					}
					
					//after get
					int lastFieldIndex=after.lastIndexOf(".");
					if(lastFieldIndex!=-1){
					String lastField=after.substring(lastFieldIndex+1);
						if(StaticValidators.asciiNumberAndCharAndUnderbarOnly().validate(lastField) && StaticValidators.asciiCharOnly().validate(lastField.substring(0,1))){//first one is not number
							
							if(!Character.isUpperCase(lastField.charAt(0))){
							after=after.substring(0,lastFieldIndex)+"."+"get"+ValuesUtils.toUpperCamel(lastField)+"()";
							}
						}
					}
					
					//need setter
					line=before.substring(0,point)+"."+"set"+ValuesUtils.toUpperCamel(field)+"("+after+")"+ends;
				}
				}
			}
			
			
			return line;
		}

		@Override
		public String getName() {
			return "get & set field";
		}
	}
	
	public static class JsParamConverter extends OneLineConverter{
		
		RegExp regExp = RegExp.compile("{(.*?)}");
		
		
		
		@Override
		public String apply(String input) {
			if(input.isEmpty()){
				return "";
			}
			
			input=input.trim();
			
			input=input.replace("{}", "JSParameter.createParameter()");//empty is new-unknown-data
			
			
			//do 1 time
			MatchResult matcher = regExp.exec(input);
			if(matcher!=null){
				String params=matcher.getGroup(0);
				int index=input.indexOf(params);
				String beforeLine=input.substring(0,index);
				String afterLine=input.substring(index+params.length());
				String method=jsToJava(matcher.getGroup(1));
				
				String threeName="TODO";
				RegExp threeregExp = RegExp.compile("THREE\\.(.*?)\\(","g");
				MatchResult matcher2 = threeregExp.exec(beforeLine);
				while(matcher2!=null){
					threeName=matcher2.getGroup(1);
					matcher2 = threeregExp.exec(beforeLine);//do global-for get last one
				}
				
				String replacement="GWTParamUtils."+threeName+"()."+method;
				
				//TODO handle first var
				return beforeLine+input.replace(input, replacement)+afterLine;
				
			}else{
				return input;
			}
			
		}
		
		private String jsToJava(String params){
			List<String> result=Lists.newArrayList();
			String[] ps=params.trim().split(",");
			Splitter splitter=Splitter.on(':').trimResults().limit(2);
			
			for(String v:ps){
				if(v.isEmpty()){
					continue;
				}
				List<String> name_value=splitter.splitToList(v);
				
				
				
				if(name_value.size()>1){
					String value=name_value.get(1);
					//convert js string to java string
					if(value.startsWith("'") && value.endsWith("'")){
						value="\""+value.substring(0,value.length()-1)+"\"";//possible contain inside,replace start and end only
					}
					result.add(name_value.get(0)+"("+value+")");	
				}else{
					LogUtils.log("invalid:"+v);
				}
				
			}
			return Joiner.on(".").join(result);
		}

		@Override
		public String getName() {
			return "comment out original code";
		}
	}
	
	public static class ComentOutConverter extends OneLineConverter{
		@Override
		public String apply(String input) {
			if(input.isEmpty()){
				return "";
			}
			return "//"+input;
		}

		@Override
		public String getName() {
			return "conver js-param{} to method-style";
		}
	}
	
	public class TrimConverter extends OneLineConverter{
		@Override
		public String apply(String input) {
			if(input.isEmpty()){
				return "";
			}
			return input.trim();
		}

		@Override
		public String getName() {
			return "trim";
		}
	}
	
	public static class SpecialReplaceConverter extends OneLineConverter{
		@Override
		public String apply(String input) {
			if(input.isEmpty()){
				return "";
			}
			
			List<List<String>> replaces=Lists.newArrayList();
			replaces.add(ImmutableList.of("window.innerWidth","getWindowInnerWidth()"));
			replaces.add(ImmutableList.of("window.innerHeight","getWindowInnerHeight()"));
			replaces.add(ImmutableList.of("window.devicePixelRatio","GWTThreeUtils.getWindowDevicePixelRatio()"));
					
			
			replaces.add(ImmutableList.of("THREE.ImageUtils.","ImageUtils."));
			replaces.add(ImmutableList.of("THREE.Math.","THREEMath."));
			
			return StringUtils.replaceStrings(input, replaces);
		}

		@Override
		public String getName() {
			return "replace for GWT-three.js-example";
		}
	}
	
	public static class SetArrayDataConverter extends OneLineConverter{
		public SetArrayDataConverter(boolean castFloat){
			this.castFloat=castFloat;
		}
		RegExp regExp = RegExp.compile("(.*)\\[(.*?)\\].*=(.*);");
		
		private boolean castFloat;
		
		@Override
		public String apply(String input) {
			if(input.isEmpty()){
				return "";
			}
		
input=input.trim();
			
			input=input.replace("{}", "JSParameter.createParameter()");//empty is new-unknown-data
			
			
			//do 1 time
			MatchResult matcher = regExp.exec(input);
			if(matcher!=null){
				String name=matcher.getGroup(1).trim();
				String index=matcher.getGroup(2).trim();
				String value=matcher.getGroup(3).trim();
				String castText=castFloat?"(float)":"";
				return name+".set("+index+","+castText+value+");";
				
			}else{
				return input;
			}
			
		}
			

		@Override
		public String getName() {
			return "set value to array"+(castFloat?"(float)":"");
		}
	}
	
	//only resize comment out,my demo handler it super class
	public static class CommentOutResizeListenerConverter extends OneLineConverter{
		@Override
		public String apply(String input) {
			if(input.isEmpty()){
				return "";
			}
			if(input.indexOf("addEventListener")!=-1 && input.indexOf("resize")!=-1){
				return "//"+input;
			}
			return input;
		}

		@Override
		public String getName() {
			return "comment out window.addEventListener( 'resize', onWindowResize, false )";
		}
	}
	
	public static class ArrayToNativeConverter extends OneLineConverter{
		private static String[] keys={"Uint8","Uint16","Float16","Float32"};
		@Override
		public String apply(String input) {
			if(input.isEmpty()){
				return "";
			}
			
			for(String key:keys){
				String check=key+"Array";
				int index=input.indexOf(check);
				if(index!=-1){
					
					String before=input.substring(0,index).trim();
					if(before.endsWith("new")){//much
						String after=input.substring(index+check.length());
						String newType=check+"Native";
						
						before=before.substring(0,before.length()-3);//remove 'new'
						
						if(before.startsWith("var")){
							before=newType+before.substring(3);
						}
						return before+newType+".create"+after;
					}
				}
			}
			
			
			return input;
		}

		@Override
		public String getName() {
			return "replace create Uint16Array to Uint16ArrayNative";
		}
	}
}

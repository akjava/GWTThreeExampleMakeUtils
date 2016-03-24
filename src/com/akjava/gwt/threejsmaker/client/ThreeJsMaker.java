package com.akjava.gwt.threejsmaker.client;

import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileHandler;
import com.akjava.gwt.html5.client.file.FileReader;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.ui.DropVerticalPanelBase;
import com.akjava.gwt.html5.client.file.ui.FileNameAndText;
import com.akjava.gwt.html5.client.file.ui.FileNameAndTextCell;
import com.akjava.gwt.html5.client.file.webkit.DirectoryCallback;
import com.akjava.gwt.html5.client.file.webkit.FileEntry;
import com.akjava.gwt.html5.client.file.webkit.FilePathCallback;
import com.akjava.gwt.html5.client.file.webkit.Item;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.widget.PasteValueReceiveArea;
import com.akjava.gwt.threejsmaker.client.oneline.OneLineConvertPanel;
import com.akjava.gwt.threejsmaker.client.resources.Bundles;
import com.google.common.base.Function;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */

public class ThreeJsMaker implements EntryPoint {
	private CellList<FileNameAndText> cellList;
	private TextArea text;
	private VerticalPanel flist;

	List<FileNameAndText> files=new ArrayList<FileNameAndText>();
	private SingleSelectionModel<FileNameAndText> selectionModel;
	
	public void onModuleLoad() {
		DropVerticalPanelBase dropBase=new DropVerticalPanelBase();
		dropBase.addDropHandler(new DropHandler() {
			@Override
			public void onDrop(DropEvent event) {
				event.preventDefault();
				uploadFiles(event);
				
			}
		});
		dropBase.addDragOverHandler(new DragOverHandler() {
			@Override
			public void onDragOver(DragOverEvent event) {

				event.preventDefault();

			}
		});
		dropBase.setSize("100%", "100%");
		//RootPanel.get().add(dropBase);
		
		TabLayoutPanel rootTab=new TabLayoutPanel(28, Unit.PX);
		RootLayoutPanel.get().add(rootTab);
		//dropBase.add(rootTab);
		
		
		OneLineConvertPanel onelinePanel=new OneLineConvertPanel();
		rootTab.add(onelinePanel,"oneline");
		//onelinePanel.setSize("400px", "400px");
		
		
		
		HorizontalPanel dropCodeConvertPanel=new HorizontalPanel();
		dropBase.add(dropCodeConvertPanel);//wrapperd drop
		rootTab.add(dropBase,"convert drop code");
		rootTab.selectTab(0);
		
		flist = new VerticalPanel();
		text = new TextArea();
		text.setSize("800px", "480px");
		dropCodeConvertPanel.add(flist);
		
		VerticalPanel center=new VerticalPanel();
		dropCodeConvertPanel.add(center);
		center.add(text);
		
		ScrollPanel scroll=new ScrollPanel();
		 scroll.setSize("300px", "800px");
		
		 FileNameAndTextCell cell=new FileNameAndTextCell();
		 cellList = new CellList<FileNameAndText>(cell);
		 cellList.setPageSize(100);
		 scroll.setWidget(cellList);
		 flist.add(scroll);
		 
		 selectionModel = new SingleSelectionModel<FileNameAndText>();
		 cellList.setSelectionModel(selectionModel);
		 selectionModel.addSelectionChangeHandler(new Handler() {
				@Override
				public void onSelectionChange(SelectionChangeEvent event) {
					FileNameAndText select=selectionModel.getSelectedObject();
					if(select!=null){
					text.setText(select.getText());
					//downloadLinks.clear();
					//downloadLinks.add(select.createDownloadLink("Download"));
					}else{
						text.setText("");
						//downloadLinks.clear();
					}
				}
			});
		 flist.add(new Button("Clear All",new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					files.clear();
					updateList();
				}
			}));
		 
		 PasteValueReceiveArea pasteHere=new PasteValueReceiveArea();
		 pasteHere.setText("Paste Here");
		 pasteHere.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				FileNameAndText ftext=new FileNameAndText("commoon.js","");
				files.add(ftext);
				
				convertText(event.getValue(), ftext);
				
				updateList();
				
			}
		});
		 flist.add(pasteHere);
		 
		 flist.add(new Button("Download",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				doDownload();
			}
		}));
		
		 
		 
		 
		 
		//try download
		
		 JSListenerFunctionMakePanel functionMaker=new JSListenerFunctionMakePanel();
		 center.add(functionMaker);
		 PasteToFunctionPanel pasteMaker=new PasteToFunctionPanel();
		 center.add(pasteMaker);
		
	}
	
	protected void doDownload() {
		try {
			//List<String> names=loadNames();
			//List<FileNameAndText> files=Lists.transform(names, new NameToFileNamesFunction());
			
			
			//flist.clear();
			FormPanel form=new FormPanel();
			
			form.setAction("/tozip");
			form.setMethod(FormPanel.METHOD_POST);
			VerticalPanel container=new VerticalPanel();
			form.add(container);
			container.add(new Hidden("filenumber", ""+files.size()));
			for(int i=0;i<files.size();i++){
				int number=i+1;
				FileNameAndText file=files.get(i);
				container.add(new Hidden("path"+number, file.getName()));
				container.add(new Hidden("text"+number, file.getText()));
				
			}
			
			form.submit();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void entryCallback(final FileEntry entry,final FilePathCallback callback,String path){
		if (entry.isFile()) {
			entry.file(callback,path);
			//GWT.log("path:"+path);
		} else if (entry.isDirectory()) {
			entry.getReader().readEntries(
					new DirectoryCallback() {
						@Override
						public void callback(
								JsArray<FileEntry> entries) {
							callback.callback(null, entry.getFullPath());
							for (int j = 0; j < entries
									.length(); j++) {
								entryCallback(entries.get(j),callback,entry.getFullPath());
							}
						}
					});
		}
	}
	/*
	public class NameToFileNamesFunction implements Function<String,FileNameAndText>{

		@Override
		public FileNameAndText apply(String name) {
			String js=getText(name);
			String packageName=getPackage(name);
			JSClass clazz=JSClassParser.parseJSClass(js, packageName);
			String java=JSConverter.convertCode(Bundles.INSTANCE.classbase().getText(), clazz);
			return new FileNameAndText("src/com/akjava/gwt/trhee/"+name.replace(".js", ".java"), java);
		}
		
		
	}*/
	

	private String classBase=Bundles.INSTANCE.classbase().getText();

	private void loadFile(File file,final FileNameAndText ftext){
		final FileReader reader = FileReader.createFileReader();
		reader.setOnLoad(new FileHandler() {
			@Override
			public void onLoad() {
				
				String text = "";

				text = reader.getResultAsString();

				String packageName=getPackage(ftext.getName());
				//String packageName="dummy";
				JSClass jsClass=JSClassParser.parseJSClass(text, packageName);
				
				ftext.setText(JSConverter.convertCode(classBase, jsClass));

				updateList();
			}
		});
		reader.readAsText(file, "UTF-8");
		
	}
	
	//for manual convert
	private void convertText(String input,FileNameAndText ftext){
		String packageName=getPackage(ftext.getName());
		//String packageName="dummy";
		JSClass jsClass=JSClassParser.parseJSClass(input, packageName);
		
		ftext.setText(JSConverter.convertCode(classBase, jsClass));
	}
	
	protected void updateList() {
		cellList.setRowData(files);
	}

	protected void uploadFiles(DropEvent event) {
		files.clear();
		final FilePathCallback callback = new FilePathCallback() {
			@Override
			public void callback(File file,String path) {
				if(file==null){
					GWT.log("directory:"+path);
					//strangely debug mode file has some value.
					return;
				}
				//GWT.log(file.getFileName()+","+path+","+file.getFileType());
				if(path.startsWith("/")){
					path=path.substring(1);
				}
				FileNameAndText ftext=new FileNameAndText(path+"/"+file.getFileName().replace(".js", ".java"),"");
				files.add(ftext);
				
				loadFile(file,ftext);
			}
		};

		final JsArray<Item> items = FileUtils.transferToItem(event
				.getNativeEvent());
		if (items.length() > 0) {
			for (int i = 0; i < items.length(); i++) {
				
				FileEntry entry = items.get(i).webkitGetAsEntry();

				entryCallback(entry,callback,"");

			}

		}
	}

	/*
	protected List<String> loadNames() throws Exception {
		List<String> fileNames=new ArrayList<String>();
		
			String text = Bundles.INSTANCE.filenames().getText();
			List<String> lines=ValuesUtils.toListLines(text);
			List<String> names=Lists.transform(lines, new NameFunction());
			for(String name:names){
				if(name==null){
					continue;
				}
				fileNames.add(name);
			}
		
		return fileNames;
	}*/
	
	public String getPackage(String path){
		if(path.startsWith("/")){
			path=path.substring(1);
		}
		
		if(path.startsWith("src/")){
			path=path.substring(4);
		}
		
		
		int end=path.lastIndexOf("/");
		if(end==-1){
			return path;
		}
		return path.substring(0,end);
	}
	
	public static String base="resources/src/";

	public class NameFunction implements Function<String,String>{

		@Override
		public String apply(String value) {
			String name_size[]=value.split(",");
			
			if(name_size[0].endsWith("/")){
				return null;
			}
			if(name_size[0].startsWith("/")){
				
				return name_size[0].substring(1);
			}
			return null;
		}
		
	}
}

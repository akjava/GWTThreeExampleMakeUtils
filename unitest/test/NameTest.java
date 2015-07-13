package test;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.akjava.gwt.threejsmaker.client.JSClass;
import com.akjava.gwt.threejsmaker.client.JSClassParser;
import com.akjava.lib.common.utils.FileNames;
import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;

public class NameTest extends TestCase{
	public static String base="resources/src/";
	List<String> names=new ArrayList<String>();
	@Override
	protected void setUp() throws Exception {
		URL url = Resources.getResource("resources/filenames.txt");
		try {
			String text = Resources.toString(url, Charsets.UTF_8);
			List<String> lines=ValuesUtils.toListLines(text);
			List<String> names=Lists.transform(lines, new NameFunction());
			for(String name:names){
				if(name==null){
					continue;
				}
				this.names.add(name);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	public static String getText(String name){
		URL url = Resources.getResource(base+name);
		
			try {
				String text = Resources.toString(url, Charsets.UTF_8);
				return text;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			return null;
	}


	public void testName(){
		for(String name:names){
			int index=name.lastIndexOf("/");
			if(index==-1){
				//three.js ignore it
			}else{
				String jsName=name.substring(index+1);
				String packageName=name.substring(0,index);
				jsName=FileNames.getRemovedExtensionName(jsName);
				String text=getText(name);
				
				JSClass clazz=JSClassParser.parseJSClass(text,packageName);
				if(!jsName.equals(clazz.getName())){
					assertEquals(jsName, clazz.getName());
				}
				
					
			}
		}
		assertTrue(true);
	}
	public void testSatic(){
		List<String> statics=new ArrayList<String>();
		for(String name:names){
			int index=name.lastIndexOf("/");
			if(index==-1){
				//three.js ignore it
			}else{
				String jsName=name.substring(index+1);
				String packageName=name.substring(0,index);
				jsName=FileNames.getRemovedExtensionName(jsName);
				String text=getText(name);
				//System.out.println(jsName);
				JSClass clazz=JSClassParser.parseJSClass(text,packageName);
				if(clazz.isStaticClass()){
					statics.add(clazz.getName());
				}
				
				
				
				
			}
		}
		try {
			URL url = Resources.getResource("resources/statics.txt");
			String staticfiles = Resources.toString(url, Charsets.UTF_8);
			assertEquals(staticfiles, Joiner.on("\r\n").join(statics));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(true);
	}
	
	
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

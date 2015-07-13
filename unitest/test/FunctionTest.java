package test;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import junit.framework.TestCase;

import com.akjava.gwt.threejsmaker.client.JSClass;
import com.akjava.gwt.threejsmaker.client.JSClassParser;
import com.akjava.gwt.threejsmaker.client.JSFunction;
import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;


public class FunctionTest extends TestCase {

	public void testCamera(){
		doTest("resources/camera_func.txt","resources/src/cameras/Camera.js");	
	}
	public void testBufferGeometry(){
		doTest("resources/buffergeometry_func.txt","resources/src/core/BufferGeometry.js");	
	}
	public void testPath(){
		doTest("resources/buffergeometry_func.txt","resources/src/extras/core/Path.js");	
	}
	
	public void doTest(String correctResource,String readJs){
		String camera=readText(readJs);
		JSClass clazz=JSClassParser.parseJSClass(camera, "cameras");
		
		List<String> functionNames=Lists.transform(clazz.getFunctions(), new JSFunctionToNameFunction());
		
		String correct=readText(correctResource);
		assertEquals(correct, Joiner.on("\r\n").join(functionNames));	
	}
	
	public class JSFunctionToNameFunction implements Function<JSFunction,String>{

		@Override
		public String apply(JSFunction func) {
			if(func==null){
				return "null-function";
			}
			return func.getName();
		}
		
	}
public String readText(String path){
	try {
		URL url = Resources.getResource(path);
		String text = Resources.toString(url, Charsets.UTF_8);
		return text;
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
}
}

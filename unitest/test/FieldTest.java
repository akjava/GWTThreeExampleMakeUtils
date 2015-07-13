package test;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import test.FunctionTest.JSFunctionToNameFunction;

import com.akjava.gwt.threejsmaker.client.JSClass;
import com.akjava.gwt.threejsmaker.client.JSClassParser;
import com.akjava.gwt.threejsmaker.client.JSField;
import com.akjava.gwt.threejsmaker.client.JSFunction;
import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;

import junit.framework.TestCase;


public class FieldTest extends TestCase {
	public void testCamera(){
		doTest("resources/camera_field.txt","resources/src/cameras/Camera.js");	
	}
	public void testBufferGeometry(){
		doTest("resources/buffergeometry_field.txt","resources/src/core/BufferGeometry.js");	
	}
	public void testRenderableFace3(){
		doTest("resources/renderableface3_field.txt","resources/src/renderers/renderables/RenderableFace3.js");	
	}
	public void testObject3D(){
		doTest("resources/object3d_field.txt","resources/src/core/Object3D.js");	
	}
	
	public void doTest(String correctResource,String readJs){
		String camera=readText(readJs);
		JSClass clazz=JSClassParser.parseJSClass(camera, "cameras");
		List<String> functionNames=Lists.transform(clazz.getFields(), new JSFieldToNameFunction());
		
		String correct=readText(correctResource);
		assertEquals(correct, Joiner.on("\r\n").join(functionNames));	
	}
	
	public class JSFieldToNameFunction implements Function<JSField,String>{

		@Override
		public String apply(JSField func) {
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

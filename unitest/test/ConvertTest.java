package test;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import test.FunctionTest.JSFunctionToNameFunction;
import test.NameTest.NameFunction;

import com.akjava.gwt.threejsmaker.client.JSClass;
import com.akjava.gwt.threejsmaker.client.JSClassParser;
import com.akjava.gwt.threejsmaker.client.JSConverter;
import com.akjava.gwt.threejsmaker.client.JSField;
import com.akjava.gwt.threejsmaker.client.JSFunction;
import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;

import junit.framework.TestCase;


public class ConvertTest extends TestCase {
	String codeBase;
	@Override
	protected void setUp() throws Exception {
		URL url = Resources.getResource("com/akjava/gwt/threejsmaker/client/resources/classbase.txt");
		try {
			codeBase  = Resources.toString(url, Charsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	public void testRenderableface3(){
		doTest("resources/renderableface3_java.txt","resources/src/renderers/renderables/RenderableFace3.js");	
	}
	public void testCamera(){
		doTest("resources/camera_java.txt","resources/src/cameras/Camera.js");	
	}
	public void testBufferGeometry(){
		doTest("resources/buffergeometry_java.txt","resources/src/core/BufferGeometry.js");	
	}
	
	public void testEular(){
		doTest("resources/eular_java.txt","resources/src/math/Euler.js");	
	}
	
	public void testRingGeometry(){
		doTest("resources/ringgeometry_java.txt","resources/src/extras/geometries/RingGeometry.js");	
	}
	
	public void doTest(String correctResource,String readJs){
		String camera=readText(readJs);
		JSClass clazz=JSClassParser.parseJSClass(camera, getPackage(readJs));
		String created=JSConverter.convertCode(codeBase, clazz);
		
		String correct=readText(correctResource);
		assertEquals(correct, created);	
	}
	
public String getPackage(String path){
	int first=path.indexOf("/");
	first=path.indexOf("/",first+1);
	int end=path.lastIndexOf("/");
	return path.substring(first+1,end);
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

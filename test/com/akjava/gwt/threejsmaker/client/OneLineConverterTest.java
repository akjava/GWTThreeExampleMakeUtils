package com.akjava.gwt.threejsmaker.client;

import com.akjava.gwt.threejsmaker.client.oneline.OneLineConvertPanel.GetSetFieldConverter;
import com.akjava.gwt.threejsmaker.client.oneline.OneLineConvertPanel.JsParamConverter;
import com.akjava.gwt.threejsmaker.client.oneline.OneLineConvertPanel.SpecialReplaceConverter;
import com.akjava.gwt.threejsmaker.client.oneline.OneLineConverter;
import com.google.gwt.junit.client.GWTTestCase;

public class OneLineConverterTest extends GWTTestCase {                       

      /**
       * Must refer to a valid module that sources this class.
   */
  public String getModuleName() {                                         
    return "com.akjava.gwt.threejsmaker.ThreeJsMaker";
  }

  
  public void testGetSetFieldConverter1() {                                              
	  doTest(new GetSetFieldConverter(),
			  "controls.damping = 0.2;",
			  "controls.setDamping(0.2);"
			  );
  }
  
  public void testGetSetFieldConverter2() {                                              
	  doTest(new GetSetFieldConverter(),
			  "controls.damping = test.input.x;",
			  "controls.setDamping(test.getInput().getX());"
			  );
  }
  
  public void testGetSetFieldConverter3() {                                              
	  doTest(new GetSetFieldConverter(),
			  "controls.setDamping(test.input.x);",
			  "controls.setDamping(test.getInput().getX());"
			  );
  }
  

  
  public void testGetSetFieldConverter5() {                                              
	  doTest(new GetSetFieldConverter(),
			  "something.x= THREE.RepeatWrapping;",
			  "something.setX(THREE.RepeatWrapping);"
			  );
  }
  
  public void testSpecialReplaceConverter1() {                                              
	  doTest(new SpecialReplaceConverter(),
			  "var x=THREE.Math.degToRad( theta );",
			  "var x=THREEMath.degToRad( theta );"
			  );
  }
  
  public void testJsParamConverter1() {                                              
	  doTest(new JsParamConverter(),
			  "new THREE.Mesh( geometry, new THREE.MeshLambertMaterial( { color: 0x606060, morphTargets: true } ) );",
			  "new THREE.Mesh( geometry, new THREE.MeshLambertMaterial( GWTParamUtils.MeshLambertMaterial().color(0x606060).morphTargets(true) ) );"
			  );
  }
  
  public void doTest(OneLineConverter converter,String input,String correct){
	  assertEquals(correct, converter.apply(input));
  }

    }
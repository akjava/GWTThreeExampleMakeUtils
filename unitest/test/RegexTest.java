package test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

public class RegexTest extends TestCase {

	public void testregex(){
		Pattern pattern=Pattern.compile("\\.uniforms\\[(.*)\\]\\.getValue\\(\\)\\.setRGB\\((.*)\\)");
		
		Matcher matcher=pattern.matcher("effectColorify1.uniforms[ 'color' ].getValue().setRGB( 1, 0.8, 0.8 );");
		
		if(matcher.find()){
		System.out.println(matcher.group()+matcher.group(1));
		}
	}
}

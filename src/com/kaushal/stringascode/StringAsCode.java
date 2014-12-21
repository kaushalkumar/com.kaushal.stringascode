package com.kaushal.stringascode;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.util.List;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * StringAsCode - Processes a java string as a code and returns the evaluated value.
 * @author KaushalKumar
 *
 */
public class StringAsCode {

	public static void main(String[] args) {
		StringAsCode stringAsCode = new StringAsCode();

		String s_js = "strgArray = [	[\"//Output[@name='BureauResponse']\",\"xml!\",\"\"], " 
								+ "		[\"//Output[@name='ReadableResponse']\",\"xml!\",\"\"], "
								+ "		[\"//Output[@name='Report']\",\"xml!\",\"\"], " 
								+ "		[\"//Output[@name='Companies']\",\"xml=\",\"\"]" 
								+ "	]";
		String s_groovy = "strings = [	[\"//Output[@name='BureauResponse']\",\"xml!\",\"\"], " + "		[\"//Output[@name='ReadableResponse']\",\"xml!\",\"\"], "
				+ "		[\"//Output[@name='Report']\",\"xml!\",\"\"], " + "		[\"//Output[@name='Companies']\",\"xml=\",\"\"]" + "	]";
		String s_java = "String[][] strings = { "
				+ "{\"//Output[@name='BureauResponse']\",\"xml!\",\"\"}, "
				+ "{\"//Output[@name='ReadableResponse']\",\"xml!\",\"\"}, "
				+ "{\"//Output[@name='Report']\",\"xml!\",\"\"}, "
				+ "{\"//Output[@name='Companies']\",\"xml=\",\"\"}};";

		String[][] eval2DString = stringAsCode.eval2DStringJS(s_js);
		for (String[] strings : eval2DString) {
			for (String string : strings) {
				System.out.println(string);
			}
		}
		
		String[][] eval2DStringViaGroovyShell = stringAsCode.eval2DStringGroovyShell(s_groovy);
		for (String[] strings : eval2DStringViaGroovyShell) {
			for (String string : strings) {
				System.out.println(string);
			}
		}
		
		String[][] eval2DStringViaBeanShell = stringAsCode.eval2DStringBeanShell(s_java);
		for (String[] strings : eval2DStringViaBeanShell) {
			for (String string : strings) {
				System.out.println(string);
			}
		}
	}

	/**
	 * eval2DStringJS - evaluates a string as a java code using Rhino javascript library.
	 * @param str - string which is a Java script code
	 * @return
	 */
	private String[][] eval2DStringJS(String str) {
		String[][] strings = null;
		Context cx = Context.enter();
		try {
			cx.setLanguageVersion(Context.VERSION_1_2);
			Scriptable scope = cx.initStandardObjects();
			Object scriptResult = cx.evaluateString(scope, str, "Source", 1, null);
			NativeArray arr = (NativeArray) scriptResult;
			strings = new String[(int) arr.getLength()][3];
			for (int counter = 0; counter < arr.getLength(); counter++) {
				strings[counter] = new String[3];
				NativeArray objects = (NativeArray) arr.get(counter);
				strings[counter][0] = (String) objects.get(0);
				strings[counter][1] = (String) objects.get(1);
				strings[counter][2] = (String) objects.get(2);
			}

		} finally {
			Context.exit();
		}
		return strings;
	}
	
	/**
	 * eval2DStringGroovyShell - evaluates a string as a java code using BeanShell library.
	 * @param str - string which is a Groovy code similar to Java code
	 * @return
	 */
	private String[][] eval2DStringGroovyShell(String str) {
		String[][] stringsArr = null;
		Binding binding = new Binding();
		GroovyShell shell = new GroovyShell(binding);
		shell.evaluate(str);
		@SuppressWarnings("unchecked")
		List<List<String>> stringsList = (List<List<String>>) binding.getVariable("strings");
		stringsArr = new String[stringsList.size()][3];
		int counter = 0;
		for (List<String> list : stringsList) {
			Object[] strings = (Object[]) list.toArray();
			stringsArr[counter][0]=strings[0].toString();
			stringsArr[counter][1]=strings[1].toString();
			stringsArr[counter][2]=strings[2].toString();
			counter = counter + 1;
		}
		return stringsArr;
	}

	/**
	 * eval2DStringBeanShell - evaluates a string as a java code using BeanShell library.
	 * @param str - string which is a Java code
	 * @return
	 */
	private String[][] eval2DStringBeanShell(String str) {
		String[][] stringsArr = null;
		try {
			Interpreter interpreter = new Interpreter();  // Construct an interpreter
			interpreter.eval(str);
			stringsArr = (String[][]) interpreter.get("strings");
			System.out.println( interpreter.get("strings") );
			System.out.println(stringsArr);
			return stringsArr;
		}
		catch (EvalError evalError) {
			evalError.printStackTrace();
		}
		return stringsArr;
	}
}

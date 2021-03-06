package SYB_LIB;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.*;
import java.io.*;
import java.util.Comparator;

/** This class contains static utility methods. */
 public class SYBTOOLS {
	/** This array contains the 16 hex digits '0'-'F'. */
	public static final char[] hexDigits = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	
	/** Converts a Color to an 7 byte hex string starting with '#'. */
	public static String c2hex(Color c) {
		int i = c.getRGB();
		char[] buf7 = new char[7];
		buf7[0] = '#';
		for (int pos=6; pos>=1; pos--) {
			buf7[pos] = hexDigits[i&0xf];
			i >>>= 4;
		}
		return new String(buf7);
	}
		
	/** Converts a float to an 9 byte hex string starting with '#'. */
	public static String f2hex(float f) {
		int i = Float.floatToIntBits(f);
		char[] buf9 = new char[9];
		buf9[0] = '#';
		for (int pos=8; pos>=1; pos--) {
			buf9[pos] = hexDigits[i&0xf];
			i >>>= 4;
		}
		return new String(buf9);
	}
		
	public static double[] getMinMax(double[] a) {
		double min = 5000 ;//Double.MAX_VALUE;
		double max = -5000;//Double.MAX_VALUE;
		double value;
		for (int i=0; i<a.length; i++) {
			value = a[i];
			if (value<min)
				min = value;
			if (value>max)
				max = value;
		}
		double[] minAndMax = new double[2];
		minAndMax[0] = min;
		minAndMax[1] = max;
		return minAndMax;
	}

	////0 : min 1 : max
	public static double[] getMinMax(float[] a) {
		double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;
		double value;
		for (int i=0; i<a.length; i++) {
			value = a[i];
			if (value<min)
				min = value;
			if (value>max)
				max = value;
		}
		double[] minAndMax = new double[2];
		minAndMax[0] = min;
		minAndMax[1] = max;
		return minAndMax;
	}
	
	/** Converts the float array 'a' to a double array. */
	public static double[] toDouble(float[] a) {
		int len = a.length;
		double[] d = new double[len];
		for (int i=0; i<len; i++)
			d[i] = a[i];
		return d;
	}
	
	/** Converts the double array 'a' to a float array. */
	public static float[] toFloat(double[] a) {
		int len = a.length;
		float[] f = new float[len];
		for (int i=0; i<len; i++)
			f[i] = (float)a[i];
		return f;
	}
	
	/** Converts carriage returns to line feeds. */
	public static String fixNewLines(String s) {
		char[] chars = s.toCharArray();
		for (int i=0; i<chars.length; i++)
			{if (chars[i]=='\r') chars[i] = '\n';}
		return new String(chars);
	}

	/**
	* Returns a double containg the value represented by the 
	* specified <code>String</code>.
	*
	* @param      s   the string to be parsed.
	* @param      defaultValue   the value returned if <code>s</code>
	*	does not contain a parsable double
	* @return     The double value represented by the string argument or
	*	<code>defaultValue</code> if the string does not contain a parsable double
	*/
	public static double parseDouble(String s, double defaultValue) {
		if (s==null) return defaultValue;
		try {
			defaultValue = Double.parseDouble(s);
		} catch (NumberFormatException e) {}
		return defaultValue;			
	}

	/**
	* Returns a double containg the value represented by the 
	* specified <code>String</code>.
	*
	* @param      s   the string to be parsed.
	* @return     The double value represented by the string argument or
	*	Double.NaN if the string does not contain a parsable double
	*/
	public static double parseDouble(String s) {
		return parseDouble(s, Double.NaN);
	}
	
	/** Returns the number of decimal places need to display two numbers. */
	public static int getDecimalPlaces(double n1, double n2) {
		if (Math.round(n1)==n1 && Math.round(n2)==n2)
			return 0;
		else {
			n1 = Math.abs(n1);
			n2 = Math.abs(n2);
		    double n = n1<n2&&n1>0.0?n1:n2;
		    double diff = Math.abs(n2-n1);
		    if (diff>0.0 && diff<n) n = diff;		    
			int digits = 2;
			if (n<100.0) digits = 3;
			if (n<0.1) digits = 4;
			if (n<0.01) digits = 5;
			if (n<0.001) digits = 6;
			if (n<0.0001) digits = 7;
			return digits;
		}
	}
	
	/** Splits a string into substrings using the default delimiter set, 
	which is " \t\n\r" (space, tab, newline and carriage-return). */
	public static String[] split(String str) {
		return split(str, " \t\n\r");
	}

	/** Splits a string into substring using the characters
	contained in the second argument as the delimiter set. */
	public static String[] split(String str, String delim) {
		if (delim.equals("\n"))
			return splitLines(str);
		StringTokenizer t = new StringTokenizer(str, delim);
		int tokens = t.countTokens();
		String[] strings;
		if (tokens>0) {
			strings = new String[tokens];
			for(int i=0; i<tokens; i++) 
				strings[i] = t.nextToken();
		} else
			strings = new String[0];
		return strings;
	}
	
	static String[] splitLines(String str) {
		Vector v = new Vector();
		try {
			BufferedReader br  = new BufferedReader(new StringReader(str));
			String line;
			while (true) {
				line = br.readLine();
				if (line == null) break;
				v.addElement(line);
			}
			br.close();
		} catch(Exception e) { }
		String[] lines = new String[v.size()];
		v.copyInto((String[])lines);
		return lines;
	}
	
	/** Returns a sorted list of indices of the specified double array.
		Modified from: http://stackoverflow.com/questions/951848 by N.Vischer.
	*/
	public static int[] rank(double[] values) {
		int n = values.length;
		final Integer[] indexes = new Integer[n];
		final Double[] data = new Double[n];
		for (int i=0; i<n; i++) {
			indexes[i] = new Integer(i);
			data[i] = new Double(values[i]);
		}
		Arrays.sort(indexes, new Comparator<Integer>() {
			public int compare(final Integer o1, final Integer o2) {
				return data[o1].compareTo(data[o2]);
			}
		});
		int[] indexes2 = new int[n];
		for (int i=0; i<n; i++)
			indexes2[i] = indexes[i].intValue();
		return indexes2;
	}

	/** Returns a sorted list of indices of the specified String array. */
	public static int[] rank(final String[] data) {
		int n = data.length;
		final Integer[] indexes = new Integer[n];
		for (int i=0; i<n; i++)
			indexes[i] = new Integer(i);
		Arrays.sort(indexes, new Comparator<Integer>() {
			public int compare(final Integer o1, final Integer o2) {
				return data[o1].compareToIgnoreCase(data[o2]);
			}
		});
		int[] indexes2 = new int[n];
		for (int i=0; i<n; i++)
			indexes2[i] = indexes[i].intValue();
		return indexes2;
	}
	
	public static BufferedImage resizeBufferedImage(BufferedImage img, int newW, int newH) { 
	    Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
	    BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

	    Graphics2D g2d = dimg.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();

	    return dimg;
	} 


	public  static double calcDistance(float x1, float y1, float x2, float y2, float x, float y)
	{
		float a = (y1 - y2) / (x1 - x2);

		//  return abs(a*(x - x1) - (y - y1)) / sqrt(a*a + 1);
		return Math.abs(a*(x - x1) - y + y1) /  Math.sqrt(a*a + 1);
	}
	
	public  static int[] getIntArrMaxMin(int[] input)
	{
		int[] out = {0,0};
		int iMin = Integer.MAX_VALUE;
		int iMax = Integer.MIN_VALUE;
		
		for(int i: input)
		{
			if(iMin>i)
				iMin  = i;
			if(iMax<i)
				iMax = i;
		}
		
		out[0] = iMin;
		out[1] = iMax;
		
		
		return out;
		
	}




}


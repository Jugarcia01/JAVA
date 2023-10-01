package java8code;

import java.util.Arrays;

//A Java program to reverse a string
class Inverter {

	//main method of program
	public static void main(String[] args) {
		String result = "";
		String str = null;
		System.out.println("Cadena suministrada:\n" + str);

		System.out.println("Cadena obtenida: " + result);		
		result = invert(null);
		System.out.println(result);

		String str1 = "c";
		System.out.println("Cadena suministrada:\n" + str1);

		System.out.println("Cadena obtenida: ");		
		result = invert(str1);
		System.out.println(result);

		String str2 = "abcd";
		System.out.println("Cadena suministrada:\n" + str2);

		System.out.println("Cadena obtenida: ");		
		result = invert2(str2);
		System.out.println(result);


		String str3 = "geeksforgeeks";
		System.out.println("Cadena suministrada:\n" + str3);

		System.out.println("Cadena obtenida: ");		
		result = invert2(str3);
		System.out.println(result);

	}

	//Function to reverse a string
	public static String invert (String str)
	{
		// Swap character starting from two corners
		try
		{
			int n = str.length();
			char []ch = str.toCharArray();
			String text;
			char temp;

			// Validate data str
			if (n == 0) {
				System.out.println();
				text = "";
			} else if (n == 1) {
				System.out.println(str);
				text = str;
			} else {
				for (int i=0, j=n-1; i<j; i++,j--)
				{
					temp = ch[i];
					ch[i] = ch[j];
					ch[j] = temp;
				}
				System.out.println(ch);
				text = Arrays.toString(ch);
			}
			return text;
		}
		catch(NullPointerException e)
		{
			System.out.print("error: dato invalido");
			return "";
		}
	}

	public static String invert2 (String str){
		String result = "";
		StringBuilder input1 = new StringBuilder();

		if (str != null) {
			// append a string into StringBuilder input1
			input1.append(str);

			// reverse StringBuilder input1
			input1.reverse();

			result = input1.toString();
		}

		return result;
	}

}

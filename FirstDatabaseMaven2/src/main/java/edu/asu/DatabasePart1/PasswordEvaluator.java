package edu.asu.DatabasePart1;

//import java.sql.SQLException;


public class PasswordEvaluator {

	public static String passwordErrorMessage = ""; //Initializing an empty string for returning an error message when invalid password is entered.
	public static String passwordInput = ""; //Initializing an empty string for the input
	public static int passwordIndexofError = -1; //Index of error 
	public static boolean foundUpperCase = false; //Boolean if upper case is found in the password string. 
	public static boolean foundLowerCase = false; //Boolean if lower case is found in the password string.
	public static boolean foundNumericDigit = false; //Boolean if number is found in the password string.
	public static boolean foundSpecialChar = false; //Boolean if special char is found in the password string.
	public static boolean foundLongEnough = false; //Boolean if the password is long enough. 
	private static String inputLine = ""; //The current string being evaluated.
	private static char currentChar; //The current character being evaluated.
	private static int currentCharNdx; //The current index being evaluated.
	private static boolean running; //Boolean for while loop.
	
	//Function to display the current input state of the password.
	private static void displayInputState() {
		System.out.println(inputLine);//Print the entire string. 
		System.out.println(inputLine.substring(0,currentCharNdx) + "?"); //Prints the current character being evaluated.
		System.out.println("The password size: " + inputLine.length() + "  |  The currentCharNdx: " + 
				currentCharNdx + "  |  The currentChar: \"" + currentChar + "\""); //Displays details about the string being evaluated.
	}
	
	//Function to check whether the password is valid or not. 
	public static String evaluatePassword(String input) {
	    passwordErrorMessage = "";
	    passwordIndexofError = 0;
	    inputLine = input;
	    currentCharNdx = 0;

	    if (input.length() <= 0) return "*** Error *** The password is empty!";

	    passwordInput = input;
	    foundUpperCase = false;
	    foundLowerCase = false;
	    foundNumericDigit = false;
	    foundSpecialChar = false;
	    foundLongEnough = input.length() >= 8;
	    running = true;

	    while (running) {
	        currentChar = input.charAt(currentCharNdx);
	        displayInputState();

	        if (currentChar >= 'A' && currentChar <= 'Z') {
	            System.out.println("Upper case letter found");
	            foundUpperCase = true;
	        } else if (currentChar >= 'a' && currentChar <= 'z') {
	            System.out.println("Lower case letter found");
	            foundLowerCase = true;
	        } else if (currentChar >= '0' && currentChar <= '9') {
	            System.out.println("Digit found");
	            foundNumericDigit = true;
	        } else if ("~`!@#$%^&*()_-+={}[]|\\:;\"'<>,.?/".indexOf(currentChar) >= 0) {
	            System.out.println("Special character found");
	            foundSpecialChar = true;
	        } else {
	            passwordIndexofError = currentCharNdx;
	            return "*** Error *** An invalid character has been found!";
	        }

	        // Move to the next character
	        currentCharNdx++;

	        // Stop if we've checked all characters
	        if (currentCharNdx >= inputLine.length()) {
	            running = false;
	        }

	        System.out.println();
	    }

	    // Collect unmet conditions
	    StringBuilder errMessage = new StringBuilder();
	    if (!foundUpperCase) errMessage.append("Upper case; ");
	    if (!foundLowerCase) errMessage.append("Lower case; ");
	    if (!foundNumericDigit) errMessage.append("Numeric digits; ");
	    if (!foundSpecialChar) errMessage.append("Special character; ");
	    if (!foundLongEnough) errMessage.append("Long Enough; ");

	    return errMessage.length() == 0 ? "Success" : "Password does not meet the requirements: " + errMessage.toString();
	}

}
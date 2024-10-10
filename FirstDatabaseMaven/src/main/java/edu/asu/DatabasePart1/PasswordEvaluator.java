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
		passwordErrorMessage = ""; //Initializes error message to empty string.
		passwordIndexofError = 0;//Initializes index to 0.
		inputLine = input; //Set input string.
		currentCharNdx = 0; //Reset current index to 0.
		
		if(input.length() <= 0) return "*** Error *** The password is empty!"; //If the password is empty, return an empty password message. 
		
		currentChar = input.charAt(0);		// The current character from the above indexed position
		//Resets all the parameters back to default values.
		passwordInput = input;
		foundUpperCase = false;
		foundLowerCase = false;	
		foundNumericDigit = false;
		foundSpecialChar = false;
		foundNumericDigit = false;
		foundLongEnough = false;
		running = true;

		while (running) { //While loop that iterates through every character in the input string.
			displayInputState();
			//Checks if the current character is an uppercase letter and if it is, it sets the foundUpperCase to true.
			if (currentChar >= 'A' && currentChar <= 'Z') { 
				System.out.println("Upper case letter found");
				foundUpperCase = true;
			}//Checks if the current character is an lowercase letter and if it is, it sets the foundLowerCase to true. 
			else if (currentChar >= 'a' && currentChar <= 'z') {
				System.out.println("Lower case letter found");
				foundLowerCase = true;
			}//Checks if the current character is a number and if it is, it sets the foundNumericDigit to true. 
			else if (currentChar >= '0' && currentChar <= '9') {
				System.out.println("Digit found");
				foundNumericDigit = true;
			}//Checks if the current character is an special character and if it is, it sets the foundSpecialChar to true.
			else if ("~`!@#$%^&*()_-+={}[]|\\:;\"'<>,.?/".indexOf(currentChar) >= 0) {
				System.out.println("Special character found");
				foundSpecialChar = true;
			}//Checks if the current character is an invalid character and if it is, it returns an error message.
			else {
				passwordIndexofError = currentCharNdx;
				return "*** Error *** An invalid character has been found!";
			}
			//Checks if the password length is > 8.
			if (currentCharNdx >= 7) {
				System.out.println("At least 8 characters found");
				foundLongEnough = true;
			}
			//Iterates to the next character.
			currentCharNdx++;
			//If the index > length of input, stop the loop.
			if (currentCharNdx >= inputLine.length() || inputLine.length() <8) {
				System.out.println("The password is too short or long.");
				running = false;
			}
			else {
				currentChar = input.charAt(currentCharNdx);
			}
			
			System.out.println();
		}
		
		//Return an error message if there is a condition met, or if there are none, return an empty string.
		String errMessage = "";
		if (!foundUpperCase)
			errMessage += "Upper case; ";
		
		if (!foundLowerCase)
			errMessage += "Lower case; ";
		
		if (!foundNumericDigit)
			errMessage += "Numeric digits; ";
			
		if (!foundSpecialChar)
			errMessage += "Special character; ";
			
		if (!foundLongEnough)
			errMessage += "Long Enough; ";
		
		if (errMessage == "")
			return "Success";
		
		passwordIndexofError = currentCharNdx;
		return errMessage + "conditions were not satisfied";

	}
}
package lab1;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

public class Program {

	static Map<String, Integer> operations = new HashMap<String, Integer>();

	/*
	 * TODO: 1. separate a and b in log(a,b) - comma search 
	 */
	private static boolean checkExpr(String expr) {
		int i = 0;
		int numOfOpBrackets = 0;

		int numOfDots = 0;
		expr = expr.replaceAll("\\s+","");
		
		while (i < expr.length()) {

			char c = expr.charAt(i);

			if (!Character.isDigit(c) && !operations.containsKey(c+"") && c != ')' && c != '.'
					&& expr.indexOf("log(") != i) {
				System.out.println("Incorrect expression: invalid symbol " + c);
				return false;
			}

			if (c == '(') {
				numOfDots = 0;
				if (!isNumeric(expr.charAt(i + 1) + "") && expr.charAt(i + 1) != '.' && expr.charAt(i + 1) != '('
						&& expr.charAt(i + 1) != 'l') {
					System.out.println(
							"Incorrect expression: no digit found after opening bracket " + expr.charAt(i + 1));
					return false;
				}
				numOfOpBrackets++;
			}

			else if (c == ')') {
				numOfDots = 0;
				if (numOfOpBrackets == 0) {
					System.out.println("Incorrect expression: mismatching number of brackets "+ expr);
					return false;
				}
				numOfOpBrackets--;
			}

			else if (operations.containsKey(c+"")) {
				numOfDots = 0;
				if ((!isNumeric(expr.charAt(i - 1) + "") && expr.charAt(i - 1) != ')' && expr.charAt(i - 1) != '.')
						|| (!isNumeric(expr.charAt(i + 1) + "") && expr.charAt(i + 1) != '('
								&& expr.charAt(i + 1) != '.') && expr.charAt(i + 1) != 'l') {
					System.out.println("Incorrect expression: no operands matching operation");
					return false;
				}
			}

			else if (Character.isDigit(c) || c == '.') {

				if (c == '.')
					numOfDots++;
				if (numOfDots > 1) {
					System.out.println("Incorrect expression: too many dots");
					return false;
				}

			}

			else if (expr.indexOf("log(") == i) {
				numOfDots = 0;
				//if (expr.indexOf(')', i) == -1) {
				//	System.out.println("Incorrect log function: no closing bracket");
				//	return false;
				//}
				
				int numOfLogBrackets = 1;
				int itemp = i + 5;
				int endOfLog = 0;
				int commaIndex = 0;
				
				while (numOfLogBrackets !=0 && itemp < expr.length()) {
					if (expr.charAt(itemp) == '(') numOfLogBrackets++;
					else if (expr.charAt(itemp) == ')') numOfLogBrackets--;
					
					if (expr.charAt(itemp) == ',' && numOfLogBrackets == 1)
						commaIndex = itemp;
					
					itemp++;
				}
				
				if (numOfLogBrackets == 0) endOfLog = itemp - 1;
				else {
					System.out.println("Incorrect log function: no closing bracket");
					return false;
				}
				
				if (commaIndex == 0) {
					System.out.println("Incorrect log function: no comma found in brackets");
					return false;
				}
				
				//if (expr.indexOf(',', i) == -1 || expr.indexOf(',', i) >= endOfLog) {
				//	System.out.println("Incorrect log function: no comma found in brackets");
				//	return false;
				//}
				

				String firstOp = expr.substring(i + 4, commaIndex);
				String secondOp = expr.substring(commaIndex + 1, endOfLog);

				System.out.println("First " + firstOp + "; Second " + secondOp);

				if (!checkExpr(firstOp) || !checkExpr(secondOp)) return false;
				i = endOfLog;

			}

			i++;
		}

		if (numOfOpBrackets > 0) {
			System.out.println("Incorrect expression: mismatching number of brackets");
			return false;
		}
		return true;
	}

	private static Stack<String> reverseStack(Stack<String> stack) {
		Stack<String> result = new Stack<>();
		while (stack.size() > 0)
			result.push(stack.pop());
		return result;
	}

	public static boolean isNumeric(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}


	private static Double calc(String operation, double operand1, double operand2) {

		double ans;
		if (operation.equals("+"))
			ans = operand1 + operand2;
		else if (operation.equals("-"))
			ans = operand1 - operand2;
		else if (operation.equals("*"))
			ans = operand1 * operand2;
		else if (operation.equals("/"))
			ans = operand1 / operand2;
		else if (operation.equals("^"))
			ans = Math.pow(operand1, operand2);
		else if (operation.equals("log"))
			ans = Math.log(operand1) / Math.log(operand2);
		else
			return null;
		return ans;
	}

	private static Stack<String> toPostfix(String line) {
		Stack<String> stack = new Stack<>();
		Stack<String> postfix = new Stack<>();
		
		char[] expr = line.toCharArray();

		for (int i = 0; i < expr.length; i++) {
			char c = expr[i];
			if (Character.isDigit(c) || c == '.') {
				String num = "";
				while (i < expr.length && !operations.containsKey(expr[i]+"") && expr[i] != ')' && expr[i] != 'l') {
					if (isNumeric(String.valueOf(expr[i])) || expr[i] == '.') {
						num += expr[i];
						i++;
					}
				}
				i--;
				// insert into output expression
				
				postfix.push(num);
			}

			else if (c == '(') {
				// push into stack
				stack.push(c+"");
			}

			else if (c == ')') {
				// insert into output expression everything before '('
				while (stack.size() > 0 && !stack.peek().equals("("))
					postfix.push(String.valueOf(stack.pop()));
				stack.pop();
			}


			else if (operations.containsKey(c+"")) {
				while (stack.size() > 0 && operations.get(stack.peek()) >= operations.get(c+""))
					postfix.push(String.valueOf(stack.pop()));

				stack.push(c+"");
			}
			

			else if (c == 'l') {
				
				int numOfBrackets = 1;
				int itemp = i + 5;
				int endOfLog = 0;
				int commaIndex = 0;
				
				while (numOfBrackets !=0 && itemp < line.length()) {
					if (line.charAt(itemp) == '(') numOfBrackets++;
					else if (line.charAt(itemp) == ')') numOfBrackets--;
					if (line.charAt(itemp) == ',' && numOfBrackets == 1)
						commaIndex = itemp;
					
					itemp++;
				}
				
				
				
				if (numOfBrackets == 0) endOfLog = itemp - 1;
				
				
				
				String firstOp = line.substring(i + 4, commaIndex);
				String secondOp = line.substring(commaIndex + 1, endOfLog);
				
				postfix.push(calcPostfix(toPostfix(firstOp))+"");
				postfix.push(calcPostfix(toPostfix(secondOp))+"");

				//System.out.println("First " + calcPostfix(toPostfix(firstOp)) + "; Second " + calcPostfix(toPostfix(secondOp)));
				
				
				while (stack.size() > 0 && operations.get(stack.peek()) >= operations.get("log"))
					postfix.push(String.valueOf(stack.pop()));

				stack.push("log");
				
				i = endOfLog;

			}
			
			//System.out.println(stack);
		}

		while (stack.size() > 0)
			postfix.push(String.valueOf(stack.pop()));

		return postfix;
	}

	private static double calcPostfix(Stack<String> postfix) {
		Stack<Double> operands = new Stack<>();
		int counter = 0;

		postfix = reverseStack(postfix);
		while (postfix.size() > 0) {
			
			//System.out.println(operands);
			String c = postfix.pop();

			// push number into stack
			if (isNumeric(c)) {
				operands.push(Double.parseDouble(c));
			}

			// take 2 last numbers from stack
			else if (operations.containsKey(c)) {
				double second = operands.size() > 0 ? operands.pop() : 0;
				double first = operands.size() > 0 ? operands.pop() : 0;

				operands.push(calc(c, first, second));
				counter++;
				
				if (c.equals("log")) System.out.println(counter + ". log(" + first + ", " + second + ") = " + operands.peek());
				else System.out.println(counter + ". " + first + " " + c + " " + second + " = " + operands.peek());
			}
		}
		return operands.pop();
	}

	public static void main(String[] args) {

		operations.put("(", 0);
		operations.put("+", 1);
		operations.put("-", 1);
		operations.put("*", 2);
		operations.put("/", 2);
		operations.put("^", 3);
		operations.put("log", 4);

		System.out.println("Enter an expression");
		Scanner sc = new Scanner(System.in);
		String expr = sc.nextLine();

		if (checkExpr(expr)) {

			Stack<String> postfix = toPostfix(expr);
			if (postfix != null) {
				System.out.println("Postfix form: " + postfix);
				System.out.println("Calculated answer:" + calcPostfix(postfix));
			}
		}
		sc.close();
	}

}
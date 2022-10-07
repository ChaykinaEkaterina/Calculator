package lab1;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

public class Program {

	static Map<Character, Integer> operations = new HashMap<Character, Integer>();
	
	private static boolean checkExpr(String expr)
	{
		
		return false;
	}
	
	private static Stack<String> reverseStack(Stack<String> stack) {
		Stack<String> result = new Stack<>();
		while (stack.size()>0) result.push(stack.pop());
		return result;
	}
	
	public static boolean isNumeric(String str) { 
		  try {  
		    Double.parseDouble(str);  
		    return true;
		  } catch(NumberFormatException e){  
		    return false;  
		  }  
		}
	
	private static char[] splitExpression(String line) {
		char[] chars = line.toCharArray();
		return chars;
	}
	
	private static double calc(char operation, double operand1, double operand2) {
		
		double ans;
		switch (operation) {
		case '+':
			ans = operand1 + operand2;
			break;
		case '-':
			ans = operand1 - operand2;
			break;
		case '*':
			ans = operand1 * operand2;
			break;
		case '/':
			ans = operand1 / operand2;
			break;
		case '^':
			ans = Math.pow(operand1, operand2);
			break;
		default:
			ans = 0;
	}
		return ans;
	}
	
	
	private static Stack<String> toPostfix(char[] expr) {
		Stack<Character> stack = new Stack<>();
		Stack<String> postfix = new Stack<>();
		
		for (int i = 0; i < expr.length; i++) {
			char c = expr[i];
			
			if (Character.isDigit(c)||c=='.') {
				String num = "";
				while (i<expr.length && !operations.containsKey(expr[i])) {
					if (isNumeric(String.valueOf(expr[i]))|| expr[i]=='.') {
						num+=expr[i];
						i++;
					}
					else {
						System.out.println("Expression must only contain real numbers, arithmetic operations and brackets: "+c);
					    return null;
					}
				}
				i--;
				//insert into output expression
				postfix.push(num);
			}
			
			else if (c == '(') {
				//push into stack
				stack.push(c);
				postfix.push(String.valueOf(c));
			}
			
			else if (c==')') {
				//insert into output expression everything before '('
				while (stack.size()>0 && stack.peek()!='(') 
					postfix.push(String.valueOf(stack.pop()));
				
				stack.pop();
			}
			
			else if (operations.containsKey(c)) {
				while (stack.size()>0 && operations.get(stack.peek()) >= operations.get(c))
					postfix.push(String.valueOf(stack.pop()));
				
				stack.push(c);
			}
			else {
				System.out.println("Expression must only contain real numbers, arithmetic operations and brackets: "+ c);
			    return null;
			}
		}
		
		while (stack.size()>0) 				
			postfix.push(String.valueOf(stack.pop()));
		
		return postfix;
	}
	
	
	private static double calcPostfix(Stack<String> postfix) {
		Stack<Double> operands = new Stack<>();
		int counter = 0;
		
		postfix = reverseStack(postfix);
		while (postfix.size()>0)
		{
			String c = postfix.pop();
			
			//push number into stack
			if (isNumeric(c)) {
				operands.push(Double.parseDouble(c));
			}
			
			//take 2 last numbers from stack 
			else if (operations.containsKey(c.charAt(0))) {
				double second = operands.size()>0? operands.pop() : 0;
				double first = operands.size()>0? operands.pop() : 0;
				
				operands.push(calc(c.charAt(0),first, second));
				counter++;
				System.out.println(counter+ "." + first+ " "+ c +" "+ second + " = " + operands.peek());
			}
		}
		return operands.pop();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		operations.put('(', 0);
		operations.put('+', 1);
		operations.put('-', 1);
		operations.put('*', 2);
		operations.put('/', 2);
		operations.put('^', 3);
		
		
		System.out.println("Enter an expression");
		Scanner sc = new Scanner(System.in);
		String expr = sc.nextLine();
		
		Stack<String> postfix = toPostfix(splitExpression(expr));
		if (postfix!=null)
		{
		System.out.println("Postfix form: " + postfix);
		System.out.println("Calculated answer:" + calcPostfix(postfix));
		}
		sc.close();
	}

}
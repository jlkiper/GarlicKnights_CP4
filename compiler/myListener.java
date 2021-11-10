package compiler;

//ASM Files
import org.objectweb.asm.Label;//classes for Labels
import static org.objectweb.asm.Opcodes.*;//Static ASM bytecode constants
import org.objectweb.asm.*;//classes for generating bytecode
import org.objectweb.asm.Opcodes;//Explicit import for ASM bytecode constants

import static utils.Utilities.writeFile;
import lexparse.*; //classes for lexer parser
import java.util.*;

//ANTLR file
import org.antlr.v4.runtime.ParserRuleContext; // need to debug every rule

/**
 * Description:
 *
 * @author Jaylon Kiper
 * @author Elizabeth Fultz
 * @version 1.0
 * Programming Project Four
 * CS322 - Compiler Construction
 * Fall 2021
 */

public class myListener extends KnightCodeBaseListener{

	private ClassWriter cw;  //class level ClassWriter 
	private MethodVisitor mainVisitor; //class level MethodVisitor
	private String programName; //name of the class and the output file (used by ASM)
	private boolean debug; //flag to indicate debug status

public class variable{

	public String variableType = "";
	public String value = "";
	public int memLoc = -1;
	public boolean valueSet = false;

	public variable(String variableType, String value){
	
		this.variableType = variableType;
		this.value = value;
	
	}//end variable constructor
	
	public variable(){
	
		variableType = "";
		value = "";
		
	}//end variable constructor


}//end variable class


	public HashMap<String, variable> SymbolTable = new HashMap<String, variable>();
	public static final String INT = "INTEGER";
	public static final String STR = "STRING";
	
	public variable currvar;
	public variable extravar;
	public variable var1;
	public variable var2;
	
	public String outputString;
	public String key;
	public String key2;
	public String keyID;
	public String id;
	public String genNum;
	public String genIntStr = "  ";
	public String genString;
	public String op1 = "";
	public String op2 = "";
	public String operation = "  ";
	public String arithmeticOperation = "  ";
	public String compString;
	public String decOp1;
	public String decOp2;
	public String decCompSymbol;
	public String prev;
	public String then1;
	public String then2;
	public String else1;
	public String else2;
	
	public int decComparison;
	public int decOperator1;
	public int decOperator2;
	public int operator1;
	public int operator2;
	public int num;
	public int outputInt;
	public int count;
	public int decCount;
	public int skipCount = 0;
	public int tempInt;
	
	public boolean exit = false;
	public boolean printString;
	public boolean operationDone;
	public boolean genBool;
	public boolean genPrint;
	public boolean expre;
	public boolean expression1;
	public boolean printTwice;
	
	public int memoryCounter = 1;
	
	//Prints Symbol Table for variables
	public void printHashMap(HashMap<String,variable> map){
	
		Object[]keys = map.keySet().toArray();
		String val;
		int mem;
		boolean set;
		
		for(int i = 0; i < keys.length; i++){
			System.out.print(keys[i]);
			System.out.print(": " + map.get(keys[i]).variableType); 
			val = map.get(keys[i]).value;
			mem = map.get(keys[i]).memLoc;
			set = map.get(keys[i]).valueSet;
			System.out.println(", " + val + ", " + mem + ", " + set);
			
		}//end for loop	
		
	}//end printHashMap method
	
	public boolean isString(variable var){
		
		if(var.variableType.equals(STR))
			return true;
		return false;
		
	}//end isString method
	
	public myListener(String programName, boolean debug){
	       
		this.programName = programName;
		this.debug = debug;

	}//end myListener constructor
	
	public myListener(String programName){
	       
		this.programName = programName;
		debug = false;

	}//end myListener constructor 


	//Labels
        	public static Label label3 = new Label();
        	public static Label label4 = new Label();
        	Label startOfLoop = new Label();
        	Label endOfLoop = new Label();
            	Label returnl = new Label();
            	public Label printSkipIf = new Label();
            	public Label printSkipElse = new Label();
            	public Label secondPart = new Label();

	public void setupClass(){
	
		if(exit){
			return;
		}//end if statement
		
		//Set up the classwriter
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        	cw.visit(Opcodes.V11, Opcodes.ACC_PUBLIC,this.programName, null, "java/lang/Object",null);
	
		//Use local MethodVisitor to create the constructor for the object
		MethodVisitor mv=cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
       	mv.visitCode();
        	mv.visitVarInsn(Opcodes.ALOAD, 0); //load the first local variable: this
        	mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V",false);
        	mv.visitInsn(Opcodes.RETURN);
        	mv.visitMaxs(1,1);
        	mv.visitEnd();
       	
		//Use global MethodVisitor to write bytecode according to entries in the parsetree	
	 	mainVisitor = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC,  "main", "([Ljava/lang/String;)V", null, null);
        	mainVisitor.visitCode(); 
      
	}//end setupClass
	
	public void closeClass(){
	
		if(exit){
			return;
		}//end if statement
			
		//Use global MethodVisitor to finish writing the bytecode and write the binary file.
		mainVisitor.visitLabel(returnl);
		mainVisitor.visitInsn(Opcodes.RETURN);
		mainVisitor.visitMaxs(0, 0);
		mainVisitor.visitEnd();

		cw.visitEnd();

        	byte[] b = cw.toByteArray();

        	writeFile(b,this.programName+".class");
        
        	System.out.println("Successfully wrote to file!");

	}//end closeClass

/**
 * All methods involved with the KnightCodeParser
 *
 */
	
	/**
	 * File methods
	 */
	@Override
	public void enterFile(KnightCodeParser.FileContext ctx){
	
		System.out.println("Enter program rule for first time");
		setupClass();
		
	}//end enterFile method
	@Override
	public void exitFile(KnightCodeParser.FileContext ctx){
	
		if(exit){
			return;
		}//end if statement	
			
		System.out.println("Attempting to exit file");	

		
		closeClass();
		System.out.println("Leaving program rule. . .");

	}//end exitFile method

	
	/**
	 * Declare methods
	 */
	@Override 
	public void enterDeclare(KnightCodeParser.DeclareContext ctx){
	
		if(exit){
			return;
		}//end if statement
			
		System.out.println("Enter declare");
		
		//enter = false;
		count = ctx.getChildCount();
		
	}//end enterDeclare method
	@Override 
	public void exitDeclare(KnightCodeParser.DeclareContext ctx){
	
		if(exit){
			return;
		}//end if statement
			
		printHashMap(SymbolTable);
		
		//enter = true;
		System.out.println("Exit declare");
		
	}//end exitDeclare method

	
	/**
	 * Varaible methods
	 */
	@Override 
	public void enterVariable(KnightCodeParser.VariableContext ctx){
		
		if(exit){
			return;
		}//end if statement
		
		System.out.println("Enter variable");
		
		variable var = new variable();
		
		String identifier = ctx.getChild(1).getText();
		var.variableType = ctx.getChild(0).getText();
		var.memLoc = memoryCounter;
		
		SymbolTable.put(identifier, var);

		memoryCounter++;
		
	
	}//end enterVariable method
	@Override 
	public void exitVariable(KnightCodeParser.VariableContext ctx){ 
		if(exit){
			return;
		}//end if statement
		
		System.out.println("Exit variable");
		
	}//end exitVariable method
	
	
	/**
	 * Identifier methods
	 */
	@Override 
	public void enterIdentifier(KnightCodeParser.IdentifierContext ctx){
		if(exit)
			return;
	}//end enterIdentifier method
	@Override 
	public void exitIdentifier(KnightCodeParser.IdentifierContext ctx){ 
		if(exit)
			return;
	}//end exitIdentifier method
	
	
	/**
	 * Variable Type methods
	 */
	@Override public void enterVartype(KnightCodeParser.VartypeContext ctx) { }
	@Override public void exitVartype(KnightCodeParser.VartypeContext ctx) { }
	
	
	/**
	 * Body methods
	 */
	@Override 
	public void enterBody(KnightCodeParser.BodyContext ctx){ 
	
		if(exit){
			return;
		}//end if statement
		
		System.out.println("Enter body!");
		
		count = ctx.getChildCount();

	}//end enterBody method
	@Override 
	public void exitBody(KnightCodeParser.BodyContext ctx){ 
	
		if(exit){
			return;
		}//end if statement
	
		printHashMap(SymbolTable);
		
		mainVisitor.visitLabel(printSkipIf);
		
		System.out.println("Exit body!");
		
	}//end exitBody method
	
	
	/**
	 * Statement methods
	 */
	@Override public void enterStat(KnightCodeParser.StatContext ctx) { }
	@Override public void exitStat(KnightCodeParser.StatContext ctx) { }
	
	public int operationCount = 0;
	
	
	/**
	 * Set Variable methods
	 */
	@Override 
	public void enterSetvar(KnightCodeParser.SetvarContext ctx){ 
	
		if(exit){
			return;
		}//end if statement
			
		System.out.println("Enter setvar");
		operationCount = 0;
		genIntStr = "";
		
		if(ctx.getChild(1) != null){
		
			key = ctx.getChild(1).getText();
			
		}//end if statement
		
		System.out.println("\n"+key);	
		if(SymbolTable.containsKey(key)){
			currvar = SymbolTable.get(key);
		} else {
			System.out.println("\n\n------------------------------------------");
			System.out.println("COMPILER ERROR");
			System.out.println("------------------------------------------");
			
			System.out.println("Identifier: " + key + " was not declared");
			exit = true;
			return;
		
		}//end if else statement
		
		if(isString(currvar)){
		
			if(ctx.getChild(3).getChildCount() != 0){
			
				System.out.println("\n\n------------------------------------------");
				System.out.println("COMPILER ERROR");
				System.out.println("------------------------------------------");
				
				System.out.println("Variable being set to " + key + " is not a string!");
				exit = true;
				return;
			
			}//end nested if statement
		
			genIntStr = ctx.getChild(3).getText();
			System.out.println(genIntStr);
			
		}//end if statement
	
	}//end enterSetvar method
	@Override 
	public void exitSetvar(KnightCodeParser.SetvarContext ctx){ 
		if(exit)
			return;
	
		System.out.println("Final value of id = " + genIntStr);
		currvar.value = genIntStr;
		
		int store = currvar.memLoc;
		
		genBool = isString(currvar);
			
		if(genBool){
	
			mainVisitor.visitLdcInsn(currvar.value);
			mainVisitor.visitVarInsn(ASTORE,store);
			
		} else {
		
			mainVisitor.visitVarInsn(ISTORE,store);
			
		}//end if else statement
		
		currvar.valueSet = true;
		SymbolTable.put(key, currvar);
		
    	    
    	    	operation = "";
    	    	genIntStr = "";
    	    	
    	    	
    	    	if(elseCount1 > 0){
		
			System.out.println("elseCount : " + elseCount1);
			
			if(ifCount1 == 1){
				
				Label tempEnd;
				Label temper;
				
				int currentUsage = Character.getNumericValue(decNestStack.charAt(0));
				
				switch(currentUsage){
					case 1: {
						tempEnd = endDecLab0;
						temper = startOfElse0;						
						break;
					}
					case 2: {
						tempEnd = endDecLab1;
						temper = startOfElse1;
						break;
					}
					case 3: {
						tempEnd = endDecLab2;
						temper = startOfElse2;						
						break;
					}
					case 4: {
						tempEnd = endDecLab3;
						temper = startOfElse3;
						break;
					}
					case 5: {
						tempEnd = endDecLab4;
						temper = startOfElse4;
						break;
					}
					case 6: { 
						tempEnd = endDecLab5;
						temper = startOfElse5;
						break;
					}
					case 7: { 
						tempEnd = endDecLab6;
						temper = startOfElse6;
						break;
					}
					case 8: { 
						tempEnd = endDecLab7;
						temper = startOfElse7;
						break;
					}
					case 9: { 
						tempEnd = endDecLab8;
						temper = startOfElse8;
						break;
					}
					case 10: {
						tempEnd = endDecLab9;
						temper = startOfElse9;
						break;
					}
					default: {
					
						System.out.println("\n\n------------------------------------------");
						System.out.println("COMPILER ERROR");
						System.out.println("------------------------------------------");
					
						System.out.println("Case 2: jump label failure for if-else statement in exit decision!");
						
						exit = true;
						return;
					}//end default	
						
				}//end switch statement
				
				System.out.println("-------------------------------------------------------");	
				System.out.println("GOTO, end label= " + tempEnd);
				System.out.println("Visit startOfElse Label= " + temper);
				System.out.println("-------------------------------------------------------");
	
				mainVisitor.visitJumpInsn(GOTO, tempEnd);
				mainVisitor.visitLabel(temper);
			
			}//end if statement
					
		}//end if statement
		
		if(ifCount1 > 0)
			ifCount1--;
		
		System.out.println("ifCount = " + ifCount1);
    	    	
		System.out.println("Exit setvar");
		
	}//end exitSetvar method
	
	public String enterAndExitNumber;
	
	
	/**
	 * Number methods
	 */
	@Override 
	public void enterNumber(KnightCodeParser.NumberContext ctx){ 
	
		if(exit){
			return;
		}//end if statement
		
		System.out.println("Enter Number");
		enterAndExitNumber = ctx.getText();
		genIntStr += enterAndExitNumber;
					
	}//end enterNumber method
	@Override 
	public void exitNumber(KnightCodeParser.NumberContext ctx){ 
	
		if(exit){
			return;
		}//end if statement
	
		num = Integer.valueOf(enterAndExitNumber);	
		mainVisitor.visitIntInsn(SIPUSH, num);
		System.out.println("Exit Number");
		
	}//end exitNumber method
	
	
	/**
	 * ID methods
	 */
	@Override 
	public void enterId(KnightCodeParser.IdContext ctx){ 
	
		if(exit){
			return;
		}//end if statement
		
		System.out.println("Enter ID");
		
		keyID = ctx.getText();
		
		if(SymbolTable.containsKey(keyID)){
			var1 = SymbolTable.get(keyID);
			op1 = keyID;
			
			//If we want the value in the symbol table to be contained as a numerical expression instead of variable 
			//expression.
			
			operator1 = var1.memLoc;
			
			
			if(var1.variableType.equalsIgnoreCase(STR) && operationCount > 0){
			
				System.out.println("\n\n------------------------------------------");
				System.out.println("COMPILER ERROR");
				System.out.println("------------------------------------------");
				
				System.out.println("Cannot perform arithmetic operations on a String");
				exit = true;
				return;
			
			}//end nested if statement
			
			if(isString(var1)){
				mainVisitor.visitIntInsn(ALOAD, operator1);
			} else {
				mainVisitor.visitIntInsn(ILOAD, operator1);
			}//end nested if else statement
		
			} else {
			
			System.out.println("\n\n------------------------------------------");
			System.out.println("COMPILER ERROR");
			System.out.println("------------------------------------------");
			
			System.out.println("ID: " + keyID + " does not exist!");
			exit = true;
			return;
		
		}//end if else statement
		
		genIntStr += op1;
		
	}//end enterId method
	@Override 
	public void exitId(KnightCodeParser.IdContext ctx){ 
	
		if(exit){
			return;
		}//end if statement
	
		System.out.println("Exit ID");
		
		genIntStr += arithmeticOperation.charAt(0);
		if(arithmeticOperation.length() != 0){
			arithmeticOperation = arithmeticOperation.substring(1);
		}//end if statement
		
		if(printTwice){
			genIntStr += arithmeticOperation.charAt(0);
			if(arithmeticOperation.length() != 0){
				arithmeticOperation = arithmeticOperation.substring(1);
			}//end nested if statement
			printTwice = false;	
		}//end if statement
	}//end exitId method
	
	
	/**
	 * Parenthesis methods
	 */
	@Override 
	public void enterParenthesis(KnightCodeParser.ParenthesisContext ctx){ 
		if(exit)
			return;
		System.out.println("Enter parenthesis");
		
		genIntStr += "(";
		arithmeticOperation = ")" + arithmeticOperation;
		
	}//end enterParenthesis method
	@Override 
	public void exitParenthesis(KnightCodeParser.ParenthesisContext ctx){ 
		if(exit)
			return;
		
		genIntStr += arithmeticOperation.charAt(0);
		if(arithmeticOperation.length() != 0)
			arithmeticOperation = arithmeticOperation.substring(1);
		
		System.out.println("Exit parenthesis");
		
	}//end exitParenthesis method
	
	
	/**
	 * Addition methods
	 */
	@Override 
	public void enterAddition(KnightCodeParser.AdditionContext ctx){ 
		if(exit)
			return;
		System.out.println("Enter addition");
		operationCount++;
		
		arithmeticOperation = "+" + arithmeticOperation;
	}//end enterAddition mehtod
	@Override 
	public void exitAddition(KnightCodeParser.AdditionContext ctx){ 
		if(exit)
			return;
			
		operationCount--;	

		//ASM bytecode to add integers
		mainVisitor.visitInsn(IADD);
                      	
		System.out.println("Exit addition");
	}
	
	/**
	 * Multiplication methods
	 */
	@Override 
	public void enterMultiplication(KnightCodeParser.MultiplicationContext ctx){ 
		if(exit)
			return;
	
		System.out.println("Enter multiplication");
		operationCount++;
		arithmeticOperation = "*" + arithmeticOperation;
	
	}//end enterMultiplication method
	@Override 
	public void exitMultiplication(KnightCodeParser.MultiplicationContext ctx){ 
		if(exit)
			return;
			
		operationCount--;	
		
		//ASM bytecode to multiply integers
		mainVisitor.visitInsn(IMUL);
            		
		System.out.println("Exit multiplication");
	
	}//end exitMultiplication method
	
	/**
	 * Division methods
	 */
	@Override 
	public void enterDivision(KnightCodeParser.DivisionContext ctx){ 
		if(exit)
			return;
	
		System.out.println("Enter division");
		operationCount++;
		arithmeticOperation = "/"+arithmeticOperation;
	
	}//end enterDivision method
	@Override 
	public void exitDivision(KnightCodeParser.DivisionContext ctx){ 
		if(exit)
			return;
			
		operationCount--;	
		
		//ASM bytecode to divide integers
		mainVisitor.visitInsn(IDIV);
            		
		System.out.println("Exit division");
	
	}//end exitDivision method
	
	
	/**
	 * Subtraction methods
	 */
	@Override 
	public void enterSubtraction(KnightCodeParser.SubtractionContext ctx){ 
		if(exit)
			return;
			
		System.out.println("Enter subtraction");
		operationCount++;
		arithmeticOperation = "-"+ arithmeticOperation;
	
	}//end enterSubtraction method
	@Override 
	public void exitSubtraction(KnightCodeParser.SubtractionContext ctx){ 
		if(exit)
			return;
			
		operationCount--;
			
		//ASM bytecode to subtract integers
		mainVisitor.visitInsn(ISUB);
            	           	
		System.out.println("Exit subtraction");
	}//end exitSubtraction method
	
	/**
	 * Comparison methods
	 */
	@Override 
	public void enterComparison(KnightCodeParser.ComparisonContext ctx){ 
		if(exit)
			return;
		System.out.println("Enter Comparison");
		
		if(ctx.getChildCount() != 0){
			compString = ctx.getChild(1).getChild(0).getText();	
			operation = compString + operation;
			if(compString.equals("<>"))
				printTwice = true;
		}//end if statement	
	
	}//end enterComparison method
	@Override 
	public void exitComparison(KnightCodeParser.ComparisonContext ctx){ 
		if(exit)
			return;		       
		Label label1 = new Label();
        	Label label2 = new Label();
        	
        		if(compString.equals(">")){
				
				//System.out.println("compString equals >: " +compString.equals(">"));
				mainVisitor.visitJumpInsn(IF_ICMPLE, label1);
				//compString = "IF_ICMPLE"; 
				arithmeticOperation = ">"+arithmeticOperation;
				
			} else if(compString.equals("<")){
				
				mainVisitor.visitJumpInsn(IF_ICMPGE, label1);
				//compString = "IF_ICMPGE"; 
				arithmeticOperation = "<"+arithmeticOperation;
			
			} else if(compString.equals("<>")){
			
				mainVisitor.visitJumpInsn(IF_ICMPEQ, label1);
				//compString = "IF_ICMPEQ";
				arithmeticOperation = "<>"+arithmeticOperation;
			
			} else if(compString.equals("=")){
			
				mainVisitor.visitJumpInsn(IF_ICMPNE, label1);
				//compString = "IF_ICMPNE";	
				arithmeticOperation = "="+arithmeticOperation;
			}//end if else statement
			
		mainVisitor.visitInsn(ICONST_1);
		mainVisitor.visitJumpInsn(GOTO, label2);
		mainVisitor.visitLabel(label1);
		mainVisitor.visitInsn(ICONST_0);
		mainVisitor.visitLabel(label2);
		
		System.out.println("Exit Comparison");
	}//end exitComparison method
	 
	/**
	 * Comp: GT | LT | EQ | NEQ methods
	 *
	 */ 
	@Override 
	public void enterComp(KnightCodeParser.CompContext ctx){ 
		if(exit)
			return;
			
		System.out.println("Enter Comp");	
			
	}//end enterComp method
	@Override 
	public void exitComp(KnightCodeParser.CompContext ctx){ 
		if(exit)
			return;
		
		System.out.println("Exit Comp");
		
	}//end exitComp method
	
	Label endDecLab0 = new Label();
	Label endDecLab1 = new Label();
	Label endDecLab2 = new Label();
	Label endDecLab3 = new Label();
	Label endDecLab4 = new Label();
	Label endDecLab5 = new Label();
	Label endDecLab6 = new Label();
	Label endDecLab7 = new Label();
	Label endDecLab8 = new Label();
	Label endDecLab9 = new Label();
	
	Label startOfElse0 = new Label();
	Label startOfElse1 = new Label();
	Label startOfElse2 = new Label();
	Label startOfElse3 = new Label();
	Label startOfElse4 = new Label();
	Label startOfElse5 = new Label();
	Label startOfElse6 = new Label();
	Label startOfElse7 = new Label();
	Label startOfElse8 = new Label();
	Label startOfElse9 = new Label();
	
	public static int ifCount1 = 0;
	public static int elseCount1 = 0;

	public static int decLabCount = 0;
	public int decCount2 = 0;

	public String decNestStack = "000";	

	public boolean firstNestedDec = false;
	
	/**
	 * Decision methods
	 */
	@Override 
	public void enterDecision(KnightCodeParser.DecisionContext ctx){ 
	
		if(exit){
			return;
		}//end if statement
		
		System.out.println("Enter Decision");
		if(decLabCount > 8){
		
			System.out.println("\n\n------------------------------------------");
			System.out.println("COMPILER ERROR");
			System.out.println("------------------------------------------");
			
			System.out.println("Too many If-Else statements, compiler can only handle 10 or less!");
			exit = true;
			return;

		} else {
			decLabCount++;	
			decCount2++;		
		}//end if else statement
		
		decNestStack = decLabCount + decNestStack;
		System.out.println("Current stack = " + decNestStack);
		
		
		
		int tempElse = elseCount1;
		
		//Possible issue might occur for nested if-else statements due to values reseting.
		
		decCount = ctx.getChildCount();
		if(decCount < 7){
		
			System.out.println("\n\n------------------------------------------");
			System.out.println("COMPILER ERROR");
			System.out.println("------------------------------------------");
			
			System.out.println("Syntax is wrong for If-Else statement!");
			
			exit = true;
			return;
		
		
		}//end if statements
		
		//IF THEN ELSE ENDIF STATEMENTS
		
		if(!ctx.getChild(0).getText().equalsIgnoreCase("IF")){
		
			System.out.println("\n\n------------------------------------------");
			System.out.println("COMPILER ERROR");
			System.out.println("------------------------------------------");
			
			System.out.println("Syntax is wrong for If-Else statement!");
			
			exit = true;
			return;
		
		}//end IF case
		
		if(!ctx.getChild(4).getText().equalsIgnoreCase("THEN")){
		
			System.out.println("\n\n------------------------------------------");
			System.out.println("COMPILER ERROR");
			System.out.println("------------------------------------------");
			
			System.out.println("Syntax is wrong for If-Else statement! If statement must be followed by then");
			
			exit = true;
			return;
		
		}//end THEN case
		
		if(ctx.getChild(decCount-2).getText().equalsIgnoreCase("ELSE")||ctx.getChild(5).getText().equalsIgnoreCase("ELSE")){
		
				System.out.println("\n\n------------------------------------------");
				System.out.println("COMPILER ERROR");
				System.out.println("------------------------------------------");
			
				System.out.println("Syntax is wrong for If-Else statement at ELSE");
			
				exit = true;
				return;
					
		}//end ELSE case
		
		if(!ctx.getChild(decCount-1).getText().equalsIgnoreCase("ENDIF")){
		
			System.out.println("\n\n------------------------------------------");
			System.out.println("COMPILER ERROR");
			System.out.println("------------------------------------------");
			

			System.out.println("Syntax is wrong for If-Else statement at ENDIF");
			exit = true;
			return;
			
		}//end ENDIF case
		
		
		decOp1 = ctx.getChild(1).getText();
		if(SymbolTable.containsKey(decOp1)){
			var1 = SymbolTable.get(decOp1);
			
			if(!var1.valueSet||var1.variableType.equalsIgnoreCase(STR)){
				
				System.out.println("\n\n------------------------------------------");
				System.out.println("COMPILER ERROR");
				System.out.println("------------------------------------------");
			
				System.out.println("Comparison failed!");
				System.out.println("ID: " + decOp1 + " value has not been set, or ID is a String!");
				
				exit = true;
				return;
        			
				
			}// end nested if statement
			
			operator1 = var1.memLoc;
			mainVisitor.visitIntInsn(ILOAD, operator1);
		} else {
			try{
            			
            			decOperator1 = Integer.valueOf(decOp1);
            			mainVisitor.visitIntInsn(SIPUSH, decOperator1);
            			
            			
       		     } catch(NumberFormatException e){
        			
        			System.out.println("\n\n------------------------------------------");
				System.out.println("COMPILER ERROR");
				System.out.println("------------------------------------------");
			
				System.out.println("Comparison failed!");
				System.out.println("ID: " + decOp1 + " does not exist, is not assigned a value or is not a valid 					integer.");
				
				exit = true;
				return;
        			
        		     }//end nested try catch clause
		
		}//end if else statement
		
		decOp2 = ctx.getChild(3).getText();
		if(SymbolTable.containsKey(decOp2)){
			var2 = SymbolTable.get(decOp2);

			if(!var2.valueSet || var2.variableType.equalsIgnoreCase(STR)){
				
				System.out.println("\n\n------------------------------------------");
				System.out.println("COMPILER ERROR");
				System.out.println("------------------------------------------");
			
				System.out.println("Comparison failed!");
				System.out.println("ID: " + decOp1 + " value has not been set, or ID is a String!");
				
				exit = true;
				return;	
			}//end if statement
			
			operator2 = var2.memLoc;
			mainVisitor.visitIntInsn(ILOAD, operator2);
		} else {
		
			  try{
            			
            			decOperator2 = Integer.valueOf(decOp2);
            			mainVisitor.visitIntInsn(SIPUSH, decOperator2);	
       		     } catch(NumberFormatException e){
       		     
        			System.out.println("\n\n------------------------------------------");
				System.out.println("COMPILER ERROR");
				System.out.println("------------------------------------------");
			
				System.out.println("Comparison failed!");
				System.out.println("ID: " + decOp2 + " does not exist, is not assigned a value or is not a valid 					integer.");
				
				exit = true;
				return;
        			
        		     }//end try catch clause
		}//end if else statement		
			
		decCompSymbol = ctx.getChild(2).getChild(0).getText();		
			
			prev = "THEN";	
			String temporaryCounterString;	
			tempInt = decCount-7;
			//System.out.println("Temp int = " +tempInt);
		if(tempInt > 0){
			//THEN
			while(tempInt > 0 && prev.equalsIgnoreCase("THEN")){
		
				if(ctx.getChild(decCount-tempInt-1).getText().equalsIgnoreCase("ELSE"))
				prev = "ELSE";
				
				//NESTED LOOP inside of if-else statement
				temporaryCounterString = ctx.getChild(decCount-tempInt-2).getText();
				System.out.println(temporaryCounterString);
				if(temporaryCounterString.length()>5){
					temporaryCounterString = temporaryCounterString.substring(0,5);
				}
				//System.out.println(temporaryCounterString);
				if(temporaryCounterString.equalsIgnoreCase("WHILE")){
				
					System.out.println("--------------------------------------------------------");
					System.out.println("ifCount before addition: " + ifCount1);
					
					System.out.println("\nChild count: " + ctx.getChild(decCount-				  tempInt-2).getChild(0).getChildCount());
					ifCount1 += (ctx.getChild(decCount-tempInt-2).getChild(0).getChildCount() - 6);
					System.out.println("ifCount after addition: " + ifCount1);
					System.out.println("--------------------------------------------------------");
					
				
				}//end if statement

				ifCount1++;
			
				tempInt--;	
			
			}//end while loop
			
			//ELSE
			while(tempInt > 0){
		
				elseCount1++;
		
				tempInt--;	
			}//end while loop
		
			//ENDIF
		} else {
			ifCount1++;
		}//end if else statement
			System.out.println("ifCount: " + ifCount1);
			System.out.println("elseCount: " + elseCount1);
			
		
		Label temp; 
		Label tempEnd;
		
			
			int currentUsage = Character.getNumericValue(decNestStack.charAt(0));
			switch(currentUsage){
				case 1: {
					temp = startOfElse0;
					tempEnd = endDecLab0;
					break;
				}
				case 2: {
					temp = startOfElse1;
					tempEnd = endDecLab1;
					break;
				}
				case 3: {
					temp = startOfElse2;
					tempEnd = endDecLab2;
					break;
				}
				case 4: {
					temp = startOfElse3;
					tempEnd = endDecLab3;
					break;
				}
				case 5: {
					temp = startOfElse4;
					tempEnd = endDecLab4;
					break;
				}
				case 6: {
					temp = startOfElse5;
					tempEnd = endDecLab5;
					break;
				}
				case 7: { 
					temp = startOfElse6;
					tempEnd = endDecLab6;
					break;
				}
				case 8: {
					temp = startOfElse7;
					tempEnd = endDecLab7;
					break;
				}
				case 9: { 
					temp = startOfElse8;
					tempEnd = endDecLab8;
					break;
				}
				case 10: { 
					temp = startOfElse9;
					tempEnd = endDecLab9;
					break;
				}
				default: {
				
					System.out.println("\n\n------------------------------------------");
					System.out.println("COMPILER ERROR");
					System.out.println("------------------------------------------");
				
					System.out.println("jump label failure for if-else statement at enter!");
					
					exit = true;
					return;
				}	
			}//end switch statement
			
			System.out.println("decLabCount = " + decLabCount);
			
		String tempStringDecBla = "ifComp... ";
		if(elseCount1 > tempElse){
			tempStringDecBla += "startOfElse Label: ";
			
		} else {
			tempStringDecBla += "end Label: ";
			temp = tempEnd;
				
		}//end if else statement	
		
		System.out.println("-------------------------------------------------------");
		System.out.println(tempStringDecBla + temp);	
		System.out.println("-------------------------------------------------------");
			if(decCompSymbol.equals(">")){
				
				//System.out.println("compString equals >: " +compString.equals(">"));
				mainVisitor.visitJumpInsn(IF_ICMPLE, temp);
				//compString = "IF_ICMPLE"; 
				//operation = ">"+operation;
				
			} else if(decCompSymbol.equals("<")){
				
				mainVisitor.visitJumpInsn(IF_ICMPGE,temp);
				//compString = "IF_ICMPGE"; 
				//operation = "<"+operation;
			
			} else if(decCompSymbol.equals("<>")){
			
				mainVisitor.visitJumpInsn(IF_ICMPEQ, temp);
				//compString = "IF_ICMPEQ";
				//operation = "<>"+operation;
			
			} else if(decCompSymbol.equals("=")){
			
				mainVisitor.visitJumpInsn(IF_ICMPNE, temp);
				//compString = "IF_ICMPNE";	
				//operation = "="+operation;
			
			}//end if else statements
	

	}//end enterDecision method
	@Override 
	public void exitDecision(KnightCodeParser.DecisionContext ctx){ 
		if(exit)
			return;
		
		System.out.println("decNestStack = " + decNestStack);	
		
			Label temper; 	
			Label temp;
			int currentUsage = Character.getNumericValue(decNestStack.charAt(0));
			
			switch(currentUsage) {
		
				case 1: {
					temp = endDecLab0;
					temper = startOfElse0;	
					break;
				}
				case 2: {
					temp = endDecLab1;
					temper = startOfElse1;	
					break;
				}
				case 3: {
					temp = endDecLab2;
					temper = startOfElse1;	
					break;
				}
				case 4: {
					temp = endDecLab3;
					temper = startOfElse1;	
					break;
				}
				case 5: {
					temp = endDecLab4;
					temper = startOfElse1;	
					break;
				}
				case 6: {
					temper = startOfElse5;
					temp = endDecLab5;
					break;
				}
				case 7: { 
					temper = startOfElse6;
					temp = endDecLab6;
					break;
				
				}
				case 8: {
					temper = startOfElse7;
					temp = endDecLab7;
					break;
				
				}
				case 9: { 
					temper = startOfElse8;
					temp = endDecLab8;
					break;
				
				}
				case 10: { 
					temper = startOfElse9;
					temp = endDecLab9;
					break;
				
				}
				default: {
					temper = startOfElse1;	
			
					System.out.println("\n\n------------------------------------------");
					System.out.println("COMPILER ERROR");
					System.out.println("------------------------------------------");
				
					System.out.println("Case 1: jump label failure for if-else statement at exit");
					
					exit = true;
					return;
				}	
					
			}//end switch statement
			
			decCount2--;
		
		System.out.println("-------------------------------------------------------");
		System.out.println("Visit end label dec= " + temp);
		System.out.println("-------------------------------------------------------");
		
		//ASM bytecode to visit label holding temp value
		mainVisitor.visitLabel(temp);
		
		if(decNestStack.length() != 0)
			decNestStack = decNestStack.substring(1);
		System.out.println("Current stack = " + decNestStack);
		
		
		if(elseCount1 > 0){
		
			System.out.println("elseCount : " + elseCount1);
			
			if(ifCount1 == 1){
				
				Label tempEnd;
				
				currentUsage = Character.getNumericValue(decNestStack.charAt(0));
				
				switch(currentUsage){
					case 1: {
						tempEnd = endDecLab0;
						temper = startOfElse0;						
						break;
					}
					case 2: {
						tempEnd = endDecLab1;
						temper = startOfElse1;
						break;
					}
					case 3: {
						tempEnd = endDecLab2;
						temper = startOfElse2;						
						break;
					}
					case 4: {
						tempEnd = endDecLab3;
						temper = startOfElse3;
						break;
					}
					case 5: {
						tempEnd = endDecLab4;
						temper = startOfElse4;
						break;
					}
					case 6: { 
						tempEnd = endDecLab5;
						temper = startOfElse5;
						break;
					
					}
					case 7: { 
						tempEnd = endDecLab6;
						temper = startOfElse6;
						break;
					
					}
					case 8: { 
						tempEnd = endDecLab7;
						temper = startOfElse7;
						break;
					
					}
					case 9: { 
						tempEnd = endDecLab8;
						temper = startOfElse8;
						break;
					
					}
					case 10: {
						tempEnd = endDecLab9;
						temper = startOfElse9;
						break;
					
					}
					default: {
					
						System.out.println("\n\n------------------------------------------");
						System.out.println("COMPILER ERROR");
						System.out.println("------------------------------------------");
					
						System.out.println("Case 2: jump label failure for if-else statement in exit decision!");
						
						exit = true;
						return;
					}	
						
				}//end switch statement
				
				System.out.println("-------------------------------------------------------");	
				System.out.println("GOTO, end label= " + tempEnd);
				System.out.println("Visit startOfElse Label= " + temper);
				System.out.println("-------------------------------------------------------");
	
				mainVisitor.visitJumpInsn(GOTO, tempEnd);
				mainVisitor.visitLabel(temper);
			
			} else {
				//System.out.println("DOES THIS EVEN HAPPEN????");
				//System.out.println("ifCount = " + ifCount);
				//ifCount1--;
			}//end nested if else statement
					
		}//end if statement
		
		if(ifCount1 > 0)
			ifCount1--;
		
		System.out.println("ifCount = " + ifCount1);
			
		System.out.println("Exit Decision");
		
	}//end exitdecision method
	
	
	/**
	 * Print methods
	 */
	@Override
	public void enterPrint(KnightCodeParser.PrintContext ctx){
	
		if(exit){
			return;
		}//end if statement
		
		System.out.println("-----------------------------------------------------------------");
		System.out.println("Enter print");

		key2 = ctx.getChild(1).getText();

		//ASM bytecode	to print varaibles
		mainVisitor.visitFieldInsn(Opcodes.GETSTATIC,"java/lang/System", "out", "Ljava/io/PrintStream;");
		
		if(SymbolTable.containsKey(key2)){
			genPrint = false;
			extravar = SymbolTable.get(key2);
			
			outputInt = extravar.memLoc;
			if(isString(extravar)){
				printString = true;
			} else { 	
				printString = false;
			}//end nested if else statement
			
		} else {
			genPrint = true;
			outputString = key2; 
		}//end if else statement
		
	}//end enterWrite_stmt
	@Override 
	public void exitPrint(KnightCodeParser.PrintContext ctx){ 
		if(exit){
			return;
		}//end if statement
	
		
		if(genPrint){
			System.out.println("\nString will be printed to bytecode file\n");
			
			
			mainVisitor.visitLdcInsn(outputString);
			mainVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",  "println", "(Ljava/lang/String;)V", 				false);
		} else {
			if(printString){
				System.out.println("\nString will be printed to bytecode file\n");
				
				mainVisitor.visitVarInsn(ALOAD, outputInt);
				mainVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",  "println", "(Ljava/lang/String;)V", false);
			} else {
				System.out.println("\nInt will be printed to bytecode file\n");
				
				
				mainVisitor.visitVarInsn(Opcodes.ILOAD, outputInt);
				mainVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
			}//end nested if else statement
			
		}//end if else statement
		
		
		
		
		if(elseCount1 > 0){
			
			if(ifCount1 == 1){
				
				Label temper; 
				Label tempEnd;
		 		int currentUsage = Character.getNumericValue(decNestStack.charAt(0));
				switch(currentUsage){
					case 1: {
						tempEnd = endDecLab0;
						temper = startOfElse0;						
						break;
					}
					case 2: {
						tempEnd = endDecLab1;
						temper = startOfElse1;
						break;
					}
					case 3: {
						tempEnd = endDecLab2;
						temper = startOfElse2;						
						break;
					}
					case 4: {
						tempEnd = endDecLab3;
						temper = startOfElse3;
						break;
					}
					case 5: {
						tempEnd = endDecLab4;
						temper = startOfElse4;
						break;
					}
					case 6: {						
						tempEnd = endDecLab5;
						temper = startOfElse5;
						break;
					}
					case 7: { 						
						tempEnd = endDecLab6;
						temper = startOfElse6;
						break;
				
					}
					case 8: {
						tempEnd = endDecLab7;
						temper = startOfElse7;
						break;
				
					}
					case 9: { 
						tempEnd = endDecLab8;
						temper = startOfElse8;
						break;
					
					}
					case 10: { 
						tempEnd = endDecLab9;
						temper = startOfElse9;
						break;
				
					}
					default: {
					
						System.out.println("\n\n------------------------------------------");
						System.out.println("COMPILER ERROR");
						System.out.println("------------------------------------------");
					
						System.out.println("jump label failure for if-else statement in print!");
						
						exit = true;
						return;
					}	
						
				}//end switch statement
			
				System.out.println("-------------------------------------------------------");	
				System.out.println("GOTO, end label= " + tempEnd);
				System.out.println("Visit startOfElse Label= " + temper);
				System.out.println("-------------------------------------------------------");
	
				mainVisitor.visitJumpInsn(GOTO, tempEnd);
				mainVisitor.visitLabel(temper);
				
				//if(decNestStack.length() != 0)
				//	decNestStack = decNestStack.substring(1);
				System.out.println("Current stack = " + decNestStack);
			
			} else {
				//System.out.println("DID THIS NOT HAPPEN???");
				//ifCount1--;
			}//end nested if else statement
		
		}//end if statement
		
		if(ifCount1 > 0){
			ifCount1--;
		}//end if statement
		
		System.out.println("ifCount = " + ifCount1);
		System.out.println("decNestStack = " + decNestStack);
		System.out.println("Exit print");
	}//end exitPrint method
	
	public boolean alreadyRead = false;
	public int readStoredLocation;
	
	/**
	 * Read methods
	 */
	@Override 
	public void enterRead(KnightCodeParser.ReadContext ctx){ 
		if(exit){
			return;
		}//end if statement
		
		System.out.println("Enter read\n");
		
		if(ctx.getChild(1) != null){
			key = ctx.getChild(1).getText();
		}//end if statement
		
		System.out.println("\n"+key);	
		if(SymbolTable.containsKey(key)){
			currvar = SymbolTable.get(key);
		} else {
			System.out.println("\n\n------------------------------------------");
			System.out.println("COMPILER ERROR");
			System.out.println("------------------------------------------");
			
			System.out.println("Identifier: " + key + " was not declared");
			exit = true;
			return;
		
		}//end if else statement
		
		
		/*
		
	    methodVisitor.visitTypeInsn(NEW, "java/util/Scanner");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitFieldInsn(GETSTATIC,"java/lang/System", "in", "Ljava/io/InputStream;");
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/Scanner", "<init>", "(Ljava/io/InputStream;)V" , false);
            methodVisitor.visitVarInsn(ASTORE,store);
		
		*/


		if(alreadyRead){
		
		} else {
			alreadyRead = true;
			
			readStoredLocation = memoryCounter;
		
			mainVisitor.visitTypeInsn(NEW, "java/util/Scanner");
            		mainVisitor.visitInsn(DUP);
            		mainVisitor.visitFieldInsn(GETSTATIC,"java/lang/System", "in", "Ljava/io/InputStream;");
            		mainVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/Scanner", "<init>", "(Ljava/io/InputStream;)V" , false);
            		mainVisitor.visitVarInsn(ASTORE,readStoredLocation);
		
		
			memoryCounter++;
			
		}//end if else statement
		
	}//end enterRead method
	@Override 
	public void exitRead(KnightCodeParser.ReadContext ctx){ 
		if(exit){
			return;
		}//end if statement
		
		System.out.println("ALOAD, readStoredLocation");
		mainVisitor.visitVarInsn(ALOAD,readStoredLocation);
		
		genBool = isString(currvar);
			
		if(genBool){
	
			System.out.println("INVOKEVIRTUAL, nextLine");
			System.out.println("ASTORE, " + currvar.memLoc);
			
			mainVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/Scanner", "nextLine", "()Ljava/lang/String;", false);
			mainVisitor.visitVarInsn(ASTORE,currvar.memLoc);
			
		} else {
		
			System.out.println("INVOKEVIRTUAL, nextInt");
			System.out.println("ISTORE, " + currvar.memLoc);
			
			mainVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/Scanner", "nextInt", "()I", false);
			mainVisitor.visitVarInsn(ISTORE,currvar.memLoc);
		}//end if else statement
	
		currvar.valueSet = true;
		SymbolTable.put(key, currvar);
		
		if(elseCount1 > 0){
			
			if(ifCount1 == 1){
				
				Label temper; 
				Label tempEnd;
		 		int currentUsage = Character.getNumericValue(decNestStack.charAt(0));
				switch(currentUsage){
					case 1: {
						tempEnd = endDecLab0;
						temper = startOfElse0;						
						break;
					}
					case 2: {
						tempEnd = endDecLab1;
						temper = startOfElse1;
						break;
					}
					case 3: {
						tempEnd = endDecLab2;
						temper = startOfElse2;						
						break;
					}
					case 4: {
						tempEnd = endDecLab3;
						temper = startOfElse3;
						break;
					}
					case 5: {
						tempEnd = endDecLab4;
						temper = startOfElse4;
						break;
					}
					case 6: {						
						tempEnd = endDecLab5;
						temper = startOfElse5;
						break;
					}
					case 7: { 						
						tempEnd = endDecLab6;
						temper = startOfElse6;
						break;
					}
					case 8: {
						tempEnd = endDecLab7;
						temper = startOfElse7;
						break;
					}
					case 9: { 
						tempEnd = endDecLab8;
						temper = startOfElse8;
						break;
					}
					case 10: { 
						tempEnd = endDecLab9;
						temper = startOfElse9;
						break;
					}
					default: {
					
						System.out.println("\n\n------------------------------------------");
						System.out.println("COMPILER ERROR");
						System.out.println("------------------------------------------");
					
						System.out.println("jump label failure for if-else statement in print!");
						
						exit = true;
						return;
					}	
						
				}//end switch statement
				
				System.out.println("-------------------------------------------------------");	
				System.out.println("GOTO, end label= " + tempEnd);
				System.out.println("Visit startOfElse Label= " + temper);
				System.out.println("-------------------------------------------------------");
	
				mainVisitor.visitJumpInsn(GOTO, tempEnd);
				mainVisitor.visitLabel(temper);
				
				System.out.println("Current stack = " + decNestStack);
			
			} else {
				//System.out.println("DID THIS NOT HAPPEN???");
				//ifCount1--;
			}//end nested if else statement
			
		}//end if statement
		
		if(ifCount1 > 0){
			ifCount1--;
		}//end if statement
		
		System.out.println("ifCount = " + ifCount1);
		
		System.out.println("Exit read\n");
		

	}//end exitRead method

	public int loopLabCount = 0;
	public int loopCount = 0;
	
	public String loopOp1;
	public int loopOperator1;
	public String loopOp2;
	public int loopOperator2;
	
	public String loopCompSymbol;
	
	public String loopNestStack = "000";	
	
	Label endOfloop0 = new Label();
	Label endOfloop1 = new Label();
	Label endOfloop2 = new Label();
	Label endOfloop3 = new Label();
	Label endOfloop4 = new Label();
	Label endOfloop5 = new Label();
	Label endOfloop6 = new Label();
	Label endOfloop7 = new Label();
	Label endOfloop8 = new Label();
	Label endOfloop9 = new Label();
	
	Label startOfloop0 = new Label();
	Label startOfloop1 = new Label();
	Label startOfloop2 = new Label();
	Label startOfloop3 = new Label();
	Label startOfloop4 = new Label();
	Label startOfloop5 = new Label();
	Label startOfloop6 = new Label();
	Label startOfloop7 = new Label();
	Label startOfloop8 = new Label();
	Label startOfloop9 = new Label();
	
	
	
	
	/**
	 * Loop methods
	 */
	@Override 
	public void enterLoop(KnightCodeParser.LoopContext ctx){ 
		if(exit){
			return;
		}//end if statement
		
		System.out.println("Enter loop");
		
		if(loopLabCount > 9){
		
			System.out.println("\n\n------------------------------------------");
			System.out.println("COMPILER ERROR");
			System.out.println("------------------------------------------");
			
			System.out.println("Too many While-loops, compiler can only handle 10 or less!");
			exit = true;
			return;

		} else {
			loopLabCount++;	
			loopCount++;		
		}//end if else statement
		
		loopNestStack = loopLabCount + loopNestStack;
		System.out.println("Current stack = " + loopNestStack);	
	
		int syntaxTest = ctx.getChildCount();
		if(syntaxTest < 7){
		
			System.out.println("\n\n------------------------------------------");
			System.out.println("COMPILER ERROR");
			System.out.println("------------------------------------------");
			
			System.out.println("Syntax is wrong for If-Else statement!");
			
			exit = true;
			return;
			
		}//end if statement
		
		//IF
		if(!ctx.getChild(0).getText().equalsIgnoreCase("WHILE")){
		
			System.out.println("\n\n------------------------------------------");
			System.out.println("COMPILER ERROR");
			System.out.println("------------------------------------------");
			
			System.out.println("Syntax is wrong for while loop");
			
			exit = true;
			return;

		}//end if statement
		
		if(!ctx.getChild(4).getText().equalsIgnoreCase("DO")){
		
			System.out.println("\n\n------------------------------------------");
			System.out.println("COMPILER ERROR");
			System.out.println("------------------------------------------");
			
			System.out.println("Syntax is wrong for while-loop! after comparison must come \"DO\"");
			
			exit = true;
			return;
		
		
		}
		
		if(!ctx.getChild(syntaxTest-1).getText().equalsIgnoreCase("ENDWHILE")){
		
				System.out.println("\n\n------------------------------------------");
				System.out.println("COMPILER ERROR");
				System.out.println("------------------------------------------");
			
				//System.out.println("ELSE");
				System.out.println("Syntax is wrong for while-loop, must end with \"ENDWHILE\"");
			
				exit = true;
				return;	
				
		}//end if statement	
		
		Label temp; 
		Label tempEnd;
		
			
			int currentUsage = Character.getNumericValue(loopNestStack.charAt(0));
			switch(currentUsage){
				case 1: {
					temp = startOfloop0;
					tempEnd = endOfloop0;
					break;
				}
				case 2: {
					temp = startOfloop1;
					tempEnd = endOfloop1;
					break;
				}
				case 3: {
					temp = startOfloop2;
					tempEnd = endOfloop2;
					break;
				}
				case 4: {
					temp = startOfloop3;
					tempEnd = endOfloop3;
					break;
				}
				case 5: {
					temp = startOfloop4;
					tempEnd = endOfloop4;
					break;
				}
				case 6: {
					temp = startOfloop5;
					tempEnd = endOfloop5;
					break;
				}
				case 7: { 
					temp = startOfloop6;
					tempEnd = endOfloop6;
					break;
				}
				case 8: {
					temp = startOfloop7;
					tempEnd = endOfloop7;
					break;
				}
				case 9: { 
					temp = startOfloop8;
					tempEnd = endOfloop8;
					break;
				
				}
				case 10: { 
					temp = startOfloop9;
					tempEnd = endOfloop9;
					break;
				}
				default: {
				
					System.out.println("\n\n------------------------------------------");
					System.out.println("COMPILER ERROR");
					System.out.println("------------------------------------------");
				
					System.out.println("jump label failure for loop at enter!");
					
					exit = true;
					return;
				}		
			}//end switch statement
		
		//ASM bytecode will visit label holding temporary variable
		mainVisitor.visitLabel(temp);
		
		loopOp1 = ctx.getChild(1).getText();
		if(SymbolTable.containsKey(loopOp1)){
			var1 = SymbolTable.get(loopOp1);
			
			if(!var1.valueSet||var1.variableType.equalsIgnoreCase(STR)){
				
				System.out.println("\n\n------------------------------------------");
				System.out.println("COMPILER ERROR");
				System.out.println("------------------------------------------");
			
				System.out.println("Comparison failed!");
				System.out.println("ID: " + loopOp1 + " value has not been set, or ID is a String!");
				
				exit = true;
				return;	
				
			}//end nested if statement
			
			operator1 = var1.memLoc;
			mainVisitor.visitIntInsn(ILOAD, operator1);
			
		} else {
			try{
            			
            			loopOperator1 = Integer.valueOf(loopOp1);
            			mainVisitor.visitIntInsn(SIPUSH, loopOperator1);
            			
       		     } catch(NumberFormatException e){
       		     
        			//System.out.println(e.getMessage());
        			System.out.println("\n\n------------------------------------------");
				System.out.println("COMPILER ERROR");
				System.out.println("------------------------------------------");
			
				System.out.println("Comparison failed!");
				System.out.println("ID: " + loopOp1 + " does not exist, is not assigned a value or is not a valid 					integer.");
				
				exit = true;
				return;
        			
        		     }//end try catch clause
		
		}//end if else statement
		
		loopOp2 = ctx.getChild(3).getText();
		if(SymbolTable.containsKey(loopOp2)){
			var2 = SymbolTable.get(loopOp2);

			if(!var2.valueSet || var2.variableType.equalsIgnoreCase(STR)){
				
				System.out.println("\n\n------------------------------------------");
				System.out.println("COMPILER ERROR");
				System.out.println("------------------------------------------");
			
				System.out.println("Comparison failed!");
				System.out.println("ID: " + loopOp1 + " value has not been set, or ID is a String!");
				
				exit = true;
				return;	
			}//end nested if statement
			
			operator2 = var2.memLoc;
			mainVisitor.visitIntInsn(ILOAD, operator2);
		} else {
		
			  try{
            			
            			loopOperator2 = Integer.valueOf(loopOp2);
            			mainVisitor.visitIntInsn(SIPUSH, loopOperator2);	
       		     } catch(NumberFormatException e){
        			System.out.println("\n\n------------------------------------------");
				System.out.println("COMPILER ERROR");
				System.out.println("------------------------------------------");
			
				System.out.println("Comparison failed!");
				System.out.println("ID: " + loopOp2 + " does not exist, is not assigned a value or is not a valid 					integer.");
				
				exit = true;
				return;
        			
        		     }	//end try catch clause
		}//end if else statement		
			
		loopCompSymbol = ctx.getChild(2).getChild(0).getText();
	
		if(loopCompSymbol.equals(">")){
				
				//System.out.println("compString equals >: " +compString.equals(">"));
				mainVisitor.visitJumpInsn(IF_ICMPLE, tempEnd);
				//compString = "IF_ICMPLE"; 
				//operation = ">"+operation;
				
			} else if(decCompSymbol.equals("<")){
				
				mainVisitor.visitJumpInsn(IF_ICMPGE,tempEnd);
				//compString = "IF_ICMPGE"; 
				//operation = "<"+operation;
			
			} else if(decCompSymbol.equals("<>")){
			
				mainVisitor.visitJumpInsn(IF_ICMPEQ, tempEnd);
				//compString = "IF_ICMPEQ";
				//operation = "<>"+operation;
			
			} else if(decCompSymbol.equals("=")){
			
				mainVisitor.visitJumpInsn(IF_ICMPNE, tempEnd);
				//compString = "IF_ICMPNE";	
				//operation = "="+operation;
			
			}//end if else statements
			
			System.out.println("-------------------------------------------------------");
			System.out.println("Visit Start of loop label = " + temp);
			System.out.println("IFICMP, " + tempEnd);
			System.out.println("-------------------------------------------------------");
			
	}//end enterLoop method
	@Override 
	public void exitLoop(KnightCodeParser.LoopContext ctx){ 
	
		if(exit){
			return;
		}//end if statement
				
			Label temp;
			Label temper;
			int currentUsage = Character.getNumericValue(loopNestStack.charAt(0));
			
			switch(currentUsage) {
		
				case 1: {
					temp = endOfloop0;
					temper = startOfloop0;
					break;
				}
				case 2: {
					temp = endOfloop1;
					temper = startOfloop1;
					break;
				}
				case 3: {
					temp = endOfloop2;
					temper = startOfloop2;
					break;
				}
				case 4: {
					temp = endOfloop3;
					temper = startOfloop3;
					break;
				}
				case 5: {
					temp = endOfloop4;
					temper = startOfloop4;
					break;
				}
				case 6: {
					temp = endOfloop5;
					temper = startOfloop5;
					break;
				}
				case 7: { 
					temp = endOfloop6;
					temper = startOfloop6;
					break;
				}
				case 8: {
					temp = endOfloop7;
					temper = startOfloop7;
					break;
				}
				case 9: {
					temp = endOfloop8;
					temper = startOfloop8;
					break;
				}
				case 10: { 
					temp = endOfloop9;
					temper = startOfloop9;
					break;
				}
				default: {
	
					System.out.println("\n\n------------------------------------------");
					System.out.println("COMPILER ERROR");
					System.out.println("------------------------------------------");
				
					System.out.println("jump label failure for if-else statement at exit");
					
					exit = true;
					return;
				}	
					
			}//end switch statement
			
		
		System.out.println("-------------------------------------------------------");
		System.out.println("GOTO = " + temper);
		System.out.println("Visit end label loop = " + temp);
		System.out.println("-------------------------------------------------------");
		
		//ASM bytecode to visit jump isntance and label with temps.
		mainVisitor.visitJumpInsn(GOTO,temper);
		mainVisitor.visitLabel(temp);
		
		if(loopNestStack.length() != 0)
			loopNestStack = loopNestStack.substring(1);
		
		System.out.println("Current stack = " + loopNestStack);	
			
		if(elseCount1 > 0){
			
			if(ifCount1 == 1){
				System.out.println("ifCount1 = " + ifCount1);
				 
				Label tempEnd;
		 		currentUsage = Character.getNumericValue(decNestStack.charAt(0));
				switch(currentUsage){
					case 1: {
						tempEnd = endDecLab0;
						temper = startOfElse0;						
						break;
					}
					case 2: {
						tempEnd = endDecLab1;
						temper = startOfElse1;
						break;
					}
					case 3: {
						tempEnd = endDecLab2;
						temper = startOfElse2;						
						break;
					}
					case 4: {
						tempEnd = endDecLab3;
						temper = startOfElse3;
						break;
					}
					case 5: {
						tempEnd = endDecLab4;
						temper = startOfElse4;
						break;
					}
					case 6: {						
						tempEnd = endDecLab5;
						temper = startOfElse5;
						break;
					}
					case 7: { 						
						tempEnd = endDecLab6;
						temper = startOfElse6;
						break;
					}
					case 8: {
						tempEnd = endDecLab7;
						temper = startOfElse7;
						break;
					}
					case 9: { 
						tempEnd = endDecLab8;
						temper = startOfElse8;
						break;
					
					}
					case 10: { 
						tempEnd = endDecLab9;
						temper = startOfElse9;
						break;
					}
					default: {
					
						System.out.println("\n\n------------------------------------------");
						System.out.println("COMPILER ERROR");
						System.out.println("------------------------------------------");
					
						System.out.println("jump label failure for if-else statement in print!");
						
						exit = true;
						return;
					}	
						
				}//end switch statement
				
				System.out.println("-------------------------------------------------------");	
				System.out.println("GOTO, end label= " + tempEnd);
				System.out.println("Visit startOfElse Label= " + temper);
				System.out.println("-------------------------------------------------------");
	
				mainVisitor.visitJumpInsn(GOTO, tempEnd);
				mainVisitor.visitLabel(temper);
				
				System.out.println("Current stack = " + decNestStack);
			
			} else {
				//System.out.println("DID THIS NOT HAPPEN???");
				//ifCount1--;
			}//end nested if else statement
			
		
		}//end if statement
		
		if(ifCount1 > 0){
			ifCount1--;
		}//end if statement
		
		System.out.println("ifCount = " + ifCount1);	
				
		System.out.println("Exit loop");
		
	}//end exitLoop method
	
	/**
	 * Prints context string. Used for debugging purposes
	 * @param ctx
	 */
	private void printContext(String ctx){
		System.out.println(ctx);
	}//end printContext
	
	@Override 
	public void enterEveryRule(ParserRuleContext ctx){
	 	if(debug) 
	 		printContext(ctx.getText()); 
	}//end enterEveryRule

}//end class

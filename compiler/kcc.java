package knightcodecompiler;

import java.util.Hashmap;

import org.antlr.v4.runtime.ParserRuleContext; // need to debug every rule
import lexparse.*; //classes for lexer parser

//AMS files
import org.objectweb.asm.*;  //classes for generating bytecode
import org.objectweb.asm.Opcodes; //Explicit import for ASM bytecode constants

/**
 * Description:
 * @author Jaylon Kiper
 * @author Elizabeth Fultz
 * @version 1.0
 * Programming Project Four
 * CS322 - Compiler Construction
 * Fall 2021
 */
public class kcc extends KnightCodeBaseListener {


	private ClassWriter cw;  //class level ClassWriter 
	private MethodVisitor mainVisitor; //class level MethodVisitor
	private String programName; //name of the class and the output file (used by ASM)
	private boolean debug; //flag to indicate debug status

	public kcc(String programName, boolean debug){
	       
		this.programName = programName;
		this.debug = debug;

	}//end constructor

	public void setupClass(){
		
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
		//Use global MethodVisitor to finish writing the bytecode and write the binary file.
		mainVisitor.visitInsn(Opcodes.RETURN);
		mainVisitor.visitMaxs(3, 3);
		mainVisitor.visitEnd();

		cw.visitEnd();

        byte[] b = cw.toByteArray();

        Utilities.writeFile(b,this.programName+".class");
        
        System.out.println("Done!");

	}//end closeClass
	
	public void enterFile(KnightCodeParser.FileContext ctx) { 
	
		System.out.println("Enter file rule for first time");
		setupClass();
	
	}//end enterFile
	
	public void exitFile(KnightCodeParser.FileContext ctx) { 
	
		System.out.println("Leaving file rule. . .");
		closeClass();
	
	}//end exitFile
	
	/**
	 * Prints context string. Used for debugging purposes
	 * @param ctx
	 */
	private void printContext(String ctx){
		System.out.println(ctx);
	}//end printContext

	@Override 
	public void enterEveryRule(ParserRuleContext ctx){ 
		if(debug) printContext(ctx.getText());
	}//end enterEveryRule
	
	@Override 
	public void enterStat(KnightCodeParser.StatContext ctx) { 
	
		String output = ctx.getChild(1).getText();
		//output = output.substring(5,output.length());
		mainVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		mainVisitor.visitLdcInsn(output);
		mainVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",  "println", "(Ljava/lang/String;)V", false);
	
	}//end enterStat
	
	@Override public void exitStat(KnightCodeParser.StatContext ctx) { }
    
}//end class
 

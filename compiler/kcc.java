package compiler;

//ANTLR packages
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.gui.Trees;

import lexparse.*;

import java.util.*;
import java.io.IOException;

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
public class kcc{

  public static void main(String[] args){
  
        CharStream input;
        KnightCodeLexer lexer;
        CommonTokenStream tokens;
        KnightCodeParser parser;

	String file;
	String output;
	
	//These statement c
	if(args.length == 2){
	
		file = args[0];
		output = args[1];
		
	} else if(args.length == 1){ 
	
		file = args[0];
		output = "output/result";
	
	} else {
	
		file = "tests/program1.kc";
		output = "output/result";
	
	}//end if else
	
	//Clause statement gets input from user to complie and parse through from myListener.
        try{
        
            input = CharStreams.fromFileName(file);//get the input
            lexer = new KnightCodeLexer(input); //create the lexer
            tokens = new CommonTokenStream(lexer);//create the token stream
            parser = new KnightCodeParser(tokens);//create the parser
        
       
            //set the start location of the parser
            ParseTree tree = parser.file(); 
            
            
            //Walk the tree using the myListener class
            myListener listener = new myListener(output);
	    ParseTreeWalker walker = new ParseTreeWalker();
	    
	    walker.walk(listener, tree);
            
        
        }//end try clasue
        
        catch(IOException e){
            System.out.println(e.getMessage());
        }//end catch clause
        
    }//end main
    
}//end class
 

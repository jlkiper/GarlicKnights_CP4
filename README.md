# GarlicKnights_CP4

Project Summary:
The purpose of this project is to write a small compiler for a toy language named KnightCode using ANTLR(lexer/parser) and ASM(bytecode manipulation) libraries. This project bulds on using the ASM library to write bytecode directly to a class file. The ANTLR library will provide the classes and mechanisms to build/walk a parse tree. Our goal was to extend those classes/mechanisms to write a bytecode class file based on KnightCode that can be executed with Java. One java file called myListener holds the symbol table to store any data the listener needs to use and methods to extend the appropriate ANTLR classes to build a compiler using the lexer and parser ANTLR generates. Another java file called kcc contains the main method to kick off the compiler.

Team Members:
Jaylon Kiper
,Elizabeth Fultz

Instructions:

1.Change directory to folder named "GarlicKnights_CP4"
-cd GarlicKnights_CP4

2.Apache command to build lexparse grammar of project by generating the .java files.
-ant build-grammar

3.Apache command to compile lexparse grammar or project by compiling the ANTLR .java source files.
-ant compile-grammar

4.Apahce command to compile compiler files by compiling the ANTLR .java source files.
-ant compile

5.Java command to build parse tree based on knightcode.
-java compiler/CompilerTest "tests/program1.kc" or java compiler/CompilerTest "tests/knight1.kc"

6.Java command to run knightcode files via the compiler to output folder.
-java compiler/kcc "tests/program1.kc" or java compiler/kcc "tests/knight1.kc"

7.Java command to run knightcode file in output folder.
-java output/reuslt

8.Java command to decompile class file into ASM bytecode.
-javap -p -v output/result

9.Apache command for cleaning the lexparse directory.
-ant clean-grammar

10.Apache command for cleaning the code directories.
-ant clean

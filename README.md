# expression-language
This program is a solution to a problem I was once asked to write.

Once downloaded, open your terminal and change directory to the downloaded source directory:
%cd expression-language
Compile the code:
%javac calculator/*.java
Run it:
%java calculator.Main "add(1,2)"
%3

Write a calculator program in Java that evaluates expressions in a very simple integer expression language.   
The program takes an input on the command line, computes the result, and prints it to the console. 

For example:
% java calculator.Main “mult(2, 2)”
% 4

Few more examples:
Input                                                  output
add(1, 2)                                                 3
add(1, mult(2, 3))                                        7
mult(add(2, 2), div(9, 3))                               12
let(a, 5, add(a, a))                                     10
let(a, 5, let(b, mult(a, 10), add(b, a)))                55
let(a, let(b, 10, add(b, b)), let(b, 20, add(a, b)))     40
 
An expression is one of the of the following:
 - Numbers: integers between Integer.MIN_VALUE and Integer.MAX_VALUE
 - Variables: strings of characters, where each character is one of a-z, A-Z
 - Arithmetic functions: add, sub, mult, div, each taking two arbitrary expressions as arguments. 
   In other words, each argument may be any of the expressions on this list.
 - A “let” operator for assigning values to variables:
   let(<variable name>, <value expression>, <expression where variable is used>)

As with arithmetic functions, the value expression and the expression where the variable is used
may be an arbitrary expression from this list. 

Please submit what you would consider testable and maintainable production code. 
If the statement of the problem is unclear, feel free to make assumptions,
but please state your assumptions in the solution.  

Please do not use third-party frameworks and stick to core Java plus standard (java.*) packages.

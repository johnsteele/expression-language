# expression-language
This program is a solution to a problem I was once asked to write.

Requirements: Java 8

Once downloaded, open your terminal and change directory to the downloaded source directory:
<p>
<b>%cd expression-language</b><br>
<br>
Compile the code:<br>
<b>%javac calculator/*.java</b><br>
<br>
Run it:<br>
<b>%java calculator.Main "add(1,2)"</b><br>
<b>%3</b><br>
</p>
<p>
<hr/>
<h3>This is the problem to be solved</h3>
Write a calculator program in Java that evaluates expressions in a very simple integer expression language.
</p>
<p>
The program takes an input on the command line, computes the result, and prints it to the console. 
</p>
For example:<br>
<p>
<b>% java calculator.Main “mult(2, 2)”</b><br>
<b>% 4</b>
</p>

<b>Few more examples:</b>

<table style="width:100%">
  <tr>
    <th>Input</th>
    <th>Output</th>
  </tr>
  <tr>
    <td>add(1, 2)</td>
    <td>3</td>
  </tr>
  <tr>
    <td>add(1, mult(2, 3))</td>
    <td>7</td>
  </tr>
  <tr>
    <td>mult(add(2, 2), div(9, 3))</td>
    <td>12</td>
  </tr>
  <tr>
    <td>let(a, 5, add(a, a))</td>
    <td>10</td>
  </tr>
  <tr>
    <td>let(a, 5, let(b, mult(a, 10), add(b, a)))</td>
    <td>55</td>
  </tr>
  <tr>
    <td>let(a, let(b, 10, add(b, b)), let(b, 20, add(a, b)))</td>
    <td>40</td>
  </tr>
</table>


<h3>An expression is one of the of the following:</h3>
<ul>
 <li>Numbers: integers between Integer.MIN_VALUE and Integer.MAX_VALUE</li>
 <li>Variables: strings of characters, where each character is one of a-z, A-Z</li>
 <li>Arithmetic functions: add, sub, mult, div, each taking two arbitrary expressions as arguments.<br>
    In other words, each argument may be any of the expressions on this list.</li>
 <li>A “let” operator for assigning values to variables:<br>
    <b>let(&lt;variable name&gt;, &lt;value expression&gt;, &lt;expression where variable is used&gt;)</b></li>
</ul>
<p>
As with arithmetic functions, the value expression and the expression <br> where the variable is used
may be an arbitrary expression from this list. 
</p>

<p>
Please submit what you would consider testable and maintainable production code.<br>
If the statement of the problem is unclear, feel free to make assumptions,<br>
but please state your assumptions in the solution.  
</p>
<p>
Please do not use third-party frameworks and stick to core Java plus standard (java.*) packages.
</p>

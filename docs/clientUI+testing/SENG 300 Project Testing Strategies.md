SENG 300 Project Testing Strategies  
Created By: Jarod Rideout 30046597

Note: All tests for this project will follow the unit testing format as taught in CPSC233. All tests will be run using IntelliJ JUnit tests.

Examples related to this document were obtained through an external source.1

Guidelines and Recommendations:

1. Separation  
     
   All test classes should be *written, executed, and maintained separately from production code*. 

   

To achieve this all tests will be saved under CLASS\_NAME\\src\\test folder.

2. Naming  
     
   All unit tests should have a unique name. These names should be descriptive of what you are testing AND what the expected result is. They can follow the format:  
   	  
   testActionPerformedExpectedResult  
     
   Ex: testCalculateAreaWithGeneralDoubleValueRadiusThatReturnsAreaInDouble  
     
   OR   
     
   givenValue\_whenActionPerformed\_thenReturnExpectedResult  
     
   Ex: givenDoubleMaxValueAsRadius\_whenCalculateArea\_thenReturnAreaAsInfinity  
     
   In short, anyone should be able to understand what is being tested from the test name.  
     
3. Comparing Outcome to what is Expected  
     
   Make sure to use assertEquals(expectedResult, actualResult), with values defined / calculated within the scope of your function.  
     
   Ex:  
   @Test   
   public void givenRadius\_whenCalculateArea\_thenReturnArea() {  
       double actualArea \= Circle.calculateArea(1d);  
       double expectedArea \= 3.141592653589793;  
       Assert.assertEquals(expectedArea, actualArea);   
   }  
     
4. Simplicity  
     
   Try to follow the KISS principle, keep the test as simple as possible. This means testing just one specific scenario in each test.  
     
   We won’t be able to test for every outcome, try to aim for 80% coverage of test cases.  
     
   Try to think of one overarching scenario that we can break up into multiple tests.  
     
   Ex: Chess player has a given game state where they can make multiple moves. Break down a move that takes opponents piece. Test players piece replaces opponents piece successfully, test if player can move piece through opponents piece, test if player can move piece illegally (pawn more than one space ahead or attack to side), etc. Lots of specific tests in one scenario.

Citation:

\[1\] Best practices for unit testing in Java | Baeldung, https://www.baeldung.com/java-unit-testing-best-practices (accessed Feb. 10, 2026).
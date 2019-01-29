# StringAccumulator
This is a simple utility to parse a string, which has some numbers (integers) embedded. The string may have an optional set of delimiters  present in the beginning. The task is to extract the positive integers based on the set of delimiters (if no delimiters are specified in the beginning of the string, then use ',' and '\n'  characters as default delimiters), and find their sum.

**LOGIC:**

i) Create a set of delimiters. This should contain all the delimiters present in the optional custom delimiter string (present in the beginning of the input string). If no such delimiter string is present, then put ',' and '\n'.

ii) Iterate through the main string and recursively split it using each member of above delimiter set. If a positive integer is produced after splitting then use it for sum Total calculation. If integers are not produced upon splitting, the split again using rest of the delimiters. Do this until, there is nothing left to split.


**ASSUMPTIONS:**

These are following assumptions made while designing this String accumulator.

i) If delimiters are changed via providing a set of delimiters in the first line, then comma and newlines would not be considered as valid delimiters.(If user still wants the comma and newlines to stay as valid delimiters, then it can be easily done via adding them to the list of valid Delimiters formed in the middle of add() method.

ii) Refer to the problem statement, Requirement-1 says - "The method can take 0, 1 or 2 numbers", While Requirement-2 says - "Allow the add method to handle an unknown amount of numbers."
Both of the above requirements are supported by using an instance Variable maxCountAllowed. If it is 0, then any amount of numbers can be parsed. If it is nonZero 	then only that much amount of numbers can be parsed.

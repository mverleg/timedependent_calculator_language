
Time-dependent calculator language
=======================================

The aim of this project is to "compile" a calculation language that works as follows:

* There are a number of variables that change in discrete time steps
* Variables may depend on current values of other variables
* Variables may depend of the previous or next values of themselves
* Variables may depend of the previous or next values other variables

One could see this as a spreadsheet, with each row being a timestep and each column a variable.

There are several aims:

* Reject, with a clear error message, any programs that cannot be computed linearly (i.e. where variables depend on the current value).
* For valid programs, find the correct evaluation order of the variables.
* For as many variables as possible, calculate the whole time series of the variable independently. This will help parallelize it using CPU SIMD, or GPUs.

Eventually there's also code generation etc, but the evaluation order is the focus for now.

Note that:

* There are special cases at the edges, no worry for now about out-of-bounds.
* This analysis is done statically, so runtime if-statements are treated as 'is dependent', not 'is maybe dependent'. This could mean that a program seems to be cyclic and is rejected, even though in practise the if-statements prevent a cycle.

Extra ideas:

* Cycles are the variables that cannot be calculated as a whole time series. It may be possible to speed things up by taking all variables inside cycles, then at the AST level of the variable, extracting all branches that don't contain references to cycle members (but whose parent does). These can then be turned into separate variables, and dividing into cycles again should put those parts of the formulas outside the cycle (and thus allow more optimization).

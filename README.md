This is the implementation of test task for the project **Formal verification framework for Kotlin**.

It builds symbolic execution tree in two stages.
First you need to build NextNodeTree from AST using buildNextNodeTree() function.
This is data structure that stores next nodes interpreter must run for each node.
This form is easily transformed to the symbolic execution tree,
which is done by buildSymbolicExecutionTree() method of NextNodeTree.
There is an example in Main.kt which you can run.
class SymbolicExecutionTree (
    val path : List<Expr>,
    val state : Map<String, Expr>,
    val node : Expr? = null,
    val children : List<SymbolicExecutionTree> = emptyList()
    )

fun NextNodeTree.buildSymbolicExecutionTree () : SymbolicExecutionTree {
    val initialState = freeVariables.associateWith { s -> Symbol(s + "_symbol") }
    return buildSymbolic(emptyList(), initialState, firstNode)
}

private fun NextNodeTree.buildSymbolic(
    currentPath: List<Expr>,
    currentState : Map<String, Expr>,
    expr: Expr?
) : SymbolicExecutionTree {
    when (expr) {
        null -> {
            return SymbolicExecutionTree(currentPath, currentState)
        }
        is If -> {
            val condExpr = expr.cond.substitution(currentState)
            val trueBranch = buildSymbolic(currentPath + condExpr.makeCondition(), currentState, trueNode[expr])
            val falseBranch = buildSymbolic(currentPath + condExpr.makeNegation(), currentState, falseNode[expr])
            return SymbolicExecutionTree(currentPath, currentState, expr, listOf(trueBranch, falseBranch))
        }
        is Let -> {
            val assignExpr = expr.newValue.substitution(currentState)
            val nextBranch = buildSymbolic(
                currentPath,
                currentState + mapOf(Pair(expr.variable.name, assignExpr)),
                nextNode[expr]
            )
            return SymbolicExecutionTree(currentPath, currentState, expr, listOf(nextBranch))
        }
        else -> {
            throw Exception("node is invalid")
        }
    }
}
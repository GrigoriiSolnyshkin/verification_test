class SymbolicExecutionTree (
    val path : List<PrimitiveExpr>,
    val state : Map<String, PrimitiveExpr>,
    val node : Expr? = null,
    val children : List<SymbolicExecutionTree> = emptyList()
    ) {
    fun prettyPrint() : String {
        return print(0).toString()
    }

    private fun printMetaInfo(padding: Int) : StringBuilder{
        val strbld = StringBuilder()
        strbld.appendLine("path:".addIndent(padding))
        path.forEach{
            strbld.appendLine(it.prettyPrint().addIndent(padding + 2))
        }
        strbld.appendLine("state:".addIndent(padding))
        state.forEach {
                (k, v) -> strbld.appendLine("$k = ${v.prettyPrint()}".addIndent(padding + 2))
        }
        return strbld
    }
    private fun print(padding: Int) : StringBuilder {
        when (node) {
            null -> {
                val strbld = StringBuilder()
                strbld.appendLine("node: final")
                strbld.append(printMetaInfo(padding))
                return strbld
            }
            is If -> {
                val strbld = StringBuilder()
                strbld.appendLine("node: if (${node.cond.prettyPrint()})")
                strbld.append(printMetaInfo(padding))
                strbld.append("-true-->".addIndent(padding))
                strbld.append(children[0].print(padding + 8))
                strbld.append("-false->".addIndent(padding))
                strbld.append(children[1].print(padding + 8))
                return strbld
            }
            is Let -> {
                val strbld = StringBuilder()
                strbld.appendLine("node: ${node.variable.name} = ${node.newValue.prettyPrint()}")
                strbld.append(printMetaInfo(padding))
                strbld.append("------->".addIndent(padding))
                strbld.append(children[0].print(padding + 8))
                return strbld
            }
            else -> {
                throw Exception("invalid node")
            }
        }
    }
}

fun NextNodeTree.buildSymbolicExecutionTree () : SymbolicExecutionTree {
    val initialState = freeVariables.associateWith { s -> Symbol(s + "_symbol") }
    return buildSymbolic(emptyList(), initialState, firstNode)
}

private fun NextNodeTree.buildSymbolic(
    currentPath: List<PrimitiveExpr>,
    currentState : Map<String, PrimitiveExpr>,
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


fun String.addIndent(pad : Int) : String {
    return "".padStart(pad) + this
}
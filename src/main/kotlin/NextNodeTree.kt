
/*
saves info about what node is next in the order of execution
and therefore is very efficient for calculating other trees:
firstNode: node to start with in the Tree
nextNode: map of next nodes for assignments
trueNode: map of next nodes for if branches
falseNode: map of next nodes for else branches

nextMissing: assignments that were not matched
trueMissing: if branches that were not matched
falseMissing: else branches that were not matched
 */
class NextNodeTree(val firstNode: Expr? = null,
                   val nextNode: Map<Let, Expr> = emptyMap(),
                   val trueNode: Map<If, Expr> = emptyMap(),
                   val falseNode: Map<If, Expr> = emptyMap(),
                   val nextMissing: Set<Let> = emptySet(),
                   val trueMissing: Set<If> = emptySet(),
                   val falseMissing: Set<If> = emptySet()

) {
    fun isEmpty() : Boolean { return firstNode == null }

    /*
    returns free variables for given AST (variables that can be used without initialization - they need symbolic value)
     */
    val freeVariables : Set<String>
        get() {
            return freeVariablesStep(firstNode, emptySet())
        }

    private fun freeVariablesStep(expr: Expr?, protectedVariables: Set<String>) : Set<String> {
        when(expr) {
            null -> {
                return emptySet()
            }
            is Let -> {
                return expr.newValue.setOfVariables() - protectedVariables +
                        freeVariablesStep(nextNode[expr], protectedVariables + expr.variable.name)
            }
            is If -> {

                return freeVariablesStep(trueNode[expr], protectedVariables) +
                        freeVariablesStep(falseNode[expr], protectedVariables) +
                        (expr.cond.setOfVariables() - protectedVariables)
            }
            else -> {
                throw Exception("node is invalid")
            }

        }
    }
}


fun buildNextNodeTree (expr: Expr?) : NextNodeTree {
    return when (expr) {
        null -> {
            NextNodeTree()
        }
        is Let -> {
            NextNodeTree(expr, emptyMap(), emptyMap(), emptyMap(), setOf(expr))
        }

        is If -> {
            val trueBranch = buildNextNodeTree(expr.thenExpr)
            val falseBranch = buildNextNodeTree(expr.elseExpr)
            NextNodeTree(
                firstNode = expr,
                nextNode = falseBranch.nextNode + trueBranch.nextNode,
                trueNode = trueBranch.trueNode + falseBranch.trueNode + if (trueBranch.firstNode != null)
                    mapOf(Pair(expr, trueBranch.firstNode)) else emptyMap(),
                falseNode = trueBranch.falseNode + falseBranch.falseNode + if (falseBranch.firstNode != null)
                    mapOf(Pair(expr, falseBranch.firstNode)) else emptyMap(),
                nextMissing = falseBranch.nextMissing + trueBranch.nextMissing,
                trueMissing = trueBranch.trueMissing + falseBranch.trueMissing + if (trueBranch.firstNode == null)
                    setOf(expr) else emptySet(),
                falseMissing = trueBranch.falseMissing + falseBranch.falseMissing + if (falseBranch.firstNode == null)
                    setOf(expr) else emptySet()

            )
        }

        else -> {
            val block = expr as Block
            val builders = block.exprs.map{buildNextNodeTree(it)}
            return builders.fold (NextNodeTree()) { acc, it -> sequence(acc, it) }
        }
    }
}

fun sequence(fst: NextNodeTree, snd: NextNodeTree) : NextNodeTree{
    if (fst.isEmpty()) return snd
    if (snd.firstNode == null) return fst

    return NextNodeTree(
        fst.firstNode,
        fst.nextNode + snd.nextNode + fst.nextMissing.associateWith { snd.firstNode },
        fst.trueNode + snd.trueNode + fst.trueMissing.associateWith { snd.firstNode },
        fst.falseNode + snd.falseNode + fst.falseMissing.associateWith { snd.firstNode },
        snd.nextMissing,
        snd.trueMissing,
        snd.falseMissing
    )

}
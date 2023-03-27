sealed class Expr

sealed class PrimitiveExpr: Expr() {
    // for simple expressions (not blocks, assigns and ifs)
    // returns the set of variables it contains
    open fun setOfVariables(): Set<String> {
        return emptySet()
    }

    // for simple expressions changes variables to their values
    abstract fun substitution(mapping: Map<String, PrimitiveExpr>) : PrimitiveExpr

    // makes a conditional value from simple expression
    open fun makeCondition(): PrimitiveExpr {
        return NEq(this, Const(0))
    }


    fun makeNegation() : PrimitiveExpr {
        return when (val cond = makeCondition()) {
            is Eq -> {
                NEq(cond.left, cond.right)
            }

            is NEq -> {
                Eq(cond.left, cond.right)
            }

            else -> {
                throw Exception("impossible to make negation")
            }
        }
    }

    abstract fun prettyPrint() : String
}

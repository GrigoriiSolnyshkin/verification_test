import kotlin.Exception

sealed class Expr {

    // for simple expressions (not blocks, assigns and ifs)
    // returns the set of variables it contains
    open fun setOfVariables(): Set<String> {
        return emptySet()
    }

    // for simple expressions changes variables to their values
    abstract fun substitution(mapping: Map<String, Expr>) : Expr

    // makes a conditional value from simple expression
    open fun makeCondition(): Expr {
        return NEq(this, Const(0))
    }

    fun makeNegation() : Expr {
        val cond = makeCondition()
        when (cond) {
            is Eq -> {
                return NEq(cond.left, cond.right)
            }
            is NEq -> {
                return Eq(cond.left, cond.right)
            }
            else -> {
                throw Exception("impossible to make negation")
            }
        }
    }
}

class Block(vararg val exprs: Expr) : Expr() {
    override fun setOfVariables(): Set<String> {
        throw Exception("block is comlpex node")
    }

    override fun substitution(mapping: Map<String, Expr>): Expr {
        throw Exception("block is comlpex node")
    }

    override fun makeCondition(): Expr {
        throw Exception("block is comlpex node")
    }
}
class Const(val constValue: Int) : Expr() {
    override fun substitution(mapping: Map<String, Expr>): Expr {
        return Const(constValue)
    }
}

class Var(val name: String) : Expr() {
    override fun setOfVariables(): Set<String> {
        return setOf(name)
    }

    override fun substitution(mapping: Map<String, Expr>): Expr {
        return mapping[name]!!
    }
}

class Let(val variable: Var, val newValue: Expr) : Expr() {
    override fun setOfVariables(): Set<String> {
        throw Exception("let is complex node")
    }

    override fun substitution(mapping: Map<String, Expr>): Expr {
        throw Exception("let is complex node")
    }

    override fun makeCondition(): Expr {
        throw Exception("let is complex node")
    }
}

class Eq(val left: Expr, val right: Expr) : Expr() {
    override fun setOfVariables(): Set<String> {
        return left.setOfVariables() + right.setOfVariables()
    }

    override fun substitution(mapping: Map<String, Expr>): Expr {
        return Eq(left.substitution(mapping), right.substitution(mapping))
    }

    override fun makeCondition(): Expr {
        return this
    }
}
class NEq(val left: Expr, val right: Expr) : Expr() {
    override fun setOfVariables(): Set<String> {
        return left.setOfVariables() + right.setOfVariables()
    }
    override fun substitution(mapping: Map<String, Expr>): Expr {
        return NEq(left.substitution(mapping), right.substitution(mapping))
    }

    override fun makeCondition(): Expr {
        return this
    }
}
class If(val cond: Expr, val thenExpr : Expr, val elseExpr : Expr? = null) : Expr() {
    override fun setOfVariables(): Set<String> {
        throw Exception("if is complex node")
    }

    override fun substitution(mapping: Map<String, Expr>): Expr {
        throw Exception("if is complex node")
    }

    override fun makeCondition(): Expr {
        throw Exception("if is complex node")
    }

}
class Plus(val left: Expr, val right:Expr) : Expr() {
    override fun setOfVariables(): Set<String> {
        return left.setOfVariables() + right.setOfVariables()
    }
    override fun substitution(mapping: Map<String, Expr>): Expr {
        return Plus(left.substitution(mapping), right.substitution(mapping))
    }
}
class Minus(val left:Expr, val right: Expr) : Expr() {
    override fun setOfVariables(): Set<String> {
        return left.setOfVariables() + right.setOfVariables()
    }

    override fun substitution(mapping: Map<String, Expr>): Expr {
        return Minus(left.substitution(mapping), right.substitution(mapping))
    }
}
class Mul(val left: Expr, val right: Expr) : Expr() {
    override fun setOfVariables(): Set<String> {
        return left.setOfVariables() + right.setOfVariables()
    }

    override fun substitution(mapping: Map<String, Expr>): Expr {
        return Mul(left.substitution(mapping), right.substitution(mapping))
    }
}

class Symbol(val name: String) : Expr() {
    override fun substitution(mapping: Map<String, Expr>): Expr {
        return Symbol(name)
    }
}

import kotlin.Exception

class Block(vararg val exprs: Expr) : Expr()

class Const(val constValue: Int) : PrimitiveExpr() {
    override fun substitution(mapping: Map<String, PrimitiveExpr>): PrimitiveExpr {
        return Const(constValue)
    }

    override fun prettyPrint(): String {
        return constValue.toString()
    }
}

class Var(val name: String) : PrimitiveExpr() {
    override fun setOfVariables(): Set<String> {
        return setOf(name)
    }

    override fun substitution(mapping: Map<String, PrimitiveExpr>): PrimitiveExpr {
        return mapping[name]!!
    }

    override fun prettyPrint(): String {
        return name
    }
}

class Let(val variable: Var, val newValue: PrimitiveExpr) : Expr()

class Eq(val left: PrimitiveExpr, val right: PrimitiveExpr) : PrimitiveExpr() {
    override fun setOfVariables(): Set<String> {
        return left.setOfVariables() + right.setOfVariables()
    }

    override fun substitution(mapping: Map<String, PrimitiveExpr>): PrimitiveExpr {
        return Eq(left.substitution(mapping), right.substitution(mapping))
    }

    override fun makeCondition(): PrimitiveExpr {
        return this
    }

    override fun prettyPrint(): String {
        return "${left.prettyPrint()} == ${right.prettyPrint()}"
    }
}
class NEq(val left: PrimitiveExpr, val right: PrimitiveExpr) : PrimitiveExpr() {
    override fun setOfVariables(): Set<String> {
        return left.setOfVariables() + right.setOfVariables()
    }
    override fun substitution(mapping: Map<String, PrimitiveExpr>): PrimitiveExpr {
        return NEq(left.substitution(mapping), right.substitution(mapping))
    }

    override fun makeCondition(): PrimitiveExpr {
        return this
    }

    override fun prettyPrint(): String {
        return "${left.prettyPrint()} != ${right.prettyPrint()}"
    }
}
class If(val cond: PrimitiveExpr, val thenExpr : Expr, val elseExpr : Expr? = null) : Expr()

class Plus(val left: PrimitiveExpr, val right: PrimitiveExpr) : PrimitiveExpr() {
    override fun setOfVariables(): Set<String> {
        return left.setOfVariables() + right.setOfVariables()
    }
    override fun substitution(mapping: Map<String, PrimitiveExpr>): PrimitiveExpr {
        return Plus(left.substitution(mapping), right.substitution(mapping))
    }

    override fun prettyPrint(): String {
        return "(${left.prettyPrint()} + ${right.prettyPrint()})"
    }
}
class Minus(val left: PrimitiveExpr, val right: PrimitiveExpr) : PrimitiveExpr() {
    override fun setOfVariables(): Set<String> {
        return left.setOfVariables() + right.setOfVariables()
    }
    override fun substitution(mapping: Map<String, PrimitiveExpr>): PrimitiveExpr {
        return Minus(left.substitution(mapping), right.substitution(mapping))
    }

    override fun prettyPrint(): String {
        return "(${left.prettyPrint()} - ${right.prettyPrint()})"
    }
}
class Mul(val left: PrimitiveExpr, val right: PrimitiveExpr) : PrimitiveExpr() {
    override fun setOfVariables(): Set<String> {
        return left.setOfVariables() + right.setOfVariables()
    }
    override fun substitution(mapping: Map<String, PrimitiveExpr>): PrimitiveExpr {
        return Mul(left.substitution(mapping), right.substitution(mapping))
    }

    override fun prettyPrint(): String {
        return "${left.prettyPrint()} * ${right.prettyPrint()}"
    }
}


class Symbol(val name: String) : PrimitiveExpr() {
    override fun substitution(mapping: Map<String, PrimitiveExpr>): PrimitiveExpr {
        return Symbol(name)
    }
    override fun prettyPrint(): String {
        return name
    }
}

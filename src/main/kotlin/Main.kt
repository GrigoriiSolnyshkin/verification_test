fun main(args: Array<String>) {
    val tree = buildNextNodeTree(
        Block(
            Let(Var("x"), Const(1)),
            Let(Var("y"), Const(0)),
            If( NEq(Var("a"), Const(0)),
                Block(
                    Let(Var("y"), Plus(Const(3), Var("x"))),
                    If( Eq(Var("b"), Const(0)),
                        Let(Var("x"), Mul(Const(2), Plus(Var("a"), Var("b")))),
                    )
                )
            ),
            If(Minus(Var("x"), Var("y")), Block(), Block())
        )
    ).buildSymbolicExecutionTree()

    println(tree.prettyPrint())
}
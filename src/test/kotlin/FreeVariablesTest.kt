import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FreeVariablesTest {

    @Test
    fun simpleTest1() {
        val tree = buildNextNodeTree(
            Let(Var("a"), Var("b"))
        )
        assertEquals(1, tree.freeVariables.size)
        assertContains(tree.freeVariables, "b")
    }

    @Test
    fun simpleTest2() {
        val tree = buildNextNodeTree(
            Let(Var("a"), Plus(Var("a"), Var("b")))
        )
        assertEquals(2, tree.freeVariables.size)
    }

    @Test
    fun noFree() {
        val tree = buildNextNodeTree(
            Block(
                Let(Var("x"), Const(0)),
                Let(Var("y"), Var("x")),
                Let(Var("z"), Mul(Var("x"), Var("y")))
            )
        )
        assertTrue(tree.freeVariables.isEmpty())
    }

    @Test
    fun oneFree() {
        val tree = buildNextNodeTree(
            Block(*(1..100).map{it -> Let(Var(it.toString()), Var((it - 1).toString()))}.toTypedArray())
        )

        assertEquals(1, tree.freeVariables.size)

    }

    @Test
    fun allFree() {
        val tree = buildNextNodeTree(
            Block(*(1..100).map{it -> Let(Var(it.toString()), Var((it + 1).toString()))}.toTypedArray())
        )

        assertEquals(100, tree.freeVariables.size)
    }

    @Test
    fun ifFree1() {
        val tree = buildNextNodeTree(
            If(
                NEq(Var("a"), Var("b")),
                Block(
                    Let(Var("c"), Var("a")),
                    Let(Var("d"), Var("b"))
                )
            )
        )

        assertEquals(2, tree.freeVariables.size)
    }

    @Test
    fun ifFree2() {
        val tree = buildNextNodeTree(
            If(
                NEq(Var("a"), Var("b")),
                Block(
                    Let(Var("c"), Var("a")),
                    Let(Var("d"), Var("b"))
                ),
                Block(
                    Let(Var("a"), Var("e")),
                    Let(Var("b"), Var("f"))
                )
            )
        )

        assertEquals(4, tree.freeVariables.size)
    }

    @Test
    fun ifTree3() {
        val tree = buildNextNodeTree(
            Block(
                If(
                    NEq(Var("a"), Var("b")),
                    Block(
                        Let(Var("c"), Var("a")),
                        Let(Var("d"), Var("b"))
                    ),
                    Let(Var("c"), Var("d"))
                ),
                Let(Var("e"), Var("c"))
            )
        )

        assertEquals(3, tree.freeVariables.size)
    }
}
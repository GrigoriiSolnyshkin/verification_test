import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NextNodeTest {

    @Test
    fun simpleTest() {
        val assn = buildNextNodeTree(
            Let(Var("x"), Var("y"))
        )
        assertTrue(assn.firstNode is Let)
        val let = assn.firstNode as Let
        assertEquals(let.variable.name, "x")
        assertTrue(let.newValue is Var)
        assertTrue(assn.nextNode.isEmpty())
        assertTrue(assn.trueNode.isEmpty())
        assertTrue(assn.falseNode.isEmpty())
        assertTrue(assn.nextMissing.size == 1 && assn.nextMissing.toList()[0].variable.name == "x")
        assertTrue(assn.falseMissing.isEmpty())
        assertTrue(assn.trueMissing.isEmpty())
    }

    @Test
    fun littleBlock() {
        val block = buildNextNodeTree(
            Block(
                Let(Var("z"), Var("t")),
                Let(Var("y"), Var("z")),
                Let(Var("x"), Var("y"))
            )
        )

        assertTrue(block.firstNode is Let)
        assertEquals(1, block.nextMissing.size)
        assertEquals(0, block.falseMissing.size)
        assertEquals(0,block.trueMissing.size)
        assertEquals(2, block.nextNode.size)
    }

    @Test
    fun bigBlock() {
        val block = buildNextNodeTree(
            Block(
                *(1..100).map {it -> Let(Var("x"), Const(it))}.toTypedArray()
            )
        )

        assertTrue(block.firstNode is Let)
        assertEquals(1, block.nextMissing.size)
        assertEquals(99, block.nextNode.size)
        block.nextNode.forEach { (it, _) -> assertEquals("x", it.variable.name) }
    }

    @Test
    fun ifBlock() {
        val ifCase = buildNextNodeTree(
            If(
                Eq(Const(1), Const(1)),
                If(
                    Eq(Const(1), Const(1)),
                    If(
                        Eq(Const(1), Const(1)),
                        Let(Var("x"), Const(42))
                    )
                )
            )
        )

        assertEquals(ifCase.falseMissing.size, 3)
        assertEquals(ifCase.trueMissing.size, 0)
        assertEquals(ifCase.nextMissing.size, 1)
        assert(ifCase.firstNode is If)
    }

    @Test
    fun emptyTest() {
        val empty = buildNextNodeTree(
            Block(
                Block(

                ),
                Block(
                    Block(
                        Block(
                            Block(),
                            Block(),
                            Block()
                        ),
                        Block()
                    )
                )
            )
        )
        assertTrue(empty.isEmpty())
    }
}
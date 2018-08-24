package me.hyuck.antdefense.map.path

import java.util.ArrayList

class PathFinder {

    var map: Array<IntArray>? = null

    internal var startX = 0
    internal var startY = 0
    internal var endX = 19
    internal var endY = 9

    private var loopLimit = -1
    private var loopCount = 0

    var movableTiles = intArrayOf(1)

    private val closedNodes: ArrayList<Node> = ArrayList()
    private val openNodes: ArrayList<Node> = ArrayList()
    var nodeOrders: ArrayList<Node> = ArrayList()

    private var startNode: Node? = null
    private var nextNode: Node? = null

    fun setStart(x: Int, y: Int) {
        startX = x
        startY = y
        startNode = Node(startX, startY)
        startNode!!.parentNode = null
        nodeOrders.add(startNode!!)
    }

    fun setEnd(x: Int, y: Int) {
        endX = x
        endY = y
    }

    fun start() {
        nextNode = searchNext(startNode!!)
    }

    fun getResult() {
        nextNode = searchNext(startNode!!)
        while( nextNode!!.x != endX || nextNode!!.y != endY) {
            nextNode = searchNext(nextNode!!)
            loopCount++
            if ( loopLimit != -1 ) {
                if ( loopCount >= loopLimit ) break
            }
        }
    }

    private fun isClosed(x: Int, y: Int): Boolean {
        closedNodes.forEach {
            if ( x == it.x && y == it.y ) {
                return true
            }
        }
        return false
    }

    private fun isMovableTile(input: Int): Boolean {
        movableTiles.forEach {
            if ( it == input ) {
                return true
            }
        }
        return false
    }

    /** 방향별 체크하여 노트 열려있는지 또는 지나친 노드(부모노드)인지 확인 */
    private fun searchDirection(node: Node, x: Int, y: Int) {
        // 부모 노드가 존재할 때
        if ( node.parentNode != null &&
                node.x + x == node.parentNode!!.x &&
                node.y + y == node.parentNode!!.y
        ) {
            // 부모 노드 닫기
            closedNodes.add(Node(node.x + x, node.y + y))
        } else {
            // 부모 노드가 아니라면
            if ( !isClosed(node.x + x, node.y + y) ) {
                // 이동 가능 타일이면
                if ( isMovableTile(map!![node.y + y][node.x + x]) ) {
                    openNodes.add(Node(node.x + x, node.y + y))
                } else {
                    closedNodes.add(Node(node.x + x, node.y + y))
                }
            }
        }
    }

    private fun searchNext(node: Node): Node? {
        // 좌
        if ( node.x > 1 ) {
            searchDirection(node, -1, 0)
        }
        // 우
        if ( node.x < map!![0].size - 1 ) {
            searchDirection(node, 1, 0)
        }
        // 상
        if ( node.y > 1 ) {
            searchDirection(node, 0, -1)
        }
        // 하
        if ( node.y < map!!.size - 1 ) {
            searchDirection(node, 0, 1)
        }

        // openNode 가중치 구하기
        openNodes.forEach {
            it.cost = Math.abs(endX - it.x) + Math.abs(endY - it.y)
        }

        // 가중치 제일 낮은거 return
        var lowest = -1
        var lowestNode: Node? = null
        openNodes.forEachIndexed { i, openNode ->
            if ( lowest == -1 ) {
                lowest = i
                lowestNode = openNode
            } else if ( lowestNode!!.cost > openNode.cost ) {
                lowest = i
                lowestNode = openNode
            }
            lowestNode!!.parentNode = node
        }

        // openNode 전부 삭제
        openNodes.clear()

        // 갈곳이 없을 때
        if ( lowestNode == null ) {
            // 현위치를 closed에 등록하고 부모 노드에서 재시작
            closedNodes.add(node)

            val itNodeOrder = nodeOrders.iterator()
            for (tmpNode in itNodeOrder) {
                if ( tmpNode.x == node.x && tmpNode.y == node.y ) {
                    itNodeOrder.remove()
                }
            }
            lowestNode = nodeOrders.get(nodeOrders.size - 1)
        }

        if ( nodeOrders.size > 0 ) {
            val nodeOrder = nodeOrders.get(nodeOrders.size - 1)
            if ( nodeOrder.x != lowestNode!!.x || nodeOrder.y != lowestNode!!.y ) {
                nodeOrders.add(lowestNode!!)
            }
        } else {
            nodeOrders.add(lowestNode!!)
        }
        return lowestNode
    }


    inner class Node(internal var x: Int, internal var y: Int) {
        internal var cost: Int = 0
        internal var parentNode: Node? = null
    }
}
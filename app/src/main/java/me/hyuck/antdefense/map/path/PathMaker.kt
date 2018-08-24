package me.hyuck.antdefense.map.path

import android.graphics.Rect
import me.hyuck.antdefense.utils.Values
import java.util.ArrayList

class PathMaker(width: Int, height: Int) {
    private var pf = PathFinder()
    var map: Array<IntArray> = Values.STAGE1_MAP

    internal var mNodes = ArrayList<PathFinder.Node>()
    var mPositions = ArrayList<Position>()
    var tileRect: Array<Array<Rect?>>? = null

    var tileW: Int = 0
    var tileH:Int = 0
    var mapX: Int = 0
    var mapY:Int = 0

    init {
        mapX = Values.STAGE1_MAP[0].size
        mapY = Values.STAGE1_MAP.size
//        map = Array(mapY) { IntArray(mapX) }
//        for (i in 0 until Values.STAGE1_MAP.size) {
//            System.arraycopy(Values.STAGE1_MAP[i], 0, map[i], 0,
//                    Values.STAGE1_MAP[i].size)
//        }
        tileW = width / mapX
        tileH = height / mapY
        tileRect = Array(mapX) { arrayOfNulls<Rect>(mapY)}
        pf.movableTiles = intArrayOf(1)
        pf.setStart(0, 0)
        pf.setEnd(19, 9)
        pf.map = map
        pf.getResult()
        mNodes = pf.nodeOrders

        map[0].forEachIndexed { x, _ ->
            map.forEachIndexed { y, _ ->
                tileRect!![x][y] = Rect(x * tileW, y * tileH, (x + 1) * tileW, (y + 1) * tileH)
            }
        }

        for (t in mNodes) {
            val x = t.x * tileW + tileW / 2
            val y = t.y * tileH + tileH / 2
            mPositions.add(Position(x, y))
        }
    }
}

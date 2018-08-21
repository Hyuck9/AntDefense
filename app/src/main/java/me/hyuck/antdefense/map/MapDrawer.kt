package me.hyuck.antdefense.map

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix

import me.hyuck.antdefense.utils.Values

class MapDrawer {

    /** 맵 ( 현재는 1스테이지만.. )  */
    // { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
    // { 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0 },
    // { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0 },
    // { 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0 },
    // { 0, 1, 0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0 },
    // { 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 0 },
    // { 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0 },
    // { 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0 },
    // { 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1 },
    // { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }
    var map: Array<out IntArray> = Values.STAGE1_MAP!!

    /** 현재는 1만 움직일 수 있는 타일, 추후 추가 예정  */
    var movableTiles = intArrayOf(1)

    /** 직선 길  */
    var tile0: Bitmap? = null
    /** 커브 길  */
    var tile1: Bitmap? = null

    /** 타일 크기  */
    var tileWidth = 50
    var tileHeight = 50


    /**
     * 입력받은 타일이 지나갈 수 있는 길인지 확인
     * input에 해당하는 타일이 지나갈 수 있는 타일이면 true, 아니면 false
     * ex ) Enemy가 지나가는 길 -> 1
     * Ant가 설치되는 땅 -> 0
     * movableTiles의 값이 { 1 } 일 때
     * input 값이 1이면 true, 1이 아니면 false 반환
     */
    private fun isMovableTile(input: Int): Boolean {
        var movable = false
        for (i in movableTiles.indices) {
            if (movableTiles[i] == input) movable = true
        }
        return movable
    }

    /**
     * 위쪽 타일이 갈 수 있는 길인지 체크
     */
    private fun checkUp(x: Int, y: Int): Boolean {
        return y > 0 && isMovableTile(map[y - 1][x])
    }

    /**
     * 아래쪽 타일이 갈 수 있는 길인지 체크
     */
    private fun checkDown(x: Int, y: Int): Boolean {
        return y < map.size - 1 && isMovableTile(map[y + 1][x])
    }

    /**
     * 왼쪽 타일이 갈 수 있는 길인지 체크
     */
    private fun checkLeft(x: Int, y: Int): Boolean {
        return x > 0 && isMovableTile(map[y][x - 1])
    }

    /**
     * 오른쪽 타일이 갈 수 있는 길인지 체크
     */
    private fun checkRight(x: Int, y: Int): Boolean {
        return x < map[0].size - 1 && isMovableTile(map[y][x + 1])
    }

    /** 이미지 회전 및 뒤집어 그리기  */
    private fun drawMatrix(canvas: Canvas, tile: Bitmap, matrix: Matrix, x: Int, y: Int) {
        val img = Bitmap.createBitmap(tile, 0, 0, tile.width, tile.height, matrix, true)
        val scaledImage = Bitmap.createScaledBitmap(img, tileWidth, tileHeight, false)
        canvas.drawBitmap(scaledImage, (tileWidth * x).toFloat(), (tileHeight * y).toFloat(), null)
    }

    /**
     * 맵 그리기
     * 맨 왼쪽 위칸에서 시작 -> 맨 아래 오른쪽 칸에서 끝 나는 단일 경로 길
     * ( 갈림길 없음 )
     */
    fun drawMap(width: Int, height: Int): Bitmap {
        val straightTile = Bitmap.createScaledBitmap(tile0!!, tileWidth, tileHeight, false)
        val curveTile = Bitmap.createScaledBitmap(tile1!!, tileWidth, tileHeight, false)
        val road = Bitmap.createBitmap(width, height, tile0!!.config)
        val canvas = Canvas(road)

        val lastX = map[0].size - 1
        val lastY = map.size - 1
        var matrix: Matrix

        for (y in map.indices) {
            for (x in 0 until map[0].size) {
                // 이동할 수 있는 타일일 경우 길 그리기
                if (isMovableTile(map[y][x])) {
                    // 한번 Rotate나 Scale을 했을 경우를 위해 매트릭스 초기화
                    matrix = Matrix()
                    if (
                            y == 0 && x == 0 && checkRight(x, y) ||                                     // 첫번째 타일에서 오른쪽에 길이 있을 경우
                            y == lastY && x == lastX && checkLeft(x, y) ||                              // 마지막 타일에서 왼쪽에 길이 있을 경우
                            !checkUp(x, y) && !checkDown(x, y) && checkLeft(x, y) && checkRight(x, y)   // 위아래 길 없고 양옆에 길이 있을 경우
                    ) {
                        // 가로 직선 길
                        canvas.drawBitmap(straightTile, (tileWidth * x).toFloat(), (tileHeight * y).toFloat(), null)
                    }
                    else if (
                            y == 0 && x == 0 && checkDown(x, y) ||                                      // 첫번째 타일에서 아래쪽에 길이 있을 경우
                            y == lastY && x == lastX && checkUp(x, y) ||                                // 마지막 타일에서 위쪽에 길이 있을 경우
                            checkUp(x, y) && checkDown(x, y) && !checkLeft(x, y) && !checkRight(x, y)   // 양옆에 길 없고 위아래 길이 있을 경우
                    ) {
                        // 세로 직선 길
                        matrix.postRotate(90.0f)
                        drawMatrix(canvas, tile0!!, matrix, x, y)
                    } else  {
                        // 첫번째, 마지막 타일 제외됨.
                        if ( checkUp(x, y) && !checkDown(x, y) && !checkLeft(x, y) && checkRight(x, y) ) {           // 아래/왼쪽 길 없고, 위/오른쪽 길이 있을 경우 ( └ ) 모양 커브길
                            canvas.drawBitmap(curveTile, (tileWidth * x).toFloat(), (tileHeight * y).toFloat(), null)
                        } else if ( checkUp(x, y) && !checkDown(x, y) && checkLeft(x, y) && !checkRight(x, y) ) {    // 아래/오른쪽 길 없고, 위/왼쪽 길이 있을 경우 ( ┘ ) 모양 커브길
                            matrix.setScale(-1.0f, 1.0f)
                            drawMatrix(canvas, tile1!!, matrix, x, y)
                        } else if ( !checkUp(x, y) && checkDown(x, y) && checkLeft(x, y) && !checkRight(x, y) ) {    // 아래/오른쪽 길 없고, 위/왼쪽 길이 있을 경우 ( ┐ ) 모양 커브길
                            matrix.setScale(-1.0f, -1.0f)
                            drawMatrix(canvas, tile1!!, matrix, x, y)
                        } else if ( !checkUp(x, y) && checkDown(x, y) && !checkLeft(x, y) && checkRight(x, y) ) {    // 아래/오른쪽 길 없고, 위/왼쪽 길이 있을 경우 ( ┌ ) 모양 커브길
                            matrix.setScale(1.0f, -1.0f)
                            drawMatrix(canvas, tile1!!, matrix, x, y)
                        }
                    }

                }
            }
        }

        return road
    }

}

package me.hyuck.antdefense.model

import android.graphics.Bitmap

import java.util.ArrayList

import me.hyuck.antdefense.map.path.Position
import me.hyuck.antdefense.utils.Values

class Enemy(private val type: Int, private val imgs: Array<Array<Bitmap>>, wave: Int, val id: Int, private val mPositions: ArrayList<Position>) {

    /** 포지션  */
    var x: Int = 0
    var y: Int = 0
    /** 다음 좌표  */
    private var dx: Int = 0
    private var dy: Int = 0
    /** 타일 좌표  */
    private var tx: Int = 0
    private var ty: Int = 0

    /** 이미지 리소스  */
    var img: Bitmap? = null

    /** 이미지 가로세로 중간값  */
    var imgW: Int = 0
    var imgH: Int = 0
    private var imgIndex = 0
    private var price: Int = 0
    private var fullHp: Int = 0
    private var currentHp: Int = 0
    private var speed: Int = 0
    private var radian: Double = 0.toDouble()
    var angle: Int = 0

    private var imgLoop = 0

    private var now: Int = 0
    private var next: Int = 0
    private var lastTile: Int = 0


    var isDead = false
    var isPassed = false

    init {
        initializeEnemy(wave)
    }

    /** 적 초기화  */
    private fun initializeEnemy(wave: Int) {
        img = imgs[type][imgIndex]
        imgW = img!!.width / 2
        imgH = img!!.height / 2

        x = mPositions[0].x
        y = mPositions[0].y

        fullHp = Values.ENEMY_STATS[type][0] * (wave * 2 + 1)
        speed = Values.ENEMY_STATS[type][1] / 10 * ( (wave - 1) / 10 + 1 )
        price = Values.ENEMY_STATS[type][2] * wave
        currentHp = fullHp

        now = 0
        next = 1
        dx = 0
        dy = 0
        lastTile = mPositions.size - 1
    }

    /** 적 움직임  */
    fun move() {
        imgLoop++

        if (imgLoop % 5 == 0) {
            imgIndex = 1 - imgIndex
        }
        img = imgs[type][imgIndex]

        x += dx
        y += dy

        /* 현재 타일 넘어가면 */
        if (Math.abs(x - tx) <= speed / 2 && Math.abs(y - ty) <= speed / 2) {
            next++
        }

        /* 다음 타일로 넘어가기 */
        if (now != next && next <= mPositions.size - 1) {
            now++
            tx = mPositions[next].x
            ty = mPositions[next].y
            radian = Math.atan2((ty - y).toDouble(), (tx - x).toDouble())
            dx = (Math.cos(radian) * speed).toInt()
            dy = (Math.sin(radian) * speed).toInt()
            angle = (Math.toDegrees(radian) + 90).toInt()
        }

        /* 마지막 타일 넘어가면 */
        if (Math.abs(x - mPositions[lastTile].x) <= speed / 2 && Math.abs(y - mPositions[lastTile].y) <= speed / 2) {
            isPassed = true
            isDead = true
        }

        /* HP가 다 떨어지면 */
        if (currentHp <= 0) {
            isDead = true
        }

    }

}

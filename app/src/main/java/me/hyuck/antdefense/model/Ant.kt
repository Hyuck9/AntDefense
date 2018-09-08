package me.hyuck.antdefense.model

import android.graphics.Bitmap

import me.hyuck.antdefense.utils.Values

class Ant(private val imgs: Array<Array<Bitmap>>, val x: Int, val y: Int, val mapX: Int, val mapY: Int) {

    /** 이미지 리소스  */
    var img: Bitmap? = null

    /** 이미지 가로세로 중간값  */
    var imgW: Int = 0
    var imgH: Int = 0

    /** 상태 정보  */
    private var type: Int = 0
    var level: Int = 0
    private var stats: Array<IntArray>? = null
    var angle: Int = 0
    private var isAttacking = false
    private val isDead = false

    /** 개미 능력치  */
    private var damage: Int = 0
    private var attackSpeed: Int = 0
    var attackRange: Int = 0
    private var cost: Int = 0

    private var attackLoop = 0

    init {

        initializeAnt()
    }

    /** 개미 초기화  */
    private fun initializeAnt() {
        val basicStats = Values.BASIC_ANT_STATS
        level = 1
        type = Values.ANT_INDEX[0]
        img = imgs[0][0]
        imgW = img!!.width / 2
        imgH = img!!.height / 2
        attackRange = Values.Height / 40 * (basicStats[0] * 2 + 3)
        damage = basicStats[1]
        attackSpeed = basicStats[2]
        cost = basicStats[3]
    }

    /** 개미는 따로 움직임이 없어 공격중인지만 체크
     * 공격속도 비례하여 공격 딜레이 계산
     */
    fun move() {
        if (isAttacking) {
            attackLoop++
            if (attackLoop % (40 / attackSpeed) == 0) {
                isAttacking = false
                attackLoop = 0
            }
        }
    }

    /** 업그레이드 개미 레벨업  */
    fun levelup() {

    }

    /** 방향 전환  */
    fun spin() {

    }

    /** 기본 개미 업그레이드  */
    fun changeType(antType: Int) {
        type = antType
        if ( type == Values.ANT_INDEX[1] ) {
            stats = Values.RIFLE_ANT_STATS
        }
        if ( type == Values.ANT_INDEX[2] ) {
            stats = Values.SPLASH_ANT_STATS
        }
        if ( type == Values.ANT_INDEX[3] ) {
            stats = Values.SNIPER_ANT_STATS
        }

        img = imgs[type/100 - 1][0]
        attackRange = Values.Height / 40 * (stats!![level - 1][0] * 2 + 3)
        damage = stats!![level - 1][1]
        attackSpeed = stats!![level - 1][2]
        cost = stats!![level - 1][3]
    }

}

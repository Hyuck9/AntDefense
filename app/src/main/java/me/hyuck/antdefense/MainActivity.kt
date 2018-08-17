package me.hyuck.antdefense

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun mOnClick(view: View) {
        when ( view.id ) {
            R.id.main_btn_start -> {
                // TODO : 스테이지 선택 창으로 이동
            }
            R.id.main_btn_exit -> finish()
            R.id.main_btn_credit -> {
                if ( main_img_credit.visibility == View.VISIBLE ) {
                    main_img_credit.visibility = View.GONE
                } else {
                    main_img_credit.visibility = View.VISIBLE
                }
            }
        }
    }
}

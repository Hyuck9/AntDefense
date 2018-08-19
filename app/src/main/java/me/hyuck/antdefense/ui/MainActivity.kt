package me.hyuck.antdefense.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import me.hyuck.antdefense.R
import me.hyuck.antdefense.ui.game.GameActivity

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun mOnClick(view: View) {
        when ( view.id ) {
            R.id.main_btn_start -> {
                startActivity(Intent(this, GameActivity::class.java))
                finish()
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

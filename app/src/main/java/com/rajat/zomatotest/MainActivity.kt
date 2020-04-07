package com.rajat.zomatotest

import android.os.Bundle
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.rajat.zomatotest.ui.main.MainFragment
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow()
        }
        btnMore.setOnClickListener { inflatePopupMenu() }
    }

    private fun inflatePopupMenu() {
        val popupMenu = PopupMenu(this, btnMore)
        popupMenu.menuInflater.inflate(R.menu.menu_sort, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            val currentFragment = supportFragmentManager.findFragmentById(R.id.container)
            if (currentFragment is MainFragment) {
                currentFragment.onMenuItemClicked(item.itemId)
            }
            true
        }
        popupMenu.show()
    }
}

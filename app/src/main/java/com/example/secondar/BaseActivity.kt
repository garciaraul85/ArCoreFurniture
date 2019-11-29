package com.example.secondar

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentActivity
import com.special.ResideMenu.ResideMenu
import com.special.ResideMenu.ResideMenuItem
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment
import com.yalantis.contextmenu.lib.MenuObject
import com.yalantis.contextmenu.lib.MenuParams
import kotlinx.android.synthetic.main.toolbar.*

open class BaseActivity: AppCompatActivity() {

    private lateinit var mContext: BaseActivity
    private lateinit var mTopToolbar: Toolbar
    private lateinit var contextMenuDialogFragment: ContextMenuDialogFragment

    override fun onResume() {
        super.onResume()
        mContext = this
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            when (it.itemId) {
                R.id.context_menu -> {
                    showContextMenuDialogFragment()
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun initToolbar() {
        mTopToolbar = findViewById (R.id.toolbar)
        setSupportActionBar(mTopToolbar);

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        toolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationOnClickListener { onBackPressed() }
        }

        tvToolbarTitle.text = "Samantha"
    }

    override fun onBackPressed() {
        if (::contextMenuDialogFragment.isInitialized && contextMenuDialogFragment.isAdded) {
            contextMenuDialogFragment.dismiss()
        } else {
            finish()
        }
    }

    private fun showContextMenuDialogFragment() {
        if (supportFragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
            contextMenuDialogFragment.show(supportFragmentManager, ContextMenuDialogFragment.TAG)
        }
    }

    /**
     * If you want to change the side you need to add 'gravity' parameter,
     * by default it is MenuGravity.END.
     *
     * For example:
     *
     * MenuParams(
     *     actionBarSize = resources.getDimension(R.dimen.tool_bar_height).toInt(),
     *     menuObjects = getMenuObjects(),
     *     isClosableOutside = false,
     *     gravity = MenuGravity.START
     * )
     */
    fun initMenuFragment() {
        val menuParams = MenuParams(
                actionBarSize = resources.getDimension(R.dimen.tool_bar_height).toInt(),
                menuObjects = getMenuObjects(),
                isClosableOutside = false
        )

        contextMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams).apply {
            menuItemClickListener = { view, position ->
                Toast.makeText(
                        this@BaseActivity,
                        "Clicked on position: $position",
                        Toast.LENGTH_SHORT
                ).show()
            }
            menuItemLongClickListener = { view, position ->
                Toast.makeText(
                        this@BaseActivity,
                        "Long clicked on position: $position",
                        Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * You can use any (drawable, resource, bitmap, color) as image:
     * menuObject.drawable = ...
     * menuObject.setResourceValue(...)
     * menuObject.setBitmapValue(...)
     * menuObject.setColorValue(...)
     *
     * You can set image ScaleType:
     * menuObject.scaleType = ScaleType.FIT_XY
     *
     * You can use any [resource, drawable, color] as background:
     * menuObject.setBgResourceValue(...)
     * menuObject.setBgDrawable(...)
     * menuObject.setBgColorValue(...)
     *
     * You can use any (color) as text color:
     * menuObject.textColor = ...
     *
     * You can set any (color) as divider color:
     * menuObject.dividerColor = ...
     */
    private fun getMenuObjects() = mutableListOf<MenuObject>().apply {
        val close = MenuObject().apply { setResourceValue(R.drawable.icn_close) }
        val bathroom = MenuObject("Bathroom").apply { setResourceValue(R.drawable.icn_1) }
        val beds = MenuObject("Beds").apply { setResourceValue(R.drawable.icn_2) }
        val cases = MenuObject("Cases").apply {setResourceValue(R.drawable.icn_3) }
        val chairs = MenuObject("Chairs").apply { setResourceValue(R.drawable.icn_4) }
        val closets = MenuObject("Closets").apply { setResourceValue(R.drawable.icn_5) }
        val doors = MenuObject("Doors").apply { setResourceValue(R.drawable.icn_1) }
        val fireplaces = MenuObject("Fireplaces").apply { setResourceValue(R.drawable.icn_2) }
        val inside = MenuObject("Inside").apply { setResourceValue(R.drawable.icn_3) }
        val kitchen = MenuObject("Kitchen").apply { setResourceValue(R.drawable.icn_4) }
        val outside = MenuObject("Outside").apply { setResourceValue(R.drawable.icn_5) }
        val tables = MenuObject("Tables").apply { setResourceValue(R.drawable.icn_1) }
        val pictures = MenuObject("Take picture").apply { setResourceValue(R.drawable.icn_2) }
        val remove = MenuObject("Remove all Items").apply { setResourceValue(R.drawable.icn_3) }

        add(close)
        add(bathroom)
        add(beds)
        add(cases)
        add(chairs)
        add(closets)
        add(doors)
        add(closets)
        add(fireplaces)
        add(inside)
        add(kitchen)
        add(outside)
        add(tables)
        add(pictures)
        add(remove)
    }
}
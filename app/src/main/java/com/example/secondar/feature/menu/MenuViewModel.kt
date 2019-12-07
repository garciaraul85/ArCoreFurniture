package com.example.secondar.feature.menu

import androidx.lifecycle.ViewModel
import com.example.secondar.R
import com.yalantis.contextmenu.lib.MenuObject

class MenuViewModel(): ViewModel() {

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
    fun getMenuObjects(): MutableList<MenuObject> = mutableListOf<MenuObject>().apply {
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
    }
}
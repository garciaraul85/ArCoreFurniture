package com.example.secondar.feature.menu

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.secondar.R
import com.example.secondar.feature.menu.data.ApiService
import com.example.secondar.feature.menu.models.Category
import com.example.secondar.util.UtilMethods
import com.google.ar.sceneform.AnchorNode
import com.yalantis.contextmenu.lib.MenuObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MenuViewModel(app: Application): AndroidViewModel(app) {

    private var categories: MutableList<MenuObject> = mutableListOf()

    private val categoriesMutableLiveData = MutableLiveData<MutableList<MenuObject>>()
    val categoriesLiveData: LiveData<MutableList<MenuObject>>
        get() = categoriesMutableLiveData

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

    @SuppressLint("CheckResult")
    fun getArMenuCategories() {
        categories.add(MenuObject().apply { setResourceValue(R.drawable.icn_close) })
        if (UtilMethods.isConnectedToInternet(this.getApplication())) {
            //UtilMethods.showLoading(this.getApplication())
            val observable = ApiService.userApiCall().getMenu()

            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ userResponse ->
                            //UtilMethods.hideLoading()
                            userResponse.files.forEach { categories ->
                                this.categories.add(MenuObject(categories.name).apply { setResourceValue(R.drawable.icn_1) })
                                //println("_xyz Name = " + categories.name + ", Icon = " + categories.icon)
                                categories.products.forEach { products ->
                                    println("_xyz Name = " + products.name + ", Icon = " + products.icon + ", Url = " + products.url)
                                }
                            }
                            this.categories.let {
                                this.categoriesMutableLiveData.value = this.categories
                            }
                            /** userResponse is response data class*/
                        }, { error ->
                            //UtilMethods.hideLoading()
                            UtilMethods.showLongToast(this.getApplication(), error.message.toString())
                        }
                    )
        } else {
            UtilMethods.showLongToast(this.getApplication(), "No Internet Connection!")
        }
    }

}
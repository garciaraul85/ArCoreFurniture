package com.example.secondar.feature.menu

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.secondar.R
import com.example.secondar.feature.menu.data.ApiService
import com.example.secondar.feature.menu.models.Category
import com.example.secondar.feature.menu.models.Products
import com.example.secondar.models.Product
import com.example.secondar.util.UtilMethods
import com.yalantis.contextmenu.lib.MenuObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MenuViewModel(app: Application): AndroidViewModel(app) {

    private var categories: MutableList<MenuObject> = mutableListOf()
    var categoriesList: MutableList<Category> = mutableListOf()

    private var productList: MutableList<Product> = mutableListOf()

    private val categoriesMutableLiveData = MutableLiveData<MutableList<MenuObject>>()
    val categoriesLiveData: LiveData<MutableList<MenuObject>>
        get() = categoriesMutableLiveData

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
                            println(userResponse.toString())
                            this.categoriesList = userResponse.categoriesList.toMutableList()
                            this.categoriesList.forEach { categories ->
                                this.categories.add(MenuObject(categories.name).apply { setResourceValue(R.drawable.icn_1) })
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

    fun getProductsFromCategory(position: Int): MutableList<Product> {
        this.productList.clear()
        this.categoriesList[position].products.forEach { product ->
            this.productList.add(Product(product.name, R.drawable.table))
        }
        return this.productList
    }

}
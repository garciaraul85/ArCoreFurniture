package com.example.secondar.feature.base

import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.example.secondar.R
import com.example.secondar.feature.menu.utils.MoreMenuFactory
import com.example.secondar.feature.menu.viewModels.MenuViewModel
import com.skydoves.powermenu.PowerMenuItem
import com.skydoves.powermenu.kotlin.powerMenu

open class BaseActivity: AppCompatActivity() {

    lateinit var toolbarTitleTxt: TextView

    lateinit var menuViewModel: MenuViewModel
    private val menuOptionSelectedMutableLiveData = MutableLiveData<Int>()
    val menuOptionSelectedLiveData: LiveData<Int>
        get() = menuOptionSelectedMutableLiveData

    var isShowing: Boolean = false
    var hamburgerWasClicked: Boolean = false
    private val moreMenu by powerMenu(MoreMenuFactory::class)

    fun initArMenu() {
        toolbarTitleTxt = findViewById(R.id.toolbarTitle)
        menuViewModel = ViewModelProviders.of(this, viewModelFactory {
            MenuViewModel(application)
        }).get(MenuViewModel::class.java)

        initMenuFragment()
    }


    fun onHamburger(view: View) {
        hamburgerWasClicked = true
        if (isShowing) {
            isShowing = false
            moreMenu.dismiss()
        } else {
            isShowing = true
            moreMenu.showAsDropDown(view)
        }
    }

    private fun initMenuFragment() {
        menuViewModel.categoriesLiveData.observe(this, Observer<MutableList<PowerMenuItem>> { menu ->

            moreMenu.addItemList(menu)

            moreMenu.setOnMenuItemClickListener { position, item ->
                if (hamburgerWasClicked) {
                    isShowing = !isShowing
                }
                moreMenu.selectedPosition = position

                toolbarTitleTxt.text = menu[position].title
                menuOptionSelectedMutableLiveData.value = (position)
            }
        })

        menuViewModel.getArMenuCategories()
    }

    protected inline fun <VM : ViewModel> viewModelFactory(crossinline f: () -> VM) =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T = f() as T
            }
}
package com.example.secondar

import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.example.secondar.feature.menu.MenuViewModel
import com.example.secondar.feature.menu.utils.MoreMenuFactory
import com.skydoves.powermenu.PowerMenuItem
import com.skydoves.powermenu.kotlin.powerMenu
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment

open class BaseActivity: AppCompatActivity() {

    private lateinit var mContext: BaseActivity
    lateinit var contextMenuDialogFragment: ContextMenuDialogFragment
    lateinit var toolbarTitleTxt: TextView

    lateinit var menuViewModel: MenuViewModel

    private val menuOptionSelectedMutableLiveData = MutableLiveData<Int>()
    val menuOptionSelectedLiveData: LiveData<Int>
        get() = menuOptionSelectedMutableLiveData


    private val moreMenu by powerMenu(MoreMenuFactory::class)

    fun initArMenu() {
        toolbarTitleTxt = findViewById(R.id.toolbarTitle)
        menuViewModel = ViewModelProviders.of(this, viewModelFactory {
            MenuViewModel(application)
        }).get(MenuViewModel::class.java)

        initMenuFragment()
    }

    var isShowing: Boolean = false
    var hamburgerWasClicked: Boolean = false
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
                Toast.makeText(this, item.title, Toast.LENGTH_SHORT).show()
            }
        })

        menuViewModel.getArMenuCategories()
    }

    protected inline fun <VM : ViewModel> viewModelFactory(crossinline f: () -> VM) =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T = f() as T
            }
}
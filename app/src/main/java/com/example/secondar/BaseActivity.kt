package com.example.secondar

import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.*
import com.example.secondar.feature.menu.MenuViewModel
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment
import com.yalantis.contextmenu.lib.MenuParams
import com.yalantis.contextmenu.lib.MenuObject
import kotlinx.android.synthetic.main.toolbar.*

open class BaseActivity: AppCompatActivity() {

    private lateinit var mContext: BaseActivity
    private lateinit var mTopToolbar: Toolbar
    lateinit var contextMenuDialogFragment: ContextMenuDialogFragment
    lateinit var toolbarTitleTxt: TextView

    lateinit var menuViewModel: MenuViewModel
    lateinit var menuParams: MenuParams

    private val menuOptionSelectedMutableLiveData = MutableLiveData<Int>()
    val menuOptionSelectedLiveData: LiveData<Int>
        get() = menuOptionSelectedMutableLiveData

    fun initArMenu() {
        toolbarTitleTxt = findViewById(R.id.toolbarTitle)
        menuViewModel = ViewModelProviders.of(this, viewModelFactory {
            MenuViewModel(application)
        }).get(MenuViewModel::class.java)

        initToolbar()
        initMenuFragment()
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

    private fun initToolbar() {
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

        toolbarTitle.text = "PAD"
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
    private fun initMenuFragment() {
        menuViewModel.categoriesLiveData.observe(this, Observer<MutableList<MenuObject>> { menu ->
            menuParams = MenuParams(
                    actionBarSize = resources.getDimension(R.dimen.tool_bar_height).toInt(),
                    menuObjects = menu,
                    isClosableOutside = false
            )
            initContextMenuDialogFragment(menu)
        })

        menuViewModel.getArMenuCategories()
    }

    private fun initContextMenuDialogFragment(menu: MutableList<MenuObject>) {
        contextMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams).apply {
            menuItemClickListener = { _, position ->
                toolbarTitleTxt.text = menu[position].title
                menuOptionSelectedMutableLiveData.value = (position - 1)
            }
        }
    }

    protected inline fun <VM : ViewModel> viewModelFactory(crossinline f: () -> VM) =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T = f() as T
            }
}
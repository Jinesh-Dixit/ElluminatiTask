package com.example.servicelist.ui.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.servicelist.R
import com.example.servicelist.databinding.ActivityMainBinding
import com.example.servicelist.databinding.BottomSheetAddBinding
import com.example.servicelist.databinding.BottomSheetServiceCustomizeBinding
import com.example.servicelist.model.AddCartItem
import com.example.servicelist.model.ListItem
import com.example.servicelist.model.ServiceResponse
import com.example.servicelist.model.SpecificationsItem
import com.example.servicelist.ui.adapter.ItemListAdapter
import com.example.servicelist.ui.viewmodel.ServiceListViewModel
import com.example.servicelist.utils.Utils
import com.example.servicelist.utils.getAssetJsonData
import com.example.servicelist.utils.setupFullHeight
import com.example.servicelist.utils.toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var bsdServiceList: BottomSheetDialog? = null
    private var bsdAdd: BottomSheetDialog? = null
    private lateinit var bottomSheetServiceCustomizeBinding: BottomSheetServiceCustomizeBinding
    private lateinit var bottomSheetAddBinding: BottomSheetAddBinding
    private lateinit var serviceListViewModel: ServiceListViewModel
    private lateinit var serviceResponse: ServiceResponse
    private var specifications: List<SpecificationsItem> = arrayListOf()
    private lateinit var typeOneAdapter: ItemListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        serviceListViewModel = ViewModelProvider(this)[ServiceListViewModel::class.java]
        init()
    }

    private fun init() {
        statusBarColor(R.color.color_0FB7B4)
        getData()
        initBottomSheet()
        initLastAddSheet()
        clicks()
    }

    private fun clicks() {
        binding.apply {
            tvCustomize.setOnClickListener {
                customizeTap(false)
            }
            imgPlus.setOnClickListener {
                bsdAdd?.show()
                setLastItemRepeatText()

            }
            imgMinus.setOnClickListener {
                if (serviceListViewModel.addToCartItem.size > 1) {
                    serviceListViewModel.addToCartItem.removeLast()
                    binding.tvNotificaionCount.text =
                        serviceListViewModel.addToCartItem.filter { !it.isRepeat }.toList().size.toString()
                    binding.tvCount.text = serviceListViewModel.addToCartItem.size.toString()
                }
            }
        }
    }

    private fun customizeTap(isFromDialog: Boolean) {
        getData()
        setFreshDataToView()
        bsdServiceList?.show()
        if (isFromDialog) {
            bsdAdd?.dismiss()
        }
        bottomSheetServiceCustomizeBinding.nsMain.fullScroll(View.FOCUS_UP)
        bottomSheetServiceCustomizeBinding.appBar.setExpanded(true)
    }

    private fun setLastItemRepeatText() {
        val lastItem = serviceListViewModel.addToCartItem.last()
        var itemInfo = ""
        for (i in lastItem.serviceResponse.specifications) {
            if (i.type == 1) {
                for (j in i.list as ArrayList<ListItem>) {
                    if (j.isDefaultSelected) {
                        itemInfo = "${j.name}"
                        break
                    }
                }
            }
        }

        for (i in lastItem.serviceResponse.specifications) {
            if (i.type == 2) {
                for (j in i.list as ArrayList<ListItem>) {
                    if (j.isDefaultSelected) {
                        itemInfo = "$itemInfo, ${j.name}"
                    }
                }
            }
        }

        bottomSheetAddBinding.tvLastItems.text = itemInfo.replace("[", "").replace("]", "")
    }

    private fun getData() {
        serviceResponse =
            Gson().fromJson(this.getAssetJsonData(), ServiceResponse::class.java)
        specifications = serviceResponse.specifications
        serviceListViewModel.serviceResponse = serviceResponse
        serviceListViewModel.specifications = specifications
        setData()
    }

    @SuppressLint("SetTextI18n")
    private fun setData() {
        binding.tvInsideTitle.text = serviceListViewModel.serviceResponse.name!![0].toString()
        binding.tvPrice.text =
            "${Utils.getCurrencySymbol("INR")} ${serviceListViewModel.serviceResponse.price.toString()}"
    }

    @SuppressLint("SetTextI18n")
    fun initBottomSheet() {
        val myDrawerView =
            layoutInflater.inflate(R.layout.bottom_sheet_service_customize, null)
        bsdServiceList = BottomSheetDialog(this, R.style.SheetDialog)
        bottomSheetServiceCustomizeBinding =
            BottomSheetServiceCustomizeBinding.inflate(
                layoutInflater,
                myDrawerView as ViewGroup,
                false
            )
        bsdServiceList?.setContentView(bottomSheetServiceCustomizeBinding.root)
        bsdServiceList?.setCancelable(false)
        bsdServiceList?.behavior?.isDraggable = false

        bottomSheetServiceCustomizeBinding.toolbar.navigationIcon =
            ContextCompat.getDrawable(this, R.drawable.ic_back)
        bottomSheetServiceCustomizeBinding.toolbar.navigationIcon?.setTint(
            ContextCompat.getColor(
                this,
                R.color.white
            )
        )
        bottomSheetServiceCustomizeBinding.toolbar.setNavigationOnClickListener(View.OnClickListener {
            bsdServiceList?.dismiss()
        })

        bottomSheetServiceCustomizeBinding.tvButtonAddToCard.setOnClickListener {
            addToCartTap()
        }

        bottomSheetServiceCustomizeBinding.toolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar)
        bottomSheetServiceCustomizeBinding.toolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar)

        bottomSheetServiceCustomizeBinding.toolbarLayout.title =
            serviceListViewModel.serviceResponse.name!![0].toString()

        bottomSheetServiceCustomizeBinding.imgPlus.setOnClickListener {
            serviceListViewModel.mainCount = serviceListViewModel.mainCount + 1
            bottomSheetServiceCustomizeBinding.tvCount.text =
                serviceListViewModel.mainCount.toString()
            calculateCardAMount()
        }
        bottomSheetServiceCustomizeBinding.imgMinus.setOnClickListener {
            if (serviceListViewModel.mainCount != 1) {
                serviceListViewModel.mainCount = serviceListViewModel.mainCount - 1
                bottomSheetServiceCustomizeBinding.tvCount.text =
                    serviceListViewModel.mainCount.toString()
                calculateCardAMount()
            }

        }

        bottomSheetServiceCustomizeBinding.clBottom.visibility = View.VISIBLE
        setupFullHeight(bsdServiceList!!)

    }

    private fun addToCartTap() {
        if (checkIsRequiredSelected()) {
            serviceListViewModel.addToCartItem.add(
                AddCartItem(
                    ServiceResponse(
                        serviceResponse.itemTaxes,
                        serviceResponse.price,
                        serviceResponse.name,
                        serviceResponse.id,
                        typeOneAdapter.itemList as ArrayList<SpecificationsItem>
                    ), onlyPrice() + serviceResponse.price?.toDouble()!!
                )

            )
            bsdServiceList?.dismiss()
            updateViewAfterCartAdd()
        } else {
            this.toast(getString(R.string.please_select_required_field))
        }
    }

    private fun setFreshDataToView() {
        serviceListViewModel.mainCount = 1
        bottomSheetServiceCustomizeBinding.tvCount.text =
            serviceListViewModel.mainCount.toString()
        filterValues()
        typeOneAdapter()
    }

    @SuppressLint("SetTextI18n")
    fun initLastAddSheet() {
        val myDrawerView =
            layoutInflater.inflate(R.layout.bottom_sheet_add, null)
        bsdAdd = BottomSheetDialog(this, R.style.DialogStyle)
        bottomSheetAddBinding =
            BottomSheetAddBinding.inflate(
                layoutInflater,
                myDrawerView as ViewGroup,
                false
            )
        bsdAdd?.setContentView(bottomSheetAddBinding.root)
        bsdAdd?.setCancelable(false)
        bsdAdd?.behavior?.isDraggable = false

        bottomSheetAddBinding.tvRepeat.setOnClickListener {
            serviceListViewModel.addToCartItem.add(
                AddCartItem(
                    serviceListViewModel.addToCartItem.last().serviceResponse,
                    serviceListViewModel.addToCartItem.last().price,
                    true
                )
            )
            bsdAdd?.dismiss()
            updateViewAfterCartAdd()
        }

        bottomSheetAddBinding.tvCustomize.setOnClickListener {
            customizeTap(true)
        }

        bottomSheetAddBinding.imgClose.setOnClickListener {
            bsdAdd?.dismiss()
        }

    }

    private fun updateViewAfterCartAdd() {
        binding.clViewCart.visibility = View.VISIBLE
        binding.llNotificationCount.visibility = View.VISIBLE
        binding.llPlusMinus.visibility = View.VISIBLE
        binding.tvCustomize.visibility = View.GONE
        binding.tvNotificaionCount.text =
            serviceListViewModel.addToCartItem.filter { !it.isRepeat }.toList().size.toString()
        binding.tvCount.text = serviceListViewModel.addToCartItem.size.toString()
    }

    @SuppressLint("SetTextI18n")
    private fun filterValues() {
        for (i in serviceListViewModel.serviceResponse.specifications as ArrayList<SpecificationsItem>) {
            if (i.range == 1 && i.maxRange == 3) {
                i.isRequired = true
                i.isMultipleAllowed = true
            } else {
                i.isMultipleAllowed = false
            }

            if (i.type == 1) {
                for (j in i.list as ArrayList<ListItem>) {
                    if (j.isDefaultSelected) {
                        serviceListViewModel.serviceResponse.specifications =
                            specifications.filter { it.modifierId.toString() == j.id.toString() && it.type != 1 }
                        calculateCardAMount()
                        serviceListViewModel.currentPrice = j.price?.toDouble()!!
                        break
                    }
                }
            }
        }
    }

    private fun typeOneAdapter() {
        specifications = serviceListViewModel.specifications.sortedBy { it.sequenceNumber }.filter {it.isParentAssociate==true }
        val id = serviceListViewModel.specifications[0].list[0].id
        serviceListViewModel.specifications.forEach { specificationsItems->
            if (specificationsItems.isAssociated == true && specificationsItems.modifierId == id ){
                (specifications as ArrayList).add(specificationsItems)
            }
        }
        typeOneAdapter =
            ItemListAdapter(
                specifications,
                this,
                itemClickChild = ::calculateItemAmountChildClick
            )

        bottomSheetServiceCustomizeBinding.rvTypeOne.apply {
            adapter = typeOneAdapter
        }
    }

    private fun calculateItemAmountChildClick(childItem : ListItem,isSingleClick : Boolean){
        if (isSingleClick){
            specifications = serviceListViewModel.specifications.sortedBy { it.sequenceNumber }.filter {it.isParentAssociate==true }
            val id = childItem.id
            serviceListViewModel.specifications.forEach { specificationsItems->
                if (specificationsItems.isAssociated == true && specificationsItems.modifierId == id ){
                    (specifications as ArrayList).add(specificationsItems)
                }
            }
            serviceListViewModel.serviceResponse.specifications = specifications
            calculateCardAMount()
            typeOneAdapter.setData(specifications as ArrayList)
        }else{
            calculateCardAMount()
        }
    }

    @SuppressLint("SetTextI18n")
    fun calculateCardAMount() {
        serviceListViewModel.currentPrice = onlyPrice()
        bottomSheetServiceCustomizeBinding.tvButtonAddToCard.text =
            "Add To Cart - ${Utils.getCurrencySymbol("INR")}${((serviceListViewModel.currentPrice) * serviceListViewModel.mainCount)}"
    }

    private fun onlyPrice(): Double {
        var price = 0.00
        for (i in serviceListViewModel.specifications) {
            if (i.type == 1) {
                for (j in i.list as ArrayList<ListItem>) {
                    if (j.isDefaultSelected) {
                        price += j.price?.toDouble()!!
                        break
                    }
                }
            }
        }

        for (i in serviceListViewModel.serviceResponse.specifications) {
            if (i.type == 2) {
                for (j in i.list) {
                    if (j.isDefaultSelected) {
                        price += ((j.price?.toDouble()!!) * j.count)
                    }
                }
            }
        }
        return price
    }

    private fun checkIsRequiredSelected(): Boolean {
        for (i in typeOneAdapter.itemList as ArrayList<SpecificationsItem> ) {
            if (i.isRequired) {
                var isSelected = false
                for (j in i.list) {
                    if (j.isDefaultSelected) {
                        isSelected = j.isDefaultSelected
                    }
                }
                if (!isSelected) {
                    return false
                }
            }
        }
        return true
    }

    @SuppressLint("NewApi")
    fun statusBarColor(id: Int) {
        window.decorView.systemUiVisibility = 0
        window.statusBarColor = ContextCompat.getColor(this, id)
    }
}
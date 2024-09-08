package com.example.servicelist.model

import com.google.gson.annotations.SerializedName

data class ServiceResponse(

	@field:SerializedName("item_taxes")
	val itemTaxes: List<Int?>? = null,

	@field:SerializedName("price")
	val price: Int? = null,

	@field:SerializedName("name")
	val name: List<String?>? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("specifications")
	var specifications: List<SpecificationsItem> = arrayListOf()
)

data class SpecificationsItem(

	@field:SerializedName("unique_id")
	val uniqueId: Int? = null,

	@field:SerializedName("modifierId")
	val modifierId: String? = null,

	@field:SerializedName("range")
	val range: Int? = null,

	@field:SerializedName("type")
	val type: Int? = null,

	@field:SerializedName("isAssociated")
	val isAssociated: Boolean? = null,

	@field:SerializedName("list")
	val list: List<ListItem> = arrayListOf(),

	@field:SerializedName("sequence_number")
	val sequenceNumber: Int? = null,

	@field:SerializedName("max_range")
	val maxRange: Int? = null,

	@field:SerializedName("is_required")
	var isRequired: Boolean = false,

	@field:SerializedName("name")
	val name: List<String?>? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("user_can_add_specification_quantity")
	val userCanAddSpecificationQuantity: Boolean? = null,

	@field:SerializedName("modifierGroupId")
	val modifierGroupId: String? = null,

	@field:SerializedName("isParentAssociate")
	val isParentAssociate: Boolean? = null,
	var isMultipleAllowed : Boolean =false
)

data class ListItem(

	@field:SerializedName("sequence_number")
	val sequenceNumber: Int? = null,

	@field:SerializedName("unique_id")
	val uniqueId: Int? = null,

	@field:SerializedName("price")
	val price: Int? = null,

	@field:SerializedName("name")
	val name: List<String?>? = null,

	@field:SerializedName("specification_group_id")
	val specificationGroupId: String? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("is_default_selected")
	var isDefaultSelected: Boolean = false,

	var count:Int = 1
)

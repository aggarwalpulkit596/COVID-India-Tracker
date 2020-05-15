package com.example.tracker.data.models

import com.google.gson.annotations.SerializedName

data class StateData (

	@SerializedName("state") val state : String,
	@SerializedName("statecode") val statecode : String,
	@SerializedName("districtData") val districtData : List<DistrictData>
)


data class DistrictData (

	@SerializedName("district") val district : String,
	@SerializedName("notes") val notes : String,
	@SerializedName("active") val active : Int,
	@SerializedName("confirmed") val confirmed : Int,
	@SerializedName("deceased") val deceased : Int,
	@SerializedName("recovered") val recovered : Int,
	@SerializedName("delta") val delta : Delta
)

data class Delta (

	@SerializedName("confirmed") val confirmed : Int,
	@SerializedName("deceased") val deceased : Int,
	@SerializedName("recovered") val recovered : Int
)

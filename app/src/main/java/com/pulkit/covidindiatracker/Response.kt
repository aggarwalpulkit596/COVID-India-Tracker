package com.pulkit.covidindiatracker

data class Response(
	val statewise: List<StatewiseItem>
)

data class StatewiseItem(
	val deltarecovered: String? = null,
	val deltaactive: String? = null,
	val deltaconfirmed: String? = null,
	val deltadeaths: String? = null,

	val recovered: String? = null,
	val active: String? = null,
	val state: String? = null,
	val confirmed: String? = null,
	val deaths: String? = null,
	val lastupdatedtime: String? = null
)


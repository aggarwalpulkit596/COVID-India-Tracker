package com.pulkit.covidindiatracker

data class Response(
	val statewise: List<StatewiseItem>
)

data class StatewiseItem(
	val recovered: String? = null,
	val delta: Delta? = null,
	val active: String? = null,
	val state: String? = null,
	val confirmed: String? = null,
	val deaths: String? = null,
	val lastupdatedtime: String? = null
)

data class Delta(
	val recovered: Int? = null,
	val active: Int? = null,
	val confirmed: Int? = null,
	val deaths: Int? = null
)

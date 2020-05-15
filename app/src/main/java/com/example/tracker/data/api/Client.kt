package com.example.tracker.data.api

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Client
{
  private  val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
    private val retrofit = Retrofit.Builder().baseUrl("https://api.covid19india.org/").addConverterFactory(GsonConverterFactory.create(gson)).build()
     val api = retrofit.create(CovidService::class.java)
}
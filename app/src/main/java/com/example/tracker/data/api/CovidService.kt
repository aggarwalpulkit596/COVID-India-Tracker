package com.example.tracker.data.api
import com.example.tracker.data.models.StateData
import com.example.tracker.data.models.response
import retrofit2.Response
import retrofit2.http.GET

interface CovidService {

    @GET("data.json")
    suspend fun getStates(): Response<response>

    @GET("v2/state_district_wise.json")
    suspend fun getDistrict(): Response<ArrayList<StateData>>

}

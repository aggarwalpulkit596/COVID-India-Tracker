package com.example.tracker.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.AbsListView
import androidx.appcompat.widget.SearchView
import com.example.tracker.R
import com.example.tracker.data.api.Client
import com.example.tracker.data.models.DistrictData
import com.example.tracker.data.models.StateData
import com.example.tracker.ui.adapter.DistrictAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_state.*
import kotlinx.android.synthetic.main.activity_state.list
import kotlinx.android.synthetic.main.activity_state.sv
import kotlinx.android.synthetic.main.activity_state.swipeToRefresh
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class State : AppCompatActivity() {

    lateinit var res: Response<ArrayList<StateData>>
    var stateName = ""
    lateinit var districtAdapter: DistrictAdapter
    var dislist: ArrayList<StateData>? = ArrayList()
    var dis: ArrayList<DistrictData> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_state)
        stateName = intent.getStringExtra("state")

        list.addHeaderView(LayoutInflater.from(this).inflate(R.layout.list_header, list, false))
        list.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {}
            override fun onScroll(
                view: AbsListView,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int
            ) {
                if (list.getChildAt(0) != null) {
                    swipeToRefresh.isEnabled = list.firstVisiblePosition === 0 && list.getChildAt(
                        0
                    ).getTop() === 0
                }
            }
        })

        fetchResults()
        swipeToRefresh.setOnRefreshListener {
            fetchResults()
        }
        sv.isSubmitButtonEnabled = true
        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {

                    searchStates(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                newText?.let {
                    searchStates(it)
                }

                return true
            }
        })

        sv.setOnCloseListener {

            res.body().let {

                bindStateWiseData(s()!!)

            }
            true


        }
    }
    private fun fetchResults() {
        GlobalScope.launch(Dispatchers.Main) {

            res = withContext(Dispatchers.IO) {

                Client.api.getDistrict()
            }

            if (res.isSuccessful) {

                swipeToRefresh.isRefreshing = false

                res.body().let {

                    for (item in it!!) {
                        if (item.state == stateName) {
                            bindStateWiseData(item.districtData.subList(0, item.districtData.size))
                            break
                        }

                    }
                }

            }


        }
    }


    private fun bindStateWiseData(subList: List<DistrictData>) {
        districtAdapter = DistrictAdapter(subList)
        list.adapter = districtAdapter

    }

    private fun searchStates(newText: String = "") {
        var statesOrg: ArrayList<DistrictData> = ArrayList()

        statesOrg.apply {
            this.addAll(
                s()?.filter {
                    it.district!!.contains(newText, ignoreCase = true)
                }!!
            )


        }
        bindStateWiseData(statesOrg)


    }

    fun s(): List<DistrictData>? {

        res.body().let {

            for (item in it!!) {
                if (item.state == stateName) {
                  return item.districtData.subList(0, item.districtData.size)

                }

            }
        }
        return null
    }
}

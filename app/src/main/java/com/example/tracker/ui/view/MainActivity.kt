package com.example.tracker.ui.view

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.tracker.R
import com.example.tracker.data.api.Client
import com.example.tracker.data.models.StatewiseItem
import com.example.tracker.data.models.response
import com.example.tracker.ui.adapter.StateListAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    lateinit var stateListAdapter:StateListAdapter
    lateinit var res: Response<response>
    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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

        if (!isNetworkConnected()) {
            Toast.makeText(this, "Connect to Internet", Toast.LENGTH_LONG).show()
        } else {
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
            initWorker()
            sv.setOnCloseListener {

                bindStateWiseData(res.body()!!.statewise.subList(0, res.body()!!.statewise.size))

                true

            }


        }
    }
    private fun searchStates(newText: String = "") {
        var statesOrg: ArrayList<StatewiseItem> = ArrayList()

        statesOrg.apply {

            this.addAll(
                res.body()!!.statewise.filter {
                    it.state!!.contains(newText, ignoreCase = true)
                }
            )
        }
        bindStateWiseData(statesOrg)


    }


    private fun fetchResults() {
        GlobalScope.launch(Dispatchers.Main) {

            res = withContext(Dispatchers.IO) {

                Client.api.getStates()
            }

            if (res.isSuccessful) {
                swipeToRefresh.isRefreshing = false
                bindCombinedData(res.body()!!.statewise[0])
                bindStateWiseData(res.body()!!.statewise.subList(0, res.body()!!.statewise.size))


            }
        }



    }

    private fun bindStateWiseData(subList: List<StatewiseItem>) {
        stateListAdapter = StateListAdapter(subList)
        list.adapter = stateListAdapter
        stateListAdapter.onItemClick = {
            val intent = Intent(this, State::class.java)
            intent.putExtra("state", it)
            startActivity(intent)
        }
    }

    private fun bindCombinedData(data: StatewiseItem) {
        val lastUpdatedTime = data.lastupdatedtime
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        lastUpdatedTv.text = "Last Updated\n ${getTimeAgo(
            simpleDateFormat.parse(lastUpdatedTime)
        )}"

        confirmedTv.text = data.confirmed
        activeTv.text = data.active
        recoveredTv.text = data.recovered
        deceasedTv.text = data.deaths

    }

    fun getTimeAgo(past: Date): String {
        val now = Date()
        val seconds = TimeUnit.MILLISECONDS.toSeconds(now.time - past.time)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(now.time - past.time)
        val hours = TimeUnit.MILLISECONDS.toHours(now.time - past.time)

        return when {
            seconds < 60 -> {
                "Few seconds ago"
            }
            minutes < 60 -> {
                "$minutes minutes ago"
            }
            hours < 24 -> {
                "$hours hour ${minutes % 60} min ago"
            }
            else -> {
                SimpleDateFormat("dd/MM/yy, hh:mm a").format(past).toString()

            }
        }

    }

    private fun isNetworkConnected(): Boolean {
        var cm: ConnectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (cm != null) {
            val cap = cm.getNetworkCapabilities(cm.activeNetwork)
            if (cap != null) {
                return true
            }
        }

        return false
    }

    @InternalCoroutinesApi
    private fun initWorker() {
        val constraints = androidx.work.Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val notificationWorkRequest =
            PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "JOB_TAG",
            ExistingPeriodicWorkPolicy.KEEP,
            notificationWorkRequest
        )
    }


}


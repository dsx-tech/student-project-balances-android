package com.example.mvvmkotlincoroutineretrofitdemo.view

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.aachartmodel.aainfographics.AAInfographicsLib.AAChartConfiger.AAChartView
import com.example.mvvmkotlincoroutineretrofitdemo.R
import com.example.mvvmkotlincoroutineretrofitdemo.viewmodel.MainViewModel
import com.example.mvvmkotlincoroutineretrofitdemo.constants.Days
import java.util.*
import android.widget.TextView




class MainActivity : AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inf: MenuInflater = menuInflater
        inf.inflate(R.menu.popup_menu_with_graphs, menu)

        return true
    }

    private lateinit var mainViewModel: MainViewModel
    private lateinit var rateAdapter: RateAdapter
    private lateinit var transAdapter: TransAdapter
    private lateinit var tradesAdapter: TradesAdapter
    private lateinit var chartAdapter: ChartAdapter

    private var aaChartView: AAChartView? = null
    private var aaChartView2: AAChartView? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        aaChartView = findViewById(R.id.AAChartView)



        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        rateAdapter = RateAdapter()
        transAdapter = TransAdapter()
        tradesAdapter = TradesAdapter()
        chartAdapter = ChartAdapter()

        registerObservers()

        //calling user list api
        mainViewModel.getTrades()
        mainViewModel.getTrans()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu1 -> {
                setContentView(R.layout.activity_main)
                aaChartView = findViewById(R.id.AAChartView)
                if (!mainViewModel.balancesAtTheEnd.value.isNullOrEmpty())
                    chartAdapter.setData(mainViewModel.balancesAtTheEnd.value!!, aaChartView)
                else{
                    mainViewModel.getTrades()
                    mainViewModel.getTrans()
                }
                true
            }
            R.id.menu2 -> {

                setContentView(R.layout.activity_rate_graph)
                val button :Button = findViewById(R.id.rate_graph_draw)
                val currencies1 : Spinner = findViewById(R.id.Spinner1)
                val currenciesArray = arrayOf("BTC", "USD", "EUR", "BCH", "RUB")
                currencies1.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, currenciesArray)
                currencies1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        (parent?.getChildAt(0) as TextView).setTextColor(Color.WHITE)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                }
                val currencies2 : Spinner = findViewById(R.id.Spinner2)
                currencies2.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, currenciesArray)
                currencies2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        (parent?.getChildAt(0) as TextView).setTextColor(Color.WHITE)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                }
                button.setOnClickListener{
                    var timeNow = Calendar.getInstance().timeInMillis / 1000
                    var time = timeNow - Days.MONTH_IN_SEC
                    mainViewModel.getRates("${currencies1.selectedItem.toString().toLowerCase()}-${currencies2.selectedItem.toString().toLowerCase()}",
                        time, timeNow)
                }
                true
            }
            else -> false
        }

    }

    private fun registerObservers() {

        mainViewModel.rateSuccessLiveData.observe(this, Observer { rates ->
            aaChartView2 = findViewById(R.id.AAChartView2)
            //if it is not null then we will display all users
            rates?.let {
                rateAdapter.setRates(it)
            }
            val cur1: Spinner = findViewById(R.id.Spinner1)
            val cur2: Spinner = findViewById(R.id.Spinner2)
            chartAdapter.setData2(mainViewModel.rateSuccessLiveData.value!!, aaChartView2,cur1.selectedItem.toString(), cur2.selectedItem.toString() )
        })

        mainViewModel.tradesSuccessLiveData.observe(this, Observer { tradesList ->

            //if it is not null then we will display all users
            tradesList?.let {
                tradesAdapter.setTrades(it)
            }
        })
        mainViewModel.transSuccessLiveData.observe(this, Observer { transactionList ->

            //if it is not null then we will display all users
            transactionList?.let {
                transAdapter.setTrans(it)
            }
        })

        mainViewModel.tradesFailureLiveData.observe(this, Observer { isFailed ->

            //if it is not null then we will display all users
            isFailed?.let {
                Toast.makeText(this, "Oops! something went wrong", Toast.LENGTH_SHORT).show()
            }
        })
        mainViewModel.transFailureLiveData.observe(this, Observer { isFailed ->

            //if it is not null then we will display all users
            isFailed?.let {
                Toast.makeText(this, "Oops! something went wrong", Toast.LENGTH_SHORT).show()
            }
        })

        tradesAdapter.tradesDownloaded.observe(this, Observer {


            if (transAdapter.transDownloaded.value == true){
                mainViewModel.countingBalance()

            }
        })
        transAdapter.transDownloaded.observe(this, Observer {
            if (tradesAdapter.tradesDownloaded.value == true){
                mainViewModel.countingBalance()

            }
        })
        mainViewModel.balancesAtTheEnd.observe(this, Observer {
                chartAdapter.setData(mainViewModel.balancesAtTheEnd.value!!, aaChartView)

            }
        )

    }
}

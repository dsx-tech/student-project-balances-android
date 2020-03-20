package com.example.mvvmkotlincoroutineretrofitdemo.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.widget.ToolbarWidgetWrapper
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewModelScope
import com.aachartmodel.aainfographics.AAInfographicsLib.AAChartConfiger.AAChartView
import com.example.mvvmkotlincoroutineretrofitdemo.R
import com.example.mvvmkotlincoroutineretrofitdemo.viewmodel.MainViewModel
import com.example.mvvmkotlincoroutineretrofitdemo.constants.Days
import com.example.mvvmkotlincoroutineretrofitdemo.constants.Currencies
import java.util.*


class MainActivity : AppCompatActivity() {


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inf: MenuInflater = menuInflater
        inf.inflate(R.menu.popup_menu_with_graphs, menu)
        inf.toString()
        return true
    }


    private lateinit var mainViewModel: MainViewModel
    private lateinit var rateAdapter: RateAdapter
    private lateinit var transAdapter: TransAdapter
    private lateinit var tradesAdapter: TradesAdapter
    private lateinit var chartAdapter: ChartAdapter

    private var aaChartView: AAChartView? = null
    private var aaChartView2: AAChartView? = null
    private var aaChartView3: AAChartView? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        loginSetup()


    }

    private fun loginSetup(){
        setContentView(R.layout.login_activity)
        val register:TextView = findViewById(R.id.register)
        register.setOnClickListener{
            setContentView(R.layout.register_activity)
            val password:EditText = findViewById(R.id.editText2)
            val passwordRepeat:EditText = findViewById(R.id.editText3)
            val createAccount:Button = findViewById(R.id.regButton)
            createAccount.setOnClickListener {
                if ((password.text.toString() == passwordRepeat.text.toString()) and (password.text.toString() !=""))
             loginSetup()
            }
        }
        val loginButton :Button = findViewById(R.id.loginButton)
        loginButton.setOnClickListener{
            setContentView(R.layout.activity_main)
            aaChartView = findViewById(R.id.AAChartView)
            val toolbar:Toolbar= findViewById(R.id.toolBar)
            toolbar.setTitleTextColor(getColor(R.color.white))
            setSupportActionBar(toolbar)
            mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

            rateAdapter = RateAdapter()
            transAdapter = TransAdapter()
            tradesAdapter = TradesAdapter()
            chartAdapter = ChartAdapter()

            registerObservers()

            mainViewModel.getTrades()
            mainViewModel.getTrans()

        }
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.menu1 -> {
                setContentView(R.layout.activity_main)
                val toolbar:Toolbar= findViewById(R.id.toolBar)
                toolbar.setTitleTextColor(getColor(R.color.white))
                setSupportActionBar(toolbar)
                aaChartView = findViewById(R.id.AAChartView)
                if (!mainViewModel.balancesAtTheEnd.value.isNullOrEmpty())
                    mainViewModel.getStringWithInstruments()
                else {
                    mainViewModel.getTrades()
                    mainViewModel.getTrans()
                }
                true
            }
            R.id.menu2 -> {

              setContentView(R.layout.activity_rate_graph)
               val toolbar:Toolbar= findViewById(R.id.toolBar)
              toolbar.setTitleTextColor(getColor(R.color.white))
               setSupportActionBar(toolbar)
                val button: Button = findViewById(R.id.rate_graph_draw)
                val currencies1: AutoCompleteTextView = findViewById(R.id.autoCoCur1)
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    Currencies.currenciesArray
                )
                currencies1.threshold = 0
                currencies1.setAdapter(adapter)
                currencies1.setOnFocusChangeListener { _, b -> if (b) currencies1.showDropDown() }

                val currencies2: AutoCompleteTextView = findViewById(R.id.autoCoCur2)
                val adapter2 = ArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    Currencies.currenciesArray
                )
                currencies2.threshold = 0
                currencies2.setAdapter(adapter2)
                currencies2.setOnFocusChangeListener { _, b -> if (b) currencies2.showDropDown() }


                button.setOnClickListener {
                    button.clearFocus()

                    val timeNow = Calendar.getInstance().timeInMillis / 1000
                    val time = timeNow - Days.MONTH_IN_SEC
                    mainViewModel.getRates(
                        "${currencies1.text.toString().toLowerCase()}-${currencies2.text.toString().toLowerCase()}",
                        time, timeNow
                    )
                    hideKeyboardFrom(this, it)
                }
                true
            }
            R.id.menu3 ->{
                setContentView(R.layout.activity_column_graph)
                val toolbar:Toolbar= findViewById(R.id.toolBar)
                toolbar.setTitleTextColor(getColor(R.color.white))
                setSupportActionBar(toolbar)
                aaChartView = findViewById(R.id.AAChartView3)
                val button: Button = findViewById(R.id.rate_column_graph)
                var editText:EditText = findViewById(R.id.year)
                button.setOnClickListener {
                    try{

                    if((mainViewModel.tradesSuccessLiveData.value?.isNotEmpty() == true) or (mainViewModel.transSuccessLiveData.value?.isNotEmpty() == true))
                        mainViewModel.modelingColumnGraph(editText.text.toString().toInt(), mainViewModel.tradesSuccessLiveData.value, mainViewModel.transSuccessLiveData.value )
                } catch(e:NumberFormatException){
                        Toast.makeText(this, "Please, enter year", Toast.LENGTH_SHORT).show()
                    }}

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
            val cur1: AutoCompleteTextView = findViewById(R.id.autoCoCur1)
            val cur2: AutoCompleteTextView = findViewById(R.id.autoCoCur2)
            chartAdapter.setData2(
                mainViewModel.rateSuccessLiveData.value!!,
                aaChartView2,
                cur1.text.toString(),
                cur2.text.toString()
            )
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
        mainViewModel.stringWithInstruments.observe(this, Observer {
            mainViewModel.getRelevantRates(mainViewModel.stringWithInstruments.value!!)
        })

        tradesAdapter.tradesDownloaded.observe(this, Observer {
            if (transAdapter.transDownloaded.value == true) {
                mainViewModel.countingBalance()
            }
        })
        transAdapter.transDownloaded.observe(this, Observer {
            if (tradesAdapter.tradesDownloaded.value == true) {
                mainViewModel.countingBalance()

            }
        })
        mainViewModel.balancesAtTheEnd.observe(this, Observer {

            mainViewModel.getStringWithInstruments()
        }
        )

        mainViewModel.relevantRatesSuccessLiveData.observe(this, Observer {

            mainViewModel.multiplyRelevant()
        }
        )

        mainViewModel.balancesMultRates.observe(this, Observer {

            chartAdapter.setData(mainViewModel.balancesMultRates.value!!, aaChartView)
        }
        )
        mainViewModel.columnGraphData.observe(this, Observer {
            var k = findViewById<EditText>(R.id.year)
            aaChartView3 = findViewById(R.id.AAChartView3)
            chartAdapter.setData3(aaChartView3, mainViewModel.columnGraphData.value!!, k.text.toString().toInt())
        })
        mainViewModel.yearBalanceLiveData.observe(this, Observer {
            mainViewModel.getStringWithInstrumentsForColumn((mainViewModel.yearBalanceLiveData.value)!![12]!!.keys.toMutableList())
        })
        mainViewModel.stringWithInstruments2.observe(this, Observer {
            var k = findViewById<EditText>(R.id.year)
           mainViewModel.getRatesForTime(mainViewModel.stringWithInstruments2.value!!, k.text.toString().toInt())
        })

    }

    private fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

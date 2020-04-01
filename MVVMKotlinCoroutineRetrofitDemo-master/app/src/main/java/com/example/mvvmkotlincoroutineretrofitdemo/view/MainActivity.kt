package com.example.mvvmkotlincoroutineretrofitdemo.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aachartmodel.aainfographics.AAInfographicsLib.AAChartConfiger.AAChartView
import com.example.mvvmkotlincoroutineretrofitdemo.R
import com.example.mvvmkotlincoroutineretrofitdemo.viewmodel.MainViewModel
import com.example.mvvmkotlincoroutineretrofitdemo.constants.Days
import com.example.mvvmkotlincoroutineretrofitdemo.constants.Currencies
import com.example.mvvmkotlincoroutineretrofitdemo.model.LoginBody
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import android.content.SharedPreferences
import com.anychart.AnyChart
import com.example.mvvmkotlincoroutineretrofitdemo.model.Portfolio
import com.example.mvvmkotlincoroutineretrofitdemo.repository.RepositoryForInputOutput
import com.example.mvvmkotlincoroutineretrofitdemo.repository.RepositoryForRelativeRates
import com.github.mikephil.charting.charts.PieChart


class MainActivity : AppCompatActivity() {


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inf: MenuInflater = menuInflater
        inf.inflate(R.menu.popup_menu_with_graphs, menu)
        inf.toString()
        return true
    }

    private val APP_PREFERENCES = "mysettings"
    private val APP_PREFERENCES_TOKEN = "token"
    lateinit var pref: SharedPreferences
    private lateinit var mainViewModel: MainViewModel
    private lateinit var rateAdapter: RateAdapter
    private lateinit var transAdapter: TransAdapter
    private lateinit var tradesAdapter: TradesAdapter
    private lateinit var chartAdapter: ChartAdapter
    private lateinit var ratesChartAdapter: RatesChartAdapter
    private lateinit var columnChartAdapter: ColumnChartAdapter
    private lateinit var incomeChartAdapter: IncomeChartAdapter
    private lateinit var curBalanceChartAdapter: CurBalanceChartAdapter
    private lateinit var rvPortfolioAdapter: RVPortfolioAdapter
    private lateinit var relativeRatesAdapter: RelativeRatesAdapter
    private lateinit var repositoryForRelativeRates: RepositoryForRelativeRates
    private lateinit var repositoryForInputOutput: RepositoryForInputOutput
    private lateinit var inputOutputAdapter: InputOutputAdapter


    private var aaChartView: AAChartView? = null
    private var chart: PieChart? = null



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        pref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        setContentView(R.layout.activity_portfolios)

         mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
         rateAdapter = RateAdapter()
         transAdapter = TransAdapter()
         tradesAdapter = TradesAdapter()
         chartAdapter = ChartAdapter()
         ratesChartAdapter = RatesChartAdapter()
         columnChartAdapter = ColumnChartAdapter()
         incomeChartAdapter = IncomeChartAdapter()
         curBalanceChartAdapter = CurBalanceChartAdapter()
         rvPortfolioAdapter = RVPortfolioAdapter()
         relativeRatesAdapter = RelativeRatesAdapter()
         repositoryForRelativeRates = RepositoryForRelativeRates()
         repositoryForInputOutput = RepositoryForInputOutput()
         inputOutputAdapter = InputOutputAdapter()

         registerObservers()
         loginSetup()


    }

    override fun onStop() {
        super.onStop()

        val editor = pref.edit()
        editor.clear()
        editor.apply()
    }
    private fun loginSetup(){
        setContentView(R.layout.login_activity)
        val register:TextView = findViewById(R.id.register)
        val back : RelativeLayout = findViewById(R.id.back)
        back.setOnClickListener {
            hideKeyboardFrom(this, it)
        }
        register.setOnClickListener{

            startActivity(Intent(this@MainActivity, RegisterView()::class.java))
        }
        val loginButton :Button = findViewById(R.id.loginButton)
        
        loginButton.setOnClickListener{
            val username :EditText = findViewById(R.id.userName)
            val passwordActual:EditText = findViewById(R.id.password)
            mainViewModel.auth(LoginBody(username.text.toString(), passwordActual.text.toString()))

        }
    }

    private fun setPortfolios(){

        setContentView(R.layout.activity_portfolios)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = rvPortfolioAdapter
    }
    private fun completedAuth(){

        var token :String? = null
        if (pref.contains(APP_PREFERENCES_TOKEN)) {
            token  = pref.getString(APP_PREFERENCES_TOKEN, "-");
        }
        mainViewModel.getPortfolios(token!!)

    }
    private fun showGraphs(){
        setContentView(R.layout.activity_main)
        val toolbar:Toolbar= findViewById(R.id.toolBar)
        toolbar.setTitleTextColor(getColor(R.color.white))
        setSupportActionBar(toolbar)

        chart = findViewById(R.id.chart)
        var token :String? = null
        if (pref.contains(APP_PREFERENCES_TOKEN)) {
            token  = pref.getString(APP_PREFERENCES_TOKEN, "-");
        }
        mainViewModel.getTrades(token!!)
        mainViewModel.getTrans(token)
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.backTo -> {
                setPortfolios()
                true
            }
            R.id.menu1 -> {
                setContentView(R.layout.activity_main)
                val toolbar:Toolbar= findViewById(R.id.toolBar)
                toolbar.setTitleTextColor(getColor(R.color.white))
                setSupportActionBar(toolbar)
                chart = findViewById(R.id.chart)
                if (!mainViewModel.balancesAtTheEnd.value.isNullOrEmpty())
                    mainViewModel.getStringWithInstruments()
                else {
                    var token :String? = null
                    if (pref.contains(APP_PREFERENCES_TOKEN)) {
                        token  = pref.getString(APP_PREFERENCES_TOKEN, "-");
                    }
                    mainViewModel.getTrades(token!!)
                    mainViewModel.getTrans(token)
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
                aaChartView = findViewById(R.id.AAChartView)
                val button: Button = findViewById(R.id.rate_column_graph)
                val editText:EditText = findViewById(R.id.year)
                button.setOnClickListener {
                    try{

                    if((mainViewModel.tradesSuccessLiveData.value?.isNotEmpty() == true) or (mainViewModel.transSuccessLiveData.value?.isNotEmpty() == true))
                        mainViewModel.modelingColumnGraph(editText.text.toString().toInt(), mainViewModel.tradesSuccessLiveData.value, mainViewModel.transSuccessLiveData.value )
                } catch(e:NumberFormatException){
                        Toast.makeText(this, "Please, enter year", Toast.LENGTH_SHORT).show()
                    }}

                true
            }
            R.id.menu4 ->{
                setContentView(R.layout.activity_income_graph)
                val toolbar:Toolbar= findViewById(R.id.toolBar)
                toolbar.setTitleTextColor(getColor(R.color.white))
                setSupportActionBar(toolbar)
                aaChartView = findViewById(R.id.AAChartView)
                val button: Button = findViewById(R.id.income_graph_draw)
                val spinner: Spinner = findViewById(R.id.incomeCur)
                val adapter = ArrayAdapter(this,
                    android.R.layout.simple_spinner_item, mainViewModel.balancesAtTheEnd.value?.keys!!.toList())
                spinner.adapter = adapter
                spinner.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener{


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


                val textDateFrom: TextView  = findViewById(R.id.incomeDateFrom)
                val textDateTo: TextView = findViewById(R.id.incomeDateTo)
                        textDateFrom.text = SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis())
                        textDateTo.text = SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis())

                val cal = Calendar.getInstance()
                val myFormat = "dd.MM.yyyy"
                val dateSetListenerFrom = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)


                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    textDateFrom.text = sdf.format(cal.time)}
                textDateFrom.setOnClickListener {
                    DatePickerDialog(this, dateSetListenerFrom,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)).show()
                }

                val dateSetListenerTo = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    textDateTo.text = sdf.format(cal.time)}
                textDateTo.setOnClickListener {
                    DatePickerDialog(this, dateSetListenerTo,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)).show()
                }
                button.setOnClickListener{
                    mainViewModel.getRatesForIncome(spinner.selectedItem.toString(), textDateFrom.text.toString(), textDateTo.text.toString())
                }

                true
            }
            R.id.menu5 ->{
                setContentView(R.layout.activity_curr_in_portfolio)
                val toolbar:Toolbar= findViewById(R.id.toolBar)
                toolbar.setTitleTextColor(getColor(R.color.white))
                setSupportActionBar(toolbar)
                aaChartView = findViewById(R.id.AAChartView)
                val button: Button = findViewById(R.id.cur_balance_graph_draw)
                val spinner: Spinner = findViewById(R.id.balanceCur)
                val adapter = ArrayAdapter(this,
                    android.R.layout.simple_spinner_item, mainViewModel.balancesAtTheEnd.value?.keys!!.toList())
                spinner.adapter = adapter
                spinner.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener{
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
                val textDateFrom: TextView  = findViewById(R.id.curDateFrom)
                val textDateTo: TextView = findViewById(R.id.curDateTo)
                textDateFrom.text = SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis())
                textDateTo.text = SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis())
                val cal = Calendar.getInstance()
                val myFormat = "dd.MM.yyyy"
                val dateSetListenerFrom = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)


                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    textDateFrom.text = sdf.format(cal.time)}
                textDateFrom.setOnClickListener {
                    DatePickerDialog(this, dateSetListenerFrom,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)).show()
                }

                val dateSetListenerTo = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    textDateTo.text = sdf.format(cal.time)}
                textDateTo.setOnClickListener {
                    DatePickerDialog(this, dateSetListenerTo,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)).show()
                }
                button.setOnClickListener{
                    mainViewModel.getRatesForCurBalance(spinner.selectedItem.toString(), textDateFrom.text.toString(), textDateTo.text.toString())
                }
                true
            }
            R.id.menu6 ->{
                setContentView(R.layout.activity_for_relative_correl)
                val toolbar:Toolbar= findViewById(R.id.toolBar)
                toolbar.setTitleTextColor(getColor(R.color.white))
                setSupportActionBar(toolbar)
                aaChartView = findViewById(R.id.AAChartView)
                val button: Button = findViewById(R.id.rate_cor_graph_draw)
                val currencies1: AutoCompleteTextView = findViewById(R.id.corCur1)
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    Currencies.currenciesArray
                )
                currencies1.threshold = 0
                currencies1.setAdapter(adapter)
                currencies1.setOnFocusChangeListener { _, b -> if (b) currencies1.showDropDown() }

                val currencies2: AutoCompleteTextView = findViewById(R.id.corCur2)
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
                    mainViewModel.getRatesCor(
                        Pair(currencies1.text.toString().toLowerCase(),currencies2.text.toString().toLowerCase()),
                        time, timeNow
                    )
                    hideKeyboardFrom(this, it)
                }
                true
            }
            R.id.menu7 ->{
                setContentView(R.layout.activity_input_output)
                val toolbar:Toolbar= findViewById(R.id.toolBar)
                toolbar.setTitleTextColor(getColor(R.color.white))
                setSupportActionBar(toolbar)
                aaChartView = findViewById(R.id.AAChartView)
                val button: Button = findViewById(R.id.input_graph_draw)
                val textDateFrom: TextView  = findViewById(R.id.input_DateFrom)
                val textDateTo: TextView = findViewById(R.id.input_DateTo)
                textDateFrom.text = SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis())
                textDateTo.text = SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis())
                val cal = Calendar.getInstance()
                val myFormat = "dd.MM.yyyy"
                val dateSetListenerFrom = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)


                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    textDateFrom.text = sdf.format(cal.time)}
                textDateFrom.setOnClickListener {
                    DatePickerDialog(this, dateSetListenerFrom,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)).show()
                }

                val dateSetListenerTo = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    textDateTo.text = sdf.format(cal.time)}
                textDateTo.setOnClickListener {
                    DatePickerDialog(this, dateSetListenerTo,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)).show()
                }
                button.setOnClickListener{
                    mainViewModel.filterTrans(mainViewModel.transSuccessLiveData.value!!, textDateFrom.text.toString(), textDateTo.text.toString())
                }
                true
            }
            else -> false
        }

    }

    private fun registerObservers() {

        mainViewModel.rateSuccessLiveData.observe(this, Observer { rates ->
            aaChartView = findViewById(R.id.AAChartView)
            //if it is not null then we will display all users
            rates?.let {
                rateAdapter.setRates(it)
            }
            val cur1: AutoCompleteTextView = findViewById(R.id.autoCoCur1)
            val cur2: AutoCompleteTextView = findViewById(R.id.autoCoCur2)
            ratesChartAdapter.setRatesChart(
                mainViewModel.rateSuccessLiveData.value!!,
                aaChartView,
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
        mainViewModel.addPortfolioSuccessLiveData.observe(this, Observer { portfolioList ->

            //if it is not null then we will display all users
            portfolioList?.let {
                rvPortfolioAdapter.addPortfolio(it)
                setPortfolios()
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
            mainViewModel.getRelevantRates(it)
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

            chartAdapter.setChart(it, chart!!)
        }
        )
        mainViewModel.columnGraphData.observe(this, Observer {
            val k = findViewById<EditText>(R.id.year)
            aaChartView = findViewById(R.id.AAChartView)
            columnChartAdapter.setColumnChart(aaChartView, it, k.text.toString().toInt())
        })
        mainViewModel.yearBalanceLiveData.observe(this, Observer {
            mainViewModel.getStringWithInstrumentsForColumn(it[12].keys.toMutableList())
        })
        mainViewModel.stringWithInstruments2.observe(this, Observer {
            val k = findViewById<EditText>(R.id.year)
           mainViewModel.getRatesForTime(it, k.text.toString().toInt())
        })

        mainViewModel.authSuccessLiveData.observe(this, Observer {
            val editor = pref.edit()
            editor.putString(APP_PREFERENCES_TOKEN, it)
            editor.apply()
            completedAuth()

        }
        )

        mainViewModel.ratesIncomeSuccessLiveData.observe(this, Observer {
            mainViewModel.modelingSeriesForIncome(it.keys.first(), mainViewModel.tradesSuccessLiveData.value, mainViewModel.transSuccessLiveData.value)
        }
        )
        mainViewModel.resultIncomeLiveData.observe(this, Observer {
           incomeChartAdapter.setIncomeChart(aaChartView, it.first, it.second)
        }
        )
        mainViewModel.ratesCurSuccessLiveData.observe(this, Observer {
           mainViewModel.modelingSeriesForRateInPortfolio(mainViewModel.tradesSuccessLiveData.value, mainViewModel.transSuccessLiveData.value, it.keys.first())
        }
        )
        mainViewModel.resultCurLiveData.observe(this, Observer {
          curBalanceChartAdapter.setCurBalanceChart(aaChartView, it.first, it.second)
        }
        )
        mainViewModel.portfolioSuccessLiveData.observe(this, Observer {
            it.add(Portfolio(-1, "Add"))
            rvPortfolioAdapter.setPortfolios(it)
            setPortfolios()

        }
        )
        rvPortfolioAdapter.selectedPortfolioLiveData.observe(this, Observer {
            if (it.second != -1)
                showGraphs()
            else{
               val dialog = Dialog(this)
                dialog.setContentView(R.layout.add_portfolio)
                val name:EditText = dialog.findViewById(R.id.portfolioName)
                val button : Button = dialog.findViewById(R.id.addPortfolio)
                dialog.show()
                button.setOnClickListener {
                    var token :String? = null
                    if (pref.contains(APP_PREFERENCES_TOKEN)) {
                        token  = pref.getString(APP_PREFERENCES_TOKEN, "-");
                    }
                    mainViewModel.addPortfolio(Portfolio(0, name.text.toString()), token!!)
                    dialog.dismiss()
                }

            }
        }
        )

        mainViewModel.authFailureLiveData.observe(this, Observer {isFailed ->
            isFailed?.let {
                Toast.makeText(this, "Oops! something went wrong", Toast.LENGTH_SHORT).show()
            }

        }
        )
        mainViewModel.rateCurSuccessLiveData.observe(this, Observer {
            relativeRatesAdapter.setRatesChart(it, aaChartView)
        }
        )
        mainViewModel.valuesForInput.observe(this, Observer {
            mainViewModel.getRatesForTimeInput(it.first, it.second.first, it.second.second)
        }
        )
        mainViewModel.inOutSuccessLiveData.observe(this, Observer {
         mainViewModel.calculationInput()
        }
        )
        rvPortfolioAdapter.deletePortfolioLiveData.observe(this, Observer {
            var token :String? = null
            if (pref.contains(APP_PREFERENCES_TOKEN)) {
                token  = pref.getString(APP_PREFERENCES_TOKEN, "-");
            }
            mainViewModel.deletePortfolio(it, token!!)
        }
        )
    }

    private fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

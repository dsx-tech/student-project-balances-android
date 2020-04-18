package com.example.portfolio.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.portfolio.R
import com.example.portfolio.model.RelevantRate
import java.math.BigDecimal

class RVRatesAdapter  : RecyclerView.Adapter<RVRatesAdapter.ViewHolder>() {
    val selectedRateLiveData = MutableLiveData<Pair<String, Float>>()
    val deleteRateLiveData = MutableLiveData<Pair<String, Float>>()
    private var ratesList : MutableList<Pair<String, Float>> = mutableListOf()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.rates_card, p0, false)
        return ViewHolder(v).listen { pos, _ ->
            selectedRateLiveData.postValue(Pair(ratesList[pos].first, ratesList[pos].second))

        }
    }
    fun setRates(rates: MutableMap<String, RelevantRate>) {
        var f :MutableList<Pair<String, Float>> = mutableListOf()
        rates.keys.forEach{f.add(Pair(it, rates[it]!!.exchangeRate.toFloat()))}
        ratesList.addAll(f)
        ratesList = ratesList.distinct().toMutableList()


    }
    fun addRate(rate: Pair<String, BigDecimal>) {

        ratesList.add(0, Pair(rate.first, rate.second.toFloat()))
        ratesList = ratesList.distinct().toMutableList()

    }
    fun deleteRate() {

        ratesList.remove(Pair(deleteRateLiveData.value!!.first, deleteRateLiveData.value!!.second))

    }
    override fun getItemCount(): Int {
        return ratesList.size
    }
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        var name = ratesList[p1].first
        var rate = ratesList[p1].second.toString()
        p0.name?.text = name
        p0.rate?.text = rate
        p0.button.setOnClickListener {
            deleteRateLiveData.postValue( Pair(ratesList[p1].first,  ratesList[p1].second))
        }
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val button = itemView.findViewById<Button>(R.id.btn_clear)
        val name = itemView.findViewById<TextView>(R.id.name)
        val rate = itemView.findViewById<TextView>(R.id.rate)
    }

    private fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(adapterPosition, itemViewType)
        }
        return this

    }


}
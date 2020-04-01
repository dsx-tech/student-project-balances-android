package com.example.mvvmkotlincoroutineretrofitdemo.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvmkotlincoroutineretrofitdemo.R
import com.example.mvvmkotlincoroutineretrofitdemo.model.Portfolio

class RVPortfolioAdapter  : RecyclerView.Adapter<RVPortfolioAdapter.ViewHolder>() {
    val selectedPortfolioLiveData = MutableLiveData<Pair<String, Int>>()
    private val portfolioList : MutableList<Portfolio> = mutableListOf()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.activity_with_potfolios, p0, false)
        return ViewHolder(v).listen { pos, _ ->
            selectedPortfolioLiveData.postValue(Pair(portfolioList[pos].name, portfolioList[pos].id))

        }
    }
    fun setPortfolios(portfolios: MutableList<Portfolio>) {

        portfolioList.addAll(portfolios)

    }
    fun addPortfolio(portfolio:Portfolio) {

        portfolioList.add(0, portfolio)

    }
    override fun getItemCount(): Int {
        return portfolioList.size
    }
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        p0.name?.text = portfolioList[p1].name
        p0.count?.text = portfolioList[p1].id.toString()
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.tvName)
        val count = itemView.findViewById<TextView>(R.id.tvCount)
    }

    fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(adapterPosition, itemViewType)
        }
        return this
    }
}
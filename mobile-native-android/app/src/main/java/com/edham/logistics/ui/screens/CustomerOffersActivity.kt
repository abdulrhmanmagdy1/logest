package com.edham.logistics.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.ui.BaseActivity

class CustomerOffersActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_offers)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        val rv = findViewById<RecyclerView>(R.id.rvOffers)
        rv.layoutManager = LinearLayoutManager(this)
        
        val offers = listOf(
            Offer("خصم الصيف ❄️", "خصم 25% على كافة شحنات التبريد العابرة للمدن.", "كود: SUMMER25"),
            Offer("شحنة مجانية 🚛", "اشحن 10 شحنات واحصل على واحدة مجانية تماماً.", "باقي لك: 4 شحنات"),
            Offer("تأمين مجاني ✅", "تأمين شامل على البضائع الحساسة خلال شهر رمضان.", "عرض محدود")
        )
        
        rv.adapter = OffersAdapter(offers)
    }

    data class Offer(val title: String, val desc: String, val badge: String)

    class OffersAdapter(private val items: List<Offer>) : RecyclerView.Adapter<OffersAdapter.ViewHolder>() {
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val t: TextView = view.findViewById(R.id.tvOfferTitle)
            val d: TextView = view.findViewById(R.id.tvOfferDesc)
            val b: TextView = view.findViewById(R.id.tvOfferBadge)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_customer_offer, parent, false)
            return ViewHolder(v)
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.t.text = item.title
            holder.d.text = item.desc
            holder.b.text = item.badge
        }
        override fun getItemCount() = items.size
    }
}

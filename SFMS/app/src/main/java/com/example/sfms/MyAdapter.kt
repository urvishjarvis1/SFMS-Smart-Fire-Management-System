package com.example.sfms

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(coDataClass: DataClass, co2DataClass: DataClass) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    private val titles = arrayOf("Cafeteria")
    private var mOnItemClickListener: View.OnClickListener? = null
    private var coDataclass=coDataClass
    private var co2DataClass=co2DataClass


    private val images = intArrayOf(R.drawable.gassensor)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_text_view, parent, false) as View
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return titles.size
    }
    fun setOnItemClickListener(itemClickListener: View.OnClickListener) {
        mOnItemClickListener = itemClickListener
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemTitle.text = titles[position]
        holder.itemImage.setImageResource(images[position])
        holder.itemCo.text=coDataclass.data
        holder.itemCo2.text=co2DataClass.data

    }



    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemImage: ImageView
        var itemTitle: TextView
        var itemCo2: TextView
        var itemCo:TextView

        init {
            itemImage = itemView.findViewById(R.id.item_image)
            itemTitle = itemView.findViewById(R.id.item_title)
            itemCo2 = itemView.findViewById(R.id.co2concentration)
            itemCo=itemView.findViewById(R.id.coconcentration)
            itemView.setOnClickListener(mOnItemClickListener);
        }
    }

}

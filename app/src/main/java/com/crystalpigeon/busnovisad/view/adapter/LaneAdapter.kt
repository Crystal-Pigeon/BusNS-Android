package com.crystalpigeon.busnovisad.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.crystalpigeon.busnovisad.R
import com.crystalpigeon.busnovisad.model.BusDatabase
import com.crystalpigeon.busnovisad.model.FavouriteLane
import com.crystalpigeon.busnovisad.model.Lane
import kotlinx.android.synthetic.main.line.view.*
import kotlinx.coroutines.*

class LaneAdapter(
    var lanes: ArrayList<Lane>,
    val context: Context
) :
    RecyclerView.Adapter<LaneAdapter.ViewHolder>() {

    var favLanesDao = BusDatabase.getDatabase(context).favLanesDao()
    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.line,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = lanes.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lanes.elementAt(position))
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(lane: Lane?) {
            view.line_number.text = lane?.number
            view.line_name.text = lane?.laneName

            coroutineScope.launch {
                withContext(Dispatchers.Main){
                    if (favLanesDao.getFavLane(lane!!.id).isEmpty()) view.check.visibility = View.INVISIBLE
                    else view.check.visibility = View.VISIBLE
                }
            }
            view.setOnClickListener {
                coroutineScope.launch(Dispatchers.IO) {
                    if(favLanesDao.getFavLane(lane!!.id).isEmpty()) {
                        val favLane = FavouriteLane(lane.id, lane.type)
                        favLanesDao.insertFavLane(favLane)
                    }
                    else favLanesDao.deleteFavLane(lane.id)

                }
                notifyItemChanged(adapterPosition)
            }
        }
    }
}
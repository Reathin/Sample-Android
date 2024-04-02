package com.reathin.sample

import android.net.wifi.p2p.WifiP2pDevice
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.reathin.sample.databinding.LayoutDeviceItemBinding

class DeviceAdapter(private var deviceList: List<WifiP2pDevice>) :
    RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    var mOnItemSelectedListener: OnItemSelectedListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_device_item, parent, false)
        return DeviceViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.mTvDeviceName.text = deviceList[position].deviceName
        holder.itemView.setOnClickListener {
            mOnItemSelectedListener?.onItemSelected(null, it, position, 0)
        }
    }

    override fun getItemCount() = deviceList.size

    class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var mTvDeviceName: TextView = itemView.findViewById(R.id.tv_device_name)
    }
}
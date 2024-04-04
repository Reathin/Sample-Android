package com.reathin.p2p

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.NetworkInfo
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener
import android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.reathin.sample.databinding.ActivityMainBinding
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.StandardCharsets


class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName

    private lateinit var mBinding: ActivityMainBinding

    private var mWifiP2pManager: WifiP2pManager? = null
    private var mChannel: WifiP2pManager.Channel? = null

    private var mDeviceList = arrayListOf<WifiP2pDevice>()
    private lateinit var mDeviceAdapter: DeviceAdapter

    private var mQuitReadData = true

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(mBinding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        registerReceiver(mReceiver, intentFilter)

        mDeviceAdapter = DeviceAdapter(mDeviceList)
        mBinding.rvDeviceList.adapter = mDeviceAdapter
        mDeviceAdapter.mOnItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val wifiP2pDevice = mDeviceList[position]
                connect(wifiP2pDevice)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        //通用步骤
        mWifiP2pManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager

        mChannel = mWifiP2pManager?.initialize(this, Looper.getMainLooper()) {
            Log.d(TAG, "Channel断开连接")
        }

        //服务端部分
        //服务端创建群组
        mWifiP2pManager?.createGroup(mChannel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d(TAG, "创建群组成功")
            }

            override fun onFailure(reason: Int) {
                Log.w(TAG, "创建群组失败$reason")
            }
        })

        //客户端部分
        //客户端搜索对等设备
        mWifiP2pManager?.discoverPeers(mChannel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d(TAG, "搜索成功")
            }

            override fun onFailure(reason: Int) {
                Log.d(TAG, "搜索失败:$reason")
            }

        })

        //使用异步方法(推荐通过广播监听) 获取设备列表
        mWifiP2pManager?.requestPeers(mChannel) {
            mDeviceList.addAll(it.deviceList)
            if (mDeviceList.isEmpty()) {
                //没有设备
                runOnUiThread { Toast.makeText(this, "没有发现设备", Toast.LENGTH_SHORT).show() }
            } else {
                //刷新列表
                runOnUiThread { mDeviceAdapter.notifyDataSetChanged() }
            }
        }
    }

    /**
     * 连接设备
     */
    private fun connect(wifiP2pDevice: WifiP2pDevice) {
        val config = WifiP2pConfig().apply {
            this.deviceAddress = wifiP2pDevice.deviceAddress
            this.wps.setup = WpsInfo.PBC
        }
        mWifiP2pManager?.connect(mChannel, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d(TAG, "连接成功")
                mQuitReadData = false
                transferData("Hello".toByteArray())
            }

            override fun onFailure(reason: Int) {
                Log.w(TAG, "连接失败$reason")
                mQuitReadData = true
            }

        })
    }

    private fun transferData(data: ByteArray) {
        //请求设备连接信息
        mWifiP2pManager?.requestConnectionInfo(mChannel) { info ->
            if (info.groupFormed && info.isGroupOwner) {
                // 将数据发送给客户端
                val serverSocket = ServerSocket(8888)
                val socket = serverSocket.accept()
                val inputStream = socket.getInputStream()
                val outputStream = socket.getOutputStream()
                //发送数据
                outputStream?.write(data)
                //此处为了方便 实际需要开启线程读取 并且要有合适的延迟
                while (!mQuitReadData) {
                    val reader = inputStream.bufferedReader(StandardCharsets.UTF_8)
                    val text = reader.readLine()
                    Log.d(TAG, "读取到的数据$text")
                }
            } else {
                //设备是客户端
                val address: InetAddress = info.groupOwnerAddress
                val socket = Socket(address, 8888)
                val inputStream = socket.getInputStream()
                val outputStream = socket.getOutputStream()
                //发送数据
                outputStream?.write(data)
                //此处为了方便 实际需要开启线程读取 并且要有合适的延迟
                while (!mQuitReadData) {
                    val reader = inputStream.bufferedReader(StandardCharsets.UTF_8)
                    val text = reader.readLine()
                    Log.d(TAG, "读取到的数据$text")
                }
            }
        }
    }

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action;
            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                // Check to see if Wi-Fi is enabled and notify appropriate activity
                // 检查 Wi-Fi P2P 是否已启用
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                val isEnabled = (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED)
            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                // Call WifiP2pManager.requestPeers() to get a list of current peers
                //异步方法
                // mWifiP2pManager?.requestPeers();
            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                // Respond to new connection or disconnections
                // 链接状态变化回调
                // 此广播 会和 WIFI_P2P_THIS_DEVICE_CHANGED_ACTION 同时回调
                // 注册广播、连接成功、连接失败 三种时机都会调用
                // 应用可使用 requestConnectionInfo()、requestNetworkInfo() 或 requestGroupInfo() 来检索当前连接信息。
            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                // Respond to this device's wifi state changing
                // 此设备的WiFi状态更改回调
                // 此广播 会和 WIFI_P2P_CONNECTION_CHANGED_ACTION 同时回调
                // 注册广播、连接成功、连接失败 三种时机都会调用
                // 应用可使用 requestDeviceInfo() 来检索当前连接信息。
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //移除群组
        mWifiP2pManager?.removeGroup(mChannel, null)
        //取消链接
        mWifiP2pManager?.cancelConnect(mChannel, null)
    }
}
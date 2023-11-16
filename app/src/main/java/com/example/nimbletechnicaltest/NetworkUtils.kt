package com.example.nimbletechnicaltest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager

class NetworkUtils(private val context: Context) {

    interface ConnectivityChangeListener {
        fun onConnectivityChanged(isConnected: Boolean)
    }

    private val connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val connectivityReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            notifyConnectivityChange()
        }
    }

    private val connectivityListeners = mutableListOf<ConnectivityChangeListener>()

    init {
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(connectivityReceiver, filter)
    }

    private fun isConnectedToInternet(): Boolean {
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    fun addConnectivityChangeListener(listener: ConnectivityChangeListener) {
        connectivityListeners.add(listener)
        listener.onConnectivityChanged(isConnectedToInternet())
    }

    fun removeConnectivityChangeListener(listener: ConnectivityChangeListener) {
        connectivityListeners.remove(listener)
    }

    private fun notifyConnectivityChange() {
        val isConnected = isConnectedToInternet()
        for (listener in connectivityListeners) {
            listener.onConnectivityChanged(isConnected)
        }
    }

    fun unregister() {
        context.unregisterReceiver(connectivityReceiver)
        connectivityListeners.clear()
    }
}

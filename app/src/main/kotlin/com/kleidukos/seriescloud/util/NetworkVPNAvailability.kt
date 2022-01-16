package com.kleidukos.seriescloud.util

import android.net.ConnectivityManager
import android.net.NetworkCapabilities

import android.net.Network




class NetworkVPNAvailability {

    companion object{

        fun isVpnAvailable(connectivityManager: ConnectivityManager):Boolean{
            val activeNetwork: Network = connectivityManager.activeNetwork ?: return false
            val caps: NetworkCapabilities? = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

            return caps?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) ?: return false
        }

    }
}
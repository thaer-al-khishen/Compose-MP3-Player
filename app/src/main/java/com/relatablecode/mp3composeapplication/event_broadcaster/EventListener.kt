package com.relatablecode.mp3composeapplication.event_broadcaster

import com.relatablecode.mp3composeapplication.service.ServiceAction

interface EventListener {
    fun onEventReceived(action: ServiceAction?)
}

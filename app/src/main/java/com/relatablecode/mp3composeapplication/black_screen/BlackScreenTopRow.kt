package com.relatablecode.mp3composeapplication.black_screen

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.relatablecode.mp3composeapplication.R
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun BlackScreenTopRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.then(
            Modifier.fillMaxWidth()
        ),
        horizontalArrangement = Arrangement.SpaceBetween,  //Divides the content equally, like SpaceEvenly, but in this case, the start and end composables inside the content are at the edges of the screen
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CurrentTime(modifier = Modifier.padding(top = 16.dp, start = 16.dp))
        CurrentBattery(modifier = Modifier.padding(top = 16.dp, end = 16.dp))
    }
}

@Composable
private fun CurrentTime(modifier: Modifier = Modifier) {
    // State for current time
    val currentTime = remember { mutableStateOf(getFormattedTime()) }

    // Coroutine to update the time every second
    LaunchedEffect(Unit) {
        while (true) {
            currentTime.value = getFormattedTime()
            delay(1000) // 1 second
        }
    }

    Text(text = currentTime.value, color = Color.White, modifier = modifier)
}

private fun getFormattedTime(): String {
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return dateFormat.format(Calendar.getInstance().time)
}

@Composable
private fun CurrentBattery(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // Use a coroutine to periodically refresh the battery level
    val batteryLevel = remember { mutableStateOf(getBatteryLevel(context)) }

    LaunchedEffect(Unit) {
        // This is a simple way to refresh the battery level every 15 seconds
        while (true) {
            delay(15000) // Delay for 15 seconds
            batteryLevel.value = getBatteryLevel(context)
        }
    }

    BatteryIcon(batteryLevel = batteryLevel.value, modifier)
}

private fun getBatteryLevel(context: Context): Int {
    val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
        context.registerReceiver(null, ifilter)
    }
    val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
    val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
    return (level / scale.toFloat() * 100).toInt()
}


@Composable
private fun BatteryIcon(batteryLevel: Int, modifier: Modifier = Modifier) {
    val batteryIconId = when (batteryLevel) {
        in 90..100 -> R.drawable.battery_full
        in 70..89 -> R.drawable.battery_high
        in 30..69 -> R.drawable.battery_mid
        else -> R.drawable.battery_low
    }

    Image(
        painter = painterResource(id = batteryIconId),
        contentDescription = "Battery Level",
        modifier = modifier
            .size(24.dp) // Adjust the size as needed
            .graphicsLayer(rotationZ = 90f) // Rotate 90 degrees around the Z axis to the right
    )
}

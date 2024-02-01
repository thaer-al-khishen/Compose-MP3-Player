package com.relatablecode.mp3composeapplication.circular_control_panel

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.relatablecode.mp3composeapplication.R

@Composable
fun CircularControlPanel(modifier: Modifier = Modifier, backgroundColor: Color = Color.White) {
    val outerCircleSize = 250.dp
    val innerCircleSize = 120.dp

    Box(contentAlignment = Alignment.Center, modifier = modifier.then(Modifier.size(outerCircleSize))) {
        // Outer Circle (Arc)
        Canvas(modifier = Modifier.matchParentSize()) {
            // Background circle
            drawCircle(
                color = backgroundColor,
                radius = size.minDimension / 2
            )
        }
        // Inner Circle (Transparent Center)
        Box(
            modifier = Modifier
                .size(innerCircleSize)
                .clip(CircleShape)
                .background(colorResource(R.color.blue_summer_82)),
            contentAlignment = Alignment.Center
        ) {
            // Your inner content here, if any
        }
        // Positioning the control texts and icons
        ControlItem(text = "Menu", alignment = Alignment.TopCenter)
        ControlItem(icon = ImageVector.vectorResource(id = R.drawable.ic_rewind), alignment = Alignment.CenterStart)
        ControlItem(modifier = Modifier.graphicsLayer(rotationZ = 180f), icon = ImageVector.vectorResource(id = R.drawable.ic_rewind), alignment = Alignment.CenterEnd)
        ControlItem(icon = ImageVector.vectorResource(id = R.drawable.ic_play_pause), alignment = Alignment.BottomCenter)
    }
}

@Composable
private fun ControlItem(modifier: Modifier = Modifier, text: String? = null, icon: ImageVector? = null, alignment: Alignment) {
    Box(
        contentAlignment = alignment,
        modifier = Modifier
            .fillMaxSize()
    ) {

        if (text != null) {
            Text(text = text, color = Color.Black, modifier = modifier.then(Modifier.padding(20.dp)), fontWeight = FontWeight.Bold, fontSize = 20.sp)
        } else if (icon != null) {
            Icon(icon, contentDescription = null, tint = Color.Black, modifier = modifier.then(Modifier.padding(15.dp)))
        }
    }
}

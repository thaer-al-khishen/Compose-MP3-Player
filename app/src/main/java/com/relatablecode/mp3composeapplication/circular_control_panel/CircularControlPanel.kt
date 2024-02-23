package com.relatablecode.mp3composeapplication.circular_control_panel

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.relatablecode.mp3composeapplication.R
import com.relatablecode.mp3composeapplication.theme.LocalAppTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CircularControlPanel(modifier: Modifier = Modifier, onEvent: (CircularControlClickEvent) -> Unit) {
    //The circular wheel with 5 clickable buttons
    val outerCircleSize = 250.dp
    val innerCircleSize = 120.dp

    val localTheme = LocalAppTheme.current

    Box(contentAlignment = Alignment.Center, modifier = modifier.then(Modifier.size(outerCircleSize))) {
        // Outer Circle (Arc)
        Canvas(modifier = Modifier.matchParentSize()) {
            // Background circle
            drawArc(
                color = localTheme.playbackOuterCircleBorderColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
                size = size
            )
            drawCircle(
                color = localTheme.playbackOuterCircleColor,
                radius = size.minDimension / 2
            )
        }

//        Inner circle with borders
//        Box(
//            modifier = Modifier
//                .size(innerCircleSize + 2.dp) // Assuming border width is 2.dp, adjust the total size to accommodate the border
//                .border(BorderStroke(1.dp, Theme.PlaybackInnerCircleBorderColor), CircleShape)
//                .padding(2.dp) // Padding equals border width to ensure inner Box aligns inside the border
//        ) {
//            // Inner Box for background
//            Box(
//                modifier = Modifier
//                    .matchParentSize() // Ensures the inner Box fills the space inside the border
//                    .clip(CircleShape)
//                    .background(Theme.PlaybackInnerCircleColor),
//                contentAlignment = Alignment.Center
//            ) {
//                // Your inner content here, if any
//            }
//        }

        // Inner Circle (Transparent Center)
        Box(
            modifier = Modifier
                .size(innerCircleSize)
                .clip(CircleShape)
                .background(localTheme.playbackInnerCircleColor)
                .combinedClickable(
                    onClick = { onEvent.invoke(CircularControlClickEvent.OnMiddleButtonClicked) },
                    onLongClick = { onEvent.invoke(CircularControlClickEvent.OnMiddleButtonLongClicked) }
                ),

            contentAlignment = Alignment.Center
        ) {
            // Your inner content here, if any
        }

        // Positioning the control texts and icons
        ControlItem(modifier = Modifier.clickable { onEvent.invoke(CircularControlClickEvent.OnMenuClicked) }, text = "Menu", alignment = Alignment.TopCenter)
        ControlItem(modifier = Modifier.clickable { onEvent.invoke(CircularControlClickEvent.OnRewindClicked) }, icon = ImageVector.vectorResource(id = R.drawable.ic_rewind), alignment = Alignment.CenterStart)
        ControlItem(modifier = Modifier.clickable { onEvent.invoke(CircularControlClickEvent.OnFastForwardClicked) }.graphicsLayer(rotationZ = 180f), icon = ImageVector.vectorResource(id = R.drawable.ic_rewind), alignment = Alignment.CenterEnd)
        ControlItem(modifier = Modifier.clickable { onEvent.invoke(CircularControlClickEvent.OnPlayPauseClicked) }, icon = ImageVector.vectorResource(id = R.drawable.ic_play_pause), alignment = Alignment.BottomCenter)
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
            Text(text = text, color = LocalAppTheme.current.playbackTextColor, modifier = modifier.then(Modifier.padding(20.dp)), fontWeight = FontWeight.Bold, fontSize = 20.sp)
        } else if (icon != null) {
            Icon(icon, contentDescription = null, tint = LocalAppTheme.current.playbackButtonColor, modifier = modifier.then(Modifier.padding(15.dp)))
        }
    }

}

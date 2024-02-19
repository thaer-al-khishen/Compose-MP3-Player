package com.relatablecode.mp3composeapplication.playback_screen.middle_section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.relatablecode.mp3composeapplication.R
import com.relatablecode.mp3composeapplication.Theme

@Composable
@Preview
fun PlaybackScreenSettings(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.then(
            Modifier.fillMaxWidth()
        ),
        verticalArrangement = Arrangement.Center,  //Divides the content equally, like SpaceEvenly, but in this case, the start and end composables inside the content are at the edges of the screen
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_left),
                tint = Theme.PlaybackScreenMiddleImageColor,
                contentDescription = "Arrow Left",
            )
            Spacer(modifier = Modifier.width(16.dp))
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    contentColor = Theme.PrimaryColor,
                    containerColor = Theme.PrimaryColor
                ),
                modifier = Modifier.size(80.dp)
            ) {}
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                painter = painterResource(id = R.drawable.arrow_left),
                tint = Theme.PlaybackScreenMiddleImageColor,
                contentDescription = "Arrow Left",
                modifier = Modifier.rotate(180f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = Theme.ThemeColorName,
            color = Theme.PlaybackScreenMiddleImageColor,
            fontSize = 20.sp
        )
    }
}

package com.karleinstein.basemvvm.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karleinstein.basemvvm.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp"
)
@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onClickContinue: ()-> Unit = {}
) {

    val scope = rememberCoroutineScope()
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1E2A38))
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.Start
    ) {

        Spacer(modifier = Modifier.height(40.dp))

        // 🔹 Top Logo Section
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.ic_logo),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Day",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Task",
                color = Color(0xFFF4C95D),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 🔹 Illustration (center area)
        Image(
            painter = painterResource(R.drawable.icon_splash),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 🔹 Big Headline
        Text(
            text = "Manage your\nTask with",
            color = Color.White,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 40.sp
        )

        Text(
            text = "DayTask",
            color = Color(0xFFF4C95D),
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 🔹 Button
        Button(
            colors = ButtonColors(
                containerColor = Color(0xFFF4C95D),
                contentColor = Color.Black,
                disabledContentColor = Color.Black,
                disabledContainerColor = Color(0xFFF4C95D)
            ),
            content = {
                Text(
                    text = "Let's start",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            onClick = {
                scope.launch {
                    delay(50) // Add a small delay
                    onClickContinue()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)   // 👈 bottom margin
        )
    }
}

package com.amir.weathora.presentaion

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.layout.R
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.amir.weathora.presentaion.ui.theme.DarkBlue
import com.amir.weathora.presentaion.ui.theme.DeepBlue
import com.amir.weathora.presentaion.ui.theme.WeathoraTheme
import dagger.hilt.android.AndroidEntryPoint

//import com.amir.weathora
import com.amir.weathora.R
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: WeatherViewModel by viewModels()
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        {
            viewModel.loadWeatherInfo()
        }
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
        setContent {
            var showLocationDialog by remember { mutableStateOf(false) }

            WeathoraTheme {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(DarkBlue)
                    ) {
                        // آیکون‌ها بالا
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_option),
                                contentDescription = "Options",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable { showLocationDialog = true }
                            )
                            Icon(
                                painter = painterResource(id = R.drawable.ic_search),
                                contentDescription = "Search",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable {
                                        // added search here
                                    }
                            )
                        }

                        WeatherCard(
                            state = viewModel.state,
                            backgroundColor = DeepBlue
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        WeatherForecast(state = viewModel.state)
                    }

                    if (showLocationDialog) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0x80000000))
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    showLocationDialog = false
                                }
                        ) {
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .background(DarkBlue, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Select Location",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                HorizontalDivider(color = Color.White)

                                Text(
                                    text = "Tehran",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.loadWeatherInfo(
                                                latitude = 35.6892,
                                                longitude = 51.3890
                                            )
                                            showLocationDialog = false
                                        }
                                        .padding(12.dp)

                                )

                                Text(
                                    text = "Isfahan",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.loadWeatherInfo(
                                                latitude = 32.6546,
                                                longitude = 51.6680
                                            )
                                            showLocationDialog = false
                                        }
                                        .padding(12.dp)
                                )
                            }
                        }
                    }

                    if (viewModel.state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    viewModel.state.error?.let { error ->
                        Text(
                            text = error,
                            color = Color.Red,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }

    }
}

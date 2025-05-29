package com.amir.weathora.presentaion

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import com.amir.weathora.R
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.amir.weathora.data.iranCities



@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: WeatherViewModel by viewModels()
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        permissionLauncher = registerForActivityResult(
//            ActivityResultContracts.RequestMultiplePermissions()
//        )
//        {
//            viewModel.loadWeatherInfo()
//        }
//        permissionLauncher.launch(
//            arrayOf(
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            )
//        )

        setContent {
            WeathoraTheme {
                val context = LocalContext.current
                val prefs = remember { CityPreference(context) }
                var showDialog by remember { mutableStateOf(false) }
                var searchQuery by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    val savedCity = prefs.getCity()
                    if (savedCity == null) {
                        showDialog = true
                    } else {
                        val city = iranCities.find { it.name == savedCity }
                        city?.let {
                            viewModel.loadWeatherForCoordinates(it.latitude, it.longitude, it.name)
                        }
                    }
                }

                val iranTime = remember {
                    SimpleDateFormat("HH:mm", Locale.ENGLISH).apply {
                        timeZone = TimeZone.getTimeZone("Asia/Tehran")
                    }
                }.format(Date())


                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(DarkBlue)
                    ) {
                        // Header with Clock and Option Icon
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
                                    .clickable { showDialog = true }
                            )
                            Text(
                                text = iranTime,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        WeatherCard(
                            state = viewModel.state,
                            backgroundColor = DeepBlue
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        WeatherForecast(state = viewModel.state)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(DarkBlue)
                        ) {
                            if (!viewModel.state.isLoading && viewModel.temp != null && viewModel.cityName != null) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = getMessageForTemperature(viewModel.temp!!),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White
                                    )
                                    Spacer(Modifier.height(12.dp))
                                    AnimatedTypingText("You are living in ${viewModel.cityName} 🌍")

                                }
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

                    if (showDialog) {
                        ModalBottomSheet(
                            onDismissRequest = { showDialog = false },
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(),
                            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                            containerColor = Color(0xFF05014a)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Choose Your City",
                                    fontSize = 20.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                TextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    colors = TextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedContainerColor = DarkBlue,
                                        unfocusedContainerColor = DarkBlue,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        cursorColor = Color.White
                                    ),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Top Cities",
                                    color = Color.LightGray,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(8.dp))


                                val filtered = if (searchQuery.isEmpty()) {
                                    iranCities
                                } else {
                                    iranCities.filter {
                                        it.name.contains(searchQuery, ignoreCase = true)
                                    }
                                }

                                LazyColumn {
                                    items(filtered) { city ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .clickable {
                                                    showDialog = false
                                                    prefs.saveCity(city.name)
                                                    viewModel.loadWeatherForCoordinates(
                                                        city.latitude,
                                                        city.longitude,
                                                        city.name
                                                    )
                                                },
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = Color(0xFF1B3B5A)
                                            )
                                        ) {
                                            Text(
                                                text = city.name,
                                                modifier = Modifier.padding(16.dp),
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

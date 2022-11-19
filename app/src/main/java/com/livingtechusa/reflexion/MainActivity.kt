package com.livingtechusa.reflexion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.livingtechusa.reflexion.navigation.NavigationHosting
import com.livingtechusa.reflexion.ui.build.BuildItemScreen
import com.livingtechusa.reflexion.util.BaseApplication
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

const val Main_Activity = "Main_Activity"
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var app: BaseApplication
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavigationHosting()
        }
    }
}

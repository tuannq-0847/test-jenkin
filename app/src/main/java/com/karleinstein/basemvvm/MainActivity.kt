package com.karleinstein.basemvvm

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.karleinstein.basemvvm.ui.theme.BaseMVVMTheme
import com.karleinstein.fastpermissions.FastPermission
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.widget.Toast

class MainActivity : ComponentActivity(), FastPermission.PermissionsListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BaseMVVMTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier
                            .padding(innerPadding)
                            .clickable {
                                FastPermission.check(
                                    this, listOf(
                                        ACCESS_FINE_LOCATION,
                                        ACCESS_COARSE_LOCATION
                                    ), this
                                )
                            }
                    )
                }
            }
        }
    }

    override fun onGranted() {
        Toast.makeText(this, "onGranted",Toast.LENGTH_LONG).show()
    }

    override fun onPermissionDeniedForever(deniedPermissionsForever: List<String>) {
        Toast.makeText(this, "$deniedPermissionsForever deniedPermissionsForever",Toast.LENGTH_LONG).show()
    }

    override fun onPermissionDenied(deniedPermissions: List<String>) {
        Toast.makeText(this, "$deniedPermissions denied",Toast.LENGTH_LONG).show()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BaseMVVMTheme {
        Greeting("Android")
    }
}
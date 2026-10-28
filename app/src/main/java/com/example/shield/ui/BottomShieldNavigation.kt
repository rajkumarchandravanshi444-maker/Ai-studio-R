package com.example.shield.ui

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.shield.model.AppLanguage
import com.example.shield.viewmodel.ShieldTab

@Composable
fun BottomShieldNavigation(
    currentTab: ShieldTab,
    currentLanguage: AppLanguage,
    onTabSelected: (ShieldTab) -> Unit
) {
    val isHi = currentLanguage.isHindi

    NavigationBar(
        modifier = Modifier
            .navigationBarsPadding()
            .testTag("bottom_navigation_bar"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = MaterialTheme.colorScheme.surfaceVariant.let { androidx.compose.ui.unit.Dp(3f) }
    ) {
        NavigationBarItem(
            selected = currentTab == ShieldTab.DASHBOARD,
            onClick = { onTabSelected(ShieldTab.DASHBOARD) },
            icon = { Icon(Icons.Default.Shield, contentDescription = "Dashboard") },
            label = {
                Text(
                    text = if (isHi) "शील्ड" else "Shield",
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == ShieldTab.DASHBOARD) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        NavigationBarItem(
            selected = currentTab == ShieldTab.SCANNER,
            onClick = { onTabSelected(ShieldTab.SCANNER) },
            icon = { Icon(Icons.Default.Radar, contentDescription = "Scanner") },
            label = {
                Text(
                    text = if (isHi) "स्कैन" else "Scan",
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == ShieldTab.SCANNER) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        NavigationBarItem(
            selected = currentTab == ShieldTab.PRIVACY,
            onClick = { onTabSelected(ShieldTab.PRIVACY) },
            icon = { Icon(Icons.Default.Lock, contentDescription = "Privacy") },
            label = {
                Text(
                    text = if (isHi) "प्राइवेसी" else "Privacy",
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == ShieldTab.PRIVACY) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        NavigationBarItem(
            selected = currentTab == ShieldTab.DIAGNOSTICS,
            onClick = { onTabSelected(ShieldTab.DIAGNOSTICS) },
            icon = { Icon(Icons.Default.Speed, contentDescription = "Diagnostics") },
            label = {
                Text(
                    text = if (isHi) "डायग्नोस्टिक्स" else "Perf",
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == ShieldTab.DIAGNOSTICS) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        NavigationBarItem(
            selected = currentTab == ShieldTab.REPAIR,
            onClick = { onTabSelected(ShieldTab.REPAIR) },
            icon = { Icon(Icons.Default.Build, contentDescription = "Repair") },
            label = {
                Text(
                    text = if (isHi) "रिपेयर" else "Repair",
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == ShieldTab.REPAIR) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        NavigationBarItem(
            selected = currentTab == ShieldTab.AI_GUARDIAN,
            onClick = { onTabSelected(ShieldTab.AI_GUARDIAN) },
            icon = { Icon(Icons.Default.SmartToy, contentDescription = "AI Guardian") },
            label = {
                Text(
                    text = if (isHi) "एआई" else "AI",
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == ShieldTab.AI_GUARDIAN) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}

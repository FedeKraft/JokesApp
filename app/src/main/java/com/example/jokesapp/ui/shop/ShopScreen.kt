package com.example.jokesapp.ui.shop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jokesapp.R
import com.example.jokesapp.ui.theme.JokesAppTheme
import com.example.jokesapp.viewmodel.JokeViewModel
import kotlinx.coroutines.delay

private data class ShopUpgrade(
    val id: String,
    val emoji: String,
    val nameRes: Int,
    val descRes: Int,
    val cost: Int,
)

private val UPGRADES = listOf(
    ShopUpgrade("chaos_mode",    "💀", R.string.upgrade_chaos_mode_name,    R.string.upgrade_chaos_mode_desc,    50),
    ShopUpgrade("lucky_charm",   "🍀", R.string.upgrade_lucky_charm_name,   R.string.upgrade_lucky_charm_desc,   75),
    ShopUpgrade("coin_doubler",  "🪙", R.string.upgrade_coin_doubler_name,  R.string.upgrade_coin_doubler_desc,  100),
    ShopUpgrade("coin_tripler",  "💎", R.string.upgrade_coin_tripler_name,  R.string.upgrade_coin_tripler_desc,  300),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    outerPadding: PaddingValues = PaddingValues(),
    viewModel: JokeViewModel,
) {
    val coins by viewModel.coins.collectAsState()
    val activeUpgrades by viewModel.activeUpgrades.collectAsState()

    var nowMs by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(100L)
            nowMs = System.currentTimeMillis()
        }
    }

    ShopScreenContent(
        coins = coins,
        activeUpgrades = activeUpgrades,
        nowMs = nowMs,
        outerPadding = outerPadding,
        onBuy = { id, cost -> viewModel.buyUpgrade(id, cost) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShopScreenContent(
    coins: Int,
    activeUpgrades: Map<String, Long>,
    nowMs: Long,
    outerPadding: PaddingValues = PaddingValues(),
    onBuy: (String, Int) -> Unit = { _, _ -> },
) {
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(stringResource(R.string.nav_shop), fontWeight = FontWeight.ExtraBold)
            })
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(bottom = outerPadding.calculateBottomPadding()),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            stringResource(R.string.shop_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            stringResource(R.string.shop_subtitle),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        Text(
                            text = "🪙 $coins",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            items(UPGRADES) { upgrade ->
                val expiryMs = activeUpgrades[upgrade.id] ?: 0L
                val remainingMs = (expiryMs - nowMs).coerceAtLeast(0L)
                val isActive = remainingMs > 0L
                val canAfford = coins >= upgrade.cost

                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(upgrade.emoji, fontSize = 36.sp, modifier = Modifier.size(48.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Text(
                                    stringResource(upgrade.nameRes),
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall,
                                )
                                if (isActive) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = MaterialTheme.shapes.small,
                                    ) {
                                        Text(
                                            text = "⚡ ${remainingMs / 1000 + 1}s",
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                stringResource(upgrade.descRes),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "🪙 ${upgrade.cost}",
                                fontWeight = FontWeight.ExtraBold,
                                color = if (canAfford) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelLarge,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Button(
                                onClick = { onBuy(upgrade.id, upgrade.cost) },
                                enabled = canAfford,
                                colors = if (isActive) ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                ) else ButtonDefaults.buttonColors(),
                            ) {
                                Text(
                                    if (isActive) stringResource(R.string.shop_btn_renew)
                                    else stringResource(R.string.shop_btn_buy),
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Shop Screen")
@Composable
private fun ShopScreenPreview() {
    JokesAppTheme {
        ShopScreenContent(
            coins = 250,
            activeUpgrades = mapOf("chaos_mode" to System.currentTimeMillis() + 7_000L),
            nowMs = System.currentTimeMillis(),
        )
    }
}

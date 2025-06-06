package pro.themed.manager.components

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.jaredrummler.ktsh.Shell
import kotlinx.coroutines.launch
import pro.themed.manager.MainActivity
import pro.themed.manager.R
import pro.themed.manager.ui.theme.contentcol
import pro.themed.manager.utils.GlobalVariables
import pro.themed.manager.utils.SharedPreferencesManager

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun OnBoardingPage() {
    val sharedPreferences = SharedPreferencesManager.getSharedPreferences()
    val context = MainActivity.appContext
    Column(
        Modifier.fillMaxSize()
        //                        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        val pageCount = 5
        val pagerState = rememberPagerState { pageCount }
        HorizontalPager(state = pagerState, Modifier.weight(1f)) { index ->
            when (index) {
                0 -> {
                    OnBoarding(
                        image = R.drawable.main_logo_circle_mask00000,
                        text = stringResource(R.string.onboarding0),
                    )
                }
                1 -> {
                    OnBoarding(
                        image = R.drawable.magisk_logo,
                        text = stringResource(R.string.onboarding1),
                    )
                }
                2 -> {
                    OnBoarding(
                        image = R.drawable.dead_android,
                        text = stringResource(R.string.onboarding2),
                    )
                }
                3 -> {
                    OnBoarding(
                        image = R.drawable.telegram_logo,
                        text = stringResource(R.string.onboarding3),
                    )
                }
                4 -> {
                    OnBoarding(
                        image = R.drawable.localazy_logo,
                        text = stringResource(R.string.onboarding4),
                    )
                }
            }
        }
        val coroutineScope = rememberCoroutineScope()

        Row(
            Modifier.fillMaxWidth().padding(horizontal = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            OutlinedButton(
                shape = CircleShape,
                colors =
                    ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                onClick = {
                    coroutineScope.launch {
                        if (pagerState.currentPage == 0) {
                            Shell.SH.run("su")
                            sharedPreferences.edit().putBoolean("onBoardingCompleted", true).apply()
                            val intent = Intent(context, MainActivity::class.java)
                            MainActivity().finish()
                            ContextCompat.startActivity(context, intent, null)
                        } else {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }
                },
            ) {
                when (pagerState.currentPage) {
                    0 -> {
                        Text(text = "Skip", color = contentcol)
                    }
                    else -> {
                        Text(text = "Back", color = contentcol)
                    }
                }
            }
            // Spacer(modifier = Modifier.fillMaxWidth())
            OutlinedButton(
                shape = CircleShape,
                colors =
                    ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                onClick = {
                    coroutineScope.launch {
                        if (pagerState.currentPage == 1) {
                            if ("root" !in GlobalVariables.whoami) {
                                Toast.makeText(
                                        context,
                                        ContextCompat.getString(context, R.string.no_root_access),
                                        Toast.LENGTH_SHORT,
                                    )
                                    .show()
                            }
                        }
                        if (pagerState.currentPage == pageCount - 1) {
                            sharedPreferences.edit().putBoolean("onBoardingCompleted", true).apply()
                            val intent = Intent(context, MainActivity::class.java)
                            MainActivity().finish()
                            context.startActivity( intent, null)
                        }
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                },
            ) {
                when (pagerState.currentPage) {
                    pageCount - 1 -> {
                        Text(text = "Get started", color = contentcol)
                    }
                    1 -> {
                        Text(text = "Grant access", color = contentcol)
                    }
                    else -> {
                        Text(text = "Next", color = contentcol)
                    }
                }
            }
        }
    }
}

package pro.themed.audhdlauncher

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import pro.themed.audhdlauncher.database.AppDataStoreRepository
import pro.themed.audhdlauncher.database.AppSettingsDataStore
import pro.themed.audhdlauncher.database.LauncherViewModel

class AuDHDLauncherApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger() // Consider using KoinAndroidLogger if you want to use Android's Log class
            androidContext(this@AuDHDLauncherApp)
            modules(appModule)
        }
    }
}

val appModule = module {
    viewModel { LauncherViewModel(get(), get()) } // Koin will provide repository and settingsDataStore
    single { AppDataStoreRepository(androidContext()) }
    single { AppSettingsDataStore(androidContext()) }
    // Add other dependencies here if they arise
}

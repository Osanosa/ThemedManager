package pro.themed.manager.utils

import android.content.*
import androidx.compose.runtime.*
import com.jaredrummler.ktsh.*
import pro.themed.manager.*


object GlobalVariables {
    val sharedPreferences: SharedPreferences by lazy {
        MyApplication.appContext.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
    }

    var modulePath by mutableStateOf("/data/adb/modules/ThemedProject")
    var isdownloaded by mutableStateOf(false)

    val whoami by lazy {
        Shell.SH.run("su -c whoami").stdout()
    }

    val magiskVersion by lazy {
        Shell.SH.run("magisk -v").stdout()
    }

    val themedId by lazy {
        Shell.SH.run("""su -c getprop | grep '\[ro\.serialno\]' | sed 's/.*\[\(.*\)\]/\1/' | md5sum -b""")
            .stdout()
    }

    val onDemandCompilerShell by lazy {
        Shell.SU
    }
    val AutoRefreshRateServiceShell by lazy {
        Shell.SU
    }

}

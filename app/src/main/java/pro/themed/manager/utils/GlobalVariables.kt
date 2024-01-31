package pro.themed.manager.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jaredrummler.ktsh.Shell
import pro.themed.manager.MainActivity



object GlobalVariables {
    val sharedPreferences: SharedPreferences by lazy {
        MainActivity.appContext.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
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



}

package pro.themed.manager.utils

import android.widget.Toast
import com.jaredrummler.ktsh.Shell
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pro.themed.manager.MainActivity

fun buildOverlay(path: String = "") {
    CoroutineScope(Dispatchers.IO).launch {
        val compileShell = Shell("su")
        compileShell.addOnStderrLineListener(
            object : Shell.OnLineListener {
                override fun onLine(line: String) {
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(MainActivity.appContext, line, Toast.LENGTH_SHORT).show()
                        line.log()
                    }
                }
            }
        )
        compileShell.addOnCommandResultListener(
            object : Shell.OnCommandResultListener {
                override fun onResult(result: Shell.Command.Result) {
                    result.log()
                }
            }
        )
        compileShell.run("cd $path")
        val PWD = compileShell.run("pwd").stdout().trim()
        compileShell.run("""aapt2 compile -v --dir res -o $PWD""").stderr.log()
        compileShell.run(
            """aapt2 link -o $PWD/unsigned.apk -I /system/framework/framework-res.apk --manifest $PWD/AndroidManifest.xml $PWD/*.flat --min-sdk-version 26 --target-sdk-version 29 --auto-add-overlay """
        )
        compileShell.run("""zipsigner unsigned.apk signed.apk""").log()
        compileShell.run("""pm install signed.apk""").log()
        MainActivity.overlayList = fetchOverlayList()
    }
}

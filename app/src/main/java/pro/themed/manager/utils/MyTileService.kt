package pro.themed.manager.utils

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.jaredrummler.ktsh.Shell

class MyTileService : TileService() {

    override fun onClick() {
        super.onClick()
        // Handle tile click
        val tile = qsTile
        if (tile != null) {
            if (tile.state == Tile.STATE_ACTIVE) {

                // Tile is active, deactivate it
                tile.state = Tile.STATE_INACTIVE
                tile.updateTile()

                Shell("su").run("am stop-service pro.themed.manager/pro.themed.manager.utils.MyForegroundService")
                Shell("su").run("killall pro.themed.manager")
            } else {
                Shell("su").run("am start-service pro.themed.manager/pro.themed.manager.utils.MyForegroundService")
                // Tile is inactive, activate it
                tile.state = Tile.STATE_ACTIVE
                tile.updateTile()

            }
        }
    }
}

package pro.themed.manager.utils

import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class MyTileService : TileService() {

    override fun onClick() {
        super.onClick()
        // Handle tile click
        val tile = qsTile
        if (tile != null) {
            if (tile.state == Tile.STATE_ACTIVE) {
                stopService(Intent(applicationContext, MyForegroundService::class.java))
                // Tile is active, deactivate it
                tile.state = Tile.STATE_INACTIVE
                tile.updateTile()

            } else {
                startService(Intent(applicationContext, MyForegroundService::class.java))
                // Tile is inactive, activate it
                tile.state = Tile.STATE_ACTIVE
                tile.updateTile()

            }
        }
    }
}

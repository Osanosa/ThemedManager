package pro.themed.audhdlauncher.database

// DefaultCategories.kt
object DefaultCategoryData {
    val categories =
        listOf(
            CategoryData(
                name = "Google",
                defaultInclude = "com.google,com.android.vending,com.android.chrome",
                defaultExclude = null,
                customInclude = null,
                customExclude = null,
                customBool = 1,
                customInt = 10,
            ),
            CategoryData(
                name = "System",
                defaultInclude = "com.android,org.lineageos",
                defaultExclude = "com.android.vending,com.android.chrome",
                customInclude = null,
                customExclude = null,
                customBool = 0,
                customInt = 5,
            ),
            CategoryData(
                name = "Samsung",
                defaultInclude = "com.samsung,com.sec",
                defaultExclude = null,
                customInclude = null,
                customExclude = null,
                customBool = 0,
                customInt = 5,
            ),
            CategoryData(
                name = "Media & Entertainment",
                defaultInclude =
                    "app.rvx.android.apps.youtube.music,com.sec.android.gallery3d,io.sbaud.wavstudio,io.fournkoner.hdrezka,com.sec.android.app.music,com.shazam.android,gmikhail.colorpicker,com.zhiliaoapp.musically,com.reddnek.syncplay,com.mxtech.videoplayer.pro,com.lemon.lvoverseas,ak.alizandro.smartaudiobookplayer,com.PixelStudio,tv.twitch.android.app,dkc.video.hdbox,com.flyersoft.moonreaderp,com.robertrareza.is_wsjp_app,com.google.android.youtube,com.sec.android.app.voicenote,com.google.android.apps.youtube.music,jp.ne.ibis.ibispaintx.app,pl.tvp.stream,org.videolan.vlc,org.kiwix.kiwixmobile,com.netflix.mediaclient,com.spotify.music,com.amazon.amazonvideo,com.hbo.hbonow,com.pandora.android,com.disney.disneyplus,com.snapchat.android,com.hulu.plus,com.tubi.tv,com.vudu.android.client,com.crunchyroll.crunchyroll,com.plex.android,com.soundcloud.android,com.rdio.android,com.libreoffice.impress,com.mxtech.videoplayer.ad,com.kodi.tv,com.apple.tv,com.bbc.iplayer.android,com.justwatch,com.imdb.mobile,com.eww.sublime.text,com.uptodown.android",
                defaultExclude = null,
                customInclude = null,
                customExclude = null,
                customBool = 0,
                customInt = 5,
            ),
        )
}

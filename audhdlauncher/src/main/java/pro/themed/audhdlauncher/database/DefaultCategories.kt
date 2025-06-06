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
                rows = 2,
                customBool = 1,
                customInt = 10,
                color = "#0F9D58",
            ),
            CategoryData(
                name = "System",
                defaultInclude = "com.android,org.lineageos,org.fossify,com.zte",
                defaultExclude = null,
                customInclude = null,
                customExclude = null,
                rows = 1,
                customBool = 0,
                customInt = 5,
                color = "#",
            ),
            CategoryData(
                name = "Samsung",
                defaultInclude = "com.samsung,com.sec",
                defaultExclude = null,
                customInclude = null,
                customExclude = null,
                customBool = 0,
                customInt = 5,
                color = "#1428A0",
            ),
            CategoryData(
                name = "Media & Entertainment",
                defaultInclude =
                    "musicplayer,playermusic,camera,com.footej.camera2,com.alightcreative,aimp,com.ss.android.ugc.trill,newpipe,nextplayer,openvideoeditor,youtube,com.zionhuang.music,com.shamim.cam,app.rvx.android.apps.youtube.music,com.sec.android.gallery3d,io.sbaud.wavstudio,io.fournkoner.hdrezka,com.sec.android.app.music,com.shazam.android,gmikhail.colorpicker,com.zhiliaoapp.musically,com.reddnek.syncplay,com.mxtech.videoplayer.pro,com.lemon.lvoverseas,ak.alizandro.smartaudiobookplayer,com.PixelStudio,tv.twitch.android.app,dkc.video.hdbox,com.flyersoft.moonreaderp,com.robertrareza.is_wsjp_app,com.google.android.youtube,com.sec.android.app.voicenote,com.google.android.apps.youtube.music,jp.ne.ibis.ibispaintx.app,pl.tvp.stream,org.videolan.vlc,org.kiwix.kiwixmobile,com.netflix.mediaclient,com.spotify.music,com.amazon.amazonvideo,com.hbo.hbonow,com.pandora.android,com.disney.disneyplus,com.snapchat.android,com.hulu.plus,com.tubi.tv,com.vudu.android.client,com.crunchyroll.crunchyroll,com.plex.android,com.soundcloud.android,com.rdio.android,com.libreoffice.impress,com.mxtech.videoplayer.ad,com.kodi.tv,com.apple.tv,com.bbc.iplayer.android,com.justwatch,com.imdb.mobile,com.eww.sublime.text,com.uptodown.android",
                defaultExclude = null,
                customInclude = null,
                customExclude = null,
                rows = 2,
                customBool = 0,
                customInt = 5,
                color = "#FF0000",
            ),
            CategoryData(
                name = "Utilities & Tools",
                defaultInclude =
                    "vpn,file.explorer,estrongs,feravolt,qrcode,barcode,rs.explorer,speedtest,vanced.manager,rvx.!!!!!!,monect,lawnchair,zarchiver,termux,twofasapp,lsposed,widget,iconpack,homebutton,automate,squarehome,rootexplorer,adaway,aegis,shell,barcode,com.kingroot.kinguser,com.looker,keepscreenon,magisk,gms,backup,launcher,activitylauncher,aida64,deviceinfohw,fdroid,com.delphicoder.flud,ttorrent,com.cheburnet,com.itsaky.androidide,project.hyperion,logcat,callsmsmanager,com.junkfood.seal,com.teamviewer,simplekeyboard,futo,app.rvx.manager,eu.thedarken.sdm,org.servo,vegabobo.dsusideloader,ru.zdevs.zarchiver,rk.android.app.shortcutmaker,io.github.domi04151309.powerapp,gr.nikolasspyr.integritycheck,fahrbot.apps.undelete,dev.MakPersonalStudio.XposedFirewall,com.updateme,com.truedevelopersstudio.automatictap.autoclicker,com.topjohnwu.magisk,com.tilks.arscmerge,com.termoneplus,com.ss.edgegestures,com.sparkine.muvizedge,com.sika524.android.quickshortcut,com.pittvandewitt.viperfx,com.paget96.batteryguru,com.origiq.wirelessadb,com.mrikso.apkrepacker,com.microsys.TouchScreenTest,com.mcal.apkeditor.pro,com.haibison.apksigner,com.franco.kernel,com.embermitre.pixolor.app,com.f0x1d.logfox,com.drdisagree.iconify,com.dergoogler.mmrl,com.crackystudio.networktypeswitcher,com.aefyr.sai,com.Saplin.CPDT,bin.mt.plus,com.chiller3.bcr,pro.themed,com.appsisle.devassistant,com.klafntolox.ahoosxkrqr,com.mixplorer.silver,com.ss.popupWidget,org.swiftapps.swiftbackup",
                defaultExclude = null,
                customInclude = null,
                customExclude = null,
                rows = 4,
                customBool = 0,
                customInt = 5,
                color = "",
            ),
            CategoryData(
                name = "Gaming",
                defaultInclude = gamesList.joinToString(","),
                defaultExclude = null,
                customInclude = null,
                customExclude = null,
                rows = 1,
                customBool = 0,
                customInt = 5,
                color = "",
            ),
            CategoryData(
                name = "Web",
                defaultInclude =
                    "ua.slando,com.opera,vkontakte,ru.fourpda,com.brave,com.discord,com.instagram,com.kivibrowser,com.reddit,org.thoughtcrime,org.telegram,org.torproject,com.viber,com.whatsapp,org.wikipediacom.mycompany.app.soulbrowser,org.thunderdog,com.aliucord,org.mozilla,com.github,com.aeroinsta,com.moodle,com.myinsta,xyz,nextalone,ch.protonemail",
                defaultExclude = null,
                customInclude = null,
                customExclude = null,
                rows = 1,
                customBool = 0,
                customInt = 5,
                color = "",
            ),
        )
}

package pro.themed.perappdownscale

import android.content.pm.PackageManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun Item(
    index: Int,
    app: Pair<String, AppInfo>,
    privilegedHelper: PrivilegedCommandHelper? = null,
    pm: PackageManager,
    density: Density,
    commandMode: CommandMode = CommandMode.DEFAULT,
    dumpsysGame: String = "",
    onRefreshNeeded: () -> Unit = {},
) {
    // Helper functions for command building
    fun buildSetCommand(
        mode: CommandMode,
        packageName: String,
        downscale: Float? = null,
        fps: Int? = null,
        useAngle: Boolean? = null,
    ): String {
        return when (mode) {
            CommandMode.DEFAULT -> {
                // cmd game set --downscale X --fps Y
                buildString {
                    append("cmd game set")
                    if (downscale != null) append(" --downscale $downscale")
                    if (fps != null) append(" --fps $fps")
                    append(" $packageName")
                }
            }
            CommandMode.ALTERNATIVE -> {
                // device_config with all 4 modes + activate mode
                val params =
                    buildList {
                            if (useAngle == true) add("angle=1")
                            if (downscale != null) add("downscaleFactor=$downscale")
                            if (fps != null) add("fps=$fps")
                        }
                        .joinToString(",")

                val allModes = (1..4).joinToString(":") { "mode=$it,$params" }
                "device_config put game_overlay $packageName $allModes ; cmd game mode 2 $packageName"
            }
        }
    }

    fun buildResetCommand(mode: CommandMode, packageName: String): String {
        return when (mode) {
            CommandMode.DEFAULT -> "cmd game reset $packageName"
            CommandMode.ALTERNATIVE ->
                "device_config delete game_overlay $packageName ; cmd game mode standard $packageName"
        }
    }

    val isAppSupported = dumpsysGame.contains(app.first)

    var interventions by rememberSaveable { mutableStateOf(app.second.interventions) }
    var availableModes by rememberSaveable { mutableStateOf(emptyList<String>()) }
    var expanded by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = app.first, privilegedHelper?.currentMode) {
        if (interventions.isEmpty() && privilegedHelper != null) {
            try {
                val result = privilegedHelper.executeCommand("cmd game list-configs ${app.first}")
                if (
                    result.output.contains("Unknown command", ignoreCase = true) ||
                        result.output.contains(
                            "EXECUTION OF THIS COMMAND FAILED",
                            ignoreCase = true,
                        )
                ) {
                    // Fallback: use already-loaded dumpsys game data
                    interventions = app.second.interventions
                } else {
                    interventions = result.output
                }
            } catch (e: Exception) {
                // Final fallback: use dumpsys game
                interventions = app.second.interventions
            }
        }

        // Get available game modes
        if (
            availableModes.isEmpty() &&
                !interventions.contains("not of game type") &&
                privilegedHelper != null
        ) {
            try {
                val modesResult =
                    privilegedHelper.executeCommand("cmd game list-modes ${app.first}")
                val output = modesResult.output

                if (
                    output.contains("Unknown command", ignoreCase = true) ||
                        output.contains("EXECUTION OF THIS COMMAND FAILED", ignoreCase = true)
                ) {
                    // Fallback for A13: assume standard and custom
                    availableModes = listOf("standard", "custom")
                } else {
                    // Parse: "package current mode: custom, available game modes:
                    // [standard,custom]"
                    val modesStart = output.indexOf("available game modes: [")
                    if (modesStart != -1) {
                        val modesEnd = output.indexOf("]", modesStart)
                        if (modesEnd != -1) {
                            val modesString = output.substring(modesStart + 23, modesEnd)
                            availableModes = modesString.split(",").map { it.trim() }
                        }
                    } else {
                        availableModes = listOf("standard", "custom")
                    }
                }
            } catch (e: Exception) {
                availableModes = listOf("standard", "custom")
            }
        }
    }
    Column(
        modifier =
            Modifier.fillMaxWidth()
                .background(
                    color =
                        if (index % 2 == 0) Color.LightGray.copy(alpha = 0.05f)
                        else Color.Transparent
                )
                .background(
                    color =
                        if (interventions.contains("Name")) Color.Yellow.copy(alpha = 0.1f)
                        else Color.Transparent
                )
    ) {
        Row(
            Modifier.fillMaxWidth().clickable {
                expanded = !expanded
                if (privilegedHelper != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val result =
                            privilegedHelper.executeCommand("cmd game list-configs ${app.first}")
                        interventions = result.output
                    }
                }
            },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BadgedBox(
                badge = {
                    if (interventions.contains("Name"))
                        Icon(
                            imageVector = Icons.Filled.AddCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(12.dp),
                        )

                    if (interventions.contains("not of game type"))
                        Icon(
                            imageVector = Icons.Filled.AddCircle,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(12.dp).rotate(45f),
                        )
                }
            ) {
                ClickableIcon(app, privilegedHelper, pm, density)
            }
            Column {
                Text(text = app.second.label)
                Text(text = app.first, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.weight(1f))
        }

        ExpandedControls(
            expanded = expanded,
            interventions = interventions,
            availableModes = availableModes,
            packageName = app.first,
            privilegedHelper = privilegedHelper,
            commandMode = commandMode,
            isAppSupported = isAppSupported,
            onRefreshNeeded = onRefreshNeeded,
        ) {
            if (privilegedHelper != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val result =
                            privilegedHelper.executeCommand("cmd game list-configs ${app.first}")
                        if (
                            result.output.contains("Unknown command", ignoreCase = true) ||
                                result.output.contains(
                                    "EXECUTION OF THIS COMMAND FAILED",
                                    ignoreCase = true,
                                )
                        ) {
                            // Fallback to parsing dumpsys game
                            if (dumpsysGame.isEmpty()) {
                                onRefreshNeeded()
                            } else {
                                interventions =
                                    dumpsysGame.split("\n").firstOrNull { it.contains(app.first) }
                                        ?: ""
                            }
                        } else {
                            interventions = result.output
                        }
                    } catch (e: Exception) {
                        // Fallback to parsing dumpsys game
                        if (dumpsysGame.isEmpty()) {
                            onRefreshNeeded()
                        } else {
                            interventions =
                                dumpsysGame.split("\n").firstOrNull { it.contains(app.first) } ?: ""
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ColumnScope.ExpandedControls(
    expanded: Boolean,
    interventions: String,
    availableModes: List<String>,
    packageName: String?,
    privilegedHelper: PrivilegedCommandHelper? = null,
    commandMode: CommandMode = CommandMode.DEFAULT,
    isAppSupported: Boolean = true,
    onRefreshNeeded: () -> Unit = {},
    callback: () -> Unit = {},
) {

    AnimatedVisibility(visible = expanded) {
        if (interventions.contains("not of game type"))
            Text(
                text =
                    interventions +
                        "\n\n(This requirement was added in some A15 roms, there's no direct workaround other then change display resolution)",
                modifier =
                    Modifier.background(MaterialTheme.colorScheme.error.copy(alpha = 0.2f))
                        .padding(16.dp),
            )
        else {
            interventions
                .substringAfter("Game Mode:", "UNSET/UNKNOWN")
                .substringBefore(",", "UNSET/UNKNOWN")
            var scaling =
                interventions
                    .substringAfter("Scaling:", "UNSET/UNKNOWN")
                    .substringBefore(",", "UNSET/UNKNOWN")
            interventions.substringAfter("Use Angle:").substringBefore(",", "UNSET/UNKNOWN")
            var fps =
                interventions
                    .substringAfter("Fps:", "UNSET/UNKNOWN")
                    .substringBefore(",", "UNSET/UNKNOWN")

            var modes by remember { mutableStateOf("") }
            LaunchedEffect(Unit) {
                CoroutineScope(Dispatchers.IO).launch {
                    modes =
                        privilegedHelper?.executeCommand("cmd game list-modes $packageName")?.output
                            ?: "ERROR"
                }
            }

            // Applied values (what's actually set in the system)
            var appliedFps by remember { mutableStateOf(fps.toFloatOrNull() ?: 0f) }
            var appliedDownscale by remember {
                mutableStateOf(scaling.toFloatOrNull().takeIf { (it ?: 1f) >= 0f } ?: 1f)
            }

            // Check actual ANGLE settings from global settings
            var angleEnabled by remember { mutableStateOf(false) }

            // Function to check ANGLE state
            suspend fun checkAngleState() {
                if (privilegedHelper == null) return

                val pkgsResult =
                    privilegedHelper.executeCommand(
                        "settings get global angle_gl_driver_selection_pkgs"
                    )
                val currentPkgs = pkgsResult.output.trim()

                val valuesResult =
                    privilegedHelper.executeCommand(
                        "settings get global angle_gl_driver_selection_values"
                    )
                val currentValues = valuesResult.output.trim()

                angleEnabled =
                    if (
                        currentPkgs != "null" &&
                            currentPkgs.isNotEmpty() &&
                            currentValues != "null" &&
                            currentValues.isNotEmpty()
                    ) {
                        val pkgList = currentPkgs.split(",").map { it.trim() }
                        val valueList = currentValues.split(",").map { it.trim() }

                        val pkgIndex = pkgList.indexOf(packageName)
                        if (pkgIndex >= 0 && pkgIndex < valueList.size) {
                            valueList[pkgIndex] == "angle"
                        } else {
                            false
                        }
                    } else {
                        false
                    }
            }

            // Check ANGLE state when controls are expanded
            LaunchedEffect(expanded, interventions) {
                if (expanded) {
                    checkAngleState()
                }
            }

            // Current slider values (what user is currently adjusting)
            var currentFps by remember {
                mutableFloatStateOf(if (appliedFps > 0f) appliedFps else 60f)
            }
            var currentDownscale by remember { mutableFloatStateOf(appliedDownscale) }

            // Update all states when interventions change
            LaunchedEffect(interventions) {
                val newFps = fps.toFloatOrNull() ?: 0f
                val newDownscale = scaling.toFloatOrNull().takeIf { (it ?: 1f) >= 0f } ?: 1f

                appliedFps = newFps
                appliedDownscale = newDownscale

                // Only update current values if not currently being dragged
                // For FPS: if it becomes set, use that value; if unset, use 60 as default
                currentFps = if (newFps > 0f) newFps else 60f
                currentDownscale = newDownscale
            }

            Column(
                Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .padding(8.dp)
            ) {

                // System Override Warning
                var systemOverride by remember { mutableStateOf<String?>(null) }
                var overrideCheckFailed by remember { mutableStateOf(false) }

                suspend fun checkSystemOverride() {
                    // Only check for conflicts when in DEFAULT mode
                    if (commandMode == CommandMode.DEFAULT) {
                        val standardResult =
                            if (privilegedHelper != null) {
                                try {
                                    val result =
                                        privilegedHelper.executeCommand(
                                            "device_config get game_overlay $packageName"
                                        )
                                    result.output.trim()
                                } catch (e: Exception) {
                                    ""
                                }
                            } else {
                                ""
                            }

                        // Check if any meaningful configuration exists
                        systemOverride =
                            if (standardResult != "null" && standardResult.isNotEmpty()) {
                                standardResult
                            } else {
                                null
                            }
                    } else {
                        systemOverride = null
                    }
                }

                LaunchedEffect(Unit) { checkSystemOverride() }

                // Alternative mode unsupported app warning
                if (commandMode == CommandMode.ALTERNATIVE && !isAppSupported) {
                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text =
                                    "ⓘ This app is not in the game system list and is not expected to work, but feel free to try anyway.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                            )
                        }
                    }
                }

                if (systemOverride != null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text =
                                    if (overrideCheckFailed) {
                                        "Unable to remove game_overlay intervention. Remove it manually through Game Space settings or similar ROM features."
                                    } else {
                                        "Conflicting game_overlay intervention: $systemOverride.\n\nThis app has conflicting game settings that may cause issues like incorrect fullscreen behavior or configs not applied. Remove from Game Space library or disable similar ROM settings."
                                    },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                            )

                            if (!overrideCheckFailed) {
                                Button(
                                    colors =
                                        ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.error,
                                            contentColor = MaterialTheme.colorScheme.onError,
                                        ),
                                    onClick = {
                                        // Try to delete with root privileges
                                        if (privilegedHelper != null) {
                                            CoroutineScope(Dispatchers.IO).launch {
                                                privilegedHelper.executeCommand(
                                                    "device_config delete game_overlay $packageName"
                                                )
                                                checkSystemOverride()
                                                if (systemOverride != null) {
                                                    overrideCheckFailed = true
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.align(Alignment.End),
                                ) {
                                    Text("Force Remove")
                                }
                            }
                        }
                    }
                }
                if (
                    modes.substringAfter("current mode:").substringBefore(",").trim() !=
                        "standard" &&
                        modes.substringAfter("current mode:").substringBefore(",").trim() !=
                            "custom"
                ) {
                    Text(
                        text =
                            if (
                                modes
                                    .substringAfter("current mode:")
                                    .substringBefore(",")
                                    .trim()
                                    .contains("Unknown command", ignoreCase = true)
                            )
                                "UNABLE TO FETCH CURRENT MODE. COMMAND EXECUTION FAILED. CHECK SELFTEST AND REPORT THIS TO SUPPORT GROUP"
                            else
                                "Current mode is <${
                                modes.substringAfter("current mode:").substringBefore(",").trim()
                            }>. Some devices have presets with default downscale value and this package currently has one selected. To proceed try clicking reset all or open debug dialog and click force set custom mode. It is also recommended to disable any rom game modes or similar features that may cause issues. ",
                        modifier =
                            Modifier.padding(8.dp)
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
                                    androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                                )
                                .padding(8.dp),
                    )
                }

                // Graphics Driver Control
                if (commandMode == CommandMode.DEFAULT) {
                    // DEFAULT mode: Simple ANGLE switch
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Use ANGLE",
                            style = MaterialTheme.typography.headlineSmallEmphasized,
                        )
                        Switch(
                            checked = angleEnabled,
                            onCheckedChange = { enabled ->
                                if (privilegedHelper == null) return@Switch

                                CoroutineScope(Dispatchers.IO).launch {
                                    // Use settings global for DEFAULT mode
                                    privilegedHelper.executeCommand(
                                        "settings put global angle_debug_package com.android.angle"
                                    )

                                    val pkgsResult =
                                        privilegedHelper.executeCommand(
                                            "settings get global angle_gl_driver_selection_pkgs"
                                        )
                                    val currentPkgs = pkgsResult.output.trim()

                                    val valuesResult =
                                        privilegedHelper.executeCommand(
                                            "settings get global angle_gl_driver_selection_values"
                                        )
                                    val currentValues = valuesResult.output.trim()

                                    val pkgList =
                                        if (currentPkgs == "null" || currentPkgs.isEmpty()) {
                                            emptyList()
                                        } else {
                                            currentPkgs.split(",").map { it.trim() }
                                        }

                                    val valueList =
                                        if (currentValues == "null" || currentValues.isEmpty()) {
                                            emptyList()
                                        } else {
                                            currentValues.split(",").map { it.trim() }
                                        }

                                    val newPkgList = mutableListOf<String>()
                                    val newValueList = mutableListOf<String>()

                                    for (i in pkgList.indices) {
                                        if (pkgList[i] != packageName) {
                                            newPkgList.add(pkgList[i])
                                            newValueList.add(
                                                if (i < valueList.size) valueList[i] else "default"
                                            )
                                        }
                                    }

                                    if (enabled) {
                                        newPkgList.add(packageName.toString())
                                        newValueList.add("angle")
                                    } else {
                                        newPkgList.add(packageName.toString())
                                        newValueList.add("native")
                                    }

                                    if (newPkgList.isNotEmpty()) {
                                        privilegedHelper.executeCommand(
                                            "settings put global angle_gl_driver_selection_pkgs ${
                                            newPkgList.joinToString(",")
                                            }"
                                        )
                                        privilegedHelper.executeCommand(
                                            "settings put global angle_gl_driver_selection_values ${
                                            newValueList.joinToString(",")
                                            }"
                                        )
                                    }

                                    checkAngleState()
                                    callback()
                                }
                            },
                        )
                    }
                } else {
                    // ALTERNATIVE mode: Graphics backend selector
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        Text(
                            text = "Graphics Backend",
                            style = MaterialTheme.typography.headlineSmallEmphasized,
                            modifier = Modifier.padding(bottom = 8.dp),
                        )

                        var selectedBackend by remember { mutableStateOf("native") }

                        // Read current backend from device_config
                        LaunchedEffect(interventions) {
                            if (privilegedHelper != null) {
                                val config =
                                    privilegedHelper
                                        .executeCommand(
                                            "device_config get game_overlay $packageName"
                                        )
                                        .output
                                        .trim()

                                if (config != "null" && config.isNotEmpty()) {
                                    // Parse:
                                    // "mode=1,angle=1,downscaleFactor=0.7:mode=2,skiavk=1,..."
                                    val firstMode = config.split(":").firstOrNull() ?: ""
                                    selectedBackend =
                                        when {
                                            firstMode.contains("angle=1") -> "angle"
                                            firstMode.contains("skiavk=1") -> "skiavk"
                                            firstMode.contains("skiagl=1") -> "skiagl"
                                            else -> "native"
                                        }
                                }
                            }
                        }

                        Row(
                            modifier =
                                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            val backends =
                                listOf(
                                    "native" to "Native",
                                    "angle" to "ANGLE",
                                    "skiavk" to "Skia+Vulkan",
                                    "skiagl" to "Skia+GL",
                                )

                            backends.forEach { (value, label) ->
                                Button(
                                    onClick = {
                                        if (privilegedHelper != null) {
                                            CoroutineScope(Dispatchers.IO).launch {
                                                val downscale = currentDownscale.takeIf { it != 1f }
                                                val fps = appliedFps.takeIf { it > 0f }?.toInt()

                                                val params =
                                                    buildList {
                                                            if (value != "native") add("$value=1")
                                                            if (downscale != null)
                                                                add("downscaleFactor=$downscale")
                                                            if (fps != null) add("fps=$fps")
                                                        }
                                                        .joinToString(",")

                                                val allModes =
                                                    (1..4).joinToString(":") { "mode=$it,$params" }
                                                privilegedHelper.executeCommand(
                                                    "device_config put game_overlay $packageName $allModes ; cmd game mode 2 $packageName"
                                                )

                                                selectedBackend = value
                                                checkAngleState()
                                                callback()
                                            }
                                        }
                                    },
                                    colors =
                                        ButtonDefaults.buttonColors(
                                            containerColor =
                                                if (selectedBackend == value)
                                                    MaterialTheme.colorScheme.primary
                                                else MaterialTheme.colorScheme.primaryContainer
                                        ),
                                ) {
                                    Text(label)
                                }
                            }
                        }
                        Text(
                            text =
                                "Please note that setting backends is experimental and you're supposed to report whether you see any difference",
                            modifier =
                                Modifier.padding(8.dp)
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
                                        androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                                    )
                                    .padding(8.dp),
                        )
                    }
                }

                var showDownscaleInfo by remember { mutableStateOf(false) }
                // manual input dialog with textfield for downscale
                if (showDownscaleInfo) {
                    Dialog(
                        onDismissRequest = { showDownscaleInfo = false },
                        properties =
                            DialogProperties(
                                dismissOnBackPress = true,
                                dismissOnClickOutside = true,
                            ),
                    ) {
                        Column(
                            modifier =
                                Modifier.padding(16.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        shape = MaterialTheme.shapes.large,
                                    )
                                    .padding(16.dp)
                        ) {
                            Text(
                                text =
                                    "Enter downscale factor manually. It can be either less or greater than 1. Greater values result in higher than native resolution (oversampling). Please note that some apps may opt-out of these interventions.",
                                modifier = Modifier.padding(bottom = 8.dp),
                            )
                            var temp by remember { mutableStateOf(appliedDownscale) }
                            OutlinedTextField(
                                value = temp.toString(),
                                onValueChange = { temp = it.toFloatOrNull() ?: appliedDownscale },
                                label = { Text("Downscale") },
                                keyboardOptions =
                                    KeyboardOptions(keyboardType = KeyboardType.Number),
                                keyboardActions =
                                    KeyboardActions(onDone = { showDownscaleInfo = false }),
                                singleLine = true,
                                placeholder = { Text("0.5") },
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            if (privilegedHelper != null) {
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    val cmd =
                                                        when (commandMode) {
                                                            CommandMode.DEFAULT ->
                                                                "cmd game set --downscale $temp $packageName ; device_config delete game_overlay $packageName"
                                                            CommandMode.ALTERNATIVE -> {
                                                                val fps =
                                                                    appliedFps
                                                                        .takeIf { it > 0f }
                                                                        ?.toInt()
                                                                val params =
                                                                    buildList {
                                                                            if (angleEnabled)
                                                                                add("angle=1")
                                                                            add(
                                                                                "downscaleFactor=$temp"
                                                                            )
                                                                            if (fps != null)
                                                                                add("fps=$fps")
                                                                        }
                                                                        .joinToString(",")
                                                                val allModes =
                                                                    (1..4).joinToString(":") {
                                                                        "mode=$it,$params"
                                                                    }
                                                                "device_config put game_overlay $packageName $allModes ; cmd game mode 2 $packageName"
                                                            }
                                                        }
                                                    privilegedHelper.executeCommand(cmd)
                                                    currentDownscale = temp
                                                    appliedDownscale = temp
                                                    callback()
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Save",
                                            tint = MaterialTheme.colorScheme.primary,
                                        )
                                    }
                                },
                            )
                            AnimatedVisibility(temp > 2f) {
                                Text(
                                    "You sure about that?",
                                    modifier =
                                        Modifier.fillMaxWidth()
                                            .padding(top = 16.dp)
                                            .background(
                                                MaterialTheme.colorScheme.errorContainer.copy(
                                                    alpha = 0.5f
                                                ),
                                                MaterialTheme.shapes.large,
                                            )
                                            .padding(16.dp),
                                )
                            }
                        }
                    }
                }
                Row {
                    Text(
                        text = "Scaling: $appliedDownscale",
                        style = MaterialTheme.typography.headlineSmallEmphasized,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = "Info",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier =
                            Modifier.size(24.dp).clickable(onClick = { showDownscaleInfo = true }),
                    )
                }

                var showFpsInfo by remember { mutableStateOf(false) }
                // manual input dialog with textfield for FPS
                if (showFpsInfo) {
                    Dialog(
                        onDismissRequest = { showFpsInfo = false },
                        properties =
                            DialogProperties(
                                dismissOnBackPress = true,
                                dismissOnClickOutside = true,
                            ),
                    ) {
                        Column(
                            modifier =
                                Modifier.padding(16.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        shape = MaterialTheme.shapes.large,
                                    )
                                    .padding(16.dp)
                        ) {
                            Text(
                                text =
                                    "Enter FPS limit value manually. This will limit the app's frame rate to the specified value. Please note that not all apps/roms support this feature and it does not unlock engine fps.",
                                modifier = Modifier.padding(bottom = 8.dp),
                            )
                            var temp by remember {
                                mutableStateOf(if (appliedFps > 0f) appliedFps.toInt() else 60)
                            }
                            OutlinedTextField(
                                value = temp.toString(),
                                onValueChange = {
                                    val input = it.substringBefore('.') // Remove fractional part
                                    temp = input.toIntOrNull() ?: temp
                                },
                                label = { Text("FPS limit") },
                                keyboardOptions =
                                    KeyboardOptions(keyboardType = KeyboardType.Number),
                                keyboardActions = KeyboardActions(onDone = { showFpsInfo = false }),
                                singleLine = true,
                                placeholder = { Text("60") },
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            if (privilegedHelper != null) {
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    val cmd =
                                                        when (commandMode) {
                                                            CommandMode.DEFAULT ->
                                                                "cmd game set --fps $temp $packageName ; device_config delete game_overlay $packageName"
                                                            CommandMode.ALTERNATIVE -> {
                                                                val downscale =
                                                                    currentDownscale.takeIf {
                                                                        it != 1f
                                                                    }
                                                                val params =
                                                                    buildList {
                                                                            if (angleEnabled)
                                                                                add("angle=1")
                                                                            if (downscale != null)
                                                                                add(
                                                                                    "downscaleFactor=$downscale"
                                                                                )
                                                                            add("fps=$temp")
                                                                        }
                                                                        .joinToString(",")
                                                                val allModes =
                                                                    (1..4).joinToString(":") {
                                                                        "mode=$it,$params"
                                                                    }
                                                                "device_config put game_overlay $packageName $allModes ; cmd game mode 2 $packageName"
                                                            }
                                                        }
                                                    privilegedHelper.executeCommand(cmd)
                                                    currentFps = temp.toFloat()
                                                    appliedFps = temp.toFloat()
                                                    callback()
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Save",
                                            tint = MaterialTheme.colorScheme.primary,
                                        )
                                    }
                                },
                            )
                        }
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                ) {
                    val downscaleValues =
                        listOf(
                            0.5f to "0.5×",
                            0.33f to "0.33×",
                            0.25f to "0.25×",
                            0.2f to "0.2×",
                            0.1f to "0.1×",
                            1.0f to "Reset",
                        )

                    downscaleValues.forEach { (value, label) ->
                        Button(
                            contentPadding = PaddingValues(0.dp),
                            onClick = {
                                if (privilegedHelper != null) {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        val cmd =
                                            when (commandMode) {
                                                CommandMode.DEFAULT ->
                                                    "cmd game set --downscale $value $packageName ; device_config delete game_overlay $packageName"
                                                CommandMode.ALTERNATIVE -> {
                                                    val fps = appliedFps.takeIf { it > 0f }?.toInt()
                                                    val params =
                                                        buildList {
                                                                if (angleEnabled) add("angle=1")
                                                                add("downscaleFactor=$value")
                                                                if (fps != null) add("fps=$fps")
                                                            }
                                                            .joinToString(",")
                                                    val allModes =
                                                        (1..4).joinToString(":") {
                                                            "mode=$it,$params"
                                                        }
                                                    "device_config put game_overlay $packageName $allModes ; cmd game mode 2 $packageName"
                                                }
                                            }
                                        privilegedHelper.executeCommand(cmd)
                                        currentDownscale = value
                                        appliedDownscale = value
                                        callback()
                                    }
                                }
                            },
                        ) {
                            Text(text = label)
                        }
                    }
                }
                Row {
                    Text(
                        text =
                            "FPS limit: ${
                                if (appliedFps <= 0f) {
                                    "UNSET/UNKNOWN"
                                } else {
                                    "${appliedFps.roundToInt()}"
                                }
                            }",
                        style = MaterialTheme.typography.headlineSmallEmphasized,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = "Edit FPS",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp).clickable(onClick = { showFpsInfo = true }),
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                ) {
                    val fpsValues = listOf(30, 40, 60, 90, 120)

                    fpsValues.forEach { fps ->
                        Button(
                            contentPadding = PaddingValues(0.dp),
                            onClick = {
                                if (privilegedHelper != null) {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        val cmd =
                                            when (commandMode) {
                                                CommandMode.DEFAULT ->
                                                    "cmd game set --fps $fps $packageName ; device_config delete game_overlay $packageName"
                                                CommandMode.ALTERNATIVE -> {
                                                    val downscale =
                                                        currentDownscale.takeIf { it != 1f }
                                                    val params =
                                                        buildList {
                                                                if (angleEnabled) add("angle=1")
                                                                if (downscale != null)
                                                                    add(
                                                                        "downscaleFactor=$downscale"
                                                                    )
                                                                add("fps=$fps")
                                                            }
                                                            .joinToString(",")
                                                    val allModes =
                                                        (1..4).joinToString(":") {
                                                            "mode=$it,$params"
                                                        }
                                                    "device_config put game_overlay $packageName $allModes ; cmd game mode 2 $packageName"
                                                }
                                            }
                                        privilegedHelper.executeCommand(cmd)
                                        currentFps = fps.toFloat()
                                        appliedFps = fps.toFloat()
                                        callback()
                                    }
                                }
                            },
                        ) {
                            Text(text = fps.toString())
                        }
                    }

                    Button(
                        contentPadding = PaddingValues(0.dp),
                        onClick = {
                            if (privilegedHelper != null) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    val cmd =
                                        "cmd game reset $packageName ; device_config delete game_overlay $packageName ; cmd game mode standard $packageName"
                                    privilegedHelper.executeCommand(cmd)
                                    callback()
                                }
                            }
                        },
                    ) {
                        Text(text = "Reset")
                    }
                }

                var showDebugDialog by remember { mutableStateOf(false) }

                // Debug dialog
                if (showDebugDialog) {
                    Dialog(
                        onDismissRequest = { showDebugDialog = false },
                        properties =
                            DialogProperties(
                                dismissOnBackPress = true,
                                dismissOnClickOutside = true,
                            ),
                    ) {
                        var configs by remember { mutableStateOf("") }
                        var modes by remember { mutableStateOf("") }
                        var dumpsys by remember { mutableStateOf("") }
                        LaunchedEffect(Unit) {
                            CoroutineScope(Dispatchers.IO).launch {
                                configs =
                                    privilegedHelper
                                        ?.executeCommand("cmd game list-configs $packageName")
                                        ?.output ?: "ERROR"
                                modes =
                                    privilegedHelper
                                        ?.executeCommand("cmd game list-modes $packageName")
                                        ?.output ?: "ERROR"

                                dumpsys =
                                    privilegedHelper
                                        ?.executeCommand("dumpsys game | grep $packageName")
                                        ?.output ?: "ERROR"
                            }
                        }

                        Column(
                            modifier =
                                Modifier.padding(16.dp)
                                    .background(
                                        MaterialTheme.colorScheme.surface,
                                        shape = MaterialTheme.shapes.large,
                                    )
                                    .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = "Debug Info for $packageName",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(bottom = 16.dp),
                            )
                            Text(
                                text = configs,
                                style = MaterialTheme.typography.bodySmall,
                                modifier =
                                    Modifier.background(
                                            MaterialTheme.colorScheme.surfaceVariant,
                                            shape =
                                                androidx.compose.foundation.shape
                                                    .RoundedCornerShape(8.dp),
                                        )
                                        .padding(12.dp)
                                        .fillMaxWidth(),
                            )
                            Text(
                                text = modes,
                                style = MaterialTheme.typography.bodySmall,
                                modifier =
                                    Modifier.background(
                                            MaterialTheme.colorScheme.surfaceVariant,
                                            shape =
                                                androidx.compose.foundation.shape
                                                    .RoundedCornerShape(8.dp),
                                        )
                                        .padding(12.dp)
                                        .fillMaxWidth(),
                            )
                            Text(
                                text = dumpsys,
                                style = MaterialTheme.typography.bodySmall,
                                modifier =
                                    Modifier.background(
                                            MaterialTheme.colorScheme.surfaceVariant,
                                            shape =
                                                androidx.compose.foundation.shape
                                                    .RoundedCornerShape(8.dp),
                                        )
                                        .padding(12.dp)
                                        .fillMaxWidth(),
                            )

                            Button({
                                CoroutineScope(Dispatchers.IO).launch {
                                    privilegedHelper?.executeCommand(
                                        "cmd game mode custom $packageName"
                                    )
                                    privilegedHelper?.executeCommand("cmd game reset $packageName")

                                    configs =
                                        privilegedHelper
                                            ?.executeCommand("cmd game list-configs $packageName")
                                            ?.output ?: "ERROR retrieving configs"
                                    modes =
                                        privilegedHelper
                                            ?.executeCommand("cmd game list-modes $packageName")
                                            ?.output ?: "ERROR retrieving modes"
                                    callback()
                                }
                            }) {
                                Text("Force set mode to custom")
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Button(
                        onClick = {
                            if (privilegedHelper != null) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    val cmd =
                                        when (commandMode) {
                                            CommandMode.DEFAULT -> "cmd game reset $packageName"
                                            CommandMode.ALTERNATIVE ->
                                                "device_config delete game_overlay $packageName ; cmd game mode standard $packageName"
                                        }
                                    privilegedHelper.executeCommand(cmd)
                                    callback()
                                }
                            }
                        }
                    ) {
                        Text(text = "Reset all")
                    }

                    Button(onClick = { showDebugDialog = true }) { Text(text = "Show debug") }
                }
            }
        }
    }
}

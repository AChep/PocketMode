package com.artemchep.pocketmode.ui.activities.compose

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.artemchep.pocketmode.R
import com.artemchep.pocketmode.models.MainScreen

@Composable
fun Content(ui: MainScreen) {
    ScrollableColumn {
        Card(
            shape = MaterialTheme.shapes.large,
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 16.dp),
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.caption,
                    text = stringResource(R.string.access),
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            shape = MaterialTheme.shapes.large,
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            TroubleshootingCardContent(ui.troubleshooting)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            shape = MaterialTheme.shapes.large,
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            SettingsCardContent(ui.settings)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun TroubleshootingCardContent(ui: MainScreen.Troubleshooting) = ConstraintLayout(
    modifier = Modifier
        .padding(vertical = 16.dp)
        .fillMaxWidth()
) {
    val (
        sectionText,
        rebootText,
        lockBtn, lockText,
        proximityIcon, proximityDistance, proximityText,
        divider,
        labItem,
    ) = createRefs()
    Text(
        modifier = Modifier
            .constrainAs(sectionText) {
                width = Dimension.fillToConstraints
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
            .padding(horizontal = 16.dp),
        style = MaterialTheme.typography.caption,
        text = stringResource(R.string.troubleshoot),
    )
    Text(
        modifier = Modifier
            .constrainAs(rebootText) {
                width = Dimension.fillToConstraints
                top.linkTo(sectionText.bottom, 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
            .padding(horizontal = 16.dp),
        style = MaterialTheme.typography.subtitle1,
        text = stringResource(R.string.troubleshoot_reboot),
    )
    // lock screen test
    Button(
        modifier = Modifier
            .constrainAs(lockBtn) {
                top.linkTo(rebootText.bottom, 16.dp)
                end.linkTo(parent.end, 16.dp)
            },
        onClick = {
            ui.onLockScreen()
        }
    ) {
        Text(
            text = stringResource(R.string.troubleshoot_lock_screen)
        )
    }
    Text(
        modifier = Modifier
            .constrainAs(lockText) {
                width = Dimension.fillToConstraints
                top.linkTo(lockBtn.top)
                start.linkTo(parent.start, 16.dp)
                end.linkTo(lockBtn.start, 16.dp)
            },
        text = stringResource(R.string.troubleshoot_lock_screen_description)
    )
    val lockBarrier = createBottomBarrier(lockBtn, lockText)
    // proximity test
    Icon(
        modifier = Modifier
            .constrainAs(proximityIcon) {
                top.linkTo(proximityText.top)
                bottom.linkTo(proximityText.bottom)
                end.linkTo(parent.end, 16.dp)
            },
        imageVector = if (ui.proximityIsClose) Icons.Outlined.Edit else Icons.Outlined.Edit,
        contentDescription = null,
    )
    Text(
        modifier = Modifier
            .constrainAs(proximityDistance) {
                top.linkTo(proximityText.top)
                bottom.linkTo(proximityText.bottom)
                end.linkTo(proximityIcon.start, 16.dp)
            },
        text = stringResource(R.string.cm, ui.proximityCm)
    )
    Text(
        modifier = Modifier
            .constrainAs(proximityText) {
                width = Dimension.fillToConstraints
                top.linkTo(lockBarrier, 16.dp)
                start.linkTo(parent.start, 16.dp)
                end.linkTo(proximityDistance.start, 16.dp)
            },
        text = stringResource(R.string.troubleshoot_proximity_description)
    )
    val proximityBarrier =
        createBottomBarrier(proximityIcon, proximityDistance, proximityText)

    Divider(
        modifier = Modifier
            .constrainAs(divider) {
                width = Dimension.fillToConstraints
                top.linkTo(proximityBarrier, 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
    )

    ListItem(
        modifier = Modifier
            .constrainAs(labItem) {
                width = Dimension.fillToConstraints
                top.linkTo(divider.bottom, 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
            .clickable(
                onClick = {
                    ui.onLaboratoryScreen()
                },
            ),
        icon = {
            Icon(
                imageVector = Icons.Outlined.Place,
                contentDescription = null,
            )
        },
        text = {
            Text(
                text = stringResource(R.string.help_test)
            )
        }
    )
}

@Composable
fun SettingsCardContent(ui: MainScreen.Settings) = ConstraintLayout(
    modifier = Modifier
        .padding(vertical = 16.dp)
        .fillMaxWidth()
) {
    val (
        sectionText,
        delayDescriptionText, delayFromText, delayToText, delaySlider,
        delayCurText, delayResetBtn,
        dividerSwitchStart,
        dividerSwitchEnd,
        vibrateBeforeLocking, overlayBeforeLocking, turnBlackIfCovered,
        aboutSectionText,
        aboutDeveloperText,
        aboutDescriptionText,
    ) = createRefs()
    Text(
        modifier = Modifier
            .constrainAs(sectionText) {
                width = Dimension.fillToConstraints
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
            .padding(horizontal = 16.dp),
        style = MaterialTheme.typography.caption,
        text = stringResource(R.string.settings),
    )
    // delay
    Text(
        modifier = Modifier
            .constrainAs(delayDescriptionText) {
                width = Dimension.fillToConstraints
                top.linkTo(sectionText.bottom, 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
            .padding(horizontal = 16.dp),
        style = MaterialTheme.typography.subtitle1,
        text = stringResource(R.string.settings_lock_screen_delay_description),
    )
    Text(
        modifier = Modifier
            .constrainAs(delayFromText) {
                top.linkTo(delaySlider.top)
                bottom.linkTo(delaySlider.bottom)
                start.linkTo(parent.start, 16.dp)
            },
        style = MaterialTheme.typography.caption,
        text = stringResource(R.string.ms, ui.lockDelayMinMs),
    )
    Text(
        modifier = Modifier
            .constrainAs(delayToText) {
                top.linkTo(delaySlider.top)
                bottom.linkTo(delaySlider.bottom)
                end.linkTo(parent.end, 16.dp)
            },
        style = MaterialTheme.typography.caption,
        text = stringResource(R.string.ms, ui.lockDelayMaxMs),
    )
    Slider(
        modifier = Modifier
            .constrainAs(delaySlider) {
                width = Dimension.fillToConstraints
                top.linkTo(delayDescriptionText.bottom, 16.dp)
                start.linkTo(delayFromText.end, 16.dp)
                end.linkTo(delayToText.start, 16.dp)
            },
        value = 0.9f,
        onValueChange = {
        },
    )
    Button(
        modifier = Modifier
            .constrainAs(delayResetBtn) {
                top.linkTo(delaySlider.bottom, 16.dp)
                end.linkTo(parent.end, 16.dp)
            },
        onClick = {}
    ) {
        Text(
            text = stringResource(R.string.troubleshoot_lock_screen)
        )
    }
    Text(
        modifier = Modifier
            .constrainAs(delayCurText) {
                width = Dimension.fillToConstraints
                top.linkTo(delayResetBtn.top)
                start.linkTo(parent.start, 16.dp)
                end.linkTo(delayResetBtn.start, 16.dp)
            },
        text = stringResource(R.string.settings_lock_screen_delay_cur, 100),
    )
    val delayBarrier = createBottomBarrier(delayResetBtn, delayCurText)
    // switch
    Divider(
        modifier = Modifier
            .constrainAs(dividerSwitchStart) {
                width = Dimension.fillToConstraints
                top.linkTo(delayBarrier, 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
    )
    ListItemTextSwitch(
        modifier = Modifier
            .constrainAs(vibrateBeforeLocking) {
                width = Dimension.fillToConstraints
                top.linkTo(dividerSwitchStart.bottom, 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
        text = stringResource(R.string.settings_vibrate_before_locking),
        checked = ui.isVibrateBeforeLockingEnabled,
        onCheckedChange = {
            ui.onVibrateBeforeLockingChanged(it)
        },
    )
    ListItemTextSwitch(
        modifier = Modifier
            .constrainAs(overlayBeforeLocking) {
                width = Dimension.fillToConstraints
                top.linkTo(vibrateBeforeLocking.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
        text = stringResource(R.string.settings_overlay_before_locking),
        checked = ui.isShowOverlayBeforeLockingEnabled,
        onCheckedChange = {
            ui.onShowOverlayBeforeLockingChanged(it)
        },
    )
    ListItemTextSwitch(
        modifier = Modifier
            .constrainAs(turnBlackIfCovered) {
                width = Dimension.fillToConstraints
                top.linkTo(overlayBeforeLocking.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
        text = stringResource(R.string.settings_turn_screen_off_when_covered),
        checked = ui.isTurnScreenBlackEnabled,
        onCheckedChange = {
            ui.onTurnScreenBlackChanged(it)
        },
    )
    // about
    Divider(
        modifier = Modifier
            .constrainAs(dividerSwitchEnd) {
                width = Dimension.fillToConstraints
                top.linkTo(turnBlackIfCovered.bottom, 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
    )
    Text(
        modifier = Modifier
            .constrainAs(aboutSectionText) {
                width = Dimension.fillToConstraints
                top.linkTo(dividerSwitchEnd.bottom, 16.dp)
                start.linkTo(parent.start, 16.dp)
                end.linkTo(aboutDeveloperText.start, 16.dp)
            },
        style = MaterialTheme.typography.caption,
        text = stringResource(R.string.about),
    )
    Text(
        modifier = Modifier
            .constrainAs(aboutDeveloperText) {
                baseline.linkTo(aboutSectionText.baseline)
                end.linkTo(parent.end, 16.dp)
            },
        style = MaterialTheme.typography.caption,
        text = stringResource(
            R.string.about_author,
            stringResource(R.string.about_author_artem_chepurnoy)
        ),
    )
    Text(
        modifier = Modifier
            .constrainAs(aboutDescriptionText) {
                width = Dimension.fillToConstraints
                top.linkTo(aboutSectionText.bottom, 16.dp)
                start.linkTo(parent.start, 16.dp)
                end.linkTo(parent.end, 16.dp)
            },
        style = MaterialTheme.typography.subtitle1,
        text = stringResource(R.string.about_description),
    )
    // refs
    val (
        donateToMe, sendBugReport, dontKillMyApp, contribute, translate,
    ) = createRefs()
    ListItem(
        modifier = Modifier
            .constrainAs(donateToMe) {
                width = Dimension.fillToConstraints
                top.linkTo(aboutDescriptionText.bottom, 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
        icon = {
            Icon(
                imageVector = Icons.Outlined.ShoppingCart,
                contentDescription = null,
            )
        },
        text = {
            Text(
                text = stringResource(R.string.help_donate)
            )
        }
    )
    ListItem(
        modifier = Modifier
            .constrainAs(sendBugReport) {
                width = Dimension.fillToConstraints
                top.linkTo(donateToMe.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
        icon = {
            Icon(
                imageVector = Icons.Outlined.Build,
                contentDescription = null,
            )
        },
        text = {
            Text(
                text = stringResource(R.string.help_bug_report)
            )
        }
    )
    ListItem(
        modifier = Modifier
            .constrainAs(dontKillMyApp) {
                width = Dimension.fillToConstraints
                top.linkTo(sendBugReport.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
        text = {
            Text(
                text = stringResource(R.string.help_bug_report_dont_kill_my_app)
            )
        }
    )
    ListItem(
        modifier = Modifier
            .constrainAs(contribute) {
                width = Dimension.fillToConstraints
                top.linkTo(dontKillMyApp.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
        text = {
            Text(
                text = stringResource(R.string.help_code)
            )
        }
    )
    ListItem(
        modifier = Modifier
            .constrainAs(translate) {
                width = Dimension.fillToConstraints
                top.linkTo(contribute.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
        text = {
            Text(
                text = stringResource(R.string.help_translate)
            )
        }
    )
}

@Composable
fun ListItemTextSwitch(
    modifier: Modifier = Modifier,
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) = ListItem(
    modifier = modifier
        .clickable(
            onClick = {
                onCheckedChange(!checked)
            },
        ),
    text = {
        Text(
            modifier = Modifier
                .padding(vertical = 6.dp),
            text = text,
        )
    },
    trailing = {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
)

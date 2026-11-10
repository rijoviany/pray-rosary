package com.trivium.rosarium.update

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UpdateStatus(
    val isAvailable: Boolean = false,
    val isDownloaded: Boolean = false,
    val availableVersionCode: Int = 0,
    val appUpdateInfo: AppUpdateInfo? = null
)

class InAppUpdateManager(private val context: Context) {

    private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(context)

    private val _updateStatus = MutableStateFlow(UpdateStatus())
    val updateStatus: StateFlow<UpdateStatus> = _updateStatus.asStateFlow()

    private val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            _updateStatus.value = _updateStatus.value.copy(
                isDownloaded = true
            )
        }
    }

    init {
        appUpdateManager.registerListener(installStateUpdatedListener)
    }

    /**
     * Checks Google Play for any available update.
     */
    fun checkForUpdate(
        onResult: (Boolean, AppUpdateInfo?) -> Unit = { _, _ -> }
    ) {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            val isAvailable = appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isDownloaded = appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED

            _updateStatus.value = UpdateStatus(
                isAvailable = isAvailable,
                isDownloaded = isDownloaded,
                availableVersionCode = appUpdateInfo.availableVersionCode(),
                appUpdateInfo = if (isAvailable) appUpdateInfo else null
            )
            onResult(isAvailable, if (isAvailable) appUpdateInfo else null)
        }.addOnFailureListener { e ->
            Log.w("InAppUpdateManager", "Failed to check for updates: ${e.message}")
            _updateStatus.value = UpdateStatus(isAvailable = false)
            onResult(false, null)
        }
    }

    /**
     * Checks if a previous update was downloaded or is currently in progress, and resumes.
     */
    fun checkResumeUpdate(activity: Activity) {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                _updateStatus.value = _updateStatus.value.copy(isDownloaded = true)
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                try {
                    appUpdateManager.startUpdateFlow(
                        appUpdateInfo,
                        activity,
                        AppUpdateOptions.defaultOptions(AppUpdateType.IMMEDIATE)
                    )
                } catch (e: Exception) {
                    Log.w("InAppUpdateManager", "Resume update failed: ${e.message}")
                }
            }
        }
    }

    /**
     * Launches the in-app update flow or opens the Play Store page as fallback.
     */
    fun startUpdate(
        activity: Activity,
        launcher: ActivityResultLauncher<IntentSenderRequest>? = null,
        preferredType: Int = AppUpdateType.FLEXIBLE
    ) {
        val info = _updateStatus.value.appUpdateInfo
        if (info != null) {
            val updateType = if (info.isUpdateTypeAllowed(preferredType)) {
                preferredType
            } else if (info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                AppUpdateType.IMMEDIATE
            } else {
                null
            }

            if (updateType != null) {
                try {
                    if (launcher != null) {
                        appUpdateManager.startUpdateFlowForResult(
                            info,
                            launcher,
                            AppUpdateOptions.defaultOptions(updateType)
                        )
                        return
                    } else {
                        appUpdateManager.startUpdateFlow(
                            info,
                            activity,
                            AppUpdateOptions.defaultOptions(updateType)
                        )
                        return
                    }
                } catch (e: Exception) {
                    Log.w("InAppUpdateManager", "startUpdateFlow failed: ${e.message}, falling back to Play Store link")
                }
            }
        }

        // Direct Play Store fallback
        openPlayStore(activity)
    }

    /**
     * Completes a flexible update by restarting the app.
     */
    fun completeFlexibleUpdate() {
        appUpdateManager.completeUpdate()
    }

    /**
     * Dismisses the current in-app prompt in memory for this session.
     */
    fun dismissPrompt() {
        _updateStatus.value = _updateStatus.value.copy(isAvailable = false)
    }

    fun unregister() {
        appUpdateManager.unregisterListener(installStateUpdatedListener)
    }

    companion object {
        fun openPlayStore(context: Context) {
            val packageName = context.packageName
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(webIntent)
            }
        }
    }
}

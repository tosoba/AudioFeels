package com.trm.audiofeels.core.player

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaStyleNotificationHelper.MediaStyle
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.toBitmap
import com.google.common.collect.ImmutableList
import com.trm.audiofeels.core.base.di.MainActivityIntent
import com.trm.audiofeels.core.base.di.ServiceContext
import com.trm.audiofeels.core.base.di.ServiceLifecycleScope
import com.trm.audiofeels.core.base.di.ServiceScope
import com.trm.audiofeels.core.base.util.AppCoroutineDispatchers
import com.trm.audiofeels.core.network.image.loadImageOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@UnstableApi
@ServiceScope
@Inject
class PlayerNotificationProvider(
  @ServiceContext private val context: Context,
  @MainActivityIntent private val mainActivityIntent: Intent,
  @ServiceLifecycleScope private val scope: LifecycleCoroutineScope,
  private val appCoroutineDispatchers: AppCoroutineDispatchers,
  private val imageLoader: ImageLoader,
  private val platformContext: PlatformContext,
) : MediaNotification.Provider {
  private val notificationManager = requireNotNull(context.getSystemService<NotificationManager>())

  override fun createNotification(
    session: MediaSession,
    customLayout: ImmutableList<CommandButton>,
    actionFactory: MediaNotification.ActionFactory,
    onNotificationChangedCallback: MediaNotification.Provider.Callback,
  ): MediaNotification {
    ensureNotificationChannel()

    val player = session.player
    val metadata = player.mediaMetadata

    val builder =
      NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_NAME)
        .setContentTitle(metadata.title)
        .setContentText(metadata.artist)
        .setStyle(MediaStyle(session))
        .setSmallIcon(R.drawable.ic_music)
        .setContentIntent(
          PendingIntent.getActivity(
            context,
            MAIN_ACTIVITY_REQUEST_CODE,
            mainActivityIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
          )
        )

    //    getNotificationActions(
    //        mediaSession = session,
    //        actionFactory = actionFactory,
    //        playWhenReady = player.playWhenReady,
    //      )
    //      .forEach(builder::addAction)

    metadata.artworkUri?.let {
      scope.launch {
        builder.setLargeIcon(loadBitmap(it))
        onNotificationChangedCallback.onNotificationChanged(
          MediaNotification(NOTIFICATION_ID, builder.build())
        )
      }
    }

    return MediaNotification(NOTIFICATION_ID, builder.build())
  }

  override fun handleCustomCommand(session: MediaSession, action: String, extras: Bundle): Boolean =
    true

  private fun ensureNotificationChannel() {
    if (
      Build.VERSION.SDK_INT < Build.VERSION_CODES.O ||
        notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_NAME) != null
    ) {
      return
    }

    notificationManager.createNotificationChannel(
      NotificationChannel(
        NOTIFICATION_CHANNEL_NAME,
        context.getString(R.string.player_notification_channel_name),
        NotificationManager.IMPORTANCE_LOW,
      )
    )
  }

  //  private fun getNotificationActions(
  //    mediaSession: MediaSession,
  //    actionFactory: MediaNotification.ActionFactory,
  //    playWhenReady: Boolean,
  //  ): List<NotificationCompat.Action> =
  //    listOf(
  //      PlayerActions.getPlayPreviousAction(context, mediaSession, actionFactory),
  //      PlayerActions.getPlayPauseAction(context, mediaSession, actionFactory, playWhenReady),
  //      PlayerActions.getPlayNextAction(context, mediaSession, actionFactory),
  //    )

  private suspend fun loadBitmap(uri: Uri): Bitmap? =
    withContext(appCoroutineDispatchers.io) {
        imageLoader.loadImageOrNull(platformContext, uri.toString())
      }
      ?.toBitmap()

  companion object {
    private const val NOTIFICATION_ID = 1001
    private const val NOTIFICATION_CHANNEL_NAME = "MusicNotificationChannel"
    private const val MAIN_ACTIVITY_REQUEST_CODE = 1002
  }
}

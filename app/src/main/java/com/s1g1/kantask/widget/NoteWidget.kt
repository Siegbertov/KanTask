package com.s1g1.kantask.widget

import androidx.glance.GlanceModifier
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionStartActivity
import android.content.Context
import android.content.Intent
import androidx.compose.ui.unit.dp
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.background
import androidx.glance.layout.size
import com.s1g1.kantask.MainActivity
import com.s1g1.kantask.R
import com.s1g1.kantask.ui.ACTION_ADD_NEW_NOTE

object NoteWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val sizeBG = 45.dp
            val sizeIMG = 30.dp

            Box(
                modifier = GlanceModifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                Box(
                    modifier = GlanceModifier
                        .size(sizeBG)
                        .background(ImageProvider(R.drawable.widget_background))
                        .clickable(
                            actionStartActivity(
                                Intent(context, MainActivity::class.java).apply {
                                    action = ACTION_ADD_NEW_NOTE
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.ic_add_note),
                        contentDescription = null,
                        modifier = GlanceModifier.size(sizeIMG),
                    )
                }
            }
        }
    }

}
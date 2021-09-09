package com.byagowi.persiancalendar.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.byagowi.persiancalendar.R
import com.byagowi.persiancalendar.ui.utils.createUpNavigationComposeView

class LicensesFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = createUpNavigationComposeView(layoutInflater) @Composable { setTitle, _ ->
        setTitle(stringResource(R.string.about_license_title))
        val context = LocalContext.current
        val sections = remember { context.resources.getCreditsSections() }
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            val isOpened = remember { List(sections.size) { false }.toMutableStateList() }
            LazyColumn {
                itemsIndexed(sections) { i, (title, license, text) ->
                    Column(modifier = Modifier
                        .clickable { isOpened[i] = !isOpened[i] }
                        .animateContentSize()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(title)
                            Spacer(modifier = Modifier.width(4.dp))
                            if (license != null) Text(
                                license,
                                modifier = Modifier
                                    .background(
                                        Color.LightGray, RoundedCornerShape(CornerSize(3.dp))
                                    )
                                    .padding(horizontal = 4.dp),
                                style = TextStyle(Color.DarkGray, 12.sp)
                            )
                        }
                        if (isOpened[i]) Text(text)
                        Divider(color = Color.LightGray)
                    }
                }
            }
        }
    }
}

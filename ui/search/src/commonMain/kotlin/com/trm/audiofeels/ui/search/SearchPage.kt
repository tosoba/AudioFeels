package com.trm.audiofeels.ui.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.trm.audiofeels.core.ui.resources.Res
import com.trm.audiofeels.core.ui.resources.search
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchPage(viewModel: SearchViewModel, topSpacerHeight: Dp, bottomSpacerHeight: Dp) {
  Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
    Spacer(modifier = Modifier.height(topSpacerHeight))

    Text(stringResource(Res.string.search))

    Spacer(modifier = Modifier.height(bottomSpacerHeight))
  }
}

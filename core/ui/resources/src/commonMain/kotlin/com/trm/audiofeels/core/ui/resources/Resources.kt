package com.trm.audiofeels.core.ui.resources

import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.PluralStringResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getPluralString
import org.jetbrains.compose.resources.getString

fun getStringBlocking(stringResource: StringResource): String = runBlocking {
  getString(stringResource)
}

fun getStringBlocking(resource: StringResource, vararg formatArgs: Any): String = runBlocking {
  getString(resource, *formatArgs)
}

fun getPluralStringBlocking(resource: PluralStringResource, quantity: Int): String = runBlocking {
  getPluralString(resource, quantity)
}

fun getPluralStringBlocking(
  resource: PluralStringResource,
  quantity: Int,
  vararg formatArgs: Any,
): String = runBlocking {
  getPluralString(resource, quantity, *formatArgs)
}

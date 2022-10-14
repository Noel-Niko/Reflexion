package com.livingtechusa.reflexion.ui.build

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.magnifier
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.livingtechusa.reflexion.ui.viewModels.ItemViewModel
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Image
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.data.entities.ReflexionItem
import com.livingtechusa.reflexion.data.entities.ReflexionItem.Companion.NAME
import kotlin.time.toDuration

@Composable
fun BuildItemScreen() {
    val configuration = LocalConfiguration.current
    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        BuildItemScreenLandscape()
    } else {
        val itemViewModel: ItemViewModel = viewModel(ItemViewModel::class.java)
        val reflexionItem by itemViewModel.reflexionItem.collectAsState()
        val isParent by itemViewModel.isParent.collectAsState()
        val topic by itemViewModel.topic.collectAsState()
        val parentName by itemViewModel.parentName.collectAsState()
        val keyWords by itemViewModel.keyWords.collectAsState()
        val linkedLists by itemViewModel.linkedLists.collectAsState()
        val siblings by itemViewModel.siblings.collectAsState()
        val children by itemViewModel.children.collectAsState()
        val context = LocalContext.current
        var tempReflexionItem: ReflexionItem = reflexionItem

        val scaffoldState = rememberScaffoldState()
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    Toast.makeText(context, "Changes Saved", Toast.LENGTH_SHORT).show()
                    itemViewModel.onTriggerEvent(BuildEvent.UpdateALLFieldText(tempReflexionItem))
                }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_baseline_save_alt_24),
                        contentDescription = null
                    )
                }
            }
        ) {
            it // padding values?
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item {
                    Spacer(Modifier.height(16.dp))
                    if (reflexionItem.parent == null) {
                        Row(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            var name = ""
                            Column(Modifier.weight(1f)) {
                                Text(text = stringResource(R.string.topic))
                            }
                            Column(
                                Modifier.weight(3f)
                            ) {
                                TextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = reflexionItem.name,
                                    onValueChange = {
                                        tempReflexionItem.name = it
                                    }
                                )
                            }
                        }

                    } else {
                        Row(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            var name = ""
                            Column(Modifier.weight(1f)) {
                                Text(text = stringResource(R.string.title))
                            }
                            Column(
                                Modifier.weight(3f)
                            ) {
                                TextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = reflexionItem.name,
                                    onValueChange = {
                                        tempReflexionItem.name = it
                                    }
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(text = stringResource(R.string.description))
                        TextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = reflexionItem.description ?: "",
                            onValueChange = {
                                tempReflexionItem.description = it
                            }
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(text = stringResource(R.string.detailedDescription))
                        TextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = reflexionItem.detailedDescription ?: "",
                            onValueChange = {
                                tempReflexionItem.detailedDescription = it
                            }
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Column(
                            Modifier.weight(3f)
                        ) {
                            ClickableText(
                                modifier = Modifier.fillMaxWidth(),
                                text = AnnotatedString(stringResource(R.string.saved_video)),
                                onClick = {
                                    Toast.makeText(context, "Button Clicked", Toast.LENGTH_SHORT).show()
                                    // TODO: show linked video or add/update video dialog
                                    // todo if dialog response is update - save new url
                                }
                            )
                        }
                        Column(
                            Modifier.weight(1f)
                        ) {
                            IconButton(
                                onClick = {
                                    Toast.makeText(context, "Button Clicked", Toast.LENGTH_SHORT).show()
                                    itemViewModel.onTriggerEvent(BuildEvent.UpdateSavedVideo)
                                }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_baseline_get_app_24),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Column(
                            Modifier.weight(1f)
                        ) {
                            ClickableText(
                                modifier = Modifier.fillMaxWidth(),
                                text = AnnotatedString(stringResource(R.string.video_link)),
                                onClick = {
                                    Toast.makeText(context, "Button Clicked", Toast.LENGTH_SHORT).show()
                                    // TODO: show linked video or add/update video dialog
                                    // todo if dialog response is update - save new url
                                }
                            )
                        }
                        Column(
                            Modifier.weight(1f)
                        ) {
                            IconButton(
                                onClick = {
                                    Toast.makeText(context, "Button Clicked", Toast.LENGTH_SHORT).show()
                                    itemViewModel.onTriggerEvent(BuildEvent.UpdateVideoURL)
                                }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_baseline_get_app_24),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Column(
                            Modifier.weight(1f)
                        ) {
                            Button(
                                onClick = {
                                    Toast.makeText(context, "Button Clicked", Toast.LENGTH_LONG).show()
                                    itemViewModel.onTriggerEvent(BuildEvent.ShowSiblings)
                                }
                            ) {
                                Text(stringResource(R.string.siblings))
                            }
                        }
                        Column(
                            Modifier.weight(1f)
                        ) {
                            Button(onClick = {
                                Toast.makeText(context, "Button Clicked", Toast.LENGTH_LONG).show()
                                itemViewModel.onTriggerEvent(BuildEvent.ShowChildren)
                            }
                            ) {
                                Text(stringResource(R.string.children))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    if (reflexionItem.hasChildren == false) {
                        Button(onClick = {
                            Toast.makeText(context, "Button Clicked", Toast.LENGTH_LONG).show()
                            itemViewModel.onTriggerEvent(BuildEvent.Delete)
                        }
                        ) {
                            Text(stringResource(R.string.delete))
                        }
                    }
                }
            }
        }
    }
}


//@Preview
//@Composable
//fun ComposablePreview() {
//    BuildItemScreen()
//}
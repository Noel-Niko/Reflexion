package com.livingtechusa.reflexion.ui.customListDisplay

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.livingtechusa.reflexion.R
import com.livingtechusa.reflexion.navigation.Screen
import com.livingtechusa.reflexion.ui.components.ImageCard
import com.livingtechusa.reflexion.ui.viewModels.CustomListsViewModel
import com.livingtechusa.reflexion.util.Constants
import com.livingtechusa.reflexion.util.ResourceProviderSingleton
import com.livingtechusa.reflexion.util.extensions.findActivity
import com.livingtechusa.reflexion.util.scopedStorageUtils.CustomListVideoImagePreviewCard
import com.livingtechusa.reflexion.util.scopedStorageUtils.DocumentFilePreviewCardListView

const val CUSTOM_LIST_DISPLAY = "customListDisplay"

@Composable
fun CustomListDisplayScreen(
    viewModel: CustomListsViewModel = hiltViewModel(),
    navController: NavHostController,
    headNodePk: Long
) {

    val context = LocalContext.current
    if (context.findActivity() != null) {
        CustomListDisplayCompactScreen(
            navController = navController,
            headNodePk = headNodePk,
            viewModel = viewModel
        )
    }
}

@Composable
fun CustomListDisplayContent(
    paddingValues: PaddingValues?,
    viewModel: CustomListsViewModel,
    navController: NavHostController
) {

    val selectedList by viewModel.customList.collectAsState()
    val children by viewModel.children.collectAsState()
    val childImageList by viewModel.childListImages.collectAsState()
    val childVideoUriList by viewModel.childVideoUriResourceList.collectAsState()
    val context = LocalContext.current
    val resource = ResourceProviderSingleton
    val colorStops: Array<out Pair<Float, Color>> =
        arrayOf(Pair(10f, Color.Black), Pair(5f, Color.Red))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = paddingValues ?: PaddingValues(8.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            /* TITLE */
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    Modifier
                        .padding(12.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelMedium,
                        text = stringResource(R.string.title)
                    )
                }
                Column(
                    Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                        text = selectedList.itemName.toString(),
                    )
                }
            }

        }
        Spacer(Modifier.height(16.dp))

        // Row of Cards
        LazyRow(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(children.size) { childItemIndex ->
                Card(
                    modifier = Modifier
                        .fillParentMaxSize()
                        .padding(16.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            navController.navigate(Screen.BuildItemScreen.route + "/" + children[childItemIndex].autogenPk)
                        },

                    elevation = 6.dp,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .verticalScroll(rememberScrollState())
                    ) {
                        /* IMAGE */
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier
                                    .weight(3f)
                                    .align(Alignment.CenterVertically)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(12.dp, 12.dp, 12.dp, 12.dp)
                                        .border(
                                            1.dp, Brush.verticalGradient(colorStops = colorStops),
                                            TextFieldDefaults.OutlinedTextFieldShape
                                        )
                                        .align(Alignment.End)
                                ) {
                                    if (childImageList.isEmpty()
                                            .not() && childImageList[childItemIndex] != null
                                    ) {
                                        ImageCard(childImageList[childItemIndex]!!, navController)
                                    }
                                }
                            }
                        }
                        /* TITLE */
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp), Arrangement.Center
                        ) {
                            Text(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.titleLarge,
                                text = children[childItemIndex].name,
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        /* DESCRIPTION */
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth()
                        ) {
                            Column() {
                                Text(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.labelMedium,
                                    text = stringResource(R.string.description)
                                )
                            }
                            Column(
                                Modifier
                                    .padding(4.dp)
                                    .weight(1f),
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Text(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyLarge,
                                    text = children[childItemIndex].description.toString(),
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        /* DETAILS */
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth()
                        ) {
                            Column() {
                                Text(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.labelMedium,
                                    text = stringResource(R.string.detailedDescription)
                                )
                            }
                            Column(
                                Modifier
                                    .padding(4.dp)
                                    .weight(1f),
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Text(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyLarge,
                                    text = children[childItemIndex].detailedDescription.toString(),
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            /* SAVED VIDEO */
                            Column(
                                Modifier
                                    .padding(12.dp)
                                    .weight(1f)

                            ) {
                                Text(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxWidth()
                                        .clickable {
                                            if (children[childItemIndex].videoUri.isNullOrEmpty()) {
                                                Toast
                                                    .makeText(
                                                        context,
                                                        resource.getString(R.string.is_saved),
                                                        Toast.LENGTH_SHORT
                                                    )
                                                    .show()
                                            } else {
                                                navController.navigate(Screen.VideoViewCustomList.route + "/" + childItemIndex)
                                            }
                                        },
                                    text = AnnotatedString(stringResource(R.string.stored_video)),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                if (children[childItemIndex].videoUri.isNullOrEmpty().not()) {
                                    if (childVideoUriList.isEmpty()
                                            .not() && childVideoUriList.size > childItemIndex
                                    ) {
                                        if (childVideoUriList[childItemIndex] != null) {
                                            DocumentFilePreviewCardListView(
                                                resource = childVideoUriList[childItemIndex]!!,
                                                navController = navController,
                                                index = childItemIndex
                                            )
                                        }
                                    }
                                }
                            }

                            /* VIDEO URL */
                            Column(
                                Modifier
                                    .padding(12.dp)
                                    .weight(1f)
                            ) {
                                Text(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxWidth()
                                        .clickable(
                                            onClick = {
                                                if (children[childItemIndex].videoUrl == Constants.EMPTY_STRING) {
                                                    Toast
                                                        .makeText(
                                                            context,
                                                            resource.getString(R.string.did_you_add_a_video_link),
                                                            Toast.LENGTH_SHORT
                                                        )
                                                        .show()
                                                } else {
                                                    val intent = Intent(
                                                        Intent.ACTION_VIEW,
                                                        Uri.parse(children[childItemIndex].videoUrl)
                                                    )
                                                    ContextCompat.startActivity(
                                                        context,
                                                        intent,
                                                        null
                                                    )
                                                }
                                            },
                                        ),
                                    text = AnnotatedString(stringResource(R.string.linked_video)),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                if (children[childItemIndex].videoUrl.isNullOrEmpty().not()) {
                                    CustomListVideoImagePreviewCard(
                                        urlString = children[childItemIndex].videoUrl,
                                        navController = navController,
                                        docType = Constants.VIDEO_URL
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

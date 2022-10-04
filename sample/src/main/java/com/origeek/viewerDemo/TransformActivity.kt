package com.origeek.viewerDemo

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HighlightOff
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.accompanist.pager.ExperimentalPagerApi
import com.origeek.imageViewer.*
import com.origeek.viewerDemo.base.BaseActivity
import com.origeek.viewerDemo.ui.component.LazyGridLayout
import com.origeek.viewerDemo.ui.component.rememberCoilImagePainter
import com.origeek.viewerDemo.ui.theme.ViewerDemoTheme
import kotlinx.coroutines.launch
import java.util.*
import java.util.stream.Collectors

/**
 * @program: ImageViewer
 *
 * @description:
 *
 * @author: JVZIYAOYAO
 *
 * @create: 2022-09-21 18:20
 **/
class TransformActivity : BaseActivity() {

    private fun getItemList(time: Int = 1): List<DrawableItem> {
        val srcList = listOf(
            R.drawable.img_01,
            R.drawable.img_02,
            R.drawable.img_03,
            R.drawable.img_04,
            R.drawable.img_05,
            R.drawable.img_06,
        )
        val resList = mutableListOf<Int>()
        for (i in 0 until time) {
            resList.addAll(srcList)
        }
        return resList.stream().map {
            DrawableItem(
                id = UUID.randomUUID().toString(),
                res = it
            )
        }.collect(Collectors.toList())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val images = getItemList()
        setBasicContent {
            ViewerDemoTheme {
                TransformBody(images)
            }
        }
    }

}

data class DrawableItem(
    val id: String,
    val res: Int,
)

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TransformBody(images: List<DrawableItem>) {
    val settingState = rememberSettingState()
    val scope = rememberCoroutineScope()
    val transformContentState = rememberTransformContentState()
    val previewerState = rememberPreviewerState(
        transformState = transformContentState,
        animationSpec = tween(settingState.animationDuration)
    )
    if (settingState.verticalDrag) {
        previewerState.enableVerticalDrag { images[it].id }
    } else {
        previewerState.disableVerticalDrag()
    }
    if (previewerState.canClose || previewerState.animating) BackHandler {
        if (previewerState.canClose) scope.launch {
            if (settingState.transformExit) {
                val id = images[previewerState.currentPage].id
                previewerState.closeTransform(id)
            } else {
                previewerState.close()
            }
        }
    }
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val horizontal = maxWidth > maxHeight
        val lineCount = if (horizontal) 6 else 3
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(3F),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(text = "🎈 Transform")
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
                    .weight(if (horizontal) 6F else 7F)
            ) {
                LazyGridLayout(
                    modifier = Modifier.fillMaxSize(),
                    columns = lineCount,
                    size = images.size,
                    padding = 2.dp,
                ) { index ->
                    val item = images[index]
                    val painter = painterResource(id = item.res)
                    val itemState = rememberTransformItemState()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1F),
                        contentAlignment = Alignment.Center
                    ) {
                        ScaleGrid(onTap = {
                            scope.launch {
                                if (settingState.transformEnter) {
                                    previewerState.openTransform(
                                        index = index,
                                        itemState = itemState,
                                    )
                                } else {
                                    previewerState.open(index)
                                }
                            }
                        }) {
                            TransformImageView(
                                painter = painter,
                                itemState = itemState,
                                key = item.id,
                                contentState = transformContentState,
                            )
                        }
                    }
                }
            }
        }
        SettingSurface(settingState)
        ImagePreviewer(
            modifier = Modifier.fillMaxSize(),
            count = images.size,
            state = previewerState,
            imageLoader = { index ->
                val image = images[index].res
                rememberCoilImagePainter(image = image)
            },
            currentViewerState = {},
        )
    }
}

@Composable
fun SettingItem(
    label: String,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 14.sp)
        Box {
            content()
        }
    }
}

@Composable
fun SettingItemSwitch(
    checked: Boolean,
    onCheckedChanged: (Boolean) -> Unit,
) {
    Box(modifier = Modifier.padding(vertical = 12.dp)) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChanged,
            colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colors.primary)
        )
    }
}

class TransformSettingState {
    var verticalDrag by mutableStateOf(true)

    var transformEnter by mutableStateOf(true)

    var transformExit by mutableStateOf(true)

    var animationDuration by mutableStateOf(400)

    fun reset() {
        verticalDrag = true
        transformEnter = true
        transformExit = true
        animationDuration = 400
    }

    companion object {
        val Saver: Saver<TransformSettingState, *> = listSaver(
            save = {
                listOf<Any>(
                    it.verticalDrag,
                    it.transformEnter,
                    it.transformExit,
                    it.animationDuration,
                )
            },
            restore = {
                val state = TransformSettingState()
                state.verticalDrag = it[0] as Boolean
                state.transformEnter = it[1] as Boolean
                state.transformExit = it[2] as Boolean
                state.animationDuration = it[3] as Int
                state
            }
        )
    }
}

@Composable
fun rememberSettingState(): TransformSettingState {
    return rememberSaveable(saver = TransformSettingState.Saver) {
        TransformSettingState()
    }
}

@Composable
fun SettingPanel(state: TransformSettingState, onClose: () -> Unit) {
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        SettingPanelBanner(onClose)
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8F)
                .weight(1F)
                .verticalScroll(rememberScrollState())
        ) {
            SettingItem(label = "Vertical Drag") {
                SettingItemSwitch(checked = state.verticalDrag, onCheckedChanged = {
                    state.verticalDrag = it
                })
            }
            Spacer(modifier = Modifier.height(12.dp))
            SettingItem(label = "Transform Enter") {
                SettingItemSwitch(checked = state.transformEnter, onCheckedChanged = {
                    state.transformEnter = it
                })
            }
            Spacer(modifier = Modifier.height(12.dp))
            SettingItem(label = "Transform Exit") {
                SettingItemSwitch(checked = state.transformExit, onCheckedChanged = {
                    state.transformExit = it
                })
            }
            Spacer(modifier = Modifier.height(12.dp))
            SettingItem(label = "Animation Duration") {
                Slider(
                    modifier = Modifier
                        .height(48.dp)
                        .width(120.dp),
                    value = state.animationDuration.toFloat(),
                    valueRange = 0F..2000F,
                    onValueChangeFinished = {
                        Toast.makeText(context, "${state.animationDuration}", Toast.LENGTH_SHORT)
                            .show()
                    },
                    onValueChange = {
                        state.animationDuration = it.toInt()
                    },
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                state.reset()
                Toast.makeText(context, "👌 Reset", Toast.LENGTH_SHORT)
                    .show()
            }) {
                Text(text = "Reset")
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SettingPanelBanner(onClose: () -> Unit) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
    ) {
        val (closeBtn, bannerTxt) = createRefs()
        Box(modifier = Modifier
            .clip(CircleShape)
            .constrainAs(closeBtn) {
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
            .clickable {
                onClose()
            }
            .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colors.onSurface.copy(0.32F)
            )
        }
        Text(text = "Settings", modifier = Modifier.constrainAs(bannerTxt) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
        })
    }
}

@Composable
fun SettingSurface(
    settingState: TransformSettingState,
) {
    var visible by rememberSaveable { mutableStateOf(false) }
    if (visible) BackHandler {
        visible = false
    }
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val (btn) = createRefs()
        val guideFromBottom = createGuidelineFromBottom(0.28F)
        Box(modifier = Modifier
            .constrainAs(btn) {
                end.linkTo(parent.end, 12.dp)
                bottom.linkTo(guideFromBottom)
            }
            .size(54.dp)
            .shadow(12.dp, shape = CircleShape)
            .clip(CircleShape)
            .background(Color.White)
            .clickable {
                visible = true
            },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colors.onSurface.copy(0.32F)
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        AnimatedVisibility(
            visible = visible,
            modifier = Modifier.fillMaxWidth(),
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.onSurface)
                    .pointerInput(Unit) {
                        detectTapGestures { }
                    }
            )
        }
        AnimatedVisibility(
            visible = visible,
            modifier = Modifier.fillMaxWidth(),
            enter = slideInVertically { it },
            exit = slideOutVertically { it },
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.6F)
                        .background(Color.White)
                        .padding(horizontal = 12.dp)
                ) {
                    SettingPanel(settingState) {
                        visible = false
                    }
                }
            }
        }
    }
}

@Composable
fun ScaleGrid(
    onTap: () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val itemScale = remember { Animatable(1F) }
    Box(
        modifier = Modifier
            .fillMaxSize(itemScale.value)
            .pointerInput(Unit) {
                forEachGesture {
                    awaitPointerEventScope {
                        awaitFirstDown()
                        // 这里开始
                        scope.launch {
                            itemScale.animateTo(0.84F)
                        }
                        var move = false
                        do {
                            val event = awaitPointerEvent()
                            if (!move) {
                                move = event.type == PointerEventType.Move
                                break
                            }
                        } while (event.changes.any { it.pressed })
                        // 这里结束
                        scope.launch {
                            itemScale.animateTo(1F)
                        }
                        if (move) {
                            return@awaitPointerEventScope
                        }
                        onTap()
                    }
                }
            }
    ) {
        content()
    }
}
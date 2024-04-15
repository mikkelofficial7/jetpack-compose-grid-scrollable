package com.example.jetpackscrollable

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.jetpackscrollable.model.PhotoResponse
import com.example.jetpackscrollable.model.Photos
import com.example.jetpackscrollable.state.CommonState
import com.example.jetpackscrollable.state.Status
import com.example.jetpackscrollable.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val mainVM by lazy { MainViewModel(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenStarted {
            mainVM.getAllPhotos()
            mainVM.getCurrentState().collect {
                setContent {
                    AppState(it, mainVM)
                }
            }
        }
    }
}

@Composable
fun AppState(state: CommonState<PhotoResponse>, mainVM: MainViewModel) {
    val totalListPhoto by rememberSaveable { mutableStateOf(arrayListOf<Photos>()) }
    val gridState = rememberLazyGridState()
    val columnState = rememberLazyListState()

    when (state.status) {
        Status.LOADING -> {
            if (mainVM.getCurrentPage() > 1 && totalListPhoto.isNotEmpty()) {
                LoadingSecondLoad(mainVM, totalListPhoto, gridState, columnState)
            } else {
                Loading()
            }
        }
        Status.FAILED -> {
            if (mainVM.getCurrentPage() > 1) {
                FailedLoadWithToast(mainVM, totalListPhoto, gridState, columnState)
            } else {
                FailedPage(mainVM)
            }
        }
        Status.SUCCESS -> {
            state.data?.map { photo -> totalListPhoto.add(photo) }
            SuccessPage(mainVM, totalListPhoto, gridState, columnState)
        }
    }
}

@Composable
fun Loading() {
    Box (
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Card(
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .height(100.dp)
                .width(100.dp),
            backgroundColor = colorResource(R.color.black_30)
        ) {
            CircularProgressIndicator(strokeWidth = 3.dp, modifier = Modifier.padding(18.dp), color = colorResource(R.color.white))
        }
    }
}

@Composable
fun LoadingSecondLoad(mainVM: MainViewModel,
                      totalListPhoto: List<Photos>,
                      gridState: LazyGridState,
                      columnState: LazyListState) {

    Box{
        SuccessPage(mainVM, totalListPhoto, gridState, columnState)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Loading()
        }
    }
}

@Composable
fun FailedPage(mainVM: MainViewModel) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(id = R.string.unable_to_load_more), fontSize = 20.sp)
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(R.color.black),
                    contentColor = colorResource(R.color.white)
                ),
                onClick = {
                    mainVM.getAllPhotos(mainVM.getCurrentPage())
                }) {
                    Text(stringResource(id = R.string.refresh_data))
                }
        }
    }
}

@Composable
fun FailedLoadWithToast(mainVM: MainViewModel,
                        totalListPhoto: List<Photos>,
                        gridState: LazyGridState,
                        columnState: LazyListState
) {
    SuccessPage(mainVM, totalListPhoto, gridState, columnState)
    showToast(LocalContext.current, stringResource(id = R.string.unable_to_load_more))
}

@Composable
fun SuccessPage(mainVM: MainViewModel,
                totalListPhoto: List<Photos>,
                gridState: LazyGridState,
                columnState: LazyListState) {
    val density = LocalDensity.current
    var columnHeightDp by remember { mutableStateOf(0.dp) }

    LazyColumn(state = columnState){
        item {
            Box(modifier = Modifier.heightIn(0.dp, LocalConfiguration.current.screenHeightDp.dp - columnHeightDp)){
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    state = gridState
                ) {
                    items(totalListPhoto.size) { index ->
                        Box(
                            modifier = Modifier
                                .padding(5.dp)
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ){
                            ImageLoaderFromURL(index, mainVM)
                        }
                    }
                }
            }
        }
        item {
            LoadMoreButton(gridState, mainVM, onButtonHeightGet = {
                with(density) {
                    columnHeightDp = it.toDp()
                }
            })
        }
    }
}

@Composable
private fun ImageLoaderFromURL(itemIndex: Int, mainVM: MainViewModel) {
    Image(
        bitmap = mainVM.getListOfPhotoBitmap()[itemIndex].asImageBitmap(),
        contentDescription = stringResource(id = R.string.image_description)
    )
}

@Composable
fun LoadMoreButton(gridState: LazyGridState, mainVM: MainViewModel, onButtonHeightGet: (Int) -> Unit) {
    if(!gridState.isScrollingUp()) {
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp)
                .onGloballyPositioned { coordinates ->
                    onButtonHeightGet(coordinates.size.height)
                },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = colorResource(R.color.black),
                contentColor = colorResource(R.color.white)
            ),
            onClick = {
                mainVM.getAllPhotos(mainVM.getCurrentPage())
            }) {
            Text(stringResource(id = R.string.load_more))
        }
    }
}
@Composable
fun LazyGridState.isScrollingUp(): Boolean {
    val offset by remember(this) { mutableStateOf(firstVisibleItemScrollOffset) }
    return remember(this) { derivedStateOf { (firstVisibleItemScrollOffset - offset) < 0 } }.value
}

private fun showToast(context: Context, message: String){
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}
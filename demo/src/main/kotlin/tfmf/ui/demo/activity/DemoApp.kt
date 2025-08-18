package tfmf.ui.demo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fluent.compose.AppBar
import fluent.compose.Fluent2AppBarDefaults
import fluent.compose.Icon
import fluent.compose.IconButton
import fluent.compose.ListItem
import fluent.compose.demo.model.Demo
import fluent.compose.demo.ui.components.Component
import fluent.compose.theme.OneDriveTheme
import kotlinx.coroutines.launch

/**
 * Composable function that returns the Demo App component.
 */
@Composable
fun DemoApp(onListItemClick: () -> Unit) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val currentDemo = remember { mutableStateOf(Demo.Alpha) }
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = OneDriveTheme.colors.neutralBackground2,
                drawerContentColor = OneDriveTheme.colors.neutralForeground1,
            ) {
                Text(
                    text = "Fluent 2 Demos",
                    style = OneDriveTheme.typography.title1,
                    color = OneDriveTheme.colors.neutralForeground1,
                    modifier = Modifier.padding(32.dp, 16.dp),
                )
                DemoList(
                    onSampleScreenSelected = { navigationItem ->
                        if (navigationItem == Demo.Fluent1) {
                            onListItemClick()
                        } else {
                            currentDemo.value = navigationItem
                            coroutineScope.launch { drawerState.close() }
                        }
                    },
                )
            }

        },
        content = {
            DemoScreenBuilder(
                navigationIcon = {
                    IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                        Icon(
                            id = R.drawable.ic_fluent_navigation_24_filled,
                            contentDescription = "Menu",
                        )
                    }
                },
                demo = currentDemo.value,
            )
        },
    )
}

/**
 * Composable function that displays a list of [Demo] items in a [Column] using [ListItem] composable.
 *
 * @param onSampleScreenSelected a lambda function that takes a [Demo] parameter and returns [Unit].
 */
@Composable
fun DemoList(
    onSampleScreenSelected: (Demo) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        for (screen in Demo.values()) {
            if (screen.useFluent2) {
                ListItem(
                    text = {
                        Text(
                            text = screen.title,
                            color = OneDriveTheme.colors.neutralForeground1,
                            modifier = Modifier.padding(16.dp),
                        )
                    },
                    modifier = Modifier
                        .clickable {
                            onSampleScreenSelected(screen)
                        }
                        .fillMaxWidth(),
                    background = OneDriveTheme.colors.neutralBackground2,
                )
            }
        }
    }
}

/**
 * Composable function that builds a sample screen component with an app bar and a content component.
 *
 * @param navigationIcon optional composable function for the navigation icon in the app bar.
 * @param demo the sample screen to be displayed.
 */
@Composable
fun DemoScreenBuilder(
    navigationIcon: @Composable (RowScope.() -> Unit)? = null,
    demo: Demo
) {
    Column(modifier = Modifier.fillMaxSize()) {
        AppBar(
            title = {
                Text(
                    text = demo.title,
                    color = OneDriveTheme.colors.neutralForeground1,
                )
            },
            colors = Fluent2AppBarDefaults.neutralColors(),
            contentPadding = WindowInsets.statusBars.asPaddingValues(),
            navigationIcon = navigationIcon,
        )
        Component(
            demo = demo,
            backgroundColor = OneDriveTheme.colors.neutralBackground1
        )
    }
}
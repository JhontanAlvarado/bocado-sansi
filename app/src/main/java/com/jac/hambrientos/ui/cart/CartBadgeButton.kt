package com.jac.hambrientos.ui.cart

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jac.hambrientos.ui.theme.PrimaryOrange

@Composable
fun CartBadgeButton(
    itemCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = PrimaryOrange,
) {
    IconButton(onClick = onClick, modifier = modifier) {
        BadgedBox(
            badge = {
                if (itemCount > 0) {
                    Badge(containerColor = PrimaryOrange, contentColor = Color.White) {
                        Text(if (itemCount > 99) "99+" else itemCount.toString())
                    }
                }
            },
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Abrir carrito",
                tint = iconTint,
                modifier = Modifier.size(25.dp),
            )
        }
    }
}

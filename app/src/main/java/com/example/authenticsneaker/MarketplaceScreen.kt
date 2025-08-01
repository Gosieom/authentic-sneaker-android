package com.example.authenticsneaker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import coil.compose.AsyncImage
import com.example.authenticsneaker.data.Sneaker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToProfile: () -> Unit,
    sneakerViewModel: SneakerViewModel = viewModel(factory = ViewModelFactory(LocalContext.current.applicationContext as android.app.Application))
) {
    val state by sneakerViewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showSearchBar by remember { mutableStateOf(false) }
    var showFilters by remember { mutableStateOf(true) }

    var showSizeDialog by remember { mutableStateOf(false) }
    var selectedSneakerForCart by remember { mutableStateOf<Sneaker?>(null) }
    var showSuccessMessage by remember { mutableStateOf<String?>(null) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E1E1E),
                        Color(0xFF2D2D2D)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            TopAppBar(
                title = { 
                    Text(
                        text = "AuthenticSneaker",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                },
                actions = {
                    // Search button
                    IconButton(onClick = { showSearchBar = !showSearchBar }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White
                        )
                    }
                    
                    // Filter toggle button
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Toggle Filters",
                            tint = Color.White
                        )
                    }
                    
                    // Favorites button
                    IconButton(onClick = onNavigateToFavorites) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favorites",
                            tint = Color.White
                        )
                    }
                    
                    // Cart button
                    IconButton(onClick = onNavigateToCart) {
                        Badge(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        ) {
                            Text(
                                text = sneakerViewModel.getCartItemCount().toString(),
                                fontSize = 12.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Cart",
                            tint = Color.White
                        )
                    }
                    
                    // Profile button
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
            
            // Search Bar
            if (showSearchBar) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { 
                        searchQuery = it
                        sneakerViewModel.searchSneakers(it)
                    },
                    placeholder = { Text("Search sneakers...", color = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00D4FF),
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.Gray
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { 
                                searchQuery = ""
                                sneakerViewModel.clearFilters()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = Color.Gray
                                )
                            }
                        }
                    }
                )
            }
            
            // Brand Filter
            if (showFilters) {
                BrandFilterSection(
                    selectedBrand = state.selectedBrand,
                    onBrandSelected = { brand ->
                        sneakerViewModel.filterByBrand(brand)
                    },
                    onClearFilter = {
                        sneakerViewModel.clearFilters()
                    }
                )
            }
            
            // Content
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF00D4FF))
                }
            } else if (state.sneakers.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = "No sneakers",
                            tint = Color.Gray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No sneakers found",
                            color = Color.Gray,
                            fontSize = 18.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.sneakers) { sneaker ->
                        SneakerCard(
                            sneaker = sneaker,
                            isFavorited = sneakerViewModel.isFavorited(sneaker.id),
                            isInCart = sneakerViewModel.isInCart(sneaker.id),
                            onSneakerClick = { onNavigateToDetail(sneaker.id) },
                            onFavoriteClick = {
                                if (sneakerViewModel.isFavorited(sneaker.id)) {
                                    sneakerViewModel.removeFromFavorites(sneaker.id)
                                    showSuccessMessage = "Removed from favorites"
                                } else {
                                    sneakerViewModel.addToFavorites(sneaker)
                                    showSuccessMessage = "Added to favorites"
                                }
                            },
                            onCartClick = {
                                selectedSneakerForCart = sneaker
                                showSizeDialog = true
                            }
                        )
                    }
                }
            }
        }
        
        // Error display
        state.error?.let { error ->
            LaunchedEffect(error) {
                kotlinx.coroutines.delay(3000)
                sneakerViewModel.clearError()
            }
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE57373)
                    )
                ) {
                    Text(
                        text = error,
                        color = Color.White,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 14.sp
                    )
                }
            }
        }
        

        
        // Size selection dialog for cart
        if (showSizeDialog && selectedSneakerForCart != null) {
            SizeSelectionDialog(
                sneaker = selectedSneakerForCart!!,
                onSizeSelected = { size ->
                    sneakerViewModel.addToCart(selectedSneakerForCart!!, size)
                    showSizeDialog = false
                    selectedSneakerForCart = null
                    showSuccessMessage = "Added to cart"
                },
                onDismiss = {
                    showSizeDialog = false
                    selectedSneakerForCart = null
                }
            )
        }
        
        // Success message
        showSuccessMessage?.let { message ->
            LaunchedEffect(message) {
                kotlinx.coroutines.delay(2000)
                showSuccessMessage = null
            }
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    Text(
                        text = message,
                        color = Color.White,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 14.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun BrandFilterSection(
    selectedBrand: String,
    onBrandSelected: (String) -> Unit,
    onClearFilter: () -> Unit
) {
    val brands = listOf("Nike", "Adidas", "Jordan", "Puma", "Reebok", "New Balance")
    
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Filter by Brand",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            
            TextButton(onClick = onClearFilter) {
                Text(
                    text = "Clear",
                    color = Color(0xFF00D4FF)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(brands) { brand ->
                FilterChip(
                    onClick = { 
                        if (selectedBrand == brand) {
                            onClearFilter()
                        } else {
                            onBrandSelected(brand)
                        }
                    },
                    label = { Text(brand) },
                    selected = selectedBrand == brand,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF00D4FF),
                        selectedLabelColor = Color.White,
                        containerColor = Color.Gray.copy(alpha = 0.3f),
                        labelColor = Color.White
                    )
                )
            }
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun SizeSelectionDialog(
    sneaker: Sneaker,
    onSizeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sizes = listOf("US 7", "US 7.5", "US 8", "US 8.5", "US 9", "US 9.5", "US 10", "US 10.5", "US 11", "US 11.5", "US 12")
    var selectedSize by remember { mutableStateOf("US 10") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Select Size",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = sneaker.name,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Choose your size:",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Column {
                    sizes.chunked(3).forEach { rowSizes ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowSizes.forEach { size ->
                                FilterChip(
                                    onClick = { selectedSize = size },
                                    label = { Text(size) },
                                    selected = selectedSize == size,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF00D4FF),
                                        selectedLabelColor = Color.White,
                                        containerColor = Color.Gray.copy(alpha = 0.3f),
                                        labelColor = Color.White
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            // Fill remaining space if row has less than 3 items
                            repeat(3 - rowSizes.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSizeSelected(selectedSize) }
            ) {
                Text(
                    text = "Add to Cart",
                    color = Color(0xFF00D4FF)
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = Color.Gray
                )
            }
        },
        containerColor = Color(0xFF2A2A2A),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun SneakerCard(
    sneaker: Sneaker,
    isFavorited: Boolean,
    isInCart: Boolean,
    onSneakerClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onCartClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSneakerClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // Image section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                AsyncImage(
                    model = sneaker.imageUrl.ifEmpty { "https://via.placeholder.com/300x200?text=${sneaker.name}" },
                    contentDescription = sneaker.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Favorite button
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorited) Color.Red else Color.White
                    )
                }
                
                // Cart button
                IconButton(
                    onClick = onCartClick,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = if (isInCart) Icons.Default.ShoppingCartCheckout else Icons.Default.ShoppingCart,
                        contentDescription = if (isInCart) "In Cart" else "Add to Cart",
                        tint = if (isInCart) Color(0xFF00D4FF) else Color.White
                    )
                }
                
                // Sale badge
                if (sneaker.isOnSale()) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Red
                        )
                    ) {
                        Text(
                            text = "-${sneaker.getDiscountPercentage()}%",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
            
            // Content section
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = sneaker.brand,
                    color = Color(0xFF00D4FF),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = sneaker.name,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        if (sneaker.isOnSale()) {
                            Text(
                                text = sneaker.getFormattedOriginalPrice(),
                                color = Color.Gray,
                                fontSize = 14.sp,
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                            )
                        }
                        Text(
                            text = sneaker.getFormattedPrice(),
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%.1f", sneaker.rating),
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = sneaker.condition,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
} 
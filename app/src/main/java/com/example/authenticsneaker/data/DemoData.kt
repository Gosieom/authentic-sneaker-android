package com.example.authenticsneaker.data

object DemoData {
    val demoSneakers = listOf(
        // Nike
        Sneaker(
            id = "1",
            name = "Nike Air Max 270",
            brand = "Nike",
            model = "Air Max 270",
            price = 150.0,
            originalPrice = 150.0,
            description = "The Nike Air Max 270 delivers unrivaled, all-day comfort with the tallest Air unit yet. The shoe's design draws inspiration from Air Max icons, showcasing Nike's greatest innovation with its large window and fresh array of colors.",
            imageUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=300&fit=crop",
            category = "Lifestyle",
            size = "US 10",
            color = "White/Black",
            condition = "New",
            releaseDate = "2023-02-15",
            inStock = true,
            stockQuantity = 25,
            rating = 4.7,
            reviewCount = 1892,
            tags = listOf("lifestyle", "air-max", "comfort", "classic")
        ),
        
        // Adidas
        Sneaker(
            id = "2",
            name = "Adidas Ultraboost 22",
            brand = "Adidas",
            model = "Ultraboost",
            price = 190.0,
            originalPrice = 220.0,
            description = "The Adidas Ultraboost 22 features responsive Boost midsole and Primeknit upper for ultimate comfort and performance during runs. The Continental™ Rubber outsole provides excellent grip in all conditions.",
            imageUrl = "https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=400&h=300&fit=crop",
            category = "Running",
            size = "US 11",
            color = "Core Black",
            condition = "New",
            releaseDate = "2023-03-20",
            inStock = true,
            stockQuantity = 18,
            rating = 4.8,
            reviewCount = 1247,
            tags = listOf("running", "boost", "performance", "comfort")
        ),
        
        // Jordan
        Sneaker(
            id = "3",
            name = "Air Jordan 1 Retro High OG",
            brand = "Jordan",
            model = "AJ1",
            price = 170.0,
            originalPrice = 200.0,
            description = "The Air Jordan 1 Retro High OG is a classic basketball shoe that has become a cultural icon. Features premium leather upper and Air-Sole unit for lightweight cushioning.",
            imageUrl = "https://images.unsplash.com/photo-1556906781-9a412961c28c?w=400&h=300&fit=crop",
            category = "Basketball",
            size = "US 9.5",
            color = "Chicago",
            condition = "New",
            releaseDate = "2023-01-10",
            inStock = true,
            stockQuantity = 12,
            rating = 4.9,
            reviewCount = 2341,
            tags = listOf("basketball", "retro", "classic", "limited")
        ),
        
        // Puma
        Sneaker(
            id = "4",
            name = "Puma RS-X Reinvention",
            brand = "Puma",
            model = "RS-X",
            price = 110.0,
            originalPrice = 130.0,
            description = "The Puma RS-X Reinvention features a bold, retro-inspired design with enhanced cushioning for maximum comfort and style. The chunky silhouette is perfect for streetwear enthusiasts.",
            imageUrl = "https://images.unsplash.com/photo-1600269452121-4f2416e55c28?w=400&h=300&fit=crop",
            category = "Lifestyle",
            size = "US 10",
            color = "Blue/White",
            condition = "New",
            releaseDate = "2023-03-10",
            inStock = true,
            stockQuantity = 22,
            rating = 4.4,
            reviewCount = 567,
            tags = listOf("lifestyle", "retro", "bold", "comfort")
        ),
        
        // Reebok
        Sneaker(
            id = "5",
            name = "Reebok Classic Leather",
            brand = "Reebok",
            model = "Classic",
            price = 75.0,
            originalPrice = 75.0,
            description = "The Reebok Classic Leather is a timeless sneaker with premium leather upper and comfortable fit for everyday wear. A true classic that never goes out of style.",
            imageUrl = "https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=400&h=300&fit=crop",
            category = "Lifestyle",
            size = "US 10.5",
            color = "White",
            condition = "New",
            releaseDate = "2023-01-20",
            inStock = true,
            stockQuantity = 35,
            rating = 4.3,
            reviewCount = 892,
            tags = listOf("lifestyle", "classic", "leather", "versatile")
        ),
        
        // New Balance
        Sneaker(
            id = "6",
            name = "New Balance 990v5",
            brand = "New Balance",
            model = "990",
            price = 185.0,
            originalPrice = 185.0,
            description = "The New Balance 990v5 is a premium lifestyle sneaker with ENCAP midsole technology for superior comfort and durability. Made in the USA with premium materials.",
            imageUrl = "https://images.unsplash.com/photo-1552346154-21d32810aba3?w=400&h=300&fit=crop",
            category = "Lifestyle",
            size = "US 11",
            color = "Grey",
            condition = "New",
            releaseDate = "2023-01-15",
            inStock = true,
            stockQuantity = 15,
            rating = 4.9,
            reviewCount = 734,
            tags = listOf("lifestyle", "premium", "comfort", "made-in-usa")
        ),
        
        // Additional Nike product
        Sneaker(
            id = "7",
            name = "Nike Dunk Low Retro",
            brand = "Nike",
            model = "Dunk Low",
            price = 110.0,
            originalPrice = 110.0,
            description = "The Nike Dunk Low Retro is a versatile skateboarding shoe that has become a streetwear staple. Features durable canvas and suede construction with classic styling.",
            imageUrl = "https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa?w=400&h=300&fit=crop",
            category = "Skateboarding",
            size = "US 9.5",
            color = "Panda",
            condition = "New",
            releaseDate = "2023-02-28",
            inStock = true,
            stockQuantity = 20,
            rating = 4.6,
            reviewCount = 1456,
            tags = listOf("skateboarding", "streetwear", "versatile", "panda")
        ),
        
        // Additional Adidas product
        Sneaker(
            id = "8",
            name = "Adidas Stan Smith",
            brand = "Adidas",
            model = "Stan Smith",
            price = 85.0,
            originalPrice = 85.0,
            description = "The Adidas Stan Smith is a timeless tennis shoe that has transcended the court to become a fashion icon. Features premium leather upper and classic green heel tab.",
            imageUrl = "https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=400&h=300&fit=crop",
            category = "Tennis",
            size = "US 10",
            color = "White/Green",
            condition = "New",
            releaseDate = "2023-01-05",
            inStock = true,
            stockQuantity = 30,
            rating = 4.5,
            reviewCount = 2103,
            tags = listOf("tennis", "classic", "leather", "versatile")
        )
    )
} 
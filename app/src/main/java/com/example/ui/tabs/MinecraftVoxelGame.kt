package com.example.ui.tabs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.FlameNeonOrange
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class VoxelBlockType(
    val id: String,
    val displayName: String,
    val emoji: String,
    val isSolid: Boolean = true,
    val harvestTimeMs: Long = 300,
    val primaryColor: Color,
    val secondaryColor: Color = primaryColor
) {
    AIR("air", "Hava", "💨", isSolid = false, harvestTimeMs = 0, primaryColor = Color.Transparent),
    GRASS("grass", "Çimen Blok", "🌱", primaryColor = Color(0xFF4CAF50), secondaryColor = Color(0xFF795548)),
    DIRT("dirt", "Toprak", "🟫", primaryColor = Color(0xFF8D6E63), secondaryColor = Color(0xFF5D4037)),
    STONE("stone", "Taş", "🪨", harvestTimeMs = 600, primaryColor = Color(0xFF78909C), secondaryColor = Color(0xFF546E7A)),
    COBBLESTONE("cobble", "Kırıktaş", "🧱", harvestTimeMs = 500, primaryColor = Color(0xFF90A4AE), secondaryColor = Color(0xFF607D8B)),
    WOOD_LOG("wood", "Meşe Kütüğü", "🪵", harvestTimeMs = 450, primaryColor = Color(0xFF6D4C41), secondaryColor = Color(0xFF4E342E)),
    WOOD_PLANKS("planks", "Meşe Tahtası", "🪚", harvestTimeMs = 350, primaryColor = Color(0xFFBCAAA4), secondaryColor = Color(0xFF8D6E63)),
    LEAVES("leaves", "Meşe Yaprakları", "🍃", harvestTimeMs = 150, primaryColor = Color(0xFF2E7D32), secondaryColor = Color(0xFF1B5E20)),
    COAL_ORE("coal_ore", "Kömür Cevheri", "⬛", harvestTimeMs = 650, primaryColor = Color(0xFF546E7A), secondaryColor = Color(0xFF212121)),
    IRON_ORE("iron_ore", "Demir Cevheri", "🟤", harvestTimeMs = 800, primaryColor = Color(0xFF78909C), secondaryColor = Color(0xFFD7CCC8)),
    GOLD_ORE("gold_ore", "Altın Cevheri", "🟡", harvestTimeMs = 1000, primaryColor = Color(0xFF607D8B), secondaryColor = Color(0xFFFFD54F)),
    DIAMOND_ORE("diamond_ore", "Elmas Cevheri", "💎", harvestTimeMs = 1200, primaryColor = Color(0xFF455A64), secondaryColor = Color(0xFF00E5FF)),
    TNT("tnt", "TNT Patlayıcı", "🧨", harvestTimeMs = 200, primaryColor = Color(0xFFD32F2F), secondaryColor = Color(0xFFFFFFFF)),
    TORCH("torch", "Meşale", "🕯️", isSolid = false, harvestTimeMs = 100, primaryColor = Color(0xFFFFC107), secondaryColor = Color(0xFFFF9800)),
    CRAFTING_TABLE("craft_table", "Çalışma Masası", "🧰", harvestTimeMs = 400, primaryColor = Color(0xFFA1887F), secondaryColor = Color(0xFF5D4037)),
    BRICK("brick", "Tuğla Blok", "🧱", harvestTimeMs = 600, primaryColor = Color(0xFFB71C1C), secondaryColor = Color(0xFFD32F2F)),
    GLASS("glass", "Cam Blok", "🪟", harvestTimeMs = 200, primaryColor = Color(0x9980DEEA), secondaryColor = Color(0xCCB2EBF2)),
    WATER("water", "Su", "💧", isSolid = false, harvestTimeMs = 0, primaryColor = Color(0xAA1976D2), secondaryColor = Color(0xCC0288D1)),
    BEDROCK("bedrock", "Katman Kayası", "⬛", harvestTimeMs = 999999, primaryColor = Color(0xFF212121), secondaryColor = Color(0xFF111111))
}

enum class MinecraftTool(val displayName: String, val speedMultiplier: Float, val icon: String) {
    HAND("El", 1.0f, "✋"),
    WOOD_PICKAXE("Tahta Kazma", 1.8f, "🪵"),
    STONE_PICKAXE("Taş Kazma", 2.6f, "🪨"),
    IRON_PICKAXE("Demir Kazma", 3.8f, "⚔️"),
    DIAMOND_PICKAXE("Elmas Kazma", 6.0f, "💎")
}

data class CraftingRecipe(
    val result: VoxelBlockType,
    val resultCount: Int,
    val resultTool: MinecraftTool? = null,
    val title: String,
    val ingredientsDescription: String,
    val requiredItems: Map<VoxelBlockType, Int>,
    val requiresCraftingTable: Boolean = false
)

data class ExplosionParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var color: Color,
    var alpha: Float = 1f,
    var size: Float = 6f
)

@Composable
fun MinecraftVoxelGameView() {
    val worldCols = 22
    val worldRows = 15

    // World Grid
    val worldGrid = remember {
        mutableStateListOf<VoxelBlockType>().apply {
            addAll(generateProceduralWorld(worldCols, worldRows))
        }
    }

    // Inventory counts
    val inventory = remember {
        mutableStateMapOf<VoxelBlockType, Int>().apply {
            put(VoxelBlockType.DIRT, 8)
            put(VoxelBlockType.WOOD_PLANKS, 6)
            put(VoxelBlockType.TORCH, 4)
            put(VoxelBlockType.TNT, 2)
            put(VoxelBlockType.COBBLESTONE, 4)
        }
    }

    // Player State
    var playerCol by remember { mutableFloatStateOf(10f) }
    var playerRow by remember { mutableFloatStateOf(4f) }
    var playerVy by remember { mutableFloatStateOf(0f) }
    var isGrounded by remember { mutableStateOf(false) }
    var facingRight by remember { mutableStateOf(true) }
    var isWalking by remember { mutableStateOf(false) }
    var isMiningAnim by remember { mutableStateOf(false) }

    // Game stats
    var health by remember { mutableIntStateOf(5) }
    var scoreMined by remember { mutableIntStateOf(0) }
    var diamondCount by remember { mutableIntStateOf(0) }
    var isCreativeMode by remember { mutableStateOf(false) }
    var currentTool by remember { mutableStateOf(MinecraftTool.HAND) }
    var selectedBlockToPlace by remember { mutableStateOf(VoxelBlockType.DIRT) }

    // Day & Night Cycle (0f to 360f)
    var dayTimeDegrees by remember { mutableFloatStateOf(45f) }

    // Dialogs & Overlays
    var showCraftingDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("⛏️ Hoş geldin! Blokları kır, inşa et, maden topla.") }

    // Active Explosions
    val particles = remember { mutableStateListOf<ExplosionParticle>() }
    var activeTntTimer by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var tntTicksLeft by remember { mutableIntStateOf(0) }

    // Helper function to check solid block at col, row
    fun getBlockAt(c: Int, r: Int): VoxelBlockType {
        if (c !in 0 until worldCols || r !in 0 until worldRows) return VoxelBlockType.BEDROCK
        return worldGrid[r * worldCols + c]
    }

    fun setBlockAt(c: Int, r: Int, type: VoxelBlockType) {
        if (c in 0 until worldCols && r in 0 until worldRows) {
            worldGrid[r * worldCols + c] = type
        }
    }

    // Break Block Logic
    fun breakBlock(c: Int, r: Int) {
        if (c !in 0 until worldCols || r !in 0 until worldRows) return
        val block = getBlockAt(c, r)
        if (block == VoxelBlockType.AIR || block == VoxelBlockType.BEDROCK) return

        if (block == VoxelBlockType.TNT) {
            // Ignite TNT
            activeTntTimer = Pair(c, r)
            tntTicksLeft = 3
            statusMessage = "🧨 TNT AKTİF! 3 SANİYE İÇİNDE KAÇ!"
            return
        }

        setBlockAt(c, r, VoxelBlockType.AIR)
        scoreMined++

        // Add to inventory
        val dropType = when (block) {
            VoxelBlockType.STONE -> VoxelBlockType.COBBLESTONE
            VoxelBlockType.GRASS -> VoxelBlockType.DIRT
            VoxelBlockType.DIAMOND_ORE -> {
                diamondCount++
                statusMessage = "💎 ELMAS BULDUN! Tebrikler!"
                VoxelBlockType.DIAMOND_ORE
            }
            VoxelBlockType.COAL_ORE -> {
                statusMessage = "⬛ Kömür toplandı! Meşale yapabilirsin."
                VoxelBlockType.COAL_ORE
            }
            VoxelBlockType.IRON_ORE -> {
                statusMessage = "🟤 Demir cevheri bulundu!"
                VoxelBlockType.IRON_ORE
            }
            VoxelBlockType.GOLD_ORE -> {
                statusMessage = "🟡 Altın cevheri bulundu!"
                VoxelBlockType.GOLD_ORE
            }
            else -> block
        }

        val currentCount = inventory[dropType] ?: 0
        inventory[dropType] = currentCount + 1
        isMiningAnim = true
    }

    // Place Block Logic
    fun placeBlock(c: Int, r: Int) {
        if (c !in 0 until worldCols || r !in 0 until worldRows) return
        val current = getBlockAt(c, r)
        if (current != VoxelBlockType.AIR && current != VoxelBlockType.WATER) return

        // Prevent placing inside player head/feet
        val pColInt = playerCol.toInt()
        val pRowInt = playerRow.toInt()
        if (c == pColInt && (r == pRowInt || r == pRowInt - 1)) return

        if (!isCreativeMode) {
            val count = inventory[selectedBlockToPlace] ?: 0
            if (count <= 0) {
                statusMessage = "⚠️ Envanterinde ${selectedBlockToPlace.displayName} kalmadı!"
                return
            }
            inventory[selectedBlockToPlace] = count - 1
        }

        setBlockAt(c, r, selectedBlockToPlace)
        statusMessage = "🧱 ${selectedBlockToPlace.displayName} yerleştirildi."
    }

    // Trigger TNT Explosion
    fun explodeTnt(c: Int, r: Int) {
        setBlockAt(c, r, VoxelBlockType.AIR)
        // Spawn particles
        for (i in 0..40) {
            val angle = Random.nextFloat() * 6.28f
            val speed = Random.nextFloat() * 4f + 1f
            particles.add(
                ExplosionParticle(
                    x = c.toFloat() + 0.5f,
                    y = r.toFloat() + 0.5f,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed,
                    color = if (Random.nextBoolean()) Color(0xFFFF5722) else if (Random.nextBoolean()) Color(0xFFFFD600) else Color(0xFFD50000)
                )
            )
        }

        // Destroy surrounding 3x3
        for (dc in -1..1) {
            for (dr in -1..1) {
                val nc = c + dc
                val nr = r + dr
                if (nc in 0 until worldCols && nr in 0 until worldRows) {
                    val b = getBlockAt(nc, nr)
                    if (b != VoxelBlockType.BEDROCK) {
                        setBlockAt(nc, nr, VoxelBlockType.AIR)
                    }
                }
            }
        }

        // Check if player took blast damage
        val distToPlayer = kotlin.math.hypot(playerCol - c, playerRow - r)
        if (distToPlayer < 2.5f) {
            health = (health - 2).coerceAtLeast(0)
            statusMessage = "💥 TNT PATLADI! Hasar aldın!"
        } else {
            statusMessage = "💥 BÜYÜK PATLAMA! Madenler açığa çıktı!"
        }
    }

    // Game loop for physics & particles & TNT timer
    LaunchedEffect(Unit) {
        while (true) {
            delay(30)
            dayTimeDegrees = (dayTimeDegrees + 0.15f) % 360f

            // Gravity & Vertical Physics
            val belowRow = (playerRow + 1.05f).toInt()
            val colInt = playerCol.toInt()
            val blockBelow = getBlockAt(colInt, belowRow)

            if (!blockBelow.isSolid) {
                playerVy = (playerVy + 0.04f).coerceAtMost(0.4f)
                playerRow = (playerRow + playerVy).coerceIn(1f, (worldRows - 2).toFloat())
                isGrounded = false
            } else {
                playerVy = 0f
                playerRow = (belowRow - 1).toFloat()
                isGrounded = true
            }

            // Update particles
            val iterator = particles.iterator()
            while (iterator.hasNext()) {
                val p = iterator.next()
                p.x += p.vx * 0.05f
                p.y += p.vy * 0.05f
                p.alpha -= 0.04f
                if (p.alpha <= 0f) {
                    iterator.remove()
                }
            }
        }
    }

    // TNT Countdown effect
    LaunchedEffect(activeTntTimer) {
        if (activeTntTimer != null) {
            while (tntTicksLeft > 0) {
                delay(800)
                tntTicksLeft--
            }
            val pos = activeTntTimer
            activeTntTimer = null
            if (pos != null) {
                explodeTnt(pos.first, pos.second)
            }
        }
    }

    // Available Crafting Recipes
    val craftingRecipes = listOf(
        CraftingRecipe(
            result = VoxelBlockType.WOOD_PLANKS,
            resultCount = 4,
            title = "Meşe Tahtası (x4)",
            ingredientsDescription = "1x Meşe Kütüğü",
            requiredItems = mapOf(VoxelBlockType.WOOD_LOG to 1)
        ),
        CraftingRecipe(
            result = VoxelBlockType.TORCH,
            resultCount = 4,
            title = "Meşale (x4)",
            ingredientsDescription = "1x Kömür Cevheri + 1x Tahta",
            requiredItems = mapOf(VoxelBlockType.COAL_ORE to 1, VoxelBlockType.WOOD_PLANKS to 1)
        ),
        CraftingRecipe(
            result = VoxelBlockType.CRAFTING_TABLE,
            resultCount = 1,
            title = "Çalışma Masası",
            ingredientsDescription = "4x Meşe Tahtası",
            requiredItems = mapOf(VoxelBlockType.WOOD_PLANKS to 4)
        ),
        CraftingRecipe(
            result = VoxelBlockType.TNT,
            resultCount = 1,
            title = "TNT Patlayıcı",
            ingredientsDescription = "2x Kömür + 3x Toprak",
            requiredItems = mapOf(VoxelBlockType.COAL_ORE to 2, VoxelBlockType.DIRT to 3)
        ),
        CraftingRecipe(
            result = VoxelBlockType.COBBLESTONE,
            resultCount = 0,
            resultTool = MinecraftTool.STONE_PICKAXE,
            title = "Taş Kazma (Hızlı Kazı)",
            ingredientsDescription = "3x Kırıktaş + 2x Tahta",
            requiredItems = mapOf(VoxelBlockType.COBBLESTONE to 3, VoxelBlockType.WOOD_PLANKS to 2)
        ),
        CraftingRecipe(
            result = VoxelBlockType.IRON_ORE,
            resultCount = 0,
            resultTool = MinecraftTool.IRON_PICKAXE,
            title = "Demir Kazma (Çok Hızlı)",
            ingredientsDescription = "3x Demir + 2x Tahta",
            requiredItems = mapOf(VoxelBlockType.IRON_ORE to 3, VoxelBlockType.WOOD_PLANKS to 2)
        ),
        CraftingRecipe(
            result = VoxelBlockType.DIAMOND_ORE,
            resultCount = 0,
            resultTool = MinecraftTool.DIAMOND_PICKAXE,
            title = "Elmas Kazma (Maksimum Güç)",
            ingredientsDescription = "3x Elmas + 2x Tahta",
            requiredItems = mapOf(VoxelBlockType.DIAMOND_ORE to 3, VoxelBlockType.WOOD_PLANKS to 2)
        ),
        CraftingRecipe(
            result = VoxelBlockType.BRICK,
            resultCount = 4,
            title = "Tuğla Blok (x4)",
            ingredientsDescription = "4x Kırıktaş",
            requiredItems = mapOf(VoxelBlockType.COBBLESTONE to 4)
        )
    )

    // Main Game Container
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TOP HUD BAR: Health, Diamond, Mined, Tool, Craft & Help Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface, RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFF4CAF50).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Health Hearts
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(5) { idx ->
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Can",
                        tint = if (idx < health) Color(0xFFFF1744) else Color.DarkGray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Stats
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "💎 $diamondCount",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )
                Text(
                    text = "⛏️ $scoreMined",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD54F)
                )
                Text(
                    text = "${currentTool.icon} ${currentTool.displayName}",
                    fontSize = 11.sp,
                    color = TextPrimary
                )
            }

            // Action buttons
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                // Crafting Table
                IconButton(
                    onClick = { showCraftingDialog = true },
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFF33691E), RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.Build, contentDescription = "Crafting", tint = Color.White, modifier = Modifier.size(16.dp))
                }

                // Help
                IconButton(
                    onClick = { showHelpDialog = true },
                    modifier = Modifier
                        .size(32.dp)
                        .background(DarkSurfaceVariant, RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.Info, contentDescription = "Yardım", tint = NeonCyan, modifier = Modifier.size(16.dp))
                }

                // Regenerate World
                IconButton(
                    onClick = {
                        worldGrid.clear()
                        worldGrid.addAll(generateProceduralWorld(worldCols, worldRows))
                        playerCol = 10f
                        playerRow = 4f
                        health = 5
                        statusMessage = "🌍 Yeni Minecraft Voxel Dünyası Oluşturuldu!"
                    },
                    modifier = Modifier
                        .size(32.dp)
                        .background(DarkSurfaceVariant, RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Yeni Harita", tint = Color(0xFFFF9800), modifier = Modifier.size(16.dp))
                }
            }
        }

        // Status banner
        Text(
            text = statusMessage,
            fontSize = 11.sp,
            color = Color(0xFFC8E6C9),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            textAlign = TextAlign.Center
        )

        // VOXEL CANVAS GAMEPLAY SCREEN
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, Color(0xFF4CAF50).copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        // Calculate grid cell clicked
                        val cellW = size.width / worldCols
                        val cellH = size.height / worldRows
                        val clickCol = (offset.x / cellW).toInt().coerceIn(0, worldCols - 1)
                        val clickRow = (offset.y / cellH).toInt().coerceIn(0, worldRows - 1)

                        val current = getBlockAt(clickCol, clickRow)
                        if (current == VoxelBlockType.AIR || current == VoxelBlockType.WATER) {
                            // Place block
                            placeBlock(clickCol, clickRow)
                        } else {
                            // Break block
                            breakBlock(clickCol, clickRow)
                        }
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasW = size.width
                val canvasH = size.height
                val cellW = canvasW / worldCols
                val cellH = canvasH / worldRows

                // Dynamic Sky gradient based on dayTimeDegrees
                val isNight = dayTimeDegrees in 180f..360f
                val skyColor = if (!isNight) {
                    Color(0xFF81D4FA) // Bright Day
                } else {
                    Color(0xFF0D1B2A) // Deep Night
                }

                drawRect(color = skyColor, size = Size(canvasW, canvasH))

                // Draw Sun / Moon
                val sunAngleRad = Math.toRadians(dayTimeDegrees.toDouble())
                val sunX = (canvasW / 2 + cos(sunAngleRad) * (canvasW * 0.45f)).toFloat()
                val sunY = (canvasH * 0.35f + sin(sunAngleRad) * (canvasH * 0.25f)).toFloat()

                if (!isNight) {
                    // Golden Sun
                    drawCircle(color = Color(0xFFFFEB3B), radius = 18f, center = Offset(sunX, sunY))
                    drawCircle(color = Color(0x55FFF59D), radius = 26f, center = Offset(sunX, sunY))
                } else {
                    // Glowing Moon
                    drawCircle(color = Color(0xFFECEFF1), radius = 14f, center = Offset(sunX, sunY))
                }

                // Clouds
                drawRoundRect(
                    color = Color.White.copy(alpha = if (isNight) 0.15f else 0.8f),
                    topLeft = Offset(canvasW * 0.15f, canvasH * 0.08f),
                    size = Size(canvasW * 0.25f, 16f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f)
                )
                drawRoundRect(
                    color = Color.White.copy(alpha = if (isNight) 0.15f else 0.8f),
                    topLeft = Offset(canvasW * 0.6f, canvasH * 0.14f),
                    size = Size(canvasW * 0.3f, 18f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f)
                )

                // Draw Blocks Grid
                for (r in 0 until worldRows) {
                    for (c in 0 until worldCols) {
                        val block = worldGrid[r * worldCols + c]
                        if (block != VoxelBlockType.AIR) {
                            val left = c * cellW
                            val top = r * cellH
                            drawVoxelBlock(
                                drawScope = this,
                                block = block,
                                left = left,
                                top = top,
                                width = cellW,
                                height = cellH,
                                isTntActive = activeTntTimer?.first == c && activeTntTimer?.second == r
                            )
                        }
                    }
                }

                // Draw Player (Steve)
                val pX = playerCol * cellW
                val pY = playerRow * cellH
                drawStevePlayer(
                    drawScope = this,
                    x = pX,
                    y = pY,
                    cellW = cellW,
                    cellH = cellH,
                    facingRight = facingRight,
                    isMining = isMiningAnim,
                    currentTool = currentTool
                )

                // Draw Explosion Particles
                for (p in particles) {
                    drawCircle(
                        color = p.color.copy(alpha = p.alpha),
                        radius = p.size,
                        center = Offset(p.x * cellW, p.y * cellH)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // INVENTORY HOTBAR (QUICK SELECT)
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface, RoundedCornerShape(12.dp))
                .padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val usableBlocks = listOf(
                VoxelBlockType.DIRT,
                VoxelBlockType.WOOD_PLANKS,
                VoxelBlockType.COBBLESTONE,
                VoxelBlockType.TORCH,
                VoxelBlockType.TNT,
                VoxelBlockType.CRAFTING_TABLE,
                VoxelBlockType.BRICK,
                VoxelBlockType.WOOD_LOG,
                VoxelBlockType.GLASS
            )

            items(usableBlocks) { block ->
                val count = inventory[block] ?: 0
                val isSelected = selectedBlockToPlace == block

                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(
                            if (isSelected) Color(0xFF2E7D32).copy(alpha = 0.4f) else DarkSurfaceVariant,
                            RoundedCornerShape(8.dp)
                        )
                        .border(
                            if (isSelected) 2.dp else 1.dp,
                            if (isSelected) Color(0xFF4CAF50) else Color.DarkGray,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { selectedBlockToPlace = block }
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = block.emoji, fontSize = 16.sp)
                        Text(
                            text = if (isCreativeMode) "∞" else "x$count",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (count > 0 || isCreativeMode) Color.White else Color.Gray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // TOUCH CONTROLS (D-PAD & ACTION BUTTONS)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Movement Buttons (Left, Right, Jump)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // LEFT ⬅️
                IconButton(
                    onClick = {
                        facingRight = false
                        val nextCol = playerCol - 0.7f
                        val targetColInt = nextCol.toInt()
                        val currentRowInt = playerRow.toInt()
                        if (nextCol >= 0 && !getBlockAt(targetColInt, currentRowInt).isSolid) {
                            playerCol = nextCol
                        }
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .background(DarkSurface, CircleShape)
                        .border(1.5.dp, Color(0xFF4CAF50), CircleShape)
                ) {
                    Text("⬅️", fontSize = 18.sp)
                }

                // RIGHT ➡️
                IconButton(
                    onClick = {
                        facingRight = true
                        val nextCol = playerCol + 0.7f
                        val targetColInt = (nextCol + 0.6f).toInt()
                        val currentRowInt = playerRow.toInt()
                        if (nextCol < worldCols - 1 && !getBlockAt(targetColInt, currentRowInt).isSolid) {
                            playerCol = nextCol
                        }
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .background(DarkSurface, CircleShape)
                        .border(1.5.dp, Color(0xFF4CAF50), CircleShape)
                ) {
                    Text("➡️", fontSize = 18.sp)
                }

                // JUMP ⬆️
                IconButton(
                    onClick = {
                        if (isGrounded) {
                            playerVy = -0.36f
                            playerRow -= 0.6f
                            isGrounded = false
                        }
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .background(DarkSurface, CircleShape)
                        .border(1.5.dp, NeonCyan, CircleShape)
                ) {
                    Text("🚀", fontSize = 18.sp)
                }
            }

            // Quick Mine & Place in Front
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // MINE IN FRONT ⛏️
                Button(
                    onClick = {
                        val targetCol = if (facingRight) (playerCol + 1f).toInt() else (playerCol - 1f).toInt()
                        val targetRow = playerRow.toInt()
                        breakBlock(targetCol, targetRow)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD54F)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("⛏️ Kaz", fontSize = 12.sp, color = Color(0xFFFFD54F), fontWeight = FontWeight.Bold)
                }

                // PLACE IN FRONT 🧱
                Button(
                    onClick = {
                        val targetCol = if (facingRight) (playerCol + 1f).toInt() else (playerCol - 1f).toInt()
                        val targetRow = playerRow.toInt()
                        placeBlock(targetCol, targetRow)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF81C784)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("🧱 Koy", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // CRAFTING DIALOG
    if (showCraftingDialog) {
        AlertDialog(
            onDismissRequest = { showCraftingDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🧰 Çalışma Masası & Crafting", fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Topladığın malzemeleri birleştirerek aletler ve yeni bloklar üret:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    craftingRecipes.forEach { recipe ->
                        val canCraft = recipe.requiredItems.all { (item, reqCount) ->
                            (inventory[item] ?: 0) >= reqCount
                        }

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (canCraft) Color(0xFF1E3A20) else DarkSurfaceVariant
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (canCraft) Color(0xFF4CAF50) else Color.DarkGray
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(recipe.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                    Text("Gereken: ${recipe.ingredientsDescription}", fontSize = 10.sp, color = TextSecondary)
                                }

                                Button(
                                    onClick = {
                                        if (canCraft) {
                                            recipe.requiredItems.forEach { (item, reqCount) ->
                                                val c = inventory[item] ?: 0
                                                inventory[item] = c - reqCount
                                            }
                                            if (recipe.resultTool != null) {
                                                currentTool = recipe.resultTool
                                                statusMessage = "✨ Yeni alet üretildi: ${recipe.resultTool.displayName}!"
                                            } else {
                                                val currentRes = inventory[recipe.result] ?: 0
                                                inventory[recipe.result] = currentRes + recipe.resultCount
                                                statusMessage = "✨ Üretildi: ${recipe.title}!"
                                            }
                                        }
                                    },
                                    enabled = canCraft,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF4CAF50)
                                    ),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("Üret", fontSize = 11.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCraftingDialog = false }) {
                    Text("Kapat", color = NeonCyan)
                }
            },
            containerColor = DarkSurface
        )
    }

    // HELP DIALOG
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = {
                Text("⛏️ Keon Craft Rehberi", fontWeight = FontWeight.Bold, color = TextPrimary)
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("🎮 Nasıl Oynanır?", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50), fontSize = 13.sp)
                    Text("• Ekranda herhangi bir bloğa dokunarak onu kırabilirsin.", fontSize = 11.sp, color = TextSecondary)
                    Text("• Boş bir alana dokunarak elindeki bloğu yerleştirebilirsin.", fontSize = 11.sp, color = TextSecondary)
                    Text("• ⬅️ ➡️ Yön butonlarıyla yürü, 🚀 zıpla.", fontSize = 11.sp, color = TextSecondary)
                    Text("• 🧰 Çalışma masasını açarak Meşale, Kazma ve TNT üretebilirsin.", fontSize = 11.sp, color = TextSecondary)
                    Text("• 🧨 TNT yerleştirip dokunduğunda 3 saniye sonra dev patlama olur!", fontSize = 11.sp, color = TextSecondary)
                    Text("• Derinlere indikçe Demir, Altın ve Elmas madenleri çıkar!", fontSize = 11.sp, color = TextSecondary)
                }
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text("Anladım", color = NeonGreen)
                }
            },
            containerColor = DarkSurface
        )
    }
}

// Procedural World Generator
fun generateProceduralWorld(cols: Int, rows: Int): List<VoxelBlockType> {
    val grid = MutableList(cols * rows) { VoxelBlockType.AIR }

    val surfaceLevel = 5

    for (c in 0 until cols) {
        // Natural small rolling hill height
        val hillOffset = if (c % 5 == 2 || c % 5 == 3) 1 else if (c % 7 == 0) -1 else 0
        val currentSurface = (surfaceLevel + hillOffset).coerceIn(4, 7)

        for (r in 0 until rows) {
            val idx = r * cols + c
            when {
                r < currentSurface -> {
                    grid[idx] = VoxelBlockType.AIR
                }
                r == currentSurface -> {
                    grid[idx] = VoxelBlockType.GRASS
                }
                r in (currentSurface + 1)..(currentSurface + 2) -> {
                    grid[idx] = VoxelBlockType.DIRT
                }
                r == rows - 1 -> {
                    grid[idx] = VoxelBlockType.BEDROCK
                }
                else -> {
                    // Deep underground ores
                    val rand = Random.nextInt(100)
                    grid[idx] = when {
                        r >= 12 && rand < 10 -> VoxelBlockType.DIAMOND_ORE
                        r >= 10 && rand < 18 -> VoxelBlockType.GOLD_ORE
                        r >= 8 && rand < 28 -> VoxelBlockType.IRON_ORE
                        rand < 38 -> VoxelBlockType.COAL_ORE
                        else -> VoxelBlockType.STONE
                    }
                }
            }
        }

        // Spawn Oak Trees at certain columns
        if (c == 4 || c == 14 || c == 19) {
            val groundY = currentSurface
            // Trunk
            if (groundY - 1 >= 0) grid[(groundY - 1) * cols + c] = VoxelBlockType.WOOD_LOG
            if (groundY - 2 >= 0) grid[(groundY - 2) * cols + c] = VoxelBlockType.WOOD_LOG
            // Leaves
            if (groundY - 3 >= 0) {
                grid[(groundY - 3) * cols + c] = VoxelBlockType.LEAVES
                if (c - 1 >= 0) grid[(groundY - 3) * cols + (c - 1)] = VoxelBlockType.LEAVES
                if (c + 1 < cols) grid[(groundY - 3) * cols + (c + 1)] = VoxelBlockType.LEAVES
            }
        }
    }

    return grid
}

// Drawing Voxel Block on Canvas
fun drawVoxelBlock(
    drawScope: DrawScope,
    block: VoxelBlockType,
    left: Float,
    top: Float,
    width: Float,
    height: Float,
    isTntActive: Boolean = false
) {
    with(drawScope) {
        val size = Size(width, height)
        val offset = Offset(left, top)

        if (block == VoxelBlockType.GRASS) {
            // Grass Block: Brown dirt base + bright green grass top
            drawRect(color = block.secondaryColor, topLeft = offset, size = size)
            drawRect(
                color = block.primaryColor,
                topLeft = offset,
                size = Size(width, height * 0.35f)
            )
            // Grass hanging blades
            drawRect(
                color = block.primaryColor,
                topLeft = Offset(left + width * 0.2f, top + height * 0.35f),
                size = Size(width * 0.2f, height * 0.2f)
            )
            drawRect(
                color = block.primaryColor,
                topLeft = Offset(left + width * 0.6f, top + height * 0.35f),
                size = Size(width * 0.2f, height * 0.15f)
            )
        } else if (block == VoxelBlockType.TNT) {
            // TNT Block
            val baseColor = if (isTntActive) Color.White else block.primaryColor
            drawRect(color = baseColor, topLeft = offset, size = size)
            // White middle band
            drawRect(
                color = if (isTntActive) Color.Red else Color.White,
                topLeft = Offset(left, top + height * 0.3f),
                size = Size(width, height * 0.4f)
            )
        } else if (block == VoxelBlockType.DIAMOND_ORE || block == VoxelBlockType.GOLD_ORE || block == VoxelBlockType.IRON_ORE || block == VoxelBlockType.COAL_ORE) {
            // Ore block
            drawRect(color = block.primaryColor, topLeft = offset, size = size)
            // Ore specks
            drawCircle(color = block.secondaryColor, radius = width * 0.12f, center = Offset(left + width * 0.3f, top + height * 0.3f))
            drawCircle(color = block.secondaryColor, radius = width * 0.15f, center = Offset(left + width * 0.7f, top + height * 0.4f))
            drawCircle(color = block.secondaryColor, radius = width * 0.12f, center = Offset(left + width * 0.4f, top + height * 0.75f))
        } else if (block == VoxelBlockType.TORCH) {
            // Torch
            val torchW = width * 0.25f
            val torchH = height * 0.65f
            drawRect(
                color = Color(0xFF6D4C41),
                topLeft = Offset(left + (width - torchW) / 2, top + height * 0.35f),
                size = Size(torchW, torchH)
            )
            // Flame
            drawCircle(
                color = Color(0xFFFFEB3B),
                radius = width * 0.2f,
                center = Offset(left + width / 2, top + height * 0.3f)
            )
            drawCircle(
                color = Color(0xFFFF5722),
                radius = width * 0.12f,
                center = Offset(left + width / 2, top + height * 0.3f)
            )
        } else if (block == VoxelBlockType.WOOD_LOG) {
            // Wood Log
            drawRect(color = block.primaryColor, topLeft = offset, size = size)
            drawRect(
                color = block.secondaryColor,
                topLeft = Offset(left + width * 0.15f, top),
                size = Size(width * 0.7f, height)
            )
        } else {
            // Standard Solid Block
            drawRect(color = block.primaryColor, topLeft = offset, size = size)
            // Subtle 3D border bevel
            drawRect(
                color = Color.Black.copy(alpha = 0.12f),
                topLeft = offset,
                size = size,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1f)
            )
        }
    }
}

// Drawing Player Steve
fun drawStevePlayer(
    drawScope: DrawScope,
    x: Float,
    y: Float,
    cellW: Float,
    cellH: Float,
    facingRight: Boolean,
    isMining: Boolean,
    currentTool: MinecraftTool
) {
    with(drawScope) {
        val playerW = cellW * 0.85f
        val playerH = cellH * 1.8f
        val top = y - cellH * 0.8f

        // Head (Skin & Hair)
        val headSize = playerW * 0.75f
        val headX = if (facingRight) x + playerW * 0.15f else x + playerW * 0.1f
        val headY = top

        // Brown Hair
        drawRect(
            color = Color(0xFF3E2723),
            topLeft = Offset(headX, headY),
            size = Size(headSize, headSize * 0.4f)
        )
        // Face skin
        drawRect(
            color = Color(0xFFFFCC80),
            topLeft = Offset(headX, headY + headSize * 0.35f),
            size = Size(headSize, headSize * 0.65f)
        )
        // Eyes
        val eyeX = if (facingRight) headX + headSize * 0.6f else headX + headSize * 0.2f
        drawRect(
            color = Color(0xFF1976D2),
            topLeft = Offset(eyeX, headY + headSize * 0.5f),
            size = Size(headSize * 0.2f, headSize * 0.2f)
        )

        // Body (Cyan/Blue Shirt)
        val bodyY = headY + headSize
        val bodyH = playerH * 0.42f
        drawRect(
            color = Color(0xFF00ACC1),
            topLeft = Offset(headX - headSize * 0.1f, bodyY),
            size = Size(headSize * 1.2f, bodyH)
        )

        // Pants (Dark Blue)
        val pantsY = bodyY + bodyH
        val pantsH = playerH * 0.35f
        drawRect(
            color = Color(0xFF1A237E),
            topLeft = Offset(headX - headSize * 0.1f, pantsY),
            size = Size(headSize * 1.2f, pantsH)
        )

        // Shoes
        drawRect(
            color = Color(0xFF424242),
            topLeft = Offset(headX - headSize * 0.1f, pantsY + pantsH - 4f),
            size = Size(headSize * 1.2f, 4f)
        )

        // Arm holding Tool
        val armX = if (facingRight) headX + headSize * 0.8f else headX - headSize * 0.3f
        val armAngle = if (isMining) 45f else 0f
        drawRect(
            color = Color(0xFFFFCC80),
            topLeft = Offset(armX, bodyY + 4f),
            size = Size(headSize * 0.35f, bodyH * 0.7f)
        )

        // Pickaxe Tool in Hand
        if (currentTool != MinecraftTool.HAND) {
            val toolColor = when (currentTool) {
                MinecraftTool.WOOD_PICKAXE -> Color(0xFF8D6E63)
                MinecraftTool.STONE_PICKAXE -> Color(0xFF90A4AE)
                MinecraftTool.IRON_PICKAXE -> Color(0xFFECEFF1)
                MinecraftTool.DIAMOND_PICKAXE -> NeonCyan
                else -> Color.Gray
            }
            drawRect(
                color = toolColor,
                topLeft = Offset(if (facingRight) armX + 6f else armX - 8f, bodyY + 2f),
                size = Size(6f, 12f)
            )
        }
    }
}

package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * Realistic Physics-Based Quantum Startup Animation Component
 * Renders authentic quantum phenomena: Wavefunction Collapse, 3D Bloch Sphere,
 * Electron Probability Cloud ($p$-Orbital), and Quantum Field Foam.
 */
@Composable
fun QuantumStartupOverlay(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    activePackage: com.example.ui.theme.KeonPackage? = com.example.ui.theme.KeonPackage.ULTIMATUM,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    // Pick a random realistic Quantum Animation mode on creation
    val animMode = remember { Random.nextInt(0, 4) }
    val isVipFast = activePackage != null
    val stepDelay = when (activePackage) {
        com.example.ui.theme.KeonPackage.ULTIMATUM -> 5L
        com.example.ui.theme.KeonPackage.AURA -> 7L
        com.example.ui.theme.KeonPackage.ZUMRUDU_ANKA -> 4L
        com.example.ui.theme.KeonPackage.FREE, null -> 16L
    }
    
    // Progress state 0.0 to 1.0
    var progress by remember { mutableFloatStateOf(0f) }
    var telemetryText by remember { mutableStateOf("KUANTUM BÖLGE SİMÜLASYONU BAŞLATILIYOR...") }

    // Continuous rotation and phase angle for physics animation
    val rotationAnim = remember { Animatable(0f) }
    val pulseAnim = remember { Animatable(0f) }

    // Phase angle timer
    LaunchedEffect(Unit) {
        rotationAnim.animateTo(
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(if (isVipFast) 2500 else 6000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    // Pulse loop for quantum state oscillations
    LaunchedEffect(Unit) {
        pulseAnim.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(if (isVipFast) 800 else 2000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    // Progress timer (Ultra fast delay for VIP Packages)
    LaunchedEffect(activePackage) {
        for (i in 1..100) {
            delay(stepDelay)
            progress = i / 100f
            telemetryText = when (i) {
                in 1..25 -> when (activePackage) {
                    com.example.ui.theme.KeonPackage.ULTIMATUM -> "⚡ ULTIMATUM PRO: REKLAMSIZ IŞIK HIZINDA AÇILIŞ..."
                    com.example.ui.theme.KeonPackage.AURA -> "🔮 AURA VIP: HOLOGRAFİK REKLAMSIZ SİBER MOTOR..."
                    com.example.ui.theme.KeonPackage.ZUMRUDU_ANKA -> "🔥 ZÜMRÜDÜ ANKA: EFSANEVİ ANINDA IŞIK HIZI AÇILIŞ..."
                    com.example.ui.theme.KeonPackage.FREE -> "🌱 ÜCRETSİZ PLAN: STANDART AÇILIŞ MOTORU..."
                    null -> "İSTETSEL DALGA FONKSİYONU Ψ(x,t) HESAPLANIYOR..."
                }
                in 26..55 -> when (activePackage) {
                    com.example.ui.theme.KeonPackage.ULTIMATUM -> "🎨 ULTRA GERÇEKÇİ FİZİK & ANİMASYON MOTORU ETKİN..."
                    com.example.ui.theme.KeonPackage.AURA -> "🎨 HOLOGRAFİK GERÇEKÇİ AURA ANİMASYONLARI YÜKLENDİ..."
                    com.example.ui.theme.KeonPackage.ZUMRUDU_ANKA -> "🔥 ZÜMRÜDÜ ANKA GERÇEKÇİ ALEV & PARÇACIK ANİMASYONU..."
                    com.example.ui.theme.KeonPackage.FREE -> "💬 TEMEL KEON AI YAZILIM MOTORU YÜKLENİYOR..."
                    null -> "KÜBİT SÜPERPOZİSYON SÜRÜSÜ BİLİNMEYENİ ÇÖZÜYOR..."
                }
                in 56..80 -> when (activePackage) {
                    com.example.ui.theme.KeonPackage.ULTIMATUM -> "🚀 REKLAMSIZ MAKSİMUM YAPAY ZEKA TEKNO KAPASİTE..."
                    com.example.ui.theme.KeonPackage.AURA -> "⚡ SÜPER HIZLI VIP CEVAP MOTORU YÜKLENDİ..."
                    com.example.ui.theme.KeonPackage.ZUMRUDU_ANKA -> "👑 EFSANEVİ SONSUZ KUANTUM BİLİŞSEL HAFIZA KİLİDİ AÇILDI..."
                    com.example.ui.theme.KeonPackage.FREE -> "☁️ 5 GB CLOUD STORAGE & STANDART AI MOTORU HAZIR..."
                    null -> "SCHRÖDINGER MATRİS EŞİTLİKLERİ İŞLENİYOR..."
                }
                else -> "KEON GERÇEK ZAMANLI KUANTUM MOTORU HAZIR!"
            }
        }
        delay(if (isVipFast) 80L else 300L)
        onDismiss()
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(300)),
        exit = fadeOut(tween(400))
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(DarkBackground)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Quantum Title Header
                Text(
                    text = "KEON QUANTUM PHYSICS CORE",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    letterSpacing = 2.5.sp
                )

                Text(
                    text = when (animMode) {
                        0 -> "Ψ(x,t) SCHRÖDINGER DALGA GİRİŞİMİ & ÇÖKME"
                        1 -> "3D BLOCH KÜRESİ KÜBİT SÜPERPOZİSYONU"
                        2 -> "ELEKTRON OLASILIK BULUTU & p-ORBİTALİ"
                        else -> "KUANTUM KÖPÜK & SANAL PARÇACIK ÇİFTLERİ"
                    },
                    fontSize = 10.sp,
                    color = TextSecondary,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
                )

                // Quantum Formula Banner
                Box(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .border(1.dp, NeonCyan.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = when (animMode) {
                            0 -> "iℏ (∂Ψ/∂t) = ĤΨ"
                            1 -> "|Ψ⟩ = α|0⟩ + β e^(iϕ)|1⟩"
                            2 -> "|Ψ(r,θ,ϕ)|^2 ∝ e^(-2r/a_0)"
                            else -> "Δx · Δp ≥ ℏ/2  (Heisenberg)"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonGreen,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // The Realistic Quantum Visual Canvas Container
                Box(
                    modifier = Modifier
                        .size(270.dp)
                        .background(Color(0xFF070B14), RoundedCornerShape(135.dp))
                        .border(1.5.dp, Brush.sweepGradient(listOf(NeonCyan, NeonMagenta, NeonGreen, NeonCyan)), RoundedCornerShape(135.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    when (animMode) {
                        0 -> WavefunctionInterferenceCanvas(rotationAnim.value, pulseAnim.value)
                        1 -> BlochSphereQubitCanvas(rotationAnim.value, pulseAnim.value)
                        2 -> ElectronOrbitalCloudCanvas(rotationAnim.value, pulseAnim.value)
                        else -> QuantumFoamCanvas(rotationAnim.value, pulseAnim.value)
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Progress Percentage
                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = NeonCyan,
                    trackColor = NeonCyan.copy(alpha = 0.15f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Telemetry Text
                Text(
                    text = telemetryText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = NeonGreen,
                    letterSpacing = 0.8.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Skip Button
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "ATLA >>",
                        fontSize = 11.sp,
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

/**
 * MODE 0: Real Schrödinger Wavefunction Interference & Double-Slit Diffraction Simulation
 * Plots continuous sine wave packets interfering with phase offsets, showing high & low intensity fringes.
 */
@Composable
private fun WavefunctionInterferenceCanvas(rotation: Float, pulse: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val maxRadius = size.width / 2f - 15f
        val phase = (rotation * PI / 180f).toFloat()

        // Background Radial Potential Well
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(NeonCyan.copy(alpha = 0.35f), NeonMagenta.copy(alpha = 0.15f), Color.Transparent),
                center = center,
                radius = maxRadius
            ),
            radius = maxRadius,
            center = center
        )

        // Draw Double Slit Wavefront Ripples from Source 1 & Source 2
        val source1 = Offset(center.x - 30f, center.y)
        val source2 = Offset(center.x + 30f, center.y)

        for (r in 10..maxRadius.toInt() step 14) {
            val waveRadius = (r + (rotation * 1.2f) % 14f)
            val alpha = (1f - waveRadius / maxRadius).coerceIn(0f, 1f) * 0.6f

            drawCircle(
                color = NeonCyan.copy(alpha = alpha),
                radius = waveRadius,
                center = source1,
                style = Stroke(width = 1.5f)
            )

            drawCircle(
                color = NeonMagenta.copy(alpha = alpha),
                radius = waveRadius,
                center = source2,
                style = Stroke(width = 1.5f)
            )
        }

        // Draw 2D Wavefunction Intensity Curve (Interference Pattern)
        val wavePath = Path()
        val step = 4
        var first = true

        for (x in 20 until size.width.toInt() - 20 step step) {
            val dx1 = x - source1.x
            val dy1 = (center.y + 70f) - source1.y
            val dist1 = sqrt(dx1 * dx1 + dy1 * dy1)

            val dx2 = x - source2.x
            val dy2 = (center.y + 70f) - source2.y
            val dist2 = sqrt(dx2 * dx2 + dy2 * dy2)

            val psi1 = sin(dist1 * 0.15f - phase * 2f)
            val psi2 = sin(dist2 * 0.15f - phase * 2f)
            val totalPsi = (psi1 + psi2) // Constructive / Destructive interference

            val amplitude = totalPsi * 22f * pulse
            val y = center.y + 60f - amplitude

            if (first) {
                wavePath.moveTo(x.toFloat(), y)
                first = false
            } else {
                wavePath.lineTo(x.toFloat(), y)
            }
        }

        drawPath(
            path = wavePath,
            color = NeonGreen,
            style = Stroke(width = 2.5f, cap = StrokeCap.Round)
        )

        // Slit Nodes
        drawCircle(color = Color.White, radius = 4f, center = source1)
        drawCircle(color = Color.White, radius = 4f, center = source2)

        // Measured Particle Collapse Dots
        for (i in 0 until 16) {
            val particleAngle = (i * 22.5f + rotation) * (PI / 180f).toFloat()
            val r = (maxRadius * 0.7f) * (0.3f + 0.7f * abs(sin(particleAngle * 3f + phase)))
            val px = center.x + r * cos(particleAngle)
            val py = center.y + r * sin(particleAngle)

            drawCircle(
                color = if (i % 2 == 0) NeonCyan else NeonMagenta,
                radius = 3.5f,
                center = Offset(px, py)
            )
        }
    }
}

/**
 * MODE 1: 3D Bloch Sphere Qubit Superposition Vector Representation
 * Draws a 3D projected sphere with equator, longitudinal meridian, $|0\rangle / |1\rangle$ poles,
 * and a precessing state vector $|\Psi\rangle = \cos(\theta/2)|0\rangle + e^{i\phi}\sin(\theta/2)|1\rangle$.
 */
@Composable
private fun BlochSphereQubitCanvas(rotation: Float, pulse: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val sphereR = size.width / 2.6f
        val rad = (rotation * PI / 180f).toFloat()

        // Outer Sphere Wireframe
        drawCircle(
            color = NeonCyan.copy(alpha = 0.3f),
            radius = sphereR,
            center = center,
            style = Stroke(width = 1.5f)
        )

        // Tilted Horizontal Equator Circle
        val equatorPath = Path().apply {
            addOval(
                androidx.compose.ui.geometry.Rect(
                    center.x - sphereR,
                    center.y - sphereR * 0.3f,
                    center.x + sphereR,
                    center.y + sphereR * 0.3f
                )
            )
        }
        drawPath(
            path = equatorPath,
            color = NeonBlue.copy(alpha = 0.5f),
            style = Stroke(width = 1.2f)
        )

        // Vertical Meridian Line
        drawLine(
            color = NeonCyan.copy(alpha = 0.5f),
            start = Offset(center.x, center.y - sphereR - 10f),
            end = Offset(center.x, center.y + sphereR + 10f),
            strokeWidth = 1.5f
        )

        // Poles Labels (Poles |0> and |1>)
        drawCircle(color = NeonGreen, radius = 5f, center = Offset(center.x, center.y - sphereR))
        drawCircle(color = NeonMagenta, radius = 5f, center = Offset(center.x, center.y + sphereR))

        // Precessing Qubit State Vector Position in 3D
        val theta = (0.7f + 0.3f * sin(rad * 1.5f)) * (PI / 2f).toFloat() // Polar angle
        val phi = rad * 2f // Azimuthal angle

        // Project 3D (x, y, z) to 2D Screen (px, py)
        val vx = sphereR * sin(theta) * cos(phi)
        val vy = sphereR * sin(theta) * sin(phi)
        val vz = sphereR * cos(theta)

        val screenX = center.x + vx.toFloat()
        val screenY = center.y - vz.toFloat() + vy.toFloat() * 0.25f // 3D tilt

        val vectorEnd = Offset(screenX, screenY)

        // Draw State Vector Line
        drawLine(
            color = Color.White,
            start = center,
            end = vectorEnd,
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )

        // State Tip Glowing Particle
        drawCircle(color = NeonMagenta, radius = 8f * pulse + 4f, center = vectorEnd)
        drawCircle(color = Color.White, radius = 4f, center = vectorEnd)

        // Phase Trace Trajectory Rings
        for (i in 0 until 12) {
            val pastPhi = phi - i * 0.15f
            val px = center.x + (sphereR * sin(theta) * cos(pastPhi)).toFloat()
            val py = center.y - (sphereR * cos(theta)).toFloat() + (sphereR * sin(theta) * sin(pastPhi)).toFloat() * 0.25f

            drawCircle(
                color = NeonCyan.copy(alpha = (1f - i / 12f) * 0.6f),
                radius = 2.5f,
                center = Offset(px, py)
            )
        }
    }
}

/**
 * MODE 2: Subatomic Electron Probability Cloud & $p$-Orbital Dumbbell Lobes
 * Simulates electron density distribution $|\psi(r,\theta,\phi)|^2$ with 120 drifting quantum particles.
 */
@Composable
private fun ElectronOrbitalCloudCanvas(rotation: Float, pulse: Float) {
    val particleSeed = remember { Array(120) { Random.nextFloat() to Random.nextFloat() } }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val scaleR = size.width / 2.5f
        val angleRad = (rotation * PI / 180f).toFloat()

        rotate(rotation * 0.5f, center) {
            // Draw p-orbital dumbbell lobes (Top and Bottom or Left and Right)
            val lobePath1 = Path().apply {
                addOval(
                    androidx.compose.ui.geometry.Rect(
                        center.x - scaleR * 0.35f,
                        center.y - scaleR * 0.95f,
                        center.x + scaleR * 0.35f,
                        center.y - scaleR * 0.05f
                    )
                )
            }
            val lobePath2 = Path().apply {
                addOval(
                    androidx.compose.ui.geometry.Rect(
                        center.x - scaleR * 0.35f,
                        center.y + scaleR * 0.05f,
                        center.x + scaleR * 0.35f,
                        center.y + scaleR * 0.95f
                    )
                )
            }

            drawPath(
                path = lobePath1,
                brush = Brush.radialGradient(
                    colors = listOf(NeonCyan.copy(alpha = 0.5f), Color.Transparent),
                    center = Offset(center.x, center.y - scaleR * 0.5f),
                    radius = scaleR * 0.5f
                )
            )

            drawPath(
                path = lobePath2,
                brush = Brush.radialGradient(
                    colors = listOf(NeonMagenta.copy(alpha = 0.5f), Color.Transparent),
                    center = Offset(center.x, center.y + scaleR * 0.5f),
                    radius = scaleR * 0.5f
                )
            )

            // Central Atomic Nucleus (Protons / Neutrons cluster)
            drawCircle(color = NeonGreen, radius = 9f * pulse + 3f, center = center)
            drawCircle(color = Color.White, radius = 4f, center = center)

            // Render 120 Quantum Electron Swarm Particles weighted by probability
            particleSeed.forEachIndexed { i, seed ->
                val seedR = seed.first
                val seedTheta = seed.second * 2f * PI.toFloat()

                // Quantum probability radial decay
                val r = scaleR * (0.15f + 0.8f * seedR * seedR)
                val cosTheta = cos(seedTheta)
                
                // p-orbital angular weighting |cos(theta)|
                val probWeight = abs(cosTheta)
                val finalR = r * (0.4f + 0.6f * probWeight)

                val px = center.x + finalR * sin(seedTheta + angleRad * 0.2f) * (if (i % 2 == 0) 0.6f else 1f)
                val py = center.y + finalR * cosTheta * (if (i % 2 == 0) 1f else 0.6f)

                val particleColor = if (py < center.y) NeonCyan else NeonMagenta
                val pAlpha = (1f - finalR / scaleR).coerceIn(0.1f, 0.9f) * pulse

                drawCircle(
                    color = particleColor.copy(alpha = pAlpha),
                    radius = if (i % 3 == 0) 3.5f else 2f,
                    center = Offset(px.toFloat(), py.toFloat())
                )
            }
        }
    }
}

/**
 * MODE 3: Quantum Foam Space-time Distortion & Virtual Particle Pair Creation
 * Renders spacetime grid warp matrix and virtual particle-antiparticle creation / annihilation ($e^- e^+$ loops).
 */
@Composable
private fun QuantumFoamCanvas(rotation: Float, pulse: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val maxR = size.width / 2f - 20f
        val phase = (rotation * PI / 180f).toFloat()

        // Deformed Spacetime Grid Matrix
        val gridLines = 8
        val spacing = size.width / gridLines

        for (i in 0..gridLines) {
            val pathH = Path()
            val pathV = Path()

            for (j in 0..gridLines) {
                val gx = j * spacing
                val gy = i * spacing

                val dx = gx - center.x
                val dy = gy - center.y
                val dist = sqrt(dx * dx + dy * dy)

                // Spacetime curvature warp around central mass
                val warp = exp(-dist / (maxR * 0.6f)) * 30f * sin(dist * 0.05f - phase)

                val wx = gx + (dx / (dist + 1f)) * warp
                val wy = gy + (dy / (dist + 1f)) * warp

                if (j == 0) {
                    pathH.moveTo(wx, wy)
                } else {
                    pathH.lineTo(wx, wy)
                }

                val vx = i * spacing
                val vy = j * spacing
                val vdx = vx - center.x
                val vdy = vy - center.y
                val vdist = sqrt(vdx * vdx + vdy * vdy)
                val vwarp = exp(-vdist / (maxR * 0.6f)) * 30f * sin(vdist * 0.05f - phase)

                val vwx = vx + (vdx / (vdist + 1f)) * vwarp
                val vwy = vy + (vdy / (vdist + 1f)) * vwarp

                if (j == 0) {
                    pathV.moveTo(vwx, vwy)
                } else {
                    pathV.lineTo(vwx, vwy)
                }
            }

            drawPath(path = pathH, color = NeonBlue.copy(alpha = 0.3f), style = Stroke(width = 1f))
            drawPath(path = pathV, color = NeonBlue.copy(alpha = 0.3f), style = Stroke(width = 1f))
        }

        // Virtual Particle Pair Creation & Annihilation Rings
        for (k in 0 until 6) {
            val kAngle = (k * 60f + rotation) * (PI / 180f).toFloat()
            val kRadius = maxR * 0.6f
            val cx = center.x + kRadius * cos(kAngle)
            val cy = center.y + kRadius * sin(kAngle)

            // Pair loop
            val pairSep = 18f * pulse
            val p1 = Offset(cx - pairSep, cy)
            val p2 = Offset(cx + pairSep, cy)

            // Electron e- (Green) and Positron e+ (Magenta)
            drawCircle(color = NeonGreen, radius = 5f, center = p1)
            drawCircle(color = NeonMagenta, radius = 5f, center = p2)

            // Energy line between pair
            drawLine(
                color = NeonCyan.copy(alpha = 0.6f * pulse),
                start = p1,
                end = p2,
                strokeWidth = 1.5f
            )
        }

        // Central Quantum Singularity Foam Core
        val radGradientRadius = (45f * pulse).coerceAtLeast(1f)
        val circleRadius = (35f * pulse).coerceAtLeast(1f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(NeonCyan, NeonMagenta.copy(alpha = 0.5f), Color.Transparent),
                center = center,
                radius = radGradientRadius
            ),
            radius = circleRadius,
            center = center
        )
    }
}

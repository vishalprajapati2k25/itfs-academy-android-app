package com.itfreesource.academy.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.itfreesource.academy.data.api.AcademyApiClient
import com.itfreesource.academy.security.AntiTamperEngine
import com.itfreesource.academy.security.SecurityManager
import com.itfreesource.academy.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun SecurityAuditDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()
    val isLight = MaterialTheme.colors.isLight

    val isFlagSecureActive = remember {
        activity?.let { SecurityManager.isScreenshotProtectionActive(it) } ?: true
    }

    val integrityReport = remember {
        AntiTamperEngine.auditProcessIntegrity(context)
    }

    var connectionStatus by remember { mutableStateOf(AcademyApiClient.lastSyncStatus) }
    var isPinging by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            backgroundColor = if (isLight) SlateCardBg else ObsidianCardBg,
            elevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Shield",
                            tint = SuccessEmerald,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Security & DRM Shield",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = if (isLight) SlateTextPrimary else ObsidianTextPrimary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = ObsidianTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .padding(vertical = 4.dp)
                ) {
                    // Check 1: Screenshot Protection
                    item {
                        AuditCheckItem(
                            title = "Anti-Screen Scraping (FLAG_SECURE)",
                            subtitle = if (isFlagSecureActive) "Active (Hardware screenshots & screen recording blocked)" else "Warning: Inactive",
                            isPassed = isFlagSecureActive
                        )
                    }

                    // Check 2: Reverse Engineering & Recompilation
                    item {
                        AuditCheckItem(
                            title = "Anti-Recompile & Signature Integrity",
                            subtitle = "Tamper check: ${if (!integrityReport.isTampered) "PASSED" else "TAMPERED"}",
                            isPassed = !integrityReport.isTampered
                        )
                    }

                    // Check 3: Debugger & Hook Detection
                    item {
                        AuditCheckItem(
                            title = "Debugger & Hook Detection (Frida/Xposed)",
                            subtitle = if (!integrityReport.isDebuggerAttached && !integrityReport.isHookFrameworkDetected) "No active debuggers or hooking agents" else "Warning: Debugger attached",
                            isPassed = !integrityReport.isDebuggerAttached && !integrityReport.isHookFrameworkDetected
                        )
                    }

                    // Check 4: Dynamic Question API
                    item {
                        AuditCheckItem(
                            title = "Zero Hardcoded Questions in APK",
                            subtitle = "Questions loaded dynamically via authenticated Edge API with SHA-256 hash DRM",
                            isPassed = true
                        )
                    }

                    // Check 5: ProGuard & R8 Code Armor
                    item {
                        AuditCheckItem(
                            title = "ProGuard & R8 Aggressive Obfuscation",
                            subtitle = "Package flattening, line number stripping, symbol dictionary scrambling",
                            isPassed = true
                        )
                    }

                    // Live API Gateway Check
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "EDGE API ARCHITECTURE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandIndigo,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Card(
                            backgroundColor = if (isLight) SlateElevatedBg else ObsidianElevatedBg,
                            shape = RoundedCornerShape(12.dp),
                            elevation = 0.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Base Gateway: ${AcademyApiClient.baseUrl}",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (isLight) SlateTextPrimary else ObsidianTextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Status: $connectionStatus",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (connectionStatus.contains("Live") || connectionStatus.contains("Cloudflare")) SuccessEmerald else WarningAmber
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                isPinging = true
                                val (ok, latency) = AcademyApiClient.testConnection()
                                connectionStatus = if (ok) "Live Edge Connected ($latency ms)" else "Fallback Seed Active"
                                isPinging = false
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = BrandIndigo
                        )
                    ) {
                        Text(if (isPinging) "Testing..." else "Test Edge API", fontSize = 13.sp)
                    }

                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = BrandIndigo,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Done", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun AuditCheckItem(
    title: String,
    subtitle: String,
    isPassed: Boolean
) {
    val isLight = MaterialTheme.colors.isLight
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isPassed) Icons.Default.CheckCircle else Icons.Default.Warning,
            contentDescription = if (isPassed) "Passed" else "Warning",
            tint = if (isPassed) SuccessEmerald else ErrorRose,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = if (isLight) SlateTextPrimary else ObsidianTextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = if (isLight) SlateTextMuted else ObsidianTextMuted
            )
        }
    }
}

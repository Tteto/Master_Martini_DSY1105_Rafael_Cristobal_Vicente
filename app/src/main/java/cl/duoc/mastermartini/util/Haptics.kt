package cl.duoc.mastermartini.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/** Recurso nativo: vibrador del dispositivo (permiso VIBRATE, no requiere permiso en tiempo de ejecucion). */
object Haptics {
    @Suppress("DEPRECATION")
    fun vibrate(context: Context, ms: Long = 100) {
        try {
            val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= 31) {
                context.getSystemService(VibratorManager::class.java).defaultVibrator
            } else {
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(ms)
            }
        } catch (_: Exception) {
            // Dispositivo sin vibrador: se ignora de forma segura.
        }
    }
}

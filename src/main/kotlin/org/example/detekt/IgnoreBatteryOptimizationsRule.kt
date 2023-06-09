package org.example.detekt

import io.gitlab.arturbosch.detekt.api.*
import io.gitlab.arturbosch.detekt.api.config
import org.jetbrains.kotlin.psi.KtClass

class IgnoreBatteryOptimizationsRule(config: Config = Config.empty) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Warning,
        "Avoid using the REQUEST_IGNORE_BATTERY_OPTIMIZATIONS permission.",
        Debt.FIVE_MINS
    )

    override fun visitClass(klass: KtClass) {
        if (klass.annotationEntries.any { it.text == "@RequiresPermission(\"android.Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS\")" }) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(klass),
                    "Avoid using the REQUEST_IGNORE_BATTERY_OPTIMIZATIONS permission."
                )
            )
        }
    }
}

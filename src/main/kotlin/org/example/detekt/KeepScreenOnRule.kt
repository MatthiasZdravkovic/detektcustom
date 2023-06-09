package org.example.detekt

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtProperty

class KeepScreenOnRule(config: Config) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Warning,
        "Avoid using the 'android:keepScreenOn' attribute or 'FLAG_KEEP_SCREEN_ON' to keep the screen on.",
        Debt.FIVE_MINS
    )

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)

        val ktProperties = file.children.filterIsInstance<KtProperty>()

        for (ktProperty in ktProperties) {
            if (ktProperty.name == "android:keepScreenOn" || ktProperty.name == "FLAG_KEEP_SCREEN_ON") {
                report(
                    CodeSmell(
                        issue,
                        Entity.from(ktProperty),
                        "Avoid using 'android:keepScreenOn' or 'FLAG_KEEP_SCREEN_ON' to keep the screen on."
                    )
                )
            }
        }
    }
}

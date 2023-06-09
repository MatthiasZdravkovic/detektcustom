package org.example.detekt

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile

class KeepCpuOnRule(config: Config) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Warning,
        "Avoid using PowerManager.WakeLock to keep the CPU on.",
        Debt.FIVE_MINS
    )

    private val wakeLockMethods = setOf(
        "newWakeLock",
        "acquire",
        "release"
    )

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)

        val ktCallExpressions = file.children.filterIsInstance<KtCallExpression>()

        for (ktCallExpression in ktCallExpressions) {
            val methodCall = ktCallExpression.calleeExpression?.text
            if (methodCall in wakeLockMethods) {
                report(
                    CodeSmell(
                        issue,
                        Entity.from(ktCallExpression),
                        "Avoid using PowerManager.WakeLock to keep the CPU on."
                    )
                )
            }
        }
    }
}

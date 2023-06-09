package org.example.detekt

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile

class DurableWakeLockRule(config: Config) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Warning,
        "Consider using a timeout when acquiring a wake lock to release it after a specified duration.",
        Debt.FIVE_MINS
    )

    private val acquireMethod = "acquire"

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)

        val ktCallExpressions = file.children.filterIsInstance<KtCallExpression>()

        for (ktCallExpression in ktCallExpressions) {
            val methodCall = ktCallExpression.calleeExpression?.text
            if (methodCall == acquireMethod && ktCallExpression.valueArguments.isEmpty()) {
                report(
                    CodeSmell(
                        issue,
                        Entity.from(ktCallExpression),
                        "Consider using a timeout when acquiring a wake lock to release it after a specified duration."
                    )
                )
            }
        }
    }
}

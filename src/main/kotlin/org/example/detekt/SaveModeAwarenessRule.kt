package org.example.detekt

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

class SaveModeAwarenessRule(config: Config) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.CodeSmell,
        "Taking into account when the device is entering or exiting the power save mode is a good practice.",
        Debt.FIVE_MINS
    )

    private val addActionMatcher: String = "addAction"
    private val createIntentFilterMatcher: String = "create"
    private val isPowerSaveModeMatcher: String = "isPowerSaveMode"
    private val intentFilterClassName: String = "android.content.IntentFilter"
    private val actionBatteryChanged: String = "android.intent.action.BATTERY_CHANGED"

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)

        val ktCallExpressions = file.collectDescendantsOfType<KtCallExpression>()

        for (ktCallExpression in ktCallExpressions) {
            when (ktCallExpression.calleeExpression?.text) {
                addActionMatcher, createIntentFilterMatcher -> {
                    val arguments = ktCallExpression.valueArguments
                    if (arguments.isNotEmpty()) {
                        val argument = arguments.firstOrNull()
                        val argumentValue = argument?.getArgumentExpression()?.text
                        if (argumentValue == "\"$actionBatteryChanged\"") {
                            report(
                                CodeSmell(
                                    issue,
                                    Entity.from(argument),
                                    "Avoid using $actionBatteryChanged in $addActionMatcher or $createIntentFilterMatcher"
                                )
                            )
                        }
                    }
                }
                isPowerSaveModeMatcher -> {
                    report(
                        CodeSmell(
                            issue,
                            Entity.from(ktCallExpression),
                            "Avoid using $isPowerSaveModeMatcher"
                        )
                    )
                }
            }
        }
    }
}

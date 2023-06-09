package org.example.detekt

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

class EverlastingServiceRule(config: Config) : Rule(config) {

    private val startServiceMatcher = "Context.startService"
    private val stopServiceMatchers = listOf("Context.stopService", "Service.stopSelf")

    override val issue = Issue(
        javaClass.simpleName,
        Severity.CodeSmell,
        "This rule detects potential everlasting services that may lead to uncontrolled energy leakage.",
        Debt.FIVE_MINS
    )

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)

        val ktCallExpressions = file.collectDescendantsOfType<KtCallExpression>()

        val startedServices = mutableSetOf<String>()

        for (ktCallExpression in ktCallExpressions) {
            when (ktCallExpression.calleeExpression?.text) {
                startServiceMatcher -> {
                    val argument = ktCallExpression.valueArguments.firstOrNull()
                    val serviceClassName = argument?.text?.removeSurrounding("\"")
                    if (serviceClassName != null) {
                        startedServices.add(serviceClassName)
                    }
                }
                in stopServiceMatchers -> {
                    val argument = ktCallExpression.valueArguments.firstOrNull()
                    val serviceClassName = argument?.text?.removeSurrounding("\"")
                    if (serviceClassName != null) {
                        startedServices.remove(serviceClassName)
                    }
                }
            }
        }

        for (serviceClassName in startedServices) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(file),
                    "The service $serviceClassName might not be properly stopped, leading to uncontrolled energy leakage."
                )
            )
        }
    }
}

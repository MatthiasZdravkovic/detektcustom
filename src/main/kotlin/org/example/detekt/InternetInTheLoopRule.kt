import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

class InternetInTheLoopRule(config: Config) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Maintainability,
        "Opening and closing internet connections within loop structures can lead to battery inefficiency.",
        Debt.FIVE_MINS
    )

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)

        val loopExpressions = function.bodyExpression?.collectDescendantsOfType<KtLoopExpression>()

        loopExpressions?.forEach { loopExpression ->
            val urlOpenConnections = loopExpression.collectDescendantsOfType<KtCallExpression> {
                it.calleeExpression?.text == "openConnection"
                        && it.parent is KtDotQualifiedExpression
                        && (it.parent as KtDotQualifiedExpression).receiverExpression?.text == "URL"
            }

            if (urlOpenConnections.isNotEmpty()) {
                report(
                    CodeSmell(
                        issue,
                        Entity.from(loopExpression),
                        "Avoid opening and closing internet connections within loops."
                    )
                )
            }
        }
    }
}

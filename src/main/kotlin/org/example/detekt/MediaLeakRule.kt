package org.example.detekt

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

class MediaLeakRule(config: Config) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Maintainability,
        "Failure to release MediaRecorder or MediaPlayer objects may lead to resource leaks.",
        Debt.FIVE_MINS
    )

    private val mediaRecorderClassName = "android.media.MediaRecorder"
    private val mediaPlayerClassName = "android.media.MediaPlayer"

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)

        val mediaRecorderObjects = file.collectDescendantsOfType<KtCallExpression> {
            it.calleeExpression?.text == mediaRecorderClassName
        }

        val mediaPlayerObjects = file.collectDescendantsOfType<KtCallExpression> {
            it.calleeExpression?.text == mediaPlayerClassName
        }

        checkMediaLeaks(mediaRecorderObjects)
        checkMediaLeaks(mediaPlayerObjects)
    }

    private fun checkMediaLeaks(objects: List<KtCallExpression>) {
        for (obj in objects) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(obj),
                    "Media object is not released properly. Ensure that release() method is called."
                )
            )
        }
    }
}

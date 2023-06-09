package org.example.detekt

import InternetInTheLoopRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

class MyRuleSetProvider : RuleSetProvider {
    override val ruleSetId: String = "MyRuleSet"

    override fun instance(config: Config): RuleSet {
        return RuleSet(
            ruleSetId,
            listOf(
                MyRule(config),
                SaveModeAwarenessRule(config),
                MediaLeakRule(config),
                EverlastingServiceRule(config),
                InternetInTheLoopRule(config),
                UncompressedDataTransmission(config),
                KeepScreenOnRule(config),
                KeepCpuOnRule(config),
                DurableWakeLockRule(config),

            ),
        )
    }
}

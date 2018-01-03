package com.sandjelkovic.kombinator.domain.service

import com.sandjelkovic.kombinator.domain.model.Combination
import java.util.*

/**
 * @author sandjelkovic
 * @date 11.11.17.
 */
interface CombinationService {
    fun getCombinationByInternalId(id: Long) : Optional<Combination>
    fun findAllCombinations() : List<Combination>
    fun findByUUID(uuid: String): Optional<Combination>
    fun createCombination(combination: Combination): Combination
}

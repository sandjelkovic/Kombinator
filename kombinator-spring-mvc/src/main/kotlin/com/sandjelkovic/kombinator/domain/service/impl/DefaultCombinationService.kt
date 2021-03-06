package com.sandjelkovic.kombinator.domain.service.impl

import arrow.core.Either
import arrow.core.Option
import arrow.core.filterOrElse
import arrow.core.rightIfNotNull
import com.sandjelkovic.flatMapToOption
import com.sandjelkovic.kombinator.domain.exception.DomainValidationException
import com.sandjelkovic.kombinator.domain.exception.DomainValidationException.RequiredParameterMissing
import com.sandjelkovic.kombinator.domain.model.Combination
import com.sandjelkovic.kombinator.domain.repository.CombinationRepository
import com.sandjelkovic.kombinator.domain.service.CombinationService
import java.util.*

/**
 * @author sandjelkovic
 * @date 11.11.17.
 */
class DefaultCombinationService(
    private val combinationRepository: CombinationRepository
) : CombinationService {
    override fun findByUUID(uuid: String): Option<Combination> =
        combinationRepository.findByUuid(uuid).flatMapToOption()

    override fun findAllCombinations(): List<Combination> = combinationRepository.findAll().toList()

    override fun getCombinationByInternalId(id: Long): Option<Combination> =
        if (id > 0) combinationRepository.findById(id).flatMapToOption()
        else Option.empty()

    override fun createCombination(combination: Combination): Either<DomainValidationException, Combination> =
        combination.rightIfNotNull { DomainValidationException.RequiredParameterMissing(combinationKey) }
            .filterOrElse({ it.id == null }, { RequiredParameterMissing("$combinationKey.id") })
            .filterOrElse({ it.uuid == null }, { RequiredParameterMissing("$combinationKey.uuid") })
            .map(combinationUuidEnricher(::generateUUIDString))
            .map { combinationRepository.save(it) }

    private fun generateUUIDString() = UUID.randomUUID().toString()
    private fun combinationUuidEnricher(uuidSupplier: () -> String) =
        { combination: Combination -> combination.copy(uuid = uuidSupplier()) }

    companion object {
        const val combinationKey = "combination"
    }
}

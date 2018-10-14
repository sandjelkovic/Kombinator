package com.sandjelkovic.kombinator.domain.service.impl

import arrow.core.Either
import assertk.fail
import com.sandjelkovic.kombinator.domain.exception.ReferenceDoesntExist
import com.sandjelkovic.kombinator.domain.exception.RequiredParameterMissing
import com.sandjelkovic.kombinator.domain.model.Combination
import com.sandjelkovic.kombinator.domain.model.Slot
import com.sandjelkovic.kombinator.domain.repository.CombinationRepository
import com.sandjelkovic.kombinator.domain.repository.SlotRepository
import com.sandjelkovic.kombinator.test.isEqualToOneOf
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.all
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo
import strikt.assertions.isNotEmpty
import java.util.*
import java.util.Comparator.comparing

/**
 * @author sandjelkovic
 * @date 30.12.17.
 */
class DefaultSlotServiceUnitTest {

    private val existingId = 555L
    private val existingCombination = Combination(id = existingId, uuid = UUID.randomUUID().toString())
    private val slotWithCombination = Slot(name = "Slot name", combination = existingCombination)
    private val slotAfterSaving = slotWithCombination.copy(id = 5)

    private val mockSlotRepository = mockk<SlotRepository>().also {
        every { it.findByCombinationUuid(existingCombination.uuid!!) } returns listOf(
			Slot(name = "GPU", combination = existingCombination, position = 2),
			Slot(name = "CPU", combination = existingCombination, position = 1)
		)
        every { it.save(eq(slotWithCombination)) } returns slotAfterSaving
    }

    private val mockCombinationRepository = mockk<CombinationRepository>().also {
        every { it.findByUuid(eq("")) } returns Optional.empty()
        every { it.findByUuid(eq(existingCombination.uuid!!)) } returns Optional.of(existingCombination)
    }

    private val slotService: DefaultSlotService = DefaultSlotService(mockSlotRepository, mockCombinationRepository)

    @Test
    fun `Should get the slots associated with Combination from UUID`() {
        val slots = slotService.getSlotsByCombination(existingCombination.uuid!!)

        expectThat(slots)
            .isNotEmpty().hasSize(2)
            .all {
                get { combination!!.id }.isEqualTo(existingCombination.id)
                get { combination!!.uuid }.isEqualTo(existingCombination.uuid)
                get { name }.isEqualToOneOf(listOf("CPU", "GPU"))
            }
//      no replacement in Strikt for this yet
        assertThat(slots).isSortedAccordingTo(comparing<Slot, Int> { it.position })
    }

    @Test
    fun `Should save the Slot`() {
        val either = slotService.save(slotWithCombination)
        when (either) {
            is Either.Left -> fail("There shouldn't be an exception")
            is Either.Right -> {
                assertThat(either.b).isEqualTo(slotAfterSaving)
            }
        }
    }

    @Test
    fun `Should return validation exception if the connected Combination doesn't exist`() {
        val either = slotService.save(Slot(name = "Slot name", combination = Combination()))

        when (either) {
            is Either.Right -> fail("No exception returned")
            is Either.Left -> {
                assert(either.a is ReferenceDoesntExist)
                assert(either.a.message.equals("slot.combination"))
            }
        }
    }

    @Test
    fun `Should return validation exception if there is no Combination object connected to the Slot`() {
        val either = slotService.save(Slot(name = "Slot name"))

        when (either) {
            is Either.Right -> fail("No exception returned")
            is Either.Left -> {
                assert(either.a is RequiredParameterMissing)
                assert(either.a.message.equals("slot.combination"))
            }
        }
    }
}

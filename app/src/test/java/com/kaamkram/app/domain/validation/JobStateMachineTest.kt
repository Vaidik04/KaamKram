package com.kaamkram.app.domain.validation

import com.kaamkram.app.domain.model.JobStatus
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class JobStateMachineTest {

    @Test
    fun testValidTransitions() {
        assertTrue(JobStateMachine.canTransition(JobStatus.REQUESTED, JobStatus.QUOTED))
        assertTrue(JobStateMachine.canTransition(JobStatus.QUOTED, JobStatus.APPROVED))
        assertTrue(JobStateMachine.canTransition(JobStatus.APPROVED, JobStatus.IN_PROGRESS))
        assertTrue(JobStateMachine.canTransition(JobStatus.IN_PROGRESS, JobStatus.COMPLETED))
        assertTrue(JobStateMachine.canTransition(JobStatus.COMPLETED, JobStatus.INVOICED))
        assertTrue(JobStateMachine.canTransition(JobStatus.INVOICED, JobStatus.PAID))
    }

    @Test
    fun testCancellationFromAnyActiveState() {
        assertTrue(JobStateMachine.canTransition(JobStatus.REQUESTED, JobStatus.CANCELLED))
        assertTrue(JobStateMachine.canTransition(JobStatus.QUOTED, JobStatus.CANCELLED))
        assertTrue(JobStateMachine.canTransition(JobStatus.APPROVED, JobStatus.CANCELLED))
        assertTrue(JobStateMachine.canTransition(JobStatus.SCHEDULED, JobStatus.CANCELLED))
        assertTrue(JobStateMachine.canTransition(JobStatus.IN_PROGRESS, JobStatus.CANCELLED))
        assertTrue(JobStateMachine.canTransition(JobStatus.COMPLETED, JobStatus.CANCELLED))
        assertTrue(JobStateMachine.canTransition(JobStatus.INVOICED, JobStatus.CANCELLED))
    }

    @Test
    fun testForbiddenTransitions() {
        // Cannot jump directly from REQUESTED to PAID
        assertFalse(JobStateMachine.canTransition(JobStatus.REQUESTED, JobStatus.PAID))
        // Cannot transition from terminal states
        assertFalse(JobStateMachine.canTransition(JobStatus.PAID, JobStatus.IN_PROGRESS))
        assertFalse(JobStateMachine.canTransition(JobStatus.CANCELLED, JobStatus.IN_PROGRESS))

        assertThrows(IllegalStateException::class.java) {
            JobStateMachine.assertValidTransition(JobStatus.PAID, JobStatus.REQUESTED)
        }
    }
}

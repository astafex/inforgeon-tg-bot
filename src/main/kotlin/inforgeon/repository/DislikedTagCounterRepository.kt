package inforgeon.inforgeon.repository

import inforgeon.inforgeon.entity.DislikedTagCounter
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface DislikedTagCounterRepository : JpaRepository<DislikedTagCounter, UUID>
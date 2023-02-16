package inforgeon.inforgeon.repository

import inforgeon.inforgeon.entity.UserSettings
import org.springframework.data.jpa.repository.JpaRepository

interface UserSettingsRepository : JpaRepository<UserSettings, Long> {

}
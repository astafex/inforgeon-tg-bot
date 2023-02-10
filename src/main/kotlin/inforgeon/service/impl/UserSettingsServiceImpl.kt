package inforgeon.inforgeon.service.impl

import inforgeon.inforgeon.entity.UserSettings
import inforgeon.inforgeon.repository.UserSettingsRepository
import inforgeon.inforgeon.service.UserSettingsService
import org.springframework.stereotype.Service

@Service
class UserSettingsServiceImpl(private val repository: UserSettingsRepository)
    : UserSettingsService
{
    @Throws(NoSuchElementException::class)
    override fun get(username: String): UserSettings {
        return repository.findById(username).orElseThrow()
    }

    override fun initializeUser(username: String): UserSettings {
        return repository.save(UserSettings(username = username))
    }

    override fun save(settings: UserSettings): UserSettings {
        return repository.save(settings)
    }
}
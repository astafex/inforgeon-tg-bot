package inforgeon.inforgeon.service.impl

import inforgeon.inforgeon.entity.UserSettings
import inforgeon.inforgeon.repository.UserSettingsRepository
import inforgeon.inforgeon.service.UserSettingsService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserSettingsServiceImpl(private val repository: UserSettingsRepository)
    : UserSettingsService
{
    @Transactional(readOnly = true)
    @Throws(NoSuchElementException::class)
    override fun get(username: String): UserSettings? {
        return repository.findById(username).orElse(null)
    }

    @Transactional
    override fun initializeUser(username: String): UserSettings {
        return repository.save(UserSettings(username = username))
    }

    @Transactional
    override fun save(settings: UserSettings): UserSettings {
        return repository.save(settings)
    }
}
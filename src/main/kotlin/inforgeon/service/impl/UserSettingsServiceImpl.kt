package inforgeon.inforgeon.service.impl

import inforgeon.inforgeon.entity.UserSettings
import inforgeon.inforgeon.repository.UserSettingsRepository
import inforgeon.inforgeon.service.UserSettingsService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserSettingsServiceImpl(private val repository: UserSettingsRepository) : UserSettingsService {
    @Transactional(readOnly = true)
    @Throws(NoSuchElementException::class)
    override fun get(userId: Long): UserSettings? {
        return repository.findById(userId).orElse(null)
    }

    @Transactional
    override fun initializeUser(userSettings: UserSettings): UserSettings {
        return repository.save(userSettings)
    }

    @Transactional
    override fun save(settings: UserSettings): UserSettings {
        return repository.save(settings)
    }
}
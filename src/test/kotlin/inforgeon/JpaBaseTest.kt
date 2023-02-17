package inforgeon

import inforgeon.inforgeon.repository.UserSettingsRepository
import inforgeon.repository.RssEntryRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
internal class JpaBaseTest {

    @Autowired
    protected lateinit var rssEntryRepository: RssEntryRepository
    @Autowired
    protected lateinit var userSettingsRepository: UserSettingsRepository


}
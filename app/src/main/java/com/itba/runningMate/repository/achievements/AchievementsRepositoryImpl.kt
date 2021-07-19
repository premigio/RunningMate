package com.itba.runningMate.repository.achievements

import com.itba.runningMate.db.achievement.AchievementDao
import com.itba.runningMate.db.achievement.AchievementEntityBuilder
import com.itba.runningMate.domain.Achievements
import io.reactivex.Completable
import io.reactivex.Flowable
import java.util.*

class AchievementsRepositoryImpl(
    private val achievementDao: AchievementDao
) : AchievementsRepository {

    override fun getAchievements(): Flowable<List<Achievements>> {
        return achievementDao.getAchievements()
            .map { l -> l.map { entity -> entity.achievement } }
    }

    override fun getAchievements(n: Int): Flowable<List<Achievements>> {
        return achievementDao.getAchievements()
            .limit(n.toLong())
            .map { l -> l.map { entity -> entity.achievement } }
    }

    override fun removeAchievements(achievements: Achievements): Completable {
        return achievementDao.deleteAchievement(achievements)
    }

    override fun addAchievements(achievements: Achievements, timestamp: Date): Completable {
        return achievementDao.insertAchievement(
            AchievementEntityBuilder()
                .achievement(achievements)
                .timestamp(timestamp)
                .build()
        )
    }

    override fun addAchievements(achievements: List<Achievements>, timestamp: Date): Completable {
        return achievementDao.insertAchievements(achievements
            .map { a ->
                AchievementEntityBuilder()
                    .achievement(a)
                    .timestamp(timestamp)
                    .build()
            }
        )
    }
}

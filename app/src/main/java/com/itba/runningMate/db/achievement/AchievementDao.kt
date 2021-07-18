package com.itba.runningMate.db.achievement

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.itba.runningMate.domain.Achievements
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface AchievementDao {

    @Query("SELECT * FROM achievements ORDER BY timestamp DESC")
    fun getAchievements(): Flowable<List<AchievementEntity>>

    @Query("DELETE FROM achievements WHERE achievements.achievement = :achievements")
    fun deleteAchievements(achievements: Achievements): Completable

    @Insert
    fun insertRoute(achievementEntity: AchievementEntity): Completable

}
package com.itba.runningMate.db.achievement

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.itba.runningMate.domain.Achievements
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface AchievementDao {

    @Query("SELECT * FROM achievements ORDER BY timestamp DESC")
    fun getAchievements(): Flowable<List<AchievementEntity>>

    @Query("DELETE FROM achievements WHERE achievements.achievement = :achievements")
    fun deleteAchievement(achievements: Achievements): Completable

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAchievement(achievementEntity: AchievementEntity): Completable

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAchievements(achievementEntities: List<AchievementEntity>): Completable

}
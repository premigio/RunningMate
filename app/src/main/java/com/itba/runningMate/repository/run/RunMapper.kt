package com.itba.runningMate.repository.run

import com.itba.runningMate.db.RunEntity
import com.itba.runningMate.db.RunEntityBuilder
import com.itba.runningMate.domain.Run
import java.util.*

class RunMapper {

    companion object {

        fun toModel(entity: RunEntity?): Run? {

            return if (entity != null) Run.Builder()
                    .uid(entity.uid)
                    .title(entity.title)
                    .route(entity.route)
                    .startTime(entity.startTime)
                    .endTime(entity.endTime)
                    .runningTime(entity.elapsedTime)
                    .distance(entity.distance)
                    .pace(entity.pace)
                    .velocity(entity.velocity)
                    .calories(entity.calories)
                    .build() else null
        }

        fun toEntity(model: Run): RunEntity {
            return RunEntityBuilder()
                    .uid(model.uid)
                    .title(model.title)
                    .route(model.route)
                    .startTime(model.startTime)
                    .endTime(model.endTime)
                    .elapsedTime(model.runningTime)
                    .distance(model.distance)
                    .pace(model.pace)
                    .velocity(model.velocity)
                    .calories(model.calories)
                    .build()
        }

        @JvmStatic
        fun toModel(entities: List<RunEntity>): List<Run?> {
            val aux: MutableList<Run?> = LinkedList()
            for (entity in entities) {
                aux.add(toModel(entity))
            }
            return aux
        }

        @JvmStatic
        fun toEntity(models: List<Run>): List<RunEntity> {
            val aux: MutableList<RunEntity> = LinkedList()
            for (model in models) {
                aux.add(toEntity(model))
            }
            return aux
        }
    }
}
package com.itba.runningMate.repository.run;

import com.itba.runningMate.db.RunEntity;
import com.itba.runningMate.domain.Run;

import java.util.LinkedList;
import java.util.List;

public class RunMapper {

    public static Run toModel(final RunEntity entity) {
        return new Run()
                .uid(entity.getUid())
                .route(entity.getRoute())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .elapsedTime(entity.getElapsedTime())
                .distance(entity.getDistance())
                .pace(entity.getPace())
                .velocity(entity.getVelocity())
                .calories(entity.getCalories());
    }

    public static RunEntity toEntity(final Run model) {
        return new RunEntity()
                .uid(model.getUid())
                .route(model.getRoute())
                .startTime(model.getStartTime())
                .endTime(model.getEndTime())
                .elapsedTime(model.getElapsedTime())
                .distance(model.getDistance())
                .pace(model.getPace())
                .velocity(model.getVelocity())
                .calories(model.getCalories());
    }

    public static List<Run> toModel(final List<RunEntity> entities) {
        final List<Run> aux = new LinkedList<>();
        for (RunEntity entity : entities) {
            aux.add(RunMapper.toModel(entity));
        }
        return aux;
    }


    public static List<RunEntity> toEntity(final List<Run> models) {
        final List<RunEntity> aux = new LinkedList<>();
        for (Run model : models) {
            aux.add(RunMapper.toEntity(model));
        }
        return aux;
    }

}

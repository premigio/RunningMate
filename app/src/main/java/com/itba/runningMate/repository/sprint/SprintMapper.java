package com.itba.runningMate.repository.sprint;

import com.itba.runningMate.db.SprintEntity;
import com.itba.runningMate.domain.Sprint;

import java.util.LinkedList;
import java.util.List;

public class SprintMapper {

    public static Sprint toModel(final SprintEntity entity) {
        return new Sprint()
                .uid(entity.getUid())
                .route(entity.getRoute())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime());
    }

    public static SprintEntity toEntity(final Sprint model) {
        return new SprintEntity()
                .uid(model.getUid())
                .route(model.getRoute())
                .startTime(model.getStartTime())
                .endTime(model.getEndTime());
    }

    public static List<Sprint> toModel(final List<SprintEntity> entities) {
        final List<Sprint> aux = new LinkedList<>();
        for (SprintEntity entity : entities) {
            aux.add(SprintMapper.toModel(entity));
        }
        return aux;
    }


    public static List<SprintEntity> toEntity(final List<Sprint> models) {
        final List<SprintEntity> aux = new LinkedList<>();
        for (Sprint model : models) {
            aux.add(SprintMapper.toEntity(model));
        }
        return aux;
    }

}

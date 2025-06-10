package com.gym.dao;

import com.gym.dto.response.ActiveCycleListDto;
import com.gym.dto.response.CycleForTrainerList;
import com.gym.dto.response.CycleWithEnrollmentDto;
import com.gym.model.Cycle;

import java.util.List;
import java.util.Optional;

public interface CycleDao {
    List<Cycle> getCyclesByPage(int page, int size);

    int getCount();

    void deleteById(long id);

    void insert(Cycle cycle);

    Optional<Cycle> findById(long id);

    void update(Cycle cycle);

    List<Cycle> getPublishedCycles();

    void enrollCycle(long accountId, long cycleId);

    List<CycleWithEnrollmentDto> getCyclesWithEnrollmentsByAccountIdAndStatus(long accountId, String status);

    void changeCycleStatus(long cycleId, String status);

    List<ActiveCycleListDto> getActiveCyclesWithTrainer(int page, int size);

    void assignTrainerToCycle(long accountCycleEnrollmentId, long trainerId);

    int getActiveCount();

    int getActiveCycleByTrainerIdCount(long trainerId);

    List<CycleForTrainerList> getCyclesByTrainerId(int page, int size, long trainerId);
}

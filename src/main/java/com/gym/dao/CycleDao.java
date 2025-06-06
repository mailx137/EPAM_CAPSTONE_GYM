package com.gym.dao;

import com.gym.model.Cycle;

import java.util.List;
import java.util.Optional;

public interface CycleDao {
    List<Cycle> getCyclesByPage(int page, int size);

    int getCount();

    void deleteById(long id);

    void insert(Cycle cycle);

    Optional<Cycle> findById(long id);
}

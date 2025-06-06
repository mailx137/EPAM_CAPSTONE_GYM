package com.gym.dao;

import com.gym.model.Cycle;

import java.util.List;

public interface CycleDao {
    List<Cycle> getCyclesByPage(int page, int size);

    int getCount();

    void deleteById(long id);
}

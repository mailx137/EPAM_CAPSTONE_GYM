package com.gym.service;

import com.gym.dao.CycleDao;
import com.gym.dto.request.CycleFormDto;
import com.gym.dto.response.Paginator;
import com.gym.model.Cycle;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

@Service
public class CycleService {
    private CycleDao cycleDao;

    public CycleService(CycleDao cycleDao) {
        this.cycleDao = cycleDao;
    }

    public Paginator<Cycle> getPaginatedAllCycles(Integer page, Integer size) {
        if (page == null || size == null || page < 1 || size < 1) {
            throw new IllegalArgumentException("Page and size must be greater than 0");
        }
        return new Paginator<>(
                cycleDao.getCount(),
                page,
                size,
                cycleDao.getCyclesByPage(page, size)
        );
    }

    @Transactional(rollbackFor = SQLException.class)
    public void deleteCycle(long id) {
        cycleDao.deleteById(id);
    }

    @Transactional
    public void createCycle(CycleFormDto cycle) {
        if (cycle == null) {
            throw new IllegalArgumentException("Cycle form data cannot be null");
        }
        Cycle newCycle = new Cycle();
        newCycle.setName(cycle.getName());
        newCycle.setDescription(cycle.getDescription());
        newCycle.setDurationInDays(cycle.getDurationInDays());
        newCycle.setPublished(cycle.isPublished());
        newCycle.setPrice(cycle.getPrice());

        cycleDao.insert(newCycle);
    }
}

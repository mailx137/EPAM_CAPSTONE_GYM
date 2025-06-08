package com.gym.service;

import com.gym.dao.CycleDao;
import com.gym.dto.request.CycleFormDto;
import com.gym.dto.response.Paginator;
import com.gym.model.Cycle;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

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

    public Optional<Cycle> findCycleById(long id) {
        return cycleDao.findById(id);
    }

    public CycleFormDto mapToFormDtoFromCycle(Cycle cycle) {
        if (cycle == null) {
            throw new IllegalArgumentException("Cycle cannot be null");
        }
        CycleFormDto cycleFormDto = new CycleFormDto();
        cycleFormDto.setId(cycle.getId());
        cycleFormDto.setName(cycle.getName());
        cycleFormDto.setDescription(cycle.getDescription());
        cycleFormDto.setDurationInDays(cycle.getDurationInDays());
        cycleFormDto.setPublished(cycle.isPublished());
        cycleFormDto.setPrice(cycle.getPrice());
        return cycleFormDto;
    }

    @Transactional(rollbackFor = SQLException.class)
    public void updateCycle(long id, CycleFormDto cycleFormDto) {
        if (cycleFormDto == null) {
            throw new IllegalArgumentException("Cycle form data cannot be null");
        }
        Cycle cycle = cycleDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cycle with id " + id + " not found"));

        cycle.setName(cycleFormDto.getName());
        cycle.setDescription(cycleFormDto.getDescription());
        cycle.setDurationInDays(cycleFormDto.getDurationInDays());
        cycle.setPublished(cycleFormDto.isPublished());
        cycle.setPrice(cycleFormDto.getPrice());

        cycleDao.update(cycle);
    }

    public List<Cycle> getPublishedCycles() {
        return cycleDao.getPublishedCycles();
    }

    @Transactional
    public void enrollCycle(long cycleId, long accountId) {
        if (cycleId <= 0 || accountId <= 0) {
            throw new IllegalArgumentException("Cycle ID and Account ID must be greater than 0");
        }
        cycleDao.enrollCycle(cycleId, accountId);
    }
}

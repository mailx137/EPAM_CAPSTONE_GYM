package com.gym.service;

import com.gym.dao.AccountCycleEnrollmentDao;
import com.gym.dao.CycleDao;
import com.gym.dto.request.CycleFormDto;
import com.gym.dto.response.ActiveCycleListDto;
import com.gym.dto.response.CycleForTrainerList;
import com.gym.dto.response.CycleWithEnrollmentDto;
import com.gym.dto.response.Paginator;
import com.gym.enums.AccountCycleEnrollmentStatus;
import com.gym.exception.AccountCycleEnrollmentAlreadyExistsException;
import com.gym.model.Cycle;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class CycleService {
    private final CycleDao cycleDao;
    private final AccountCycleEnrollmentDao accountCycleEnrollmentDao;

    public CycleService(CycleDao cycleDao, AccountCycleEnrollmentDao accountCycleEnrollmentDao) {
        this.cycleDao = cycleDao;
        this.accountCycleEnrollmentDao = accountCycleEnrollmentDao;
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

    public Paginator<ActiveCycleListDto> getPaginatedActiveCyclesWithTrainer(Integer page, Integer size) {
        if (page == null || size == null || page < 1 || size < 1) {
            throw new IllegalArgumentException("Page and size must be greater than 0");
        }

        return new Paginator<>(
                cycleDao.getActiveCount(),
                page,
                size,
                cycleDao.getActiveCyclesWithTrainer(page, size)
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

    @Transactional(rollbackFor = SQLException.class)
    public void enrollCycle(long accountId, long cycleId) {
        if (accountId <= 0 || cycleId <= 0) {
            throw new IllegalArgumentException("Account ID and Cycle ID must be greater than 0");
        }
        if (accountCycleEnrollmentDao.existsByAccountIdAndCycleId(accountId, cycleId)) {
            throw new AccountCycleEnrollmentAlreadyExistsException(
                    "Account is already enrolled in cycle" + cycleId
            );
        }

        cycleDao.enrollCycle(accountId, cycleId);
    }

    public List<CycleWithEnrollmentDto> getCyclesWithEnrollmentsByAccountIdAndStatus(long accountId, AccountCycleEnrollmentStatus status) {
        if (accountId <= 0) {
            throw new IllegalArgumentException("Account ID must be greater than 0");
        }
        return cycleDao.getCyclesWithEnrollmentsByAccountIdAndStatus(accountId, status != null ? status.name() : null);
    }

    @Transactional(rollbackFor = SQLException.class)
    public void assignTrainerToCycle(long cycleId, Long trainerId) {
        if (cycleId <= 0) {
            throw new IllegalArgumentException("Cycle ID must be greater than 0");
        }
        if (trainerId != null && trainerId <= 0) {
            throw new IllegalArgumentException("Trainer ID must be greater than 0 if provided");
        }
        cycleDao.assignTrainerToCycle(cycleId, trainerId);
    }

    public Paginator<CycleForTrainerList> getPaginatedCyclesByTrainerId(Integer page, Integer size, long trainerId) {
        if (page == null || size == null || page < 1 || size < 1) {
            throw new IllegalArgumentException("Page and size must be greater than 0");
        }
        return new Paginator<>(
                cycleDao.getActiveCycleByTrainerIdCount(trainerId),
                page,
                size,
                cycleDao.getCyclesByTrainerId(page, size, trainerId)
        );
    }
}

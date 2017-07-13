package com.example.demo.repository;

import com.example.demo.domain.AccountOperationsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface AccountOperationsRepository extends JpaRepository<AccountOperationsEntity, Long> {
    @Query("select e from AccountOperationsEntity e where e.account.personalId = :userId")
    List<AccountOperationsEntity> findByUserId(@Param("userId") Long userId);

    @Query("select case when count(e) >= 5 then false else true end " +
            "   from AccountOperationsEntity e " +
            "       where e.account.personalId = :userId and e.operationTime between :startTime and :endTime and e.countryCode = :countryCode")
    Boolean isValidOperationsCountPerTime(@Param("userId") Long userId,
                                          @Param("startTime") Date startTime,
                                          @Param("endTime") Date endTime,
                                          @Param("countryCode") String countryCode);
}

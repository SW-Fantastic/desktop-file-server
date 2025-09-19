package org.swdc.rmdisk.core.repo;

import org.swdc.data.JPARepository;
import org.swdc.data.anno.*;
import org.swdc.rmdisk.core.entity.Activity;
import org.swdc.rmdisk.core.entity.ActivityType;
import org.swdc.rmdisk.core.repo.filters.FilteredActivityQueryCountFactory;
import org.swdc.rmdisk.core.repo.filters.FilteredActivityQueryFactory;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ActivityRepo extends JPARepository<Activity,Long> {

    @SQLQueryFactory(FilteredActivityQueryFactory.class)
    List<Activity> searchByFilters(
            @Param("keyword")String keyword,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("operation") ActivityType op,
            @Param("pageNo") Integer pageNo,
            @Param("pageSize") Integer pageSize
    );

    @SQLQueryFactory(FilteredActivityQueryCountFactory.class)
    Long countByFilters(
            @Param("keyword")String keyword,
            @Param("start")LocalDate start,
            @Param("end") LocalDate end,
            @Param("operation") ActivityType op
    );

    @Modify
    @SQLQuery("delete from Activity where user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

}

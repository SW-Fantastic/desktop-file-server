package org.swdc.rmdisk.core.repo;

import org.swdc.data.JPARepository;
import org.swdc.data.anno.Param;
import org.swdc.data.anno.Repository;
import org.swdc.data.anno.SQLQuery;
import org.swdc.data.anno.SQLQueryFactory;
import org.swdc.rmdisk.core.entity.CommonState;
import org.swdc.rmdisk.core.entity.State;
import org.swdc.rmdisk.core.entity.UserRegisterRequest;
import org.swdc.rmdisk.core.repo.filters.FilteredRegisterQueryCountFactory;
import org.swdc.rmdisk.core.repo.filters.FilteredRegisterQueryFactory;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserRegisterRequestRepo extends JPARepository<UserRegisterRequest,Long> {

    @SQLQuery("FROM UserRegisterRequest WHERE name = :name and state != 'REJECTED' order by createdOn desc")
    UserRegisterRequest findByName(@Param("name") String name);

    @SQLQuery("From UserRegisterRequest WHERE state = :state order by createdOn desc")
    List<UserRegisterRequest> findByState(@Param("state") CommonState state);

    @SQLQueryFactory(FilteredRegisterQueryFactory.class)
    List<UserRegisterRequest> searchByFilters(
            @Param("keyword")String keyword,
            @Param("start")LocalDate start,
            @Param("end") LocalDate end,
            @Param("state") CommonState state,
            @Param("pageNo") Integer pageNo,
            @Param("pageSize") Integer pageSize
    );

    @SQLQueryFactory(FilteredRegisterQueryCountFactory.class)
    Long countByFilters(
            @Param("keyword")String keyword,
            @Param("start")LocalDate start,
            @Param("end") LocalDate end,
            @Param("state") CommonState state
    );

}

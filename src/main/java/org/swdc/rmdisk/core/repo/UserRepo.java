package org.swdc.rmdisk.core.repo;

import org.swdc.data.JPARepository;
import org.swdc.data.anno.Param;
import org.swdc.data.anno.Repository;
import org.swdc.data.anno.SQLQuery;
import org.swdc.data.anno.SQLQueryFactory;
import org.swdc.rmdisk.core.entity.User;
import org.swdc.rmdisk.core.repo.filters.FilteredUserQueryCountFactory;
import org.swdc.rmdisk.core.repo.filters.FilteredUserQueryFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserRepo extends JPARepository<User,Long> {

    @SQLQuery("From User where username = :name and state = 'NORMAL'")
    User findByUserName(@Param("name")String userName);

    @SQLQuery("count(0) From User where group.id = :groupId")
    Long countByGroup(@Param("groupId")Long groupId);

    @SQLQuery("From User where group.id = :groupId")
    List<User> findByGroup(@Param("groupId")Long group);

    @SQLQuery("From User where state = 'NORMAL' and permissions = 'SUPER_ADMIN'")
    List<User> findSuperAdmins();

    @SQLQueryFactory(FilteredUserQueryFactory.class)
    List<User> searchByFilters(
            @Param("keyword")String keyword,
            @Param("start") LocalDateTime start,
            @Param("end")LocalDateTime end,
            @Param("groupId")Long groupId,
            @Param("pageNo")Integer pageNo,
            @Param("pageSize")Integer pageSize
    );

    @SQLQueryFactory(FilteredUserQueryCountFactory.class)
    Long countByFilters(
            @Param("keyword")String keyword,
            @Param("start")LocalDateTime start,
            @Param("end")LocalDateTime end,
            @Param("groupId")Long groupId
    );

}

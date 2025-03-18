package org.swdc.rmdisk.core.repo;

import org.swdc.data.JPARepository;
import org.swdc.data.anno.Param;
import org.swdc.data.anno.Repository;
import org.swdc.data.anno.SQLQuery;
import org.swdc.rmdisk.core.entity.State;
import org.swdc.rmdisk.core.entity.User;
import org.swdc.rmdisk.core.entity.UserGroup;

import java.util.List;

@Repository
public interface UserGroupRepo extends JPARepository<UserGroup,Long> {

    @SQLQuery("FROM UserGroup where groupName = :name and state = 'NORMAL'")
    UserGroup findByName(@Param("name")String name);

    @SQLQuery("FROM UserGroup where state = :state")
    List<UserGroup> findGroupByState(@Param("state")String state);

    @SQLQuery("FROM UserGroup where state = 'NORMAL' and registrable = true")
    List<UserGroup> findRegistrable();

}

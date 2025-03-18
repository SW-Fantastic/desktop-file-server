package org.swdc.rmdisk.core.repo;

import org.swdc.data.JPARepository;
import org.swdc.data.anno.Param;
import org.swdc.data.anno.Repository;
import org.swdc.data.anno.SQLQuery;
import org.swdc.rmdisk.core.entity.DiskFolder;

import java.util.List;

@Repository
public interface DiskFolderRepo extends JPARepository<DiskFolder,Long> {

    @SQLQuery("from DiskFolder where parent is null and owner.id = :ownerId")
    DiskFolder getUserRootFolder(@Param("ownerId") Long userId);

    @SQLQuery("from DiskFolder where parent.id = :parent and name = :name")
    DiskFolder getByParentAndName(@Param("parent") Long parent, @Param("name")String name);

    @SQLQuery("from DiskFolder where parent.id = :id")
    List<DiskFolder> getByParent(@Param("id")Long parentId);

}

package org.swdc.rmdisk.core.repo;

import org.swdc.data.JPARepository;
import org.swdc.data.anno.Param;
import org.swdc.data.anno.Repository;
import org.swdc.data.anno.SQLQuery;
import org.swdc.rmdisk.core.entity.DiskFile;

import java.util.List;

@Repository
public interface DiskFileRepo extends JPARepository<DiskFile,Long> {

    @SQLQuery("from DiskFile where parent.id = :parentId and name = :name")
    DiskFile findByFolderAndName(@Param("parentId")Long parentId, @Param("name") String name);

    @SQLQuery("from DiskFile where parent.id = :folderId")
    List<DiskFile> getFilesByParent(@Param("folderId")Long parentId);

}

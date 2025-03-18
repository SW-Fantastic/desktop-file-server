package org.swdc.rmdisk.core.repo;

import org.swdc.data.JPARepository;
import org.swdc.data.anno.Param;
import org.swdc.data.anno.Repository;
import org.swdc.data.anno.SQLQuery;
import org.swdc.rmdisk.core.entity.TemplateFolder;

@Repository
public interface TemplateFolderRepo extends JPARepository<TemplateFolder,Long> {

    @SQLQuery("from TemplateFolder where group.id = :groupId")
    TemplateFolder findRootByGroup(@Param("groupId")Long groupId);

    @SQLQuery("from TemplateFolder where parent.id = :parentId and name = :name")
    TemplateFolder findTemplateFolderByName(@Param("parentId")Long parentId, @Param("name") String name);

}

package org.swdc.rmdisk.core;

import org.swdc.data.EMFProvider;
import org.swdc.rmdisk.core.entity.*;

import java.util.Arrays;
import java.util.List;

public class EMFactory extends EMFProvider {
    @Override
    public List<Class> registerEntities() {
        return Arrays.asList(
                User.class,
                UserGroup.class,
                UserRegisterRequest.class,
                TemplateFolder.class,
                DiskFolder.class,
                DiskFile.class
        );
    }
}

package org.swdc.rmdisk;

import java.io.IOException;

public class Launcher {

    public static void main(String[] args) throws IOException {
        RmDiskServiceApplication application = new RmDiskServiceApplication();
        application.applicationLaunch(args);
    }

}

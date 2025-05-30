package org.swdc.rmdisk;
import java.io.IOException;

public class Launcher {


    public static void main(String[] args) throws IOException {

        if ( args.length > 0 ) {
            // 纯Server环境的CLI启动流程，直接加载依赖注入环境，启动Service不使用Javafx。
            RmDiskHeadlessApplication headlessApplication = new RmDiskHeadlessApplication();
            headlessApplication.launch(args);
        } else {
            // 正常桌面环境启动。
            RmDiskServiceApplication application = new RmDiskServiceApplication();
            application.applicationLaunch(args);
        }

    }


}

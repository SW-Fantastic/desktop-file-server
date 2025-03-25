package org.swdc.rmdisk.views;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import org.controlsfx.control.PropertySheet;
import org.swdc.fx.FXResources;
import org.swdc.fx.config.ConfigViews;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.rmdisk.RmDiskApplicationConfig;
import org.swdc.rmdisk.core.LanguageKeys;
import org.swdc.rmdisk.core.ServerConfigure;

import java.util.ResourceBundle;

@View(
        viewLocation = "views/main/ServerConfigureView.fxml",
        title = LanguageKeys.UI_CONFIG_TITLE,
        resizeable = false
)
public class ServerConfigureView extends AbstractView {

    @Inject
    private FXResources resources;

    @Inject
    private ServerConfigure serverConfigure;

    @Inject
    private RmDiskApplicationConfig regularConfig;

    private PropertySheet generalConfSheet;

    private PropertySheet serverConfSheet;

    @PostConstruct
    public void init() {

        ResourceBundle bundle = resources.getResourceBundle();

        BorderPane root = (BorderPane) getView();
        TabPane tabPane = (TabPane) root.getCenter();

        generalConfSheet = new PropertySheet();
        generalConfSheet.setPropertyEditorFactory(ConfigViews.factory(resources));
        generalConfSheet.setModeSwitcherVisible(false);
        generalConfSheet.setSearchBoxVisible(false);
        generalConfSheet.getStyleClass().add("prop-sheets");

        Tab tab = new Tab(bundle.getString(LanguageKeys.SERVER_CONFIG_TAB_GENERAL));
        tab.setContent(generalConfSheet);
        tabPane.getTabs().add(tab);

        serverConfSheet = new PropertySheet();
        serverConfSheet.setPropertyEditorFactory(ConfigViews.factory(resources));
        serverConfSheet.setModeSwitcherVisible(false);
        serverConfSheet.setSearchBoxVisible(false);
        serverConfSheet.getStyleClass().add("prop-sheets");

        Tab tabServer = new Tab(bundle.getString(LanguageKeys.SERVER_CONFIG_TAB_SERVER));
        tabServer.setContent(serverConfSheet);
        tabPane.getTabs().add(tabServer);

        reload();

    }

    public void reload() {

        ObservableList itemsServer = serverConfSheet.getItems();
        ObservableList confServers = ConfigViews.parseConfigs(resources,serverConfigure);
        itemsServer.clear();
        itemsServer.addAll(confServers);

        ObservableList itemsGeneral = generalConfSheet.getItems();
        ObservableList confGenerals = ConfigViews.parseConfigs(resources,regularConfig);
        itemsGeneral.clear();
        itemsGeneral.addAll(confGenerals);

    }

    @Override
    public void show() {
        reload();
        super.show();
    }
}

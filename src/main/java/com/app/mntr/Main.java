package main.java.com.app.mntr;

import com.zaxxer.hikari.HikariDataSource;
import main.java.com.app.mntr.api.Config;
import main.java.com.app.mntr.api.Property;
import main.java.com.app.mntr.api.custom.MetaConfigCustom;
import main.java.com.app.mntr.api.custom.engine.Server;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) throws IOException {
        final MetaConfigCustom metaConfig = metaConfigCustom();
    }

    private static DataSource getDataSource() {
        final HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setJdbcUrl("jdbc:h2:mem:test");
        dataSource.setUsername("SA");
        dataSource.setPassword("");
        return dataSource;
    }

    public static MetaConfigCustom metaConfigCustom() {
        final HashMap<String, String> dataMapping = new HashMap<>();
        dataMapping.put(Constants.Mapping.CONFIGS_TABLE, "CONFIGS");
        // Create the web server config
        final Config webServer = new Config.Builder(Server.Settings.CONFIG_NAME,
                Arrays.asList(
                        new Property.Builder(Server.Settings.HOSTNAME, "0.0.0.0").build(),
                        new Property.Builder(Constants.Endpoints.DATA, "data").build(),
                        new Property.Builder(Server.Settings.PORT, 8080).build(),
                        new Property.Builder(Server.Settings.BACKLOG, 0).build(),
                        new Property.Builder(Server.Settings.KEY_STORE_FILE, "./data/metacfg4j.keystore").build(),
                        new Property.Builder(Server.Settings.ALIAS, "alias").build(),
                        new Property.Builder(Server.Settings.STORE_PASSWORD, "password").build(),
                        new Property.Builder(Server.Settings.KEY_PASSWORD, "password").build())
        ).build();
        // Create the meta configuration
        return new MetaConfigCustom.Builder().
                webServer(webServer).
                dataSource(getDataSource()).
                dataMapping(dataMapping).
                build();
    }
}

package de.abas.esdk.g30l0;

import de.abas.erp.db.DbContext;
import de.abas.erp.db.util.ContextHelper;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class GeolocationInfosystemTest {

	private DbContext ctx = createClientContext();

	@Test
	public void helloTestingWorld() {
		ctx.out().println("Hello, Testing World!");
	}

	private DbContext createClientContext() {
		Properties properties = new Properties();
		File file = new File("gradle.properties");
		try {
			properties.load(new FileReader(file));
			String host = properties.getProperty("EDP_HOST");
			int port = Integer.parseInt(properties.getProperty("EDP_PORT"));
			String password = properties.getProperty("EDP_PASSWORD");
			String clientDir = properties.getProperty("ABAS_CLIENTDIR");
			return ContextHelper.createClientContext(host, port, clientDir, password, "Integration Tests");
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Could not find file '" + file.getAbsolutePath() + "'.\n" +
					"This file can be generated by executing './initGradleProperties.sh'.");
		} catch (IOException e) {
			throw new RuntimeException("Error while reading file '" + file.getAbsolutePath() + "'.\n" + e.getMessage());
		}
	}

}

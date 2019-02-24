package de.abas.esdk.g30l0;

import de.abas.erp.db.DbContext;
import de.abas.erp.db.Deletable;
import de.abas.erp.db.Query;
import de.abas.erp.db.schema.customer.Customer;
import de.abas.erp.db.schema.customer.CustomerContact;
import de.abas.erp.db.schema.customer.CustomerContactEditor;
import de.abas.erp.db.schema.customer.CustomerEditor;
import de.abas.erp.db.schema.referencetypes.TradingPartner;
import de.abas.erp.db.schema.referencetypes.TradingPartnerEditor;
import de.abas.erp.db.schema.vendor.Vendor;
import de.abas.erp.db.schema.vendor.VendorContact;
import de.abas.erp.db.schema.vendor.VendorContactEditor;
import de.abas.erp.db.schema.vendor.VendorEditor;
import de.abas.erp.db.selection.Conditions;
import de.abas.erp.db.selection.SelectionBuilder;
import de.abas.erp.db.util.ContextHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import static de.abas.esdk.g30l0.AbstractTest.TestData.*;

public abstract class AbstractTest {

	public static DbContext ctx = createClientContext();

	@BeforeClass
	public static void prepare() {
		createTestData(CustomerEditor.class, CUSTOMER);
		createTestData(VendorEditor.class, VENDOR);
		createTestData(CustomerContactEditor.class, CUSTOMER_CONTACT);
		createTestData(VendorContactEditor.class, VENDOR_CONTACT);
		createTestData(CustomerEditor.class, INVALID);
	}

	private static <T extends TradingPartnerEditor> void createTestData(Class<T> clazz, GeolocationInfosystemTest.TestData testData) {
		T editor = ctx.newObject(clazz);
		if (editor instanceof CustomerContactEditor) {
			((CustomerContactEditor) editor).setCompanyARAP((Customer) CUSTOMER.tradingPartner);
		}
		if (editor instanceof VendorContactEditor) {
			((VendorContactEditor) editor).setCompanyARAP((Vendor) VENDOR.tradingPartner);
		}
		editor.setSwd(testData.swd);
		editor.setZipCode(testData.zipCode);
		editor.setTown(testData.town);
		editor.commit();
		testData.tradingPartner = editor;
	}

	@AfterClass
	public static void cleanup() {
		deleteTestData(Customer.class, CUSTOMER);
		deleteTestData(Vendor.class, VENDOR);
		deleteTestData(CustomerContact.class, CUSTOMER_CONTACT);
		deleteTestData(VendorContact.class, VENDOR_CONTACT);
		deleteTestData(Customer.class, INVALID);
		ctx.close();
	}

	private static <T extends TradingPartner & Deletable> void deleteTestData(Class<T> clazz, GeolocationInfosystemTest.TestData testData) {
		Query<T> tradingPartners = ctx.createQuery(SelectionBuilder.create(clazz).add(Conditions.eq(T.META.swd, testData.swd)).build());
		for (final T tradingPartner : tradingPartners) {
			tradingPartner.delete();
		}
	}

	private static DbContext createClientContext() {
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

	public enum TestData {
		CUSTOMER("G30L0CUS", "67165", "Waldsee"),
		VENDOR("G30L0VEN", "76135", "Karlsruhe"),
		CUSTOMER_CONTACT("G30L0CCO", "67346", "Speyer"),
		VENDOR_CONTACT("G30L0VCO", "76227", "Karlsruhe"),
		INVALID("G30L0INV", "invalid", "invalid");

		String swd;
		String zipCode;
		String town;
		TradingPartner tradingPartner;

		TestData(String swd, String zipCode, String town) {
			this.swd = swd;
			this.zipCode = zipCode;
			this.town = town;
		}

	}

}
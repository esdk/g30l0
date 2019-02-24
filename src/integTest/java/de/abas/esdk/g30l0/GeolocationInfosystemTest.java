package de.abas.esdk.g30l0;

import de.abas.erp.db.DbContext;
import de.abas.erp.db.Deletable;
import de.abas.erp.db.Query;
import de.abas.erp.db.infosystem.custom.ow1.GeoLocation;
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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import static de.abas.esdk.g30l0.GeolocationInfosystemTest.TestData.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

public class GeolocationInfosystemTest<T extends TradingPartner & Deletable> {

	private static DbContext ctx = createClientContext();

	public GeoLocation infosystem = ctx.openInfosystem(GeoLocation.class);

	@BeforeClass
	public static void prepare() {
		createTestData(CustomerEditor.class, CUSTOMER);
		createTestData(VendorEditor.class, VENDOR);
		createTestData(CustomerContactEditor.class, CUSTOMER_CONTACT);
		createTestData(VendorContactEditor.class, VENDOR_CONTACT);
		createTestData(CustomerEditor.class, INVALID);
	}

	private static <T extends TradingPartnerEditor> void createTestData(Class<T> clazz, TestData testData) {
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

	@Test
	public void canDisplayCustomerInfo() {
		infosystem.setCustomersel(CUSTOMER.swd);
		infosystem.invokeStart();

		assertInfosystemTableContains(CUSTOMER);
	}

	@Test
	public void canDisplayVendorInfo() {
		infosystem.setCustomersel(VENDOR.swd);
		infosystem.invokeStart();

		assertInfosystemTableContains(VENDOR);
	}

	@Test
	public void canDisplayCustomerContactInfo() {
		infosystem.setCustomersel(CUSTOMER_CONTACT.swd);
		infosystem.invokeStart();

		assertInfosystemTableContains(CUSTOMER_CONTACT);
	}

	@Test
	public void canDisplayVendorContactInfo() {
		infosystem.setCustomersel(VENDOR_CONTACT.swd);
		infosystem.invokeStart();

		assertInfosystemTableContains(VENDOR_CONTACT);
	}

	@Test
	public void canSelectBasedOnZipCode() {
		infosystem.setZipcodesel(CUSTOMER.zipCode);
		infosystem.invokeStart();

		assertInfosystemTableContains(CUSTOMER);
	}

	@Test
	public void canSelectBasedOnZipCodeAndSwd() {
		infosystem.setCustomersel(VENDOR.swd);
		infosystem.setZipcodesel(CUSTOMER.zipCode);
		infosystem.invokeStart();

		assertInfosystemTableContains(2, CUSTOMER, VENDOR);
	}

	@Test
	public void canDisplayGeolocation() {
		infosystem.setCustomersel(CUSTOMER.swd);
		infosystem.invokeStart();

		assertThat(infosystem.table().getRow(1).getCustomer().getSwd(), is(CUSTOMER.swd));
		assertThat(Double.valueOf(infosystem.table().getRow(1).getLatitude()), is(closeTo(49.3953008, 0.1)));
		assertThat(Double.valueOf(infosystem.table().getRow(1).getLongitude()), is(closeTo(8.440276, 0.1)));
	}

	@Test
	public void displaysEmptyStringForInvalidAddress() {
		infosystem.setCustomersel(INVALID.swd);
		infosystem.invokeStart();

		assertThat(infosystem.table().getRow(1).getCustomer().getSwd(), is(INVALID.swd));
		assertThat(infosystem.table().getRow(1).getLatitude(), is(""));
		assertThat(infosystem.table().getRow(1).getLongitude(), is(""));
	}

	private void assertInfosystemTableContains(int expectedRowCount, final TestData... testData) {
		assertThat(infosystem.table().getRowCount(), is(expectedRowCount));
		for (int i = 0; i < testData.length; i++) {
			int rowNo = i + 1;
			assertThat(infosystem.table().getRow(rowNo).getCustomer(), is(notNullValue()));
			assertThat(infosystem.table().getRow(rowNo).getCustomer().getSwd(), is(testData[i].swd));
			assertThat(infosystem.table().getRow(rowNo).getZipcode(), is(testData[i].zipCode));
			assertThat(infosystem.table().getRow(rowNo).getTown(), is(testData[i].town));
		}
	}

	private void assertInfosystemTableContains(final TestData testData) {
		assertInfosystemTableContains(1, testData);
	}

	@After
	public void tidyup() {
		infosystem.abort();
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

	private static <T extends TradingPartner & Deletable> void deleteTestData(Class<T> clazz, TestData testData) {
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

	enum TestData {
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

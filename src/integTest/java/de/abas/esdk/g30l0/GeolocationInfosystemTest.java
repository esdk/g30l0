package de.abas.esdk.g30l0;

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
import de.abas.esdk.test.util.EsdkIntegTest;
import de.abas.esdk.test.util.TestData;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static de.abas.esdk.g30l0.GeolocationInfosystemTest.TestingData.CUSTOMER;
import static de.abas.esdk.g30l0.GeolocationInfosystemTest.TestingData.CUSTOMER_CONTACT;
import static de.abas.esdk.g30l0.GeolocationInfosystemTest.TestingData.VENDOR;
import static de.abas.esdk.g30l0.GeolocationInfosystemTest.TestingData.VENDOR_CONTACT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

public class GeolocationInfosystemTest extends EsdkIntegTest {

	public GeoLocation infosystem = ctx.openInfosystem(GeoLocation.class);

	@BeforeClass
	public static void prepare() {
		createTestData(CustomerEditor.class, CUSTOMER);
		createTestData(VendorEditor.class, VENDOR);
		createTestData(CustomerContactEditor.class, CUSTOMER_CONTACT);
		createTestData(VendorContactEditor.class, VENDOR_CONTACT);
	}

	private static <T extends TradingPartnerEditor> void createTestData(Class<T> clazz, TestingData testData) {
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

	private void assertInfosystemTableContains(final TestingData vendor) {
		assertThat(infosystem.table().getRowCount(), is(1));
		assertThat(infosystem.table().getRow(1).getCustomer(), is(notNullValue()));
		assertThat(infosystem.table().getRow(1).getCustomer().getSwd(), is(vendor.swd));
		assertThat(infosystem.table().getRow(1).getZipcode(), is(vendor.zipCode));
		assertThat(infosystem.table().getRow(1).getTown(), is(vendor.town));
	}

	@After
	public void tidyup() {
		infosystem.abort();
	}

	@AfterClass
	public static void cleanup() {
		TestData.deleteData(ctx, Customer.class, Customer.META.swd, CUSTOMER.swd);
		TestData.deleteData(ctx, Vendor.class, Vendor.META.swd, VENDOR.swd);
		TestData.deleteData(ctx, CustomerContact.class, CustomerContact.META.swd, CUSTOMER_CONTACT.swd);
		TestData.deleteData(ctx, VendorContact.class, VendorContact.META.swd, VENDOR_CONTACT.swd);
	}

	enum TestingData {
		CUSTOMER("G30L0CUS", "67165", "Waldsee"),
		VENDOR("G30L0VEN", "76135", "Karlsruhe"),
		CUSTOMER_CONTACT("G30L0CCO", "67346", "Speyer"),
		VENDOR_CONTACT("G30L0VCO", "76227", "Karlsruhe");

		String swd;
		String zipCode;
		String town;
		TradingPartner tradingPartner;

		TestingData(String swd, String zipCode, String town) {
			this.swd = swd;
			this.zipCode = zipCode;
			this.town = town;
		}

	}

}

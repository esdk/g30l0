package de.abas.esdk.g30l0;

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
import org.junit.AfterClass;
import org.junit.BeforeClass;

import static de.abas.esdk.g30l0.AbstractTest.TestingData.CUSTOMER;
import static de.abas.esdk.g30l0.AbstractTest.TestingData.CUSTOMER_CONTACT;
import static de.abas.esdk.g30l0.AbstractTest.TestingData.INVALID;
import static de.abas.esdk.g30l0.AbstractTest.TestingData.VENDOR;
import static de.abas.esdk.g30l0.AbstractTest.TestingData.VENDOR_CONTACT;

public abstract class AbstractTest extends EsdkIntegTest {

	@BeforeClass
	public static void prepare() {
		createTestData(CustomerEditor.class, CUSTOMER);
		createTestData(VendorEditor.class, VENDOR);
		createTestData(CustomerContactEditor.class, CUSTOMER_CONTACT);
		createTestData(VendorContactEditor.class, VENDOR_CONTACT);
		createTestData(CustomerEditor.class, INVALID);
	}

	private static <T extends TradingPartnerEditor> void createTestData(Class<T> clazz, GeolocationInfosystemTest.TestingData testData) {
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
		TestData.deleteData(ctx, Customer.class, Customer.META.swd, CUSTOMER.swd);
		TestData.deleteData(ctx, Vendor.class, Vendor.META.swd, VENDOR.swd);
		TestData.deleteData(ctx, CustomerContact.class, CustomerContact.META.swd, CUSTOMER_CONTACT.swd);
		TestData.deleteData(ctx, VendorContact.class, VendorContact.META.swd, VENDOR_CONTACT.swd);
		TestData.deleteData(ctx, Customer.class, Customer.META.swd, INVALID.swd);
	}

	enum TestingData {
		CUSTOMER("G30L0CUS", "67165", "Waldsee"),
		VENDOR("G30L0VEN", "76135", "Karlsruhe"),
		CUSTOMER_CONTACT("G30L0CCO", "67346", "Speyer"),
		VENDOR_CONTACT("G30L0VCO", "76227", "Karlsruhe"),
		INVALID("G30L0INV", "invalid", "invalid");

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

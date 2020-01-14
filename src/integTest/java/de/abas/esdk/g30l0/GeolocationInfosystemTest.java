package de.abas.esdk.g30l0;

import de.abas.erp.db.infosystem.custom.ow1.GeoLocation;
import de.abas.erp.db.schema.customer.Customer;
import de.abas.erp.db.schema.customer.CustomerEditor;
import de.abas.esdk.test.util.EsdkIntegTest;
import de.abas.esdk.test.util.TestData;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static de.abas.esdk.g30l0.GeolocationInfosystemTest.TestingData.CUSTOMER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

public class GeolocationInfosystemTest extends EsdkIntegTest {

	public GeoLocation infosystem = ctx.openInfosystem(GeoLocation.class);

	@BeforeClass
	public static void createTestData() {
		CustomerEditor customerEditor = ctx.newObject(CustomerEditor.class);
		customerEditor.setSwd(CUSTOMER.swd);
		customerEditor.setZipCode(CUSTOMER.zipCode);
		customerEditor.setTown(CUSTOMER.town);
		customerEditor.commit();
	}

	@Test
	public void canDisplayCustomerInfo() {
		infosystem.setCustomersel(CUSTOMER.swd);
		infosystem.invokeStart();

		assertThat(infosystem.table().getRowCount(), is(1));
		assertThat(infosystem.table().getRow(1).getCustomer(), is(notNullValue()));
		assertThat(infosystem.table().getRow(1).getCustomer().getSwd(), is(CUSTOMER.swd));
		assertThat(infosystem.table().getRow(1).getZipcode(), is(CUSTOMER.zipCode));
		assertThat(infosystem.table().getRow(1).getTown(), is(CUSTOMER.town));
	}

	@After
	public void tidyup() {
		infosystem.abort();
	}

	@AfterClass
	public static void cleanup() {
		TestData.deleteData(ctx, Customer.class, Customer.META.swd, CUSTOMER.swd);
	}

	enum TestingData {
		CUSTOMER("G30L0CUS", "67165", "Waldsee");

		String swd;
		String zipCode;
		String town;

		TestingData(String swd, String zipCode, String town) {
			this.swd = swd;
			this.zipCode = zipCode;
			this.town = town;
		}

	}

}

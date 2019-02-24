package de.abas.esdk.g30l0;

import de.abas.erp.db.infosystem.custom.ow1.GeoLocation;
import org.junit.After;
import org.junit.Test;

import static de.abas.esdk.g30l0.AbstractTest.TestingData.CUSTOMER;
import static de.abas.esdk.g30l0.AbstractTest.TestingData.CUSTOMER_CONTACT;
import static de.abas.esdk.g30l0.AbstractTest.TestingData.INVALID;
import static de.abas.esdk.g30l0.AbstractTest.TestingData.VENDOR;
import static de.abas.esdk.g30l0.AbstractTest.TestingData.VENDOR_CONTACT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

public class GeolocationInfosystemTest extends AbstractTest {

	public GeoLocation infosystem = ctx.openInfosystem(GeoLocation.class);

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

	private void assertInfosystemTableContains(int expectedRowCount, final TestingData... testData) {
		assertThat(infosystem.table().getRowCount(), is(expectedRowCount));
		for (int i = 0; i < testData.length; i++) {
			int rowNo = i + 1;
			assertThat(infosystem.table().getRow(rowNo).getCustomer(), is(notNullValue()));
			assertThat(infosystem.table().getRow(rowNo).getCustomer().getSwd(), is(testData[i].swd));
			assertThat(infosystem.table().getRow(rowNo).getZipcode(), is(testData[i].zipCode));
			assertThat(infosystem.table().getRow(rowNo).getTown(), is(testData[i].town));
		}
	}

	private void assertInfosystemTableContains(final TestingData testData) {
		assertInfosystemTableContains(1, testData);
	}

	@After
	public void tidyup() {
		infosystem.abort();
	}

}

package de.abas.esdk.g30l0;

import de.abas.erp.db.schema.referencetypes.TradingPartner;
import de.abas.erp.db.schema.regions.RegionCountryEconomicArea;
import fr.dudie.nominatim.model.Address;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest(OpenStreetMapGeolocationResolver.class)
@RunWith(PowerMockRunner.class)
public class GeolocationResolverTest {

	private GeolocationResolver resolver = PowerMockito.spy(new OpenStreetMapGeolocationResolver());

	@Test
	public void canResolveLocation() throws Exception {
		TradingPartner tradingPartner = getTradingPartner("Gartenstraße 67", "76135", "Karlsruhe", "DEUTSCHLAND");
		Address address = new Address();
		address.setLatitude(49.0049809);
		address.setLongitude(8.3839609);
		List<Address> addresses = new ArrayList<>();
		addresses.add(address);
		doReturn(addresses).when(resolver, "resolveFromOpenStreetMaps", tradingPartner);
		Geolocation geolocation = resolver.resolve(tradingPartner);
		assertThat(Double.valueOf(geolocation.getLatitude()), is(closeTo(49.0039809, 0.001)));
		assertThat(Double.valueOf(geolocation.getLongitude()), is(closeTo(8.3849609, 0.001)));
	}

	@Test
	public void returnsNullForLatitudeAndLongitudeIfAddressIsInvalid() throws Exception {
		TradingPartner tradingPartner = getTradingPartner("invalid", "invalid", "does not exist", "UNITED STATES");
		List<Address> addresses = new ArrayList<>();
		doReturn(addresses).when(resolver, "resolveFromOpenStreetMaps", tradingPartner);
		Geolocation geolocation = resolver.resolve(tradingPartner);
		assertThat(geolocation.getLatitude(), is(""));
		assertThat(geolocation.getLongitude(), is(""));
	}

	@Test
	public void canHandleIOExceptionDuringGeolocationResolution() throws Exception {
		TradingPartner tradingPartner = getTradingPartner("invalid", "invalid", "does not exist", "UNITED STATES");
		doThrow(new IOException("Simulating IOException")).when(resolver, "resolveFromOpenStreetMaps", tradingPartner);
		Geolocation geolocation = resolver.resolve(tradingPartner);
		assertThat(geolocation.getLatitude(), is(""));
		assertThat(geolocation.getLongitude(), is(""));
	}

	private TradingPartner getTradingPartner(String street, String zipCode, String town, String country) {
		TradingPartner tradingPartner = mock(TradingPartner.class);
		when(tradingPartner.getStreet()).thenReturn(street);
		when(tradingPartner.getZipCode()).thenReturn(zipCode);
		when(tradingPartner.getTown()).thenReturn(town);
		RegionCountryEconomicArea regionCountryEconomicArea = getRegionCountryEconomicArea(country);
		when(tradingPartner.getStateOfTaxOffice()).thenReturn(regionCountryEconomicArea);
		return tradingPartner;
	}

	private RegionCountryEconomicArea getRegionCountryEconomicArea(String country) {
		RegionCountryEconomicArea regionCountryEconomicArea = mock(RegionCountryEconomicArea.class);
		when(regionCountryEconomicArea.getSwd()).thenReturn(country);
		return regionCountryEconomicArea;
	}

}

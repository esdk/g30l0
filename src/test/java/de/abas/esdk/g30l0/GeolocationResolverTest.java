package de.abas.esdk.g30l0;

import de.abas.erp.db.schema.referencetypes.TradingPartner;
import de.abas.erp.db.schema.regions.RegionCountryEconomicArea;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GeolocationResolverTest {

	private final GeolocationResolver resolver = new OpenStreetMapGeolocationResolver();

	@Test
	public void canResolveLocation() {
		TradingPartner tradingPartner = getTradingPartner("Gartenstraße 67", "76135", "Karlsruhe", "DEUTSCHLAND");
		Geolocation geolocation = resolver.resolve(tradingPartner);
		assertThat(geolocation.getLatitude(), is(closeTo(49.0039809, 0.001)));
		assertThat(geolocation.getLongitude(), is(closeTo(8.3849609, 0.001)));
	}

	@Test
	public void returnsNullForLatitudeAndLongitudeIfAddressIsInvalid() {
		TradingPartner tradingPartner = getTradingPartner("invalid", "invalid", "does not exist", "UNITED STATES");
		Geolocation geolocation = resolver.resolve(tradingPartner);
		assertThat(geolocation.getLatitude(), is(nullValue()));
		assertThat(geolocation.getLongitude(), is(nullValue()));
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

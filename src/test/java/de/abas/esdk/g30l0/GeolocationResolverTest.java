package de.abas.esdk.g30l0;

import de.abas.erp.db.schema.referencetypes.TradingPartner;
import de.abas.erp.db.schema.regions.RegionCountryEconomicArea;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GeolocationResolverTest {

	private final GeolocationResolver resolver = new OpenStreetMapGeolocationResolver();

	@Test
	public void canResolveLocation() {
		TradingPartner tradingPartner = mock(TradingPartner.class);
		when(tradingPartner.getStreet()).thenReturn("Gartenstraße 67");
		when(tradingPartner.getZipCode()).thenReturn("76135");
		when(tradingPartner.getTown()).thenReturn("Karlsruhe");
		RegionCountryEconomicArea germany = mock(RegionCountryEconomicArea.class);
		when(germany.getSwd()).thenReturn("DEUTSCHLAND");
		when(tradingPartner.getStateOfTaxOffice()).thenReturn(germany);
		Geolocation geolocation = resolver.resolve(tradingPartner);
		assertThat(geolocation.getLatitude(), is(49.0039809));
		assertThat(geolocation.getLongitude(), is(8.3849609));
	}

}

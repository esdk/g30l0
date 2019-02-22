package de.abas.esdk.g30l0;

import de.abas.erp.db.schema.referencetypes.TradingPartner;
import fr.dudie.nominatim.client.JsonNominatimClient;
import fr.dudie.nominatim.client.NominatimClient;
import fr.dudie.nominatim.model.Address;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;

public class OpenStreetMapGeolocationResolver implements GeolocationResolver {

	private static final Logger logger = Logger.getLogger(OpenStreetMapGeolocationResolver.class);

	@Override
	public Geolocation resolve(final TradingPartner tradingPartner) {
		NominatimClient jsonNominatimClient = new JsonNominatimClient(new DefaultHttpClient(), "scrumteamesdk@abas.de");
		Geolocation geolocation = new Geolocation();
		try {
			List<Address> addresses = jsonNominatimClient.search(tradingPartner.getStreet() + " " + tradingPartner.getZipCode() + " " + tradingPartner.getTown() + " " + tradingPartner.getStateOfTaxOffice().getSwd());
			if (addresses.size() > 0) {
				geolocation.setLatitude(addresses.get(0).getLatitude());
				geolocation.setLongitude(addresses.get(0).getLongitude());
			} else {
				logger.debug(String.format("No matches found for address '%s'", getFormattedAddress(tradingPartner)));
			}
		} catch (IOException e) {
			logger.error(String.format("Invalid address '%s': %s", getFormattedAddress(tradingPartner), e.getMessage()), e);
		}
		return geolocation;
	}

	private String getFormattedAddress(TradingPartner tradingPartner) {
		return String.format("%s, %s %s, %s", tradingPartner.getStreet(), tradingPartner.getZipCode(), tradingPartner.getTown(), tradingPartner.getStateOfTaxOffice().getSwd());
	}

}

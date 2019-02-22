package de.abas.esdk.g30l0;

import de.abas.erp.db.schema.referencetypes.TradingPartner;

public interface GeolocationResolver {
	Geolocation resolve(TradingPartner tradingPartner);
}

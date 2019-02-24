package de.abas.esdk.g30l0;

public class Geolocation {

	private Double latitude;
	private Double longitude;

	public String getLatitude() {
		return (latitude != null ? latitude.toString() : "");
	}

	public void setLatitude(final Double latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return (longitude != null ? longitude.toString() : "");
	}

	public void setLongitude(final Double longitude) {
		this.longitude = longitude;
	}

}

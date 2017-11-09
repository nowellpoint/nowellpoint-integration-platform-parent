package com.nowellpoint.api.resource;

import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.nowellpoint.api.rest.domain.IsoCountry;
import com.nowellpoint.api.rest.domain.IsoCountryList;
import com.nowellpoint.api.util.MessageConstants;
import com.nowellpoint.api.util.MessageProvider;

@Path("iso-countries")
public class IsoCountryResource {
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	@PermitAll
    public Response findAll() {
		Set<IsoCountry> countries = new HashSet<>();
		ResourceBundle bundle = ResourceBundle.getBundle("countries", Locale.getDefault());
		bundle.keySet().stream().forEach(key -> {
			IsoCountry country = IsoCountry.builder().iso2Code(key).name(bundle.getString(key)).build();
			countries.add(country);
		});
		IsoCountryList countryList = new IsoCountryList(countries);
		return Response.ok(countryList).build();
    }
	
	@GET
	@Path("q")
    @Produces(MediaType.APPLICATION_JSON)
	@PermitAll
    public Response findByLanguage(@QueryParam("language") String language) {
		Set<IsoCountry> countries = new HashSet<>();
		ResourceBundle bundle = ResourceBundle.getBundle("countries", Locale.forLanguageTag(language));
		bundle.keySet().stream().forEach(key -> {
			IsoCountry country = IsoCountry.builder().iso2Code(key).name(bundle.getString(key)).build();
			countries.add(country);
		});
		IsoCountryList countryList = new IsoCountryList(countries);
		return Response.ok(countryList).build();
    }
	
	@GET
	@Path("q")
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	public Response findByIsoCode(@QueryParam("language") String language, @QueryParam("iso2Code") String iso2Code) {
		ResourceBundle bundle = ResourceBundle.getBundle("countries", Locale.forLanguageTag(language));
		Optional<String> key = bundle.keySet().stream().filter(k -> iso2Code.equals(k)).findFirst();
		if (! key.isPresent()) {
			throw new NotFoundException(
					String.format(
							MessageProvider.getMessage(Locale.getDefault(), MessageConstants.INVALID_COUNTRY_PARAMETERS), language, iso2Code));
		}
		return Response.ok(bundle.getObject(key.get())).build();
	}
}
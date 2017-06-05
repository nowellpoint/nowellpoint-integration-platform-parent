package com.nowellpoint.api.resource;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.nowellpoint.api.rest.domain.IsoCountry;
import com.nowellpoint.api.rest.domain.IsoCountryList;
import com.nowellpoint.api.service.IsoCountryService;

@Path("iso-countries")
public class IsoCountryResource {
	
	@Inject
	private IsoCountryService isoCountryService;
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	@PermitAll
    public Response findAll() {
		IsoCountryList isoCountries = isoCountryService.findAll();
		return Response.ok(isoCountries).build();
    }
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	@PermitAll
    public Response findByLanguage(@QueryParam("language") String language) {
		IsoCountryList isoCountries = isoCountryService.findByLanguage(language);
		return Response.ok(isoCountries).build();
    }
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	public Response findByIsoCode(@QueryParam("language") String language, @QueryParam("iso2Code") String iso2Code) {
		IsoCountry isoCountry = isoCountryService.findByIso2CodeAndLanguage(iso2Code, language);
		return Response.ok(isoCountry).build();
	}
}
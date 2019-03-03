package com.nowellpoint.console.view;

import static spark.Spark.get;
import static spark.Spark.post;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import com.nowellpoint.console.entity.AggregationResult;
import com.nowellpoint.console.model.FeedItem;
import com.nowellpoint.console.model.Organization;
import com.nowellpoint.console.model.ProcessTemplateRequest;
import com.nowellpoint.console.model.StreamingEventListener;
import com.nowellpoint.console.model.StreamingEventListenerRequest;
import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.console.util.MessageProvider;
import com.nowellpoint.console.util.Path;
import com.nowellpoint.console.util.Templates;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class StreamingEventsController extends BaseController {
	
	public static void configureRoutes(Configuration configuration) {
		
		get(Path.Route.STREAMING_EVENTS, (request, response) 
				-> viewStreamingEvents(request, response));
		
		get(Path.Route.STREAMING_EVENTS_SOURCES, (request, response) 
				-> viewStreamingEventsSources(request, response));
		
		get(Path.Route.STREAMING_EVENTS_SETUP, (request, response)
				-> setupStreamingEvents(request, response));
		
		post(Path.Route.STREAMING_EVENTS_SETUP, (request, response)
				-> saveEventListener(request, response));
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String viewStreamingEvents(Request request, Response response) {
		
		String organizationId = getIdentity(request).getOrganization().getId();
		
		Organization organization = ServiceClient.getInstance()
				.organization()
				.get(organizationId);
		
		List<AggregationResult> results = ServiceClient.getInstance()
				.organization()
				.getEventsLastDays(organizationId, 7, TimeZone.getTimeZone(ZoneId.of( "UTC" )));
		
		String data = results.stream()
				.sorted(Comparator.reverseOrder())
				.map(r -> formatLabel(getIdentity(request).getLocale(), r))
				.collect(Collectors.joining(", "));
		
		List<FeedItem> feedItems = ServiceClient.getInstance()
				.organization()
				.getStreamingEventsFeed(organizationId, getIdentity(request).getTimeZone());
		
		Map<String,Object> model = getModel();
		model.put("organization", organization);
		model.put("feedItems", feedItems);
		model.put("data", data);
		
    	ProcessTemplateRequest templateProcessRequest = ProcessTemplateRequest.builder()
				.controllerClass(StreamingEventsController.class)
				.model(model)
				.templateName(Templates.STREAMING_EVENTS)
				.build();
		
		return processTemplate(templateProcessRequest);
	};	
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String viewStreamingEventsSources(Request request, Response response) {
		
		String organizationId = getIdentity(request).getOrganization().getId();
		
		Organization organization = ServiceClient.getInstance()
				.organization()
				.get(organizationId);
		
		Map<String,Object> model = getModel();
		model.put("organization", organization);
		
		ProcessTemplateRequest templateProcessRequest = ProcessTemplateRequest.builder()
				.controllerClass(StreamingEventsController.class)
				.model(model)
				.templateName(Templates.STREAMING_EVENTS_SOURCES)
				.build();
		
		return processTemplate(templateProcessRequest);
	}
	
	
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String setupStreamingEvents(Request request, Response response) {
		
		String organizationId = getIdentity(request).getOrganization().getId();
		
		String source = request.params(":source");
		
		ZoneId zoneId = ZoneId.of(request.queryParamOrDefault("zoneId", "UTC"));
		
		Organization organization = ServiceClient.getInstance()
				.organization()
				.get(organizationId);
		
		Optional<StreamingEventListener> eventListener = organization.getStreamingEventListeners()
				.stream()
				.filter(e -> source.equals(e.getSource()))
				.findFirst();
		
		List<FeedItem> feedItems = ServiceClient.getInstance()
				.organization()
				.getStreamingEventsFeed(organizationId, source);
		
		LocalDate today = LocalDate.now( zoneId );
		LocalDate firstDayOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
		LocalDate firstDayOfMonth = LocalDate.of(today.getYear(), today.getMonth(), 1);
		LocalDate firstDayOfYear = LocalDate.of(today.getYear(), 1, 1);
		Long daysBetween = ChronoUnit.DAYS.between(today.minusYears(1).plusDays(1), today);
		
		List<AggregationResult> results = ServiceClient.getInstance()
				.organization()
				.getEventsBySourceByDays(organization.getId().toString(), source, daysBetween.intValue(), TimeZone.getTimeZone(zoneId));
		
		AtomicLong eventsToday = new AtomicLong(0);
		AtomicLong eventsThisWeek = new AtomicLong(0);
		AtomicLong eventsThisMonth = new AtomicLong(0);
		AtomicLong eventsThisYear = new AtomicLong(0);
		
		results.forEach(r -> {
			if (r.getGroupByDate().isEqual(today)) {
				eventsToday.set(r.getCount());
			} 
			if (r.getGroupByDate().isEqual(firstDayOfWeek) || r.getGroupByDate().isAfter(firstDayOfWeek)) {
				eventsThisWeek.addAndGet(r.getCount());
			} 
			if (r.getGroupByDate().isEqual(firstDayOfMonth) || r.getGroupByDate().isAfter(firstDayOfMonth)) {
				eventsThisMonth.addAndGet(r.getCount());
			}
			if (r.getGroupByDate().isEqual(firstDayOfYear) || r.getGroupByDate().isAfter(firstDayOfYear)) {
				eventsThisYear.addAndGet(r.getCount());
			}
		});
		
		Locale locale = getIdentity(request).getLocale();
		
		Map<String,Object> model = getModel();
		model.put("organization", organization);
		model.put("viewAsUtc", zoneId.getId().equals("UTC") ? Boolean.TRUE : Boolean.FALSE);
		model.put("viewAsDefaultTimeZone", zoneId.getId().equals(getIdentity(request).getTimeZone()) ? Boolean.TRUE : Boolean.FALSE);
		model.put("eventListener", eventListener.get());
		model.put("feedItems", feedItems);
		model.put("UTC", ZoneId.of( "UTC" ).getDisplayName(TextStyle.SHORT, locale));
		model.put("DEFAULT_TIME_ZONE", ZoneId.of(getIdentity(request).getTimeZone()).getDisplayName(TextStyle.FULL, locale));
		model.put("TODAY", formatToday(today, locale));
		model.put("FIRST_DAY_OF_WEEK", formatToday(firstDayOfWeek, locale));
		model.put("FIRST_DAY_OF_MONTH", formatToday(firstDayOfMonth, locale));
		model.put("FIRST_DAY_OF_YEAR", formatToday(firstDayOfYear, locale));
		model.put("EVENTS_RECEIVED_TODAY", eventsToday);
		model.put("EVENTS_RECEIVED_THIS_WEEK", eventsThisWeek);
		model.put("EVENTS_RECEIVED_THIS_MONTH", eventsThisMonth);
		model.put("EVENTS_RECEIVED_THIS_YEAR", eventsThisYear);
		model.put("VIEW_AS_UTC_HREF", Path.Route.STREAMING_EVENTS_SETUP.replace(":source", source).concat("?zoneId=UTC"));
		model.put("VIEW_AS_DEFAULT_TIMEZONE_HREF", Path.Route.STREAMING_EVENTS_SETUP.replace(":source", source).concat("?zoneId=").concat(getIdentity(request).getTimeZone()));
		
    	ProcessTemplateRequest templateProcessRequest = ProcessTemplateRequest.builder()
				.controllerClass(StreamingEventsController.class)
				.model(model)
				.templateName(Templates.STREAMING_EVENTS_SETUP)
				.build();
		
		return processTemplate(templateProcessRequest);
	};	
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String saveEventListener(Request request, Response response) {
		
		String source = request.params(":source");
		
		List<NameValuePair> params = Collections.emptyList();
		
		if (! request.body().isEmpty()) {
			params = URLEncodedUtils.parse(request.body(), Charset.forName("UTF-8"));
		}
		
		Map<String,String> map = params.stream().collect(Collectors.toMap(NameValuePair::getName, NameValuePair::getValue));
		
		Boolean active = Boolean.valueOf(map.getOrDefault("active", "false"));
		Boolean notifyForOperationCreate = Boolean.valueOf(map.getOrDefault("notifyForOperationCreate", "false"));
		Boolean notifyForOperationDelete = Boolean.valueOf(map.getOrDefault("notifyForOperationDelete", "false"));
		Boolean notifyForOperationUndelete = Boolean.valueOf(map.getOrDefault("notifyForOperationUndelete", "false"));
		Boolean notifyForOperationUpdate = Boolean.valueOf(map.getOrDefault("notifyForOperationUpdate", "false"));
		
		StreamingEventListenerRequest eventListenerRequest = StreamingEventListenerRequest.builder()
				.active(active)
				.notifyForOperationCreate(notifyForOperationCreate)
				.notifyForOperationDelete(notifyForOperationDelete)
				.notifyForOperationUndelete(notifyForOperationUndelete)
				.notifyForOperationUpdate(notifyForOperationUpdate)
				.source(source)
				.build();
		
		ServiceClient.getInstance().organization().update(getIdentity(request).getOrganization().getId(), eventListenerRequest);
		
		response.header("location", Path.Route.STREAMING_EVENTS);
		
		return "";
	};	
	
	/**
	 * 
	 * @param locale
	 * @param result
	 * @return
	 */
	
	private static String formatLabel(Locale locale, AggregationResult result) {
		
		ZoneId utc = ZoneId.of( "UTC" );
		
		LocalDate now = LocalDate.now( utc ).minusDays(Integer.valueOf(result.getId()));
		
		String text = null;
		if (now.equals(LocalDate.now( utc ))) {
			text = MessageProvider.getMessage(locale, "today");
		} else if (now.equals(LocalDate.now( utc ).minusDays(1))) {
			text = MessageProvider.getMessage(locale, "yesterday");
		} else {
			text = now.getDayOfWeek().getDisplayName(TextStyle.FULL, locale);
		}
		
		return new StringBuilder("['").append(text)
				.append("'")
				.append(", ")
				.append(result.getCount())
				.append("]")
				.toString();
	}
	
	private static String formatToday(LocalDate today, Locale locale) {
		return today.format(DateTimeFormatter.ofPattern("EEEE, MMMM d yyyy", locale)); 
	}
}
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
import com.nowellpoint.console.model.EventStreamListener;
import com.nowellpoint.console.model.EventStreamListenerRequest;
import com.nowellpoint.console.model.EventStreamMonitor;
import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.console.util.MessageProvider;
import com.nowellpoint.console.util.Path;
import com.nowellpoint.console.util.Templates;

import spark.Request;
import spark.Response;

public class EventStreamsController extends BaseController2 {
	
	public void configureRoutes() {
		
		get(Path.Route.EVENT_STREAMS, (request, response) 
				-> listEventStreams(request, response));
		
		get(Path.Route.EVENT_STREAM_VIEW, (request, response)
				-> viewEventStream(request, response));
		
		post(Path.Route.EVENT_STREAM_VIEW, (request, response)
				-> saveEventSteamListener(request, response));
		
		post(Path.Route.EVENT_STREAMS_ACTION, (request, response)
				-> processEventStreamAction(request, response));
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String listEventStreams(Request request, Response response) {
		
		String organizationId = getIdentity(request).getOrganization().getId();
		
		Organization organization = ServiceClient.getInstance()
				.organization()
				.get(organizationId);
		
		
		
		Map<String,Object> model = getModel();
		model.put("organization", organization);
		//model.put("feedItems", feedItems);
		//model.put("data", data);
		
    	ProcessTemplateRequest templateProcessRequest = ProcessTemplateRequest.builder()
				.controllerClass(EventStreamsController.class)
				.model(model)
				.templateName(Templates.EVENT_STREAMS)
				.build();
		
		return processTemplate(templateProcessRequest);
	};	
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String viewEventStream(Request request, Response response) {
		
		ZoneId zoneId = ZoneId.of(request.queryParamOrDefault("zoneId", "UTC"));
		
		String organizationId = getIdentity(request).getOrganization().getId();
		
		String source = request.params(":source");
		
		Organization organization = ServiceClient.getInstance()
				.organization()
				.get(organizationId);
		
		Optional<EventStreamListener> eventListener = organization.getEventStreamListeners()
				.stream()
				.filter(e -> source.equals(e.getSource()))
				.findFirst();
		
		EventStreamMonitor monitor = ServiceClient.getInstance()
				.eventStream()
				.getEventStreamMonitor(organizationId, source, zoneId);
		
//		List<FeedItem> feedItems = ServiceClient.getInstance()
//				.organization()
//				.getStreamingEventsFeed(organizationId, getIdentity(request).getTimeZone());
		
		List<FeedItem> feedItems = ServiceClient.getInstance()
				.eventStream()
				.getStreamingEventsFeed(organizationId, source);
		
		LocalDate today = LocalDate.now( zoneId );
		LocalDate firstDayOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
		LocalDate firstDayOfMonth = LocalDate.of(today.getYear(), today.getMonth(), 1);
		LocalDate firstDayOfYear = LocalDate.of(today.getYear(), 1, 1);
		
		Locale locale = getIdentity(request).getLocale();
		
		Map<String,Object> model = getModel();
		model.put("organization", organization);
		model.put("viewAsUtc", zoneId.getId().equals("UTC") ? Boolean.TRUE : Boolean.FALSE);
		model.put("viewAsDefaultTimeZone", zoneId.getId().equals(getIdentity(request).getTimeZone()) ? Boolean.TRUE : Boolean.FALSE);
		model.put("eventListener", eventListener.get());
		model.put("feedItems1", feedItems.stream().limit(17).collect(Collectors.toList()));
		model.put("feedItems2", feedItems.stream().skip(17).limit(17).collect(Collectors.toList()));
		model.put("feedItems3", feedItems.stream().skip(34).collect(Collectors.toList()));
		//model.put("data", data);
		model.put("feedItems", feedItems);
		model.put("UTC", ZoneId.of( "UTC" ).getDisplayName(TextStyle.SHORT, locale));
		model.put("DEFAULT_TIME_ZONE", ZoneId.of(getIdentity(request).getTimeZone()).getDisplayName(TextStyle.FULL, locale));
		model.put("TODAY", formatToday(today, locale));
		model.put("FIRST_DAY_OF_WEEK", formatToday(firstDayOfWeek, locale));
		model.put("FIRST_DAY_OF_MONTH", formatToday(firstDayOfMonth, locale));
		model.put("FIRST_DAY_OF_YEAR", formatToday(firstDayOfYear, locale));
		model.put("eventStreamMonitor", monitor);
//		model.put("EVENTS_RECEIVED_TODAY", eventsToday);
//		model.put("EVENTS_RECEIVED_THIS_WEEK", eventsThisWeek);
//		model.put("EVENTS_RECEIVED_THIS_MONTH", eventsThisMonth);
//		model.put("EVENTS_RECEIVED_THIS_YEAR", eventsThisYear);
		model.put("VIEW_AS_UTC_HREF", Path.Route.EVENT_STREAM_VIEW.replace(":source", source).concat("?zoneId=UTC"));
		model.put("VIEW_AS_DEFAULT_TIMEZONE_HREF", Path.Route.EVENT_STREAM_VIEW.replace(":source", source).concat("?zoneId=").concat(getIdentity(request).getTimeZone()));
		
    	ProcessTemplateRequest templateProcessRequest = ProcessTemplateRequest.builder()
				.controllerClass(EventStreamsController.class)
				.model(model)
				.templateName(Templates.EVENT_STREAM)
				.build();
		
		return processTemplate(templateProcessRequest);
	};	
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String saveEventSteamListener(Request request, Response response) {
		
		String source = request.params(":source");
		
		List<NameValuePair> params = Collections.emptyList();
		
		if (! request.body().isEmpty()) {
			params = URLEncodedUtils.parse(request.body(), Charset.forName("UTF-8"));
		}
		
		Map<String,String> map = params.stream().collect(Collectors.toMap(NameValuePair::getName, NameValuePair::getValue));
		
		Boolean active = Boolean.valueOf(map.getOrDefault("active", "false"));
		Boolean create = Boolean.valueOf(map.getOrDefault("notifyForOperationCreate", "false"));
		Boolean delete = Boolean.valueOf(map.getOrDefault("notifyForOperationDelete", "false"));
		Boolean undelete = Boolean.valueOf(map.getOrDefault("notifyForOperationUndelete", "false"));
		Boolean update = Boolean.valueOf(map.getOrDefault("notifyForOperationUpdate", "false"));
		
		EventStreamListenerRequest eventListenerRequest = EventStreamListenerRequest.builder()
				.active(active)
				.notifyForOperationCreate(create)
				.notifyForOperationDelete(delete)
				.notifyForOperationUndelete(undelete)
				.notifyForOperationUpdate(update)
				.source(source)
				.build();
		
		ServiceClient.getInstance().organization().update(getIdentity(request).getOrganization().getId(), eventListenerRequest);
		
		response.header("location", Path.Route.EVENT_STREAM_VIEW.replace(":source", source));
		
		return "";
	};	
	
	private String processEventStreamAction(Request request, Response response) {
		
		String source = request.params(":source");
		String action = request.params(":action");
		
		Boolean active = "start".equals(action) ? Boolean.TRUE : Boolean.FALSE;
		
		EventStreamListenerRequest eventListenerRequest = EventStreamListenerRequest.builder()
				.active(active)
				.notifyForOperationCreate(active)
				.notifyForOperationDelete(active)
				.notifyForOperationUndelete(active)
				.notifyForOperationUpdate(active)
				.source(source)
				.build();
		
		ServiceClient.getInstance().organization().update(getIdentity(request).getOrganization().getId(), eventListenerRequest);
		
		response.header("location", Path.Route.EVENT_STREAMS);
		
		return "";
	}
	
	/**
	 * 
	 * @param locale
	 * @param result
	 * @return
	 */
	
	private String formatLabel(Locale locale, AggregationResult result) {
		
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
	
	private String formatToday(LocalDate today, Locale locale) {
		return today.format(DateTimeFormatter.ofPattern("EEEE, MMMM d yyyy", locale)); 
	}
}
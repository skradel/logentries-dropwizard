package com.coriell.logging;

import java.util.TimeZone;

import javax.validation.constraints.NotNull;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Layout;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("logentries")
public class LogentriesAppenderFactory extends io.dropwizard.logging.AbstractAppenderFactory {

	private String token;
	// private String suffixPattern = "%d{yyyy-MM-dd HH:mm:ss.SSS Z} [%thread] %-5level %logger{36} - %msg%n";
	
	@NotNull
    private TimeZone timeZone = TimeZone.getTimeZone("UTC");
	
	@NotNull
	private String facility = "USER";
	
	private boolean ssl = true;
	
	@JsonProperty
	public String getFacility() {
		return this.facility;
	}
	
	@JsonProperty
	public void setFacility(String value) {
		this.facility = value;
	}
	
	@JsonProperty
	public String getToken() {
		return token;
	}
	
	@JsonProperty
	public void setToken(String token) {
		this.token = token;
	}
	
	@JsonProperty
    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }
	
	@JsonProperty
	public TimeZone getTimeZone() {
		return this.timeZone;
	}
	
	@JsonProperty
    public void setSsl(boolean isSsl) {
        ssl = isSsl;
    }
	
	@JsonProperty
	public boolean getSsl() {
		return ssl;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Appender<ILoggingEvent> build(LoggerContext context, String applicationName, Layout<ILoggingEvent> layout) {
		
		final Appender<ILoggingEvent> appender; 
        
        try {
        	appender = (Appender<ILoggingEvent>) (Class.forName("com.logentries.logback.LogentriesAppender").newInstance());
            appender.setName("le-appender");
            appender.setContext(context);
			appender.getClass().getMethod("setToken", String.class).invoke(appender, token);
			appender.getClass().getMethod("setSsl", boolean.class).invoke(appender, ssl);
			appender.getClass().getMethod("setFacility", String.class).invoke(appender, this.facility);

			// Is this what we ought to manipulate -- suffixPattern?
	        appender.getClass().getMethod("setSuffixPattern", String.class).invoke(appender, this.getLogFormat());
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
        addThresholdFilter(appender, threshold);
        appender.start();

        return appender;
	}
}

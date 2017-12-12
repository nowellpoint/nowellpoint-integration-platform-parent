package com.nowellpoint.client.resource;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.client.Environment;
import com.nowellpoint.client.model.CreateResult;
import com.nowellpoint.client.model.DeleteResult;
import com.nowellpoint.client.model.Error;
import com.nowellpoint.client.model.Result;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.UpdateResult;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.util.Assert;

public abstract class AbstractResource {
		
	protected final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());
	protected final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
	
	protected Token token;
	protected Environment environment;
	protected static ObjectMapper objectMapper;
	
	static {
		objectMapper = new ObjectMapper();
	}
	
	public AbstractResource(Token token) {
		this.token = token;
	}
	
	public AbstractResource(Environment environment) {
		this.environment = environment;
	}
	
	protected String formatDate(Date value) {
		return Assert.isNotNull(value) ? dateFormat.format(value) : null;
	}
	
	protected String formatDateTime(Date value) {
		return Assert.isNotNull(value) ? dateTimeFormat.format(value) : null;
	}
	
	class DeleteResultImpl extends ResultImpl implements DeleteResult {
		
		public DeleteResultImpl() {
			super();
		}
		
		public DeleteResultImpl(Error error) {
			super(error);
		}
	}
	
	class CreateResultImpl<T> extends ResultImpl<T> implements CreateResult<T> {
		
		private T target;
		
		public CreateResultImpl(T target) {
			super();
			this.target = target;
		}
		
		public CreateResultImpl(Class<T> type, HttpResponse httpResponse) {
			super(type, httpResponse);
			this.target = (T) super.getTarget();
		}
		
		public CreateResultImpl(Error error) {
			super(error);
		}
		
		@Override
		public T getTarget() {
			return target;
		}
	}
	
	class UpdateResultImpl<T> extends ResultImpl<T> implements UpdateResult<T> {
		
		private T target;
		
		public UpdateResultImpl(T target) {
			super();
			this.target = target;
		}
		
		public UpdateResultImpl(Class<T> type, HttpResponse httpResponse) {
			super(type, httpResponse);
			this.target = (T) super.getTarget();
		}
		
		public UpdateResultImpl(Error error) {
			super(error);
		}	
		
		@Override
		public T getTarget() {
			return target;
		}
	}	
	
	private class ResultImpl<T> implements Result {
		
		private T target;
		
		protected Boolean isSuccess;
		
		protected String error;
		
		protected String errorMessage;
		
		public ResultImpl() {
			this.isSuccess = Boolean.TRUE;
		}
		
		public ResultImpl(Error error) {
			this.isSuccess = Boolean.FALSE;
			this.error = error.getCode();
			this.errorMessage = error.getErrorMessage();
		}
		
		public ResultImpl(Class<T> type, HttpResponse httpResponse) {
			if (httpResponse.getStatusCode() < 300) {
				this.isSuccess = Boolean.TRUE;
				this.target = (T) httpResponse.getEntity(type);
			} else {
				Error error = httpResponse.getEntity(Error.class);
				this.isSuccess = Boolean.FALSE;
				this.error = error.getCode();
				this.errorMessage = error.getErrorMessage();
			}
		}
		
		public T getTarget() {
			return target;
		}

		@Override
		public Boolean isSuccess() {
			return isSuccess;
		}

		@Override
		public String getError() {
			return error;
		}

		@Override
		public String getErrorMessage() {
			return errorMessage;
		}
	}
}
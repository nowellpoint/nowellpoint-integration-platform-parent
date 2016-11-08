package com.nowellpoint.client.resource;

import java.text.SimpleDateFormat;
import java.util.Locale;

import com.nowellpoint.client.model.AddResult;
import com.nowellpoint.client.model.CreateResult;
import com.nowellpoint.client.model.DeleteResult;
import com.nowellpoint.client.model.Error;
import com.nowellpoint.client.model.GetResult;
import com.nowellpoint.client.model.Result;
import com.nowellpoint.client.model.SetResult;
import com.nowellpoint.client.model.UpdateResult;
import com.nowellpoint.client.model.Token;

public class AbstractResource {
	
	protected static final String API_ENDPOINT = System.getenv("NOWELLPOINT_API_ENDPOINT");
	protected static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());
	
	protected Token token;
	
	public AbstractResource(Token token) {
		this.token = token;
	}
	
	class DeleteResultImpl implements DeleteResult {
		
		private Boolean isSuccess;
		
		private Integer error;
		
		private String errorMessage;
		
		public DeleteResultImpl() {
			this.isSuccess = Boolean.TRUE;
		}
		
		public DeleteResultImpl(Error error) {
			this.isSuccess = Boolean.FALSE;
			this.error = error.getCode();
			this.errorMessage = error.getMessage();
		}

		@Override
		public Boolean getIsSuccess() {
			return isSuccess;
		}

		@Override
		public Integer getError() {
			return error;
		}

		@Override
		public String getErrorMessage() {
			return errorMessage;
		}
	}
	
	class GetResultImpl <T> extends ResultImpl <T> implements GetResult <T> {
		
		public GetResultImpl(T target) {
			super(target);
		}
		
		public GetResultImpl(Error error) {
			super(error);
		}	
	}	
	
	class CreateResultImpl <T> extends ResultImpl <T> implements CreateResult <T> {
		
		public CreateResultImpl(T target) {
			super(target);
		}
		
		public CreateResultImpl(Error error) {
			super(error);
		}	
	}	
	
	class AddResultImpl <T> extends ResultImpl <T>	implements AddResult <T> {
		
		public AddResultImpl(T target) {
			super(target);
		}
		
		public AddResultImpl(Error error) {
			super(error);
		}
	}
	
	class SetResultImpl <T> extends ResultImpl <T>	implements SetResult <T> {
		
		public SetResultImpl(T target) {
			super(target);
		}
		
		public SetResultImpl(Error error) {
			super(error);
		}
	}
	
	class UpdateResultImpl <T> extends ResultImpl <T> implements UpdateResult <T> {
		
		public UpdateResultImpl(T target) {
			super(target);
		}
		
		public UpdateResultImpl(Error error) {
			super(error);
		}	
	}	
	
	private class ResultImpl <T> implements Result <T> {
		
		private Boolean isSuccess;
		
		private Integer error;
		
		private String errorMessage;
		
		private T target;
		
		public ResultImpl(T target) {
			this.isSuccess = Boolean.TRUE;
			this.target = target;
		}
		
		public ResultImpl(Error error) {
			this.isSuccess = Boolean.FALSE;
			this.error = error.getCode();
			this.errorMessage = error.getMessage();
		}

		@Override
		public Boolean isSuccess() {
			return isSuccess;
		}

		@Override
		public Integer getError() {
			return error;
		}

		@Override
		public String getErrorMessage() {
			return errorMessage;
		}

		@Override
		public T getTarget() {
			return target;
		}
	}
}
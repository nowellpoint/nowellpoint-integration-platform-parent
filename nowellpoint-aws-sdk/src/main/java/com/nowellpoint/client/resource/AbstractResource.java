package com.nowellpoint.client.resource;

import java.text.SimpleDateFormat;
import java.util.Locale;

import com.nowellpoint.client.model.CreateResult;
import com.nowellpoint.client.model.DeleteResult;
import com.nowellpoint.client.model.Error;
import com.nowellpoint.client.model.Result;

import com.nowellpoint.client.model.SignUpResult;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.UpdateResult;

public abstract class AbstractResource {
		
	protected final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());
	
	protected Token token;
	
	public AbstractResource(Token token) {
		this.token = token;
	}
	
	class DeleteResultImpl extends ResultImpl implements DeleteResult {
		
		public DeleteResultImpl() {
			super();
		}
		
		public DeleteResultImpl(Error error) {
			super(error);
		}
	}
	
	class CreateResultImpl<T> extends ResultImpl implements CreateResult<T> {
		
		private T target;
		
		public CreateResultImpl(T target) {
			super();
			this.target = target;
		}
		
		public CreateResultImpl(Error error) {
			super(error);
		}
		
		@Override
		public T getTarget() {
			return target;
		}
	}
	
	class UpdateResultImpl<T> extends ResultImpl implements UpdateResult<T> {
		
		private T target;
		
		public UpdateResultImpl(T target) {
			super();
			this.target = target;
		}
		
		public UpdateResultImpl(Error error) {
			super(error);
		}	
		
		@Override
		public T getTarget() {
			return target;
		}
	}	
	
	class SignUpResultImpl<T> extends ResultImpl implements SignUpResult<T> {
		
		private T target;
		
		public SignUpResultImpl(T target) {
			super();
			this.target = target;
		}
		
		public SignUpResultImpl(Error error) {
			super(error);
		}
		
		@Override
		public T getTarget() {
			return target;
		}
	}	
	
	private class ResultImpl implements Result {
		
		protected Boolean isSuccess;
		
		protected Integer error;
		
		protected String errorMessage;
		
		public ResultImpl() {
			this.isSuccess = Boolean.TRUE;
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
	}
}
package com.nowellpoint.console.entity;

public class AggregationResult implements Comparable<AggregationResult> {
	
	private String _id;
	private Long count;
	
	public AggregationResult() {
		
	}
	
	public AggregationResult(String id, Long count) {
		this._id = id;
		this.count = count;
	}
	
	public String getId() {
		return _id;
	}
	
	public void setId(String id) {
		this._id = id;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	@Override
	public int compareTo(AggregationResult o) {
		return _id.compareTo(o.getId());
	}
}
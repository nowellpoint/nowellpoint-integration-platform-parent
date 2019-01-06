package com.nowellpoint.console.entity;

import java.time.LocalDate;

public class AggregationResult implements Comparable<AggregationResult> {
	
	private String _id;
	private LocalDate groupByDate;
	private Long count;
	
	public AggregationResult() {
		
	}
	
	public AggregationResult(String id, LocalDate groupByDate, Long count) {
		this._id = id;
		this.groupByDate = groupByDate;
		this.count = count;
	}
	
	public String getId() {
		return _id;
	}
	
	public void setId(String id) {
		this._id = id;
	}

	public LocalDate getGroupByDate() {
		return groupByDate;
	}

	public void setGroupByDate(LocalDate groupByDate) {
		this.groupByDate = groupByDate;
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
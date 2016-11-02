package com.test.dynamodb.misc.vo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFilter;

@JsonAutoDetect
@JsonFilter("clientFilter")
public class DynamoDbResponseVO {

	private static final long serialVersionUID = 1544988317671183455L;
	
	private String result;

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}

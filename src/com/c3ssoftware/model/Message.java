package com.c3ssoftware.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.c3ssoftware.process.ProcessStatus;

public class Message {
	private String host = null;
	private int Port = 0;
	private long timeStamp = 0;
	private ProcessStatus processStatus = null;
	private String body = " ";
	private List<Integer> taskList = new ArrayList<>();
	private Integer minValue = 0;

	public Message(String message) {
		String msgSpecification[] = message.split(" , ");
		timeStamp = Long.parseLong(msgSpecification[0]);
		Port = Integer.parseInt(msgSpecification[1]);
		processStatus = ProcessStatus.valueOf(msgSpecification[2]);
		if (msgSpecification.length > 3)
			body = msgSpecification[3];
		else
			body = " ";
		
		if (msgSpecification[5] != null) {
			minValue = Integer.valueOf(msgSpecification[5]);
		}
	}

	public Message(long timeStamp, String host, int port, ProcessStatus processStatus) {
		this(timeStamp, host, port, processStatus, " ");
	}

	public Message(long timeStamp, String host, int port, ProcessStatus processStatus, String body) {
		this.timeStamp = timeStamp;
		this.host = host;
		this.Port = port;
		this.processStatus = processStatus;
		this.body = body;
	}

	public Message(long timeStamp, String host, int port, ProcessStatus processStatus, Integer minValue) {
		this.timeStamp = timeStamp;
		this.host = host;
		this.Port = port;
		this.processStatus = processStatus;
		System.out.println("sencodwqsdsadasdasd");
		this.minValue = minValue;
	}

	public String toString() {
		return timeStamp + " , " + Port + " , " + processStatus + " , " + body + " , " + taskList + " , " + minValue;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return Port;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public ProcessStatus getProcessStatus() {
		return processStatus;
	}

	public void setPort(int port) {
		Port = port;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setContent(ProcessStatus processStatus) {
		this.processStatus = processStatus;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public List<Integer> getTaskList() {
		return this.taskList;
	}

	public List<Integer> getTaskList(String message) {
		String msgSpecification[] = message.split(" , ");
		if (msgSpecification[4] != null) {
			String[] listAsString = msgSpecification[4].replaceAll("\\[|\\]| ", "").split(",");
			taskList.addAll(Arrays.stream(listAsString) // stream of String
					.map(Integer::valueOf) // stream of Integer
					.collect(Collectors.toList()));

			return taskList;
		}
		return taskList;
	}

	public void setTaskList(List<Integer> taskList) {
		this.taskList = taskList;
	}

	public Integer getMinValue() {
		return minValue;
	}

	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}
}
package com.sharer.entities;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "resource")
public class Asset {
	private String name;
	private int version;
	private String data;
	private long length;
	private String remotePathTofile;
	
	public String getName() {
		return name;
	}
	
	@XmlElement(name = "documentName")
	public void setName(String name) {
		this.name = name;
	}
	
	public int getVersion() {
		return version;
	}
	
	@XmlElement
	public void setVersion(int version) {
		this.version = version;
	}
	
	public String getData() {
		return data;
	}
	
	@XmlElement
	public void setData(String data) {
		this.data = data;
	}

	public long getLength() {
		return length;
	}

	@XmlElement
	public void setLength(long length) {
		this.length = length;
	}
	
	public String getRemotePathTofile() {
		return remotePathTofile;
	}

	@XmlElement(name = "pathTofile")
	public void setRemotePathTofile(String remotePathTofile) {
		this.remotePathTofile = remotePathTofile;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.name.equals(((Asset)obj).name) && this.version == ((Asset) obj).version;
	}
}

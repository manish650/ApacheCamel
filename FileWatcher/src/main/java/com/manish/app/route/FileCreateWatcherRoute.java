package com.manish.app.route;

import java.io.File;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileCreateWatcherRoute extends RouteBuilder {

	@Value("${watecherFolderPath}")
	String hazelcastConfiglocation;
	private static final String EVENT = "CREATE";// CREATE,DELETE,MODIFY

	@Override
	public void configure() throws Exception {
		from("file-watch:" + hazelcastConfiglocation + "?events=" + EVENT + "&recursive=false")

				.process(new Processor() {
					@Override
					public void process(Exchange exchange) throws Exception {

						File file = exchange.getIn().getBody(File.class);
						if (null != file) {
								System.err.println("Created  File Name::  " + file.getName());
						}
					}

				});
	}

}
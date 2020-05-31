package com.manish.app.route;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileModifyWatcherRoute extends RouteBuilder {

	@Value("${watecherFolderPath}")
	String hazelcastConfiglocation;
	private static final String FILENAME = "sampleFile.txt";
	private static final String EVENT="MODIFY";//CREATE,DELETE,MODIFY



	@Override
	public void configure() throws Exception {
		from("file-watch:" + hazelcastConfiglocation + "?events="+EVENT+"&recursive=false")

				.process(new Processor() {
					@Override
					public void process(Exchange exchange) throws Exception {

						File file = exchange.getIn().getBody(File.class);
						if (null != file && StringUtils.containsIgnoreCase(file.getName(), FILENAME)) {
							byte[] bytes = new byte[1024];
							try (InputStream inStream = new FileInputStream(file)) {
								inStream.read(bytes);
								String data = new String(bytes,Charset.defaultCharset()).trim();
								exchange.getIn().setHeader(Exchange.HTTP_QUERY, "id="+data);
							}
						}
					}

				}).setHeader(Exchange.HTTP_METHOD,org.apache.camel.component.http.HttpMethods.GET)
				.to("http://localhost:8787/get").process(new Processor() {
					
					@Override
					public void process(Exchange exchange) throws Exception {
						System.err.println(exchange.getIn().getBody(String.class));
					}
				});
	}

}
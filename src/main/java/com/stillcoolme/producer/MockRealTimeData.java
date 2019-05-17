package com.stillcoolme.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

public class MockRealTimeData extends Thread {

	private static Logger LOG = LoggerFactory.getLogger(MockRealTimeData.class);

	private static final String TOPIC_NAME = "monitor";
	private static final String BROKER = "172.16.21.189:9092";

	private static final Random random = new Random();
	private static SimpleDateFormat sdf = new SimpleDateFormat("YY-MM-dd HH:mm:ss");

	String[] url_paths = new String[]{"www/2", "www/1", "www/6", "www/4", "www/3", "pianhua/130", "toukouxu/821"};
	Integer[] ips = new Integer[]{132,156,124,10,29,167,143,187,30,100};
	Integer[] status = new Integer[]{404, 200, 302};
	String[] http_referers = new String[]{"https://www.baidu.com/s?wd={query}",
			"https://www.sogou.com/web?qu={query}",
			"http://cn.bing.com/search?q={query}",
			"https://search.yahoo.com/search?p={query}"};
	String[] search_keyword = new String[]{
			"java入门",
			"spark项目实战",
			"养生节目",
			"猎场",
			"快乐人生",
			"极限挑战",
			"我的体育老师",
			"幸福满院"};

	private Producer<Object, String> producer;
	
	public MockRealTimeData() {
		Properties props = new Properties();
		props.put("bootstrap.servers", BROKER);
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		producer = new KafkaProducer<Object, String>(props);
	}

	public void run() {
		int sum = 1;
		while(true) {
			int random_count = random.nextInt(3);
			String query_log = "";
			for(int i = 0; i < random_count; i++){
				String sample_urls = url_paths[random.nextInt(url_paths.length - 1)];

				String ip = "";
				ip += "192.168.";
				for(int j = 0; j < 2; j++){
					ip += String.valueOf(ips[random.nextInt(ips.length - 1)]);
					ip += ".";
				}
				ip = ip.substring(0, ip.length() - 1);

				Integer sample_status = status[random.nextInt(status.length - 1)];
				String http_referece = http_referers[random.nextInt(http_referers.length - 1)];
				http_referece = http_referece.replaceAll("query", search_keyword[random.nextInt(search_keyword.length - 1)]);
				String date_time = sdf.format(new Date());
				query_log += ip + " " + date_time + " GET " + sample_urls + " HTTP/1.0 " + http_referece + " " + sample_status + " " + sum++ + "\n";

				if(i == 0){
					System.out.println(query_log);
				}
			}
			System.out.println("produount: " + random_count);
			//LOG.info("produce log: " + query_log);
			producer.send(new ProducerRecord<Object, String>(TOPIC_NAME, query_log));

			try {
				Thread.sleep(random_count * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 启动Kafka Producer
	 * @param args
	 */
	public static void main(String[] args) {
		MockRealTimeData producer = new MockRealTimeData();
		producer.start();
	}
	
}

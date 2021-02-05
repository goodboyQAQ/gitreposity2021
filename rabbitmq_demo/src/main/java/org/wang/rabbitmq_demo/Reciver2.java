package org.wang.rabbitmq_demo;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class Reciver2 {
	private final static String QUEUE_NAME = "simple_queue";
	 
    public static void main(String[] argv) throws Exception {
        Connection connection = ConnectionUtil.getConnection();
        final Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        DefaultConsumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body,"utf-8");
                System.out.println(" [x] received : " + msg + "!");
                //执行ACK
                channel.basicAck(envelope.getDeliveryTag(), false); 
            }
        };
        //设置为手动ACK
        channel.basicConsume(QUEUE_NAME, false, consumer);
    }
}

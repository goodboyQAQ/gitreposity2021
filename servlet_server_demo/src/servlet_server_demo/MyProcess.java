package servlet_server_demo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
 

public class MyProcess extends Thread {
 
	private static final String SUCCESS = "200";
	private static final String NOT_FOUND = "404";
	
	private Socket socket;
	private String address;
	private Integer port;
	private String status;
	private String url;
	
	public MyProcess(Socket socket) {
		this.socket = socket;
		InetAddress inetAddress = socket.getInetAddress();
		this.address = inetAddress.getHostAddress();
		this.port = socket.getLocalPort();
	}
	
	@Override
	public void run() {
		try {
			// 接收到请求,处理请求携带信息
			// 自定义 request进行封装
			MyServletRequest request = new MyServletRequest(socket.getInputStream());
			// 自定义 response进行封装
			MyServletResponse response = new MyServletResponse(socket.getOutputStream());
			String url = request.getUrl();
			this.url = url;
			// 通过URL匹配Servlet(这里我们的servlet-mapping使用等于匹配,还可以采取正则匹配,如*.do)
			MyServlet servlet = (MyServlet) MyStarter.servletMapping.get(url);
			if (null != servlet) {
				// 容器中存在处理该请求的Servlet,假设程序没有运行错误,状态200
				this.status = SUCCESS;
				servlet.service(request, response);
			} else {
				// 容器中不存在处理该请求的Servlet,状态404
				this.status = NOT_FOUND;
				OutputStream outputStream = response.getOutputStream();
				outputStream.write(new String(MyServletResponse.RESPONSE_HEADER + "Welcome! error: Cannot find the servlet!").getBytes());
				outputStream.flush();
				outputStream.close();
			}
			if (!"/favicon.ico".equals(url)) {
				// 简单记录我们自己的访问日志
				logRecord();
			} 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != socket) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 
	 * function: 日志记录
	 * @author zhanghaolin
	 * @date 2018年7月25日   下午4:35:34
	 */
	@SuppressWarnings("resource")
	private void logRecord(){
		try {
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
			String dateStr = sdf.format(date);
			String record = dateStr + " " + this.address + ":" + this.port
					+ " ==> " + this.url + " , response:" + this.status + "\r\n";
			File file = new File("d:\\mytomcat-log\\mylog.log");
			if (!file.exists()) {
				file.createNewFile();
			}
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));
			bufferedWriter.write(record);
			bufferedWriter.flush();
			bufferedWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

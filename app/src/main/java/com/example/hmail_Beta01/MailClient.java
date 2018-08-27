package com.example.hmail_Beta01;

import android.os.Message;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by GOD on 2017/8/29.
 */

public class MailClient implements Runnable {

	// public MailClient(Context context){
	// this.context = context;
	// }
	// Context context;
	private String pop_address = null;
	private Socket sock = null;
	private DataInputStream in = null;
	private BufferedReader reader = null;
	private DataOutputStream out = null;
	private boolean run = true;
	private Date lastCommuTime = null;
	public boolean loginsuccess = false;
	private static int waitToProcessInput = 0;
	private Object lock = new Object();
	private String mailUser = null;
	private String mailPass = null;
	private String mailorder = null;// 操作序列号
	private static String InputOk2 = null;// 阅读邮件序号
	static int mailnumber = 0;
	StringBuffer mailNumberout = new StringBuffer();// 记录第几封
	static String mailNumberArray[] = new String[100000];
	StringBuffer mailTitleout = new StringBuffer();// 记录邮件主题
	static String mailTitleArray[] = new String[100000];
	StringBuffer mailDateout = new StringBuffer();// 记录发件日期
	static String mailDateArray[] = new String[100000];
	static String mailDetailedDateArray[] = new String[100000];// 记录详细日期
	StringBuffer mailFromout = new StringBuffer();// 记录发件人
	static String mailFromArray[] = new String[100000];
	StringBuffer mailStateout = new StringBuffer();// 记录邮件状态
	static String mailStateArray[] = new String[100000];
	static String[] mailContentArray = new String[100000];// 邮件正文
	static String[] mailFuJianNumArray = new String[100000];// 邮件附件数
	static String[][] mailFuJianNameArray = new String[1000][1000];// 附件名
	static String mailToArray[] = new String[100000];// 收件人
	StringBuffer mailExistFuJian;
	static String mailExistFuJianArray[] = new String[100000];// 判断存在附件
	static int Judgelock = 1;

	// Context context;

	/**
	 * 得到邮件总数
	 */
	public void setmailnumber(int mailnumber) {
		this.mailnumber = mailnumber;
		// System.out.println("后台中的邮件标题：" + mailTitleArray[1]);
		notifyActivity(1);
		// LoginActivity.entryMainMenuInputMode();
		// LoginActivity.StartNextActivity();
		// ReturnHome.entryMainMenuInputMode();
	}

	public void setMailUser(String mailUser) {// 得到的用户名
		this.mailUser = mailUser;
		// System.out.println(this.mailUser);
	}

	public void setMailPass(String mailPass) {// 得到的密码
		this.mailPass = mailPass;
		// System.out.println("Pass:"+this.mailPass);
	}

	public void setOrder(String mailorder) {// 得到操作序号
		this.mailorder = mailorder;
		try {
			postUserInput(this.mailorder);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println(this.mailUser);
	}

	/**
	 * 得到用户需要读取的邮件序号
	 * 
	 * @param InputOk2
	 */
	public void setNumber(String InputOk2) {// 得到邮件序号
		this.InputOk2 = InputOk2;
	}

	/**
	 * 创建与服务器的连接
	 * 
	 * @return
	 */
	private int connectToPopServ() {
		int ret = 0;
		try {
			this.sock = new Socket("pop3.163.com", 110);// 创建与服务器的连接
			this.sock.setSoTimeout(10 * 000);
			// this.sock.connect(new InetSocketAddress("pop.163.com", 110),
			// 10*000);
			in = new DataInputStream(this.sock.getInputStream());
			reader = new BufferedReader(new InputStreamReader(in));
			out = new DataOutputStream(this.sock.getOutputStream());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("与服务器连接失败，返回ret = -1");
			ret = -1;
		}
		return ret;// 连接成功，ret = 0
	}

	/**
	 * 判断服务器是否返回+ok
	 * 
	 * @return
	 */
	public int getResp() {// 判断服务器是否返回 +ok
		int ret = 0;
		String line = null;
		try {
			line = reader.readLine();
			System.out.println("服务器返回：" + line);
			if (line.startsWith("+OK")) { // 服务器返回 +ok 则 ret = 0
				ret = 0;
			} else {
				ret = 1;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ret = -1;
		}

		return ret;
	}

	static int num2 = 0;// 邮件总数

	/**
	 * list返回+ok
	 * 
	 * @return
	 */
	public int getResplist() {
		int ret = 0;

		String[] num1 = null;
		String line = null;
		try {
			in = new DataInputStream(this.sock.getInputStream());
			reader = new BufferedReader(new InputStreamReader(in));
			out = new DataOutputStream(this.sock.getOutputStream());
			line = reader.readLine();
			System.out.println("服务器返回：" + line);
			if (line.startsWith("+OK")) { // 服务器返回 +ok 则 ret = 0
				num1 = line.split(" ");
				num2 = (int) Double.parseDouble(num1[1]);
				System.out.println("邮件总数：" + num2);
				ret = 0;
			} else {
				ret = 1;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ret = -1;
		}

		return ret;
	}

	/**
	 * 判断用户帐号密码是否正确； 正确输出 “登录邮箱帐号成功”
	 * 
	 * @return
	 */
	public int login() {
		int ret = 0;
		ret = this.sendCmd("user " + this.mailUser);
		if (ret == 0) {
			ret = this.getResp();
			if (ret == 0) {
				ret = this.sendCmd("pass " + this.mailPass);
				if (ret == 0) {
					ret = this.getResp();
					if (ret == 0) {
						System.out.println("登录邮箱帐号成功！");
						loginsuccess = true;

					}
				}
			}
		}

		return ret;
	}

	/**
	 * 输出配置文件含pass则密码改为******； 其他直接输出
	 * 
	 * @param cmd
	 * @return
	 */
	public int sendCmd(String cmd) {
		int ret = 0;
		try {

			System.out.println(cmd);

			this.out.write((cmd + "\r\n").getBytes("UTF-8"));

			this.lastCommuTime = new Date();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ret = -1;
		}

		return ret;
	}

	/**
	 * 判断用户输入的序号是否为1到4
	 * 
	 * @param input
	 * @return
	 * @throws IOException
	 */
	private void postUserInput(String input) throws Exception {// 输入操作序号
		String order = input; // 获得操作序列号
		if (order.matches("[1-4]{1}")) {
			int choice = Integer.parseInt(order);// 读取操作台输入转换为int值
			boolean isBusy = true;

			synchronized (lock) {
				if (waitToProcessInput == 0) {
					isBusy = false;
					waitToProcessInput = choice;
					lock.notify();

				}
			}
			if (isBusy) {
				// Toast.makeText(context, "后台线程正在处理中，请稍候再选择操作指令！",
				// Toast.LENGTH_SHORT).show();
				System.out.println("后台线程正在处理中，请稍候再选择操作指令！");
			}
		} else {

			// Toast.makeText(context, "请输入指定范围内的序号，重新输入",
			// Toast.LENGTH_SHORT).show();
			System.out.println("invalidate cmd !");
		}

	}

	public void run() {
		// TODO Auto-generated method stub
		if (connectToPopServ() == 0) {// ret = 0
			if (getResp() == 0) {// 服务器返回 +ok 则 getResp() = 0
				if (this.login() == 0) { // 输出登录邮箱帐号成功
					waitToProcessInput = 1;// 登录后立即显示邮件列表
					do {
						// System.out.println(this+":==>"+waitToProcessInput);
						synchronized (lock) {
							if (waitToProcessInput == 0) {
								try {
									// System.out.println("1秒刷新 1秒刷新 1秒刷新");
									lock.wait(1000);// 等待输入，1000毫秒刷新一次
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							} else {
								int value = waitToProcessInput;
								waitToProcessInput = 0;
								try {
									System.out.println("开始执行序号对指令");
									process(value);
									System.out.println("已执行序号对指令");// 用户输入的操作数所对应的命令
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();

									run = false;
								}

							}
						}
						// 防止邮件会话超时的处理
						if (new Date().getTime() - lastCommuTime.getTime() > 10000) {
							this.sendCmd("NOOP");
							if (this.getResp() == 0) {
								// 会话正常
								entryMainMenuInputMode();
							} else {
								System.out.println("邮件会话断开！");
								run = false;
							}
						}
					} while (run);

				} else {
					System.out.println("帐号密码不正确，程序退出！");
					entryMainMenuInputMode();
					// System.exit(1);
				}
			}
		} else {
			System.out.println("Can't connect to pop server :" + pop_address);
		}
notifyActivity(-1);
		
	}

	// // 创建FileOutputStream对象
	// FileOutputStream outputStream = null;
	// // 创建BufferedOutputStream对象
	// BufferedOutputStream bufferedOutputStream = null;

	/**
	 * 对用户输入的序号进行执行
	 * 
	 * @param value
	 * @throws IOException
	 * @throws Exception
	 */
	private void process(int value) throws IOException {
		switch (value) {
		case 1: {
			if (fileIsExists() == 0) {// 判断记录邮件状态文件存在
				File file = new File("/mnt/sdcard/MailBeta_zyt/"
						+ "mailstate.txt");
				file.createNewFile();
				System.out.println("已经创建完状态记录文件！");
			}

			readMailState();// 读取邮件状态文件

			// InsertMysql(mailUser, "1", "0");
			this.sendCmd("list");
			if (getResplist() == 0) {// 得到邮件总数 num2
				System.out.println(getRespContent());
			}
			for (int mailId = 1; mailId <= num2; mailId++) {

				this.sendCmd("retr " + mailId);
				if (getResp() == 0) {
					String content = getRespContent();// 得到邮件的所有内容
					// System.out.println("邮件大小："+content.length()+"字节");
					BufferedReader reader = new BufferedReader(
							new StringReader(content));
					String line = null;
					// 得到邮件头部信息
					Map<String, String> mailHeader = this
							.getMailHeaderFromReader(reader);
					System.out.println("===============第 " + mailId
							+ " 封==================");
					mailNumberout.append("第 " + mailId + " 封");
					mailNumberArray[mailId] = "第 " + mailId + " 封";

					System.out.println("邮件主题："
							+ this.getBase64Content(mailHeader.get("Subject")));
					mailTitleout.append("邮件主题："
							+ this.getBase64Content(mailHeader.get("Subject")));
					mailTitleArray[mailId] = this.getBase64Content(mailHeader
							.get("Subject"));

//					System.out.println("发件日期：" + mailHeader.get("Date"));
//					mailDateout.append("发件日期：" + mailHeader.get("Date"));
					
					mailDateArray[mailId] = translateMailDate(mailHeader.get("Date"));
					System.out.println("发件日期：" +mailDateArray[mailId]);

					System.out.println("发件人："
							+ this.translateMailSender(mailHeader.get("From")));
					mailFromout.append("发件人："
							+ this.translateMailSender(mailHeader.get("From")));
					mailFromArray[mailId] = this.translateMailSender(mailHeader
							.get("From"))+" ";

					System.out.println("收件人：" + mailHeader.get("To"));
					mailToArray[mailId] = mailHeader.get("To");
					String mailIdString = Integer.toString(mailId);
					if (str.contains("/" + mailIdString + "/")) {
						mailStateArray[mailId] = "已读";
					} else {
						mailStateArray[mailId] = "未读";
					}

					System.out
							.println("==========================================");
					// 如果邮件存在多个组成部分，则从邮件头信息中得到分隔符
					String boundary = null;
					String contentType = mailHeader.get("Content-Type");
					System.out.println("Content-Type" + contentType);
					if (contentType.indexOf("multipart") > -1) {// 是否为复杂体邮件
						boundary = contentType.substring(contentType
								.indexOf("boundary=") + 9);// 前9个字符为boundary=，后面才是分隔符
						boundary = boundary.trim().replaceAll("\"", "");
						// System.out.println("这一封多部分构成的邮件，分隔符为:["+boundary+"]");
					}

					boolean isEnd = false;
					// 快速找到第一个分隔符，抛弃邮件头与第一个分隔符中间的部分，原因查看返回的邮件内容
					// 第一个分隔符后就是第一个附件的内容，解析邮件的各个附件
					List<StringBuffer> part = new ArrayList<StringBuffer>();
					StringBuffer partContent = new StringBuffer();
					while (!isEnd) {
						while (true) {
							line = reader.readLine();
							// System.out.println(line);// 控制台输出得到的邮件所有内容
							if (line == null) {
								isEnd = true;
								break;
							} else if (line.equals("--" + boundary)) {
								// 此处要加"--"的原因，查看返回的邮件内容里的分隔符
								if (partContent.length() > 0) {
									System.out.println("#######NEW PART######");
									part.add(partContent);
								}
								partContent = new StringBuffer();
								break;
							} else if (line.equals("--" + boundary + "--")) {
								// 此处前后都要加"--"的原因，查看返回的邮件内容里的分隔符 结束此分隔符
								if (partContent.length() > 0) {
									System.out
											.println("#######NEW  PART######");
									part.add(partContent);
								}
								isEnd = true;
								break;
							} else {
								partContent.append(line);
							}
						}
					}
					System.out.println("=================================");

					System.out.println("邮件主题："
							+ this.getBase64Content(mailHeader.get("Subject")));

					System.out.println("邮件大小：" + content.length() + "字节");

					System.out.println("邮件总共有" + part.size() + "个部分");
					for (int i = 0; i < part.size(); i++) {
						int mailId2 = mailId;
						System.out.println("  part[" + (i + 1) + "]:"
								+ part.get(i).length() + "字节(包含头部信息)");
						System.out.println("字符串为：" + part.get(i));
						if (i != 0) {
							int FuJianNum = part.size() - 1;
							System.out.println("邮件中包含附件!" + "\n" + "附件数为： "
									+ FuJianNum);// 在明文中显示邮件附件个数

							String FuJianNum_2 = Integer.toString(FuJianNum);
							mailFuJianNumArray[mailId2] = FuJianNum_2;// 显示到页面上的附件数
						}
					}
				}
			}
			// setOutToPrint(mailNumberout);
			setmailnumber(num2);

		}
			break;
		case 2: {
			String mailId = InputOk2;
			int mailId2 = Integer.parseInt(mailId);
			StringBuffer out = new StringBuffer();
			this.sendCmd("retr " + mailId);
			// InsertMysql(mailUser, "2", mailId);
			if (getResp() == 0) {
				String content = getRespContent();// 得到邮件的所有内容
				// System.out.println("邮件大小："+content.length()+"字节");
				BufferedReader reader = new BufferedReader(new StringReader(
						content));
				String line = null;
				// 得到邮件头部信息
				Map<String, String> mailHeader = this
						.getMailHeaderFromReader(reader);

				mailDetailedDateArray[mailId2] = translateMailDetailedDate(mailHeader.get("Date"));
				System.out.println("发件日期：" +mailDetailedDateArray[mailId2]);
				// 如果邮件存在多个组成部分，则从邮件头信息中得到分隔符
				String boundary = null;
				String contentType = mailHeader.get("Content-Type");
				System.out.println("Content-Type" + contentType);
				if (contentType.indexOf("multipart") > -1) {// 是否为复杂体邮件
					boundary = contentType.substring(contentType
							.indexOf("boundary=") + 9);// 前9个字符为boundary=，后面才是分隔符
					boundary = boundary.trim().replaceAll("\"", "");
					// System.out.println("这一封多部分构成的邮件，分隔符为:["+boundary+"]");
				}

				boolean isEnd = false;
				// 快速找到第一个分隔符，抛弃邮件头与第一个分隔符中间的部分，原因查看返回的邮件内容
				// 第一个分隔符后就是第一个附件的内容，解析邮件的各个附件
				List<StringBuffer> part = new ArrayList<StringBuffer>();
				StringBuffer partContent = new StringBuffer();
				while (!isEnd) {
					while (true) {
						line = reader.readLine();
//						System.out.println(line);// 控制台输出得到的邮件所有内容
						if (line == null) {
							isEnd = true;
							break;
						} else if (line.equals("--" + boundary)) {
							// 此处要加"--"的原因，查看返回的邮件内容里的分隔符
							if (partContent.length() > 0) {
								System.out.println("#######NEW PART######");
								part.add(partContent);
							}
							partContent = new StringBuffer();
							break;
						} else if (line.equals("--" + boundary + "--")) {
							// 此处前后都要加"--"的原因，查看返回的邮件内容里的分隔符 结束此分隔符
							if (partContent.length() > 0) {
								System.out.println("#######NEW  PART######");
								part.add(partContent);
							}
							isEnd = true;
							break;
						} else {
							partContent.append(line);
						}
					}
				}
				System.out.println("=================================");
				out.append("=================================");
				System.out.println("邮件主题："
						+ this.getBase64Content(mailHeader.get("Subject")));
				out.append("邮件主题："
						+ this.getBase64Content(mailHeader.get("Subject")));
				System.out.println("邮件大小：" + content.length() + "字节");
				out.append("邮件大小：" + content.length() + "字节");
				System.out.println("邮件总共有" + part.size() + "个部分");
				for (int i = 0; i < part.size(); i++) {
					System.out.println("  part[" + (i + 1) + "]:"
							+ part.get(i).length() + "字节(包含头部信息)");
					System.out.println("字符串为：" + part.get(i));
					if (i != 0) {
						int FuJianNum = part.size() - 1;
						System.out.println("邮件中包含附件!" + "\n" + "附件数为： "
								+ FuJianNum);// 在明文中显示邮件附件个数
						out.append("邮件中包含附件!" + "\n" + "附件数为： " + FuJianNum);
						String FuJianNum_2 = Integer.toString(FuJianNum);
						mailFuJianNumArray[mailId2] = FuJianNum_2;// 显示到页面上的附件数
						String mailFuJianName = (part.get(i)).substring((part
								.get(i)).indexOf("name=\"") + 6);
						System.out.println("附件名截取1：" + mailFuJianName);// 截取name="之后的字符串
						mailFuJianName = mailFuJianName
								.split("\"Content-Transfer-Encoding:")[0]
								.toString();
						System.out.println("附件名截取2：" + mailFuJianName);
						if (mailFuJianName.contains("?")) {// 附件名解码成明文
							String FuJianNameCharset;
							String FuJianNameTxt = "";
							FuJianNameCharset = mailFuJianName.split("[?]")[1]
									.toString();
							System.out.println("附件名编码格式截取3："
									+ FuJianNameCharset);
							FuJianNameTxt = mailFuJianName.split("[?]")[3]
									.toString();
							System.out.println("附件名编码内容截取4：" + FuJianNameTxt);
							byte[] asBytes = Base64.decode(FuJianNameTxt,
									Base64.DEFAULT);
							;// 通过base64解码成明文
							System.out.println(new String(asBytes,
									FuJianNameCharset));
							mailFuJianNameArray[mailId2][i] = new String(asBytes,
									FuJianNameCharset);
						} else {
							mailFuJianNameArray[mailId2][i] = mailFuJianName;
						}
					}
					if (i == 0) {// 第一个数组里面一般为明文
						System.out.println(part.get(i));
						String mailTxt = (part.get(i)).substring((part.get(i))
								.indexOf("base64") + 6);// 截取base64之后的字符串
						mailTxt = mailTxt.split("--")[0].toString();// 通过 "--"
						// 对字符串进行分割，得到明文编码
						System.out.println("截取后的字符串：" + mailTxt);

						String mailTxtCharest = (part.get(i)).substring((part
								.get(i)).indexOf("charset") + 8);// 截取charest=之后的字符串
						mailTxtCharest = mailTxtCharest
								.split("Content-Transfer-Encoding:")[0]
								.toString();// 通过
						System.out.println("截取后的字符串2：" + mailTxtCharest); // 对字符串进行分割，得到明文编码格式

						if (mailTxtCharest.contains("\"")) {// gb18030的编码格式带双引号，需要去掉，如：Content-Type:
															// text/plain;
							// charset="gb18030"
							mailTxtCharest = mailTxtCharest.replace("\"", "");
						}
						byte[] asBytes = Base64.decode(mailTxt, Base64.DEFAULT);// 通过base64解码成明文
						System.out.println(new String(asBytes, mailTxtCharest));
						out.append(new String(asBytes, mailTxtCharest));
						mailContentArray[mailId2] = new String(asBytes,
								mailTxtCharest);

						System.out.println("mailId2 = " + mailId2);
						System.out.println("邮件内容数组："
								+ mailContentArray[mailId2]);
						// ShowContent.entryMainMenuInputMode();

					}
				}
			}
//			do {
//				// MailListActivity.entryMainMenuInputMode();
//			} while (Judgelock == 1);// 避免发生在上锁前由于邮件解码快提前释放锁导致上锁后无法释放的情况发生！
			writeMailState("/mnt/sdcard/MailBeta_zyt/mailstate.txt", str + "/"
					+ mailId + "/");// 在阅读邮件后记录阅读邮件的序号
			str = str + "/" + mailId + "/";// 将之前的阅读序号加在前面
			
			notifyActivity(2);
		}
			break;
		case 3: {
			String mailId = InputOk2;
			int mailId2 = Integer.parseInt(mailId);
			this.sendCmd("retr " + mailId);
			// InsertMysql(mailUser, "3", mailId);
			if (getResp() == 0) {
				String content = getRespContent();// 得到邮件的所有内容
				// System.out.println("邮件大小："+content.length()+"字节");
				BufferedReader reader = new BufferedReader(new StringReader(
						content));
				String line = null;
				// 得到邮件头部信息
				Map<String, String> mailHeader = this
						.getMailHeaderFromReader(reader);

				// 如果邮件存在多个组成部分，则从邮件头信息中得到分隔符
				String boundary = null;
				String contentType = mailHeader.get("Content-Type");
				System.out.println("Content-Type" + contentType);
				if (contentType.indexOf("multipart") > -1) {// 是否为复杂体邮件
					boundary = contentType.substring(contentType
							.indexOf("boundary=") + 9);// 前9个字符为boundary=，后面才是分隔符
					boundary = boundary.trim().replaceAll("\"", "");
					// System.out.println("这一封多部分构成的邮件，分隔符为:["+boundary+"]");
				}

				boolean isEnd = false;
				// 快速找到第一个分隔符，抛弃邮件头与第一个分隔符中间的部分，原因请自己查看返回的邮件内容
				// 第一个分隔符后就是第一个附件的内容，解析邮件的各个附件
				List<StringBuffer> part = new ArrayList<StringBuffer>();
				StringBuffer partContent = new StringBuffer();
				while (!isEnd) {
					while (true) {
						line = reader.readLine();
						System.out.println(line);// 控制台输出得到的邮件所有内容
						if (line == null) {
							isEnd = true;
							break;
						} else if (line.equals("--" + boundary)) {
							// 此处要加"--"的原因，请自己去查看返回的邮件内容里的分隔符
							if (partContent.length() > 0) {
								System.out.println("#######NEW PART######");
								part.add(partContent);
							}
							partContent = new StringBuffer();
							break;
						} else if (line.equals("--" + boundary + "--")) {
							// 此处前后都要加"--"的原因，请自己去查看返回的邮件内容里的分隔符 结束此分隔符
							if (partContent.length() > 0) {
								System.out
										.println("#######NEW 2222222 PART######");
								part.add(partContent);
							}
							isEnd = true;
							break;
						} else {
							partContent.append(line);
						}
					}
				}
				System.out.println("=================================");
				System.out.println("邮件主题："
						+ this.getBase64Content(mailHeader.get("Subject")));
				System.out.println("邮件大小：" + content.length() + "字节");
				System.out.println("邮件总共有" + part.size() + "个部分");
				for (int i = 0; i < part.size(); i++) { // 一般第一个为邮件文字，第二个开始为邮件附件
					System.out.println("  part[" + (i + 1) + "]:"
							+ part.get(i).length() + "字节(包含头部信息)");
					System.out.println("字符串为：" + part.get(i));
					StringBuffer FuJian = part.get(i);
					int ImageJpg = FuJian.indexOf("Content-Type: image/jpeg");
					int msword = FuJian
							.indexOf("Content-Type: application/msword");
					int conf = FuJian
							.indexOf("Content-Type: application/octet-stream");
					// System.out.println("是否含有conf "+conf); // 有则输出 0，没有为 -1
					if (ImageJpg == 0) {

						// String fileName=i + ".jpg";
						// File file=new
						// File(Environment.getExternalStorageDirectory(),
						// fileName);
						String mailImage = (part.get(i))
								.substring((part.get(i))
										.indexOf("Content-Type") + 1);// 截取字符串
						mailImage = mailImage.split("\"")[4].toString();// 通过
																		// "和--
						mailImage = mailImage.split("--")[0].toString();// 对字符串进行分割，得到明文编码
						System.out.println("截取后的字符串：" + mailImage);
						byte[] base64Decoding = Base64.decode(mailImage,
								Base64.DEFAULT);
						writeImageToDisk(base64Decoding, mailFuJianNameArray[mailId2][i] );

					}
					if (msword == 0) {
						String mailImage = (part.get(i))
								.substring((part.get(i))
										.indexOf("Content-Type") + 1);// 截取字符串
						mailImage = mailImage.split("\"")[4].toString();// 通过
																		// "和--
						mailImage = mailImage.split("--")[0].toString();// 对字符串进行分割，得到明文编码
						System.out.println("截取后的字符串：" + mailImage);
						byte[] base64Decoding = Base64.decode(mailImage,
								Base64.DEFAULT);// 解码
						writeImageToDisk(base64Decoding,mailFuJianNameArray[mailId2][i]);
						// Files.write(
						// Paths.get("D:\\Eclipse\\eclipseJ2EE\\DocumentStorage\\MailWeb02Beta\\"
						// + i + ".doc"),
						// base64Decoding, StandardOpenOption.CREATE);
					}
					if (conf == 0) {
						String mailImage = (part.get(i))
								.substring((part.get(i))
										.indexOf("Content-Type") + 1);// 截取字符串
						mailImage = mailImage.split("\"")[4].toString();// 通过
																		// "和--
						mailImage = mailImage.split("--")[0].toString();// 对字符串进行分割，得到明文编码
						System.out.println("截取后的字符串：" + mailImage);
						byte[] base64Decoding = Base64.decode(mailImage,
								Base64.DEFAULT);// 解码
						writeImageToDisk(base64Decoding, mailFuJianNameArray[mailId2][i] );

					}
				}

			}
			 ContentActivity.toast = 1;
			// entryMainMenuInputMode();
		}

			break;
		case 4: {
			this.sendCmd("quit");
			if (getResp() == 0) {
				System.out.println("POP会话即将中止！");
				run = false;
			}
		}
			break;

		}
	}

	/**
	 * 通知UI线程提示用户输入邮件序号，并返回序号内容
	 * 
	 * @return
	 * @throws IOException
	 */
	public String getMailIDFromConsole() throws IOException {
		String ret = null;
		while (true) {
			System.out.print("请输入要邮件序号：");
			String line = reader.readLine();
			if (line.matches("\\d{1,4}")) {
				ret = line;
				break;
			} else {
				System.out.println("邮件序号格式不正确！");
			}
		}
		return ret;
	}

	/**
	 * 显示list邮件列表
	 * 
	 * @return
	 */
	public String getRespContent() {
		StringBuffer sb = new StringBuffer();
		String line = null;
		int respCode = 0;
		try {
			while ((line = reader.readLine()) != null) {// 得到服务器传来数据
				sb.append(line);
				sb.append("\r\n");
				if (line.equals(".")) {
					break;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			respCode = -1;
		}
		return respCode == 0 ? sb.toString() : null;
	}

	/**
	 * 读取邮件的头部信息
	 * 
	 * @param reader
	 * @return Map<String, String>
	 * @throws IOException
	 */
	private Map<String, String> getMailHeaderFromReader(BufferedReader reader)
			throws IOException {
		Map<String, String> mailHeader = new HashMap<String, String>();
		StringBuffer headerString = null;
		String line = null;
		// 从邮件内容中分析邮件头信息
		while (true) {
			line = reader.readLine();
			// System.out.println("line:"+line);
			if (line.indexOf(':') > 0) {
				if (headerString != null) {
					int index;
					if ((index = headerString.indexOf(":")) > 0) {
						String headerName = headerString.substring(0, index)
								.trim();
						String headerValue = headerString.substring(index + 1)
								.trim();
						mailHeader.put(headerName, headerValue);

					}
				}
				headerString = new StringBuffer();
				headerString.append(line.trim());
			} else if (line.length() > 0 && line.indexOf(":") == -1) {
				if (headerString != null) {
					headerString.append(line.trim());
				}
			} else {
				if (headerString != null && headerString.length() > 0) {
					int index;
					if ((index = headerString.indexOf(":")) > 0) {// 避免头文件最后带编码的只记录了存在“：”的行
						String headerName = headerString.substring(0, index)
								.trim();
						String headerValue = headerString.substring(index + 1)
								.trim();
						mailHeader.put(headerName, headerValue);

					}
				}
				break;
			}
		}
		return mailHeader;
	}

	/**
	 * 翻译采用Base64编码的内容串<br>
	 * 内容格式如：=?GB2312?B?u9i4tDogyPyw2S3E3MGmzOHJ/cH5xtottPrC68nzvMa94bn70+
	 * s0QcjV1r692M28?=
	 * 
	 * @param base64Str
	 * @return
	 */
	public String getBase64Content(String base64Str) {// 解码邮件头重要信息
		// =?GB2312?B?u9i4tDogyPyw2S3E3MGmzOHJ/cH5xtottPrC68nzvMa94bn70+s0QcjV1r692M28?=
		String content = "";
		if (base64Str.startsWith("=?") && base64Str.endsWith("?=")) {// 判断邮件头含
			// =?xxx?B?xxxxxx?=
			// 的字符串
			base64Str = base64Str.substring(2, base64Str.length() - 2);// 提取邮件头
			// =?xxx?B?xxxxxx?=
			// 字符串的信息
			String[] strs = base64Str.split("\\?");// 用 ? 来分割字符串
			String charset = strs[0];
			base64Str = strs[2];// 编码的字符串赋值
			if ((strs[1] != null && strs[1].equals("B"))
					|| (strs[1] != null && strs[1].equals("b"))) {// 邮件主题有大写和小写的B
				try {
					content = new String(Base64.decode(base64Str,
							Base64.DEFAULT), charset);// 解码字符串
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return content;// 返回已经解码了的邮件头信息
	}

	/**
	 * 将发件人信息翻译成明文
	 * 
	 * @param str
	 * @return
	 */
	private String translateMailSender(String str) {
		StringBuffer ret = new StringBuffer();
		String[] mans = str.split(",");
		if (mans != null && mans.length > 0) {
			for (int i = 0; i < mans.length; i++) {
				String[] title = mans[i].split(" ");
				if (title[0].indexOf("<") == -1) {
					ret.append(getBase64Content(title[0]));
				} else {
					ret.append(title[0]);
				}
				if (title.length > 1) {
					ret.append(title[1]);
				}
				ret.append(",");
			}
		}

		return ret.toString();
	}
	/**
	 * 将发件时间翻译成明文
	 * @param str
	 * @return 
	 * @return
	 */
private String translateMailDate(String str){
	String ret = new String();
	String[] mans = str.split("\\s+");
	System.out.println(">>>>>>>>>>>>>>>"+mans[1] +"+"+ mans[2] + "<<<<<<<<<<<<<<<<<");
	if(mans[2].indexOf("Jan")!=-1){ret = "1月"+mans[1]+"日";System.out.println("1月");}
	if(mans[2].indexOf("Feb")!=-1){ret = "2月"+mans[1]+"日";System.out.println("2月");}
	if(mans[2].indexOf("Mar")!=-1){ret = "3月"+mans[1]+"日";System.out.println("3月");}
	if(mans[2].indexOf("Apr")!=-1){ret = "4月"+mans[1]+"日";System.out.println("4月");}
	if(mans[2].indexOf("May")!=-1){ret = "5月"+mans[1]+"日";System.out.println("5月");}
	if(mans[2].indexOf("Jun")!=-1){ret = "6月"+mans[1]+"日";System.out.println("6月");}
	if(mans[2].indexOf("Jul")!=-1){ret = "7月"+mans[1]+"日";System.out.println("7月");}
	if(mans[2].indexOf("Aug")!=-1){ret = "8月"+mans[1]+"日";System.out.println("8月");}
	if(mans[2].indexOf("Sep")!=-1 ){ret = "9月"+mans[1]+"日";System.out.println("9月");}
	if(mans[2].indexOf("Oct")!=-1){ret = "10月"+mans[1]+"日";System.out.println("10月");}
	if(mans[2].indexOf("Nov")!=-1){ret = "11月"+mans[1]+"日";System.out.println("11月");}
	if(mans[2].indexOf("Dec")!=-1){ret = "12月"+mans[1]+"日";System.out.println("12月");}
	System.out.println("ret："+ret);
	return ret;
}
/**
 * 将发件时间翻译成详细明文
 * @return
 */
private String translateMailDetailedDate(String str){
	String ret = new String();
	String[] mans = str.split("\\s+");
	System.out.println(">>>>>>>>>>>>>>>"+mans[1] +"+"+ mans[2] +"+"+ mans[3] +"+"+mans[4] + "<<<<<<<<<<<<<<<<<");
	if(mans[2].indexOf("Jan")!=-1){ret = mans[3] +"年" + "1月"+mans[1]+"日"+mans[4];System.out.println("1月");}
	if(mans[2].indexOf("Feb")!=-1){ret =  mans[3] +"年" +"2月"+mans[1]+"日"+mans[4];System.out.println("2月");}
	if(mans[2].indexOf("Mar")!=-1){ret =  mans[3] +"年" +"3月"+mans[1]+"日"+mans[4];System.out.println("3月");}
	if(mans[2].indexOf("Apr")!=-1){ret =  mans[3] +"年" +"4月"+mans[1]+"日"+mans[4];System.out.println("4月");}
	if(mans[2].indexOf("May")!=-1){ret =  mans[3] +"年" +"5月"+mans[1]+"日"+mans[4];System.out.println("5月");}
	if(mans[2].indexOf("Jun")!=-1){ret =  mans[3] +"年" +"6月"+mans[1]+"日"+mans[4];System.out.println("6月");}
	if(mans[2].indexOf("Jul")!=-1){ret =  mans[3] +"年" +"7月"+mans[1]+"日"+mans[4];System.out.println("7月");}
	if(mans[2].indexOf("Aug")!=-1){ret =  mans[3] +"年" +"8月"+mans[1]+"日"+mans[4];System.out.println("8月");}
	if(mans[2].indexOf("Sep")!=-1 ){ret = mans[3] +"年" +"9月"+mans[1]+"日"+mans[4];System.out.println("9月");}
	if(mans[2].indexOf("Oct")!=-1){ret = mans[3] +"年" + "10月"+mans[1]+"日"+mans[4];System.out.println("10月");}
	if(mans[2].indexOf("Nov")!=-1){ret =  mans[3] +"年" +"11月"+mans[1]+"日"+mans[4];System.out.println("11月");}
	if(mans[2].indexOf("Dec")!=-1){ret =  mans[3] +"年" +"12月"+mans[1]+"日"+mans[4];System.out.println("12月");}
	System.out.println("ret："+ret);
	return ret;
}
	/**
	 * 将图片写入到磁盘
	 * 
	 * @param img
	 *            图片数据流
	 * @param fileName
	 *            文件保存时的名称
	 */
	public void writeImageToDisk(byte[] img, String fileName) {
		try {
			File file = new File("/mnt/sdcard/MailBeta_zyt/DownLoad/"
					+ fileName);
			FileOutputStream fops = new FileOutputStream(file);
			fops.write(img);
			fops.flush();
			fops.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 登录后判断是否存在邮件状态记录文件
	 * 
	 * @return
	 */
	public int fileIsExists() {
		System.out.println("进入状态文件判断");
		File f = new File("/mnt/sdcard/MailBeta_zyt/mailstate.txt");
		if (!f.exists()) {
			System.out.println("无状态文件，创建路径");
			File createFile = new File("/mnt/sdcard/MailBeta_zyt");
			createFile.mkdirs();
			return 0;
		} else {
			System.out.println("有状态文件，返回1");
			return 1;
		}
	}

	String str = "";

	/**
	 * 读取邮件状态记录文件序号
	 */
	public void readMailState() throws IOException {
		File urlFile = new File("/mnt/sdcard/MailBeta_zyt/mailstate.txt");
		InputStreamReader isr = new InputStreamReader(new FileInputStream(
				urlFile), "UTF-8");
		BufferedReader br = new BufferedReader(isr);

		String mimeTypeLine = null;
		while ((mimeTypeLine = br.readLine()) != null) {// 读取序号
			str = str + mimeTypeLine;
		}
	}

	/**
	 * 写， 读sdcard目录上的文件，要用FileOutputStream， 不能用openFileOutput
	 * 不同点：openFileOutput是在raw里编译过的，FileOutputStream是任何文件都可以
	 * 
	 * @param fileName
	 * @param message
	 */
	// 写在/mnt/sdcard/目录下面的文件
	public void writeMailState(String fileName, String message) {

		try {

			FileOutputStream fout = new FileOutputStream(fileName);

			byte[] bytes = message.getBytes();

			fout.write(bytes);

			fout.close();

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	/**
	 * 想主线程发送message
	 * 
	 * @param msg
	 */
	public void notifyActivity(int msg) {
		Message message = new Message();
		if (msg == 1) {
			message.what = LoginActivity.toListActivity;
		}
		if (msg == -1) {
			message.what = LoginActivity.toExit;
		}
		if (msg == 2) {
			message.what = LoginActivity.toContentActivity;
		}
		LoginActivity.handler.sendMessage(message); // 将Message对象发送出去

	}

	// /**
	// * 将用户的操作记录到mysql
	// */
	// public void InsertMysql(String mailUser, String mailValue, String
	// mailNumber) {
	// Connection con = null;
	// PreparedStatement pst = null;
	//
	// String url = "jdbc:mysql://localhost:3306/";
	// String db = "student";
	// String driver = "com.mysql.jdbc.Driver";
	// String user = "root";
	// String pass = "12345678";
	//
	// try {
	// Class.forName(driver);
	// con = DriverManager.getConnection(url + db, user, pass);
	// con.setAutoCommit(true);// Disables auto-commit.
	//
	// String sql =
	// "insert into mail_db(mail_user,mail_value,mail_number) values(?,?,?) ";
	// pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	//
	// pst.setString(1, mailUser);
	// pst.setString(2, mailValue);
	// pst.setString(3, mailNumber);
	// pst.executeUpdate();
	// pst.close();
	// con.close();
	//
	// } catch (Exception e) {
	// System.out.println(e);
	// }
	// }
	//
	// /**
	// * 查询本序号邮件是否有阅读记录
	// *
	// * @param mailUser
	// * @param mailNumber
	// * @return
	// */
	// public int mailStateInMysql(String mailUser, int mailNumber) {
	// // 驱动程序名
	// String driver = "com.mysql.jdbc.Driver";
	// // 数据表url
	// String url = "jdbc:mysql://localhost:3306/student";
	// // MySQL用户名
	// String user = "root";
	// // MySQL密码
	// String password = "12345678";
	// // 存储返回状态
	// String state = null;
	// // 开始连接数据库
	// try {
	// // 加载驱动程序
	// Class.forName(driver);
	// // 连续数据库
	// Connection conn = DriverManager.getConnection(url, user, password);
	// if (!conn.isClosed())
	// System.out.println("connecting to the database successfully!");
	// // statement用来执行SQL语句
	// Statement statement = conn.createStatement();
	// // select语句
	// String sql =
	// "select id,mail_user,mail_value,mail_number from mail_db where mail_user='"
	// + mailUser
	// + "' and mail_value='2' and mail_number='" + mailNumber + "'";
	// ResultSet rs = statement.executeQuery(sql);
	//
	// // 输出student的所有信息
	// while (rs.next()) {
	// state = (rs.getString("mail_user") + "\t" + rs.getString("mail_value") +
	// "\t"
	// + rs.getString("mail_number"));
	// }
	//
	// rs.close();
	// conn.close();
	// if (state == null) {
	// return -1;
	// } else {
	// return 1;
	// }
	// } catch (ClassNotFoundException e) {
	// System.out.println("sorry, can't find the driver!");
	// e.printStackTrace();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return -1;
	// }

	/**
	 * 通知UI线程，可以处理用户操作指令了
	 */
	public void entryMainMenuInputMode() {
		synchronized (lock) {
			lock.notify();// 唤醒某一个线程
		}
	}

}
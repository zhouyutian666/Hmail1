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
	private String mailorder = null;// �������к�
	private static String InputOk2 = null;// �Ķ��ʼ����
	static int mailnumber = 0;
	StringBuffer mailNumberout = new StringBuffer();// ��¼�ڼ���
	static String mailNumberArray[] = new String[100000];
	StringBuffer mailTitleout = new StringBuffer();// ��¼�ʼ�����
	static String mailTitleArray[] = new String[100000];
	StringBuffer mailDateout = new StringBuffer();// ��¼��������
	static String mailDateArray[] = new String[100000];
	static String mailDetailedDateArray[] = new String[100000];// ��¼��ϸ����
	StringBuffer mailFromout = new StringBuffer();// ��¼������
	static String mailFromArray[] = new String[100000];
	StringBuffer mailStateout = new StringBuffer();// ��¼�ʼ�״̬
	static String mailStateArray[] = new String[100000];
	static String[] mailContentArray = new String[100000];// �ʼ�����
	static String[] mailFuJianNumArray = new String[100000];// �ʼ�������
	static String[][] mailFuJianNameArray = new String[1000][1000];// ������
	static String mailToArray[] = new String[100000];// �ռ���
	StringBuffer mailExistFuJian;
	static String mailExistFuJianArray[] = new String[100000];// �жϴ��ڸ���
	static int Judgelock = 1;

	// Context context;

	/**
	 * �õ��ʼ�����
	 */
	public void setmailnumber(int mailnumber) {
		this.mailnumber = mailnumber;
		// System.out.println("��̨�е��ʼ����⣺" + mailTitleArray[1]);
		notifyActivity(1);
		// LoginActivity.entryMainMenuInputMode();
		// LoginActivity.StartNextActivity();
		// ReturnHome.entryMainMenuInputMode();
	}

	public void setMailUser(String mailUser) {// �õ����û���
		this.mailUser = mailUser;
		// System.out.println(this.mailUser);
	}

	public void setMailPass(String mailPass) {// �õ�������
		this.mailPass = mailPass;
		// System.out.println("Pass:"+this.mailPass);
	}

	public void setOrder(String mailorder) {// �õ��������
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
	 * �õ��û���Ҫ��ȡ���ʼ����
	 * 
	 * @param InputOk2
	 */
	public void setNumber(String InputOk2) {// �õ��ʼ����
		this.InputOk2 = InputOk2;
	}

	/**
	 * �����������������
	 * 
	 * @return
	 */
	private int connectToPopServ() {
		int ret = 0;
		try {
			this.sock = new Socket("pop3.163.com", 110);// �����������������
			this.sock.setSoTimeout(10 * 000);
			// this.sock.connect(new InetSocketAddress("pop.163.com", 110),
			// 10*000);
			in = new DataInputStream(this.sock.getInputStream());
			reader = new BufferedReader(new InputStreamReader(in));
			out = new DataOutputStream(this.sock.getOutputStream());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("�����������ʧ�ܣ�����ret = -1");
			ret = -1;
		}
		return ret;// ���ӳɹ���ret = 0
	}

	/**
	 * �жϷ������Ƿ񷵻�+ok
	 * 
	 * @return
	 */
	public int getResp() {// �жϷ������Ƿ񷵻� +ok
		int ret = 0;
		String line = null;
		try {
			line = reader.readLine();
			System.out.println("���������أ�" + line);
			if (line.startsWith("+OK")) { // ���������� +ok �� ret = 0
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

	static int num2 = 0;// �ʼ�����

	/**
	 * list����+ok
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
			System.out.println("���������أ�" + line);
			if (line.startsWith("+OK")) { // ���������� +ok �� ret = 0
				num1 = line.split(" ");
				num2 = (int) Double.parseDouble(num1[1]);
				System.out.println("�ʼ�������" + num2);
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
	 * �ж��û��ʺ������Ƿ���ȷ�� ��ȷ��� ����¼�����ʺųɹ���
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
						System.out.println("��¼�����ʺųɹ���");
						loginsuccess = true;

					}
				}
			}
		}

		return ret;
	}

	/**
	 * ��������ļ���pass�������Ϊ******�� ����ֱ�����
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
	 * �ж��û����������Ƿ�Ϊ1��4
	 * 
	 * @param input
	 * @return
	 * @throws IOException
	 */
	private void postUserInput(String input) throws Exception {// ����������
		String order = input; // ��ò������к�
		if (order.matches("[1-4]{1}")) {
			int choice = Integer.parseInt(order);// ��ȡ����̨����ת��Ϊintֵ
			boolean isBusy = true;

			synchronized (lock) {
				if (waitToProcessInput == 0) {
					isBusy = false;
					waitToProcessInput = choice;
					lock.notify();

				}
			}
			if (isBusy) {
				// Toast.makeText(context, "��̨�߳����ڴ����У����Ժ���ѡ�����ָ�",
				// Toast.LENGTH_SHORT).show();
				System.out.println("��̨�߳����ڴ����У����Ժ���ѡ�����ָ�");
			}
		} else {

			// Toast.makeText(context, "������ָ����Χ�ڵ���ţ���������",
			// Toast.LENGTH_SHORT).show();
			System.out.println("invalidate cmd !");
		}

	}

	public void run() {
		// TODO Auto-generated method stub
		if (connectToPopServ() == 0) {// ret = 0
			if (getResp() == 0) {// ���������� +ok �� getResp() = 0
				if (this.login() == 0) { // �����¼�����ʺųɹ�
					waitToProcessInput = 1;// ��¼��������ʾ�ʼ��б�
					do {
						// System.out.println(this+":==>"+waitToProcessInput);
						synchronized (lock) {
							if (waitToProcessInput == 0) {
								try {
									// System.out.println("1��ˢ�� 1��ˢ�� 1��ˢ��");
									lock.wait(1000);// �ȴ����룬1000����ˢ��һ��
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							} else {
								int value = waitToProcessInput;
								waitToProcessInput = 0;
								try {
									System.out.println("��ʼִ����Ŷ�ָ��");
									process(value);
									System.out.println("��ִ����Ŷ�ָ��");// �û�����Ĳ���������Ӧ������
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();

									run = false;
								}

							}
						}
						// ��ֹ�ʼ��Ự��ʱ�Ĵ���
						if (new Date().getTime() - lastCommuTime.getTime() > 10000) {
							this.sendCmd("NOOP");
							if (this.getResp() == 0) {
								// �Ự����
								entryMainMenuInputMode();
							} else {
								System.out.println("�ʼ��Ự�Ͽ���");
								run = false;
							}
						}
					} while (run);

				} else {
					System.out.println("�ʺ����벻��ȷ�������˳���");
					entryMainMenuInputMode();
					// System.exit(1);
				}
			}
		} else {
			System.out.println("Can't connect to pop server :" + pop_address);
		}
notifyActivity(-1);
		
	}

	// // ����FileOutputStream����
	// FileOutputStream outputStream = null;
	// // ����BufferedOutputStream����
	// BufferedOutputStream bufferedOutputStream = null;

	/**
	 * ���û��������Ž���ִ��
	 * 
	 * @param value
	 * @throws IOException
	 * @throws Exception
	 */
	private void process(int value) throws IOException {
		switch (value) {
		case 1: {
			if (fileIsExists() == 0) {// �жϼ�¼�ʼ�״̬�ļ�����
				File file = new File("/mnt/sdcard/MailBeta_zyt/"
						+ "mailstate.txt");
				file.createNewFile();
				System.out.println("�Ѿ�������״̬��¼�ļ���");
			}

			readMailState();// ��ȡ�ʼ�״̬�ļ�

			// InsertMysql(mailUser, "1", "0");
			this.sendCmd("list");
			if (getResplist() == 0) {// �õ��ʼ����� num2
				System.out.println(getRespContent());
			}
			for (int mailId = 1; mailId <= num2; mailId++) {

				this.sendCmd("retr " + mailId);
				if (getResp() == 0) {
					String content = getRespContent();// �õ��ʼ�����������
					// System.out.println("�ʼ���С��"+content.length()+"�ֽ�");
					BufferedReader reader = new BufferedReader(
							new StringReader(content));
					String line = null;
					// �õ��ʼ�ͷ����Ϣ
					Map<String, String> mailHeader = this
							.getMailHeaderFromReader(reader);
					System.out.println("===============�� " + mailId
							+ " ��==================");
					mailNumberout.append("�� " + mailId + " ��");
					mailNumberArray[mailId] = "�� " + mailId + " ��";

					System.out.println("�ʼ����⣺"
							+ this.getBase64Content(mailHeader.get("Subject")));
					mailTitleout.append("�ʼ����⣺"
							+ this.getBase64Content(mailHeader.get("Subject")));
					mailTitleArray[mailId] = this.getBase64Content(mailHeader
							.get("Subject"));

//					System.out.println("�������ڣ�" + mailHeader.get("Date"));
//					mailDateout.append("�������ڣ�" + mailHeader.get("Date"));
					
					mailDateArray[mailId] = translateMailDate(mailHeader.get("Date"));
					System.out.println("�������ڣ�" +mailDateArray[mailId]);

					System.out.println("�����ˣ�"
							+ this.translateMailSender(mailHeader.get("From")));
					mailFromout.append("�����ˣ�"
							+ this.translateMailSender(mailHeader.get("From")));
					mailFromArray[mailId] = this.translateMailSender(mailHeader
							.get("From"))+" ";

					System.out.println("�ռ��ˣ�" + mailHeader.get("To"));
					mailToArray[mailId] = mailHeader.get("To");
					String mailIdString = Integer.toString(mailId);
					if (str.contains("/" + mailIdString + "/")) {
						mailStateArray[mailId] = "�Ѷ�";
					} else {
						mailStateArray[mailId] = "δ��";
					}

					System.out
							.println("==========================================");
					// ����ʼ����ڶ����ɲ��֣�����ʼ�ͷ��Ϣ�еõ��ָ���
					String boundary = null;
					String contentType = mailHeader.get("Content-Type");
					System.out.println("Content-Type" + contentType);
					if (contentType.indexOf("multipart") > -1) {// �Ƿ�Ϊ�������ʼ�
						boundary = contentType.substring(contentType
								.indexOf("boundary=") + 9);// ǰ9���ַ�Ϊboundary=��������Ƿָ���
						boundary = boundary.trim().replaceAll("\"", "");
						// System.out.println("��һ��ಿ�ֹ��ɵ��ʼ����ָ���Ϊ:["+boundary+"]");
					}

					boolean isEnd = false;
					// �����ҵ���һ���ָ����������ʼ�ͷ���һ���ָ����м�Ĳ��֣�ԭ��鿴���ص��ʼ�����
					// ��һ���ָ�������ǵ�һ�����������ݣ������ʼ��ĸ�������
					List<StringBuffer> part = new ArrayList<StringBuffer>();
					StringBuffer partContent = new StringBuffer();
					while (!isEnd) {
						while (true) {
							line = reader.readLine();
							// System.out.println(line);// ����̨����õ����ʼ���������
							if (line == null) {
								isEnd = true;
								break;
							} else if (line.equals("--" + boundary)) {
								// �˴�Ҫ��"--"��ԭ�򣬲鿴���ص��ʼ�������ķָ���
								if (partContent.length() > 0) {
									System.out.println("#######NEW PART######");
									part.add(partContent);
								}
								partContent = new StringBuffer();
								break;
							} else if (line.equals("--" + boundary + "--")) {
								// �˴�ǰ��Ҫ��"--"��ԭ�򣬲鿴���ص��ʼ�������ķָ��� �����˷ָ���
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

					System.out.println("�ʼ����⣺"
							+ this.getBase64Content(mailHeader.get("Subject")));

					System.out.println("�ʼ���С��" + content.length() + "�ֽ�");

					System.out.println("�ʼ��ܹ���" + part.size() + "������");
					for (int i = 0; i < part.size(); i++) {
						int mailId2 = mailId;
						System.out.println("  part[" + (i + 1) + "]:"
								+ part.get(i).length() + "�ֽ�(����ͷ����Ϣ)");
						System.out.println("�ַ���Ϊ��" + part.get(i));
						if (i != 0) {
							int FuJianNum = part.size() - 1;
							System.out.println("�ʼ��а�������!" + "\n" + "������Ϊ�� "
									+ FuJianNum);// ����������ʾ�ʼ���������

							String FuJianNum_2 = Integer.toString(FuJianNum);
							mailFuJianNumArray[mailId2] = FuJianNum_2;// ��ʾ��ҳ���ϵĸ�����
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
				String content = getRespContent();// �õ��ʼ�����������
				// System.out.println("�ʼ���С��"+content.length()+"�ֽ�");
				BufferedReader reader = new BufferedReader(new StringReader(
						content));
				String line = null;
				// �õ��ʼ�ͷ����Ϣ
				Map<String, String> mailHeader = this
						.getMailHeaderFromReader(reader);

				mailDetailedDateArray[mailId2] = translateMailDetailedDate(mailHeader.get("Date"));
				System.out.println("�������ڣ�" +mailDetailedDateArray[mailId2]);
				// ����ʼ����ڶ����ɲ��֣�����ʼ�ͷ��Ϣ�еõ��ָ���
				String boundary = null;
				String contentType = mailHeader.get("Content-Type");
				System.out.println("Content-Type" + contentType);
				if (contentType.indexOf("multipart") > -1) {// �Ƿ�Ϊ�������ʼ�
					boundary = contentType.substring(contentType
							.indexOf("boundary=") + 9);// ǰ9���ַ�Ϊboundary=��������Ƿָ���
					boundary = boundary.trim().replaceAll("\"", "");
					// System.out.println("��һ��ಿ�ֹ��ɵ��ʼ����ָ���Ϊ:["+boundary+"]");
				}

				boolean isEnd = false;
				// �����ҵ���һ���ָ����������ʼ�ͷ���һ���ָ����м�Ĳ��֣�ԭ��鿴���ص��ʼ�����
				// ��һ���ָ�������ǵ�һ�����������ݣ������ʼ��ĸ�������
				List<StringBuffer> part = new ArrayList<StringBuffer>();
				StringBuffer partContent = new StringBuffer();
				while (!isEnd) {
					while (true) {
						line = reader.readLine();
//						System.out.println(line);// ����̨����õ����ʼ���������
						if (line == null) {
							isEnd = true;
							break;
						} else if (line.equals("--" + boundary)) {
							// �˴�Ҫ��"--"��ԭ�򣬲鿴���ص��ʼ�������ķָ���
							if (partContent.length() > 0) {
								System.out.println("#######NEW PART######");
								part.add(partContent);
							}
							partContent = new StringBuffer();
							break;
						} else if (line.equals("--" + boundary + "--")) {
							// �˴�ǰ��Ҫ��"--"��ԭ�򣬲鿴���ص��ʼ�������ķָ��� �����˷ָ���
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
				System.out.println("�ʼ����⣺"
						+ this.getBase64Content(mailHeader.get("Subject")));
				out.append("�ʼ����⣺"
						+ this.getBase64Content(mailHeader.get("Subject")));
				System.out.println("�ʼ���С��" + content.length() + "�ֽ�");
				out.append("�ʼ���С��" + content.length() + "�ֽ�");
				System.out.println("�ʼ��ܹ���" + part.size() + "������");
				for (int i = 0; i < part.size(); i++) {
					System.out.println("  part[" + (i + 1) + "]:"
							+ part.get(i).length() + "�ֽ�(����ͷ����Ϣ)");
					System.out.println("�ַ���Ϊ��" + part.get(i));
					if (i != 0) {
						int FuJianNum = part.size() - 1;
						System.out.println("�ʼ��а�������!" + "\n" + "������Ϊ�� "
								+ FuJianNum);// ����������ʾ�ʼ���������
						out.append("�ʼ��а�������!" + "\n" + "������Ϊ�� " + FuJianNum);
						String FuJianNum_2 = Integer.toString(FuJianNum);
						mailFuJianNumArray[mailId2] = FuJianNum_2;// ��ʾ��ҳ���ϵĸ�����
						String mailFuJianName = (part.get(i)).substring((part
								.get(i)).indexOf("name=\"") + 6);
						System.out.println("��������ȡ1��" + mailFuJianName);// ��ȡname="֮����ַ���
						mailFuJianName = mailFuJianName
								.split("\"Content-Transfer-Encoding:")[0]
								.toString();
						System.out.println("��������ȡ2��" + mailFuJianName);
						if (mailFuJianName.contains("?")) {// ���������������
							String FuJianNameCharset;
							String FuJianNameTxt = "";
							FuJianNameCharset = mailFuJianName.split("[?]")[1]
									.toString();
							System.out.println("�����������ʽ��ȡ3��"
									+ FuJianNameCharset);
							FuJianNameTxt = mailFuJianName.split("[?]")[3]
									.toString();
							System.out.println("�������������ݽ�ȡ4��" + FuJianNameTxt);
							byte[] asBytes = Base64.decode(FuJianNameTxt,
									Base64.DEFAULT);
							;// ͨ��base64���������
							System.out.println(new String(asBytes,
									FuJianNameCharset));
							mailFuJianNameArray[mailId2][i] = new String(asBytes,
									FuJianNameCharset);
						} else {
							mailFuJianNameArray[mailId2][i] = mailFuJianName;
						}
					}
					if (i == 0) {// ��һ����������һ��Ϊ����
						System.out.println(part.get(i));
						String mailTxt = (part.get(i)).substring((part.get(i))
								.indexOf("base64") + 6);// ��ȡbase64֮����ַ���
						mailTxt = mailTxt.split("--")[0].toString();// ͨ�� "--"
						// ���ַ������зָ�õ����ı���
						System.out.println("��ȡ����ַ�����" + mailTxt);

						String mailTxtCharest = (part.get(i)).substring((part
								.get(i)).indexOf("charset") + 8);// ��ȡcharest=֮����ַ���
						mailTxtCharest = mailTxtCharest
								.split("Content-Transfer-Encoding:")[0]
								.toString();// ͨ��
						System.out.println("��ȡ����ַ���2��" + mailTxtCharest); // ���ַ������зָ�õ����ı����ʽ

						if (mailTxtCharest.contains("\"")) {// gb18030�ı����ʽ��˫���ţ���Ҫȥ�����磺Content-Type:
															// text/plain;
							// charset="gb18030"
							mailTxtCharest = mailTxtCharest.replace("\"", "");
						}
						byte[] asBytes = Base64.decode(mailTxt, Base64.DEFAULT);// ͨ��base64���������
						System.out.println(new String(asBytes, mailTxtCharest));
						out.append(new String(asBytes, mailTxtCharest));
						mailContentArray[mailId2] = new String(asBytes,
								mailTxtCharest);

						System.out.println("mailId2 = " + mailId2);
						System.out.println("�ʼ��������飺"
								+ mailContentArray[mailId2]);
						// ShowContent.entryMainMenuInputMode();

					}
				}
			}
//			do {
//				// MailListActivity.entryMainMenuInputMode();
//			} while (Judgelock == 1);// ���ⷢ��������ǰ�����ʼ��������ǰ�ͷ��������������޷��ͷŵ����������
			writeMailState("/mnt/sdcard/MailBeta_zyt/mailstate.txt", str + "/"
					+ mailId + "/");// ���Ķ��ʼ����¼�Ķ��ʼ������
			str = str + "/" + mailId + "/";// ��֮ǰ���Ķ���ż���ǰ��
			
			notifyActivity(2);
		}
			break;
		case 3: {
			String mailId = InputOk2;
			int mailId2 = Integer.parseInt(mailId);
			this.sendCmd("retr " + mailId);
			// InsertMysql(mailUser, "3", mailId);
			if (getResp() == 0) {
				String content = getRespContent();// �õ��ʼ�����������
				// System.out.println("�ʼ���С��"+content.length()+"�ֽ�");
				BufferedReader reader = new BufferedReader(new StringReader(
						content));
				String line = null;
				// �õ��ʼ�ͷ����Ϣ
				Map<String, String> mailHeader = this
						.getMailHeaderFromReader(reader);

				// ����ʼ����ڶ����ɲ��֣�����ʼ�ͷ��Ϣ�еõ��ָ���
				String boundary = null;
				String contentType = mailHeader.get("Content-Type");
				System.out.println("Content-Type" + contentType);
				if (contentType.indexOf("multipart") > -1) {// �Ƿ�Ϊ�������ʼ�
					boundary = contentType.substring(contentType
							.indexOf("boundary=") + 9);// ǰ9���ַ�Ϊboundary=��������Ƿָ���
					boundary = boundary.trim().replaceAll("\"", "");
					// System.out.println("��һ��ಿ�ֹ��ɵ��ʼ����ָ���Ϊ:["+boundary+"]");
				}

				boolean isEnd = false;
				// �����ҵ���һ���ָ����������ʼ�ͷ���һ���ָ����м�Ĳ��֣�ԭ�����Լ��鿴���ص��ʼ�����
				// ��һ���ָ�������ǵ�һ�����������ݣ������ʼ��ĸ�������
				List<StringBuffer> part = new ArrayList<StringBuffer>();
				StringBuffer partContent = new StringBuffer();
				while (!isEnd) {
					while (true) {
						line = reader.readLine();
						System.out.println(line);// ����̨����õ����ʼ���������
						if (line == null) {
							isEnd = true;
							break;
						} else if (line.equals("--" + boundary)) {
							// �˴�Ҫ��"--"��ԭ�����Լ�ȥ�鿴���ص��ʼ�������ķָ���
							if (partContent.length() > 0) {
								System.out.println("#######NEW PART######");
								part.add(partContent);
							}
							partContent = new StringBuffer();
							break;
						} else if (line.equals("--" + boundary + "--")) {
							// �˴�ǰ��Ҫ��"--"��ԭ�����Լ�ȥ�鿴���ص��ʼ�������ķָ��� �����˷ָ���
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
				System.out.println("�ʼ����⣺"
						+ this.getBase64Content(mailHeader.get("Subject")));
				System.out.println("�ʼ���С��" + content.length() + "�ֽ�");
				System.out.println("�ʼ��ܹ���" + part.size() + "������");
				for (int i = 0; i < part.size(); i++) { // һ���һ��Ϊ�ʼ����֣��ڶ�����ʼΪ�ʼ�����
					System.out.println("  part[" + (i + 1) + "]:"
							+ part.get(i).length() + "�ֽ�(����ͷ����Ϣ)");
					System.out.println("�ַ���Ϊ��" + part.get(i));
					StringBuffer FuJian = part.get(i);
					int ImageJpg = FuJian.indexOf("Content-Type: image/jpeg");
					int msword = FuJian
							.indexOf("Content-Type: application/msword");
					int conf = FuJian
							.indexOf("Content-Type: application/octet-stream");
					// System.out.println("�Ƿ���conf "+conf); // ������� 0��û��Ϊ -1
					if (ImageJpg == 0) {

						// String fileName=i + ".jpg";
						// File file=new
						// File(Environment.getExternalStorageDirectory(),
						// fileName);
						String mailImage = (part.get(i))
								.substring((part.get(i))
										.indexOf("Content-Type") + 1);// ��ȡ�ַ���
						mailImage = mailImage.split("\"")[4].toString();// ͨ��
																		// "��--
						mailImage = mailImage.split("--")[0].toString();// ���ַ������зָ�õ����ı���
						System.out.println("��ȡ����ַ�����" + mailImage);
						byte[] base64Decoding = Base64.decode(mailImage,
								Base64.DEFAULT);
						writeImageToDisk(base64Decoding, mailFuJianNameArray[mailId2][i] );

					}
					if (msword == 0) {
						String mailImage = (part.get(i))
								.substring((part.get(i))
										.indexOf("Content-Type") + 1);// ��ȡ�ַ���
						mailImage = mailImage.split("\"")[4].toString();// ͨ��
																		// "��--
						mailImage = mailImage.split("--")[0].toString();// ���ַ������зָ�õ����ı���
						System.out.println("��ȡ����ַ�����" + mailImage);
						byte[] base64Decoding = Base64.decode(mailImage,
								Base64.DEFAULT);// ����
						writeImageToDisk(base64Decoding,mailFuJianNameArray[mailId2][i]);
						// Files.write(
						// Paths.get("D:\\Eclipse\\eclipseJ2EE\\DocumentStorage\\MailWeb02Beta\\"
						// + i + ".doc"),
						// base64Decoding, StandardOpenOption.CREATE);
					}
					if (conf == 0) {
						String mailImage = (part.get(i))
								.substring((part.get(i))
										.indexOf("Content-Type") + 1);// ��ȡ�ַ���
						mailImage = mailImage.split("\"")[4].toString();// ͨ��
																		// "��--
						mailImage = mailImage.split("--")[0].toString();// ���ַ������зָ�õ����ı���
						System.out.println("��ȡ����ַ�����" + mailImage);
						byte[] base64Decoding = Base64.decode(mailImage,
								Base64.DEFAULT);// ����
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
				System.out.println("POP�Ự������ֹ��");
				run = false;
			}
		}
			break;

		}
	}

	/**
	 * ֪ͨUI�߳���ʾ�û������ʼ���ţ��������������
	 * 
	 * @return
	 * @throws IOException
	 */
	public String getMailIDFromConsole() throws IOException {
		String ret = null;
		while (true) {
			System.out.print("������Ҫ�ʼ���ţ�");
			String line = reader.readLine();
			if (line.matches("\\d{1,4}")) {
				ret = line;
				break;
			} else {
				System.out.println("�ʼ���Ÿ�ʽ����ȷ��");
			}
		}
		return ret;
	}

	/**
	 * ��ʾlist�ʼ��б�
	 * 
	 * @return
	 */
	public String getRespContent() {
		StringBuffer sb = new StringBuffer();
		String line = null;
		int respCode = 0;
		try {
			while ((line = reader.readLine()) != null) {// �õ���������������
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
	 * ��ȡ�ʼ���ͷ����Ϣ
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
		// ���ʼ������з����ʼ�ͷ��Ϣ
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
					if ((index = headerString.indexOf(":")) > 0) {// ����ͷ�ļ����������ֻ��¼�˴��ڡ���������
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
	 * �������Base64��������ݴ�<br>
	 * ���ݸ�ʽ�磺=?GB2312?B?u9i4tDogyPyw2S3E3MGmzOHJ/cH5xtottPrC68nzvMa94bn70+
	 * s0QcjV1r692M28?=
	 * 
	 * @param base64Str
	 * @return
	 */
	public String getBase64Content(String base64Str) {// �����ʼ�ͷ��Ҫ��Ϣ
		// =?GB2312?B?u9i4tDogyPyw2S3E3MGmzOHJ/cH5xtottPrC68nzvMa94bn70+s0QcjV1r692M28?=
		String content = "";
		if (base64Str.startsWith("=?") && base64Str.endsWith("?=")) {// �ж��ʼ�ͷ��
			// =?xxx?B?xxxxxx?=
			// ���ַ���
			base64Str = base64Str.substring(2, base64Str.length() - 2);// ��ȡ�ʼ�ͷ
			// =?xxx?B?xxxxxx?=
			// �ַ�������Ϣ
			String[] strs = base64Str.split("\\?");// �� ? ���ָ��ַ���
			String charset = strs[0];
			base64Str = strs[2];// ������ַ�����ֵ
			if ((strs[1] != null && strs[1].equals("B"))
					|| (strs[1] != null && strs[1].equals("b"))) {// �ʼ������д�д��Сд��B
				try {
					content = new String(Base64.decode(base64Str,
							Base64.DEFAULT), charset);// �����ַ���
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return content;// �����Ѿ������˵��ʼ�ͷ��Ϣ
	}

	/**
	 * ����������Ϣ���������
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
	 * ������ʱ�䷭�������
	 * @param str
	 * @return 
	 * @return
	 */
private String translateMailDate(String str){
	String ret = new String();
	String[] mans = str.split("\\s+");
	System.out.println(">>>>>>>>>>>>>>>"+mans[1] +"+"+ mans[2] + "<<<<<<<<<<<<<<<<<");
	if(mans[2].indexOf("Jan")!=-1){ret = "1��"+mans[1]+"��";System.out.println("1��");}
	if(mans[2].indexOf("Feb")!=-1){ret = "2��"+mans[1]+"��";System.out.println("2��");}
	if(mans[2].indexOf("Mar")!=-1){ret = "3��"+mans[1]+"��";System.out.println("3��");}
	if(mans[2].indexOf("Apr")!=-1){ret = "4��"+mans[1]+"��";System.out.println("4��");}
	if(mans[2].indexOf("May")!=-1){ret = "5��"+mans[1]+"��";System.out.println("5��");}
	if(mans[2].indexOf("Jun")!=-1){ret = "6��"+mans[1]+"��";System.out.println("6��");}
	if(mans[2].indexOf("Jul")!=-1){ret = "7��"+mans[1]+"��";System.out.println("7��");}
	if(mans[2].indexOf("Aug")!=-1){ret = "8��"+mans[1]+"��";System.out.println("8��");}
	if(mans[2].indexOf("Sep")!=-1 ){ret = "9��"+mans[1]+"��";System.out.println("9��");}
	if(mans[2].indexOf("Oct")!=-1){ret = "10��"+mans[1]+"��";System.out.println("10��");}
	if(mans[2].indexOf("Nov")!=-1){ret = "11��"+mans[1]+"��";System.out.println("11��");}
	if(mans[2].indexOf("Dec")!=-1){ret = "12��"+mans[1]+"��";System.out.println("12��");}
	System.out.println("ret��"+ret);
	return ret;
}
/**
 * ������ʱ�䷭�����ϸ����
 * @return
 */
private String translateMailDetailedDate(String str){
	String ret = new String();
	String[] mans = str.split("\\s+");
	System.out.println(">>>>>>>>>>>>>>>"+mans[1] +"+"+ mans[2] +"+"+ mans[3] +"+"+mans[4] + "<<<<<<<<<<<<<<<<<");
	if(mans[2].indexOf("Jan")!=-1){ret = mans[3] +"��" + "1��"+mans[1]+"��"+mans[4];System.out.println("1��");}
	if(mans[2].indexOf("Feb")!=-1){ret =  mans[3] +"��" +"2��"+mans[1]+"��"+mans[4];System.out.println("2��");}
	if(mans[2].indexOf("Mar")!=-1){ret =  mans[3] +"��" +"3��"+mans[1]+"��"+mans[4];System.out.println("3��");}
	if(mans[2].indexOf("Apr")!=-1){ret =  mans[3] +"��" +"4��"+mans[1]+"��"+mans[4];System.out.println("4��");}
	if(mans[2].indexOf("May")!=-1){ret =  mans[3] +"��" +"5��"+mans[1]+"��"+mans[4];System.out.println("5��");}
	if(mans[2].indexOf("Jun")!=-1){ret =  mans[3] +"��" +"6��"+mans[1]+"��"+mans[4];System.out.println("6��");}
	if(mans[2].indexOf("Jul")!=-1){ret =  mans[3] +"��" +"7��"+mans[1]+"��"+mans[4];System.out.println("7��");}
	if(mans[2].indexOf("Aug")!=-1){ret =  mans[3] +"��" +"8��"+mans[1]+"��"+mans[4];System.out.println("8��");}
	if(mans[2].indexOf("Sep")!=-1 ){ret = mans[3] +"��" +"9��"+mans[1]+"��"+mans[4];System.out.println("9��");}
	if(mans[2].indexOf("Oct")!=-1){ret = mans[3] +"��" + "10��"+mans[1]+"��"+mans[4];System.out.println("10��");}
	if(mans[2].indexOf("Nov")!=-1){ret =  mans[3] +"��" +"11��"+mans[1]+"��"+mans[4];System.out.println("11��");}
	if(mans[2].indexOf("Dec")!=-1){ret =  mans[3] +"��" +"12��"+mans[1]+"��"+mans[4];System.out.println("12��");}
	System.out.println("ret��"+ret);
	return ret;
}
	/**
	 * ��ͼƬд�뵽����
	 * 
	 * @param img
	 *            ͼƬ������
	 * @param fileName
	 *            �ļ�����ʱ������
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
	 * ��¼���ж��Ƿ�����ʼ�״̬��¼�ļ�
	 * 
	 * @return
	 */
	public int fileIsExists() {
		System.out.println("����״̬�ļ��ж�");
		File f = new File("/mnt/sdcard/MailBeta_zyt/mailstate.txt");
		if (!f.exists()) {
			System.out.println("��״̬�ļ�������·��");
			File createFile = new File("/mnt/sdcard/MailBeta_zyt");
			createFile.mkdirs();
			return 0;
		} else {
			System.out.println("��״̬�ļ�������1");
			return 1;
		}
	}

	String str = "";

	/**
	 * ��ȡ�ʼ�״̬��¼�ļ����
	 */
	public void readMailState() throws IOException {
		File urlFile = new File("/mnt/sdcard/MailBeta_zyt/mailstate.txt");
		InputStreamReader isr = new InputStreamReader(new FileInputStream(
				urlFile), "UTF-8");
		BufferedReader br = new BufferedReader(isr);

		String mimeTypeLine = null;
		while ((mimeTypeLine = br.readLine()) != null) {// ��ȡ���
			str = str + mimeTypeLine;
		}
	}

	/**
	 * д�� ��sdcardĿ¼�ϵ��ļ���Ҫ��FileOutputStream�� ������openFileOutput
	 * ��ͬ�㣺openFileOutput����raw�������ģ�FileOutputStream���κ��ļ�������
	 * 
	 * @param fileName
	 * @param message
	 */
	// д��/mnt/sdcard/Ŀ¼������ļ�
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
	 * �����̷߳���message
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
		LoginActivity.handler.sendMessage(message); // ��Message�����ͳ�ȥ

	}

	// /**
	// * ���û��Ĳ�����¼��mysql
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
	// * ��ѯ������ʼ��Ƿ����Ķ���¼
	// *
	// * @param mailUser
	// * @param mailNumber
	// * @return
	// */
	// public int mailStateInMysql(String mailUser, int mailNumber) {
	// // ����������
	// String driver = "com.mysql.jdbc.Driver";
	// // ���ݱ�url
	// String url = "jdbc:mysql://localhost:3306/student";
	// // MySQL�û���
	// String user = "root";
	// // MySQL����
	// String password = "12345678";
	// // �洢����״̬
	// String state = null;
	// // ��ʼ�������ݿ�
	// try {
	// // ������������
	// Class.forName(driver);
	// // �������ݿ�
	// Connection conn = DriverManager.getConnection(url, user, password);
	// if (!conn.isClosed())
	// System.out.println("connecting to the database successfully!");
	// // statement����ִ��SQL���
	// Statement statement = conn.createStatement();
	// // select���
	// String sql =
	// "select id,mail_user,mail_value,mail_number from mail_db where mail_user='"
	// + mailUser
	// + "' and mail_value='2' and mail_number='" + mailNumber + "'";
	// ResultSet rs = statement.executeQuery(sql);
	//
	// // ���student��������Ϣ
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
	 * ֪ͨUI�̣߳����Դ����û�����ָ����
	 */
	public void entryMainMenuInputMode() {
		synchronized (lock) {
			lock.notify();// ����ĳһ���߳�
		}
	}

}
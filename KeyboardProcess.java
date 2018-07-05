package com.richercoin.client;

import java.util.Scanner;

import com.richercoin.util.StringUtils;

public class KeyboardProcess {
	private Scanner scan;

	public static KeyboardProcess instance;

	private KeyboardProcess() {
		scan = new Scanner(System.in);
	}

	public static KeyboardProcess getInstance() {
		if (null == instance) {
			instance = new KeyboardProcess();
		}
		return instance;
	}

	public void clear() {
		String next = null;
		while (true) {
			if (scan.hasNextLine()) {
				next = scan.nextLine();
			}
			if (StringUtils.nullorBlank(next)) {
				break;
			}
		}
	}

	public String getString() {
		if (scan.hasNext()) {
			return scan.next();
		} else {
			return null;
		}
	}

	public String getNexLine() {
		if (scan.hasNextLine()) {
			return scan.nextLine();
		} else {
			scan.hasNext();
			return scan.nextLine();
		}
	}

	public String getAllInput() {
		StringBuilder strBuild = new StringBuilder();
		String next = null;
		while (scan.hasNextLine()) {
			next = scan.nextLine();

			if (StringUtils.nullorBlank(next) || next.toLowerCase().equals("done")) {
				break;
			} else {
				strBuild.append(next);
			}
		}
		return strBuild.toString();
	}

	public void close() {
		if (null != scan) {
			scan.close();
		}
	}
}

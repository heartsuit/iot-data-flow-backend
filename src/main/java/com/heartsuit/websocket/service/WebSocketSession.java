package com.heartsuit.websocket.service;

import org.yeauty.pojo.Session;

import java.util.*;

public class WebSocketSession {
	private static final Map<Session, String> sessionKV = new HashMap<Session, String>();

	/**
	 * 向连接池中添加连接
	 * 
	 * @param session
	 * @param client
	 */
	public static void addClient(Session session, String client) {
		sessionKV.put(session, client);
	}

	/**
	 * 获取所有连接池中的用户，因为set是不允许重复的，所以可以得到无重复的user数组
	 * 
	 * @return
	 */
	public static Collection<String> getOnlineClient() {
		Set<String> result = new HashSet<String>();
		Collection<String> clients = sessionKV.values();
		for (String client : clients) {
			result.add(client);
		}
		return result;
	}

	/**
	 * 获取在线客户数量
	 * 
	 * @return
	 */
	public static synchronized int getOnlineCount() {
		return sessionKV.size();
	}

	/**
	 * 通过websocket连接获取其对应的用户
	 * 
	 * @param session
	 * @return
	 */
	public static String getClientBySession(Session session) {
		return sessionKV.get(session);
	}

	/**
	 * 根据小组ID获取Session
	 * 
	 * @param groupID
	 * @return
	 */
	public static List<Session> getSessionsByGroupID(String groupID) {
		List<Session> sessions = new ArrayList<Session>();

		Set<Session> keys = sessionKV.keySet();
		synchronized (keys) {
			for (Session session : keys) {
				String value = sessionKV.get(session);
				if (value.equals(groupID)) {
					sessions.add(session);
				}
			}
		}
		return sessions;
	}

	/**
	 * 根据client获取Session,这是一个Set, 此处取第一个
	 * 
	 * @param client
	 * @return
	 */
	public static Session getSessionByClient(String client) {
		Set<Session> keys = sessionKV.keySet();
		synchronized (keys) {
			for (Session session : keys) {
				String c = sessionKV.get(session);
				if (c.equals(client)) {
					return session;
				}
			}
		}
		return null;
	}

	/**
	 * 向特定的用户发送数据
	 * 
	 * @param session
	 * @param message
	 */
	public static void sendMessage2Target(Session session, String message) {
		if (null != session && null != sessionKV.get(session)) {
			session.sendText(message);
		}
	}

	/**
	 * 向一个小组内用户发送消息
	 * 
	 * @param message
	 */
	public static void sendMessage2Group(String groupID, String message) {
		List<Session> sessionsByGroupID = getSessionsByGroupID(groupID);

		sessionsByGroupID.forEach(session -> {
			if(sessionKV.get(session) != null){
				session.sendText(message);
			}
		});
	}

	/**
	 * 向所有的用户发送消息
	 * 
	 * @param message
	 */
	public static void sendMessage2All(String message) {
		Set<Session> keys = sessionKV.keySet();
		synchronized (keys) {
			for (Session session : keys) {
				String user = sessionKV.get(session);
				if (user != null) {
					session.sendText(message);
				}
			}
		}
	}

	/**
	 * 移除连接池中的连接
	 * 
	 * @param session
	 * @return
	 */
	public static boolean removeClient(Session session) {
		if (sessionKV.containsKey(session)) {
			sessionKV.remove(session);
			return true;
		} else {
			return false;
		}
	}
}

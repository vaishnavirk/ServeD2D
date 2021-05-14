package com.sapient.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.sapient.entity.Message;
import com.sapient.utils.DbUtil;

public class MessageDaoImpl implements MessageDao {
	
    public Boolean liveMessage(Message message)
        throws DaoException {
		String sql = "INSERT INTO MESSAGE (message_id, sender_id, receiver_id, content, timestamp) VALUES (?,?,?,?,?)";
		//what to do about user id generation?
		try (Connection conn = DbUtil.createConnection(); 
			PreparedStatement stmt = conn.prepareStatement(sql);
			) {
			stmt.setInt(1, message.getMessageId());
            stmt.setInt(2, message.getSenderId());
            stmt.setInt(3, message.getReceiverId());
			stmt.setString(4, message.getContent());
			stmt.setDate(5, (java.sql.Date) message.getTimestamp());
			stmt.executeUpdate();
			System.out.println("message sent");
			return true;

		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

    public List<Message> returnAllMessagesForAnOrder(Integer orderId) throws DaoException {
		
		List<Message> chats = new ArrayList<Message>();
		
		String sql = "SELECT sender_id,receiver_id,content,timestamp FROM MESSAGE JOIN ORDER ON MESSAGE.sender_id = ORDER.user_id WHERE ORDER.order_id = ?";
		try (Connection conn = DbUtil.createConnection(); PreparedStatement stmt = conn.prepareStatement(sql);) 
		{
			try(ResultSet rs = stmt.executeQuery();)
			{
                stmt.setInt(1, orderId);
				if(rs.next()) {
					do {
						Message message = new Message();
						message.setSenderId(rs.getInt("sender_id"));
						message.setReceiverId(rs.getInt("receiver_id"));
						message.setContent(rs.getString("content"));
						message.setTimestamp(rs.getDate("timestamp"));
						chats.add(message);
					} while (rs.next());

				}
				else
				{
					System.out.println("No data found!"); 
				}
			
			}
			catch (Exception e) {
				throw new DaoException(e);
			}
				
		}
		catch (Exception e) {
			throw new DaoException(e);
		}
		return chats;

	}

    public List<Integer> returnChatsOfUser(Integer userId) throws DaoException {
		
		List<Integer> recievers = new ArrayList<Integer>();
		//Costly Operation
		String sql = "SELECT DISTINCT(receiver_id) from MESSAGE WHERE sender_id=? INNER JOIN USERS ON MESSAGE.receiver_id=USERS.userId ";
		try (Connection conn = DbUtil.createConnection(); PreparedStatement stmt = conn.prepareStatement(sql);) 
		{
			try(ResultSet rs = stmt.executeQuery();)
			{
                stmt.setInt(1, userId);
				if(rs.next()) {
					do {
						recievers.add(rs.getInt("receiver_id"));
					} while (rs.next());

				}
				else
				{
					System.out.println("User has made no conversation!"); 
				}
			
			}
			catch (Exception e) {
				throw new DaoException(e);
			}
				
		}
		catch (Exception e) {
			throw new DaoException(e);
		}
		return recievers;

	}
}

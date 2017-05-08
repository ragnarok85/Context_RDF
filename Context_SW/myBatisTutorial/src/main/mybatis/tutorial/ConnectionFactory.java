package main.mybatis.tutorial;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class ConnectionFactory {
	
	private static SqlSessionFactory sqlSessionFactory;
//	private static Reader reader;
	static{
		InputStream inputStream;
		inputStream = ConnectionFactory.class.getResourceAsStream("/mybatis-config.xml");
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
	}
	
	public static SqlSessionFactory getSession(){
		return sqlSessionFactory;
	}

}

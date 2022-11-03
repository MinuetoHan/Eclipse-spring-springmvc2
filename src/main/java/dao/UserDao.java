package dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import logic.User;

@Repository
public class UserDao {
	private NamedParameterJdbcTemplate template;
	private Map<String, Object> param = new HashMap<>();
	private RowMapper<User> mapper = new BeanPropertyRowMapper<>(User.class);
		
	@Autowired
	public void setDataSource(DataSource dataSource) {
		template = new NamedParameterJdbcTemplate(dataSource);
	}

	public void insert(User user) {
		SqlParameterSource param = new BeanPropertySqlParameterSource(user);
		String sql = "insert into useraccount "
				+ " (userid, password, username, phoneno, postcode,"
				+ " address, email, birthday)"
				+ " values(:userid, :password, :username, :phoneno, :postcode,"
				+ " :address, :email, :birthday)";
		template.update(sql, param);
	}

	public User selectOne(String userid) {
		param.clear();
		param.put("userid", userid);
//조회 결과가 없는 경우 : EmptyResultDataAccessException 예외 발생
		return template.queryForObject
("select * from useraccount where userid=:userid", param, mapper);
	}

	public void update(@Valid User user) {
		SqlParameterSource param = new BeanPropertySqlParameterSource(user);
		String sql = "update useraccount set username=:username, "
				+ "birthday=:birthday, phoneno=:phoneno, postcode=:postcode, "
				+ "address=:address, email=:email where userid=:userid";
		template.update(sql, param);
	}

	public void delete(String userid) {
		param.clear();
		param.put("userid", userid);
		template.update
			("delete from useraccount where userid=:userid", param);
	}

	public void chgpass(String userid, String pass) {
		param.clear();
		param.put("userid", userid);
		param.put("password", pass);
		template.update
 ("update useraccount set password=:password where userid=:userid",
		param);
	}

	public String search(User user, String url) {
		String col = "userid";
		if(url.equals("pw")) col = "password";
		String sql = "select "+ col +" from useraccount "
				+ "where email=:email and phoneno=:phoneno";
		if(url.equals("pw")) {
			sql += " and userid=:userid";
		}
		SqlParameterSource param = 
				new BeanPropertySqlParameterSource(user);
		return template.queryForObject(sql, param, String.class);
	}

	public List<User> list() {
		
		return template.query
				("select * from useraccount",param,mapper);
	}

}

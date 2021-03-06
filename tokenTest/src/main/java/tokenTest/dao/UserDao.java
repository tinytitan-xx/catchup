package tokenTest.dao;

import java.util.List;
import java.util.Set;

import tokenTest.model.User;

public interface UserDao {
	void save(User user);

	void update(User user);

	void delete(User user);

	void merge(User user);

	User findByUserNickName(String nickName);
	
	User findByUserPhoneNum(String phoneNum);
	
	User findById(Long id);
	
	Set<User> getBlackList(Long id);

	List<User> getBizcardValidations();

	List<User> getBizcardRejects(int pagenum);
}

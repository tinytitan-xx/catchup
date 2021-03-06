package tokenTest.dao;

import tokenTest.model.ValidationCode;

public interface ValidationCodeDao {
	void save(ValidationCode code);

	void update(ValidationCode code);

	void delete(ValidationCode code);

	ValidationCode findByPhoneNum(String phoneNum);
	
}

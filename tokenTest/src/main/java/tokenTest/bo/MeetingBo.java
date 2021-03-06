package tokenTest.bo;

import java.util.List;

import tokenTest.Util.Status;
import tokenTest.exception.ApplyNotFoundException;
import tokenTest.exception.MeetingNotFoundException;
import tokenTest.exception.NotAppliedException;
import tokenTest.exception.TooManyAppliesException;
import tokenTest.model.Meeting;
import tokenTest.model.MeetingApply;
import tokenTest.model.User;

public interface MeetingBo {
	void save(Meeting meeting);

	void update(Meeting meeting);

	void delete(Meeting meeting);

	Meeting getMeetingById(Long id) throws MeetingNotFoundException;

	List<MeetingApply> getApplyByMeeting(Meeting meeting) throws Exception;

	MeetingApply getApplyById(Long applyId) throws ApplyNotFoundException;

	void applyForMeeting(User user, Meeting meeting, String applyContent) throws TooManyAppliesException;

	void withdrawMeetingApply(MeetingApply meetingApply);

	void processMeetingApply(MeetingApply meetingApply, boolean approved);

	Status stopMeeting(Meeting meeting, String cancelReason);
	
	List getMeetingList(Double longitude, Double latitude, Integer pagenum,
			Integer sorttype, Integer range, String gender, String job,
			String shopName) throws Exception;

	List getMeetingListForUser(User user, Double longitude, Double latitude,
			Integer pagenum, Integer sorttype, Integer range, String gender,
			String job, String shopName) throws Exception;
	
	List getMeetingListByUser(User user, Integer pagenum) throws Exception;

	List getMeetingListByParticipate(User user, Integer pagenum)
			throws Exception;

	boolean testIfApplied(Meeting m, User u);
}

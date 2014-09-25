/**
 * 
 */
package tokenTest.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import tokenTest.Util.Constants;
import tokenTest.Util.Status;
import tokenTest.bo.PictureBo;
import tokenTest.bo.UserBo;
import tokenTest.model.Picture;
import tokenTest.model.User;
import tokenTest.response.LoginResponse;
import tokenTest.response.UserDetailResponse;
import tokenTest.response.ValidatePhoneResponse;
import tokenTest.service.UserServiceInterface;

/**
 * @author pengtao
 * 
 */
@RestController
@RequestMapping("/user")
public class UserServiceImpl implements UserServiceInterface {
	@Autowired
	private UserBo userBo;

	@Autowired
	private PictureBo pictureBo;

	@Autowired
	private ServletContext servletContext = null;

	private static String DIR = "picture";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * tokenTest.service.UserServiceInterface#userRegister(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.util.Date,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@RequestMapping(value = { "/userRegister**" }, method = RequestMethod.GET)
	public LoginResponse userRegister(
			@RequestParam(required = true) String nickName,
			@RequestParam(required = true) String password,
			@RequestParam(required = true) String building,
			@RequestParam(required = true) String phoneNum,
			@RequestParam(required = true) String gender,
			@RequestParam(required = false) Date birthday,
			@RequestParam(required = false) String emailaddress,
			@RequestParam(required = false) String company) {
		// TODO Auto-generated method stub
		/* 必填信息 */
		User user = new User(password, nickName, gender, building, phoneNum);

		/* 非必填信息 */
		if (birthday != null)
			user.setBirthday(birthday);
		if (emailaddress != null)
			user.setEmail_address(emailaddress);
		if (company != null)
			user.setCompany(company);

		/* 生成令牌 */
		user.setToken(RandomStringUtils.randomAlphanumeric(30));

		/* 注册用户 */
		try {
			userBo.save(user);
		} catch (DataIntegrityViolationException e) {
			// TODO: handle exception
			// 不满足唯一性约束，phonenum或nickname重复,占用token做错误信息。
			return new LoginResponse(Status.ERROR_GENERIC,
					"phonenum或nickname重复");
		}
		return new LoginResponse(Status.OK, user.getToken());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tokenTest.service.UserServiceInterface#userLogin(java.lang.Long,
	 * java.lang.String)
	 */
	/*
	 * @RequestMapping(value = { "/userLogin**" }, method = RequestMethod.GET)
	 * public LoginResponse userLogin(@RequestParam(required = true) Long id,
	 * 
	 * @RequestParam(required = true) String password) { // TODO Auto-generated
	 * method stub User user = null;
	 * 
	 * 查找用户 try { user = userBo.findByUserId(id); } catch (Exception e) { //
	 * TODO: handle exception // 不知道啥错 return new
	 * LoginResponse(Status.SERVICE_NOT_AVAILABLE, "服务器不可用"); }
	 * 
	 * 用户不存在或密码不对 if (user == null || !password.equals(user.getPassword()))
	 * return new LoginResponse(Status.ERROR_USER_NOT_FOUND, "用户名或密码错误");
	 * 
	 * 生产、更新令牌 user.setToken(RandomStringUtils.randomAlphanumeric(30)); try {
	 * userBo.update(user); } catch (Exception e) { // TODO: handle exception
	 * return null; } return new LoginResponse(Status.OK, user.getId(),
	 * user.getToken()); }
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see tokenTest.service.UserServiceInterface#userLogin(java.lang.Long,
	 * java.lang.String)
	 */
	@RequestMapping(value = { "/userLogin**" }, method = RequestMethod.GET)
	public LoginResponse userLogin(
			@RequestParam(required = true) String nickorphone,
			@RequestParam(required = true) String password) {
		// TODO Auto-generated method stub
		User user = null;

		/* 查找用户 */
		try {
			user = userBo.findByUserNickName(nickorphone);
		} catch (Exception e) {
			// TODO: handle exception
			// 不知道啥错
			return new LoginResponse(Status.SERVICE_NOT_AVAILABLE, "服务器不可用");
		}

		int attempts = 0;
		attempts = user.getLogin_attempts();
		// 用户存在密码不对
		if (user != null && !password.equals(user.getPassword())) {
			// password wrong
			attempts++;
			user.setLogin_attempts(attempts);
			userBo.update(user);
		}

		if (attempts >= Constants.MAX_LOGIN_ATTEMPTS) {
			return new LoginResponse(Status.ERROR_MAX_LOGIN_ATTEMPTS,
					"密码输入错误过多，请重置密码");
		}

		/* 用昵称查找用户不存在 */
		if (user == null || !password.equals(user.getPassword())) {
			try {
				user = userBo.findByUserPhoneNum(nickorphone);
			} catch (Exception e) {
				// TODO: handle exception
				// 不知道啥错
				return new LoginResponse(Status.SERVICE_NOT_AVAILABLE, "服务器不可用");
			}

			if (user == null || !password.equals(user.getPassword()))
				return new LoginResponse(Status.ERROR_USER_NOT_FOUND,
						"用户不存在或者密码错误");
		}

		/* 生产、更新令牌 */
		user.setToken(RandomStringUtils.randomAlphanumeric(30));
		user.setLogin_attempts(0); // clear login attempts
		try {
			userBo.update(user);
		} catch (Exception e) {
			// TODO: handle exception
			return new LoginResponse(Status.SERVICE_NOT_AVAILABLE, "服务器不可用");
		}
		return new LoginResponse(Status.OK, user.getId(), user.getToken());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tokenTest.service.UserServiceInterface#userLogout(java.lang.Long,
	 * java.lang.String)
	 */
	@RequestMapping(value = { "/userLogout**" }, method = RequestMethod.GET)
	public Enum<Status> userLogout(@RequestParam(required = true) Long id,
			@RequestParam(required = true) String token) {
		// TODO Auto-generated method stub
		User user = null;

		/* 查找用户 */
		try {
			user = userBo.findByUserId(id);
		} catch (Exception e) {
			// TODO: handle exception
			// 不管什么错，用户不用知道。
			return Status.OK;
		}

		/* 用户不存在或者令牌不正确 ，不管什么错，用户不用知道。 */
		if (user == null || !user.getToken().equals(token))
			return Status.OK;

		/* 清除令牌 */
		user.setToken("");
		try {
			userBo.update(user);
		} catch (Exception e) {
			// TODO: handle exception
			// 不管什么错，用户不用知道。
			return Status.OK;
		}
		return Status.OK;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tokenTest.service.UserServiceInterface#getUserDetail(java.lang.Long,
	 * java.lang.String, java.lang.Long) 查找用户信息，queriedid为空表示查找自己的信息，显示内容更多。
	 */
	@RequestMapping(value = { "/getUserDetail**" }, method = RequestMethod.GET)
	public UserDetailResponse getUserDetail(
			@RequestParam(required = true) Long id,
			@RequestParam(required = true) String token,
			@RequestParam(required = false) Long targetId) {
		// TODO Auto-generated method stub
		User user = null;

		/* 查找用户 */
		try {
			user = userBo.findByUserId(id);
		} catch (Exception e) {
			// TODO: handle exception
			return UserDetailResponse.getError(Status.SERVICE_NOT_AVAILABLE);
		}

		/* 用户不存在或者令牌不正确 */
		if (user == null)
			return UserDetailResponse.getError(Status.ERROR_USER_NOT_FOUND);

		if (!StringUtils.equals(user.getToken(), token)) {
			return UserDetailResponse.getError(Status.ERROR_WRONG_TOKEN);
		}

		/* targetId==null表示查看自己信息，否则为查看别人信息 */
		if (targetId == null) {
			return new UserDetailResponse(user, true);
		} else {
			try {
				user = userBo.findByUserId(targetId);
			} catch (Exception e) {
				UserDetailResponse.getError(Status.SERVICE_NOT_AVAILABLE);
			}
			if (user == null)
				UserDetailResponse.getError(Status.ERROR_USER_NOT_FOUND);
			return new UserDetailResponse(user, false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * tokenTest.service.UserServiceInterface#updateUserProfile(java.lang.Long,
	 * java.lang.String, java.lang.String, java.lang.String, java.util.Date,
	 * java.lang.String, java.lang.String, java.lang.String) 修改用户信息
	 */
	@RequestMapping(value = { "/updateUserProfile**" }, method = RequestMethod.GET)
	public String updateUserProfile(@RequestParam(required = true) Long id,
			@RequestParam(required = true) String token,
			@RequestParam(required = false) String nickname,
			@RequestParam(required = false) String building,
			@RequestParam(required = false) Date birthday,
			@RequestParam(required = false) String sex,
			@RequestParam(required = false) String emailaddress,
			@RequestParam(required = false) String company) {
		// TODO Auto-generated method stub
		User user = null;

		/* 查找用户 */
		try {
			user = userBo.findByUserId(id);
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}

		/* 用户不存在或者令牌不正确 */
		if (user == null || !user.getToken().equals(token))
			return null;

		/* 设置新的 用户信息 */
		if (nickname != null)
			user.setNickname(nickname);
		if (building != null)
			user.setBuilding(building);
		if (birthday != null)
			user.setBirthday(birthday);
		if (sex != null)
			user.setSex(sex);
		if (emailaddress != null)
			user.setEmail_address(emailaddress);
		if (company != null)
			user.setCompany(company);

		/* 更新用户信息 */
		try {
			userBo.update(user);
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
		return "Successed";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * tokenTest.service.UserServiceInterface#changePassWord(java.lang.Long,
	 * java.lang.String, java.lang.String)
	 */
	@RequestMapping(value = { "/changePassWord**" }, method = RequestMethod.GET)
	public LoginResponse changePassWord(@RequestParam(required = true) Long id,
			@RequestParam(required = true) String oldpassword,
			@RequestParam(required = true) String newpassword) {
		// TODO Auto-generated method stub
		User user = null;

		/* 查找用户 */
		try {
			user = userBo.findByUserId(id);
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}

		/* 用户不存在或者令牌不正确 */
		if (user == null || !user.getPassword().equals(oldpassword))
			return null;

		/* 设置新的 用户密码 */
		user.setPassword(newpassword);
		user.setLogin_attempts(0);

		/* 更新用户密码 */
		try {
			userBo.update(user);
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}

		return new LoginResponse(Status.OK);
	}

	@RequestMapping(value = { "/validatePhone**" }, method = RequestMethod.GET)
	public ValidatePhoneResponse validatePhone(
			@RequestParam(required = true) String phoneNum) {
		// TODO Auto-generated method stub
		/*
		 * User user = null; 查找用户 try { user =
		 * userBo.findByUserPhoneNum(phoneNum); } catch (Exception e) { // TODO:
		 * handle exception // 不知道啥错 return new
		 * ValidatePhoneResponse(Status.SERVICE_NOT_AVAILABLE, "服务器不可用"); }
		 * 
		 * 手机号码已经被注册,不做控制 if (user != null) { // TODO }
		 */

		/* 随机验证码 */
		String code = RandomStringUtils.randomNumeric(6);
		// TODO 发送验证码

		return new ValidatePhoneResponse(Status.OK, code);
	}

	/* 默认增的不是头像 */
	@RequestMapping(value = { "/addPhoto**" }, method = RequestMethod.POST)
	public Enum<Status> addPhoto(@RequestParam(required = true) Long id,
			@RequestParam(required = true) String token,
			@RequestParam(required = true) MultipartFile file,
			@RequestParam(required = false) String description,
			@RequestParam(required = false) Boolean isProfile) {
		/* 验证图片 */
		if (!StringUtils.equals(file.getContentType(), "image/png")) {
			/* 文件格式错误 */
			return Status.ERROR_BANNED;
		}

		String path = servletContext.getRealPath("/") + File.separator + DIR;
		User user = null;
		/* 验证用户 */
		try {
			user = userBo.validateUser(id, token);
		} catch (Exception e) {
			// TODO: handle exception
		}

		/* 用户不存在或者令牌不正确 */
		if (user == null)
			return Status.ERROR_USER_NOT_FOUND;

		/* 新建图片 */
		Picture picture = null;
		if (description != null)
			picture = new Picture(new Date(), description);
		else
			picture = new Picture(new Date());

		try {
			if (isProfile != null && isProfile) {
				pictureBo.save(user, file, picture, path, true);
			} else {
				pictureBo.save(user, file, picture, path, false);
			}

		} catch (Exception e) {
			// TODO: handle exception
			return Status.SERVICE_NOT_AVAILABLE;
		}
		return Status.OK;
	}

	@RequestMapping(value = { "/deletePhoto**" }, method = RequestMethod.GET)
	public Enum<Status> deletePhoto(@RequestParam(required = true) Long id,
			@RequestParam(required = true) String token,
			@RequestParam(required = true) Long picId) {
		// TODO Auto-generated method stub
		String path = servletContext.getRealPath("/") + File.separator + DIR;
		User user = null;
		/* 验证用户 */
		try {
			user = userBo.validateUser(id, token);
		} catch (Exception e) {
			// TODO: handle exception
		}

		/* 用户不存在或者令牌不正确 */
		if (user == null)
			return Status.ERROR_USER_NOT_FOUND;

		/* 查找图片 */
		Picture picture = null;
		try {
			picture = pictureBo.findById(picId);
		} catch (Exception e) {
			// TODO: handle exception
			return Status.SERVICE_NOT_AVAILABLE;
		}
		if (picture == null) {
			// 图片不存在
			return Status.ERROR_PIC_NOT_FOUND;
		}

		/* 是头像，不能删除 */
		if (user.getPic().equals(picture)) {
			return Status.ERROR_CANNOT_DELETE_PROFILE_PHOTO;
		}

		/* 不是自己的图片，不能删除 */
		if (!user.getPicture().contains(picture)) {
			return Status.ERROR_BANNED;
		}

		/* 删除图片数据和文件,头像不能删除 */
		try {
			/* 删除图片数据和文件 */
			pictureBo.delete(user, picture, path);
		} catch (Exception e) {
			// TODO: handle exception
			return Status.SERVICE_NOT_AVAILABLE;
		}
		return Status.OK;
	}

	/* 默认原图 */
	@RequestMapping(value = { "/getPhoto**" }, method = RequestMethod.GET)
	public void getPhoto(@RequestParam(required = true) Long picId,
			@RequestParam(required = false) Integer size,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		String path = servletContext.getRealPath("/") + File.separator + DIR;

		/* 查找图片 */
		Picture picture = null;
		try {
			picture = pictureBo.findById(picId);
		} catch (Exception e) {
			// TODO: handle exception
			return;
		}
		if (picture == null)
			return;

		File file = new File(path + File.separator + picture.getFilename());
		if (file.exists()) {
			if (size == null || size == 0) {
				/* 返回原图 */
				try {
					FileInputStream is = new FileInputStream(file);
					IOUtils.copy(is, response.getOutputStream());
				} catch (Exception e) {
					// TODO: handle exception
					return;
				}
			} else {
				/* 返回小图 */
				try {
					BufferedImage originalImage = ImageIO
							.read(new FileInputStream(file));
					originalImage = pictureBo.zoomOutImage(originalImage, 100);
					IOUtils.copy((InputStream) ImageIO
							.createImageInputStream(originalImage), response
							.getOutputStream());
				} catch (Exception e) {
					// TODO: handle exception
					return;
				}
			}
		}
	}
}

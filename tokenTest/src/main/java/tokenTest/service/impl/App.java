package tokenTest.service.impl;

import java.util.Date;
import java.util.HashSet;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import tokenTest.bo.MeetingBo;
import tokenTest.bo.PictureBo;
import tokenTest.bo.ShopBo;
import tokenTest.bo.TagBo;
import tokenTest.bo.UserBo;
import tokenTest.model.Meeting;
import tokenTest.model.Picture;
import tokenTest.model.Shop;
import tokenTest.model.Tag;
import tokenTest.model.User;

public class App {
	public static void main(String[] args) {
		@SuppressWarnings("resource")
		ApplicationContext appContext = new ClassPathXmlApplicationContext(
				"spring/config/BeanLocations.xml");

		/* StockBo stockBo = (StockBo)appContext.getBean("stockBo"); */

		/** insert **/
		/*
		 * Stock stock = new Stock(); stock.setStockCode("7668");
		 * stock.setStockName("HAIO"); stockBo.save(stock);
		 *//** select **/
		/*
		 * Stock stock2 = stockBo.findByStockCode("7668");
		 * System.out.println(stock2);
		 *//** update **/
		/*
		 * stock2.setStockName("HAIO-1"); stockBo.update(stock2);
		 *//** delete **/
		/*
		 * stockBo.delete(stock2);
		 */

		/*PictureBo pictureBo = (PictureBo) appContext.getBean("pictureBo");
		Picture picture = new Picture(new Date(), "peach's photo", "peach");
		pictureBo.save(picture);

		ShopBo shopBo = (ShopBo) appContext.getBean("shopBo");
		Shop shop = new Shop();
		shop.setArea("��һ�");
		shop.setCity("�Ϻ�");
		shop.setType("�в�");
		shop.setPrice(99.99);*/
//		shopBo.save(shop);
		
		
		/*TagBo tagBo = (TagBo) appContext.getBean("tagBo");
		Tag tag = new Tag("�ٽŴ�");
		tagBo.save(tag);*/
		/*tagBo.save(new Tag("�ٱǴ���"));
		tagBo.save(new Tag("�ٽŴ�"));
		tagBo.save(new Tag("office����"));*/
		
		UserBo userBo = (UserBo) appContext.getBean("userBo");
		User user = new User("123456", "peac1h", "M", "CCB", "ccb", new Date(), 10086, "18918760155", "tpeng915@gmail.com");
		/*user.setPic(picture);
		HashSet<Tag> tagSet = new HashSet<Tag>();
		tagSet.add(tag);
		user.setTags(tagSet);*/
		userBo.save(user);
		
		
		/*MeetingBo meetingBo = (MeetingBo) appContext.getBean("meetingBo");
		Meeting meeting = new Meeting(user, new Date(), 2, 2, "F", 30, "һ��Ȳ�");*/
//		meetingBo.save(meeting);

		System.out.println("Done");
	}
}
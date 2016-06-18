package test;

import java.util.List;
import java.util.Random;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import dao.BlackNumberDBOpenHelper;
import dao.BlackNumberDao;
import domain.BlackNumberInfo;

public class TestBlackNumberDB extends AndroidTestCase {

	public void testCreateDB() throws Exception {
		BlackNumberDBOpenHelper helper = new BlackNumberDBOpenHelper(getContext());
		SQLiteDatabase writableDatabase = helper.getWritableDatabase();
	}
	
	public void testAdd()  throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		long basenumber = 1350000001;
		Random random = new Random();
		for (int i = 0; i < 100; i++) {
		 dao.insert(String.valueOf(basenumber + i), String.valueOf(random.nextInt(3) + 1));
		}
	}
	
	public void testfindAll()  throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		List<BlackNumberInfo> findAll = dao.findAll();
		for (BlackNumberInfo info : findAll) {
			System.out.println(info.toString());
		}
	}
	
	public void testDelete()  throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		dao.delete("110");
	}
	
	public void testUpdate()  throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		dao.update("110", "2");
	}
	
	public void testFind()  throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		boolean result = dao.find("110");
		assertEquals(result, true);
	}
}

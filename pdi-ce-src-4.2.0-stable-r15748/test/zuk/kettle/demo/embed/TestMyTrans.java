package zuk.kettle.demo.embed;

import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.util.EnvUtil;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;

/***
 * 执行trans.ktr文件，并输出文件到c:/file.txt中
 * @author admin
 *
 */
public class TestMyTrans {
	
	public static void main(String[] args)  {
		try {
			KettleEnvironment.init();
		} catch (KettleException e) {
			e.printStackTrace();
		}
		TransMeta tm = null;
		try {
			tm = new TransMeta("F:/gitlib/kettle-4-2-0-stable/pdi-ce-src-4.2.0-stable-r15748/test/zuk/kettle/demo/embed/trans.ktr");
		} catch (KettleXMLException e) {
			e.printStackTrace();
		}
		Trans t = new Trans(tm);
		try {
			t.execute(null);
		} catch (KettleException e) {
			e.printStackTrace();
		}
		t.waitUntilFinished();
	}

}

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
	
	public static void main(String[] args) throws KettleException  {
		KettleEnvironment.init();
		TransMeta tm = new TransMeta("F:/gitlib/kettle-4-2-0-stable/pdi-ce-src-4.2.0-stable-r15748/test/zuk/kettle/demo/embed/trans.ktr");
		Trans t = new Trans(tm);
		String[] arguments = null;
		//		t.execute(null);
//		t.waitUntilFinished();
		t.prepareExecution(arguments);
		t.startThreads();
		t.waitUntilFinished();
	}

}

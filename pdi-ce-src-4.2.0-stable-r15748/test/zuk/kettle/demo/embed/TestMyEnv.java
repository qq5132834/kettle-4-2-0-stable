package zuk.kettle.demo.embed;

import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;

public class TestMyEnv {
	
	/***
	 * 获取环境命令的方式
	 * @param args
	 * @throws KettleException
	 */
	public static void main(String[] args) throws KettleException {
		KettleEnvironment.init();
		TransMeta tm = new TransMeta("F:/gitlib/kettle-4-2-0-stable/pdi-ce-src-4.2.0-stable-r15748/test/zuk/kettle/demo/embed/env.ktr");
		Trans t = new Trans(tm);
		String[] arguments = {"hello"};
		t.execute(arguments);
		t.waitUntilFinished();
		
	}

}

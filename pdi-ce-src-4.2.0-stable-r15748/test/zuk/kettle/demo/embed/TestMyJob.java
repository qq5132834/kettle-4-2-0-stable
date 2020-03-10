package zuk.kettle.demo.embed;

import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogWriter;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;

/***
 * 执行一个job，运行trans
 * 
 * @author Administrator
 *
 */
public class TestMyJob {
	public static void main(String[] args) throws KettleException {
		KettleEnvironment.init();
//		LogWriter lw = LogWriter.getInstance("c:/job.log", true);
//		JobMeta jm = new JobMeta("c:/job.kjb", null);
//		Job j = new Job(null, jm);
//		j.execute(LogWriter., result);
		
		JobMeta jobMeta = new JobMeta("c:/job.kjb",null);
		Job job = new Job(null, jobMeta);  
		//向Job 脚本传递参数，脚本中获取参数值：${参数名}  
		//job.setVariable(paraname, paravalue);  
		job.start();  
		job.waitUntilFinished();  
		if (job.getErrors() > 0) {
			System.out.println("ERROR.");
		}  
		
	}
}

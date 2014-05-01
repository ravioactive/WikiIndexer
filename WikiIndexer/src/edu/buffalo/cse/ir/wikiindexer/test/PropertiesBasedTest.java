/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.test;

import edu.buffalo.cse.ir.wikiindexer.FileUtil;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

/**
 *
 * Common class to do all the parameterized loading
 * Any junit test class that needs a properties file MUST extend this class
 * Others can stay as are
 */
@RunWith(Parameterized.class)
public class PropertiesBasedTest {
protected Properties idxProps;
	
	public PropertiesBasedTest(Properties props) {
		this.idxProps = props;
		try {
			this.idxProps = FileUtil.loadProperties("C:\\Launchpad\\wikiindexer_final\\workspace\\test.properties");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Parameters
	public static Collection<Object[]> generateData() {
		System.setProperty("PROPSFILENAME", "C:\\Launchpad\\wikiindexer_final\\workspace\\test.properties");
		String propFile = System.getProperty("PROPSFILENAME");
		try {
			Properties p = FileUtil.loadProperties(propFile);
			return Arrays.asList(new Object[][]{{p}});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class GraphGenerateTest {
	

	@Test
	public void testQueryBridgeWords() {
		GraphGenerate test = new GraphGenerate();
		String res = test.queryBridgeWords("nop", "niptoo");
		//System.out.println(test.graphOfWords);
		String expect = "No " + "nop" + " and " + "niptoo" + " in the graph!";
		assertEquals(res,expect);
	}
	@Test
	public void testQueryBridgeWords1() {
		GraphGenerate test = new GraphGenerate();
		String res = test.queryBridgeWords("nop", "me");
		//System.out.println(test.graphOfWords);
		String expect = "No "+ "nop" + " in the graph!";
		assertEquals(res,expect);
	}
	@Test
	public void testQueryBridgeWords2() {
		GraphGenerate test = new GraphGenerate();
		String res = test.queryBridgeWords("me","nop");
		String expect ="No "+ "nop" + " in the graph!";
		assertEquals(res,expect);
	}
	@Test
	public void testQueryBridgeWords3() {
		GraphGenerate test = new GraphGenerate();
		String res = test.queryBridgeWords("me", "hacking");
		String expect ="No bridge words from "+"me"+" to "+"hacking"+"!";
		assertEquals(res,expect);
	}
	@Test
	public void testQueryBridgeWords4() {
		GraphGenerate test = new GraphGenerate();
		String res = test.queryBridgeWords("i", "you");
		String expect ="you,love,like.";
		assertEquals(res,expect);
	}
}

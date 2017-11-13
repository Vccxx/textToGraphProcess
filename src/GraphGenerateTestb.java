import static org.junit.Assert.*;

import org.junit.Test;

public class GraphGenerateTest {

	@Test
	public void testCalcShortestPathStringStringString1() {
		GraphGenerate test = new GraphGenerate();
		String result = test.calcShortestPath("","war","red");
		String expect = "No  in the graph!";	
		assertEquals(result,expect);
	}
	
	@Test
	public void testCalcShortestPathStringStringString2() {
		GraphGenerate test = new GraphGenerate();
		String result = test.calcShortestPath("高德","war","red");
		String expect = "No 高德 in the graph!";	
		assertEquals(result,expect);
	}
	
	@Test
	public void testCalcShortestPathStringStringString3() {
		GraphGenerate test = new GraphGenerate();
		String result = test.calcShortestPath("ccc","war","red");
		String expect = "No ccc in the graph!";	
		assertEquals(result,expect);
	}
	
	@Test
	public void testCalcShortestPathStringStringString4() {
		GraphGenerate test = new GraphGenerate();
		String result = test.calcShortestPath("or","war","red");
		String expect = "No path from or to war";	
		assertEquals(result,expect);
	}
	
	@Test
	public void testCalcShortestPathStringStringString5() {
		GraphGenerate test = new GraphGenerate();
		String result = test.calcShortestPath("that","a","red");
		String expect = "the shortest length from that to a is :that->we->in->a";	
		assertEquals(result,expect);
	}
}

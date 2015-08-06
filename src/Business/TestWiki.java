package Business;

	import static org.junit.Assert.*;

import org.junit.Test;

import org.junit.BeforeClass;

public class TestWiki {
	private static Wiki wiki;
	
	@BeforeClass
	public static void init(){
		wiki = new Wiki();
		wiki.addLink("https://ru.wikipedia.org/wiki/JSP");
		wiki.addLink("https://ru.wikipedia.org/wiki/SVG");
		wiki.addLink("https://ru.wikipedia.org/wiki/WML");
	}
	
	@Test
	public void test1AddAndUpdateLink() {
		Link link = wiki.getById(2);
		assertTrue(link != null);
		assertTrue(link.getPriority() == 0);
		assertTrue(link.getProcStatus().equals(Link.Status.NOT_STARTED));
		assertTrue(link.getStudyStatus().equals(Link.Status.NOT_STARTED));
		assertTrue(link.getRef().equals("https://ru.wikipedia.org/wiki/WML"));
		assertTrue(wiki.getLinks().size() == 3);
	}
	
	@Test
	public void test2GetLink() {
		Link link = wiki.getById(1);
		assertTrue(link.getId() == 1);
	}
	
	@Test
	public void test3SetPrio() {
		wiki.setPriority(2, 1);
		Link l = wiki.getById(2);
		assertEquals(1, l.getPriority());
	}

	@Test
	public void test4SetLinksProc() {
		wiki.setProcStatus(2, Link.Status.STARTED);
		Link l = wiki.getById(2);
		assertEquals(Link.Status.STARTED, l.getProcStatus());
	}
	
	@Test
	public void test5SetStudyProc() {
		wiki.setStudyStatus(2, Link.Status.FINISHED);
		Link l = wiki.getById(2);
		assertEquals(Link.Status.FINISHED, l.getStudyStatus());
	}
	
	@Test
	public void test6Update() {
		Link l = wiki.getById(2);
		l.setPriority(5);
		wiki.updateLink(l);
		l = wiki.getById(2);
		assertEquals(5, l.getPriority());
	}

}
